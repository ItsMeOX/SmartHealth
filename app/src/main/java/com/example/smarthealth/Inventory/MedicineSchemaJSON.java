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

            // Property: name (string)
            properties.put("name", new JSONObject().put("type", "string"));

            // Property: category (enum)
            properties.put("category", new JSONObject()
                    .put("type", "string")
                    .put("enum", new JSONArray()
                            .put("Liquids")
                            .put("Pills")
                            .put("Other")));

            // Property: amount_pill (string)
            properties.put("amount_pill", new JSONObject().put("type", "string"));

            // Property: amount_ml (string)
            properties.put("amount_ml", new JSONObject().put("type", "string"));

            // Property: treatment (array of enum strings)
            JSONArray treatmentEnum = new JSONArray()
                    .put("Cough")
                    .put("Fever")
                    .put("Cold")
                    .put("Diarrhea")
                    .put("Phlegm")
                    .put("Painkiller")
                    .put("Diabetes")
                    .put("High Cholesterol")
                    .put("Dry Eyes")
                    .put("High Blood Pressure")
                    .put("Vomiting")
                    .put("Giddiness")
                    .put("Others");

            JSONObject treatmentItems = new JSONObject()
                    .put("type", "string")
                    .put("enum", treatmentEnum);

            properties.put("treatment", new JSONObject()
                    .put("type", "array")
                    .put("items", treatmentItems));

            // Property: recommended_dosage (string)
            properties.put("recommended_dosage", new JSONObject().put("type", "string"));

            // Property: contains (array of strings)
            properties.put("contains", new JSONObject()
                    .put("type", "array")
                    .put("items", new JSONObject().put("type", "string")));

            // Property: side_effects (array of strings)
            properties.put("side_effects", new JSONObject()
                    .put("type", "array")
                    .put("items", new JSONObject().put("type", "string")));

            // Required fields
            JSONArray required = new JSONArray()
                    .put("name")
                    .put("category")
                    .put("amount_pill")
                    .put("amount_ml")
                    .put("treatment")
                    .put("recommended_dosage")
                    .put("contains")
                    .put("side_effects");

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

            // Final response schema
            responseSchema.put("format", format);

            return responseSchema;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args) {
//        JSONObject responseSchema = getResponseSchema();
//        if (responseSchema != null) {
//            System.out.println(responseSchema.toString(2));
//        }
//    }
}
