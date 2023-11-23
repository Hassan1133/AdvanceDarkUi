package com.example.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Models.TutorialModel;
import com.example.R;
import com.example.utils.ClickInterface;
import com.example.databinding.TutorialItemBinding;

import java.util.LinkedList;

public class TutorialAdp extends RecyclerView.Adapter<TutorialAdp.Holder> {
    private LinkedList<TutorialModel> list;
    private Context context;
    private ClickInterface click;

    public TutorialAdp(LinkedList<TutorialModel> list, Context context, ClickInterface click) {
        this.list = list;
        this.context = context;
        this.click = click;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.tutorial_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            TutorialModel tutorialModel = list.get(position);
            holder.binding.title.setText(tutorialModel.getTitle());
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TutorialItemBinding binding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = TutorialItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.clickItem(getAdapterPosition());
                }
            });
        }
    }
}

