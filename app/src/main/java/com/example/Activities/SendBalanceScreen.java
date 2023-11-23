package com.example.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Adapters.CustomSpinnerAdp;
import com.example.Models.PaymentOptionsModel;
import com.example.R;
import com.example.databinding.ActivitySendBalanceScreenBinding;

import java.util.LinkedList;

public class SendBalanceScreen extends AppCompatActivity implements View.OnClickListener {
    ActivitySendBalanceScreenBinding binding;
    CustomSpinnerAdp customSpinnerAdp;
    String selectedId;
    LinkedList<PaymentOptionsModel> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendBalanceScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.back.setOnClickListener(this);
        setPaymentList();
        binding.selectedItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PaymentOptionsModel b = customSpinnerAdp.getItem(position);
                selectedId = b.getId();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setPaymentList() {
        paymentList = new LinkedList<>();
        paymentList.add(new PaymentOptionsModel("1", "JazzCash", getResources().getDrawable(R.drawable.jazzcash)));
        paymentList.add(new PaymentOptionsModel("2", "Easy Paisa", getResources().getDrawable(R.drawable.jazzcash)));
        paymentList.add(new PaymentOptionsModel("3", "Pay Pro", getResources().getDrawable(R.drawable.jazzcash)));
        customSpinnerAdp = new CustomSpinnerAdp(this, paymentList);
        binding.selectedItem.setAdapter(customSpinnerAdp);
        binding.selectedItem.setThreshold(1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            onBackPressed();
        }
    }
}