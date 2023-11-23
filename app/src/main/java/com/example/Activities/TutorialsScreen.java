package com.example.Activities;

import static com.example.utils.Constants.REF_TUTORIAL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.Adapters.TutorialAdp;
import com.example.Models.TutorialModel;
import com.example.utils.ClickInterface;
import com.example.databinding.ActivityTutorialsScreenBinding;

import java.util.LinkedList;

public class TutorialsScreen extends AppCompatActivity implements ClickInterface {
    private static final String TAG = "TutorialsScreen";
    ActivityTutorialsScreenBinding binding;
    LinkedList<TutorialModel> tutorialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTutorialsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchTutorials();
        binding.back.setOnClickListener(v -> finish());
    }

    void fetchTutorials() {
        tutorialList = new LinkedList<>();
        FirebaseDatabase.getInstance().getReference(REF_TUTORIAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tutorialList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        TutorialModel model = ds.getValue(TutorialModel.class);
                        tutorialList.add(model);
                    }
                    binding.tutorialRecycler.setAdapter(new TutorialAdp(tutorialList, TutorialsScreen.this, TutorialsScreen.this));
                }
                if (tutorialList.size() > 0) binding.empty.setVisibility(View.GONE);
                else binding.empty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void clickItem(int position) {
        startActivity(new Intent(TutorialsScreen.this, YoutubePlayerScreen.class).putExtra(REF_TUTORIAL, tutorialList.get(position)));
    }
}