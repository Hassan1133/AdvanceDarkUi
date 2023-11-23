package com.example.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.Activities.AddRechargeScreen;
import com.example.Adapters.RechargeAdp;
import com.example.R;
import com.example.databinding.FragmentRechargeHistoryBinding;

import java.util.LinkedList;

public class RechargeHistoryFrag extends Fragment implements View.OnClickListener {
    FragmentRechargeHistoryBinding binding;
    LinkedList<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRechargeHistoryBinding.inflate(getLayoutInflater(), container, false);
        binding.rechargeBtn.setOnClickListener(this);
        setRecharge();
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rechargeBtn) {
            startActivity(new Intent(getContext(), AddRechargeScreen.class));
        }
    }

    void setRecharge() {
        list = new LinkedList<>();
        list.add("qq");
        list.add("qq");
        list.add("qq");
        list.add("qq");
        list.add("qq");
        binding.rechargeRecycler.setAdapter(new RechargeAdp(list, getContext()));
    }
}