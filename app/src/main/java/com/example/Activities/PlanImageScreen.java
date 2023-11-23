package com.example.Activities;

import static com.example.utils.Constants.AMOUNT_FB;
import static com.example.utils.Constants.IMAGE_PERMISSION;
import static com.example.utils.Constants.PENDING;
import static com.example.utils.Constants.PLANS_FB;
import static com.example.utils.Constants.PLANS_REQUEST_FB;
import static com.example.utils.Constants.REQUEST_PATH;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.Models.PlanPaymentRequest;
import com.example.Models.PlansModel;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.databinding.ActivityPlanImageScreenBinding;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class PlanImageScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PlanImageScreen";
    ActivityPlanImageScreenBinding binding;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    ActivityResultLauncher<Intent> pickGalleyIntent;
    Uri selectedImageUri = null;
    StorageReference storageRef;
    String image;
    PlansModel plansModel;
    int amount;
    PrefManager prefManager;
    byte[] compressedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlanImageScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    void init() {
        galleryIntent();
        database = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        prefManager = new PrefManager(this);
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(this);
        binding.uploadImg.setOnClickListener(this);
        binding.proceed.setOnClickListener(this);
        getData();
    }

    void getData() {
        try {
            plansModel = (PlansModel) getIntent().getSerializableExtra(PLANS_FB);
            amount = getIntent().getIntExtra(AMOUNT_FB, 0);
            amount = Integer.parseInt(plansModel.getPrice());
            Log.d(TAG, "getData: " + amount);
            Log.d(TAG, "getData: " + plansModel.getPrice());
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.uploadImg) {
            isPermissionAllow();
        } else if (id == R.id.proceed) {
            if (selectedImageUri != null) addImage();
            else Toast.makeText(this, "Upload image", Toast.LENGTH_SHORT).show();
        }
    }

    void isPermissionAllow() {
        int permissionReadExternalStorage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
        else
            permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, IMAGE_PERMISSION);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PERMISSION);
        } else launchIntent();
    }

    void galleryIntent() {
        pickGalleyIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            try {
                                if (result.getData() != null) {
                                    selectedImageUri = result.getData().getData();
                                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                                    Log.d(TAG, "onActivityResult: " + selectedImageUri);
                                    compressedImg = compressedBitmap(bitmap); // compress
                                    //binding.image.setImageURI(selectedImageUri);
                                    binding.image.setImageBitmap(bitmap);
                                }
                            } catch (Exception exception) {
                                Log.d("TAG", "" + exception.getLocalizedMessage());
                            }
                        }
                    }
                });
    }

    void addPlan(String image) {
        String key = database.collection(PLANS_REQUEST_FB).document().getId();

        PlanPaymentRequest planPaymentRequest = new PlanPaymentRequest(plansModel.getTitle(), plansModel.getDescription()
                , plansModel.getSpeed(), plansModel.getId(), plansModel.getPrice(), prefManager.getUid(), image, PENDING, amount, key);

        database.collection(PLANS_REQUEST_FB).document(key).set(planPaymentRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PlanImageScreen.this, "Plan request send.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(PlanImageScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            customProgressDialogue.hideCustomDialog();
                        }
                    }
                });
    }

    void launchIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickGalleyIntent.launch(intent);
    }

    void addImage() {
        customProgressDialogue.showCustomDialog(this);
        StorageReference reference = storageRef.child(REQUEST_PATH + UUID.randomUUID());

        UploadTask uploadTask = reference.putBytes(compressedImg);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                image = uri.toString();
                                addPlan(image);
                            }
                        });
                    }
                }
                customProgressDialogue.hideCustomDialog();
                // Handle successful upload
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failed upload
                customProgressDialogue.hideCustomDialog();
                Toast.makeText(PlanImageScreen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } // addImage end

    public byte[] compressedBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos); // 80 is the quality percentage
        return baos.toByteArray();
    }


}