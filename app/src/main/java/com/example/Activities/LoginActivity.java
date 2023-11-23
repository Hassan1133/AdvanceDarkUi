package com.example.Activities;

import static com.example.utils.Constants.AC_NAME;
import static com.example.utils.Constants.SETTING_ADMIN_FB;
import static com.example.utils.Constants.URL;
import static com.example.utils.Constants.USER_FB;
import static com.example.utils.Constants.USER_TYPE;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.databinding.ActivityLoginBinding;
import com.example.Models.SettingsModel;
import com.example.Models.User;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;

    private String emailTxt, passwordTxt, text;
    private FirebaseAuth mAuth;
    private CustomProgressDialogue customProgressDialogue;
    private static final String TAG = "LoginActivity";
    private PrefManager prefManager;
    private FirebaseFirestore database;
    private SettingsModel settingsModel;

    public static boolean isAndroidEmulator() {
        String model = Build.MODEL;
        Log.d(TAG, "model=" + model);
        String product = Build.PRODUCT;
        Log.d(TAG, "product=" + product);
        boolean isEmulator = false;
        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
        }
        Log.d(TAG, "isEmulator=" + isEmulator);
        return isEmulator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        init();
        setContentView(binding.getRoot());
    }

    private void init() {
        customProgressDialogue = new CustomProgressDialogue();
        mAuth = FirebaseAuth.getInstance();

        text = binding.text.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        prefManager = new PrefManager(this);
        database = FirebaseFirestore.getInstance();
//        if (isAndroidEmulator()) {
//            showDialogue();
//        }
        fetchDetails();

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (settingsModel != null)
                    goNext(settingsModel.getPrivacy(), getResources().getString(R.string.privacy_policy));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.suffix_txt_clr_yellow));
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (settingsModel != null)
                    goNext(settingsModel.getTerms(), getResources().getString(R.string.terms_service));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.suffix_txt_clr_yellow));
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan1, 21, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 40, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.text.setText(spannableString);
        binding.text.setMovementMethod(LinkMovementMethod.getInstance());
        // click listeners
        binding.forgotPassword.setOnClickListener(this);
        binding.createOne.setOnClickListener(this);
        binding.loginBtn.setOnClickListener(this);
    }

    private void getEditTextData() {
        emailTxt = binding.email.getText().toString().trim();
        passwordTxt = binding.password.getText().toString().trim();
    }

    private boolean isValid() {
        getEditTextData();

        boolean valid = true;

        if (emailTxt.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            binding.email.setError(getResources().getString(R.string.valid_email));
            valid = false;
        }
        if (passwordTxt.isEmpty()) {
            binding.password.setError(getResources().getString(R.string.fill_this));
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createOne:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
                break;
            case R.id.loginBtn:
                if (isValid()) {
                    customProgressDialogue.showCustomDialog(LoginActivity.this);
                    login();
                }
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgetPasswordScreen.class));
                break;
        }
    }

    private void login() {
        mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                getUserData(mAuth.getUid());
                customProgressDialogue.hideCustomDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                customProgressDialogue.hideCustomDialog();
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    void getUserData(String id) {
        database.collection(USER_FB).document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {


                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Toast.makeText(LoginActivity.this, document.getId(), Toast.LENGTH_SHORT).show();

                                // check if the user is really client user
                                User user = document.toObject(User.class);
                                try {// check block

                                    if (user.isBlock()) {
                                        Toast.makeText(LoginActivity.this, "Your account is blocked", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
//                                    else if (user.isLogin())
//                                    {
//                                        Toast.makeText(LoginActivity.this, "You cannot login on more than one device", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                if (user.getUserType().equals(USER_TYPE)) {

                                    Toast.makeText(LoginActivity.this, "e.getMessage()", Toast.LENGTH_SHORT).show();
                                    // set preference
                                    try {
                                        prefManager.setUserDetails(user);
                                    } catch (Exception e) {
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void fetchDetails() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(SETTING_ADMIN_FB).document(SETTING_ADMIN_FB).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    void goNext(String url, String name) {
        Intent i = new Intent(getApplicationContext(), WebViewSec.class);
        i.putExtra(URL, url);
        i.putExtra(AC_NAME, name);
        startActivity(i);
    }

    void showDialogue() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = LayoutInflater.from(this).inflate(R.layout.emulator_detected_layout, null);
        builder.setView(customLayout);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}