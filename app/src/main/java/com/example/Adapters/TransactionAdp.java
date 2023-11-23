package com.example.Adapters;

import static com.example.utils.Constants.APPROVED;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.PENDING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Models.Withdrawal;
import com.example.R;
import com.example.utils.ClickInterface;
import com.example.utils.Utilis;
import com.example.databinding.TransactionItemBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class TransactionAdp extends RecyclerView.Adapter<TransactionAdp.Holder> {
    private static final String TAG = "TransactionAdp";
    LinkedList<Withdrawal> list;
    Context context;
    ClickInterface clickInterface;

    public TransactionAdp(LinkedList<Withdrawal> list, Context context, ClickInterface clickInterface) {
        this.list = list;
        this.context = context;
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(LinkedList<Withdrawal> filterList) {
        // below line is to add our filtered
        // list in our course array list.
        this.list = filterList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Withdrawal withdrawal = list.get(position);
        holder.binding.amount.setText(" " + Utilis.round2(withdrawal.getAmount(), BALANCE_RANGE));
        holder.binding.transactionId.setText(withdrawal.getTransactionId());
        holder.convert(withdrawal.getDate());
        if (list.get(position).getStatus().equals(PENDING)) {
            holder.binding.status.setBackground(context.getResources().getDrawable(R.drawable.transaction_orange));
        } else if (list.get(position).getStatus().equals(APPROVED)) {
            holder.binding.status.setBackground(context.getResources().getDrawable(R.drawable.transaction_green));
        } else
            holder.binding.status.setBackground(context.getResources().getDrawable(R.drawable.transaction_red));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TransactionItemBinding binding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = TransactionItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickInterface.clickItem(getAdapterPosition());
                }
            });
        }

        void convert(String value) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date date = dateFormat.parse(value);

                String day = (String) DateFormat.format("dd", date); // 20
                String monthString = (String) DateFormat.format("MMM", date); // Jun
                String year = (String) DateFormat.format("yyyy", date); // 2013
                binding.month.setText(monthString);
                binding.date.setText(day);
                binding.year.setText(year);
            } catch (ParseException e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}

