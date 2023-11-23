package com.example.Activities;

import static com.example.utils.Constants.IMAGE;
import static com.example.utils.Constants.UNFREEZE_REQUEST_FB;
import static com.example.utils.Constants.USER_FB;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.Models.SingleData;
import com.example.Models.User;
import com.example.R;
import com.example.databinding.ActivityProfileScreenBinding;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileScreen";
    private ActivityProfileScreenBinding binding;
    private PrefManager prefManager;
    private FirebaseFirestore database;
    private CustomProgressDialogue customProgressDialogue;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        fetchUserDetails();
    }

    void init() {
        prefManager = new PrefManager(this);
        binding.back.setOnClickListener(this);
        binding.copy.setOnClickListener(this);
        binding.unfreeze.setOnClickListener(this);
        binding.editProfile.setOnClickListener(this);
        binding.profileImg.setOnClickListener(this);
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            onBackPressed();
        } else if (id == R.id.copy) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied", prefManager.getUid());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.unfreeze) {
            requestUnfreeze();
        } else if (id == R.id.profileImg) {
            try {
                if (user.getProfile() != null || !user.getProfile().isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), ShowPictureScreen.class).putExtra(IMAGE, user.getProfile()));
                }
            } catch (Exception e) {
            }
        } else if (id == R.id.editProfile) {
            startActivity(new Intent(ProfileScreen.this, EditProfileScreen.class).putExtra(USER_FB, user));
        }
    }

    void setDetails(User u) {
        if (u.isFreeze()) {
            freeze();
        } else normal();
        binding.name.setText(u.getName());
        binding.refid.setText(u.getUid());
        binding.phone.setText(u.getPhone());
        binding.email.setText(u.getEmail());
        binding.address.setText(u.getAddress());
        if (u.getProfile() != null || !u.getProfile().isEmpty()) {
            Glide.with(this).load(u.getProfile()).into(binding.profileImg);
        }


    }

    void fetchUserDetails() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(USER_FB).document(prefManager.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    customProgressDialogue.hideCustomDialog();
                    Toast.makeText(ProfileScreen.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value.exists()) {
                    try {
                        user = value.toObject(User.class);
                        prefManager.setUserDetails(user);
                        setDetails(user);
                    } catch (Exception e) {

                    }
                }
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

    void freeze() {
        binding.status.setText("Freeze");
        binding.status.setTextColor(getResources().getColor(R.color.red));
        binding.unfreeze.setVisibility(View.VISIBLE);
    }

    void normal() {
        binding.status.setText("Active");
        binding.status.setTextColor(getResources().getColor(R.color.green));
        binding.unfreeze.setVisibility(View.GONE);
    }

    void requestUnfreeze() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(UNFREEZE_REQUEST_FB).document(user.getUid()).set(new SingleData(user.getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileScreen.this, "Received request.", Toast.LENGTH_SHORT).show();
                    binding.unfreeze.setEnabled(false);
                } else
                    Toast.makeText(ProfileScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                customProgressDialogue.hideCustomDialog();
            }
        });
    }


}