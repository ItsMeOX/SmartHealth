package com.example.smarthealth.chatbot.messages;

import org.json.JSONException;
import org.json.JSONObject;

public class SystemMessage implements Message_API {
    private final String content;

    public SystemMessage(String content) {
        this.content = content;
    }

    @Override
    public JSONObject toJson() {
        try {
            JSONObject message = new JSONObject();
            message.put("role", "system");
            message.put("content", content);
            return message;
        } catch (JSONException e) {
            throw new RuntimeException("Error creating SystemMessage JSON", e);
        }
    }
}
