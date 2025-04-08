package com.example.smarthealth.Inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MedicineSchemaJSON {
    // Method to generate and return the response schema
    public static JSONObject getResponseSchema() {
        try {
            // Create the outer structure
            JSONObject responseSchema = new JSONObject();
            JSONObject format = new JSONObject();
            JSONObject schema = new JSONObject();
            JSONObject properties = new JSONObject();

            // Add property: name
            properties.put("name", new JSONObject().put("type", "string"));
            // Add property: amount
            properties.put("amount", new JSONObject().put("type", "string"));
            // Add property: treatment (array of strings)
            properties.put("treatment", new JSONObject()
                    .put("type", "array")
                    .put("items", new JSONObject().put("type", "string")));
            // Add property: recommended_dosage
            properties.put("recommended_dosage", new JSONObject().put("type", "string"));
            // Add property: contains (array of strings)
            properties.put("contains", new JSONObject()
                    .put("type", "array")
                    .put("items", new JSONObject().put("type", "string")));
            // Add property: side_effects (array of strings)
            properties.put("side_effects", new JSONObject()
                    .put("type", "array")
                    .put("items", new JSONObject().put("type", "string")));

            // Required fields
            JSONArray required = new JSONArray();
            required.put("name");
            required.put("amount");
            required.put("treatment");
            required.put("recommended_dosage");
            required.put("contains");
            required.put("side_effects");

            // Build schema
            schema.put("type", "object");
            schema.put("properties", properties);
            schema.put("required", required);
            schema.put("additionalProperties", false);

            // Build format
            format.put("type", "json_schema");
            format.put("name", "medicine_info");
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

//    public static void main(String[] args) {
//        // Call the getResponseSchema method and print the schema
//        JSONObject responseSchema = getResponseSchema();
//        if (responseSchema != null) {
//            System.out.println(responseSchema.toString(2));  // Pretty print with 2-space indent
//        }
//    }
}
