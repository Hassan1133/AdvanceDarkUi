package com.example.Activities;

import static com.example.utils.Constants.BALANCE_FB;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.PENDING;
import static com.example.utils.Constants.POINTS_FB;
import static com.example.utils.Constants.POINTS_WITHDRAW_FB;
import static com.example.utils.Constants.REF_ACCOUNT;
import static com.example.utils.Constants.TEAM_POINTS;
import static com.example.utils.Constants.TOTAL_POINTS_FB;
import static com.example.utils.Constants.TOTAL_WITHDRAW_FB;
import static com.example.utils.Constants.USER_FB;
import static com.example.utils.Constants.USER_POINTS;
import static com.example.utils.Constants.ZUCOBIT_FB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Models.BankAccountModel;
import com.example.Models.PaymentModel;
import com.example.Models.PlanPaymentRequest;
import com.example.Models.PointsWithdrawModel;
import com.example.Models.ShakehandBitRate;
import com.example.Models.User;
import com.example.R;
import com.example.databinding.ActivityPointsWithdrawScreenBinding;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.utils.Utilis;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class PointsWithdrawScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PointsWithdrawScreen";
    ActivityPointsWithdrawScreenBinding binding;
    CustomProgressDialogue customProgressDialogue;
    PlanPaymentRequest activePlan;
    PrefManager prefManager;
    FirebaseFirestore database;
    Double myPoints = 0.0, teamPoints = 0.0, zucoBit, totalPoints = 0.0, amount = 0.0, remaining = 0.0;
    //  String bankName, accountNumber;
    PaymentModel paymentModel;
    private boolean freeze = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointsWithdrawScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    void init() {
        customProgressDialogue = new CustomProgressDialogue();
        prefManager = new PrefManager(this);
        database = FirebaseFirestore.getInstance();
        binding.withdraw.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.bankLayout.setOnClickListener(this);
        fetchUserData();
        checkActivePlan();
        getData();
        getZucoBit();
    }

    @SuppressLint("SetTextI18n")
    void getData() {
        try {
            myPoints = getIntent().getDoubleExtra(POINTS_FB, 0);
            teamPoints = getIntent().getDoubleExtra(BALANCE_FB, 0);
            totalPoints = myPoints + teamPoints;
            binding.points.setText((Utilis.round2(totalPoints, BALANCE_RANGE) + ""));

        } catch (Exception e) {

        }
    }

    void fetchUserData() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(USER_FB).document(prefManager.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    freeze = task.getResult().toObject(User.class).isFreeze();
                    if (!freeze) {
                        checkCurrentWithdraw();
                        customProgressDialogue.hideCustomDialog();
                    } else {
                        Toast.makeText(PointsWithdrawScreen.this, "Your account is freezed", Toast.LENGTH_SHORT).show();
                        customProgressDialogue.hideCustomDialog();
                    }
                }
            }
        });
    }

    void getZucoBit() {
        database.collection(ZUCOBIT_FB).document(ZUCOBIT_FB).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {
                    zucoBit = Objects.requireNonNull(task.getResult().toObject(ShakehandBitRate.class)).getZucobit();
                    amount = zucoBit * totalPoints;
                    binding.amount.setText(Utilis.round2(amount, BALANCE_RANGE) + "");
                } else
                    Toast.makeText(PointsWithdrawScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.bankLayout) {
            startActivity(new Intent(PointsWithdrawScreen.this, MyBankDetailScreen.class));
        } else if (id == R.id.withdraw) {
            if (isValid()) {
                if ((amount + paymentModel.getAmount()) > activePlan.getAmount() * 5) {
                    Toast.makeText(this, "Your already withdraw max amount. buy new plan", Toast.LENGTH_SHORT).show();
                } else {
                    getBankDetails();
                }
            }
        }
    }

    void addWithdraw(String bankName, String accountNumber) {
        customProgressDialogue.showCustomDialog(this);
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String transactionId = database.collection(POINTS_WITHDRAW_FB).document().getId();
        PointsWithdrawModel pointsWithdrawModel = new PointsWithdrawModel(date, transactionId, prefManager.getUid(), PENDING, bankName, accountNumber, amount, totalPoints, teamPoints, myPoints);
        database.collection(POINTS_WITHDRAW_FB).document(prefManager.getUid()).set(pointsWithdrawModel);
        database.collection(POINTS_WITHDRAW_FB).document(prefManager.getUid()).collection(POINTS_WITHDRAW_FB).document(transactionId).set(pointsWithdrawModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PointsWithdrawScreen.this, "success", Toast.LENGTH_SHORT).show();
                            emptyPoints();
                            onBackPressed();
                        } else {
                            Toast.makeText(PointsWithdrawScreen.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        customProgressDialogue.hideCustomDialog();
                    }
                });
    }

    void emptyPoints() {
        database.collection(TOTAL_POINTS_FB).document(prefManager.getUid()).update(USER_POINTS, 0, TEAM_POINTS, 0);
    }

    boolean isValid() {
        if (freeze) {
            Toast.makeText(this, "your account is freeze", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (amount <= 0) {
            Toast.makeText(this, "Amount not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (activePlan == null) {
            Toast.makeText(this, "Plan is not active", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void checkActivePlan() {
//        database.collection(ACTIVE_PLANS_FB).document(prefManager.getUid())
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            Toast.makeText(PointsWithdrawScreen.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "onEvent: " + error.getMessage());
//                            return;
//                        }
//                        assert value != null;
//                        if (value.exists()) activePlan = value.toObject(PlanPaymentRequest.class);
//                    }
//                });
    }

    void checkCurrentWithdraw() {
        database.collection(TOTAL_WITHDRAW_FB).document(prefManager.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(PointsWithdrawScreen.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                assert value != null;
                if (value.exists()) {
                    paymentModel = value.toObject(PaymentModel.class);

                    try {
                        remaining = activePlan.getAmount() * 5 - paymentModel.getAmount();
                        binding.remaining.setText(getString(R.string.pk_currency) + Utilis.round2(remaining, BALANCE_RANGE));
                    } catch (Exception e) {

                    }
                }
            }
        });
    }


    void getBankDetails() {
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
                            Toast.makeText(PointsWithdrawScreen.this, "Please enter bank details", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PointsWithdrawScreen.this, MyBankDetailScreen.class));
                            customProgressDialogue.hideCustomDialog();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }
}