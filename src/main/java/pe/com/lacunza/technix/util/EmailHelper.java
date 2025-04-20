package pe.com.lacunza.technix.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class EmailHelper {

    @Value(value = "${app.technix.valid-emails}")
    private List<String> validEmailsToAlertLowStocks;

    private final JavaMailSender mailSender;

    public void sendMail(String categorieName, String productName, Integer stockQuantity) {
        MimeMessage message = mailSender.createMimeMessage();
        String htmlContent = this.readHTMLTemplate(categorieName, productName, stockQuantity);

        InternetAddress[] recipients = validEmailsToAlertLowStocks
                .stream()
                .map(email -> {
                    try {
                        return new InternetAddress(email);
                    } catch (AddressException e) {
                        log.error("Email inválido: {}", email, e);
                        throw new RuntimeException("Email inválido: " + email);
                    }
                })
                .toArray(InternetAddress[]::new);
        try {
            message.setFrom(new InternetAddress("lacunzamiguel04@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, recipients);
            message.setSubject("Tecnix System's notification: Low Stock");
            message.setContent(htmlContent, MediaType.TEXT_HTML_VALUE);
            mailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Error sending email", ex);
        }
    }
    private String readHTMLTemplate(String categorieName, String productName, Integer stockQuantity) {
        try(var lines = Files.lines(TEMPLATE_PATH)) {
            var html = lines.collect(Collectors.joining());
            return html.replace("{categorieName}", categorieName).replace("{productName}", productName).replace("{stockQuantity}", String.valueOf(stockQuantity));
        } catch (IOException ex) {
            log.error("Can't read email html template", ex);
            throw new RuntimeException();
        }
    }

    private final Path TEMPLATE_PATH = Paths.get("src/main/resources/email/email_template.html");
}