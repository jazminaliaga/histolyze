package com.histolyze.histolyze.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine; // O spring5 según tu versión

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfService {

    private final SpringTemplateEngine templateEngine;

    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generarPdf(String templateName, Map<String, Object> datos) {
        // 1. Contexto de Thymeleaf (las variables que van al HTML)
        Context context = new Context();
        context.setVariables(datos);

        // 2. Renderizar HTML a String
        String html = templateEngine.process(templateName, context);

        // 3. Convertir HTML a PDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode(); // Opcional, mejora rendimiento
            // Importante: Asegura que el HTML sea XHTML válido o usa Jsoup para limpiar si es necesario
            builder.withHtmlContent(html, "");
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }
}