package com.crmportal.response.dto;

public class ChatResponse {
    private String response;
    private boolean success;
    private String error;

    public ChatResponse() {}

    public ChatResponse(String response) {
        this.response = response;
        this.success = true;
    }

    public ChatResponse(String error, boolean success) {
        this.error = error;
        this.success = success;
    }

    // Getters and setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}