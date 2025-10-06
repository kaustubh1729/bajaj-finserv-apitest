// File: src/main/java/com/bajajfinserv/apitest/dto/SolutionRequest.java
package com.bajajfinserv.apitest.dto;

public class SolutionRequest {
    private String finalQuery;

    public SolutionRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    // Getters and Setters
    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }
}