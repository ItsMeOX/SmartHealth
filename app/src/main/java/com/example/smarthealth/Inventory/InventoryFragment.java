package com.example.smarthealth.Inventory;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.lifecycle.ViewModel;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private ActivityResultLauncher<Intent> resultLauncher;
    private View view;
    private ImageView popupImageView;
    private Button uploadImageButton, openCameraButton;
    private SVMInventory sharedViewModel;

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
        ImageButton liquidsButton = view.findViewById(R.id.fillForm);
        ImageButton othersButton = view.findViewById(R.id.fillFormByHistory);

        popup_window = view.findViewById(R.id.inventory_medicine);

        // Set button click for respective buttons
        pillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add placeholder medicine to pills category
                addMedicineToLayout(pillsContainers, pillsAdapter, "Pills");
                Toast.makeText(requireContext(), "New Pill Added", Toast.LENGTH_SHORT).show();
            }
        });

        liquidsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });

        othersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add placeholder medicine to others category
                addMedicineToLayout(othersContainers, othersAdapter, "Others");
                Toast.makeText(requireContext(), "New Other Medicine Added", Toast.LENGTH_SHORT).show();
            }
        });
        resultLauncher = registerForActivityResult(
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
                            Toast.makeText(requireContext(), "Popup not open!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        return view;
    }
    private void addMedicineToLayout(ArrayList<MedicineButton> containerList, MedicineAdapter adapter, String category) {
        // Add placeholder medicine button
        MedicineButton placeholder = new MedicineButton(category + " Placeholder", "Description", 100, R.drawable.clock);
        // Add the placeholder to the appropriate container list
        containerList.add(placeholder);
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
    }
    private void pickImage() {
        // Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }
}





