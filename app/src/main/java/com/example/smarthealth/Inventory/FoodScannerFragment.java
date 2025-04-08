package com.example.smarthealth.Inventory;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smarthealth.R;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FoodScannerFragment extends DialogFragment {

    private ImageView foodImage;
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


                Uri food = Uri.parse(imageUri);
                byte[] foodByte = uriToByteArray(food);

            }
        }

        MaterialButton confirmButton = popupView.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(foodImage.getDrawable() != null){
                    // Convert drawable to byte array
                    byte[] foodByteArray = drawableToByteArray(foodImage.getDrawable());
                    // Pass to chatbot
                    // Update database
                }

                dismiss();
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


}
