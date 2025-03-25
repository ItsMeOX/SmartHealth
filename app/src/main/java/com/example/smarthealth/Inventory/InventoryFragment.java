package com.example.smarthealth.Inventory;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModel;
import android.util.TypedValue;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import android.provider.MediaStore;
import android.net.Uri;

import com.example.smarthealth.R;

public class InventoryFragment extends Fragment {
    private ArrayList<MedicineButton> pillsContainers;
    private ArrayList<MedicineButton> liquidsContainers;
    private ArrayList<MedicineButton> othersContainers;
    private MedicineAdapter pillsAdapter;
    private MedicineAdapter liquidsAdapter;
    private MedicineAdapter othersAdapter;
    private RecyclerView pillsLayout;
    private RecyclerView liquidsLayout;
    private RecyclerView othersLayout;

    ConstraintLayout popup_window;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private  ActivityResultLauncher<Intent> cameraLauncher;
    private View view;
    private ImageView popupImageView;
    private Button uploadImageButton, openCameraButton, confirmButton;
    private SVMInventory sharedViewModel;
    private Uri camUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.inventory_fragment, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SVMInventory.class);

        // Retrieve the information stored in sharedViewModel
        pillsContainers = sharedViewModel.getPillsButtonList();
        liquidsContainers = sharedViewModel.getLiquidsButtonList();
        othersContainers = sharedViewModel.getOthersButtonList();

        // Create adapters for each category
        pillsAdapter = new MedicineAdapter(requireContext(), pillsContainers);
        liquidsAdapter = new MedicineAdapter(requireContext(), liquidsContainers);
        othersAdapter = new MedicineAdapter(requireContext(), othersContainers);

        // Get respective layout
        pillsLayout = view.findViewById(R.id.pillsRV);
        liquidsLayout = view.findViewById(R.id.liquidsRV);
        othersLayout = view.findViewById(R.id.othersRV);

        // Create grid layout for each layout
        GridLayoutManager pillsManager = new GridLayoutManager(requireContext(), 2);
        GridLayoutManager liquidsManager = new GridLayoutManager(requireContext(), 2);
        GridLayoutManager othersManager = new GridLayoutManager(requireContext(), 2);

        // Set grid as the layout manager for each recycler view
        // Set the respective adapter
        pillsLayout.setLayoutManager(pillsManager);
        pillsLayout.setAdapter(pillsAdapter);

        liquidsLayout.setLayoutManager(liquidsManager);
        liquidsLayout.setAdapter(liquidsAdapter);

        othersLayout.setLayoutManager(othersManager);
        othersLayout.setAdapter(othersAdapter);

        // Placeholder button
        ImageButton pillsButton = view.findViewById(R.id.fillByScan);
        ImageButton fillFormButton = view.findViewById(R.id.fillForm);
        ImageButton othersButton = view.findViewById(R.id.fillFormByHistory);

        popup_window = view.findViewById(R.id.inventory_medicine);

        // Toggle Buttons
        ImageView togglePills = view.findViewById(R.id.pillsCollapse);
        ImageView toggleLiquids = view.findViewById(R.id.liquidsCollapse);
        ImageView toggleOthers = view.findViewById(R.id.othersCollapse);

        togglePills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pillsAdapter != null){
                    pillsAdapter.toggleItemLimits();
                }
                ImageView pillsCollapse = view.findViewById(R.id.pillsCollapse);
                if(pillsAdapter.getIsExpanded()){
                    pillsCollapse.setImageResource(R.drawable.arrow_up);
                }
                else{
                    pillsCollapse.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        toggleLiquids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liquidsAdapter != null){
                    liquidsAdapter.toggleItemLimits();
                }
                ImageView liquidsCollapse = view.findViewById(R.id.liquidsCollapse);
                if(liquidsAdapter.getIsExpanded()){
                    liquidsCollapse.setImageResource(R.drawable.arrow_up);
                }
                else{
                    liquidsCollapse.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        toggleOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(othersAdapter != null){
                    othersAdapter.toggleItemLimits();
                }
                ImageView othersCollapse = view.findViewById(R.id.othersCollapse);
                if(othersAdapter.getIsExpanded()){
                    othersCollapse.setImageResource(R.drawable.arrow_up);
                }
                else{
                    othersCollapse.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        // Set button click for respective buttons
        pillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add placeholder medicine to pills category
                addMedicineToLayout("Pills","PlaceHolder", "Fever", 100, ContextCompat.getDrawable(requireContext(), R.drawable.app_logo));
                Toast.makeText(requireContext(), "New Pill Added", Toast.LENGTH_SHORT).show();
            }
        });

        fillFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });

        othersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add placeholder medicine to others category
                addMedicineToLayout("Others","PlaceHolder", "Fever", 100, ContextCompat.getDrawable(requireContext(), R.drawable.app_logo));
                Toast.makeText(requireContext(), "New Other Medicine Added", Toast.LENGTH_SHORT).show();
            }
        });
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (popupImageView != null) {
                            // Update the popup's ImageView
                            popupImageView.setImageURI(imageUri);
                            uploadImageButton.setVisibility(View.GONE);
                            openCameraButton.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(requireContext(), "Popup not open!", Toast.LENGTH_SHORT).show();}
                    } else {
                        Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();}
                }
        );

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (camUri != null) {
                        popupImageView.setImageURI(camUri); // Set image from saved URI
                        uploadImageButton.setVisibility(View.GONE);
                        openCameraButton.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(requireContext(), "Popup not open!", Toast.LENGTH_SHORT).show();}
                    }
                else {
                    Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();}
                }
        });
        return view;
    }

    private void addMedicineToLayout(String category, String mediName, String mediDesc, int mediAmount, Drawable mediImage) {
        ArrayList<MedicineButton> containerList = new ArrayList<>();
        MedicineAdapter adapter = null;

        if(category.equals("Pills")){
            containerList = pillsContainers;
            adapter = pillsAdapter;
        }
        else if(category.equals("Liquids")){
            containerList = liquidsContainers;
            adapter = liquidsAdapter;
        }
        else{
            containerList = othersContainers;
            adapter = othersAdapter;

        }
        // Add placeholder medicine button
        MedicineButton newButton = new MedicineButton(mediName, mediDesc, mediAmount, mediImage);
        // Add the placeholder to the appropriate container list
        containerList.add(newButton);

        // Update medicine List such that when new button is added, it will be added to sublist
        adapter.updateMedicineList(containerList);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

        if(category.equals("Pills")){
            sharedViewModel.setPillsButtonList(containerList);
        }
        if(category.equals("Liquids")){
            sharedViewModel.setLiquidsButtonList(containerList);
        }
        if(category.equals("Others")){
            sharedViewModel.setOthersButtonList(containerList);
        }
    }

    private void showPopUpWindow(){
        View popupView = View.inflate(requireContext(), R.layout.form_fillup, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        // Focusable allow us to focus on the edit text and key values
        popupImageView = popupView.findViewById(R.id.image);

        PopupWindow popupWindow = new PopupWindow(popupView, width , height ,true);
        popupWindow.showAtLocation(popup_window, Gravity.CENTER, 0,0);

        openCameraButton = popupView.findViewById(R.id.open_camera);

        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCamera();
            }
        });


        // Drop down list for category
        Spinner category = popupView.findViewById(R.id.category);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) view;
                // Change the text color of the selected item
                selectedText.setTextColor(Color.BLACK);
                // Change the text size of the selected item (e.g., 20sp)
                selectedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.formMediCategory,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(spinnerAdapter);

        // Exit button to close popup window
        Button exitButton = popupView.findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }}
        );

        uploadImageButton = popupView.findViewById(R.id.upload_image);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }}
        );

        confirmButton = popupView.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO retrieve information from the popup window
                EditText nameView = popupView.findViewById(R.id.formMediName);
                EditText amountView = popupView.findViewById(R.id.formMediAmount);
                EditText descView = popupView.findViewById(R.id.formMediDesc);
                ImageView imageView = popupView.findViewById(R.id.image);
                String mediType = category.getSelectedItem().toString();

                String mediName = nameView.getText().toString().trim();
                String mediDesc = descView.getText().toString().trim();
                String amount = amountView.getText().toString().trim();
                int mediAmount = Integer.parseInt(amount);
                Drawable image = imageView.getDrawable();
                // Pass corresponding parameters
                addMedicineToLayout(mediType,mediName, mediDesc,mediAmount, image);
                 popupWindow.dismiss();
            }
        });

    }
    private void pickImage() {
        // Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void pickCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Medicine");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera");
        camUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,camUri);
        cameraLauncher.launch(cameraIntent);
    }
}





