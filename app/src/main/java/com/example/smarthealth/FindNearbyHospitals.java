package com.example.smarthealth;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindNearbyHospitals extends AsyncTask<Object, String, String> {

    private String googlePlaceData, url;

    private List<HashMap<String, String>> allNearbyPlaces;

    public  FindNearbyHospitals()
    {
        allNearbyPlaces = new ArrayList<>();
    }

    public List<HashMap<String, String>> getAllNearbyPlaces() {
        return allNearbyPlaces;
    }

    public void setAllNearbyPlaces(List<HashMap<String, String>> allNearbyPlaces) {
        this.allNearbyPlaces = allNearbyPlaces;
    }
    @Override
    protected String doInBackground(@NonNull Object... objects) {
        url = (String) objects[0];   // First argument is the URL (String)
        String jsonBody = (String) objects[1];  // Second argument is the JSON body (String)

        DownloadUrl downloadUrl = new DownloadUrl();

        try {
            googlePlaceData = downloadUrl.readTheUrl(url, jsonBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        try {
            allNearbyPlaces = parseDetails(s);
            MapClinicFinder.displayNearbyPlaces(allNearbyPlaces);
            Log.d("Assigning:",allNearbyPlaces.toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    public List<HashMap<String, String>> parseDetails(String jsonData) throws JSONException {
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(jsonData);
        jsonArray = jsonObject.getJSONArray("places");

        return getAllNearbyPlaces(jsonArray);
    }
}
