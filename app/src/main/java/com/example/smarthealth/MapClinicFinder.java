package com.example.smarthealth;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Location;
import android.location.LocationManager;
import android.location.Criteria;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;


// Implement OnMapReadyCallback.
public class MapClinicFinder extends AppCompatActivity implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private static GoogleMap myMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private SearchView searchView;

    private double latitude, longitude;
    private int proximityRad = 5000;

    private PlacesClient placesClient;

    private void parseTextInput() {
        String location = searchView.getQuery().toString();
        List<Address> addressList = null;

        if (location != null) {
            Geocoder geocoder = new Geocoder(MapClinicFinder.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng resultLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            myMap.addMarker(new MarkerOptions().position(resultLatLng).title(location));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(resultLatLng, 10));
        }
    }

    public GoogleMap getMapReference()
    {
        return myMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.clinic_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            return;
        }
        getLastLocation();
        mapFragment.getMapAsync(this);

        Places.initializeWithNewPlacesApiEnabled(this, "AIzaSyDdq_d2iskKqtOZUwPvKlB-kgH7cstKiI0");
    }

//    private void getSearachPlacesRequestBody(String placetype, double latitude, double longitude, int proximityRad)
//    {
//        return "{"
//                + "\"input\": \"" + placeType + "\","
//                + "\"locationBias\": {"
//                + "  \"circle\": {"
//                + "    \"center\": {"
//                + "      \"latitude\": " + latitude + ","
//                + "      \"longitude\": " + longitude + ""
//                + "    },"
//                + "    \"radius\": " + proximityRad + ".0"
//                + "  }"
//                + "}"
//                + "}";
//    }

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

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return; // Exit method if permission is not granted
        }

        if (!isLocationEnabled(this)) {
            Log.i("Error", "Please Enable Location services");
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(MapClinicFinder.this);
                }
            } else {
                requestNewLocationData();
            }
        }).addOnFailureListener(e -> {
            Log.e("LocationError", "Failed to get last known location: " + e.getMessage());
            requestNewLocationData();
        });
    }

    private void requestNewLocationData() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLocation = location;
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(MapClinicFinder.this);
                        }
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        break;
                    }
                }
            }
        }, Looper.getMainLooper());
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

//        String url = getSearchPlacesUrl();
//        String searchReqJsonBody = getSearachPlacesRequestBody("Restaurants",c);
//        Object[] transferData = prepareWebQueryDetails(url, searchReqJsonBody);
//        FindNearbyHospitals findNearbyHospitals = new FindNearbyHospitals();
//        findNearbyHospitals.execute(transferData);
    }

    @Override //Called upon starting the app and going to this page in the application.
    //We will automatically call this method. Hence the @Override as it is a standard
    //class function of CompactActivity.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location Permission is Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.hospital_icon) {
            String url = getUrl();
            String jsonBody = getRequestBody(currentLocation.getLatitude(), currentLocation.getLongitude(), "hospital");

            FindNearbyHospitals findNearbyHospitals = new FindNearbyHospitals();
            findNearbyHospitals.execute(prepareWebQueryDetails(url, jsonBody));

            Toast.makeText(this, "Finding Nearby Hospitals", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
        }
    }

    private Object[] prepareWebQueryDetails(Object url, Object jsonBody)
    {
        Object transferData[] = new Object[2];

        transferData[0] = url;   // URL goes first
        transferData[1] = jsonBody;  // JSON body goes second
        return transferData;
    }


    public static void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList)
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
            createMapMarker(myMap, nameOfPlace, vicinity, latLng);
        }
    }

    private static void createMapMarker(GoogleMap mapData, String placeName, String address, LatLng latLngCoordinates )
    {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngCoordinates);
        markerOptions.title(placeName + " : " + address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mapData.addMarker(markerOptions);
        mapData.moveCamera(CameraUpdateFactory.newLatLng(latLngCoordinates));
        mapData.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    private String getUrl() {
        return "https://places.googleapis.com/v1/places:searchNearby?key=AIzaSyDdq_d2iskKqtOZUwPvKlB-kgH7cstKiI0";
    }

    private String getSearchPlacesUrl()
    {
        return "https://places.googleapis.com/v1/places:autocomplete?key=AIzaSyDdq_d2iskKqtOZUwPvKlB-kgH7cstKiI0";
    }


    private String getRequestBody(double latitude, double longitude, String placeType) {
        return "{"
                + "\"includedTypes\": [\"" + placeType + "\"],"
                + "\"maxResultCount\": 10,"
                + "\"locationRestriction\": {"
                + "  \"circle\": {"
                + "    \"center\": {"
                + "      \"latitude\": " + latitude + ","
                + "      \"longitude\": " + longitude + ""
                + "    },"
                + "    \"radius\": " + proximityRad + ".0"
                + "  }"
                + "}"
                + "}";
    }
}


