package org.example.smartexceljsonparser.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationUtilsTest {

    @Test
    public void testValidExcelFile() {
        MultipartFile mockFile = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.ms-excel", new byte[10]
        );
        assertDoesNotThrow(() -> ValidationUtils.validateFile(mockFile));
    }

    @Test
    public void testEmptyFile() {
        MultipartFile mockFile = new MockMultipartFile(
                "file", "empty.xlsx", "application/vnd.ms-excel", new byte[0]
        );
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateFile(mockFile));
    }

    @Test
    public void testInvalidFileType() {
        MultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", new byte[10]
        );
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.validateFile(mockFile));
    }
}
