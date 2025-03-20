package com.example.smarthealth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.DoubleToLongFunction;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

// Implement OnMapReadyCallback.
public class MapClinicFinder extends AppCompatActivity implements OnMapReadyCallback, MyRecyclerViewAdapter.ItemClickListener {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;

    public GoogleMap getMyMap() {
        return myMap;
    }

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private SearchView searchView;
    private RecyclerView recyclerView;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private MyRecyclerViewAdapter adapter;

    public MyRecyclerViewAdapter getAdapter()
    {
        return adapter;
    }



    private double latitude, longitude;
    private int proximityRad = 5000;

    private PlacesClient placesClient;

    private ConductGMapWebserviceSearch conductGMapWebserviceSearch;

    public ConductGMapWebserviceSearch getConductGMapWebServiceSearch()
    {
        return conductGMapWebserviceSearch;
    }

    private MapOverviewSetup mapOverviewSetup;

    private List<HashMap<String, String>> searchResults;

    public List<HashMap<String, String>> getSearchResults()
    {
        return searchResults;
    }

    private SearchBarExploration searchBarExploration;

    public SearchBarExploration getSearchBarExploration()
    {
        return searchBarExploration;
    }
    private ProximityExploration proximityExploration;

    public ProximityExploration getProximityExploration()
    {
        return proximityExploration;
    }

    public GoogleMap getMapReference()
    {
        return myMap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initialising Classes that reference this main class
        mapOverviewSetup = new MapOverviewSetup(this);
        this.conductGMapWebserviceSearch = new ConductGMapWebserviceSearch();
        searchBarExploration = new SearchBarExploration(5000, "places.displayName,places.formattedAddress,places.location", true);
        proximityExploration = new ProximityExploration(5000, "places.displayName,places.formattedAddress,places.location", true);
        ConductGMapWebserviceSearch conductGMapWebserviceSearch = new ConductGMapWebserviceSearch();


        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.clinic_map);

        mapOverviewSetup.getLastLocation();

        Places.initializeWithNewPlacesApiEnabled(this, "AIzaSyDdq_d2iskKqtOZUwPvKlB-kgH7cstKiI0");


        // data to populate the RecyclerView with
        searchResults = new ArrayList<>();
        // set up the RecyclerView
        recyclerView = findViewById(R.id.searchResultsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, searchResults);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);

        //SearchView Initialisation and Input Query
        //Set up the search view
        searchView = findViewById(R.id.mapSearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Debug", searchResults.toString());
                TextInputAsyncProcess textInputAsyncProcess = new TextInputAsyncProcess(MapClinicFinder.this, newText);
                textInputAsyncProcess.createAsynchronousRunnerProcess(newText);
                return false;
            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        HashMap<String, String> placeClicked = (HashMap<String, String>) adapter.getmData().get(position);
        String placeName = placeClicked.get("Place Name");
        String latitude = placeClicked.get("Latitude");
        String longitude = placeClicked.get("Longitude");
        String address = placeClicked.get("Address");
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        createMapMarker(myMap, placeName, address, latLng, Colours.HUE_AZURE, 100);
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Called automatically when the Map Fragment UI is loaded,
        //by Google Maps API itself. So, must set a location before that.
        //Hence, we call getLastLocation() before mapFragment.getMap(async);
        myMap = googleMap;
        if (currentLocation == null) {
            Log.i("Error", "current location is null");
            return;
        }

        LatLng sydney = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(sydney).title("Location"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override //Called upon starting the app and going to this page in the application.
    //We will automatically call this method. Hence the @Override as it is a standard
    //class function of CompactActivity.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapOverviewSetup.getLastLocation();
            } else {
                Toast.makeText(this, "Location Permission is Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.hospital_icon) {
            IconButtonInputAsyncProcess iconButtonInputAsyncProcess = new IconButtonInputAsyncProcess(MapClinicFinder.this, 50);
            iconButtonInputAsyncProcess.createAsynchronousRunnerProcess("hospital");

            Toast.makeText(this, "Finding Nearby Hospitals", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
        }
    }

    protected Object[] prepareDetails(MapExploration mapExploration, String placeName)
    {
        String url = mapExploration.getURL();
        String jsonBody = mapExploration.getRequestBody(placeName, currentLocation.getLatitude(), currentLocation.getLongitude());
        Log.d("Request Body", jsonBody);
        String fieldMask = mapExploration.getFieldMask();
        Log.d("Field Mask here",fieldMask);
        Boolean hasfieldMask = mapExploration.getHasFieldMask();
        SearchType searchType = mapExploration.getSearchType();

        Object[] details = {url, jsonBody, fieldMask, hasfieldMask, searchType};
        return details;
    }

    public void setLocation(Location location)
    {
        currentLocation = location;
    }


    protected void createMapMarker(GoogleMap mapData, String placeName, String address, LatLng latLngCoordinates, Colours colours, int zoomFactor)
    {
        float colourValue = ParseBitmMapDescriptorIconColours.bitMapMarkerColour(colours);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngCoordinates);
        markerOptions.title(placeName + " : " + address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(colourValue));
        mapData.addMarker(markerOptions);
        mapData.moveCamera(CameraUpdateFactory.newLatLng(latLngCoordinates));
        mapData.animateCamera(CameraUpdateFactory.zoomTo(zoomFactor));
    }
}


