package org.example.smartexceljsonparser.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.smartexceljsonparser.service.ExcelService;
import org.example.smartexceljsonparser.service.JsonService;
import org.example.smartexceljsonparser.utils.ValidationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

@Controller
public class FileController {
    private final ExcelService excelService;
    private final JsonService jsonService;

    public FileController(ExcelService excelService, JsonService jsonService) {
        this.excelService = excelService;
        this.jsonService = jsonService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/convert")
    public String handleConversion(
            @RequestParam("file") MultipartFile file,
            @RequestParam("conversionType") String conversionType,
            Model model,
            HttpSession session // Add HttpSession
    ) {
        try {
            ValidationUtils.validateFile(file);
            if (conversionType.equals("excelToJson")) {
                String json = excelService.convertExcelToJson(file);
                model.addAttribute("json", json);
            } else {
                byte[] excelBytes = jsonService.convertJsonToExcel(file.getBytes());
                // Store in session instead of model
                session.setAttribute("excelBytes", excelBytes);
                model.addAttribute("fileName", "converted.xlsx");
            }
            return "result";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }
    @GetMapping("/download")
    public void downloadFile(
            @RequestParam("fileName") String fileName,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {
        byte[] excelBytes = (byte[]) session.getAttribute("excelBytes");
        if (excelBytes == null) {
            throw new IllegalArgumentException("File not found");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();

        // Clear the session data after download
        session.removeAttribute("excelBytes");
    }
}