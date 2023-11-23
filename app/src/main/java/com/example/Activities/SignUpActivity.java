package com.example.Activities;

import static com.example.utils.Constants.AC_NAME;
import static com.example.utils.Constants.BALANCE_FB;
import static com.example.utils.Constants.SETTING_ADMIN_FB;
import static com.example.utils.Constants.TOTAL_WITHDRAW_FB;
import static com.example.utils.Constants.URL;
import static com.example.utils.Constants.USER_FB;
import static com.example.utils.Constants.USER_TYPE;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.databinding.ActivitySignUpBinding;
import com.example.Models.PaymentModel;
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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySignUpBinding binding;
    private String text, nameTxt, emailTxt, passwordTxt, refIdTxt, addressTxt, phoneTxt;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignupScreen";
    private CustomProgressDialogue customProgressDialogue;
    private FirebaseFirestore database;

    private PrefManager prefManager;
    private SettingsModel settingsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        init();
        setContentView(binding.getRoot());
    }

    private void init() {
        binding.loginNow.setOnClickListener(this);
        binding.signUpBtn.setOnClickListener(this);
        customProgressDialogue = new CustomProgressDialogue();
        mAuth = FirebaseAuth.getInstance();
        prefManager = new PrefManager(this);
        database = FirebaseFirestore.getInstance();
        text = binding.text.getText().toString();

        fetchDetails();
        getLink();
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
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

        spannableString.setSpan(clickableSpan1, 8, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 34, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.text.setText(spannableString);
        binding.text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginNow:
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.signUpBtn:
                if (isValid()) {
                    customProgressDialogue.showCustomDialog(this);
                    // todo
                    if (refIdTxt.isEmpty()) {
                        signUp();
                    } else {
                        checkRefIdValid(refIdTxt);
                    }
                }
                break;

        }
    }

    private void getEditTextData() {
        emailTxt = binding.email.getText().toString().trim();
        nameTxt = binding.name.getText().toString().trim();
        // todo 4
        refIdTxt = binding.referralId.getText().toString().trim();
        addressTxt = binding.address.getText().toString().trim();
        phoneTxt = binding.phone.getText().toString().trim();
        passwordTxt = binding.password.getText().toString().trim();
    }

    void checkRefIdValid(String checkId) {
        database.collection(USER_FB).document(checkId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                signUp();
                            } else {
                                // todo 3
                                binding.referralId.setError("Ref id is not valid");
                                customProgressDialogue.hideCustomDialog();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            customProgressDialogue.hideCustomDialog();
                        }
                    }
                });


    }

    private boolean isValid() {
        getEditTextData();

        boolean valid = true;

        if (nameTxt.isEmpty()) {
            binding.name.setError(getResources().getString(R.string.fill_this));
            valid = false;
        }
        if (emailTxt.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            binding.email.setError(getResources().getString(R.string.valid_email));
            valid = false;
        }
        // todo 2
//        if (refId.isEmpty()) {
//            binding.refEt.setError(getResources().getString(R.string.fill_this));
//            return false;
//        }
        if (addressTxt.isEmpty()) {
            binding.address.setError(getResources().getString(R.string.fill_this));
            valid = false;
        }
        if (phoneTxt.isEmpty()) {
            binding.phone.setError(getResources().getString(R.string.fill_this));
            valid = false;
        }
        if (passwordTxt.length() < 6) {
            binding.password.setError(getResources().getString(R.string.valid_pass));
            valid = false;
        }
        if (!binding.acceptTC.isChecked()) {
            Toast.makeText(this, "" + getResources().getString(R.string.accept), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }


    private void signUp() {
        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // todo
                balanceUpload(mAuth.getUid());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: register error:  " + e.getMessage());
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

    private void balanceUpload(String uid) {
        database.collection(BALANCE_FB).document(uid).set(new PaymentModel(0.0)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                allPoints(uid);
            }
        });
    }

    private void allPoints(String uid) {
        //   database.collection(TOTAL_POINTS_FB).document(uid).set(new AllPoints(0.0, 0.0, uid));
        database.collection(TOTAL_WITHDRAW_FB).document(uid).set(new PaymentModel(0.0)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                addToDB(uid);
            }
        });
    }

    private void addToDB(String UID) {
        User user = new User(nameTxt, emailTxt, addressTxt, refIdTxt, phoneTxt, UID, USER_TYPE, false, false, false);
        try {
            prefManager.setUserDetails(user);
        } catch (Exception ignored) {
        }

        database.collection(USER_FB).document(UID).set(user).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        customProgressDialogue.hideCustomDialog();
                        finish();
                        Log.d(TAG, "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialogue.hideCustomDialog();
                        Log.d(TAG, "onFailure: ");
                    }
                });
    }

    void fetchDetails() {
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

    void getLink() {
        if (prefManager.getUid().isEmpty()) {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            String referLink;
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                                referLink = deepLink.toString();
                                Log.d(TAG, "onSuccess: " + referLink);
                                try {
                                    // https://exampleurl.com/refid=exampleuid
                                    String[] parts = referLink.split("=");
                                    refIdTxt = parts[1];
                                    binding.referralId.setText(refIdTxt);
                                } catch (Exception e) {
                                    Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: " + e.getMessage());
                                }
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}