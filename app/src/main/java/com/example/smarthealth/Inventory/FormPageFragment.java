package com.example.smarthealth.Inventory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.smarthealth.R;

import java.io.ByteArrayOutputStream;

public class FormPageFragment extends DialogFragment {
    private Uri camUri;
    private ImageView popupImageView;
    private Button openCameraButton, uploadImageButton;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (popupImageView != null) {
                        // Update the popup's ImageView
                        popupImageView.setImageURI(imageUri);
                        uploadImageButton.setVisibility(View.GONE);
                        openCameraButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(requireContext(), "Popup not open!", Toast.LENGTH_SHORT).show();}
                } else {
                    Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();}
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (camUri != null) {
                    popupImageView.setImageURI(camUri); // Set image from saved URI
                    uploadImageButton.setVisibility(View.GONE);
                    openCameraButton.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(requireContext(), "Popup not open!", Toast.LENGTH_SHORT).show();}
            }
            else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();}
        }
    });

    @Override
    public void onStart()
    {
        super.onStart();
        if (getDialog() != null)
        {
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
        View popupView = inflater.inflate(R.layout.form_fillup, null);
        popupImageView = popupView.findViewById(R.id.image);
        openCameraButton = popupView.findViewById(R.id.open_camera);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCamera();
            }
        });

        uploadImageButton = popupView.findViewById(R.id.upload_image);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        Button exitButton = popupView.findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }}
        );

        Spinner category = popupView.findViewById(R.id.category);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) view;
                // Change the text color of the selected item
                selectedText.setTextColor(Color.BLACK);
                // Change the text size of the selected item (e.g., 20sp)
                selectedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button confirmButton = popupView.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO retrieve information from the popup window
                EditText nameView = popupView.findViewById(R.id.formMediName);
                EditText amountView = popupView.findViewById(R.id.formMediAmount);
                EditText descView = popupView.findViewById(R.id.formMediDesc);
                ImageView imageView = popupView.findViewById(R.id.image);
                String mediType = category.getSelectedItem().toString();
                EditText mediInfo = popupView.findViewById(R.id.formMediInfo);

                String mediName = nameView.getText().toString().trim();
                String mediDesc = descView.getText().toString().trim();
                String amount = amountView.getText().toString().trim();
                Drawable image = imageView.getDrawable();
                String info = mediInfo.getText().toString().trim();

                // Pass corresponding parameters
                if (mediName.isEmpty() || mediDesc.isEmpty() || amount.isEmpty() || imageView.getDrawable() == null || info.isEmpty()) {
                    if (mediName.isEmpty()) {
                        nameView.setError("Required");
                    }
                    if (mediDesc.isEmpty()) {
                        descView.setError("Required");
                    }
                    if (amount.isEmpty()) {
                        amountView.setError("Required");
                    }
                    if (info.isEmpty()) {
                        mediInfo.setError("Required");
                    }
                    if (imageView.getDrawable() == null) {
                        Toast.makeText(requireContext(), "Please select an image!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    int mediAmount = Integer.parseInt(amount);
                    byte[] imageData = drawableToByteArray(image);

                    Bundle result = new Bundle();
                    result.putString("Name", mediName);
                    result.putString("Desc", mediDesc);
                    result.putString("Info", info);
                    result.putInt("Amount", mediAmount);
                    result.putString("Type", mediType);
                    result.putByteArray("Image", imageData);

                    getParentFragmentManager().setFragmentResult("medicineData", result);
                    dismiss();
                }
            };
        });

                return popupView;

    }

    private void pickCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Medicine");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera");
        Uri camUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,camUri);
        cameraLauncher.launch(cameraIntent);
    }
    private void pickImage() {
        // Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
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
