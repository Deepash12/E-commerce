package com.example.E.commerce.E_commerce.Service.File;

import com.example.E.commerce.E_commerce.Exception.BadRequestException;
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

    public String uploadFile(MultipartFile file) throws IOException
    {
        // Check if file is empty
        if (file.isEmpty())
        {
            throw new BadRequestException("File cannot be empty");
        }

        // Allowed image types
        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png")  ||
                        contentType.equals("image/jpg")  ||
                        contentType.equals("image/webp")))
        {
            throw new BadRequestException(
                    "Only image files are allowed (jpg, jpeg, png, webp)"
            );
        }

        // Create uploads folder if not exists
        File folder = new File(uploadDir);

        if (!folder.exists())
        {
            folder.mkdir();
        }

        // Generate unique file name
        String fileName = System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();

        Path filePath = Paths.get(uploadDir, fileName);

        // Copy file
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/" + fileName;
    }
}