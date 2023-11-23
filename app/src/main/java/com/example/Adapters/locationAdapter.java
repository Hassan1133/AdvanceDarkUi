package com.example.Adapters;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Fragments.BottomSheetMapsFragment;
import com.example.Models.AreaModel;
import com.example.R;
import com.example.databinding.ItemMapBinding;

import java.util.ArrayList;

public class locationAdapter extends RecyclerView.Adapter<locationAdapter.myViewHolder> {
    private Context context;
    private ArrayList<AreaModel> arrayList;

    public locationAdapter(Context context, ArrayList<AreaModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_map, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        AreaModel areaModel = arrayList.get(position);
        holder.binding.title.setText(areaModel.getTitle());
        holder.binding.desc.setText(areaModel.getDescription());
        holder.itemView.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putSerializable("location", areaModel);
            BottomSheetMapsFragment fragment = new BottomSheetMapsFragment();
            fragment.setArguments(bundle);
            fragment.show(((FragmentActivity) context).getSupportFragmentManager(), fragment.getTag());
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ItemMapBinding binding;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMapBinding.bind(itemView);
        }
    }
}
