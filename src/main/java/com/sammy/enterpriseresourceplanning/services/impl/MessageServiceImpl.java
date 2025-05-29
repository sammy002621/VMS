package com.sammy.enterpriseresourceplanning.services.impl;

import com.sammy.enterpriseresourceplanning.dtos.response.message.MessageResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.enums.IEmailTemplate;
import com.sammy.enterpriseresourceplanning.exceptions.BadRequestException;
import com.sammy.enterpriseresourceplanning.exceptions.NotFoundException;
import com.sammy.enterpriseresourceplanning.models.Message;
import com.sammy.enterpriseresourceplanning.models.PaySlip;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.repositories.IMessageRepository;
import com.sammy.enterpriseresourceplanning.services.IMessageService;
import com.sammy.enterpriseresourceplanning.services.IUserService;
import com.sammy.enterpriseresourceplanning.standalone.EmailService;
import com.sammy.enterpriseresourceplanning.standalone.PdfService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final IMessageRepository messageRepository;
    private final IUserService userService;
    private final EmailService emailService;
    private final PdfService pdfService;

    @Value("${application.payslip.pdf.storage-path:./payslips}")
    private String pdfStoragePath;

    @Override
    @Transactional
    public MessageResponseDTO createMessage(PaySlip paySlip) {
        User employee = paySlip.getEmployee();
        String content = generateMessageContent(employee, paySlip);

        Message message = Message.builder()
                .employee(employee)
                .content(content)
                .month(paySlip.getMonth())
                .year(paySlip.getYear())
                .sent(false)
                .createdAt(LocalDateTime.now())
                .build();

        message = messageRepository.save(message);

        return mapToResponseDTO(message);
    }

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(UUID id) {
        Message message = findMessageEntity(id);

        if (message.isSent()) {
            throw new BadRequestException("Message has already been sent");
        }

        User employee = message.getEmployee();

        try {
            // Prepare variables for the email template
            Map<String, Object> variables = new HashMap<>();
            variables.put("month", Month.of(message.getMonth()).name());
            variables.put("year", message.getYear());
            variables.put("institution", "Rwanda Government");
            variables.put("amount", String.format("%.2f", extractAmountFromMessage(message.getContent())));
            variables.put("employeeId", employee.getId().toString());
            variables.put("paymentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            // Send the email
            emailService.sendEmail(
                employee.getEmail(),
                employee.getFirstName(),
                "Salary Payment Notification",
                IEmailTemplate.PAYROLL_NOTIFICATION,
                variables
            );

            // Generate and save PDF
            generateAndSavePdf(employee, message, variables);

            // Update message status
            message.setSent(true);
            message.setSentAt(LocalDateTime.now());
            message = messageRepository.save(message);

            return mapToResponseDTO(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email notification: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a PDF from the payroll notification template and saves it to the file system.
     *
     * @param employee  The employee
     * @param message   The message
     * @param variables Variables to be used in the template
     */
    private void generateAndSavePdf(User employee, Message message, Map<String, Object> variables) {
        try {
            // Generate PDF
            byte[] pdfContent = pdfService.generatePdf(
                employee.getFirstName(),
                IEmailTemplate.PAYROLL_NOTIFICATION,
                variables
            );

            // Create directory if it doesn't exist
            Path directoryPath = Paths.get(pdfStoragePath);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // Generate filename
            String filename = generatePdfFilename(employee, message);
            Path filePath = directoryPath.resolve(filename);

            // Save PDF to file
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(pdfContent);
            }

            System.out.println("PDF saved to: " + filePath.toAbsolutePath());
        } catch (Exception e) {
            // Log error but don't fail the process
            System.err.println("Failed to generate or save PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates a filename for the PDF based on employee and message information.
     *
     * @param employee The employee
     * @param message  The message
     * @return The filename
     */
    private String generatePdfFilename(User employee, Message message) {
        String monthName = Month.of(message.getMonth()).name();
        return String.format("payslip_%s_%s_%d_%s.pdf",
            employee.getId().toString().replace("-", ""),
            employee.getLastName().toLowerCase(),
            message.getYear(),
            monthName.toLowerCase()
        );
    }

    private double extractAmountFromMessage(String content) {
        // Extract the amount from the message content
        // Example content: "Dear John, Your salary of JANUARY/2025 from Rwanda Government 57400.00 has been credited to your 123e4567-e89b-12d3-a456-426614174000 account successfully."
        try {
            String[] parts = content.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("Government")) {
                    return Double.parseDouble(parts[i + 1]);
                }
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    @Transactional
    public List<MessageResponseDTO> sendAllPendingMessages() {
        List<Message> pendingMessages = messageRepository.findBySent(false);
        List<MessageResponseDTO> sentMessages = new ArrayList<>();

        for (Message message : pendingMessages) {
            try {
                User employee = message.getEmployee();

                // Prepare variables for the email template
                Map<String, Object> variables = new HashMap<>();
                variables.put("month", Month.of(message.getMonth()).name());
                variables.put("year", message.getYear());
                variables.put("institution", "Rwanda Government");
                variables.put("amount", String.format("%.2f", extractAmountFromMessage(message.getContent())));
                variables.put("employeeId", employee.getId().toString());
                variables.put("paymentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                // Send the email
                emailService.sendEmail(
                    employee.getEmail(),
                    employee.getFirstName(),
                    "Salary Payment Notification",
                    IEmailTemplate.PAYROLL_NOTIFICATION,
                    variables
                );

                // Generate and save PDF
                generateAndSavePdf(employee, message, variables);

                // Update message status
                message.setSent(true);
                message.setSentAt(LocalDateTime.now());
                message = messageRepository.save(message);

                sentMessages.add(mapToResponseDTO(message));
            } catch (MessagingException e) {
                // Log the error but continue with other messages
                System.err.println("Failed to send email notification to employee " + message.getEmployee().getId() + ": " + e.getMessage());
            }
        }

        return sentMessages;
    }

    @Override
    public MessageResponseDTO getMessageById(UUID id) {
        Message message = findMessageEntity(id);
        return mapToResponseDTO(message);
    }

    @Override
    public List<MessageResponseDTO> getMessagesByEmployee(UUID employeeId) {
        User employee = userService.findUserById(employeeId);
        return messageRepository.findByEmployee(employee).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseDTO> getMessagesByMonthAndYear(Integer month, Integer year) {
        return messageRepository.findByMonthAndYear(month, year).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseDTO> getAllPendingMessages() {
        return messageRepository.findBySent(false).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseDTO> getAllSentMessages() {
        return messageRepository.findBySent(true).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseDTO> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMessage(UUID id) {
        if (!messageRepository.existsById(id)) {
            throw new NotFoundException("Message not found with id: " + id);
        }
        messageRepository.deleteById(id);
    }

    @Override
    public Message findMessageEntity(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found with id: " + id));
    }

    @Override
    public String generateMessageContent(User employee, PaySlip paySlip) {
        String monthName = Month.of(paySlip.getMonth()).name();
        String institution = "Rwanda Government"; // This could be configurable or retrieved from a settings service

        return String.format(
                "Dear %s, Your salary of %s/%d from %s %.2f has been credited to your %s account successfully.",
                employee.getFirstName(),
                monthName,
                paySlip.getYear(),
                institution,
                paySlip.getNetSalary(),
                employee.getId().toString()
        );
    }

    private MessageResponseDTO mapToResponseDTO(Message message) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .employee(new UserResponseDTO(message.getEmployee()))
                .content(message.getContent())
                .month(message.getMonth())
                .year(message.getYear())
                .sent(message.isSent())
                .createdAt(message.getCreatedAt())
                .sentAt(message.getSentAt())
                .build();
    }
}
