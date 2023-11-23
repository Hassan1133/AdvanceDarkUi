package com.example.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.R;
import com.example.databinding.FragmentBottomRechargeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomRechargeFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    FragmentBottomRechargeBinding binding;
    private BottomSheetListener listener;

    public static BottomRechargeFragment newInstance() {
        return new BottomRechargeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBottomRechargeBinding.inflate(getLayoutInflater(), container, false);
        binding.jazz.setOnClickListener(this);
        binding.ufone.setOnClickListener(this);
        binding.zong.setOnClickListener(this);
        binding.telenor.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.jazz) {
            listener.onClicked(getResources().getString(R.string.jazz));
        } else if (id == R.id.ufone) {
            listener.onClicked(getResources().getString(R.string.ufone));
        } else if (id == R.id.zong) {
            listener.onClicked(getResources().getString(R.string.zong));
        } else if (id == R.id.telenor) {
            listener.onClicked(getResources().getString(R.string.telenor));
        }
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BottomSheetListener) {
            listener = (BottomSheetListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface BottomSheetListener {
        void onClicked(String company);
    }

}