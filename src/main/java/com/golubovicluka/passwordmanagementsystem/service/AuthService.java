package com.golubovicluka.passwordmanagementsystem.service;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public CompletableFuture<Boolean> validateUser(String username, String password) {
        System.out.println("Validating user...");
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/users/1")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("Sending request...");
                return response.isSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}