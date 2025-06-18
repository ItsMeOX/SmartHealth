package com.example.smarthealth.Inventory;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.smarthealth.R;
import com.example.smarthealth.api_service.MedicineDto;
import com.example.smarthealth.api_service.MedicineService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormHistoryFragment extends DialogFragment {
    private AutoCompleteTextView searchBar;
    private ArrayList<MedicineButton> medicineList;
    private MedicineService medicineService;
    private long userId;
    private SharedPreferences sharedPreferences;
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
        medicineService = RetrofitClient.getInstance().create(MedicineService.class);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);
        Log.d("User_ID", "current User_ID: " + userId);

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

        Call<List<MedicineDto>> call = medicineService.getAllMedicinesByUser(userId);
        call.enqueue(new Callback<List<MedicineDto>>() {
            @Override
            public void onResponse(Call<List<MedicineDto>> call, Response<List<MedicineDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for(MedicineDto medicineDto : response.body()){
                        MedicineButton medicineButton = new MedicineButton(
                                medicineDto.getId(),
                                medicineDto.getMedicineName(),
                                medicineDto.getMedicineCategory(),
                                medicineDto.getMedicineAmount(),
                                medicineDto.getMedicineImage(),
                                medicineDto.getMedicineDosage(),
                                medicineDto.getMedicineContains(),
                                medicineDto.getMedicineSideEffect(),
                                new ArrayList<>(
                                        Arrays.asList(medicineDto.getMedicineType().split(","))
                                )
                        );

                        medicineList.add(medicineButton);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MedicineDto>> call, Throwable t) {
                Log.d("debug", "Not Adding Okay at Searching!");
            }
        });

        searchBar = popupView.findViewById(R.id.searchBar);

        MedicineSearchBarAdapter adapter = new MedicineSearchBarAdapter(requireContext(), medicineList);
        searchBar.setAdapter(adapter);
        searchBar.setThreshold(1);

        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!searchBar.isPopupShowing()) {
                        searchBar.showDropDown();
                    }
                    searchBar.performClick();
                }
                return false;
            }
        });

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

                String imageUrl = selectedMedicine.getMedicineImage();

                Log.d("DEBUG", "Selected medicine image URL: " + imageUrl);

                Glide.with(view.getContext()) // use `getContext()` or activity/fragment context
                        .load(imageUrl)
                        .into(image);
                name.setText(selectedMedicine.getMedicineName());
                dosage.setText(selectedMedicine.getMedicineDosage());
                contain.setText(selectedMedicine.getMedicineContains());
                sideEffect.setText(selectedMedicine.getMedicineSideEffect());

                // Add tags
                TagManager.addTags(requireContext(), tags, tagLayout);
                // Button to add from history
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
                        if (amount.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Please Key in Amount", Toast.LENGTH_SHORT).show();
                        }
                        if (Integer.parseInt(amount.getText().toString().trim()) < 0 || Integer.parseInt(amount.getText().toString().trim()) > 999) {
                            Toast.makeText(getContext(), "Enter number between 0 and 999", Toast.LENGTH_SHORT).show();
                        } else {
                            int mediAmount = Integer.parseInt(amount.getText().toString().trim());
                            byte[] imageData = drawableToByteArray(image);

                            MedicineDto medicineDto = new MedicineDto(
                                    mediName,
                                    mediAmount,
                                    category,
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
                    }
                });

            }
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

    private Drawable byteArrayToDrawable(byte[] imageData) {
        if (imageData == null) return null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return new BitmapDrawable(getResources(), bitmap);
    }
}
