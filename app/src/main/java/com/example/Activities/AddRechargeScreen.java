package com.example.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Fragments.BottomRechargeFragment;
import com.example.R;
import com.example.databinding.ActivityAddRechargeScreenBinding;

public class AddRechargeScreen extends AppCompatActivity implements View.OnClickListener, BottomRechargeFragment.BottomSheetListener {
    private static final String TAG = "AddRechargeScreen";
ActivityAddRechargeScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRechargeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(this);
        binding.selectCompany.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.selectCompany) {// open bottom sheet
            openBottomSheet();
        }
    }

    @Override
    public void onClicked(String company) {
        binding.selectedCompany.setText(company);
    }

    void openBottomSheet() {
        BottomRechargeFragment bottomRechargeFragment = BottomRechargeFragment.newInstance();
        bottomRechargeFragment.show(getSupportFragmentManager(), "bottom");
    }
}