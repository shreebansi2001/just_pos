package com.crmportal.service.impl;

import com.crmportal.config.OpenAIProperties;
import com.crmportal.request.dto.OpenAIRequest;
import com.crmportal.response.dto.OpenAIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIService {

    @Autowired
    private OpenAIProperties openAIProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public String generateResponse(String systemPrompt, String userPrompt) throws IOException {
        OpenAIRequest request = buildRequest(systemPrompt, userPrompt);
        
        HttpPost httpPost = new HttpPost(openAIProperties.getBaseUrl() + "/chat/completions");
        httpPost.setHeader("Authorization", "Bearer " + openAIProperties.getApiKey());
        httpPost.setHeader("Content-Type", "application/json");
        
        String requestBody = objectMapper.writeValueAsString(request);
        httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
        
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//            String responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        	 String responseBody = "";
            
            if (response.getCode() != 200) {
                throw new RuntimeException("OpenAI API call failed: " + responseBody);
            }
            
            OpenAIResponse openAIResponse = objectMapper.readValue(responseBody, OpenAIResponse.class);
            
            if (openAIResponse.getChoices() != null && !openAIResponse.getChoices().isEmpty()) {
                return openAIResponse.getChoices().get(0).getMessage().getContent();
            }
            
            throw new RuntimeException("No response received from OpenAI");
        }
    }

    private OpenAIRequest buildRequest(String systemPrompt, String userPrompt) {
        OpenAIRequest request = new OpenAIRequest();
        request.setModel(openAIProperties.getModel());
        request.setMaxTokens(openAIProperties.getMaxTokens());
        request.setTemperature(openAIProperties.getTemperature());
        
        List<OpenAIRequest.Message> messages = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
            messages.add(new OpenAIRequest.Message("system", systemPrompt));
        }
        
        messages.add(new OpenAIRequest.Message("user", userPrompt));
        
        request.setMessages(messages);
        return request;
    }
}