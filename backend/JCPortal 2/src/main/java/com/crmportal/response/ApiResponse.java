package com.crmportal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int status;

    public static <T> ApiResponse<T> success(String message, T data, int status) {

        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        response.status = status;

        return response;
    }

    public static <T> ApiResponse<T> error(String message, int status) {

        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.status = status;

        return response;
    }
}
