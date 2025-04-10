package com.example.smarthealth.Inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class ChatBotImageMedicine {
    private static final String API_URL = "https://api.openai.com/v1/responses";
    private static final String API_Key = "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";


    private final JSONObject response_schema = MedicineSchemaJSON.getResponseSchema();

    private final String systemPrompt = "You are an AI specialized in analyzing medicine packaging images.\n" +
            "Carefully follow a step-by-step approach to extract relevant information and respond in JSON format.\n" +
            "First, analyze the image to identify the text on the package. Then, extract key details and categorize them under the following:\n\n" +
            "1. **Name of the medicine**\n" +
            "   - Identify the brand or generic name clearly displayed on the packaging.\n" +
            "2. **Category of the medicine**\n" +
            "   - Identify the type of medicine (Select from: Pills, Liquids, Others)\n" +
            "3. **Amount of the medicine**\n" +
            "   - Look for numerical values indicating dosage strength. It should be the integer indicating the number of pills (tablets) stated on the picture. If there is no number stating the pill, return 0.\n" +
            "4. **Amount in ml**\n" +
            "   - Look for numerical values indicating dosage strength. It should be the integer indicating the amount in ml stated on the picture.\n" +
            "5. **Treatment(s)**\n" +
            "   - Determine the intended medical condition(s) treated by the medicine.\n" +
            "     (Select from: Cough, Fever, Cold, Diarrhea, Phlegm, Painkiller, Diabetes, High Cholesterol, Dry Eyes, High Blood Pressure, Others). Can be multiple values.\n" +
            "6. **Recommended Dosage**\n" +
            "   - Extract usage instructions such as frequency and amount per dose.\n" +
            "7. **Contains**\n" +
            "   - List the main chemical ingredients present in the medicine.\n" +
            "8. **Side Effects**\n" +
            "   - Identify potential adverse effects, if mentioned. If none are mentioned, indicate with \"N/A\" \n\n" +
            "If any information is unavailable, search on the website like \"Medicine Information: name of the medicine\" to find the details.\n" +
            "If you still cannot find the value, return a value based on the average information of similar medicines.\n\n" +
            "Format the response strictly in JSON, ensuring keys are in lowercase and use underscores. Example response:\n" +
            "    {\n" +
            "        \"name\": \"Paracetamol\",\n" +
            "        \"category\": \"Pills\",\n" +
            "        \"amount_pill\": \"500\",\n" +
            "        \"amount_ml\": \"0\",\n" +
            "        \"treatment\": [\"Painkiller\"],\n" +
            "        \"recommended_dosage\": \"1 tablet every 6 hours\",\n" +
            "        \"contains\": [\"Paracetamol\"],\n" +
            "        \"side_effects\": [\"Nausea\", \"Rash\"]\n" +
            "    }\n\n" +

            "Do not include any commentary or references. Only return the extracted data in JSON format.";

    private final String userPrompt = "Analyze this medical product image and extract the required information.";
    private final String base64_image;

    public ChatBotImageMedicine(String base64_image) {
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
//            imageContent.put("image_url", "dat");
            imageContent.put("image_url", "data:image/jpeg;base64," +image);
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
