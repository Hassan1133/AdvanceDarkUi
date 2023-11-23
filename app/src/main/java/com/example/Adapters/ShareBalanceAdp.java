package com.example.Adapters;

import static com.example.utils.Constants.BALANCE_RANGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Models.ShareBalanceModel;
import com.example.R;
import com.example.utils.PrefManager;
import com.example.utils.Utilis;
import com.example.databinding.BalanceShareItemBinding;

import java.util.LinkedList;

public class ShareBalanceAdp extends RecyclerView.Adapter<ShareBalanceAdp.Holder> {
    LinkedList<ShareBalanceModel> list;
    Context context;
    PrefManager prefManager;

    public ShareBalanceAdp(LinkedList<ShareBalanceModel> list, Context context) {
        this.list = list;
        this.context = context;
        prefManager = new PrefManager(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.balance_share_item,parent,false));
    }
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(LinkedList<ShareBalanceModel> filterList) {
        // below line is to add our filtered
        // list in our course array list.
        this.list = filterList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ShareBalanceModel shareBalanceModel = list.get(position);
        try {
            holder.binding.date.setText(shareBalanceModel.getDate());

            if (shareBalanceModel.getReceiver().equals(prefManager.getUid()))
            {
                holder.binding.profitCoins.setText("+ " + Utilis.round2(shareBalanceModel.getTotalAmount(),BALANCE_RANGE));
                holder.binding.profitLayout.setVisibility(View.VISIBLE);
                holder.binding.lossLayout.setVisibility(View.INVISIBLE);
            } else {
                holder.binding.lossLayout.setVisibility(View.VISIBLE);
                holder.binding.profitLayout.setVisibility(View.INVISIBLE);
                holder.binding.lossCoins.setText("- " + Utilis.round2(shareBalanceModel.getTotalAmount(),BALANCE_RANGE));
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        BalanceShareItemBinding binding;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = BalanceShareItemBinding.bind(itemView);
        }
    }
}
