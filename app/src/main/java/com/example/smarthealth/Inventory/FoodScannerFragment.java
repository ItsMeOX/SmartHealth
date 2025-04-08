package com.example.smarthealth.Inventory;

import static com.example.smarthealth.chatbot.ChatBotFragment.JSON;

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
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FoodScannerFragment extends DialogFragment {

    private ImageView foodImage;

    OkHttpClient client = new OkHttpClient();
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View popupView = inflater.inflate(R.layout.food_scan, null);

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
                    callAPI(base64String);
                    // Update database
                }

                dismiss();
            }
        });


        return popupView;
    }




    void callAPI(String base64Image) {
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
                        JSONObject messageObject = outputArray.getJSONObject(0);
                        JSONArray contentArray = messageObject.getJSONArray("content");
                        String image_result = contentArray.getJSONObject(0).getString("text");
                        Log.d("Response", image_result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("debug", "Failed to load Json File due to " + e.getMessage());
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

}
