package com.sammy.enterpriseresourceplanning.standalone;

import com.sammy.enterpriseresourceplanning.enums.IEmailTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfServiceTest {

    @Autowired
    private PdfService pdfService;

    @Test
    void testGeneratePdf() {
        // Arrange
        String username = "Test User";
        IEmailTemplate templateName = IEmailTemplate.PAYROLL_NOTIFICATION;
        Map<String, Object> variables = new HashMap<>();
        variables.put("month", "JANUARY");
        variables.put("year", "2025");
        variables.put("institution", "Rwanda Government");
        variables.put("amount", "50000.00");
        variables.put("employeeId", "test-employee-id");
        variables.put("paymentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        byte[] pdfContent = pdfService.generatePdf(username, templateName, variables);

        // Assert
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
        
        // Log the size of the generated PDF
        System.out.println("[DEBUG_LOG] Generated PDF size: " + pdfContent.length + " bytes");
        
        // Check if the PDF starts with the PDF signature (%PDF-)
        assertTrue(new String(pdfContent, 0, 5).startsWith("%PDF-"), 
                "Generated content does not start with PDF signature");
    }

    @Test
    void testGeneratePdfWithNullVariables() {
        // Arrange
        String username = "Test User";
        IEmailTemplate templateName = IEmailTemplate.PAYROLL_NOTIFICATION;

        // Act
        byte[] pdfContent = pdfService.generatePdf(username, templateName, null);

        // Assert
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
        
        // Log the size of the generated PDF
        System.out.println("[DEBUG_LOG] Generated PDF size (null variables): " + pdfContent.length + " bytes");
        
        // Check if the PDF starts with the PDF signature (%PDF-)
        assertTrue(new String(pdfContent, 0, 5).startsWith("%PDF-"), 
                "Generated content does not start with PDF signature");
    }

    @Test
    void testGeneratePdfAndSaveToFile(@TempDir Path tempDir) throws Exception {
        // Arrange
        String username = "Test User";
        IEmailTemplate templateName = IEmailTemplate.PAYROLL_NOTIFICATION;
        Map<String, Object> variables = new HashMap<>();
        variables.put("month", "JANUARY");
        variables.put("year", "2025");
        variables.put("institution", "Rwanda Government");
        variables.put("amount", "50000.00");
        variables.put("employeeId", "test-employee-id");
        variables.put("paymentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        byte[] pdfContent = pdfService.generatePdf(username, templateName, variables);
        
        // Save the PDF to a temporary file
        File pdfFile = tempDir.resolve("test-payslip.pdf").toFile();
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(pdfContent);
        }

        // Assert
        assertTrue(pdfFile.exists());
        assertTrue(pdfFile.length() > 0);
        
        // Log the path and size of the saved PDF
        System.out.println("[DEBUG_LOG] PDF saved to: " + pdfFile.getAbsolutePath());
        System.out.println("[DEBUG_LOG] Saved PDF size: " + pdfFile.length() + " bytes");
    }
}