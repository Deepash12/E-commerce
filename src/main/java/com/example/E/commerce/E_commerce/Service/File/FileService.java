package com.example.E.commerce.E_commerce.Service.File;

import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService
{
    private final String uploadDir = "uploads/";
    public String uploadFile(MultipartFile file) throws IOException {
        File folder = new File(uploadDir);
        if(!folder.exists()){
            folder.mkdir();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(uploadDir + fileName);

        Files.copy(file.getInputStream(), filePath);

        return "/uploads/" + fileName;
    }
}
