package com.example.Activities;

import static com.example.utils.Constants.AC_NAME;
import static com.example.utils.Constants.ADDRESS;
import static com.example.utils.Constants.PHONE;
import static com.example.utils.Constants.PROFILE;
import static com.example.utils.Constants.REQUEST_PATH;
import static com.example.utils.Constants.USER_FB;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.Models.User;
import com.example.R;
import com.example.utils.Constants;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.databinding.ActivityEditProfileScreenBinding;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditProfileScreen extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "EditProfileScreen";
ActivityEditProfileScreenBinding binding;
    CustomProgressDialogue customProgressDialogue;
    FirebaseFirestore database;
    PrefManager prefManager;
    ActivityResultLauncher<Intent> pickGalleyIntent;
    Uri selectedImageUri = null;
    User userData;
    String name, address, phone, profile;
    StorageReference storageRef;
    byte[] compressedImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
    }

    void init() {
        galleryIntent();
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        prefManager = new PrefManager(this);
        storageRef = FirebaseStorage.getInstance().getReference();
        binding.back.setOnClickListener(this);
        binding.image.setOnClickListener(this);
        binding.update.setOnClickListener(this);
        getIntentData();
    }

    void getIntentData() {
        try {
            userData = (User) getIntent().getSerializableExtra(USER_FB);
            if (userData != null) {
                setData(userData);
            } else fetchUserData();
        } catch (Exception e) {
            //Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void fetchUserData() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(USER_FB).document(prefManager.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userData = task.getResult().toObject(User.class);
                    setData(userData);
                } else {
                    Toast.makeText(EditProfileScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

    boolean isValid() {
        getET();
        if (name.isEmpty()) {
            binding.nameEt.setError(getString(R.string.fill_this));
            return false;
        }
        if (address.isEmpty()) {
            binding.addressEt.setError(getString(R.string.fill_this));
            return false;
        }
        if (phone.isEmpty()) {
            binding.phoneEt.setError(getString(R.string.fill_this));
            return false;
        }
        return true;
    }

    void getET() {
        name = binding.nameEt.getText().toString().trim();
        address = binding.addressEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
    }

    void setData(User user) {
        binding.nameEt.setText(user.getName());
        binding.addressEt.setText(user.getAddress());
        binding.phoneEt.setText(user.getPhone());
        if (user.getProfile() != null || !user.getProfile().isEmpty()) {
            Glide.with(this).load(user.getProfile()).into(binding.profileImg);
            profile = user.getProfile();
        } else profile = "";
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.image) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProfileScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PERMISSION_CODE);
                } else launchIntent();
            } else {
                Log.d(TAG, "granted but launch: ");
                launchIntent();
            }
        } else if (id == R.id.update) {
            if (isValid()) {
                updateData();
            }
        }
    }

    void updateData() {
        customProgressDialogue.showCustomDialog(this);
        if (selectedImageUri != null) {
            addImage();
        } else {
            // update without image
            addToDb(profile);
        }
    }

    void addToDb(String profile) {
        database.collection(USER_FB).document(prefManager.getUid())
                .update(AC_NAME, name,
                        PROFILE, profile, ADDRESS, address, PHONE, phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileScreen.this, "updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(EditProfileScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        customProgressDialogue.hideCustomDialog();
                    }
                });
    }

    void launchIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickGalleyIntent.launch(intent);
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
                                    compressedImg = compressedBitmap(bitmap); // compress
                                    binding.profileImg.setImageBitmap(bitmap); // original
                                    // Log.d(TAG, "compressed: "+compressedImg);
                                }
                            } catch (Exception exception) {
                                Log.d("TAG", "" + exception.getLocalizedMessage());
                            }
                        }
                    }
                });
    }

    void addImage() {
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
                                profile = uri.toString();
                                addToDb(profile);
                            }
                        });
                    }
                }
                Log.d(TAG, "onSuccess: " + profile);
                customProgressDialogue.hideCustomDialog();
                // Handle successful upload
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failed upload
                customProgressDialogue.hideCustomDialog();
                Toast.makeText(EditProfileScreen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } // addImage end

    public byte[] compressedBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos); // 80 is the quality percentage
        return baos.toByteArray();
    }

}