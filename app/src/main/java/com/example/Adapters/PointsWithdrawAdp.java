package com.example.Adapters;

import static com.example.utils.Constants.APPROVED;
import static com.example.utils.Constants.BALANCE_RANGE;
import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.PENDING;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.example.Models.PointsWithdrawModel;
import com.example.R;
import com.example.utils.ClickInterface;
import com.example.utils.Utilis;
import com.example.databinding.LayoutAdBinding;
import com.example.databinding.TransactionItemBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class PointsWithdrawAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;
    private static final int ITEM_FEED_COUNT = 6;
    private final LinkedList<PointsWithdrawModel> mainList;
    private final Activity context;
    private final ClickInterface clickInterface;

    public PointsWithdrawAdp(LinkedList<PointsWithdrawModel> mainList, Activity context, ClickInterface clickInterface) {
        this.mainList = mainList;
        this.context = context;
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == ITEM_VIEW) {
            View view = layoutInflater.inflate(R.layout.transaction_item, parent, false);
            return new MainViewHolder(view);
        } else if (viewType == AD_VIEW) {
            View view = layoutInflater.inflate(R.layout.layout_ad, parent, false);
            return new AdViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_VIEW) {
            int pos = position - Math.round(position / ITEM_FEED_COUNT);
            ((MainViewHolder) holder).bindData(mainList.get(pos));
        } else if (holder.getItemViewType() == AD_VIEW) {
            ((AdViewHolder) holder).bindAdData();
        }
    }

    @Override
    public int getItemCount() {
        if (mainList.size() > 0) {
            return mainList.size() + Math.round(mainList.size() / ITEM_FEED_COUNT);
        }
        return mainList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % ITEM_FEED_COUNT == 0) {
            return AD_VIEW;
        }
        return ITEM_VIEW;
    }

    private void populateNativeADView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        TransactionItemBinding binding;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = TransactionItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickInterface.clickItem(getAdapterPosition());
                }
            });
        }


        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        private void bindData(PointsWithdrawModel pointsWithdrawModel) {
            binding.amount.setText("" + Utilis.round2(pointsWithdrawModel.getAmount(), BALANCE_RANGE));
            binding.transactionId.setText(pointsWithdrawModel.getTransactionId());
            convert(pointsWithdrawModel.getDate());
            if (pointsWithdrawModel.getStatus().equals(PENDING)) {
                binding.status.setBackground(context.getResources().getDrawable(R.drawable.transaction_orange));
            } else if (pointsWithdrawModel.getStatus().equals(APPROVED)) {
                binding.status.setBackground(context.getResources().getDrawable(R.drawable.transaction_green));
            } else
                binding.status.setBackground(context.getResources().getDrawable(R.drawable.transaction_red));
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

    public class AdViewHolder extends RecyclerView.ViewHolder {

        LayoutAdBinding binding;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutAdBinding.bind(itemView);
        }

        private void bindAdData() {
            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.admob_native_ad_unit))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            NativeAdView nativeAdView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.layout_native_ad, null);
                            populateNativeADView(nativeAd, nativeAdView);
                            binding.adLayout.removeAllViews();
                            binding.adLayout.addView(nativeAdView);
                        }
                    });

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d("notification", "onAdFailedToLoad: " + loadAdError.getMessage());
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }
}

