package com.example.smarthealth.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthealth.Inventory.FoodScannerFragment;
import com.example.smarthealth.Inventory.InventoryFragment;
import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;
import com.example.smarthealth.R;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private Map<View, Integer> selectedIcons;
    private Map<View, Integer> unselectedIcons;
    private Map<View, View> iconViews;
    private Map<View, TextView> iconTexts;
    private Uri camUri;
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (camUri != null) {
                    Bundle res = new Bundle();
                    res.putString("imageUri", camUri.toString());
                    Fragment foodScannerFragment = new FoodScannerFragment();
                    foodScannerFragment.setArguments(res);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, foodScannerFragment)
                            .addToBackStack(null).commit();

                }
                else{
                    Toast.makeText(BaseActivity.this, "Popup not open!", Toast.LENGTH_SHORT).show();}
            }
            else {
                Toast.makeText(BaseActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();}
        }
    });

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // Redirect to LoginActivity
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.base_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        setNavbarListeners();
    }

    private void setNavbarListeners() {
        View navbarHomeLayout = findViewById(R.id.navbarHomeLayout);
        View navbarInventoryLayout = findViewById(R.id.navbarInventoryLayout);
        View navbarMedbotLayout = findViewById(R.id.navbarMedbotLayout);
        View navbarUserLayout = findViewById(R.id.navbarUserLayout);

        View navbarHomeIcon = findViewById(R.id.navbarHomeIcon);
        View navbarInventoryIcon = findViewById(R.id.navbarInventoryIcon);
        View navbarMedbotIcon = findViewById(R.id.navbarMedbotIcon);
        View navbarUserIcon = findViewById(R.id.navbarUserIcon);

        TextView navbarHomeText = findViewById(R.id.navbarHomeText);
        TextView navbarInventoryText = findViewById(R.id.navbarInventoryText);
        TextView navbarMedbotText = findViewById(R.id.navbarMedBotText);
        TextView navbarUserText = findViewById(R.id.navbarUserText);
        View navbarHospitalButton = findViewById(R.id.navbarHospitalBtn);
        AppCompatButton navbarCameraButton = findViewById(R.id.navbarCameraBtn);

        iconViews = new HashMap<>();
        iconViews.put(navbarHomeLayout, navbarHomeIcon);
        iconViews.put(navbarInventoryLayout, navbarInventoryIcon);
        iconViews.put(navbarMedbotLayout, navbarMedbotIcon);
        iconViews.put(navbarUserLayout, navbarUserIcon);

        iconTexts = new HashMap<>();
        iconTexts.put(navbarHomeLayout, navbarHomeText);
        iconTexts.put(navbarInventoryLayout, navbarInventoryText);
        iconTexts.put(navbarMedbotLayout, navbarMedbotText);
        iconTexts.put(navbarUserLayout, navbarUserText);

        selectedIcons = new HashMap<>();
        selectedIcons.put(navbarHomeLayout, R.drawable.navbar_home_selected);
        selectedIcons.put(navbarInventoryLayout, R.drawable.navbar_inventory_selected);
        selectedIcons.put(navbarMedbotLayout, R.drawable.navbar_medbot_selected);
        selectedIcons.put(navbarUserLayout, R.drawable.navbar_user_selected);

        unselectedIcons = new HashMap<>();
        unselectedIcons.put(navbarHomeLayout, R.drawable.navbar_home);
        unselectedIcons.put(navbarInventoryLayout, R.drawable.navbar_inventory);
        unselectedIcons.put(navbarMedbotLayout, R.drawable.navbar_medbot);
        unselectedIcons.put(navbarUserLayout, R.drawable.navbar_user);

        navbarHomeLayout.setOnClickListener(v -> handleNavbarClick(navbarHomeLayout, new HomeFragment()));
        navbarInventoryLayout.setOnClickListener(v -> handleNavbarClick(navbarInventoryLayout, new InventoryFragment()));
        navbarMedbotLayout.setOnClickListener(v -> handleNavbarClick(navbarMedbotLayout, new HomeFragment()));
        navbarUserLayout.setOnClickListener(v -> handleNavbarClick(navbarUserLayout, new UserFragment()));
        navbarHospitalButton.setOnClickListener(view -> {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!lm.isLocationEnabled()) {
                    Toast.makeText(this, "Please Enable Location Services First", Toast.LENGTH_SHORT).show();
                } else {
                    Intent startMapClinicFinder = new Intent(BaseActivity.this, MapClinicFinder.class);
                    startActivity(startMapClinicFinder);
                }
            }
        });

        navbarCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickCamera();
            }
        });


    }

    private void handleNavbarClick(View selectedLayout, Fragment fragment) {
        for (Map.Entry<View, View> entry : iconViews.entrySet()) {
            View layout = entry.getKey();
            View iconView = entry.getValue();
            TextView textView = iconTexts.get(layout);
            iconView.setBackgroundResource(unselectedIcons.get(layout));
            textView.setTextColor(getColor(R.color.lightgray));
        }

        View selectedIcon = iconViews.get(selectedLayout);
        selectedIcon.setBackgroundResource(selectedIcons.get(selectedLayout));

        TextView textView = iconTexts.get(selectedLayout);
        textView.setTextColor(getColor(R.color.primary_orange));

        loadFragment(fragment);
    }
    // TODO 1: highlight current page icon.
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void pickCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Medicine");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera");
        camUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,camUri);
        cameraLauncher.launch(cameraIntent);
    }




}