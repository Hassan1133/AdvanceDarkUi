package com.example.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.Models.User;
import com.example.R;
import com.example.databinding.FragmentBottomSheetBinding;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    FragmentBottomSheetBinding binding;
    User user;
    private BottomSheetListener listener;

    public BottomSheetFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomSheetBinding.inflate(getLayoutInflater(), container, false);
        binding.profile.setOnClickListener(this);
        binding.balance.setOnClickListener(this);
        return binding.getRoot();
    }

    // on button click
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.profile) {
            listener.onClicked("p");
            dismiss();
        } else if (id == R.id.balance) {
            listener.onClicked("b");
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (BottomSheetListener) getParentFragment();
        } catch (Exception e) {

        }
    }

    public interface BottomSheetListener {
        void onClicked(String filter);
    }
}