package com.example.smarthealth.MedicalCentreFinder.MapPageNavigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.TravelModes;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.Colours;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.ParseBitmapDescriptorIconColours;
import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.ProximityExploration;
import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.SearchBarExploration;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.SearchListRecyclerViewAdapter;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.TabPagerAdapter;
import com.example.smarthealth.R;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MapClinicFinder extends Fragment implements OnMapReadyCallback, SearchListRecyclerViewAdapter.ItemClickListener{

    //Definitions
    private View view;
    private GoogleMap myMap;
    Location currentLocation;
    private RecyclerView recyclerView;
    private SearchListRecyclerViewAdapter adapter;

    private ViewPager2 viewPager2;

    private TabPagerAdapter tabPagerAdapter;

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private SearchBarExploration searchBarExploration;
    private ProximityExploration proximityExploration;
    private List<HashMap<String, String>> searchResults;

    private List<Polyline> polylines;

    LatLng destination;
    //Getters
    public GoogleMap getMyMap() {
        return myMap;
    }
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    public SearchListRecyclerViewAdapter getAdapter()
    {
        return adapter;
    }
    public List<HashMap<String, String>> getSearchResults()
    {
        return searchResults;
    }
    public SearchBarExploration getSearchBarExploration()
    {
        return searchBarExploration;
    }
    public ProximityExploration getProximityExploration()
    {
        return proximityExploration;
    }
    public Double getLatitude()
    {
        return currentLocation.getLatitude();
    }
    public Double getLongitude()
    {
        return currentLocation.getLongitude();
    }

    public LatLng getLatLng()
    {
        return new LatLng(getLatitude(), getLongitude());
    }


    public List<Polyline> getPolylines()
    {
        return polylines;
    }

    public ViewPager2 getViewPager2()
    {
        return viewPager2;
    }


    //Setter
    public void setLocation(Location location)
    {
        currentLocation = location;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.clinic_map, container, false);

        //Initialising Classes that reference this main class
        InitializeUserLocation initializeUserLocation = new InitializeUserLocation(this);
        searchBarExploration = new SearchBarExploration("places.displayName,places.formattedAddress,places.location", 5000, this);
        proximityExploration = new ProximityExploration("places.displayName,places.formattedAddress,places.location", 5000,this);

        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.

        SupportMapFragment mapFragment = (SupportMapFragment) this.requireActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }

        initializeUserLocation.getLastLocation();

        //Initialise the polylines array to show routes on the map
        polylines = new ArrayList<>();


        // data to populate the RecyclerView with
        searchResults = new ArrayList<>();
        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.searchResultsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SearchListRecyclerViewAdapter(requireContext(), searchResults);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        //SearchView Initialisation and Input Query
        //Set up the search view
        SearchView searchView = view.findViewById(R.id.mapSearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                TextInputAsyncProcess textInputAsyncProcess = new TextInputAsyncProcess(MapClinicFinder.this, newText);
                textInputAsyncProcess.createAsynchronousRunnerProcess(newText, null);
                return false;
            }
        });

        View findNearbyMedCtrsButton = view.findViewById(R.id.find_nearby_med_ctrs_btn);

        //Button to find nearby medical centres
        findNearbyMedCtrsButton.setOnClickListener(view -> {
            IconButtonInputAsyncProcess iconButtonInputAsyncProcess = new IconButtonInputAsyncProcess(MapClinicFinder.this, 20);
            iconButtonInputAsyncProcess.createAsynchronousRunnerProcess("hospital", null);

            Toast.makeText(requireContext(), "Finding Nearby Hospitals", Toast.LENGTH_SHORT).show();
            Toast.makeText(requireContext(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
        });


        LinearLayout bottomSheet = view.findViewById(R.id.bottomsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Set initial state
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        // Close Bottom Sheet
        Button closeBottomSheetButton = view.findViewById(R.id.closeBottomsheetButton);
        closeBottomSheetButton.setOnClickListener(v -> {
            Log.d("Closing", "Closing!");
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });


        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        tabPagerAdapter = new TabPagerAdapter(this);
        // Set up adapter with tab fragments
        viewPager2.setAdapter(tabPagerAdapter);
        viewPager2.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position)
            {
                case 0:
                    tab.setText("Car");
                    break;
                case 1:
                    tab.setText("Walk");
                    break;
                case 2:
                    tab.setText("Transit");
                    break;
            }
        }).attach();
        return view;
    }




    //For clicking the recycler view layout
    @Override
    public void onItemClick(View view, int position) {
        HashMap<String, String> placeClicked = adapter.getmData().get(position);
        String placeName = placeClicked.get("Place Name");
        String latitude = placeClicked.get("Latitude");
        String longitude = placeClicked.get("Longitude");
        String address = placeClicked.get("Address");
        assert latitude != null;
        assert longitude != null;
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        createMapMarker(myMap, placeName, address, latLng, Colours.HUE_AZURE, 30);
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    @SuppressLint("PotentialBehaviorOverride")
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Called automatically when the Map Fragment UI is loaded,
        //by Google Maps API itself. So, must set a location before that.
        //Hence, we call getLastLocation() before mapFragment.getMap(async);
        myMap = googleMap;
        if (currentLocation == null) {
            Log.i("Error", "current location is null");
            return;
        }

        LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(userLocation).title("You"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


        //On selecting a specific map marker, the following is done:
        myMap.setOnMarkerClickListener(marker -> {

            viewPager2.setCurrentItem(0);

            String id = marker.getId();
            String title = marker.getTitle();
            LatLng destination = marker.getPosition();
            if (!destination.equals(getLatLng()))
            {
                Log.d("Marker!", "id: "+id+"title: "+title+"latlng: "+destination);
                RouteFinderAsyncProcess routeFinderAsyncProcess = new RouteFinderAsyncProcess(MapClinicFinder.this, getResources().getColor(R.color.polyLine_blue));
                routeFinderAsyncProcess.setTravelModes(TravelModes.DRIVE);

                routeFinderAsyncProcess.setFragmentDrive(tabPagerAdapter.getPositionalFragment(0));
                routeFinderAsyncProcess.createAsynchronousRunnerProcess("routes", destination);
            }

            //show the bottom sheet
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                RouteFinderAsyncProcess routeFinderAsyncProcess = new RouteFinderAsyncProcess(MapClinicFinder.this,getResources().getColor(R.color.polyLine_blue));
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    switch(position)
                    {
                        case 0:
                                routeFinderAsyncProcess.setTravelModes(TravelModes.DRIVE);
                                routeFinderAsyncProcess.setFragmentDrive(tabPagerAdapter.getPositionalFragment(0));
                        break;
                        case 1:
                                routeFinderAsyncProcess.setTravelModes(TravelModes.WALK);
                                routeFinderAsyncProcess.setFragmentWalk(tabPagerAdapter.getPositionalFragment(1));
                        break;
                        case 2:
                                routeFinderAsyncProcess.setTravelModes(TravelModes.TRANSIT);
                                routeFinderAsyncProcess.setFragmentTransit(tabPagerAdapter.getPositionalFragment(2));

                        break;
                    }
                    routeFinderAsyncProcess.createAsynchronousRunnerProcess("routes", destination);
                    //So, onMapReady, we also check if the bottomsheet is up and hence is for any changes tabs detected by the viewpager.
                }
            });

            return false;
        });
    }


    protected void createMapMarker(GoogleMap mapData, String placeName, String address, LatLng latLngCoordinates, Colours colours, int zoomFactor)
    {
        float colourValue = ParseBitmapDescriptorIconColours.bitMapMarkerColour(colours);
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
class InitializeUserLocation {

    Activity context;
    OnMapReadyCallback mapReadyCallback;
    FusedLocationProviderClient fusedLocationProviderClient;

    MapClinicFinder mapClinicFinder;

    InitializeUserLocation(MapClinicFinder mapClinicFinder)
    {
        this.mapReadyCallback = mapClinicFinder;
        this.context = mapClinicFinder.getActivity();
        assert this.context != null;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context);
        this.mapClinicFinder = mapClinicFinder;
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
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
                    else {
                        Log.d("Location","Location is null");
                    }
                }
            }
        }, Looper.getMainLooper());
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int FINE_PERMISSION_CODE = 1;

            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            Log.d("Permissions","Here2"); // Exit method if permission is not granted
        }

        if (!isLocationEnabled(context)) {
            Log.i("Error", "Please Enable Location services");
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                mapClinicFinder.setLocation(location);
                SupportMapFragment mapFragment = (SupportMapFragment) mapClinicFinder.getChildFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(mapReadyCallback);
                }
                else{
                    Log.d("Null", ",mapFragment is null");
                }
            } else {
                Log.d("Resquesting new data","Requesting new data");
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






