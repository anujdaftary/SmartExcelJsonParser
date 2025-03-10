package org.example.smartexceljsonparser.service;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.smartexceljsonparser.service.ExcelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ExcelServiceTest {

    @Autowired
    private ExcelService excelService;

    @Test
    public void testExcelToJsonConversion() throws Exception {
        // ✅ Create an in-memory Excel file
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Policy Info");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(101);
        dataRow.createCell(1).setCellValue("Test Policy");

        workbook.write(out);
        workbook.close();

        // ✅ Convert to MockMultipartFile
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.ms-excel", new ByteArrayInputStream(out.toByteArray())
        );

        // ✅ Test the service
        String json = excelService.convertExcelToJson(mockFile);
        assertNotNull(json);
        assertTrue(json.contains("Test Policy")); // Check data exists
    }
}
