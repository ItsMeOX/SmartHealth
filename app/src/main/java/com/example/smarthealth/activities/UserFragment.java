package com.example.smarthealth.activities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.smarthealth.R;
import com.example.smarthealth.api_service.ProfileService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.api_service.UserDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private TextView fullNameText, emailText, mobileText, dobText, addressText, metricsText;
    private ProfileService profileService;
    private ImageView profileImage;
    private long userId;

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_fragment, container, false);

        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);
        Log.d("User_ID", "current User_ID: " + userId);

        profileService = RetrofitClient.getInstance().create(ProfileService.class);

        fullNameText = view.findViewById(R.id.fullNameText);
        emailText = view.findViewById(R.id.emailText);
        mobileText = view.findViewById(R.id.mobileText);
        dobText = view.findViewById(R.id.dobText);
        addressText = view.findViewById(R.id.addressText);
        metricsText = view.findViewById(R.id.metricsText);
        profileImage = view.findViewById(R.id.profileImage);

        TextView editProfileBtn = view.findViewById(R.id.editProfile);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment editProfileFragment = new EditProfileFragment();
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                                .beginTransaction();
                transaction.replace(R.id.fragmentContainer, editProfileFragment);
                transaction.commit();
            }
        });

        ImageView editMetricsBtn = view.findViewById(R.id.editMetricsBtn);
        editMetricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment editMetricsFragment = new EditMetricsFragment();
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragmentContainer, editMetricsFragment);
                transaction.commit();
            }
        });

        CardView signOutButton = view.findViewById(R.id.signOutBtn);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        loadUserDetails();

        return view;
    }

    private void loadUserDetails(){
        if (userId == -1) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserDto> call = profileService.getUserById(userId);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if(response.isSuccessful() && response.body() != null){
                    UserDto user = response.body();
                    if (user != null && user.getProfilePictureUrl() != null) {
                        Glide.with(requireContext())
                                .load(user.getProfilePictureUrl())
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_error)
                                .into(profileImage);
                    }
                    fullNameText.setText(String.valueOf(user.getFullName()));
                    emailText.setText(String.valueOf(user.getEmail()));
                    mobileText.setText(setTextView(String.valueOf(user.getPhoneNumber())));
                    dobText.setText(setTextView(String.valueOf(user.getDob())));
                    addressText.setText(setTextView(String.valueOf(user.getAddress())));
                    String weight = String.valueOf(user.getWeight());
                    String height = String.valueOf(user.getHeight());
                    metricsText.setText(weight + " kg | " + height + " cm" );
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Log.d("UserCall", t.getMessage());
                if (isAdded() && getActivity() != null) {
                    Toast.makeText(getActivity(), "Failed to load metrics", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String setTextView(String str){
        if(str.equals("null")){
            return "-";
        }
        return str;
    }

    public void logout() {
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putLong("userId", -1);
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}