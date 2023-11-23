package com.example.Activities;

import static com.example.utils.Constants.FAQS_FB;
import static com.example.utils.Constants.SETTING_ADMIN_FB;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Adapters.FAQsAdp;
import com.example.Models.FAQsModel;
import com.example.utils.CustomProgressDialogue;
import com.example.databinding.ActivityFaqscreenBinding;

import java.util.LinkedList;

public class FAQScreen extends AppCompatActivity {
    private static final String TAG = "FAQScreen";
ActivityFaqscreenBinding binding;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    FAQsAdp adp;
    LinkedList<FAQsModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFaqscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
        fetchFAQs();
    }

    void init() {
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void fetchFAQs() {
        list = new LinkedList<>();
        customProgressDialogue.showCustomDialog(this);
        database.collection(SETTING_ADMIN_FB).document(SETTING_ADMIN_FB).collection(FAQS_FB)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            customProgressDialogue.hideCustomDialog();
                            Toast.makeText(FAQScreen.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        assert value != null;
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                                list.add(dc.getDocument().toObject(FAQsModel.class));
                            }
                        }
                        adp = new FAQsAdp(list, FAQScreen.this);
                        binding.faqRecycler.setAdapter(adp);
                        customProgressDialogue.hideCustomDialog();
                    }
                });
    }
}