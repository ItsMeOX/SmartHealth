package com.example.smarthealth;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ConductGMapWebserviceSearch {

    private String googlePlaceData, url;

    private List<HashMap<String, String>> allNearbyPlaces;

    private DownloadUrl downloadUrl;

    public ConductGMapWebserviceSearch()
    {
        allNearbyPlaces = new ArrayList<>();
    }

    public List<? extends Object> getSearchResults(String url, String jsonBody, String fieldMask, boolean hasFieldMask, SearchType searchType) {
        downloadUrl = new DownloadUrl();
        try{
            googlePlaceData = downloadUrl.readTheUrl(url, jsonBody, fieldMask, hasFieldMask);
            if (searchType == SearchType.SEARCH_BAR) {
                JSONArray jsonArray = parseDetails(googlePlaceData, "places");
                return getAllPlacePredictions(jsonArray);
            } else if (searchType == searchType.PROXIMITY_SEARCH) {
                JSONArray jsonArray = parseDetails(googlePlaceData, "places");
                return getAllNearbyPlaces(jsonArray);
            }
        }catch (IOException|JSONException e)
        {
            e.printStackTrace();
        }
        return java.util.Collections.emptyList();
    }

    private HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJSON) throws JSONException {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        if (!googlePlaceJSON.isNull("displayName")) {
            placeName = googlePlaceJSON.getString("displayName");
        }
        if (!googlePlaceJSON.isNull("formattedAddress")) {
            vicinity = googlePlaceJSON.getString("formattedAddress");
        }
        latitude = googlePlaceJSON.getJSONObject("location").getString("latitude");
        longitude = googlePlaceJSON.getJSONObject("location").getString("longitude");

        googlePlaceMap.put("place_name", placeName);
        googlePlaceMap.put("vicinity", vicinity);
        googlePlaceMap.put("lat", latitude);
        googlePlaceMap.put("lng", longitude);

        Log.d("Place data", googlePlaceMap.toString());

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) throws JSONException {
        int counter = jsonArray.length();
        List<HashMap<String, String>> allNearbyPlaces = new ArrayList<>();
        HashMap<String, String> nearbyPlaceDetails = null;

        for (int i = 0; i < counter; i++)
        {
            nearbyPlaceDetails = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
            allNearbyPlaces.add(nearbyPlaceDetails);
        }
        return allNearbyPlaces;
    }

    public JSONArray parseDetails(String jsonData, String headerKey) throws JSONException {
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(jsonData);
        jsonArray = jsonObject.getJSONArray(headerKey);

        return jsonArray;
    }

    public List<HashMap<String, String>> getAllPlacePredictions(JSONArray jsonArray) throws JSONException {
        List<HashMap<String, String>> placePredictions = new ArrayList<>();
        for (int i =0; i < jsonArray.length(); i++) //iterate through all JSON objects!
        {
            HashMap<String, String> onePlacePredictionDetails = new HashMap<>();
            String formattedAddres = jsonArray.getJSONObject(i).getString("formattedAddress");
            String latitude = jsonArray.getJSONObject(i).getJSONObject("location").getString("latitude");
            String longitude = jsonArray.getJSONObject(i).getJSONObject("location").getString("longitude");
            String placeName = jsonArray.getJSONObject(i).getJSONObject("displayName").getString("text");

            onePlacePredictionDetails.put("Place Name", placeName);
            onePlacePredictionDetails.put("Address", formattedAddres);
            onePlacePredictionDetails.put("Latitude", latitude);
            onePlacePredictionDetails.put("Longitude", longitude);

            placePredictions.add(onePlacePredictionDetails);
        }
        return placePredictions;
    }

}
