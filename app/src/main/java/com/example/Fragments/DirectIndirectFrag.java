package com.example.Fragments;

import static com.example.utils.Constants.DIRECT_INDIRECT_FB;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.Models.DirectIndirect;
import com.example.utils.CustomProgressDialogue;
import com.example.databinding.FragmentDirectIndirectBinding;

import java.util.Objects;

public class DirectIndirectFrag extends Fragment {
    FragmentDirectIndirectBinding binding;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDirectIndirectBinding.inflate(getLayoutInflater(), container, false);

        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        fetchData();
        return binding.getRoot();
    }

    void fetchData() {
        customProgressDialogue.showCustomDialog(getContext());
        database.collection(DIRECT_INDIRECT_FB).document(DIRECT_INDIRECT_FB)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            setData(Objects.requireNonNull(documentSnapshot.toObject(DirectIndirect.class)));
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
    }
}