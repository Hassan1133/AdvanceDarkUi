package com.example.Activities;

import static com.example.utils.Constants.AMOUNT_FB;
import static com.example.utils.Constants.PLANS_FB;
import static com.example.utils.Constants.SETTING_ADMIN_FB;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.example.Models.PlansModel;
import com.example.Models.SettingsModel;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.databinding.ActivityPlansDetailScreenBinding;

public class PlansDetailScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PlansDetailScreen";
    ActivityPlansDetailScreenBinding binding;
    PlansModel plansModel;
    FirebaseFirestore database;
    PrefManager prefManager;
    CustomProgressDialogue customProgressDialogue;
    int convertedRate = 0;
    SettingsModel settingsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlansDetailScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        fetchInfo();

        playAd();

    }


    void init() {
        binding.back.setOnClickListener(this);
        binding.proceed.setOnClickListener(this);
        binding.copy.setOnClickListener(this);
        database = FirebaseFirestore.getInstance();
        prefManager = new PrefManager(this);
        customProgressDialogue = new CustomProgressDialogue();
        getData();
    }

    void getData() {
        try {
            plansModel = (PlansModel) getIntent().getSerializableExtra(PLANS_FB);
            if (plansModel != null) setData();
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            onBackPressed();
        } else if (id == R.id.proceed) {// todo
            Intent i = new Intent(getApplicationContext(), PlanImageScreen.class);
            i.putExtra(PLANS_FB, plansModel);
            i.putExtra(AMOUNT_FB, convertedRate);
            startActivity(i);
            //activePlan();
        } else if (id == R.id.copy) {
            try {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", settingsModel.getAccountNumber());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    void setData() {
        binding.title.setText(plansModel.getTitle());
        binding.activePrice.setText(getString(R.string.dollar_currency) + plansModel.getPrice());
        binding.description.setText(plansModel.getDescription());
        binding.speed.setText(plansModel.getSpeed() + "X speed");
    }


    void fetchInfo() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(SETTING_ADMIN_FB).document(SETTING_ADMIN_FB).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    customProgressDialogue.hideCustomDialog();
                    return;
                }

                assert value != null;
                if (value.exists()) {
                    settingsModel = value.toObject(SettingsModel.class);
                    binding.bankName.setText(settingsModel.getBankName());
                    binding.accountNumber.setText(settingsModel.getAccountNumber());
                }
                customProgressDialogue.hideCustomDialog();
            }
        });
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