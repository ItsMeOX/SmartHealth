package com.example.smarthealth.chatbot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class ChatBotImage {
    private static final String API_URL = "https://api.openai.com/v1/responses";
    private static final String API_Key = "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";

    private static final MediaType JSON = MediaType.parse("application/json");
    private final OkHttpClient client = new OkHttpClient();;

    private final JSONObject response_schema = MedicineSchemaJSON.getResponseSchema();

    private final String systemPrompt = "You are an AI specialized in analyzing medicine packaging images. Extract the following information and respond in JSON format:\n\n" +
            "- **Name of the medicine**\n" +
            "- **Amount of the medicine** (e.g., 500mg, 10 tablets)\n" +
            "- **Treatment(s)** (Select from: Cough, Fever, Cold, Diarrhea, Phlegm, Painkiller, Diabetes, High Cholesterol, Dry Eyes, High Blood Pressure, Others)\n" +
            "- **Recommended Dosage** (Dosage instructions)\n" +
            "- **Contains** (Main chemical ingredients)\n" +
            "- **Side Effects** (Possible side effects)\n\n" +
            "If the information is not available, respond with \"N/A\".\n" +
            "Ensure the JSON keys are in lowercase and use underscores.\n" +
            "Additionally, search on the web for missing information.";
    private final String userPrompt = "Analyze this image";
    private final String base64_image;

    public ChatBotImage(String image_path) {

        this.base64_image = ImageURL.encodeImageToBase64(new File(image_path));;
    }
    public String getAPI_URL() {
        return API_URL;
    }
    public String getAPI_Key() {
        return API_Key;
    }


    public JSONArray analyzeImage() {
        try {
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);

            // Build the text message content
            JSONObject textContent = createMessageContent(userPrompt);

            // Build the image message content
            JSONObject imageContent = createImageMessageContent(base64_image);


            JSONArray messageContent = new JSONArray();
            messageContent.put(textContent);
            messageContent.put(imageContent);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", messageContent);

            JSONArray conversation = new JSONArray();
            conversation.put(systemMessage);
            conversation.put(userMessage);

            return conversation;

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject createMessageContent(String content) {
        JSONObject messageContent = new JSONObject();
        try{
            messageContent.put("type", "text");
            messageContent.put("text", content);
            return messageContent;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private JSONObject createImageMessageContent(String image) {
        JSONObject imageContent = new JSONObject();
        try {
            imageContent.put("type", "input_image");
            imageContent.put("image_url", "data:image/jpeg;base64,"+ image);
            imageContent.put("image_url", image);

            return imageContent;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private JSONObject buildPrompt() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-4o");

            // Adding the "tools" parameter with web_search_preview
            JSONArray toolsArray = new JSONArray();
            JSONObject toolObject = new JSONObject();
            toolObject.put("type", "web_search_preview");
            toolsArray.put(toolObject);

            jsonBody.put("tools", toolsArray);
            jsonBody.put("text", response_schema);
            jsonBody.put("messages", analyzeImage());
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
