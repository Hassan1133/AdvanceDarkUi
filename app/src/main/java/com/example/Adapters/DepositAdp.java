package com.example.Adapters;

import static com.example.utils.Constants.BALANCE_RANGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Models.DepositModel;
import com.example.R;
import com.example.utils.Utilis;
import com.example.databinding.BalanceDepositItemBinding;

import java.util.LinkedList;

public class DepositAdp extends RecyclerView.Adapter<DepositAdp.Holder> {
    LinkedList<DepositModel> list;
    Context context;

    public DepositAdp(LinkedList<DepositModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.balance_deposit_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            DepositModel adminDivision = list.get(position);
            holder.binding.date.setText(Utilis.convertToDate(adminDivision.getDate()));
            holder.binding.amount.setText(context.getString(R.string.pk_currency) +Utilis.round2(adminDivision.getAmount(),BALANCE_RANGE));
        }catch (Exception ignored){}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        BalanceDepositItemBinding binding;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = BalanceDepositItemBinding.bind(itemView);
        }
    }
}
