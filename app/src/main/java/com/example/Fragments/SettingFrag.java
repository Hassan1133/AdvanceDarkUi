package com.example.Fragments;

import static com.example.utils.Constants.AC_NAME;
import static com.example.utils.Constants.SETTING_ADMIN_FB;
import static com.example.utils.Constants.URL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.Activities.AboutusScreen;
import com.example.Activities.LoginActivity;
import com.example.Activities.NotificationsScreen;
import com.example.Activities.ProfileScreen;
import com.example.Activities.WebViewSec;
import com.example.Models.SettingsModel;
import com.example.R;
import com.example.databinding.FragmentSettingBinding;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingFrag extends Fragment implements View.OnClickListener {
    FragmentSettingBinding binding;
    PrefManager prefManager;
    CustomProgressDialogue customProgressDialogue;
    FirebaseFirestore database;
    SettingsModel settingsModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(getContext());
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        fetchDetails();
        binding.profile.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.logout.setOnClickListener(this);
        binding.privacy.setOnClickListener(this);
        binding.preference.setOnClickListener(this);
        binding.security.setOnClickListener(this);
        binding.about.setOnClickListener(this);
        binding.notification.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            getActivity().onBackPressed();
        } else if (id == R.id.profile) {
            startActivity(new Intent(getContext(), ProfileScreen.class));
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.privacy) {
            if (settingsModel != null)
                goNext(settingsModel.getPrivacy(), getResources().getString(R.string.privacy_policy));
        } else if (id == R.id.preference) {
            Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.security) {
            Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.about) {
            Intent i = new Intent(getContext(), AboutusScreen.class);
            startActivity(i);
        } else if (id == R.id.notification) {
            startActivity(new Intent(getContext(), NotificationsScreen.class));
        }
    }

    void logout() {
//        database.collection(USER_FB).document(prefManager.getUid()).update(LOGIN_FB, false).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
        prefManager.clearAll();
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
//                } else {
//                    Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    void goNext(String url, String name) {
        Intent i = new Intent(getContext(), WebViewSec.class);
        i.putExtra(URL, url);
        i.putExtra(AC_NAME, name);
        startActivity(i);
    }

    void fetchDetails() {
        customProgressDialogue.showCustomDialog(getContext());
        database.collection(SETTING_ADMIN_FB).document(SETTING_ADMIN_FB).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    customProgressDialogue.hideCustomDialog();
                    return;
                }
                assert value != null;
                if (value.exists()) {
                    settingsModel = value.toObject(SettingsModel.class);
                }
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

    public String firstTwo(String str) {
        return str.length() < 1 ? str : str.substring(0, 1);
    }

}