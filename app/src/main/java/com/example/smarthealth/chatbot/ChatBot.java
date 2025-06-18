package com.example.smarthealth.chatbot;

import com.example.smarthealth.chatbot.messages.Message_API;
import com.example.smarthealth.chatbot.messages.SystemMessage;



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
    private final String systemPrompt =
            "You are MedicalBot, a smart, caring, and professional virtual assistant created by SmartHealth Company. " +
                    "You are designed to help users manage and improve their health on a daily basis. Your top priority is the user's well-being.\n\n" +
                    "As MedicalBot, you must:\n" +
                    "• Understand and remember the user's daily schedule, health routines, and lifestyle habits.\n" +
                    "• Monitor and support the user’s nutrition intake and offer helpful suggestions.\n" +
                    "• Remind the user of their medications, hydration, sleep, and any other daily medical needs.\n" +
                    "• Provide concise, easy-to-follow, and medically sound recommendations when the user feels unwell or asks for advice.\n" +
                    "• Communicate with empathy, clarity, and professionalism at all times.\n\n" +
                    "Always respond in a way that is helpful, approachable, and focused on helping the user live a healthier life, powered by SmartHealth.";

    public ChatBot() {

        addMessage(new SystemMessage(systemPrompt));
    }

    public String getAPI_URL() {
        return API_URL;
    }


    public String getAPI_Key() {
        return API_Key;
    }


//    private void addSystemMessage(String content) {
//        try {
//            JSONObject message = new JSONObject();
//            message.put("role", "system");
//            message.put("content", content);
//            conversationHistory.add(message);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public void addMessage(String role, String content) {
//        try {
//            JSONObject message = new JSONObject();
//            message.put("role", role);
//            message.put("content", content);
//            conversationHistory.add(message);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public void addMessage(Message_API message) {
        conversationHistory.add(message.toJson());
    }


    private JSONObject buildPrompt() {
        JSONObject jsonBody = new JSONObject();
        JSONArray messagesArray = new JSONArray(conversationHistory);

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
