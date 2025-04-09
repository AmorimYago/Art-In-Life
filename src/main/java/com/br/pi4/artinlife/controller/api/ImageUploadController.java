package com.br.pi4.artinlife.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> fileNames = new ArrayList<>();
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                fileNames.add(fileName); // Retorna só o nome (ex: abc.jpg)
            }

            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
