package com.example.Activities;

import static com.example.utils.Constants.DIRECT_INDIRECT_FB;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.Models.DirectIndirect;
import com.example.utils.CustomProgressDialogue;
import com.example.databinding.ActivityDirectIndirectScreenBinding;

public class DirectIndirectScreen extends AppCompatActivity {
    private static final String TAG = "DirectIndirectScreen";
    ActivityDirectIndirectScreenBinding binding;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectIndirectScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        fetchData();
    }

    void init() {
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void fetchData() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(DIRECT_INDIRECT_FB).document(DIRECT_INDIRECT_FB)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            setData(documentSnapshot.toObject(DirectIndirect.class));
                            customProgressDialogue.hideCustomDialog();
                        }
                        customProgressDialogue.hideCustomDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgressDialogue.hideCustomDialog();
                    }
                });
    }

    void setData(DirectIndirect data) {
        binding.level1.setText(data.getLevel1());
        binding.level2.setText(data.getLevel2());
        binding.level3.setText(data.getLevel3());
        binding.level4.setText(data.getLevel4());
        binding.level5.setText(data.getLevel5());
        binding.point1.setText(data.getPoint1());
        binding.point2.setText(data.getPoint2());
        binding.point3.setText(data.getPoint3());
        binding.point4.setText(data.getPoint4());
        binding.point5.setText(data.getPoint5());
    }
}