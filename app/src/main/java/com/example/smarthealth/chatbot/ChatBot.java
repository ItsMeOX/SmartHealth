package com.example.smarthealth.chatbot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class ChatBot {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_Key = "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";

    private static final MediaType JSON = MediaType.parse("application/json");
    private final OkHttpClient client = new OkHttpClient();;
    public final List<JSONObject> conversationHistory = new ArrayList<>();

    public ChatBot() {
        addSystemMessage("You are helpful assistant named MedicalBot");
    }

    public String getAPI_URL() {
        return API_URL;
    }


    public String getAPI_Key() {
        return API_Key;
    }


    private void addSystemMessage(String content) {
        try {
            JSONObject message = new JSONObject();
            message.put("role", "system");
            message.put("content", content);
            conversationHistory.add(message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMessage(String role, String content) {
        try {
            JSONObject message = new JSONObject();
            message.put("role", role);
            message.put("content", content);
            conversationHistory.add(message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private JSONObject buildPrompt() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-4o");
            jsonBody.put("messages", new JSONArray(conversationHistory));
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
            jsonBody.put("top_p", 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonBody;
        }
    public JSONObject getPrompt() {
        return buildPrompt();
    }





}
