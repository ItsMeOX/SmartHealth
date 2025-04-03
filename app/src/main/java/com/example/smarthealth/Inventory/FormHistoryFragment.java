package com.example.smarthealth.Inventory;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthealth.R;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
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
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Medicine List Setup
        medicineList = new ArrayList<>();
        medicineList.add(new MedicineButton(
                "Paracetamol",
                "Pills",
                90,
                ContextCompat.getDrawable(requireContext(), R.drawable.app_logo),
                "Info",
                "Tel",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "PainKiller",
                "Others",
                200,
                ContextCompat.getDrawable(requireContext(), R.drawable.camera),
                "Info",
                "Lpo",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));

        medicineList.add(new MedicineButton(
                "Fever",
                "Liquids",
                100,
                ContextCompat.getDrawable(requireContext(), R.drawable.clock),
                "Info",
                "Lol",
                "Side Effect",
                new ArrayList<>()
        ));


        searchBar = popupView.findViewById(R.id.searchBar);
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
            TextView dosage = popupView.findViewById(R.id.displayDosage);
            TextView contain = popupView.findViewById(R.id.displayContain);
            TextView sideEffect = popupView.findViewById(R.id.displaySideEffect);
            LinearLayout tagLayout = popupView.findViewById(R.id.displayTag);
            String category = selectedMedicine.getMedicineCategory();
            ArrayList<String> tags = selectedMedicine.getMedicineType();

            image.setImageDrawable(selectedMedicine.getMedicineImage());
            name.setText(selectedMedicine.getMedicineName());
            dosage.setText(selectedMedicine.getMedicineDosage());
            contain.setText(selectedMedicine.getMedicineContains());
            sideEffect.setText(selectedMedicine.getMedicineSideEffect());
            for (String tag : tags) {
                // Create a new TextView dynamically
                TextView tagView = new TextView(getContext());
                tagView.setText(tag);
                tagView.setTextSize(14);
                tagView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                tagView.setPadding(16, 8, 16, 8);
                tagView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded)); // Custom background

                // Set layout parameters
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(8, 8, 8, 8);
                tagView.setLayoutParams(params);

                // Add the TextView to the container
                tagLayout.addView(tagView);
            }
            ;
//            searchBar.setOnFocusChangeListener((view, hasFocus) -> {
//                if (hasFocus) {
//                    // Check if the view is an instance of EditText (since AutoCompleteTextView uses EditText internally)
//                    if (view instanceof EditText) {
//                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0); // Hide keyboard explicitly
//                    }
//                }
//            });

            MaterialButton confirm = popupView.findViewById(R.id.addByHistory);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediName = searchBar.getText().toString();
                    ImageView imageView = popupView.findViewById(R.id.displayImage);
                    EditText amount = popupView.findViewById(R.id.amountInput);
                    TextView dosage = popupView.findViewById(R.id.displayDosage);
                    TextView contain = popupView.findViewById(R.id.displayContain);
                    TextView sideEffect = popupView.findViewById(R.id.displaySideEffect);
                    LinearLayout tagChosen = popupView.findViewById(R.id.displayTag);
                    String mediDosage = dosage.getText().toString();
                    String mediContains = contain.getText().toString();
                    String mediSideEffect = sideEffect.getText().toString();
                    Drawable image = imageView.getDrawable();

                    ArrayList<String> tagList = new ArrayList<>();
                    for (int i = 0; i < tagChosen.getChildCount(); i++) {
                        View child = tagChosen.getChildAt(i);

                        // Check if the child is a TextView
                        if (child instanceof TextView) {
                            TextView tagView = (TextView) child;
                            String tagText = tagView.getText().toString();
                            tagList.add(tagText);
                        }
                    }

                    // Check for empty fields (adjust this logic as needed)
                    if (mediName.isEmpty() || mediDosage.isEmpty() || mediContains.isEmpty() || mediSideEffect.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        byte[] imageData = drawableToByteArray(image);
                        int mediAmount = Integer.parseInt(amount.getText().toString().trim());

                        // Create the Bundle to pass data
                        Bundle result = new Bundle();
                        result.putString("Name", mediName);
                        result.putString("Category", category);
                        result.putInt("Amount", mediAmount);
                        result.putByteArray("Image", imageData);
                        result.putString("Dosage", mediDosage);
                        result.putString("Contains", mediContains);
                        result.putString("Side Effect", mediSideEffect);
                        result.putStringArrayList("Tags", tagList);

                        getParentFragmentManager().setFragmentResult("History Data", result);
                        dismiss();
                    }


                }
            });
        });
        return popupView;
    }

    private byte[] drawableToByteArray(Drawable drawable) {
        if (drawable == null) return null;

        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