//Class for setting up the main map layout on startup
class MapOverviewSetup{

    Activity context;
    OnMapReadyCallback mapReadyCallback;
    FusedLocationProviderClient fusedLocationProviderClient;

    MapClinicFinder mapClinicFinder;

    private int FINE_PERMISSION_CODE = 1;

    MapOverviewSetup(MapClinicFinder mapClinicFinder)
    {
        this.mapReadyCallback = mapClinicFinder;
        this.context = mapClinicFinder;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.mapClinicFinder = mapClinicFinder;
    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mapClinicFinder.setLocation(location);
                        SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.map);
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(mapReadyCallback);
                        }
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        break;
                    }
                }
            }
        }, Looper.getMainLooper());
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return; // Exit method if permission is not granted
        }

        if (!isLocationEnabled(context)) {
            Log.i("Error", "Please Enable Location services");
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                mapClinicFinder.setLocation(location);
                SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(mapReadyCallback);
                }
            } else {
                requestNewLocationData();
            }
        }).addOnFailureListener(e -> {
            Log.e("LocationError", "Failed to get last known location: " + e.getMessage());
            requestNewLocationData();
        });
    }

    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }
}

class TextInputAsyncProcess implements HandleAsyncInputProcess{
    MyRecyclerViewAdapter adapter;
    ConductGMapWebserviceSearch conductGMapWebserviceSearch;

    RecyclerView recyclerView;

    MapClinicFinder mapClinicFinder;

    String newText;

    List<HashMap<String, String>> asyncCollectedResults;

    TextInputAsyncProcess(MapClinicFinder mapClinicFinder, String newText)
    {
        this.mapClinicFinder = mapClinicFinder;
        this.conductGMapWebserviceSearch = mapClinicFinder.getConductGMapWebServiceSearch();
        this.adapter = mapClinicFinder.getAdapter();
        this.recyclerView = mapClinicFinder.getRecyclerView();
        this.newText = newText;
    }

    @Override
    public Object[] outerFunction(String inputText)
    {

        // Show the RecyclerView
        recyclerView.setVisibility(View.VISIBLE);

        // Prepare details for the search
        Object[] details = mapClinicFinder.prepareDetails(mapClinicFinder.getSearchBarExploration(), inputText);

        return details;
    }
    @Override
    public void backgroundThreadFunction(Object[] details){
        asyncCollectedResults = (List<HashMap<String, String>>) conductGMapWebserviceSearch.getSearchResults(
                (String) details[0], (String) details[1], (String) details[2], (boolean) details[3], (SearchType) details[4]);
        Log.d("HERE!",asyncCollectedResults.toString());
    }
    @Override
    public void mainThreadFunction(){
        mapClinicFinder.getSearchResults().clear();
        mapClinicFinder.getSearchResults().addAll(asyncCollectedResults);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void exitFunction(){
        mapClinicFinder.getSearchResults().clear();
        adapter.notifyDataSetChanged();
    }


    public void createAsynchronousRunnerProcess(String newText) {
        Handler handler = new Handler();
        // Create a new Runnable for the search operation
        Runnable searchRunnable = new Runnable() {
            @Override
            public void run() {
                if (newText.length() > 0) {
                    outerFunction(newText);
                    // Perform the search on a background thread
                    new Thread(() -> {
                        backgroundThreadFunction(outerFunction(newText));
                        // Update the UI on the main thread
                        mapClinicFinder.runOnUiThread(() -> {
                            mainThreadFunction();
                        });
                    }).start();
                } else {
                    // Clear the results if the query is empty
                    mapClinicFinder.runOnUiThread(() -> {
                        exitFunction();
                    });
                }
            }
        };
        // Schedule the Runnable with a debounce delay (e.g., 500ms)
        handler.postDelayed(searchRunnable, 100);
    }
}

class IconButtonInputAsyncProcess implements HandleAsyncInputProcess{
    MapClinicFinder mapClinicFinder;
    ConductGMapWebserviceSearch conductGMapWebserviceSearch;

