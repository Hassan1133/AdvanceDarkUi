package com.example.Activities;

import static com.example.utils.Constants.ALL_DEPOSIT;
import static com.example.utils.Constants.DATE_FORMAT;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Adapters.DepositAdp;
import com.example.Models.DepositModel;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.databinding.ActivityPointsWithdrawRequestsScreenBinding;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

public class PointsWithdrawRequestsScreen extends AppCompatActivity implements View.OnClickListener {
ActivityPointsWithdrawRequestsScreenBinding binding;
    CustomProgressDialogue customProgressDialogue;
    FirebaseFirestore database;
    PrefManager prefManager;
    LinkedList<DepositModel> list;
    DepositAdp depositAdp;
    private static final String TAG = "PointsWithdrawRequestsS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointsWithdrawRequestsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        fetchTransactions();
    }

    void init() {
        customProgressDialogue = new CustomProgressDialogue();
        database = FirebaseFirestore.getInstance();
        prefManager = new PrefManager(this);
        binding.back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
    }

    void fetchTransactions() {
        list = new LinkedList<>();
        customProgressDialogue.showCustomDialog(this);
        Log.d(TAG, "fetchTransactions: "  +prefManager.getUid());
        database.collection(ALL_DEPOSIT).whereEqualTo("uid",prefManager.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot documentSnapshot : task.getResult())
                    {
                        list.add(documentSnapshot.toObject(DepositModel.class));
                    }
                    if (list.size()>0)
                    {
                        list.sort(new Comparator<DepositModel>()
                        {
                            @SuppressLint("SimpleDateFormat")
                            final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                            public int compare(DepositModel obj1, DepositModel obj2) {
                                try {
                                    return Objects.requireNonNull(dateFormat.parse(obj2.getDate())).compareTo(dateFormat.parse(obj1.getDate()));
                                } catch (Exception e) {
                                    return 0;
                                }
                            }
                        });

                        depositAdp = new DepositAdp(list,PointsWithdrawRequestsScreen.this);
                        binding.transactionRecycler.setAdapter(depositAdp);
                    }

                }else Toast.makeText(PointsWithdrawRequestsScreen.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

}
