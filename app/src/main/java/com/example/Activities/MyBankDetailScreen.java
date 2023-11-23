package com.example.Activities;

import static com.example.utils.Constants.REF_ACCOUNT;
import static com.example.utils.Constants.USER_FB;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.Models.BankAccountModel;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.databinding.ActivityMyBankDetailScreenBinding;

import java.util.Objects;

public class MyBankDetailScreen extends AppCompatActivity implements View.OnClickListener {
ActivityMyBankDetailScreenBinding binding;
    CustomProgressDialogue customProgressDialogue;
    String bankName, accountNumber, id;
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBankDetailScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
        getData();
    }

    void init() {
        prefManager = new PrefManager(this);
        id = prefManager.getUid();
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(this);
        binding.updateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.back) {
            finish();
        } else if (vId == R.id.updateBtn) {
            if (isValid()) {
                customProgressDialogue.showCustomDialog(MyBankDetailScreen.this);
                addToDB();
            }
        }
    }

    boolean isValid() {
        getEdittext();
        if (bankName.isEmpty()) {
            Snackbar.make(binding.bankName, "Please fill all fields.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (accountNumber.isEmpty()) {
            Snackbar.make(binding.bankName, "Please fill all fields.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    void addToDB() {
        BankAccountModel accountModel = new BankAccountModel(bankName, accountNumber, id);
        FirebaseFirestore.getInstance().collection(USER_FB).document(id).collection(REF_ACCOUNT).document(REF_ACCOUNT).set(accountModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MyBankDetailScreen.this, "Success", Toast.LENGTH_SHORT).show();
                    customProgressDialogue.hideCustomDialog();
                    finish();
                } else {
                    Toast.makeText(MyBankDetailScreen.this, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    customProgressDialogue.hideCustomDialog();
                }
            }
        });
    }

    void getEdittext() {
        bankName = binding.bankName.getText().toString().trim();
        accountNumber = binding.accountNumber.getText().toString().trim();
    }

    void getData() {
        try {
            FirebaseFirestore.getInstance().collection(USER_FB).document(id).collection(REF_ACCOUNT).document(REF_ACCOUNT).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            BankAccountModel model = task.getResult().toObject(BankAccountModel.class);
                            assert model != null;
                            binding.bankName.setText(model.getBankName());
                            binding.accountNumber.setText(model.getAccountNumber());
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }
}