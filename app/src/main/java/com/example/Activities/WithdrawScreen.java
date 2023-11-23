package com.example.Activities;

import static com.example.utils.Constants.ACTIVE_PLANS_FB;
import static com.example.utils.Constants.AMOUNT_FB;
import static com.example.utils.Constants.BALANCE_FB;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.MIN_WITHDRAW_RANGE;
import static com.example.utils.Constants.PENDING;
import static com.example.utils.Constants.REF_ACCOUNT;
import static com.example.utils.Constants.SILVER;
import static com.example.utils.Constants.USER_FB;
import static com.example.utils.Constants.WITHDRAW_FB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.Models.BankAccountModel;
import com.example.Models.PaymentModel;
import com.example.Models.PlanPaymentRequest;
import com.example.Models.Withdrawal;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.utils.Utilis;
import com.example.databinding.ActivityWithdrawScreenBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class WithdrawScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "WithdrawScreen";
    ActivityWithdrawScreenBinding binding;
    FirebaseFirestore database;
    String key, tempBalance;
    CustomProgressDialogue customProgressDialogue;
    PrefManager prefManager;
    Double balance = 0.0;
    boolean isSend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        getCurrentBalance(false);

    }

    @SuppressLint("SetTextI18n")
    void init() {
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        prefManager = new PrefManager(this);
        binding.withdraw.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.bankLayout.setOnClickListener(this);
        binding.terms.setText(getString(R.string._1_tax_of_0_6_will_be_deduct_on_withdrawal_n2_minimum_withdrawal_is)+" "+MIN_WITHDRAW_RANGE);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.withdraw) {
            getET();
            if (isValid()) {
                customProgressDialogue.showCustomDialog(this);
                getCurrentBalance(true);
            }
        } else if (id == R.id.back) {
            onBackPressed();
        } else if (id == R.id.bankLayout) {
            startActivity(new Intent(WithdrawScreen.this, MyBankDetailScreen.class));
        }
    }


    void getET() {
        tempBalance = binding.balanceEt.getText().toString().trim();
    }

    boolean isValid() {
        if (tempBalance.isEmpty()) {
            binding.balanceEt.setError(getResources().getString(R.string.enter_amount));
            return false;
        }
        balance = Double.parseDouble(binding.balanceEt.getText().toString());
        if (balance <= 0 || balance < MIN_WITHDRAW_RANGE) {
            binding.balanceEt.setError(getResources().getString(R.string.enter_amount));
            return false;
        }

        return true;
    }

    void addWithdraw(String bankName, String accountNumber) {
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        key = database.collection(WITHDRAW_FB).document().getId();
        database.collection(WITHDRAW_FB).document(prefManager.getUid()).set(new Withdrawal(date, balance, key, prefManager.getUid(), prefManager.getUserName(), PENDING, bankName, accountNumber));
        database.collection(WITHDRAW_FB).document(prefManager.getUid()).collection(WITHDRAW_FB).document(key).
                set(new Withdrawal(date, balance, key, prefManager.getUid(), prefManager.getUserName(), PENDING, bankName, accountNumber))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) updateBalance();
                        else {
                            Toast.makeText(WithdrawScreen.this, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            customProgressDialogue.hideCustomDialog();
                        }
                    }
                });
    }

    void updateBalance() {
        database.collection(BALANCE_FB).document(prefManager.getUid()).update(AMOUNT_FB, FieldValue.increment(-balance)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(WithdrawScreen.this, "Success", Toast.LENGTH_SHORT).show();
                    customProgressDialogue.hideCustomDialog();
                    finish();
                } else
                    Toast.makeText(WithdrawScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

    void getCurrentBalance(boolean isSend)
    {
        database.collection(BALANCE_FB).document(prefManager.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    double currentBalance = Objects.requireNonNull(documentSnapshot.toObject(PaymentModel.class)).getAmount();
                    if (isSend)
                    {
                        if (currentBalance >= balance) {
                            checkActivePlan();
                        } else {
                            customProgressDialogue.hideCustomDialog();
                            Toast.makeText(WithdrawScreen.this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                        }
                    }else binding.balance.setText(Utilis.round2(currentBalance, BALANCE_RANGE)+"");

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customProgressDialogue.hideCustomDialog();
                Toast.makeText(WithdrawScreen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void checkActivePlan() {
        database.collection(ACTIVE_PLANS_FB).document(prefManager.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    PlanPaymentRequest planPaymentRequest = documentSnapshot.toObject(PlanPaymentRequest.class);
                    assert planPaymentRequest != null;
                    if (planPaymentRequest.getAmount() > SILVER) // go next
                    {
                        getData();
                    } else {
                        customProgressDialogue.hideCustomDialog();
                        Toast.makeText(WithdrawScreen.this, "Please upgrade your plan to Gold or more", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customProgressDialogue.hideCustomDialog();
                Toast.makeText(WithdrawScreen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void getData() {
        try {
            FirebaseFirestore.getInstance().collection(USER_FB).document(prefManager.getUid()).collection(REF_ACCOUNT).document(REF_ACCOUNT).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            BankAccountModel model = task.getResult().toObject(BankAccountModel.class);
                            assert model != null;
                            addWithdraw(model.getBankName(), model.getAccountNumber());
                        } else {
                            Toast.makeText(WithdrawScreen.this, "Please update bank details", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(WithdrawScreen.this, MyBankDetailScreen.class));
                            customProgressDialogue.hideCustomDialog();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }
}