package com.example.smarthealth.chatbot.messages;

import org.json.JSONException;
import org.json.JSONObject;

public class UserMessage implements Message_API {
    private final String content;

    public UserMessage(String content) {
        this.content = content;
    }

    @Override
    public JSONObject toJson() {
        try {
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", content);
            return message;
        } catch (JSONException e) {
            throw new RuntimeException("Error creating UserMessage JSON", e);
        }
    }
}
