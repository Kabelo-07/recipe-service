package com.km.recipe.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponseDTO {
    private String message;

    private String key;
    private List<String> errors = new ArrayList<>(1);

    public ErrorResponseDTO(String message) {
        this.message = message;
    }

    public ErrorResponseDTO(String message, String key) {
        this.message = message;
        this.key = key;
    }

    public ErrorResponseDTO(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