    List<HashMap<String, String>> asyncCollectedResults;

    int zoomFactor = 10;

    IconButtonInputAsyncProcess(MapClinicFinder mapClinicFinder, int zoomFactor)
    {
        this.mapClinicFinder = mapClinicFinder;
        this.conductGMapWebserviceSearch = mapClinicFinder.getConductGMapWebServiceSearch();
        this.zoomFactor = zoomFactor;
    }
    @Override
    public Object[] outerFunction(String inputText)
    {
        // Prepare details for the search
        Object[] details = mapClinicFinder.prepareDetails(mapClinicFinder.getProximityExploration(), inputText);
        return details;
    }
    @Override
    public void backgroundThreadFunction(Object[] details){
        asyncCollectedResults = (List<HashMap<String,String>>) conductGMapWebserviceSearch.getSearchResults(
                (String) details[0], (String) details[1], (String) details[2], (boolean) details[3], (SearchType) details[4]);
    }
    @Override
    public void mainThreadFunction(){
        displayNearbyPlaces(asyncCollectedResults);
    }

    @Override
    public void exitFunction(){

    }

    public void createAsynchronousRunnerProcess(String newText) {
        Handler handler = new Handler();
        // Create a new Runnable for the search operation
        Runnable searchRunnable = new Runnable() {
            @Override
            public void run() {
                if (newText.length() > 0) {
                    outerFunction(newText);
                    // Perform the search on a background thread
                    new Thread(() -> {
                        backgroundThreadFunction(outerFunction(newText));
                        // Update the UI on the main thread
                        mapClinicFinder.runOnUiThread(() -> {
                            mainThreadFunction();
                        });
                    }).start();
                } else {
                    // Clear the results if the query is empty
                    mapClinicFinder.runOnUiThread(() -> {
                        exitFunction();
                    });
                }
            }
        };
        // Schedule the Runnable with a debounce delay (e.g., 500ms)
        handler.postDelayed(searchRunnable, 100);
    }

    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList)
    {
        Log.d("MyList", nearbyPlacesList.toString());
        for(int i = 0; i < nearbyPlacesList.size(); i++)
        {
            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));
            LatLng latLng = new LatLng(lat,lng);

            //create the lat-long marker UI
            mapClinicFinder.createMapMarker(mapClinicFinder.getMyMap(), nameOfPlace, vicinity, latLng, Colours.HUE_YELLOW, zoomFactor);
        }
    }
}

enum Colours{

    HUE_AZURE,

    HUE_BLUE,
    HUE_CYAN,
    HUE_GREEN,
    HUE_MAGENTA,
    HUE_ORANGE,
    HUE_RED,
    HUE_ROSE,
    HUE_VIOLET,
    HUE_YELLOW
}
class ParseBitmMapDescriptorIconColours{
    public static float bitMapMarkerColour(Colours colours)
    {
        float value = 0.0f;
        switch (colours){
            case HUE_RED:
                value = BitmapDescriptorFactory.HUE_RED;
            break;

            case HUE_BLUE:
                value =BitmapDescriptorFactory.HUE_BLUE;
            break;

            case HUE_AZURE:
                value = BitmapDescriptorFactory.HUE_AZURE;
            break;

            case HUE_CYAN:
                value = BitmapDescriptorFactory.HUE_CYAN;
            break;

            case HUE_GREEN:
                value = BitmapDescriptorFactory.HUE_GREEN;
            break;

            case HUE_MAGENTA:
                value = BitmapDescriptorFactory.HUE_MAGENTA;
            break;
            case HUE_ORANGE:
                value = BitmapDescriptorFactory.HUE_ORANGE;
            break;

            case HUE_ROSE:
                value = BitmapDescriptorFactory.HUE_ROSE;
            break;

            case HUE_VIOLET:
                value = BitmapDescriptorFactory.HUE_VIOLET;
            break;

            case HUE_YELLOW:
                value = BitmapDescriptorFactory.HUE_YELLOW;
            break;
        }
        return value;
    }
}



