package org.example.smartexceljsonparser.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JsonServiceTest {

    @Autowired
    private JsonService jsonService;

    @Test
    public void testJsonToExcelConversion() throws Exception {
        // Sample JSON structure matching your format
        String json = """
            {
                "Policy Info": {
                    "key1": "value1",
                    "key2": "value2"
                }
            }
        """;

        byte[] excelBytes = jsonService.convertJsonToExcel(json.getBytes());
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
    }
}
