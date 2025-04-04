package com.example.smarthealth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class CameraPage3 extends AppCompatActivity {

    private static final int REQUEST_CODE = 22;

    Button btnpicture;
    ImageView imageView;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_page);

        btnpicture = findViewById(R.id.btncamera_id);
        imageView = findViewById(R.id.image);

        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(cameraIntent);
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");

                            if (imageBitmap != null) {
                                WeakReference<Bitmap> result_1 = new WeakReference<>(
                                        Bitmap.createScaledBitmap(imageBitmap,
                                                        imageBitmap.getWidth(), imageBitmap.getHeight(), false)
                                                .copy(Bitmap.Config.RGB_565, true)
                                );

                                Bitmap bm = result_1.get();
                                String imagePath = saveImageToGallery(bm);

                                if (imagePath != null) {
                                    imageView.setImageURI(Uri.parse(imagePath));
                                    Toast.makeText(CameraPage3.this, "Image saved: " + imagePath, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CameraPage3.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        );
    }

    private String saveImageToGallery(Bitmap bitmap) {
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        OutputStream fos;
        Uri imageUri;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SmartHealth");

            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (imageUri != null) {
                try {
                    fos = resolver.openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    return imageUri.toString();  // Return file path as URI string
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SmartHealth");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                return file.getAbsolutePath();  // Return file path
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}

