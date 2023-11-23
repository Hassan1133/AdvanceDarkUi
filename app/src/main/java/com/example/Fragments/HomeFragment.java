package com.example.Fragments;

import static com.example.utils.Constants.BALANCE_FB;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.USER_FB;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.Activities.InviteScreen;
import com.example.Activities.LoginActivity;
import com.example.Activities.NotificationsScreen;
import com.example.Activities.ShowPoinsScreen;
import com.example.Models.PaymentModel;
import com.example.Models.User;
import com.example.R;
import com.example.databinding.FragmentHomeBinding;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.utils.Utilis;
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

import java.util.LinkedList;
import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeBinding binding;
    private static final String TAG = "HomeFragment";
    private final long timer = 28800; //28800
    public Double points = 0.0;
    private FirebaseFirestore database;
    private PrefManager prefManager;
    private LinkedList<User> list = new LinkedList<>();
    private User currentUser;
    private CustomProgressDialogue customProgressDialogue;
    private LinkedList<String> usersListPoints;

    private Activity activity;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        init();
        return binding.getRoot();
    }

    private void init() {
        database = FirebaseFirestore.getInstance();
        prefManager = new PrefManager(activity);
        customProgressDialogue = new CustomProgressDialogue();
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

//        binding.notification.setOnClickListener(this);
//        binding.inviteCard.setOnClickListener(this);
//        binding.cardTeam.setOnClickListener(this);
//        binding.cardShowPoints.setOnClickListener(this);
        binding.profile.setOnClickListener(this);
        usersListPoints = new LinkedList<>();
        fetchUserDetails(); // updates iser data
        fetchTeam();
        getCurrentBalance();
        initializeAd();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }


    // on item click
    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.inviteCard) {
            startActivity(new Intent(activity, InviteScreen.class));
        } else if (id == R.id.notification) {
            startActivity(new Intent(activity, NotificationsScreen.class));
        } else if (id == R.id.cardShowPoints) {
            startActivity(new Intent(getContext(), ShowPoinsScreen.class));
        } else if (id == R.id.cardTeam) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_teamFragment);
        } else if (id == R.id.profile) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_settingFragment);
        }
    }

    void getCurrentBalance() {
        database.collection(BALANCE_FB).document(prefManager.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                assert value != null;

                if (value.exists()) {
                    double currentBalance = Objects.requireNonNull(value.toObject(PaymentModel.class)).getAmount();
                    binding.balance.setText(Utilis.round2(currentBalance, BALANCE_RANGE) + "");
                }
            }
        });
    }


    // updated user details
    void fetchUserDetails() {

        database.collection(USER_FB).document(prefManager.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value.exists()) {
                    try {
                        currentUser = value.toObject(User.class);
                        // blocked
                        if (currentUser.isBlock()) {
                            Toast.makeText(activity, "Your account is blocked", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(activity, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            prefManager.clearAll();
                            getActivity().finish();
                            return;
                        } else {
                            prefManager.setUserDetails(currentUser);
                        }
                        // Convert the first character to uppercase and concatenate the rest
                        String userName = prefManager.getUserName();
                        String capatalizedName = Character.toUpperCase(userName.charAt(0)) + userName.substring(1);
                        binding.name.setText(capatalizedName);

                    } catch (Exception e) {
                        Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void fetchTeam() {

        list = new LinkedList<>();
        database.collection(USER_FB).whereEqualTo("refId", prefManager.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        Log.d(TAG, "onEvent: " + dc.getDocument().toObject(User.class).getName());
                        list.add(dc.getDocument().toObject(User.class));
                    }
                }
//                binding.teamCount.setText(" " + list.size());
            }
        });
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