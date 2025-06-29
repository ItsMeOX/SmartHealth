package com.example.smarthealth.Inventory;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.MedicineDto;
import com.example.smarthealth.api_service.MedicineService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormPageFragment extends DialogFragment {
    private Uri camUri;
    private ImageView popupImageView;
    private Button openCameraButton, uploadImageButton;
    private LinearLayout tagChosen;
    final int MAX_SELECTED = 2;
    private MedicineService medicineService;
    private long userId;
    private SharedPreferences sharedPreferences;
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (popupImageView != null) {
                    popupImageView.setImageURI(imageUri);
                    uploadImageButton.setVisibility(View.GONE);
                    openCameraButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(requireContext(), "Popup not open!", Toast.LENGTH_SHORT).show();}
            } else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();}
        }
    });
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

    @Override
    public void onStart(){
        super.onStart();
        if (getDialog() != null)
        {
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
        medicineService = RetrofitClient.getInstance().create(MedicineService.class);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);
        Log.d("User_ID", "current User_ID: " + userId);
        View popupView = inflater.inflate(R.layout.form_fillup, null);
        popupImageView = popupView.findViewById(R.id.image);
        openCameraButton = popupView.findViewById(R.id.open_camera);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCamera();
            }
        });

        uploadImageButton = popupView.findViewById(R.id.upload_image);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        // Get image uri from smartScan

        Bundle res = getArguments();
        if (res != null) {
            String imageUri = res.getString("ImageString");  // Retrieve the URI from the Bundle
            if (imageUri != null) {
                Uri camUri = Uri.parse(imageUri);  // Parse the URI string
                Log.d("FormPageFragment", "Received image URI: " + camUri.toString());

                // Retrieve Chatbot data
                // Set the image to the ImageView
                popupImageView = popupView.findViewById(R.id.image);
                popupImageView.setImageURI(camUri);

                EditText nameView = popupView.findViewById(R.id.formMediName);
                EditText amountView = popupView.findViewById(R.id.formMediAmount);
                EditText dosageView = popupView.findViewById(R.id.formMediDosage);
                EditText containsView = popupView.findViewById(R.id.formMediContains);
                EditText sideEffectView = popupView.findViewById(R.id.formMediSideEffect);

                nameView.setText(res.getString("Name"));
                String category = res.getString("Category");
                String amountml = res.getString("Amountml");
                String amountPill = res.getString("Amountpill");
                if(category.equals("Pills")){
                    amountView.setText(amountPill);
                }
                else if(category.equals("Liquids")){
                    amountView.setText(amountml);
                }
//                ArrayList<String> tags = res.getStringArrayList("Tags");
                ArrayList<String> contains = res.getStringArrayList("Contains");
                String joinedString = String.join(", ", contains);
                containsView.setText(joinedString);

                String dosage = res.getString("Dosage");
                dosageView.setText(dosage);

                ArrayList<String> sideEffect = res.getStringArrayList("SideEffect");
                String sideEffectString = String.join(", ", sideEffect);
                sideEffectView.setText(sideEffectString);

                // Hide the upload and camera buttons since the image is already set
                Button uploadImageButton = popupView.findViewById(R.id.upload_image);
                Button openCameraButton = popupView.findViewById(R.id.open_camera);
                uploadImageButton.setVisibility(View.GONE);
                openCameraButton.setVisibility(View.GONE);
            } else {
                Log.d("FormPageFragment", "Image URI is null in the Bundle");
            }
        }
        else {
            Log.d("FormPageFragment", "No data received in the Bundle");
        }

        Button exitButton = popupView.findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }}
        );

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

        Spinner medicineTags = popupView.findViewById(R.id.multiSelectSpinner);
        tagChosen = popupView.findViewById(R.id.tagChosen);

        String[] mediTag = getResources().getStringArray(R.array.formMediTag);
        boolean[] selectedItems = new boolean[mediTag.length];
        ArrayList<String> selectedTags = new ArrayList<>();
        medicineTags.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showMultiSelectDialog(tagChosen, mediTag, selectedItems, selectedTags);
                }
                return true;
            }
        });

        Button confirmButton = popupView.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameView = popupView.findViewById(R.id.formMediName);
                String type = category.getSelectedItem().toString();
                EditText amountView = popupView.findViewById(R.id.formMediAmount);
                ImageView imageView = popupView.findViewById(R.id.image);
                EditText dosageView = popupView.findViewById(R.id.formMediDosage);
                EditText sideEffectView = popupView.findViewById(R.id.formMediSideEffect);
                EditText containsView = popupView.findViewById(R.id.formMediContains);
                LinearLayout mediTags = popupView.findViewById(R.id.tagChosen);

                String mediName = nameView.getText().toString().trim();
                String amount = amountView.getText().toString().trim();
                Drawable image = imageView.getDrawable();
                String mediDosage = dosageView.getText().toString().trim();
                String mediSideEffect = sideEffectView.getText().toString().trim();
                String mediContains = containsView.getText().toString().trim();
                ArrayList<String> tagList = new ArrayList<>();
                for(int i=0; i<mediTags.getChildCount();i++){
                    TextView child = (TextView) mediTags.getChildAt(i);
                    String tag = child.getText().toString().trim();
                    tagList.add(tag);
                }

                // Pass corresponding parameters
                if (mediName.isEmpty() || mediDosage.isEmpty() || amount.isEmpty() || mediSideEffect.isEmpty() ||
                        mediContains.isEmpty() || imageView.getDrawable() == null || Integer.parseInt(amount) > 999
                        || Integer.parseInt(amount) <= 0 || tagList.isEmpty()) {
                    if (mediName.isEmpty()) {
                        nameView.setError("Required");
                    }
                    if (mediContains.isEmpty()) {
                        containsView.setError("Required");
                    }
                    if (amount.isEmpty()) {
                        amountView.setError("Required");
                    }
                    if (mediSideEffect.isEmpty()) {
                        sideEffectView.setError("Required");
                    }
                    if (mediDosage.isEmpty()) {
                        dosageView.setError("Required");
                    }
                    if (imageView.getDrawable() == null) {
                        Toast.makeText(requireContext(), "Please select an image!", Toast.LENGTH_SHORT).show();
                    }
                    if (Integer.parseInt(amount) > 999){
                        amountView.setError("Amount lesser than 999");
                    }
                    if(Integer.parseInt(amount) <= 0){
                        amountView.setError("Amount greater than 0");
                    }

                    if(tagList.isEmpty()){
                        Toast.makeText(requireContext(), "Please select Tags!", Toast.LENGTH_SHORT).show();
                    }

                    return;
                } else {
                    int mediAmount = Integer.parseInt(amount);
                    byte[] imageData = drawableToByteArray(image);

                    MedicineDto medicineDto = new MedicineDto(
                            mediName,
                            mediAmount,
                            type,
                            null,
                            mediDosage,
                            mediContains,
                            String.join(",",tagList),
                            mediSideEffect
                    );

                    Gson gson = new Gson();
                    String json = gson.toJson(medicineDto);

                    okhttp3.RequestBody medicineDtoBody = okhttp3.RequestBody.create(
                            json,
                            MediaType.parse("application/json")
                    );

                    MultipartBody.Part imagePart = null;
                    if (imageData != null) {
                        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(
                                imageData,
                                MediaType.parse("image/*")
                        );
                        String fileName = UUID.randomUUID().toString() + ".jpg";
                        imagePart = MultipartBody.Part.createFormData("imageFile", fileName, requestFile);
                    }

                    Call<MedicineDto> call = medicineService.createMedicine(userId, medicineDtoBody, imagePart);
                    call.enqueue(new Callback<MedicineDto>() {
                        @Override
                        public void onResponse(Call<MedicineDto> call, Response<MedicineDto> response) {
                            if(response.isSuccessful() && response.body() != null){
                                Log.d("debug", "Add Medicine Successfully!");
                            }
                        }

                        @Override
                        public void onFailure(Call<MedicineDto> call, Throwable t) {
                            Log.d("debug", "Network Error! " + t.getMessage());
                        }
                    });

                    dismiss();
                }
            };
        });

        return popupView;
    }
    private void showMultiSelectDialog(LinearLayout tagView, String[] items, boolean[] selectedItems, ArrayList<String> selectedTags) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Medicine Tags");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tag_multiselect, null);
        ListView listView = dialogView.findViewById(R.id.multiSelectListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_tag, R.id.tagText, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null){
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_tag, parent, false);
                }

                CheckBox checkBox = convertView.findViewById(R.id.checkBox);
                TextView textView = convertView.findViewById(R.id.tagText);

                textView.setText(items[position]);
                // Prevent reusing view and changing selected items
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(selectedItems[position]);

                // Set background tint color dynamically
                ColorStateList colorStateList = ColorStateList.valueOf(TagManager.getMedicineTagColor(items[position], requireContext()));
                textView.setBackgroundTintList(colorStateList);  // Apply background tint

                // Selected Items stored in selectedItems
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tagView.removeAllViews();
                        int count = 0;
                        for(boolean selected:selectedItems){
                            if(selected){count++;}
                        }
                        selectedItems[position] = isChecked;
                        if(isChecked){
                            if(count >= MAX_SELECTED){
                                checkBox.setChecked(false);
                                Toast.makeText(requireContext(), "Select up to 2 Only", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                selectedItems[position] = true;
                                if(!selectedTags.contains(items[position])){
                                    selectedTags.add(items[position]);
                                }
                            }
                        }
                        else{
                            selectedItems[position] = false;
                            selectedTags.remove(items[position]);
                        }
                    }
                });
                return convertView;
            }
        };
        // Set Confirm button for selection of tags
        listView.setAdapter(adapter);
        builder.setView(dialogView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                layoutParams.leftMargin = 18;

                // Remove all views before adding to keep the latest selection
                tagView.removeAllViews();
                if(!selectedTags.isEmpty()){
                    TagManager.addTags(requireContext(),selectedTags,tagView);
                }
            }
        });
        // Set cancel button to dismiss and clear selection
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedTags.clear();
                dialog.dismiss();
            }
        });
        builder.show();
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
    private void pickImage() {
        // Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
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