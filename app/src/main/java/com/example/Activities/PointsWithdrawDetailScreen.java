package com.example.Activities;

import static com.example.utils.Constants.APPROVED;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.IMAGE;
import static com.example.utils.Constants.PENDING;
import static com.example.utils.Constants.POINTS_WITHDRAW_FB;
import static com.example.utils.Constants.REJECTED;
import static com.example.utils.Constants.USER_FB;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.Models.PointsWithdrawModel;
import com.example.Models.User;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.Utilis;
import com.example.databinding.ActivityPointsWithdrawDetailScreenBinding;

public class PointsWithdrawDetailScreen extends AppCompatActivity implements View.OnClickListener {
ActivityPointsWithdrawDetailScreenBinding binding;
    User user;
    CustomProgressDialogue customProgressDialogue;
    FirebaseFirestore database;
    PointsWithdrawModel pointsWithdrawModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointsWithdrawDetailScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
        playAd();
        binding.back.setOnClickListener(this);
        binding.profile.setOnClickListener(this);
        binding.copy.setOnClickListener(this);
    }

    void init() {
        customProgressDialogue = new CustomProgressDialogue();
        database = FirebaseFirestore.getInstance();
        try {
            pointsWithdrawModel = (PointsWithdrawModel) getIntent().getSerializableExtra(POINTS_WITHDRAW_FB);
            if (pointsWithdrawModel != null) {
                setData();
                getUserData(pointsWithdrawModel.getUid());
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    void setData() {
        setColor();
        binding.status.setText(pointsWithdrawModel.getStatus());
        binding.transactionId.setText(pointsWithdrawModel.getTransactionId());
        binding.date.setText(pointsWithdrawModel.getDate());
        binding.bankName.setText(pointsWithdrawModel.getBankName());
        binding.accountNumber.setText(pointsWithdrawModel.getAccountNumber());
        binding.points.setText(pointsWithdrawModel.getPoints() + "");
        binding.amount.setText(getString(R.string.pk_currency) + Utilis.round2(pointsWithdrawModel.getAmount(), BALANCE_RANGE));
    }

    void setColor() {
        if (APPROVED.equals(pointsWithdrawModel.getStatus())) {
            binding.status.setTextColor(getResources().getColor(R.color.green));
        } else if (PENDING.equals(pointsWithdrawModel.getStatus())) {
            binding.status.setTextColor(getResources().getColor(R.color.orange));
        } else if (REJECTED.equals(pointsWithdrawModel.getStatus())) {
            binding.status.setTextColor(getResources().getColor(R.color.red));
        }
    }

    void getUserData(String uid) {
        customProgressDialogue.showCustomDialog(this);
        database.collection(USER_FB).document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(User.class);
                            setUserDetails(user);
                        }
                        customProgressDialogue.hideCustomDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialogue.hideCustomDialog();
                    }
                });
    }

    void setUserDetails(User user) {
        binding.name.setText(user.getName());
        binding.email.setText(user.getEmail());
        try {
            if (user.getProfile() != null || !user.getProfile().isEmpty()) {
                Glide.with(this).load(user.getProfile()).into(binding.profile);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            onBackPressed();
        } else if (id == R.id.copy) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied", pointsWithdrawModel.getTransactionId());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.profile) {
            try {
                if (user.getProfile() != null || !user.getProfile().isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), ShowPictureScreen.class).putExtra(IMAGE, user.getProfile()));
                }
            } catch (Exception e) {
            }
        }
    }

    private void playAd() {
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
    }
}