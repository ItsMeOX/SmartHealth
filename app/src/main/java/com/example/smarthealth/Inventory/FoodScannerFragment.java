package com.example.smarthealth.Inventory;

import static android.content.Context.MODE_PRIVATE;
import static com.example.smarthealth.chatbot.ChatBotFragment.JSON;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smarthealth.R;
import com.example.smarthealth.nutrient_intake.DatabaseNutrientIntakeProvider;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FoodScannerFragment extends DialogFragment {

    private ImageView foodImage;
    private long userId;
    private SharedPreferences sharedPreferences;
    OkHttpClient client = new OkHttpClient();
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View popupView = inflater.inflate(R.layout.food_scan, null);

        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);
        Log.d("User_ID", "current User_ID: " + userId);

        foodImage = popupView.findViewById(R.id.foodImage);
        Bundle res = getArguments();
        if(res != null){
            String imageUri = res.getString("imageUri");
            if(imageUri != null){
                // Set image uri in the image view
                foodImage.setImageURI(Uri.parse(imageUri));
            }
        }

        MaterialButton confirmButton = popupView.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(foodImage.getDrawable() != null){
                    // Convert drawable to byte array
                    byte[] foodByteArray = drawableToByteArray(foodImage.getDrawable());
                    String base64String = Base64.encodeToString(foodByteArray, Base64.DEFAULT);
                    callAPI(base64String, new OnNutrientParsedCallback() {
                        @Override
                        public void onParsed(List<Double> nutrientValues) {
                            if (nutrientValues != null) {
                                DatabaseNutrientIntakeProvider dbProvider = new DatabaseNutrientIntakeProvider();
                                dbProvider.updateNutrientIntake(userId, nutrientValues, new DatabaseNutrientIntakeProvider.OnIntakeUpdateCallback() {
                                    @Override
                                    public void onIntakeUpdate(boolean success) {
                                        if(success){
                                            Log.d("debug", "nutrient intake added!");
                                        } else {
                                            Log.d("debug", "nutrient intake failed!");
                                        }
                                    }
                                });
                            } else {
                                Log.d("debug", "Die!");
                            }
                        }
                    });
                }

                dismiss();
            }
        });


        return popupView;
    }

    private void callAPI(String base64Image, OnNutrientParsedCallback callback) {
        //okhttp'
//        String image_result = "";
        ChatBotImageFood chatBotImageFood = new ChatBotImageFood(base64Image);
        JSONObject jsonBody = chatBotImageFood.getPrompt();
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(chatBotImageFood.getAPI_URL())
                .header("Authorization", "Bearer " + chatBotImageFood.getAPI_Key())
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("Request Error", "Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        Log.d("debug", jsonObject.toString());
                        JSONArray outputArray = jsonObject.getJSONArray("output");
                        Log.d("debug", outputArray + "");
                        JSONObject messageObject = outputArray.getJSONObject(0);
                        Log.d("debug", messageObject + "");
                        JSONArray contentArray = messageObject.getJSONArray("content");
                        Log.d("debug", contentArray + "");
                        JSONObject image_result = contentArray.getJSONObject(0).getString();
                        Log.d("debug", image_result + "");

                        List<Double> nutrients = new ArrayList<>();
                        nutrients.add(Double.parseDouble(jsonObject.getString("Carbs")));
                        nutrients.add(Double.parseDouble(jsonObject.getString("Proteins")));
                        nutrients.add(Double.parseDouble(jsonObject.getString("Fats")));
                        nutrients.add(Double.parseDouble(jsonObject.getString("Fibre")));
                        nutrients.add(Double.parseDouble(jsonObject.getString("Sugars")));
                        nutrients.add(Double.parseDouble(jsonObject.getString("Sodium")));
                        callback.onParsed(nutrients);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("debug", "Failed to load Json File due to " + e.getMessage());
                        callback.onParsed(null);
                    }
                } else {
                    Log.d("debug", "Failed to load response due to " + response.body().toString());
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    Log.d("Failed to load the Response File ", errorBody);
                }
            }
        });
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
    public interface OnNutrientParsedCallback {
        void onParsed(List<Double> nutrientValues);
    }
}


