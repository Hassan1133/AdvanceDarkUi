package com.example.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.Activities.InviteScreen;
import com.example.Activities.PlansScreen;
import com.example.Activities.PointsWithdrawRequestsScreen;
import com.example.Activities.TutorialsScreen;
import com.example.R;
import com.example.databinding.FragmentMoreBinding;

public class MoreFrag extends Fragment implements View.OnClickListener {
    FragmentMoreBinding binding;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(getLayoutInflater(), container, false);
        binding.setting.setOnClickListener(this);
        binding.help.setOnClickListener(this);
        binding.plans.setOnClickListener(this);
        binding.invite.setOnClickListener(this);
        binding.balanceDeposit.setOnClickListener(this);
        binding.tutorials.setOnClickListener(this);
        binding.cardDirectIndirect.setOnClickListener(this);
        binding.balanceShare.setOnClickListener(this);

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.setting) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_moreFragment_to_settingFragment);
        } else if (id == R.id.help) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_moreFragment_to_helpFragment);
        } else if (id == R.id.plans) {
            startActivity(new Intent(activity, PlansScreen.class));
        } else if (id == R.id.invite) {
            startActivity(new Intent(activity, InviteScreen.class));
        } else if (id == R.id.balanceDeposit) {
            startActivity(new Intent(activity, PointsWithdrawRequestsScreen.class));
        } else if (id == R.id.tutorials) {
            startActivity(new Intent(activity, TutorialsScreen.class));
        } else if (id==R.id.cardDirectIndirect) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_moreFragment_to_directIndirectFrag);
        } else if (id==R.id.balanceShare) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_moreFragment_to_balanceShareFragment);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
}
