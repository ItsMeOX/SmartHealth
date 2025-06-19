package com.example.smarthealth.Inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class ChatBotImageFood {
    private static final String API_URL = "https://api.openai.com/v1/responses";
    private static final String API_Key = "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";

    private final JSONObject response_schema = FoodNutritionSchemaJSON.getResponseSchema();

    private final String systemPrompt = "You are an AI specialized in analyzing food packaging and nutrition labels from images. " +
            "Carefully follow a step-by-step approach to extract relevant information and respond in JSON format.\n\n" +
            "First, analyze the image to identify the food item. Then, extract nutritional values based on any visible nutrition labels or text.\n\n" +
            "1. **Name of the food**\n" +
            "   - Identify the name of the food item clearly based on visual features or visible text.\n\n" +
            "2. **Nutritional Components**\n" +
            "   - Extract the value (in grams) of each of the following, if available:\n" +
            "     - Carbs\n" +
            "     - Proteins\n" +
            "     - Fats\n" +
            "     - Fibre\n" +
            "     - Sugars\n" +
            "     - Sodium\n\n" +
            "> If any value is not mentioned or unclear in the image, search on the website like \"Restaurant Food Information: name of the food item\" to find the nutritional information.\n" +
            "> If you can still not find the value, return a value based on the average nutritional information of similar food items.\n" +
            "> If the value is in milligrams (mg), convert it to grams and use double values (e.g., 120mg = 0.12).\n\n" +
            "**Format the response strictly in JSON**, using lowercase keys and underscores where applicable. Example format:\n" +
            "{\n" +
            "    \"Name\": \"Granola Bar\",\n" +
            "    \"Carbs\": \"23\",\n" +
            "    \"Proteins\": \"5\",\n" +
            "    \"Fats\": \"8\",\n" +
            "    \"Fibre\": \"3\",\n" +
            "    \"Sugars\": \"10\",\n" +
            "    \"Sodium\": \"0.12\"\n" +
            "}\n\n" +
            "Do not include any commentary or references. Only return the extracted data in JSON format.";


    private final String userPrompt = "Analyze this food image and extract the required information";
    private final String base64_image;

    public ChatBotImageFood(String base64_image) {
        this.base64_image = base64_image;
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
            messageContent.put("type", "input_text");
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
//            imageContent.put("image_url", "dat")
            imageContent.put("image_url", "data:image/jpeg;base64," + image);
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
            jsonBody.put("input", analyzeImage());
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
