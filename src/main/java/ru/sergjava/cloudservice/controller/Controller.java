package ru.sergjava.cloudservice.controller;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import ru.sergjava.cloudservice.service.JwtTokenHandler;
import ru.sergjava.cloudservice.service.ServiceRepositoUsersBucketImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@RestController
public class Controller {
    private ServiceRepositoUsersBucketImpl serviceRepositoUsersBucketImpl;
    private JwtTokenHandler jwtTokenHandler;

    public Controller(ServiceRepositoUsersBucketImpl serviceRepositoUsersBucketImple, JwtTokenHandler jwtTokenHandler) {
        this.serviceRepositoUsersBucketImpl = serviceRepositoUsersBucketImple;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @PostMapping("/login")
    public void login(@RequestBody String login, @RequestBody String password) {


    }
//    @RolesAllowed({"USER", "ADMIN"})
//    @PostMapping("/logout")
//    public void logout(HttpServletRequest request){
//        jwtTokenHandler.deleteToken(request);
//    }
    @RolesAllowed({"USER", "ADMIN"})
    @PostMapping("/file")
    public String handleFileUpload(@RequestParam("filename") String name,
                                   @RequestBody MultipartFile file){
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                System.out.println(name);
                return name;
            } catch (Exception e) {
                System.out.println("Вам не удалось загрузить " + name + " => " + e.getMessage());
                return "Вам не удалось загрузить " + name + " => " + e.getMessage();
            }
        } else {
            System.out.println("Вам не удалось загрузить " + name + " потому что файл пустой.");
            return "Вам не удалось загрузить " + name + " потому что файл пустой.";
        }
    }
    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/list")
    public  ArrayNode list(HttpServletRequest request,  HttpServletResponse response) throws JsonProcessingException {

        // create `ObjectMapper` instance
        ObjectMapper mapper = new ObjectMapper();

        // create a JSON object
        ArrayNode user = mapper.createArrayNode();
        ObjectNode node = mapper.createObjectNode();
        node.put("filename", "sfdsdfsdf.exe").put("size",564654);
        user.add(node);
        response.setContentType("application/json;charset=ISO-8859-1");
        // convert `ObjectNode` to pretty-print JSON
        // without pretty-print, use `user.toString()` method
//        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
        return  user;
    }

}
