package com.sammy.enterpriseresourceplanning.standalone;

import com.sammy.enterpriseresourceplanning.enums.IEmailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating PDF documents from Thymeleaf templates.
 */
@Service
@RequiredArgsConstructor
public class PdfService {

    private final SpringTemplateEngine templateEngine;

    /**
     * Generates a PDF document from a Thymeleaf template.
     *
     * @param username      The username to be displayed in the template
     * @param templateName  The name of the template to use
     * @param variables     Variables to be used in the template
     * @return              The generated PDF as a byte array
     */
    public byte[] generatePdf(String username, IEmailTemplate templateName, Map<String, Object> variables) {
        try {
            // Create a new context and set variables
            if (variables == null) {
                variables = new HashMap<>();
            }

            variables.put("username", username);
            variables.put("supportEmail", "contact@sammy.com");
            variables.put("currentYear", LocalDate.now().getYear());

            Context context = new Context();
            context.setVariables(variables);

            // Process the template
            String htmlContent = templateEngine.process(templateName.getName(), context);

            // Convert HTML to PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent, null);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
