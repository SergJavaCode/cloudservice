package ru.sergjava.cloudservice.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.minio.errors.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sergjava.cloudservice.service.ServiceUsersBucketImpl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
@Log4j2
@RestController
public class Controller {
    private ServiceUsersBucketImpl serviceUsersBucketImpl;

    public Controller(ServiceUsersBucketImpl serviceUsersBucketImple) {
        this.serviceUsersBucketImpl = serviceUsersBucketImple;
    }

    @RolesAllowed({"USER", "ADMIN"})
    @PostMapping("/file")
    public void file(@RequestParam("filename") String fileName,
                     @RequestBody MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("Начата загрузка файла " + fileName+ " в облако.");
        serviceUsersBucketImpl.fileUpLoad(fileName, file);
        log.info("Файл " + fileName+ " успешно загружен в облако.");
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/list")
    public ArrayNode list(HttpServletResponse response, @RequestParam("limit") Integer limit) {
        response.setContentType("application/json;charset=UTF-8");
        return serviceUsersBucketImpl.listFiles(limit);
    }

    @RolesAllowed({"USER", "ADMIN"})
    @DeleteMapping("/file")
    public void deleteFile(@RequestParam("filename") String fileName) {
        serviceUsersBucketImpl.deleteFile(fileName);
        log.info("Файл "+fileName+" успешно удален.");
    }

    @RolesAllowed({"USER", "ADMIN"})
    @PutMapping("/file")
    public void editFile(@RequestParam("filename") String oldFileName, @RequestBody String fileName) {
        serviceUsersBucketImpl.editFileName(oldFileName, fileName);
        log.info("Файл "+fileName+" успешно изменён.");
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/file")
    public void getFile(@RequestParam("filename") String fileName, HttpServletResponse response) {
        log.info("Начата загрузка файла " + fileName+ " на ПК.");
        InputStream inputStream = serviceUsersBucketImpl.getFile(fileName);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.setContentLengthLong(serviceUsersBucketImpl.getLengthObject(fileName));
        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            bos.write(inputStream.readAllBytes());
            bos.flush();
            bos.close();
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        log.info("Файл "+fileName+ " успешно скачан.");
    }
}
