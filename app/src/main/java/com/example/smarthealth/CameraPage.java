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

import android.view.View;

import android.widget.Button;

import android.widget.ImageView;


import java.io.File;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;

import java.io.IOException;

import java.lang.ref.WeakReference;



public class CameraPage extends AppCompatActivity {

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



        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override

            public void onActivityResult(ActivityResult result) {



                Bundle extras = result.getData().getExtras();

                Uri imageUri;

                Bitmap imageBitmap = (Bitmap) extras.get("data");

                WeakReference<Bitmap> result_1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap,

                                imageBitmap.getWidth(), imageBitmap.getHeight(), false).

                        copy(Bitmap.Config.RGB_565, true));



                Bitmap bm = result_1.get();

                imageUri = saveImage(bm, CameraPage.this);

                imageView.setImageURI(imageUri);

            }

        });



    }



    private Uri saveImage(Bitmap image, CameraPage context) {

        File imagefolder = new File(context.getCacheDir(), "images");

        Uri uri = null;

        try{

            imagefolder.mkdirs();

            File file = new File(imagefolder, "captured_image.jpg");

            FileOutputStream stream = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            stream.flush();

            stream.close();

            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.smarthealth.provider", file);

        }



        catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return uri ;

    }



}




