package com.example.Activities;

import static com.example.utils.Constants.AC_NAME;
import static com.example.utils.Constants.URL;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.databinding.ActivityWebViewSecBinding;

public class WebViewSec extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "WebViewSec";
    private ActivityWebViewSecBinding binding;
    private String url = "";
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewSecBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        binding.back.setOnClickListener(this);
        binding.webView.setWebViewClient(new WebViewClient());
        fetData();
    }

    private void fetData() {
        try {
            url = getIntent().getStringExtra(URL);
            name = getIntent().getStringExtra(AC_NAME);
            setData();
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            onBackPressed();
        }
    }

    private void setData() {
        binding.text.setText(name);
        binding.webView.loadUrl(url);
    }


}