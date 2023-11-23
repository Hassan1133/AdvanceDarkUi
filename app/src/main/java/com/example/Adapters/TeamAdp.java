package com.example.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Models.User;
import com.example.R;
import com.example.utils.ClickInterface;
import com.example.databinding.TeamItemBinding;

import java.util.List;

public class TeamAdp extends RecyclerView.Adapter<TeamAdp.Holder> {
    private static final String TAG = "TeamAdp";
    List<User> list;
    Context context;
    ClickInterface clickInterface;

    public TeamAdp(List<User> list, Context context, ClickInterface clickInterface) {
        this.list = list;
        this.context = context;
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.team_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.name.setText(list.get(position).getName());
        holder.binding.email.setText(list.get(position).getEmail());
        try {
            if (list.get(position).getProfile() != null || !list.get(position).getProfile().isEmpty()) {
                Glide.with(context).load(list.get(position).getProfile()).into(holder.binding.profile);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + list.size());
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TeamItemBinding binding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = TeamItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickInterface.clickItem(getAdapterPosition());
                }
            });
        }
    }
}
