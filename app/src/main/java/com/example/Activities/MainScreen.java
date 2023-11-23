package com.example.Activities;

import static com.example.utils.Constants.SETTING_ADMIN_FB;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.BuildConfig;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.example.Models.SettingsModel;
import com.example.R;
import com.example.utils.Constants;
import com.example.utils.PrefManager;
import com.example.databinding.ActivityMainScreenBinding;

import java.util.Objects;

public class MainScreen extends AppCompatActivity {
ActivityMainScreenBinding binding;
    Handler handler = new Handler();
    PrefManager prefManager;
    FirebaseFirestore database;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefManager = new PrefManager(this);
        database = FirebaseFirestore.getInstance();
        //fetchInfo();
        goDashboard();
    }
    private void checkAppVersion(String version) {
        try {
            // PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (version.equals(BuildConfig.VERSION_NAME)) {
                goDashboard();
            } else {
                showDialog();
            }
        } catch (Exception e) {

        }
    }

    void fetchInfo() {
        database.collection(SETTING_ADMIN_FB).document(SETTING_ADMIN_FB).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                assert value != null;
                if (value.exists()) {
                    checkAppVersion(Objects.requireNonNull(value.toObject(SettingsModel.class)).getVersion());
                    goDashboard();
                } else goDashboard();
            }
        });
    }

    void goDashboard() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!prefManager.getUid().isEmpty())
                    startActivity(new Intent(MainScreen.this, MainActivity.class));
                else startActivity(new Intent(MainScreen.this, LoginActivity.class));
                finish();
            }
        }, Constants.SPLASH_TIME);
    }

    private void goToPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = LayoutInflater.from(this).inflate(R.layout.layout_update, null);
        builder.setView(customLayout);
        TextView btn_yes = customLayout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayStore();
            }
        });
        dialog = builder.create();
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}