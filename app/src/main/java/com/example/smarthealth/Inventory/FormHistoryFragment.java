package com.example.smarthealth.Inventory;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.example.smarthealth.R;
import java.util.ArrayList;
import java.util.List;

public class FormHistoryFragment extends DialogFragment {
    private AutoCompleteTextView searchBar;
    private ArrayList<MedicineButton> medicineList;
    private InputMethodManager inputMethodManager;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = getDialog().getWindow();
            window.setLayout(width, height);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View popupView = inflater.inflate(R.layout.form_history, null);

        // Exit Dialog Fragment
        ImageView exit = popupView.findViewById(R.id.backarrow);
        exit.setOnClickListener(v -> dismiss());

        // Medicine List Setup
        medicineList = new ArrayList<>();
        medicineList.add(new MedicineButton("Paracetamol", "Pills", "Tel", 90,
                ContextCompat.getDrawable(requireContext(), R.drawable.app_logo), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("PainKiller", "Others", "Lpo", 200,
                ContextCompat.getDrawable(requireContext(), R.drawable.camera), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));
        medicineList.add(new MedicineButton("Fever", "Liquids", "Lol", 100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock), "Info", new ArrayList<>()));

        searchBar = popupView.findViewById(R.id.searchBar);
//        searchBar.setFocusable(false);
//        searchBar.setFocusableInTouchM ode(true);
        inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        MedicineSearchBarAdapter adapter = new MedicineSearchBarAdapter(requireContext(), medicineList);
        searchBar.setAdapter(adapter);
        searchBar.setThreshold(1);
        searchBar.setOnClickListener(v -> {
            // Show the dropdown without opening the keyboard
            searchBar.showDropDown();

            // Explicitly hide the keyboard when clicking on the search bar
            inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });

        searchBar.setOnItemClickListener((parent, view, position, id) -> {
            MedicineButton selectedMedicine = (MedicineButton) parent.getItemAtPosition(position);
            searchBar.setText(selectedMedicine.getMedicineName()); // Set selected text
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
            // Fill UI with selected medicine details
            ImageView image = popupView.findViewById(R.id.displayImage);
            TextView name = popupView.findViewById(R.id.displayName);
            TextView desc = popupView.findViewById(R.id.displayDesc);

            image.setImageDrawable(selectedMedicine.getMedicineImage());
            name.setText(selectedMedicine.getMedicineName());
            desc.setText(selectedMedicine.getMedicineInfo());
        });
        searchBar.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // Check if the view is an instance of EditText (since AutoCompleteTextView uses EditText internally)
                if (view instanceof EditText) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0); // Hide keyboard explicitly
                }
            }
        });
//
        return popupView;
    }
}
