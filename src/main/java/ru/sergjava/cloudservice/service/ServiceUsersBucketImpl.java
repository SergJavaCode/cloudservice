package ru.sergjava.cloudservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sergjava.cloudservice.dto.ListDto;
import ru.sergjava.cloudservice.model.User;
import ru.sergjava.cloudservice.repository.BucketRepository;
import ru.sergjava.cloudservice.repository.UsersRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class ServiceUsersBucketImpl implements ServiceUsersBucketInt {
    private final UsersRepository usersRepository;
    private final BucketRepository bucketRepository;


    public ServiceUsersBucketImpl(UsersRepository usersRepository, BucketRepository bucketRepository) {
        this.usersRepository = usersRepository;
        this.bucketRepository = bucketRepository;
    }


    @Override
    public User login(String login, String password) { //нигде не используется
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return usersRepository.getUserByName(userDetails.getUsername());
    }

    @Override
    public void fileUpLoad(String fileName, MultipartFile file, HttpServletResponse response) throws RegionConflictException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        bucketRepository.fileUpload(fileName, file, getUser(), response);
    }

    @Override
    public ArrayNode listFiles(HttpServletResponse response, Integer limit) {
        ObjectMapper mapper = new ObjectMapper();
        // create a JSON object
        ArrayNode arrayNode = mapper.createArrayNode();
        List<ListDto> listFiles = bucketRepository.listFiles(getUser(), response, limit);
        if (listFiles.isEmpty()) {
            return null;
        } else {
            for (ListDto file : listFiles) {
                ObjectNode node = mapper.createObjectNode();
                node.put("filename", String.valueOf(file.getListDto().getFilename())).put("size", file.getSize());
                arrayNode.add(node);
            }
        }
        response.setContentType("application/json;charset=UTF-8");
        return arrayNode;
    }

    @Override
    public void deleteFile(String fileName, HttpServletResponse response) {
        bucketRepository.deleteFile(fileName, getUser(), response);
    }

    @Override
    public void editFileName(String fileName, String newName, HttpServletResponse response) {
        bucketRepository.editFileName(fileName, newName, getUser(), response);
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getPrincipal().toString();
        return usersRepository.getUserByName(userName);
    }

}
