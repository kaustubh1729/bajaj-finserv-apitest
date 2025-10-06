// File: src/main/java/com/bajajfinserv/apitest/runner/ApiTestRunner.java
package com.bajajfinserv.apitest.runner;

import com.bajajfinserv.apitest.dto.SolutionRequest;
import com.bajajfinserv.apitest.dto.WebhookRequest;
import com.bajajfinserv.apitest.dto.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiTestRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting API Test...");

        // --- Step 1: Generate Webhook ---
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        // !!! IMPORTANT: REPLACE WITH YOUR DETAILS !!!
        WebhookRequest webhookRequest = new WebhookRequest("Kaustubh", "112215159", "kaustubh.h.salunkhe1729@gmail.com");

        System.out.println("Sending request to generate webhook...");
        ResponseEntity<WebhookResponse> webhookResponseEntity = restTemplate.postForEntity(
                generateWebhookUrl, webhookRequest, WebhookResponse.class);

        if (webhookResponseEntity.getStatusCode().is2xxSuccessful()) {
            WebhookResponse webhookResponse = webhookResponseEntity.getBody();
            String webhookUrl = webhookResponse.getWebhook();
            String accessToken = webhookResponse.getAccessToken();

            System.out.println("Webhook URL received: " + webhookUrl);
            System.out.println("Access Token received: " + accessToken);

            // --- Step 2: Solve the SQL Problem ---
            String regNo = webhookRequest.getRegNo();
            String finalQuery = getFinalQuery(regNo);

            System.out.println("Final SQL Query: " + finalQuery);

            // --- Step 3: Submit the Solution ---
            submitSolution(webhookUrl, accessToken, finalQuery);
        } else {
            System.err.println("Failed to generate webhook. Status code: " + webhookResponseEntity.getStatusCode());
            System.err.println("Response body: " + webhookResponseEntity.getBody());
        }
    }

    private String getFinalQuery(String regNo) {
        // Extract the last two digits of the registration number
        int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));

        // Determine if the number is odd or even
        if (lastTwoDigits % 2 != 0) {
            // Odd Number: Question 1
            return "WITH RankedSalaries AS ( SELECT e.*, DENSE_RANK() OVER (PARTITION BY d.id ORDER BY e.salary DESC) as salary_rank FROM Employee e JOIN Department d ON e.departmentId = d.id) SELECT d.name AS Department, rs.name AS Employee, rs.salary AS Salary FROM RankedSalaries rs JOIN Department d ON rs.departmentId = d.id WHERE rs.salary_rank <= 3;";
        } else {
            // Even Number: Question 2
            return "SELECT score, DENSE_RANK() OVER (ORDER BY score DESC) as 'rank' FROM Scores;";
        }
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        System.out.println("Submitting the final query...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        SolutionRequest solutionRequest = new SolutionRequest(finalQuery);

        HttpEntity<SolutionRequest> requestEntity = new HttpEntity<>(solutionRequest, headers);

        ResponseEntity<String> solutionResponseEntity = restTemplate.exchange(
                webhookUrl, HttpMethod.POST, requestEntity, String.class);

        if (solutionResponseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("Solution submitted successfully!");
            System.out.println("Response: " + solutionResponseEntity.getBody());
        } else {
            System.err.println("Failed to submit solution. Status code: " + solutionResponseEntity.getStatusCode());
            System.err.println("Response body: " + solutionResponseEntity.getBody());
        }
    }
}