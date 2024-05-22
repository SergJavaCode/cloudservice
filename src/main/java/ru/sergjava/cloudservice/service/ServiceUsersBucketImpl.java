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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public void fileUpLoad(String fileName, MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        bucketRepository.fileUpload(fileName, file, getUser());
    }

    @Override
    public ArrayNode listFiles(Integer limit) {
        ObjectMapper mapper = new ObjectMapper();
        // create a JSON object
        ArrayNode arrayNode = mapper.createArrayNode();
        List<ListDto> listFiles = bucketRepository.listFiles(getUser(), limit);
        if (listFiles.isEmpty()) {
            return null;
        } else {
            for (ListDto file : listFiles) {
                ObjectNode node = mapper.createObjectNode();
                node.put("filename", String.valueOf(file.getListDto().getFilename())).put("size", file.getSize());
                arrayNode.add(node);
            }
        }
        return arrayNode;
    }

    @Override
    public void deleteFile(String fileName) {
        bucketRepository.deleteFile(fileName, getUser());
    }

    @Override
    public void editFileName(String fileName, String newName) {
        bucketRepository.editFileName(fileName, newName, getUser());
    }

    @Override
    public Long getLengthObject(String fileName) {
        return bucketRepository.getLengthObject(getUser(), fileName);
    }

    @Override
    public InputStream getFile(String fileName) {
        return bucketRepository.getFile(fileName, getUser());
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getPrincipal().toString();
        return usersRepository.getUserByName(userName);
    }

}
