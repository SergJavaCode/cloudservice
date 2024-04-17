package ru.sergjava.cloudservice.service;

import ru.sergjava.cloudservice.model.User;

import java.util.List;

public interface ServiceUsersBucketInt{
   // public List<String> getListOfFiles(String city);

    public User login(String login, String password);
}
