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
    public User login(String login, String password) { //нигде не используется
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return usersRepository.getUserByName(userDetails.getUsername());
    }

    @Override
    public void fileUpLoad(String fileName, MultipartFile file, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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

    @Override
    public void downloadFile(String fileName) {
        bucketRepository.downloadFile(fileName, getUser());
    }

    @Override
    public void getFile(String fileName, HttpServletResponse response) {
        Long size = bucketRepository.getLengthObject(getUser(), fileName);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.setContentLengthLong(size);

        try {
            InputStream inputStream = bucketRepository.getFile(fileName, getUser());
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            bos.write(inputStream.readAllBytes());
            bos.flush();
            bos.close();
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//                BufferedInputStream bis = new BufferedInputStream(bucketRepository.getFile(fileName, getUser()));
//                byte barray[] = new byte[5000];
//                byte echo[]   = null;
//                int i         = 0;
//                int counter   = 0;
//                while (i > -1) {
//                    bis.mark(counter);
//                    echo = barray;
//                    if(counter == 0 && i == -1)break;
//                    if(counter > 0)outputStream.write(echo, 0, i);
//                    i = bis.read(barray);
//                    counter+=5000;
//                }
//                int b = 0;
//                bis.reset();
//                while(b != -1){
//                    b = bis.read();
//                    if(b > -1)outputStream.write(b);
//                }
//                bis.close();

//                outputStream.flush();
//                outputStream.close();

//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }


    }
//@Override
//public void getFile(String fileName,HttpServletResponse response) {
////    Long size = bucketRepository.getLengthObject(getUser(), fileName);
////    response.setContentLengthLong(size);
//   // response.setHeader("Content-disposition", "form-data;filename=" + fileName);
//    response.setContentType("multipart/form-data; boundary=\"AaB03x\"");
//    InputStream inputStream = bucketRepository.getFile(fileName, getUser());
//    MessageDigest md = null;
//    try {
//        md = MessageDigest.getInstance("MD5");
//    } catch (NoSuchAlgorithmException e) {
//        throw new RuntimeException(e);
//    }
//    DigestInputStream dis = new DigestInputStream(inputStream, md);
//    {
//
//        try {
//            ServletOutputStream outputStream = response.getOutputStream();
////        outputStream.println();
////        outputStream.println("--END");
////        outputStream.println("ContentType: APPLICATION_OCTET_STREAM");
////        outputStream.println("Content-disposition: attachment;filename=" + fileName);
//            // outputStream.println("Content-Length: "+ size);
//            outputStream.print("\r\n\r\n");
//            outputStream.print("--AaB03x\r\n");
//            //пишем в тело json error
//            ObjectMapper objectMapper = new ObjectMapper();
//            byte[] digest = md.digest();
//            GetFileBody getFileBody = new GetFileBody(String.valueOf(digest), fileName);
//            String jsonGetFileBody = objectMapper.writeValueAsString(getFileBody);
//            outputStream.print("Content-Disposition: form-data; name=\"json\"\r\n");
//            outputStream.print("Content-Type: application/json\r\n\r\n" );
//            outputStream.print(jsonGetFileBody);
//            outputStream.print("\r\n" );
//            outputStream.print("--AaB03x\r\n");
//            outputStream.print("Content-Disposition: form-data; name="+fileName+"; filename="+fileName +"\r\n");
//            outputStream.print("Content-Type: application/octet-stream\r\n\r\n");
//            outputStream.write(inputStream.readAllBytes());
//            outputStream.print("\r\n");
//            outputStream.print("--AaB03x--");
//            outputStream.print("\r\n");
//
//            inputStream.close();
//            outputStream.flush();
//            outputStream.close();
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    response.setContentLengthLong(size);
//    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//    MessageDigest md = null;
//    try {
//        md = MessageDigest.getInstance("MD5");
//    } catch (NoSuchAlgorithmException e) {
//        throw new RuntimeException(e);
//    }
//    DigestInputStream dis = new DigestInputStream(inputStream, md);
//    {
////            try {
//
//        //   OutputStream outputStream = response.getOutputStream();
//        byte[] digest = md.digest();
//        //пишем в тело json error
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        builder.addBinaryBody(fileName, inputStream, ContentType.APPLICATION_OCTET_STREAM, fileName);
//
//
//
//        try {
//
//            GetFileBody getFileBody = new GetFileBody(String.valueOf(digest), fileName);
//            String jsonGetFileBody = objectMapper.writeValueAsString(getFileBody);
////                    outputStream.write(jsonGetFileBody.getBytes());
////                    outputStream.flush();
//            builder.addPart("APPLICATION_JSON", new StringBody(jsonGetFileBody, ContentType.APPLICATION_JSON));
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
////                BufferedInputStream bis = new BufferedInputStream(inputStream);
////                byte barray[] = new byte[5000];
////                byte echo[]   = null;
////                int i         = 0;
////                int counter   = 0;
////                while (i > -1) {
////                    bis.mark(counter);
////                    echo = barray;
////                    if(counter == 0 && i == -1)break;
////                    if(counter > 0)outputStream.write(echo, 0, i);
////                    i = bis.read(barray);
////                    counter+=5000;
////                }
////                int b = 0;
////                bis.reset();
////                while(b != -1){
////                    b = bis.read();
////                    if(b > -1)outputStream.write(b);
////                }
////                bis.close();
//
////                outputStream.flush();
////                outputStream.close();
//
////            } catch (IOException e) {
////                throw new RuntimeException(e);
////            }
//
//
//        HttpEntity multipart = builder.build();
//        try {
//            multipart.writeTo(response.getOutputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }


    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getPrincipal().toString();
        return usersRepository.getUserByName(userName);
    }

}
