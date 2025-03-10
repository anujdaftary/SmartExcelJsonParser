package org.example.smartexceljsonparser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {
    private final DataFormatter formatter = new DataFormatter();

    public String convertExcelToJson(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Map<String, Object> result = new LinkedHashMap<>();

        for (Sheet sheet : workbook) {
            String sheetName = sheet.getSheetName().trim().toLowerCase();
            List<String> words = Arrays.asList(sheetName.split("\\s+")); // Split by spaces

            System.out.println("Processing Sheet: [" + sheetName + "] | Words: " + words); // Debugging

            if (words.contains("policy") && words.contains("info")) {
                result.put(sheet.getSheetName(), processKeyValueSheet(sheet));
            } else if (words.contains("ala") && words.contains("carte") && words.contains("benefits")) {
                result.put(sheet.getSheetName(), processAlaCarteSheet(sheet));
            } else if (words.contains("modular") && words.contains("plans")) {
                result.put(sheet.getSheetName(), processModularPlanSheet(sheet));
            }
            else {
                System.out.println("Default case triggered for: [" + sheet.getSheetName() + "]");
                result.put(sheet.getSheetName(), processColumnarSheet(sheet));
            }
        }


        workbook.close();
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }



    private Map<String, String> processKeyValueSheet(Sheet sheet) {
        Map<String, String> data = new LinkedHashMap<>();
        for (Row row : sheet) {
            String key = formatter.formatCellValue(row.getCell(0)).trim();
            String value = formatter.formatCellValue(row.getCell(1)).trim();
            if (!key.isEmpty()) data.put(key, value);
        }
        return data;
    }

    private Map<String, Object> processModularPlanSheet(Sheet sheet) {
        Map<String, Object> sheetResult = new LinkedHashMap<>();
        List<String> headers = new ArrayList<>();
        List<Map<String, String>> data = new ArrayList<>();

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell headerCell = null;

            if (i >= 0 && i <= 3) {
                headerCell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            } else if (i >= 5) {
                headerCell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            } else {
                continue;
            }

            String headerValue = getCellValueAsString(headerCell).trim();
            if (!headerValue.isEmpty()) {
                headers.add(headerValue);
            }
        }

        int firstPlanCol = 5;
        int lastPlanCol = 8;
        Map<String, Map<String, String>> plansData = new LinkedHashMap<>();

        for (int col = firstPlanCol; col <= lastPlanCol; col++) {
            Cell planCell = sheet.getRow(0).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String planName = getCellValueAsString(planCell).trim();

            if (planName.isEmpty()) continue;

            Map<String, String> planDetails = new LinkedHashMap<>();

            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                String header = (rowIdx >= 0 && rowIdx <= 3) ?
                        getCellValueAsString(row.getCell(1)).trim() :
                        (rowIdx >= 5 ? getCellValueAsString(row.getCell(2)).trim() : "");

                if (header.isEmpty()) continue;

                Cell valueCell = row.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String value = getCellValueAsString(valueCell).trim();
                planDetails.put(header, value);
            }

            plansData.put(planName, planDetails);
        }

        sheetResult.put("plans", plansData);

        return sheetResult;
    }


    private String getCellValueAsString(Cell cell) {
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf(cell.getNumericCellValue());
        return "";
    }


    private Map<String, Object> processAlaCarteSheet(Sheet sheet) {
        Map<String, Object> sheetResult = new LinkedHashMap<>();
        List<String> headers = new ArrayList<>();
        List<String> optional = new ArrayList<>();
        List<String> mandatory = new ArrayList<>();


        Row optionalMandatoryRow = sheet.getRow(0);

        Row headerRow = sheet.getRow(2);

        if (optionalMandatoryRow == null || headerRow == null) {
            throw new IllegalArgumentException("Ala carte Benefits sheet is missing metadata or headers!");
        }

        // Extract headers from Row 2 (Excel row 3)
        for (Cell cell : headerRow) {
            String header = formatter.formatCellValue(cell).trim();
            if (!header.isEmpty()) headers.add(header);
        }

        // Parse [optional]/[mandatory] from Row 0 (Excel row 1)
        for (int col = 0; col < headers.size(); col++) {
            Cell cell = optionalMandatoryRow.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String value = formatter.formatCellValue(cell).trim();
            if ("[optional]".equalsIgnoreCase(value)) {
                optional.add(headers.get(col));
            } else if ("[mandatory]".equalsIgnoreCase(value)) {
                mandatory.add(headers.get(col));
            }
        }

        // ------------------------------------------
        // Step 2: Parse Data (Rows 3 onwards in POI = Excel rows 4+)
        // ------------------------------------------
        List<Map<String, String>> data = new ArrayList<>();
        for (int rowNum = 3; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) continue;

            Map<String, String> rowData = new LinkedHashMap<>();
            boolean isEmptyRow = true;
            for (int col = 0; col < headers.size(); col++) {
                Cell cell = row.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String value = formatter.formatCellValue(cell).trim();
                rowData.put(headers.get(col), value);
                if (!value.isEmpty()) isEmptyRow = false;
            }
            if (!isEmptyRow) data.add(rowData);
        }

        // ------------------------------------------
        // Step 3: Build JSON Structure
        // ------------------------------------------
        sheetResult.put("schema", Map.of(
                "mandatory", mandatory,
                "optional", optional
        ));
        sheetResult.put("data", data);

        return sheetResult;
    }


    private Map<String, Object> processColumnarSheet(Sheet sheet) {
        Map<String, Object> sheetResult = new LinkedHashMap<>();
        List<String> headers = new ArrayList<>();
        List<Map<String, String>> data = new ArrayList<>();

        // Detect the header row index
        int headerRowIndex = detectHeaderRow(sheet);
        Row headerRow = sheet.getRow(headerRowIndex);

        // Extract headers
        for (Cell cell : headerRow) {
            headers.add(formatter.formatCellValue(cell).trim());
        }

        // Extract data rows
        for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, String> rowData = new LinkedHashMap<>();
            boolean isEmptyRow = true;

            for (int j = 0; j < headers.size(); j++) {
                String value = formatter.formatCellValue(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
                rowData.put(headers.get(j), value);
                if (!value.isEmpty()) isEmptyRow = false;
            }

            if (!isEmptyRow) data.add(rowData);
        }

        // Store only the data in the result
        sheetResult.put("data", data);
        return sheetResult;
    }


    // Find header row by skipping metadata
    private int detectHeaderRow(Sheet sheet) {
        for (int i = 0; i < 5; i++) { // Check first 5 rows max
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String firstCell = formatter.formatCellValue(row.getCell(0)).trim();
            if (!firstCell.startsWith("[") && !firstCell.startsWith("<")) return i;
        }
        return 0;
    }
}