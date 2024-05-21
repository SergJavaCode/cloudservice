package ru.sergjava.cloudservice.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import ru.sergjava.cloudservice.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ServiceUsersBucketInt {
        public void fileUpLoad(String name, MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    public ArrayNode listFiles(Integer limit);

    public void deleteFile(String fileName);

    public void editFileName(String fileName, String newName);

    public InputStream getFile(String fileName);
    public Long getLengthObject(String fileName);
}
