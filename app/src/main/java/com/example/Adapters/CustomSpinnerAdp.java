package com.example.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.Models.PaymentOptionsModel;
import com.example.R;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdp extends ArrayAdapter<PaymentOptionsModel> {
    private List<PaymentOptionsModel> paymentList;
    private Filter paymentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<PaymentOptionsModel> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(paymentList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (PaymentOptionsModel item : paymentList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((PaymentOptionsModel) resultValue).getName();
        }
    };

    public CustomSpinnerAdp(@NonNull Context context, @NonNull List<PaymentOptionsModel> countryList) {
        super(context, 0, countryList);
        paymentList = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return paymentFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.textview_item, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text);
        ImageView icon = convertView.findViewById(R.id.icon);
        PaymentOptionsModel countryItem = getItem(position);

        if (countryItem != null) {
            textViewName.setText(countryItem.getName());
            icon.setImageDrawable(countryItem.getDrawable());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.textview_item, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text);
        //ImageView icon = convertView.findViewById(R.id.icon);
        PaymentOptionsModel countryItem = getItem(position);

        if (countryItem != null) {
            textViewName.setText(countryItem.getName());
            // icon.setImageDrawable(countryItem.getDrawable());
        }

        return convertView;

    }
}
