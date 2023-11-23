package com.example.Activities;

import static com.example.utils.Constants.ALL_DEPOSIT;
import static com.example.utils.Constants.AMOUNT_FB;
import static com.example.utils.Constants.BALANCE_FB;
import static com.example.utils.Constants.DIRECT_INDIRECT_FB;
import static com.example.utils.Constants.STATUS_FB;
import static com.example.utils.Constants.USER_FB;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.Models.DepositModel;
import com.example.Models.DirectIndirect;
import com.example.Models.User;
import com.example.R;
import com.example.databinding.ActivityMainBinding;
import com.example.utils.PrefManager;
import com.example.utils.ShakehandApp;
import com.example.utils.Utilis;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PrefManager prefManager;
    private FirebaseFirestore database;
    private LinkedList<Integer> directList;
    private LinkedList<String> usersList;
    private DepositModel depositModel;
    private int count = 1;
    private double totalSent = 0;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        prefManager = new PrefManager(this);
        database = FirebaseFirestore.getInstance();

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        playAd();


        Application application = getApplication();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!(application instanceof ShakehandApp)) {
                    // Log.e(TAG, "Failed to cast application to MyApplication.");
                    return;
                }

                // Log.d(TAG, "run: show ad");
                // Show the app open ad.
                ((ShakehandApp) application)
                        .showAdIfAvailable(
                                MainActivity.this,
                                new ShakehandApp.OnShowAdCompleteListener() {
                                    @Override
                                    public void onShowAdComplete() {
                                        //  Log.d(TAG, "onShowAdComplete: ");
                                    }
                                });
            }
        }, 4000);

        getDirectIndirect();
    }

    private void playAd() {
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
    }

    private int convertToInt(String value) {
        return Integer.parseInt(value);
    }

    void getDirectIndirect() {
        directList = new LinkedList<>();
        database.collection(DIRECT_INDIRECT_FB).document(DIRECT_INDIRECT_FB).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DirectIndirect directIndirect = task.getResult().toObject(DirectIndirect.class);
                        assert directIndirect != null;
                        directList.add(convertToInt(directIndirect.getLevel1()));
                        directList.add(convertToInt(directIndirect.getLevel2()));
                        directList.add(convertToInt(directIndirect.getLevel3()));
                        directList.add(convertToInt(directIndirect.getLevel4()));
                        directList.add(convertToInt(directIndirect.getLevel5()));
                        fetchPendingBalance();
                    }
                }
            }
        });
    }

    void fetchPendingBalance() {
        usersList = new LinkedList<>();
        database.collection(ALL_DEPOSIT).document(prefManager.getUid()).collection(ALL_DEPOSIT).whereEqualTo(STATUS_FB, false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) return;
                assert value != null;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        depositModel = documentChange.getDocument().toObject(DepositModel.class);
                        getUserDetails(depositModel.getUid());
                        Log.d(TAG, "onEvent: " + depositModel.getAmount());
                        break; // it means only one division at one time
                    }
                }
            }
        });
    }

    void getUserDetails(String uid) {
        if (count <= 5 && !uid.isEmpty()) {
            database.collection(USER_FB).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            User user = task.getResult().toObject(User.class);
                            assert user != null;
                            String refid = user.getRefId();
                            if (!refid.isEmpty() && !user.isFreeze()) usersList.add(refid);
                            // Log.d(TAG, "ref id is: " + refid + " : uid  of parent  " + uid);
                            count = count + 1;
                            getUserDetails(refid);
                        }
                    }
                }
            });
        }// if end
        else {
            sendAmount();
        }
    }

    private void sendAmount() {
        if (!usersList.isEmpty()) // divide in referrals
        {
            for (int i = 0; i < usersList.size(); i++) {
                double amountSent = Utilis.getPercentage(depositModel.getAmount(), directList.get(i));
                sendAmountToUser(usersList.get(i), amountSent);
                Log.d(TAG, "sendAmount: percen " + directList.get(i) + "   " + amountSent + " to user " + usersList.get(i));
                totalSent = totalSent + amountSent;
                if (i == usersList.size() - 1) {
                    sendAmountToUser(prefManager.getUid(), depositModel.getAmount() - totalSent);
                    changeStatus();
                }
            }
        } else  // no ref to send all money is his own / code here
        {
            changeStatus();
            sendAmountToUser(prefManager.getUid(), depositModel.getAmount());
        }
    }

    void sendAmountToUser(String id, double amount) {
        Log.d(TAG, "sendAmountToUser: " + id + "   " + amount);
        database.collection(BALANCE_FB).document(id).update(AMOUNT_FB, FieldValue.increment(amount));
    }

    void changeStatus() {
        Log.d(TAG, "changeStatus: " + depositModel.getId());
        database.collection(ALL_DEPOSIT).document(prefManager.getUid()).collection(ALL_DEPOSIT).document(depositModel.getId()).update(STATUS_FB, true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) Log.d(TAG, "updated: ");
                else Log.d(TAG, "error: " + task.getException().getMessage());
            }
        });
        count = 1;
        totalSent = 0;
    }
}