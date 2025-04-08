package com.example.smarthealth.activities;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.ProfileService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.api_service.UserDto;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class EditProfileFragment extends Fragment {
    private CardView saveProfileBtn, changePhotoButton;
    private TextInputEditText editFullName, editDOB, editMobile, editAddress;
    private ImageView editProfileImage;
    private SharedPreferences sharedPreferences;
    private ProfileService profileService;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    View view;
    private long userId;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                // editProfileImage.setImageURI(selectedImageUri);
                uploadProfileImage(userId, selectedImageUri);
                Toast.makeText(requireContext(), "Image selected!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceBundle);
//        setContentView(R.layout.edit_profile_fragment);
        view = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        profileService = RetrofitClient.getInstance().create(ProfileService.class);

        editFullName = view.findViewById(R.id.editFullName);
        editDOB = view.findViewById(R.id.editDOB);
        editMobile = view.findViewById(R.id.editMobile);
        editAddress = view.findViewById(R.id.editAddress);
        editProfileImage = view.findViewById(R.id.editProfileImage);

        changePhotoButton = view.findViewById(R.id.changePhotoButton);
        saveProfileBtn = view.findViewById(R.id.saveProfileButton);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);
        Log.d("User_ID", "current User_ID: " + userId);

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(userId);
            }
        });

        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Gallery", "Context: successfully");
                pickImage();
            }
        });


        return view;
    }

    private void updateProfile(long userId){
        String fullName = editFullName.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        if(!validateInputs(fullName, dob, mobile, address)){
            return;
        }

        if (fullName.isEmpty() || dob.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserDto> call = profileService.updateProfile(userId, fullName, dob, mobile, address);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Update Profile Successful!", Toast.LENGTH_SHORT).show();
                    Fragment userFragment = new UserFragment();
                    FragmentTransaction transaction = getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragmentContainer, userFragment);
                    transaction.commit();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(getActivity(), "Update Profile Failed: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Update Profile Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String fullName, String dob, String mobile, String address) {
        if (fullName.isEmpty()) {
            editFullName.setError("Name is required");
            return false;
        }

        if (!validateDOB(dob)) {
            return false;
        }

        if (mobile.isEmpty()) {
            editMobile.setError("Phone number is required");
            return false;
        }

        if (address.isEmpty()) {
            editAddress.setError("Phone number is required");
            return false;
        }

        return true;
    }
    private boolean validateDOB(String dobStr) {
        if (dobStr.isEmpty()) {
            editDOB.setError("Date of Birth cannot be empty");
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        sdf.setLenient(false); // Ensures strict date parsing

        try {
            Date dob = sdf.parse(dobStr);
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);

            // Adjust age if the birth date has not occurred this year yet
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            if (age < 13) {
                editDOB.setError("You must be at least 13 years old");
                return false;
            }

            if (age > 120) {
                editDOB.setError("Please enter a valid age");
                return false;
            }

        } catch (ParseException e) {
            editDOB.setError("Enter a valid date (dd/MM/yyyy)");
            return false;
        }

        return true;
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void uploadProfileImage(long userId, Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File file = createTempFileFromUri(imageUri);
            okhttp3.RequestBody requestFile =
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Call<UserDto> call = profileService.uploadProfileImage(userId, body);
            call.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Profile image updated!", Toast.LENGTH_SHORT).show();
                        // Optionally update ImageView with new image
                        editProfileImage.setImageURI(imageUri);
                    } else {
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Toast.makeText(getActivity(), "Upload failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "File conversion failed", Toast.LENGTH_SHORT).show();
        }
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
        File tempFile = new File(requireContext().getCacheDir(), "profile_image.jpg");;
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();
        return tempFile;
    }
}