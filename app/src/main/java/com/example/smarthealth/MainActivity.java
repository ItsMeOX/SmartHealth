package com.example.smarthealth;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.provider.MediaStore;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Set layout for inventory_medicine
        setContentView(R.layout.inventory_medicine);

        // Create list to hold button
        pillsContainers = new ArrayList<>();
        liquidsContainers = new ArrayList<>();
        othersContainers = new ArrayList<>();

        // Create adapters for each category
        pillsAdapter = new MedicineAdapter(this, pillsContainers);
        liquidsAdapter = new MedicineAdapter(this, liquidsContainers);
        othersAdapter = new MedicineAdapter(this, othersContainers);

        // Get respective layout
        pillsLayout = findViewById(R.id.pillsRV);
        liquidsLayout = findViewById(R.id.liquidsRV);
        othersLayout = findViewById(R.id.othersRV);

        // Create grid layout for each layout
        GridLayoutManager pillsManager = new GridLayoutManager(this, 2);
        GridLayoutManager liquidsManager = new GridLayoutManager(this, 2);
        GridLayoutManager othersManager = new GridLayoutManager(this, 2);

        // Set grid as the layout manager for each recycler view
        // Set the respective adapter
        pillsLayout.setLayoutManager(pillsManager);
        pillsLayout.setAdapter(pillsAdapter);

        liquidsLayout.setLayoutManager(liquidsManager);
        liquidsLayout.setAdapter(liquidsAdapter);

        othersLayout.setLayoutManager(othersManager);
        othersLayout.setAdapter(othersAdapter);

        // Placeholder button
        ImageButton pillsButton = findViewById(R.id.fillByScan);
        ImageButton liquidsButton = findViewById(R.id.fillForm);
        ImageButton othersButton = findViewById(R.id.fillFormByHistory);

        popup_window = findViewById(R.id.inventory_medicine);

        // Set button click for respective buttons
        pillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add placeholder medicine to pills category
                addMedicineToLayout(pillsContainers, pillsAdapter, "Pills");
                Toast.makeText(MainActivity.this, "New Pill Added", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "New Other Medicine Added", Toast.LENGTH_SHORT).show();
            }
        });
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            // Display image
                            ImageView imageView = findViewById(R.id.image);
                            imageView.setImageURI(imageUri);
                        } else {
                            Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    private void addMedicineToLayout(ArrayList<MedicineButton> containerList, MedicineAdapter adapter, String category) {
        // Add placeholder medicine button
        MedicineButton placeholder = new MedicineButton(category + " Placeholder", "Description", 100, R.drawable.clock);

        // Add the placeholder to the appropriate container list
        containerList.add(placeholder);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }

    private void showPopUpWindow(){
        View view = View.inflate(this, R.layout.form_fillup, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        // Focusable allow us to focus on the edit text and key values
        PopupWindow popupWindow = new PopupWindow(view, width , height ,true);
        popupWindow.showAtLocation(popup_window, Gravity.CENTER, 0,0);

        // Exit button to close popup window
        Button exitButton = view.findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }}
        );

        Button uploadImage = view.findViewById(R.id.upload_image);
        uploadImage.setOnClickListener(new View.OnClickListener() {
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





