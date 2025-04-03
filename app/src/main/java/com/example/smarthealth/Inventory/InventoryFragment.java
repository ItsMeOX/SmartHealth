package com.example.smarthealth.Inventory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Optional;
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
    private View view;
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
        ImageButton smartScan = view.findViewById(R.id.fillByScan);
        ImageButton fillFormButton = view.findViewById(R.id.fillForm);
        ImageButton formHistory = view.findViewById(R.id.fillFormByHistory);

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
        smartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add placeholder medicine to pills category
                ArrayList<String> list = new ArrayList<>();
                list.add("Cough");
                addMedicineToLayout("Pills","PlaceHolder", 100,
                        ContextCompat.getDrawable(requireContext(), R.drawable.app_logo),"1 tab per day",
                        "Paracetamol","Drowsy",list);
                Toast.makeText(requireContext(), "New Pill Added", Toast.LENGTH_SHORT).show();
            }
        });

        fillFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormPageFragment formDialog = new FormPageFragment();
                formDialog.show(getParentFragmentManager(), "Form fill");
            }
        });

        formHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormHistoryFragment historyDialog = new FormHistoryFragment();
                historyDialog.show(getParentFragmentManager(), "History fill");
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve medicine data from dialog fragment
        getParentFragmentManager().setFragmentResultListener("medicineData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Retrieve the data needed to add medicine
                String name = result.getString("Name");
                String category = result.getString("Category");
                int amount = result.getInt("Amount");
                byte[] imageData = result.getByteArray("Image");
                String dosage = result.getString("Dosage");
                String contains = result.getString("Contains");
                String sideEffect = result.getString("Side Effect");
                ArrayList<String> tagList = result.getStringArrayList("Tags");

                Drawable imageDrawable = byteArrayToDrawable(imageData);
                // Add medicine to layout
                addMedicineToLayout(name, category, amount, imageDrawable, dosage, contains, sideEffect, tagList);
            }
        });

        getParentFragmentManager().setFragmentResultListener("History Data", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Retrieve the data needed to add medicine
                String name = result.getString("Name");
                String category = result.getString("Category");
                int amount = result.getInt("Amount");
                byte[] imageData = result.getByteArray("Image");
                String dosage = result.getString("Dosage");
                String contains = result.getString("Contains");
                String sideEffect = result.getString("Side Effect");
                ArrayList<String> tagList = result.getStringArrayList("Tags");

                Drawable imageDrawable = byteArrayToDrawable(imageData);
                // Add medicine to layout
                addMedicineToLayout(name, category, amount, imageDrawable, dosage, contains, sideEffect, tagList);
            }
        });
    }

    private void addMedicineToLayout(String mediName, String category, int mediAmount,
                                     Drawable mediImage, String mediDosage, String mediContains,
                                     String mediSideEffect, ArrayList<String> type) {
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
        MedicineButton newButton = new MedicineButton(mediName, category, mediAmount,
                mediImage, mediDosage, mediContains, mediSideEffect, type);
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
    private Drawable byteArrayToDrawable(byte[] imageData) {
        if (imageData == null) return null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return new BitmapDrawable(getResources(), bitmap);
    }
}





