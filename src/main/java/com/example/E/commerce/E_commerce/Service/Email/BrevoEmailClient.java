
package com.example.E.commerce.E_commerce.Service.Email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailClient {

    private final String apiKey;
    private final String senderEmail;
    private final String senderName;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public BrevoEmailClient(
            @Value("${brevo.api-key}") String apiKey,
            @Value("${brevo.from}") String senderEmail,
            @Value("${brevo.from-name}") String senderName,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.objectMapper = objectMapper;
    }

    public void sendEmail(String to, String subject, String body) {

        try {

            String json = objectMapper.writeValueAsString(
                    Map.of(
                            "sender", Map.of(
                                    "name", senderName,
                                    "email", senderEmail
                            ),
                            "to", List.of(
                                    Map.of("email", to)
                            ),
                            "subject", subject,
                            "textContent", body
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://api.brevo.com/v3/smtp/email"
                    ))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 201) {
                throw new IllegalStateException(
                        "Brevo email request failed: HTTP "
                                + response.statusCode()
                );
            }

        } catch (InterruptedException ex) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Email request interrupted", ex
            );

        } catch (IOException ex) {

            throw new IllegalStateException(
                    "Unable to send email through Brevo", ex
            );
        }
    }
}
