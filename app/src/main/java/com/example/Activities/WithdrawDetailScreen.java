package com.example.Activities;

import static com.example.utils.Constants.APPROVED;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.IMAGE;
import static com.example.utils.Constants.PENDING;
import static com.example.utils.Constants.REJECTED;
import static com.example.utils.Constants.USER_FB;
import static com.example.utils.Constants.WITHDRAW_FB;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.Models.User;
import com.example.Models.Withdrawal;
import com.example.R;
import com.example.databinding.ActivityWithdrawDetailScreenBinding;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.Utilis;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WithdrawDetailScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "WithdrawDetailScreen";
    ActivityWithdrawDetailScreenBinding binding;
    Withdrawal withdrawal;
    User user;
    CustomProgressDialogue customProgressDialogue;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawDetailScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        getData();
        playAd();
    }

    void init() {
        customProgressDialogue = new CustomProgressDialogue();
        database = FirebaseFirestore.getInstance();
        binding.copy.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.profile.setOnClickListener(this);
    }

    void getData() {
        try {
            withdrawal = (Withdrawal) getIntent().getSerializableExtra(WITHDRAW_FB);
            if (withdrawal != null) {
                setData();
                getUserData(withdrawal.getUserId());
            }
            Log.d(TAG, "getData: " + withdrawal.getTransactionId());
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            onBackPressed();
        } else if (id == R.id.copy) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied", withdrawal.getTransactionId());
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

    @SuppressLint("SetTextI18n")
    void setData() {
        setColor();
        binding.status.setText(withdrawal.getStatus());
        binding.transactionId.setText(withdrawal.getTransactionId());
        binding.date.setText(withdrawal.getDate());
        binding.bankName.setText(withdrawal.getBankName());
        binding.accountNumber.setText(withdrawal.getAccountNumber());
        binding.amount.setText(getString(R.string.pk_currency) + Utilis.round2(withdrawal.getAmount(), BALANCE_RANGE));
    }

    void setColor() {
        switch (withdrawal.getStatus()) {
            case APPROVED:
                binding.status.setTextColor(getResources().getColor(R.color.green));
                break;
            case PENDING:
                binding.status.setTextColor(getResources().getColor(R.color.orange));
                break;
            case REJECTED:
                binding.status.setTextColor(getResources().getColor(R.color.red));
                break;
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