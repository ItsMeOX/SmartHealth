package com.example.smarthealth.MedicalCentreFinder.MapPageNavigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.RoutesExploration;
import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.TravelModes;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.Colours;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.Fragment_Transit;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.Fragment_Walk;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.Fragment_drive;
import com.example.smarthealth.MedicalCentreFinder.UI_Elements.SearchListRecyclerViewAdapter;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

abstract class HandleAsyncInputProcess{

    Activity activity;
    long delayTime;
    HandleAsyncInputProcess(Activity activity, long delayTime)
    {
        this.activity = activity;
        this.delayTime = delayTime;
    }

    public abstract void outerFunction();

    public abstract void backgroundThreadFunction(String inputText, LatLng destination);

    public abstract void mainThreadFunction();

    public abstract void exitFunction();

    public void createAsynchronousRunnerProcess(String newText, LatLng destination) {
        Handler handler = new Handler();
        // Create a new Runnable for the search operation
        Runnable searchRunnable = () -> {
            if (!newText.isEmpty()) {
                outerFunction();
                // Perform the search on a background thread
                new Thread(() -> {
                    backgroundThreadFunction(newText, destination);
                    // Update the UI on the main thread
                    activity.runOnUiThread(this::mainThreadFunction);
                }).start();
            } else {
                // Clear the results if the query is empty
                activity.runOnUiThread(this::exitFunction);
            }
        };
        // Schedule the Runnable with a debounce delay (e.g., 500ms)
        handler.postDelayed(searchRunnable, delayTime);
    }
}



class TextInputAsyncProcess extends HandleAsyncInputProcess {
    SearchListRecyclerViewAdapter adapter;

    RecyclerView recyclerView;

    MapClinicFinder mapClinicFinder;

    String newText;

    List<HashMap<String, String>> asyncCollectedResults;

    TextInputAsyncProcess(MapClinicFinder mapClinicFinder, String newText)
    {
        super(mapClinicFinder.requireActivity(),100);
        this.mapClinicFinder = mapClinicFinder;
        this.adapter = mapClinicFinder.getAdapter();
        this.recyclerView = mapClinicFinder.getRecyclerView();
        this.newText = newText;
    }

    @Override
    public void outerFunction()
    {

        // Show the RecyclerView
        recyclerView.setVisibility(View.VISIBLE);


        //return mapClinicFinder.prepareDetails(mapClinicFinder.getSearchBarExploration(), inputText);

    }
    @Override
    public void backgroundThreadFunction(String inputText, LatLng destination){
        mapClinicFinder.getSearchBarExploration().setPlaceName(inputText);
        mapClinicFinder.getSearchBarExploration().setLongitude(mapClinicFinder.getLongitude());
        mapClinicFinder.getSearchBarExploration().setLatitude(mapClinicFinder.getLatitude());
        mapClinicFinder.getSearchBarExploration().setSearchKey("places");

        asyncCollectedResults = (List<HashMap<String, String>>) mapClinicFinder.getSearchBarExploration().search();

        // Prepare details for the search
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void mainThreadFunction(){
        mapClinicFinder.getSearchResults().clear();
        mapClinicFinder.getSearchResults().addAll(asyncCollectedResults);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void exitFunction(){
        mapClinicFinder.getSearchResults().clear();
        adapter.notifyDataSetChanged();
    }
}


class IconButtonInputAsyncProcess extends HandleAsyncInputProcess{
    MapClinicFinder mapClinicFinder;

    List<HashMap<String, String>> asyncCollectedResults;

    int zoomFactor = 10;

    IconButtonInputAsyncProcess(MapClinicFinder mapClinicFinder, int zoomFactor)
    {
        super(mapClinicFinder.requireActivity(),100);
        this.mapClinicFinder = mapClinicFinder;
        this.zoomFactor = zoomFactor;

    }
    @Override
    public void outerFunction()
    {
        // Prepare details for the search

    }
    @Override
    public void backgroundThreadFunction(String newText, LatLng destination){
        mapClinicFinder.getProximityExploration().setPlaceName("hospital");
        mapClinicFinder.getProximityExploration().setLatitude(mapClinicFinder.getLatitude());
        mapClinicFinder.getProximityExploration().setLongitude(mapClinicFinder.getLongitude());
        mapClinicFinder.getProximityExploration().setSearchKey("places");
        asyncCollectedResults = (List<HashMap<String, String>>) mapClinicFinder.getProximityExploration().search();
    }

    @Override
    public void mainThreadFunction(){
        displayNearbyPlaces(asyncCollectedResults);
    }

    @Override
    public void exitFunction(){

    }
    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList)
    {
        Log.d("MyList", nearbyPlacesList.toString());
        for(int i = 0; i < nearbyPlacesList.size(); i++)
        {
            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            double lat = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lat")));
            double lng = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lng")));
            LatLng latLng = new LatLng(lat,lng);

            //create the lat-long marker UI
            mapClinicFinder.createMapMarker(mapClinicFinder.getMyMap(), nameOfPlace, vicinity, latLng, Colours.HUE_GREEN, zoomFactor);
        }
    }
}

class RouteFinderAsyncProcess extends HandleAsyncInputProcess{

