package org.example.smartexceljsonparser.utils;

import org.springframework.web.multipart.MultipartFile;

public class ValidationUtils {
    public static void validateFile(MultipartFile file) throws IllegalArgumentException {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls") && !fileName.endsWith(".json")) {
            throw new IllegalArgumentException("Invalid file type. Use Excel (.xlsx/.xls) or JSON");
        }
    }
}