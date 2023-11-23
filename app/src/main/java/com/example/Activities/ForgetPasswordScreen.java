package com.example.Activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.databinding.ActivityForgetPasswordScreenBinding;
import com.example.utils.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordScreen extends AppCompatActivity implements View.OnClickListener {
    private ActivityForgetPasswordScreenBinding binding;
    private FirebaseAuth mAuth;
    private CustomProgressDialogue customProgressDialogue;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(this);
        binding.reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.reset) {
            email = binding.emailEt.getText().toString().trim();
            if (isValid()) {
                sendEmail(email);
            }
        }

    }

    private boolean isValid() {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.setError(getResources().getString(R.string.valid_email));
            return false;
        }
        return true;
    }

    private void sendEmail(String email) {
        customProgressDialogue.showCustomDialog(this);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordScreen.this, "" + getString(R.string.checkmail), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ForgetPasswordScreen.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                customProgressDialogue.hideCustomDialog();
            }
        });
    }


}