package ru.sergjava.cloudservice.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import ru.sergjava.cloudservice.model.User;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ServiceUsersBucketInt{
   // public List<String> getListOfFiles(String city);

    public User login(String login, String password);
    public void fileUpLoad(String name, MultipartFile file,  HttpServletResponse response) throws RegionConflictException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    public ArrayNode listFiles(HttpServletResponse response, Integer limit);
    public void deleteFile(String fileName, HttpServletResponse response);
    public void editFileName(String fileName, String newName, HttpServletResponse response);
}
