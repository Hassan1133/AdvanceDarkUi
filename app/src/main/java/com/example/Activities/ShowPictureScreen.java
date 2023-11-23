package com.example.Activities;

import static com.example.utils.Constants.IMAGE;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.R;
import com.example.databinding.ActivityShowPictureScreenBinding;

public class ShowPictureScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ShowPictureScreen";
    ActivityShowPictureScreenBinding binding;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowPictureScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setImage();
    }

    void setImage() {
        imageUrl = getIntent().getStringExtra(IMAGE);
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.image);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
    }
}