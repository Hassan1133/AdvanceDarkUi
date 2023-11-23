package com.example.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.databinding.RechargeItemBinding;

import java.util.LinkedList;

public class RechargeAdp extends RecyclerView.Adapter<RechargeAdp.Holder> {
    private LinkedList<String> list;
    private Context context;

    public RechargeAdp(LinkedList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new Holder(LayoutInflater.from(context).inflate(R.layout.recharge_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        RechargeItemBinding binding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = RechargeItemBinding.bind(itemView);

        }
    }
}