    private MapClinicFinder mapClinicFinder;

    private List<HashMap<String, String>> routesData;

    private List<HashMap<String, String >> routesLegInstructions;

    private TravelModes travelModes;

    Fragment_drive fragmentDrive;
    Fragment_Transit fragmentTransit;
    Fragment_Walk fragmentWalk;

    int routeColor;

    RouteFinderAsyncProcess(MapClinicFinder mapClinicFinder, int routeColor)
    {
        super(mapClinicFinder.requireActivity(), 100);
        this.mapClinicFinder = mapClinicFinder;
        this.routeColor = routeColor;
    }

    public void setFragmentDrive(Fragment fragmentDrive)
    {
        this.fragmentDrive = (Fragment_drive) fragmentDrive;
    }

    public void setTravelModes(TravelModes travelModes)
    {
        this.travelModes = travelModes;
    }

    public void setFragmentWalk(Fragment fragmentWalk)
    {
        this.fragmentWalk = (Fragment_Walk) fragmentWalk;
    }

    public void setFragmentTransit(Fragment fragmentTransit)
    {
        this.fragmentTransit = (Fragment_Transit) fragmentTransit;
    }

    @Override
    public void outerFunction() {
        Log.d("Outer", "Getting routes");
    }
    @Override
    public void backgroundThreadFunction(String inputText, LatLng destination) {
        RoutesExploration routesExploration = new RoutesExploration("routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline,routes.legs.steps.navigationInstruction", mapClinicFinder);
        routesExploration.setTravelMode(travelModes);
        routesExploration.setSearchKey(inputText);
        LatLng originLoc = new LatLng(mapClinicFinder.getLatitude(), mapClinicFinder.getLongitude());
        routesExploration.setOriginAndDestination(originLoc, destination);
        routesExploration.search();
        if (routesExploration == null)
        {
            Toast.makeText(mapClinicFinder.requireContext(), "No available Routes", Toast.LENGTH_SHORT);
            return;
        }
        routesData = routesExploration.getRoutesData();
        routesLegInstructions = routesExploration.getAllpossibleRouteInstructions(); //got all possible instructions
        Log.d("Routes!!!!", routesLegInstructions.toString());
    }

    @Override
    public void mainThreadFunction() {
        if (routesData == null)
        {
            return;
        }
        //Now, convert the encoded polyline route data to the set of latitudes and longitude stop-points.

        Log.d("Routes",routesData.toString());
        HashMap<String, String> firstRouteResult = routesData.get(0);
        List<LatLng> latLngStopPoints = PolyUtil.decode(firstRouteResult.get("Encoded Polyline"));
        Log.d("Lat Long Stop Points", latLngStopPoints.toString());

        //remove all existing polylines first, if any.
        if(!mapClinicFinder.getPolylines().isEmpty())
        {
            for (Polyline polyline : mapClinicFinder.getPolylines())
            {
                polyline.remove();
            }
        }


        //Polyline customization!!!
        Polyline polyline = mapClinicFinder.getMyMap().addPolyline(new PolylineOptions().color(routeColor).width(20).clickable(false).addAll(latLngStopPoints));
        polyline.setGeodesic(true);

        int PATTERN_GAP_LENGTH_PX = 10;
        final PatternItem DOT = new Dot();
        PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

        // Create a stroke pattern of a gap followed by a dot.
        List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
        polyline.setPattern(PATTERN_POLYLINE_DOTTED);

        mapClinicFinder.getPolylines().add(polyline);

        if (travelModes == TravelModes.WALK)
        {
            fragmentWalk.updateResults(routesLegInstructions);
        }

        else if (travelModes == TravelModes.DRIVE)
        {
            fragmentDrive.updateResults(routesLegInstructions);
        }

        else if (travelModes == TravelModes.TRANSIT)
        {
            fragmentTransit.updateResults(routesLegInstructions);
        }
    }

    @Override
    public void exitFunction() {

    }
}
