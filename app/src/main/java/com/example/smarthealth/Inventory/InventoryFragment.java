package com.example.smarthealth.Inventory;

import static android.content.Context.MODE_PRIVATE;

import static com.example.smarthealth.chatbot.ChatBotFragment.JSON;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.MedicineDto;
import com.example.smarthealth.api_service.MedicineService;
import com.example.smarthealth.api_service.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private MedicineService medicineService;
    private SharedPreferences sharedPreferences;
    private long userId;
    private Bundle res;
    private Uri camUri;
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        OkHttpClient client = new OkHttpClient();

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (camUri != null) {
                    byte[] medicineByteArray = uriToByteArray(camUri);
                    res = new Bundle();
                    res.putString("ImageString", camUri.toString());
                    String base64String = Base64.encodeToString(medicineByteArray, Base64.DEFAULT);
                    callAPI(base64String);


                }
                else{
                    Toast.makeText(requireContext(), "Popup not open!", Toast.LENGTH_SHORT).show();}
            }
            else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();}
        }


        void callAPI(String base64Image) {

            ChatBotImageMedicine chatBotImageMedicine = new ChatBotImageMedicine(base64Image);
            JSONObject jsonBody = chatBotImageMedicine.getPrompt();
            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(chatBotImageMedicine.getAPI_URL())
                    .header("Authorization", "Bearer " + chatBotImageMedicine.getAPI_Key())
                    .post(body)
                    .build();


            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    Log.d("Request Error", "Failed to load response due to " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.body().string());
                            JSONArray outputArray = jsonObject.getJSONArray("output");
                            JSONObject messageObject = outputArray.getJSONObject(0);
                            JSONArray contentArray = messageObject.getJSONArray("content");
                            String image_result = contentArray.getJSONObject(0).getString("text");
                            Log.d("GPT Response For Medicine Image", image_result);
//                            {"name":"Metoclopramide","category":"Pills","amount_pill":"10","amount_ml":"0","treatment":["Vomiting","Giddiness"],"recommended_dosage":"1 tablet 3 times a day","contains":["Metoclopramide"],"side_effects":["Drowsiness"]}//
                            JSONObject object = new JSONObject(image_result);

                            res.putString("Name",object.getString("name"));
                            res.putString("Category",object.getString("category"));
                            res.putString("Amountpill",object.getString("amount_pill"));
                            res.putString("Amountml",object.getString("amount_ml"));
                            JSONArray tags = object.getJSONArray("treatment");
                            ArrayList<String> tagList = new ArrayList<>();
                            for (int i = 0; i < tags.length(); i++) {
                                tagList.add(tags.getString(i));
                            }
                            res.putStringArrayList("Tags",tagList);
                            res.putString("Dosage", object.getString("recommended_dosage"));

                            JSONArray contains = object.getJSONArray("contains");
                            ArrayList<String> containsList = new ArrayList<>();
                            for (int i = 0; i < contains.length(); i++) {
                                containsList.add(contains.getString(i));
                            }
                            res.putStringArrayList("Contains",containsList);

                            JSONArray sideEffect = object.getJSONArray("side_effects");
                            ArrayList<String> sideEffectList = new ArrayList<>();
                            for (int i = 0; i < sideEffect.length(); i++) {
                                sideEffectList.add(sideEffect.getString(i));
                            }
                            res.putStringArrayList("SideEffect",sideEffectList);

                            FormPageFragment formDialog = new FormPageFragment();
                            formDialog.setArguments(res);
                            formDialog.show(getParentFragmentManager(), "Smart Scan");


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Debug", "Failed to load Json File due to " + e.getMessage());
                        }
                    } else {
                        Log.d("Debug", "Failed to load response due to " + response.body().toString());
                        String errorBody = response.body() != null ? response.body().string() : "No response body";
                        Log.d("Failed to load the Response File ", errorBody);
                    }
                }
            });
        }
    });

    public interface OnMedicineParsedCallBack {
        void onParsed(List<Double> medicineValues);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.inventory_fragment, container, false);

        medicineService = RetrofitClient.getInstance().create(MedicineService.class);
        if(isAdded() && getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        }
        userId = sharedPreferences.getLong("userId", -1);
        Log.d("User_ID", "current User_ID: " + userId);

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
                pickCamera();

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

        Call<List<MedicineDto>> call = medicineService.getAllMedicinesByUser(userId);
        call.enqueue(new Callback<List<MedicineDto>>() {
            @Override
            public void onResponse(Call<List<MedicineDto>> call, Response<List<MedicineDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    pillsContainers.clear();
                    liquidsContainers.clear();
                    othersContainers.clear();
                    for(MedicineDto medicine : response.body()){
                        Drawable imageDrawable = byteArrayToDrawable(medicine.getMedicineImage());
                        addMedicineToLayout(
                                medicine.getId(),
                                medicine.getMedicineName(),
                                medicine.getMedicineCategory(),
                                medicine.getMedicineAmount(),
                                imageDrawable,
                                medicine.getMedicineDosage(),
                                medicine.getMedicineContains(),
                                medicine.getMedicineSideEffect(),
                                new ArrayList<>(
                                        Arrays.asList(medicine.getMedicineType().split(","))
                                )
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MedicineDto>> call, Throwable t) {
                Log.d("debug", "Not Adding okay!" + t.getMessage());
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addMedicineToLayout(Long medicineId, String mediName, String category, int mediAmount,
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
        MedicineButton newButton = new MedicineButton(medicineId, mediName, category, mediAmount,
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
        if (isAdded() && getResources() != null) {
            return new BitmapDrawable(getResources(), bitmap);
        }
        return null;
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

    public Drawable uriToDrawable(Uri uri) {
        try {
            // Get the InputStream from the Uri
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);

            // Decode the InputStream into a Drawable
            return Drawable.createFromStream(inputStream, uri.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null; // Return null if there's an error
    }
    public byte[] uriToByteArray(Uri uri){
        Drawable draw = uriToDrawable(uri);
        return drawableToByteArray(draw);
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

    private void smartScanDialog(boolean data){

    }







}




