package com.example.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.Models.PlansModel;
import com.example.R;
import com.example.databinding.PlansCustomSliderItemBinding;

import java.util.List;

public class PlansAdp extends RecyclerView.Adapter<PlansAdp.Holder> {
    private static final String TAG = "PlansAdp";
    private Context context;
    private List<PlansModel> list;
    private ViewPager2 pager;

    public PlansAdp(Context context, List<PlansModel> list, ViewPager2 pager) {
        this.context = context;
        this.list = list;
        this.pager = pager;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.plans_custom_slider_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.title.setText(list.get(position).getTitle());
        holder.binding.description.setText(list.get(position).getDescription());
        holder.binding.price.setText(context.getString(R.string.dollar_currency) + list.get(position).getPrice());
        holder.binding.speed.setText(list.get(position).getSpeed() + "X speed");
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + list.size());
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        PlansCustomSliderItemBinding binding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = PlansCustomSliderItemBinding.bind(itemView);
        }
    }
}

