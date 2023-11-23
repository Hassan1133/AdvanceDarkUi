package com.example.Fragments;

import static com.example.utils.Constants.SETTING_ADMIN_FB;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.example.Activities.FAQScreen;
import com.example.Models.SettingsModel;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.databinding.FragmentHelpBinding;

public class HelpFrag extends Fragment implements View.OnClickListener {
    FragmentHelpBinding binding;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    SettingsModel data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHelpBinding.inflate(getLayoutInflater(), container, false);
        customProgressDialogue = new CustomProgressDialogue();
        database = FirebaseFirestore.getInstance();
        binding.back.setOnClickListener(this);
        binding.faq.setOnClickListener(this);
        binding.email.setOnClickListener(this);
        binding.chat.setOnClickListener(this);
        fetchInfo();
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            getActivity().onBackPressed();
        } else if (id == R.id.faq) {
            startActivity(new Intent(getContext(), FAQScreen.class));
        } else if (id == R.id.email) {
            if (data != null) composeEmail(data.getEmail());
        } else if (id == R.id.chat) {
            if (data != null) sendWhatsapp(data.getWhatsapp());
        }
    }

    void sendWhatsapp(String phone) {
        try {
            phone = phone.replace("+", "").replace(" ", "");
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + phone + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "it may be you dont have whatsapp", Toast.LENGTH_LONG).show();
        }
    }

    public void composeEmail(String address) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, "help");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    void fetchInfo() {
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
                    data = value.toObject(SettingsModel.class);
                }
                customProgressDialogue.hideCustomDialog();
            }
        });
    }
}