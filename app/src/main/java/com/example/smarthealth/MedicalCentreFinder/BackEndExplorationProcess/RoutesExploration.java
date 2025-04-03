package com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess;

import static android.provider.Settings.System.getString;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;
import com.example.smarthealth.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RoutesExploration extends MapExploration{
    double originLatitude;
    double originLongitude;

    double destinationLatitude;
    double destinationLongitude;

    private String travelMode;

    private List<HashMap<String,String>> allpossibleRouteInstructions;

    private List<HashMap<String, String>> routesData;

    public List<HashMap<String, String>> getRoutesData()
    {
        return routesData;
    }

    public List<HashMap<String, String>> getAllpossibleRouteInstructions()
    {
        return allpossibleRouteInstructions;
    }

    public RoutesExploration(String fieldMask, MapClinicFinder mapClinicFinder)
    {
        super(fieldMask, mapClinicFinder);
        allpossibleRouteInstructions = new ArrayList<>();
        routesData = new ArrayList<>();
    }
    public void setOriginAndDestination(LatLng origin, LatLng destination)
    {
        originLatitude = origin.latitude;
        originLongitude = origin.longitude;

        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;
    }

    public void clearDestinationData()
    {
        this.destinationLongitude = 0;
        this.destinationLatitude = 0;
    }

    public void setTravelMode(TravelModes travelMode)
    {
        switch (travelMode){
            case DRIVE:
                this.travelMode = "DRIVE";
                break;
            case WALK:
                this.travelMode = "WALK";
                break;
            case TRANSIT:
                this.travelMode = "TRANSIT";
                break;
        }
    }

    @Override
    public String getRequestBody() {
        String body = "";
        switch (travelMode)
        {
            case "DRIVE":
                body = "{" +
                    "  \"origin\": {" +
                    "    \"location\": {" +
                    "      \"latLng\": {" +
                    "        \"latitude\": \""+ originLatitude + "\" ," +
                    "        \"longitude\": \"" + originLongitude + "\","+
                    "      }" +
                    "    }" +
                    "  }," +
                    "  \"destination\": {" +
                    "    \"location\": {" +
                    "      \"latLng\": {" +
                    "        \"latitude\": \"" + destinationLatitude +"\","+
                    "        \"longitude\": \"" + destinationLongitude +"\","+
                    "      }" +
                    "    }" +
                    "  }," +
                    "  \"travelMode\": \"" + travelMode + "\"," +
                    "  \"routingPreference\": \"TRAFFIC_AWARE\"," +
                    "  \"computeAlternativeRoutes\": false," +
                    "  \"routeModifiers\": {" +
                    "    \"avoidTolls\": false," +
                    "    \"avoidHighways\": false," +
                    "    \"avoidFerries\": false" +
                    "  }," +
                    "  \"languageCode\": \"en-US\"," +
                    "  \"units\": \"IMPERIAL\"" +
                    "}";
            break;
            case "WALK":
                body = "{" +
                        "  \"origin\": {" +
                        "    \"location\": {" +
                        "      \"latLng\": {" +
                        "        \"latitude\": \"" + originLatitude + "\"," +
                        "        \"longitude\": \"" + originLongitude + "\"" +
                        "      }" +
                        "    }" +
                        "  }," +
                        "  \"destination\": {" +
                        "    \"location\": {" +
                        "      \"latLng\": {" +
                        "        \"latitude\": \"" + destinationLatitude + "\"," +
                        "        \"longitude\": \"" + destinationLongitude + "\"" +
                        "      }" +
                        "    }" +
                        "  }," +
                        "  \"travelMode\": \"WALK\"," +
                        "  \"languageCode\": \"en-US\"," +
                        "  \"units\": \"IMPERIAL\"" +
                        "}";
            break;
            case "TRANSIT":
                Log.d("Executing here","Transit!");
                body = "{" +
                        "  \"origin\": {" +
                        "    \"location\": {" +
                        "      \"latLng\": {" +
                        "        \"latitude\": \"" + originLatitude + "\"," +
                        "        \"longitude\": \"" + originLongitude + "\"" +
                        "      }" +
                        "    }" +
                        "  }," +
                        "  \"destination\": {" +
                        "    \"location\": {" +
                        "      \"latLng\": {" +
                        "        \"latitude\": \"" + destinationLatitude + "\"," +
                        "        \"longitude\": \"" + destinationLongitude + "\"" +
                        "      }" +
                        "    }" +
                        "  }," +
                        "  \"travelMode\": \"TRANSIT\"," +
                        "  \"computeAlternativeRoutes\": false," +
                        "  \"languageCode\": \"en-US\"," +
                        "  \"units\": \"IMPERIAL\"" +
                        "}";

        }
        return body;
    }

    @Override
    public String getURL() {
        String apiKey = mapClinicFinder.getString(R.string.my_gmap_api_key);
        return "https://routes.googleapis.com/directions/v2:computeRoutes?key=" + apiKey;
    }

    @Override
    public List<? extends Object> getExplorationResults() throws JSONException {
        if (jsonArray == null)
        {
            Toast.makeText(mapClinicFinder, "No available routes", Toast.LENGTH_SHORT).show();
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++)
        {
            HashMap<String, String> singleRouteData = new HashMap<>();
            JSONObject routeData = jsonArray.getJSONObject(i);
            String distanceMeters = routeData.getString("distanceMeters");
            String duration = routeData.getString("duration");
            String encodedPolyline = routeData.getJSONObject("polyline").getString("encodedPolyline");

            singleRouteData.put("Encoded Polyline", encodedPolyline);
            singleRouteData.put("Duration", duration);
            singleRouteData.put("Distance  Meters", distanceMeters);

            routesData.add(singleRouteData);

            //get the instructions
            List<HashMap<String ,String>> allinstructionsSingleRoute = new ArrayList<>();
            JSONArray legs = routeData.getJSONArray("legs");
            //This "legs" JSON array actually contains many many steps. Because no intermediate waypoints have been set,
            //1 leg is essentially representative of the entire journey from start to end, and it has many steps.
            JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
            if (steps != null) {
                for (int j = 0; j < steps.length(); j++) {
                    Log.d("Result", steps.getJSONObject(j).toString());
                    HashMap<String, String> instructions = new HashMap<>();

                    try{
                        JSONObject instruction = steps.getJSONObject(j).optJSONObject("navigationInstruction");
                        String direction = instruction.optString("instructions");
                        instructions.put("Directions: ", direction);
                        allinstructionsSingleRoute.add(instructions);
                        allpossibleRouteInstructions = allinstructionsSingleRoute;

                    }catch (NullPointerException e)
                    {
                        Toast.makeText(mapClinicFinder, "Fetching results", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return routesData;
    }


}
