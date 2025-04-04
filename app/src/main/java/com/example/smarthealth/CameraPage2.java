package com.example.smarthealth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class CameraPage2 extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    private static final String OPENAI_API_KEY = "your_openai_api_key"; // Replace with your actual API key

    Button btnpicture;
    ImageView imageView;
    TextView textView;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_page);

        btnpicture = findViewById(R.id.btncamera_id);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.result_text);

        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(cameraIntent);
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        WeakReference<Bitmap> result_1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap,
                                        imageBitmap.getWidth(), imageBitmap.getHeight(), false).
                                copy(Bitmap.Config.RGB_565, true));

                        Bitmap bm = result_1.get();
                        imageView.setImageBitmap(bm);

                        // Convert to Base64
                        String base64Image = encodeImageToBase64(bm);

                        // Send API Request
                        sendImageToOpenAI(base64Image);
                    }
                }
            }
        });
    }

    // Convert Bitmap to Base64
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    // Send image to OpenAI API
    private void sendImageToOpenAI(String base64Image) {
        executorService.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("model", "gpt-4o");

                JSONArray inputArray = new JSONArray();
                JSONObject textObject = new JSONObject();
                textObject.put("type", "input_text");
                textObject.put("text", "What's in this image?");
                inputArray.put(textObject);

                JSONObject imageObject = new JSONObject();
                imageObject.put("type", "input_image");
                imageObject.put("image_url", "data:image/jpeg;base64," + base64Image);
                inputArray.put(imageObject);

                jsonBody.put("input", inputArray);

                RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/responses") // Correct the endpoint if needed
                        .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseString);
                    String outputText = jsonResponse.getString("output_text");

                    runOnUiThread(() -> textView.setText(outputText));
                } else {
                    runOnUiThread(() -> textView.setText("Error: " + response.code()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> textView.setText("Failed to connect to API."));
            }
        });
    }
}
