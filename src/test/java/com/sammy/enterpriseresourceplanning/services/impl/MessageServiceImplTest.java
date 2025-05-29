package com.sammy.enterpriseresourceplanning.services.impl;

import com.sammy.enterpriseresourceplanning.models.Message;
import com.sammy.enterpriseresourceplanning.models.PaySlip;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.repositories.IMessageRepository;
import com.sammy.enterpriseresourceplanning.standalone.EmailService;
import com.sammy.enterpriseresourceplanning.standalone.PdfService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {

    @Mock
    private IMessageRepository messageRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGenerateAndSavePdf(@TempDir Path tempDir) throws MessagingException, IOException {
        // Arrange
        String tempDirPath = tempDir.toString();
        System.out.println("[DEBUG_LOG] Using temp directory: " + tempDirPath);

        // Set the PDF storage path to the temp directory
        ReflectionTestUtils.setField(messageService, "pdfStoragePath", tempDirPath);

        // Create test data
        User employee = new User();
        employee.setId(UUID.randomUUID());
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");

        Message message = new Message();
        message.setEmployee(employee);
        message.setMonth(1); // January
        message.setYear(2025);
        message.setContent("Dear John, Your salary of JANUARY/2025 from Rwanda Government 50000.00 has been credited to your " + employee.getId() + " account successfully.");

        // Mock PDF service to return a sample PDF
        byte[] samplePdf = "Sample PDF Content".getBytes();
        when(pdfService.generatePdf(anyString(), any(), any())).thenReturn(samplePdf);

        // Act - Call the private method using reflection
        ReflectionTestUtils.invokeMethod(
            messageService, 
            "generateAndSavePdf", 
            employee, 
            message, 
            new HashMap<String, Object>()
        );

        // Assert
        // Verify PDF service was called
        verify(pdfService, times(1)).generatePdf(eq(employee.getFirstName()), any(), any());

        // Check if PDF file was created
        String expectedFilename = String.format("payslip_%s_%s_%d_%s.pdf",
            employee.getId().toString().replace("-", ""),
            employee.getLastName().toLowerCase(),
            message.getYear(),
            Month.of(message.getMonth()).name().toLowerCase()
        );

        Path expectedFilePath = tempDir.resolve(expectedFilename);
        System.out.println("[DEBUG_LOG] Expected file path: " + expectedFilePath);

        assertTrue(Files.exists(expectedFilePath), "PDF file should be created");

        // Verify file content
        byte[] fileContent = Files.readAllBytes(expectedFilePath);
        assertArrayEquals(samplePdf, fileContent, "File content should match the generated PDF");

        System.out.println("[DEBUG_LOG] PDF file size: " + fileContent.length + " bytes");
    }

    @Test
    void testGenerateAndSavePdfCreatesDirectoryIfNotExists(@TempDir Path tempDir) throws MessagingException, IOException {
        // Arrange
        // Create a non-existent subdirectory path
        Path nonExistentDir = tempDir.resolve("non-existent-dir");
        String nonExistentDirPath = nonExistentDir.toString();
        System.out.println("[DEBUG_LOG] Using non-existent directory: " + nonExistentDirPath);

        // Set the PDF storage path to the non-existent directory
        ReflectionTestUtils.setField(messageService, "pdfStoragePath", nonExistentDirPath);

        // Create test data
        User employee = new User();
        employee.setId(UUID.randomUUID());
        employee.setFirstName("Jane");
        employee.setLastName("Smith");
        employee.setEmail("jane.smith@example.com");

        Message message = new Message();
        message.setEmployee(employee);
        message.setMonth(2); // February
        message.setYear(2025);
        message.setContent("Dear Jane, Your salary of FEBRUARY/2025 from Rwanda Government 60000.00 has been credited to your " + employee.getId() + " account successfully.");

        // Mock PDF service to return a sample PDF
        byte[] samplePdf = "Another Sample PDF Content".getBytes();
        when(pdfService.generatePdf(anyString(), any(), any())).thenReturn(samplePdf);

        // Act - Call the private method using reflection
        ReflectionTestUtils.invokeMethod(
            messageService, 
            "generateAndSavePdf", 
            employee, 
            message, 
            new HashMap<String, Object>()
        );

        // Assert
        // Verify directory was created
        assertTrue(Files.exists(nonExistentDir), "Directory should be created");
        assertTrue(Files.isDirectory(nonExistentDir), "Path should be a directory");

        // Check if PDF file was created
        String expectedFilename = String.format("payslip_%s_%s_%d_%s.pdf",
            employee.getId().toString().replace("-", ""),
            employee.getLastName().toLowerCase(),
            message.getYear(),
            Month.of(message.getMonth()).name().toLowerCase()
        );

        Path expectedFilePath = nonExistentDir.resolve(expectedFilename);
        System.out.println("[DEBUG_LOG] Expected file path: " + expectedFilePath);

        assertTrue(Files.exists(expectedFilePath), "PDF file should be created");

        System.out.println("[DEBUG_LOG] PDF file size: " + Files.size(expectedFilePath) + " bytes");
    }
}
