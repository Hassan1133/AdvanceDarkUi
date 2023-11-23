package com.example.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Models.FAQsModel;
import com.example.R;
import com.example.databinding.FaqItemBinding;

import java.util.LinkedList;

public class FAQsAdp extends RecyclerView.Adapter<FAQsAdp.Holder> {
    LinkedList<FAQsModel> list;
    Context context;

    public FAQsAdp(LinkedList<FAQsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.faq_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.question.setText(list.get(position).getQuestion());
        holder.binding.answer.setText(list.get(position).getAnswer());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        FaqItemBinding binding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = FaqItemBinding.bind(itemView);
        }
    }
}
