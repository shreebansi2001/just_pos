package com.crmportal.service.impl;

import com.crmportal.config.GeminiProperties;
import com.crmportal.request.dto.GeminiRequest;
import com.crmportal.response.dto.GeminiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.util.Timeout;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GeminiService {

    @Autowired
    private GeminiProperties geminiProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(
                    RequestConfig.custom()
                            .setConnectTimeout(Timeout.ofMilliseconds(30000))
                            .setResponseTimeout(Timeout.ofMilliseconds(60000))
                            .build()
            )
            .build();

    private final List<String> fallbackModels = Arrays.asList(
            "gemini-2.5-flash",
            "gemini-2.5-pro",
            "gemini-2.0-flash-lite"
    );

    public String generateResponse(String systemPrompt, String userPrompt) throws IOException {

        String finalPrompt = buildFinalPrompt(systemPrompt, userPrompt);

        Exception lastException = null;

        String primaryModel = geminiProperties.getModel();

        // ✅ Try primary model first
        try {
            return callGeminiAPI(primaryModel, finalPrompt);
        } catch (Exception ex) {
            lastException = ex;
            System.out.println("Primary model failed: " + primaryModel + " → " + ex.getMessage());
        }

        // 🔁 Fallback models
        for (String model : fallbackModels) {

            if (model.equalsIgnoreCase(primaryModel)) continue;

            try {
                return callGeminiAPI(model, finalPrompt);
            } catch (Exception ex) {
                lastException = ex;
                System.out.println("Fallback failed: " + model + " → " + ex.getMessage());
            }
        }

        throw new RuntimeException("All Gemini models failed", lastException);
    }

    private String callGeminiAPI(String model, String prompt) throws IOException {

        GeminiRequest request = buildRequest(prompt);

        String url = geminiProperties.getBaseUrl()
                + "/models/" + model
                + ":generateContent?key=" + geminiProperties.getApiKey();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");

        String requestBody = objectMapper.writeValueAsString(request);
        httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {

        	InputStream inputStream = response.getEntity().getContent();

        	ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        	int nRead;
        	byte[] data = new byte[1024];

        	while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        	    buffer.write(data, 0, nRead);
        	}

        	buffer.flush();

        	String responseBody = new String(
        	        buffer.toByteArray(),
        	        StandardCharsets.UTF_8
        	);

            if (response.getCode() != 200) {
                throw new RuntimeException(extractErrorMessage(responseBody));
            }

            return extractTextResponse(responseBody);
        }
    }

    private String extractTextResponse(String responseBody) throws IOException {

        GeminiResponse geminiResponse =
                objectMapper.readValue(responseBody, GeminiResponse.class);

        if (geminiResponse.getCandidates() != null &&
                !geminiResponse.getCandidates().isEmpty()) {

            GeminiResponse.Candidate candidate = geminiResponse.getCandidates().get(0);

            if (candidate.getContent() != null &&
                    candidate.getContent().getParts() != null &&
                    !candidate.getContent().getParts().isEmpty()) {

                return candidate.getContent().getParts().get(0).getText();
            }
        }

        throw new RuntimeException("No valid response from Gemini");
    }

    private String extractErrorMessage(String responseBody) {
        try {
            JsonNode node = objectMapper.readTree(responseBody);
            return node.path("error").path("message").asText("Unknown error from Gemini");
        } catch (Exception e) {
            return "Gemini API error: " + responseBody;
        }
    }

    private String buildFinalPrompt(String systemPrompt, String userPrompt) {

        StringBuilder finalPrompt = new StringBuilder();

        if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
            finalPrompt.append(systemPrompt.trim()).append("\n");
        }

        if (userPrompt != null) {
            finalPrompt.append(userPrompt.trim());
        }

        return finalPrompt.toString();
    }

    private GeminiRequest buildRequest(String prompt) {

        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content =
                new GeminiRequest.Content(Collections.singletonList(part));

        GeminiRequest request = new GeminiRequest();
        request.setContents(Collections.singletonList(content));

        return request;
    }
}