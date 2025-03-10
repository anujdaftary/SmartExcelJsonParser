package org.example.smartexceljsonparser.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;

@Service
public class JsonService {
    private final ObjectMapper mapper = new ObjectMapper();

    public byte[] convertJsonToExcel(byte[] jsonBytes) throws IOException {
        Map<String, Object> data = mapper.readValue(jsonBytes, new TypeReference<>() {});
        Workbook workbook = new XSSFWorkbook();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String sheetName = entry.getKey();
            Object sheetData = entry.getValue();

            if (sheetName.equalsIgnoreCase("Policy Info")) {
                writeKeyValueSheet(workbook, sheetName, (Map<String, String>) sheetData);
            } else if (sheetName.equalsIgnoreCase("Ala Carte Benefits")) {
                writeAlaCarteSheet(workbook, sheetName, (Map<String, Object>) sheetData);
            } else if (sheetName.equalsIgnoreCase("Modular Plans")) {
                writeModularPlansSheet(workbook, sheetName, (Map<String, Object>) sheetData);
            } else {
                writeColumnarSheet(workbook, sheetName, (Map<String, Object>) sheetData);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    private void writeKeyValueSheet(Workbook workbook, String sheetName, Map<String, String> data) {
        Sheet sheet = workbook.createSheet(sheetName);
        int rowNum = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
    }

    private void writeAlaCarteSheet(Workbook workbook, String sheetName, Map<String, Object> sheetData) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Get schema and data
        Map<String, Object> schema = (Map<String, Object>) sheetData.get("schema");
        List<String> mandatory = (List<String>) schema.get("mandatory");
        List<String> optional = (List<String>) schema.get("optional");
        List<Map<String, String>> data = (List<Map<String, String>>) sheetData.get("data");

        // Create headers
        List<String> headers = new ArrayList<>(mandatory);
        headers.addAll(optional);

        // Create metadata rows
        Row optionalRow = sheet.createRow(0);
        Row dataTypeRow = sheet.createRow(1);
        Row headerRow = sheet.createRow(2);

        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            // Optional/Mandatory markers
            optionalRow.createCell(i).setCellValue(optional.contains(header) ? "[optional]" : "[mandatory]");
            // Data types (simplified - you might need to store actual types in JSON)
            dataTypeRow.createCell(i).setCellValue("<text>");
            // Headers
            headerRow.createCell(i).setCellValue(header);
        }

        // Create data rows
        int dataRowNum = 3;
        for (Map<String, String> rowData : data) {
            Row row = sheet.createRow(dataRowNum++);
            for (int i = 0; i < headers.size(); i++) {
                String value = rowData.getOrDefault(headers.get(i), "");
                row.createCell(i).setCellValue(value);
            }
        }
    }

    private void writeModularPlansSheet(Workbook workbook, String sheetName, Map<String, Object> sheetData) {
        Sheet sheet = workbook.createSheet(sheetName);
        Map<String, Map<String, String>> plans = (Map<String, Map<String, String>>) sheetData.get("plans");

        // Create header structure
        int rowNum = 0;
        Map<String, Integer> headerRowMap = new LinkedHashMap<>();

        // First pass to collect all headers and their row numbers
        for (Map.Entry<String, Map<String, String>> planEntry : plans.entrySet()) {
            for (String header : planEntry.getValue().keySet()) {
                if (!headerRowMap.containsKey(header)) {
                    headerRowMap.put(header, rowNum++);
                }
            }
        }

        // Create column headers (plan names)
        int colNum = 5; // Starting at column F
        Row headerRow = sheet.createRow(0);
        for (String planName : plans.keySet()) {
            headerRow.createCell(colNum).setCellValue(planName);
            colNum++;
        }

        // Create rows for each header
        for (Map.Entry<String, Integer> entry : headerRowMap.entrySet()) {
            String header = entry.getKey();
            int rowIdx = entry.getValue();

            Row row = sheet.createRow(rowIdx + 1); // Offset for header row
            row.createCell(1).setCellValue(header); // Column B

            colNum = 5; // Reset to column F
            for (Map<String, String> planData : plans.values()) {
                String value = planData.getOrDefault(header, "");
                row.createCell(colNum).setCellValue(value);
                colNum++;
            }
        }
    }

    private void writeColumnarSheet(Workbook workbook, String sheetName, Map<String, Object> sheetData) {
        Sheet sheet = workbook.createSheet(sheetName);
        List<Map<String, String>> data = (List<Map<String, String>>) sheetData.get("data");

        if (data.isEmpty()) return;

        // Create header row
        Row headerRow = sheet.createRow(0);
        List<String> headers = new ArrayList<>(data.get(0).keySet());
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }

        // Create data rows
        int rowNum = 1;
        for (Map<String, String> rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.size(); i++) {
                String value = rowData.getOrDefault(headers.get(i), "");
                row.createCell(i).setCellValue(value);
            }
        }
    }
}