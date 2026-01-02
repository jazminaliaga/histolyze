package com.histolyze.histolyze.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generarPdf(String templateName, Map<String, Object> variables) {
        // 1. Crear el contexto de Thymeleaf e inyectar las variables
        Context context = new Context();
        context.setVariables(variables);

        // 2. Procesar el HTML: Thymeleaf reemplaza los th:text con los datos reales
        // Nota: Asegúrate que tus templates estén en src/main/resources/templates/pdf/
        String html = templateEngine.process("pdf/" + templateName, context);

        // 3. Convertir ese HTML a PDF usando OpenHTMLtoPDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode(); // Mejora el rendimiento
            // Si tienes imágenes locales, aquí se configura la base URL, por ahora null
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF del informe: " + templateName, e);
        }
    }
}