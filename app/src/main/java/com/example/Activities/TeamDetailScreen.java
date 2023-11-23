package com.example.Activities;

import static com.example.utils.Constants.COUNTER;
import static com.example.utils.Constants.IMAGE;
import static com.example.utils.Constants.TEAM_FB;
import static com.example.utils.Constants.USER_FB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Adapters.TeamAdp;
import com.example.Models.User;
import com.example.R;
import com.example.utils.ClickInterface;
import com.example.utils.CustomProgressDialogue;
import com.example.databinding.ActivityTeamDetailScreenBinding;

import java.util.LinkedList;

public class TeamDetailScreen extends AppCompatActivity implements View.OnClickListener, ClickInterface {
    private static final String TAG = "TeamDetailScreen";
    ActivityTeamDetailScreenBinding binding;
    CustomProgressDialogue customProgressDialogue;
    FirebaseFirestore database;
    LinkedList<User> list;
    User user;
    TeamAdp adp;
    int levelCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeamDetailScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
        playAd();
    }

    void init() {
        binding.back.setOnClickListener(this);
        binding.call.setOnClickListener(this);
        binding.message.setOnClickListener(this);
        binding.profile.setOnClickListener(this);
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        getBundle();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.call) {
            callIntent(user.getPhone());
        } else if (id == R.id.message) {
            sendWhatsapp(user.getPhone());
        } else if (id == R.id.profile) {
            try {
                if (user.getProfile() != null || !user.getProfile().isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), ShowPictureScreen.class).putExtra(IMAGE, user.getProfile()));
                }
            } catch (Exception e) {
            }
        }
    }

    void getBundle() {
        try {
            user = (User) getIntent().getSerializableExtra(TEAM_FB);
            levelCounter = getIntent().getIntExtra(COUNTER, 0) + 1;
            //  TODO:sadfghjklrewewrtyuiytrew
            setUserData();
            if (user != null && levelCounter <= 5) {
                fetchTeam(user.getUid());
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    void fetchTeam(String uid) {
        customProgressDialogue.showCustomDialog(this);
        list = new LinkedList<>();
        database.collection(USER_FB).whereEqualTo("refId", uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    customProgressDialogue.hideCustomDialog();
                    return;
                }
                assert value != null;
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        Log.d(TAG, "onEvent: " + dc.getDocument().toObject(User.class).getName());
                        list.add(dc.getDocument().toObject(User.class));
                    }
                }
                adp = new TeamAdp(list, TeamDetailScreen.this, TeamDetailScreen.this);
                binding.teamRecycler.setAdapter(adp);
                customProgressDialogue.hideCustomDialog();
                binding.teamSize.setText("Team size: " + list.size());
                if (list.isEmpty() || levelCounter == 6) {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.teamRecycler.setVisibility(View.GONE);
                } else {
                    binding.empty.setVisibility(View.GONE);
                    binding.teamRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void setUserData() {
        binding.name.setText(user.getName());
        binding.email.setText(user.getEmail());
        binding.address.setText(user.getAddress());
        binding.level.setText("Level: " + levelCounter);
        try {
            if (user.getProfile() != null || !user.getProfile().isEmpty()) {
                Glide.with(this).load(user.getProfile()).into(binding.profile);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void clickItem(int position) {
        Intent intent = new Intent(TeamDetailScreen.this, TeamDetailScreen.class);
        intent.putExtra(TEAM_FB, list.get(position));
        intent.putExtra(COUNTER, levelCounter);

        startActivity(intent);

    }

    void callIntent(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    void sendWhatsapp(String phone) {
        try {
            phone = phone.replace("+", "").replace(" ", "");
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + phone + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(TeamDetailScreen.this, "it may be you dont have whats app", Toast.LENGTH_LONG).show();
        }
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
}