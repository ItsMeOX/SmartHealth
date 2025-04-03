package com.example.smarthealth.MedicalCentreFinder.UI_Elements;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_Walk extends Fragment {

    private RecyclerView recyclerView;
    private DirectionsRecyclerViewAdapter adapter;

    private List<HashMap<String, String>> searchResults;

    Fragment_Walk()
    {
        searchResults = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_walk, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_walk_directions);

        // Setup your drive-specific RecyclerView
        searchResults = new ArrayList<>();

        HashMap<String, String> test = new HashMap<>();
        test.put("ededed","ededed");
        searchResults.add(test);
        searchResults.add(test);
        searchResults.add(test);
        searchResults.add(test);
        searchResults.add(test);
        searchResults.add(test);

        Log.d("sseed",searchResults.toString());

        adapter = new DirectionsRecyclerViewAdapter(requireContext(), searchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void updateResults(List<HashMap<String, String>> instructions)
    {
        searchResults.clear();
        searchResults.addAll(instructions);
        adapter.notifyDataSetChanged();
    }
}



