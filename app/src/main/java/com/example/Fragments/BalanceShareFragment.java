package com.example.Fragments;

import static com.example.utils.Constants.ADMIN_BALANCE;
import static com.example.utils.Constants.ADMIN_PERCENTAGE;
import static com.example.utils.Constants.AMOUNT_FB;
import static com.example.utils.Constants.BALANCE_FB;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.RECEIVER;
import static com.example.utils.Constants.SENDER;
import static com.example.utils.Constants.SHARE_BALANCE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.Adapters.ShareBalanceAdp;
import com.example.Models.PaymentModel;
import com.example.Models.ShareBalanceModel;
import com.example.R;
import com.example.databinding.FragmentBalanceShareBinding;
import com.example.databinding.SendCoinsDialogueBinding;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.utils.Utilis;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;


public class BalanceShareFragment extends Fragment implements View.OnClickListener {
    FragmentBalanceShareBinding binding;
    Activity activity;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    PrefManager prefManager;
    SendCoinsDialogueBinding sendCoinsDialogueBinding;
    AlertDialog dialog;
    String receiverId;
    float balance = 0;
    LinkedList<ShareBalanceModel> list;
    ShareBalanceAdp adp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBalanceShareBinding.inflate(getLayoutInflater(), container, false);
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        prefManager = new PrefManager(activity);
        binding.back.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        getCurrentBalance();
        getTransactionHistory();
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            activity.onBackPressed();
        } else if (v.getId() == R.id.share) {
            openDialog();
        }
    }

    void getTransactionHistory() {
        customProgressDialogue.showCustomDialog(activity);
        list = new LinkedList<>();

        Query query = database.collection(SHARE_BALANCE).where(Filter.or(
                Filter.equalTo(RECEIVER, prefManager.getUid()),
                Filter.equalTo(SENDER, prefManager.getUid())
        ));

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    customProgressDialogue.hideCustomDialog();
                    Toast.makeText(activity, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                assert value != null;
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        list.add(dc.getDocument().toObject(ShareBalanceModel.class));
                    }
                }
                if (list.size() > 0) {
                    list.sort(new Comparator<ShareBalanceModel>() {
                        @SuppressLint("SimpleDateFormat")
                        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                        public int compare(ShareBalanceModel obj1, ShareBalanceModel obj2) {
                            try {
                                return Objects.requireNonNull(dateFormat.parse(obj2.getDate())).compareTo(dateFormat.parse(obj1.getDate()));
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                    });
                    adp = new ShareBalanceAdp(list, activity);
                    binding.balanceRecycler.setAdapter(adp);

                }
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

    void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = LayoutInflater.from(activity).inflate(R.layout.send_coins_dialogue, null);
        builder.setView(customLayout);
        sendCoinsDialogueBinding = SendCoinsDialogueBinding.bind(customLayout);

        sendCoinsDialogueBinding.addBtn.setOnClickListener(v -> {
            String amountTemp = Objects.requireNonNull(sendCoinsDialogueBinding.balanceEt.getText()).toString();
            receiverId = sendCoinsDialogueBinding.refEt.getText().toString().trim();
            if (isValid(amountTemp)) {
                customProgressDialogue.showCustomDialog(activity);
                getBalance();
            }
        });
        sendCoinsDialogueBinding.clear.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog = builder.create();
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    boolean isValid(String amountTemp) {
        if (receiverId.isEmpty()) {
            sendCoinsDialogueBinding.refEt.setError(getString(R.string.valid_uid));
            return false;
        }
        if (receiverId.contains(prefManager.getUid())) {
            sendCoinsDialogueBinding.refEt.setError(getString(R.string.valid_uid));
            return false;
        }
        if (amountTemp.isEmpty()) {
            sendCoinsDialogueBinding.balanceEt.setError(getString(R.string.valid_balance));
            return false;
        }
        balance = Float.parseFloat(amountTemp);
        if (balance <= 0) {
            sendCoinsDialogueBinding.balanceEt.setError(getString(R.string.valid_balance));
            return false;
        }
        return true;
    }


    void getBalance() {
        database.collection(BALANCE_FB).document(prefManager.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    PaymentModel paymentModel = documentSnapshot.toObject(PaymentModel.class);
                    assert paymentModel != null;
                    if (paymentModel.getAmount() >= balance) // have balance to share
                    {
                        double percent = Utilis.round2(Utilis.getPercentage(balance, 100 - ADMIN_PERCENTAGE), BALANCE_RANGE);
                        sendToUser(receiverId, percent);
                        sendToUser(prefManager.getUid(), balance * (-1));
                        addBalance(percent);
                        sendToAdmin(balance);
                    } else {
                        sendCoinsDialogueBinding.balanceEt.setError(getString(R.string.valid_balance));
                        customProgressDialogue.hideCustomDialog();
                        Toast.makeText(activity, "You don't have sufficient balance", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void sendToAdmin(double balance) {
        double percentage = Utilis.getPercentage(balance, ADMIN_PERCENTAGE);
        database.collection(ADMIN_BALANCE).document(ADMIN_BALANCE).update(AMOUNT_FB, FieldValue.increment(percentage));
    }

    void addBalance(double amount) {
        String key = database.collection(SHARE_BALANCE).document().getId();
        ShareBalanceModel shareBalanceModel = new ShareBalanceModel(prefManager.getUid(), receiverId, key, Utilis.getCurrentDateTime(), amount, balance);
        database.collection(SHARE_BALANCE).document(key).set(shareBalanceModel).addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                customProgressDialogue.hideCustomDialog();
                Toast.makeText(activity, "Sent Successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(activity, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                customProgressDialogue.hideCustomDialog();
                dialog.dismiss();
            }
        });
    }

    void sendToUser(String uid, double balance) {
        database.collection(BALANCE_FB).document(uid).update(AMOUNT_FB, FieldValue.increment(balance));
    }


    void getCurrentBalance() {
        database.collection(BALANCE_FB).document(prefManager.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;

                if (value.exists()) {
                    double currentBalance = Objects.requireNonNull(value.toObject(PaymentModel.class)).getAmount();
                    binding.balance.setText(Utilis.round2(currentBalance, BALANCE_RANGE) + "");
                }
            }
        });
    }


}