package ru.sergjava.cloudservice.repository;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.sergjava.cloudservice.dto.ListDto;
import ru.sergjava.cloudservice.exceptions.BadRequestExceptionCust;
import ru.sergjava.cloudservice.exceptions.InternalServerErrorCust;
import ru.sergjava.cloudservice.model.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class BucketRepository {
    private UsersRepository usersRepository;

    public BucketRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    private MinioClient minioClient = MinioClient.builder()
            .endpoint("http://localhost:9000")
            .credentials("cloudservice", "alw23lkn23b434hb232b3bv")
            .build();

    public boolean checkBucketByUser(User user) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(user.getUsername().replace('@', '.')).build())) {
            log.info("Bucket "+ user.getUsername().replace('@', '.')+ " найден.");
            return true;
        } else {
            log.info("Bucket "+ user.getUsername().replace('@', '.')+ " не обнаружен.");
            return false;
        }
    }

    public void makeBucketByUser(User user) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket(user.getUsername().replace('@', '.'))
                        .build());
        usersRepository.updateUserBucket(user.getUsername(), user.getUsername().replace('@', '.'));
        log.info("Bucket "+ user.getUsername().replace('@', '.')+ " успешно создан.");
    }

    public void fileUpload(String fileName, MultipartFile file, User user) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (!checkBucketByUser(user)) {
            log.info("Bucket для " +user.getUsername() + " не обнаружен.");
            makeBucketByUser(user);
        }
        try {
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(user.getUsername().replace('@', '.')).object(fileName).stream(
                                    inputStream, -1, 5242880)
                            .build());
        } catch (Exception e) {
            log.error("File upload error");
            throw new BadRequestExceptionCust("File upload error");
        }
    }

    public Long getLengthObject(User user, String fileName) {
        Long size;
        log.info("Определение размера объекта.");
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(user.getBucket()).object(fileName).build());
            size = stat.size();
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        return size;
    }

    public List<ListDto> listFiles(User user, Integer limit) {
        log.info("Вывод списка объектов поьзователя " + user.getUsername());
        List<ListDto> list = new ArrayList<>();
        String userBucket = usersRepository.getBucketUser(user.getUsername());
        if (userBucket == null) {
            return list;
        }
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(userBucket).maxKeys(limit).build());
        for (Result<Item> result : results) {
            try {
                list.add(new ListDto(URLDecoder.decode(result.get().objectName(), "UTF-8"), Integer.valueOf(String.valueOf(result.get().size()))));
                System.out.println(result.get().objectName());
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                log.error(e);
                throw new InternalServerErrorCust("File download error");
            }
        }
        return list;
    }

    public void deleteFile(String fileName, User user) {
        log.info("Удаление файла "+fileName);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(user.getBucket())
                    .object(fileName)
                    .build());
          //  response.setStatus(200);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e);
            throw new BadRequestExceptionCust("File deletion error");
        }
    }

    public void editFileName(String fileName, String newName, User user) {
        log.info("Изменение файла "+ fileName);
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(user.getBucket())
                            .object(newName)
                            .source(
                                    CopySource.builder()
                                            .bucket(user.getBucket())
                                            .object(fileName)
                                            .build())
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e);
            throw new InternalServerErrorCust("File edit error");
        }
        deleteFile(fileName, user);
    }

     public InputStream getFile(String fileName, User user) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(user.getBucket())
                            .object(fileName)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e);
            throw new InternalServerErrorCust("File download error");
        }
    }

}
