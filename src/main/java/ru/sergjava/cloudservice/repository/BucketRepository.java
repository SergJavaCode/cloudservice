package ru.sergjava.cloudservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.sergjava.cloudservice.dto.ErrorDto;
import ru.sergjava.cloudservice.dto.ListDto;
import ru.sergjava.cloudservice.model.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


@Component
public class BucketRepository {
    private SessionFactory sessionFactory;
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
            return true;
        } else {
            return false;
        }
    }

    public void makeBucketByUser(User user) throws  ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket(user.getUsername().replace('@', '.'))
                        .build());

        usersRepository.updateUserBucket(user.getUsername(), user.getUsername().replace('@', '.'));
    }

    public void fileUpload(String fileName, MultipartFile file, User user, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException{
        if (!checkBucketByUser(user)) {
            makeBucketByUser(user);
        }

        try {
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(user.getUsername().replace('@', '.')).object(fileName).stream(
                                    inputStream, -1, 5242880)
                            .build());
            System.out.println("файл " + fileName + " загружен");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");

            //пишем в тело json error
            ObjectMapper objectMapper = new ObjectMapper();
            PrintWriter out = null;
            out = response.getWriter();
            ErrorDto errorDto = new ErrorDto(e.getMessage());
            String jsonErrorDto = objectMapper.writeValueAsString(errorDto);
            out.println(jsonErrorDto);
            out.close();
        }


    }

    public Long getLengthObject(User user,String fileName){
        Long size;
        try {
            StatObjectResponse stat  = minioClient.statObject( StatObjectArgs.builder().bucket(user.getBucket()).object(fileName).build());
            size= stat.size();
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
        return size;
    }
    public List<ListDto> listFiles(User user, HttpServletResponse response, Integer limit) {
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
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    public void deleteFile(String fileName, User user, HttpServletResponse response) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(user.getBucket())
                    .object(fileName)
                    .build());
            response.setStatus(200);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    public void editFileName(String fileName, String newName, User user, HttpServletResponse response) {
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
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }

        //  deleteFile(fileName, user, response);
    }

    public void downloadFile(String fileName, User user) {
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(user.getBucket())
                            .object(fileName)
                            .filename(fileName)
                            .build());
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }  catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getFile(String fileName, User user) {
        try {
          return  minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(user.getBucket())
                            .object(fileName)
                            .build());
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }  catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

}
