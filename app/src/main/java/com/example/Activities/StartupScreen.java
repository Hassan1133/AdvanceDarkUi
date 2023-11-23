package com.example.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.databinding.ActivityStartupScreenBinding;
import com.google.firebase.auth.FirebaseAuth;

public class StartupScreen extends AppCompatActivity implements View.OnClickListener {
    ActivityStartupScreenBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartupScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        binding.signin.setOnClickListener(this);
        binding.signup.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.signin) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.signup) {
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }
}
