package com.example.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.databinding.ActivityInviteScreenBinding;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class InviteScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InviteScreen";
    ActivityInviteScreenBinding binding;
    PrefManager prefManager;
    CustomProgressDialogue customProgressDialogue;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        createLink();
    }

    void init() {
        prefManager = new PrefManager(this);
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        binding.copy.setOnClickListener(this);
    }

    void createLink() {
        customProgressDialogue.showCustomDialog(this);
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://shakehand.page.link/?refid=" + prefManager.getUid()))
                .setDomainUriPrefix("https://shakehand.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildShortDynamicLink().addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        Log.d(TAG, "onSuccess: " + shortDynamicLink.getShortLink());
                        link = shortDynamicLink.getShortLink().toString();
//                        binding.link.setText(link);
                        customProgressDialogue.hideCustomDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InviteScreen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        binding.btns.setVisibility(View.INVISIBLE);
                        customProgressDialogue.hideCustomDialog();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            onBackPressed();
        } else if (id == R.id.share) {
            share();
        } else if (id == R.id.copy) {
            copy();
        }
    }

    void copy() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied", FirebaseAuth.getInstance().getUid());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
    }

    void share() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, link);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }
}