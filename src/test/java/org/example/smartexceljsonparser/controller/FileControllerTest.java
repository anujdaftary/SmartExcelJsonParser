package org.example.smartexceljsonparser.controller;


import org.example.smartexceljsonparser.controller.FileController;
import org.junit.jupiter.api.Test;
import org.example.smartexceljsonparser.service.ExcelService;
import org.example.smartexceljsonparser.service.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExcelService excelService;

    @MockBean
    private JsonService jsonService;

    @Test
    public void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testExcelToJsonConversion() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.ms-excel", "test data".getBytes()
        );

        when(excelService.convertExcelToJson(any())).thenReturn("{}");

        mockMvc.perform(multipart("/convert")
                        .file(file)
                        .param("conversionType", "excelToJson"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("json"));
    }
}
