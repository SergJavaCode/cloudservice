package ru.sergjava.cloudservice.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.minio.errors.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sergjava.cloudservice.service.JwtTokenHandler;
import ru.sergjava.cloudservice.service.ServiceUsersBucketImpl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class Controller {
    private ServiceUsersBucketImpl serviceUsersBucketImpl;
    private JwtTokenHandler jwtTokenHandler;

    public Controller(ServiceUsersBucketImpl serviceUsersBucketImple, JwtTokenHandler jwtTokenHandler) {
        this.serviceUsersBucketImpl = serviceUsersBucketImple;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @PostMapping("/login")
    public void login(@RequestBody String login, @RequestBody String password) {


    }

    @RolesAllowed({"USER", "ADMIN"})
    @PostMapping("/file")
    public void file(@RequestParam("filename") String fileName,
                     @RequestBody MultipartFile file, HttpServletResponse response) throws RegionConflictException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        serviceUsersBucketImpl.fileUpLoad(fileName, file, response);
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/list")
    public ArrayNode list(HttpServletRequest request, HttpServletResponse response, @RequestParam("limit") Integer limit) {
        return serviceUsersBucketImpl.listFiles(response, limit);
    }

    @RolesAllowed({"USER", "ADMIN"})
    @DeleteMapping("/file")
    public void deleteFile(@RequestParam("filename") String fileName, HttpServletResponse response) {
        serviceUsersBucketImpl.deleteFile(fileName, response);
    }

    @RolesAllowed({"USER", "ADMIN"})
    @PutMapping("/file")
    public void editFile(@RequestParam("filename") String oldFileName, @RequestBody String filename, HttpServletResponse response) {
        serviceUsersBucketImpl.editFileName(oldFileName, filename, response);
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/file")
    public void downloadFile(@RequestParam("filename") String fileName) {
        serviceUsersBucketImpl.downloadFile(fileName);
    }
}
