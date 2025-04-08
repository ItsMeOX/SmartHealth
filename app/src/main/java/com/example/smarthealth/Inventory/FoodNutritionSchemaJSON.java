package com.example.smarthealth.Inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoodNutritionSchemaJSON {
    // Method to generate and return the response schema
    public static JSONObject getResponseSchema() {
        try {
            // Create the outer structure
            JSONObject responseSchema = new JSONObject();
            JSONObject format = new JSONObject();
            JSONObject schema = new JSONObject();
            JSONObject properties = new JSONObject();

            // Add property: Name
            properties.put("Name", new JSONObject().put("type", "string"));
            // Add property: Carbs
            properties.put("Carbs", new JSONObject().put("type", "string"));
            // Add property: Proteins
            properties.put("Proteins", new JSONObject().put("type", "string"));
            // Add property: Fats
            properties.put("Fats", new JSONObject().put("type", "string"));
            // Add property: Fibre
            properties.put("Fibre", new JSONObject().put("type", "string"));
            // Add property: Sugars
            properties.put("Sugars", new JSONObject().put("type", "string"));
            // Add property: Sodium
            properties.put("Sodium", new JSONObject().put("type", "string"));

            // Required fields
            JSONArray required = new JSONArray();
            required.put("Name");
            required.put("Carbs");
            required.put("Proteins");
            required.put("Fats");
            required.put("Fibre");
            required.put("Sugars");
            required.put("Sodium");

            // Build schema
            schema.put("type", "object");
            schema.put("properties", properties);
            schema.put("required", required);
            schema.put("additionalProperties", false);

            // Build format
            format.put("type", "json_schema");
            format.put("name", "food_nutrition_info");
            format.put("schema", schema);
            format.put("strict", true);

            // Combine all
            responseSchema.put("format", format);

            // Return the schema as a string (pretty print with 2-space indent)
            return responseSchema;

        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null if an error occurs
        }
    }

}
