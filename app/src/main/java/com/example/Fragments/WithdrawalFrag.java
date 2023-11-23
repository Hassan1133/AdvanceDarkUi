package com.example.Fragments;

import static com.example.utils.Constants.BALANCE_FB;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.WITHDRAW_FB;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Activities.WithdrawDetailScreen;
import com.example.Activities.WithdrawScreen;
import com.example.Adapters.TransactionAdp;
import com.example.Models.PaymentModel;
import com.example.Models.Withdrawal;
import com.example.R;
import com.example.utils.ClickInterface;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.utils.Utilis;
import com.example.databinding.FragmentWithdrawalBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

public class WithdrawalFrag extends Fragment implements View.OnClickListener, ClickInterface {
    private static final String TAG = "WithdrawalFragment";
    FragmentWithdrawalBinding binding;
    LinkedList<Withdrawal> list, filteredList;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    TransactionAdp adp;
    PrefManager prefManager;
    Double currentBalance;
    Activity activity;
    InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWithdrawalBinding.inflate(getLayoutInflater(), container, false);
        customProgressDialogue = new CustomProgressDialogue();
        binding.withdraw.setOnClickListener(this);
        prefManager = new PrefManager(activity);
        database = FirebaseFirestore.getInstance();

        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredList = new LinkedList<>();
//                || list.getEmail().toLowerCase().contains(s.toString().toLowerCase())
                for (Withdrawal list : list) {
                    if (list.getTransactionId().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredList.add(list);
                    }
                    if (filteredList.isEmpty()) {
                        // if no item is added in filtered list we are
                        // displaying a toast message as no data found.
                        adp.filterList(filteredList);
                    } else {
                        // at last we are passing that filtered
                        // list to our adapter class.
                        adp.filterList(filteredList);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getCurrentBalance();
        setRecycler();
        initializeAd();
        return binding.getRoot();
    }

    void setRecycler() {
        customProgressDialogue.showCustomDialog(getContext());
        list = new LinkedList<>();
        database.collection(WITHDRAW_FB).document(prefManager.getUid()).collection(WITHDRAW_FB).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            customProgressDialogue.hideCustomDialog();
                            return;
                        }
                        assert value != null;
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                                list.add(dc.getDocument().toObject(Withdrawal.class));
                            }
                            // modify
                            if (dc.getType().equals(DocumentChange.Type.MODIFIED))
                            {
                                for (int i = 0; i < list.size(); i++) {
                                    if (dc.getDocument().toObject(Withdrawal.class).getTransactionId().equals(list.get(i).getTransactionId())) {
                                        list.get(i).setStatus(dc.getDocument().toObject(Withdrawal.class).getStatus());
                                    }
                                }
                                adp.notifyDataSetChanged();
                            }
                        }
                        if (list.size() > 0) {
                            list.sort(new Comparator<Withdrawal>() {
                                @SuppressLint("SimpleDateFormat")
                                final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                                public int compare(Withdrawal obj1, Withdrawal obj2)
                                {
                                    try {
                                        return Objects.requireNonNull(dateFormat.parse(obj2.getDate())).compareTo(dateFormat.parse(obj1.getDate()));
                                    } catch (ParseException e) {
                                        return 0;
                                    }
                                }
                            });

                        }
                        adp = new TransactionAdp(list, getContext(), WithdrawalFrag.this);
                        binding.transcRecycler.setAdapter(adp);
                        customProgressDialogue.hideCustomDialog();
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.withdraw) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(activity);
            } else {
                startActivity(new Intent(getContext(), WithdrawScreen.class));
            }
        }
    }

    @Override
    public void clickItem(int position) {
        Intent intent = new Intent(getContext(), WithdrawDetailScreen.class);
        if (!filteredList.isEmpty()) {
            intent.putExtra(WITHDRAW_FB, filteredList.get(position));
        } else {
            intent.putExtra(WITHDRAW_FB, list.get(position));
        }
        startActivity(intent);
    }

    void getCurrentBalance() {
        filteredList = new LinkedList<>();
        database.collection(BALANCE_FB).document(prefManager.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                assert value != null;

                if (value.exists())
                {
                    currentBalance = Objects.requireNonNull(value.toObject(PaymentModel.class)).getAmount();
                    binding.balance.setText(Utilis.round2(currentBalance, BALANCE_RANGE) + "");
                }
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    private void initializeAd() {
        if (activity == null) {
            return;
        }
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, getResources().getString(R.string.admob_interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        setListeners();
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }

    private void setListeners() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    startActivity(new Intent(getContext(), WithdrawScreen.class));
                    mInterstitialAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    mInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        }
    }
}