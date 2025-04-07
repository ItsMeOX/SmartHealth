package com.example.smarthealth.api_service;

public class GoogleAuthRequest {
    private String email;
    private String idToken;

    public GoogleAuthRequest(String email, String idToken) {
        this.email = email;
        this.idToken = idToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
