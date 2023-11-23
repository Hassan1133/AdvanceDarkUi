package com.example.Activities;

import static com.example.utils.Constants.ACTIVE_PLANS_FB;
import static com.example.utils.Constants.PLANS_FB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Adapters.PlansAdp;
import com.example.Models.PlanPaymentRequest;
import com.example.Models.PlansModel;
import com.example.R;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.databinding.ActivityPlansScreenBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class PlansScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PlansScreen";
    ActivityPlansScreenBinding binding;
    LinkedList<PlansModel> plansList;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    int pos = 0;
    PrefManager prefManager;
    PlanPaymentRequest activePlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlansScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        checkActivePlan();
        setPlans();

    }


    void init() {
        database = FirebaseFirestore.getInstance();
        binding.back.setOnClickListener(this);
        binding.nextBtn.setOnClickListener(this);
        binding.changePlan.setOnClickListener(this);
        customProgressDialogue = new CustomProgressDialogue();
        prefManager = new PrefManager(this);
    }

    void setPlans() {
        plansList = new LinkedList<>();
        database.collection(PLANS_FB).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    customProgressDialogue.hideCustomDialog();
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        plansList.add(dc.getDocument().toObject(PlansModel.class));
                    }
                }
                Collections.sort(plansList, new Comparator<PlansModel>() {
                    public int compare(PlansModel obj1, PlansModel obj2) {
                        return Integer.valueOf(obj1.getPrice()).compareTo(Integer.valueOf(obj2.getPrice())); // To compare integer values

                    }
                });
                setViewPager(plansList);
                customProgressDialogue.hideCustomDialog();
            }
        });
    }


    void setViewPager(LinkedList<PlansModel> sliderItems) {
        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setClipChildren(false);

        if (sliderItems.size() >= 2) {
            binding.viewPager.setOffscreenPageLimit(sliderItems.size() - 1);
        } else binding.viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);

        binding.viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));

        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                binding.priceBtn.setText("PKR: " + sliderItems.get(position).getPrice());
                pos = position;
            }
        });
        binding.viewPager.setPageTransformer(compositePageTransformer);
        binding.viewPager.setAdapter(new PlansAdp(this, sliderItems, binding.viewPager));
        binding.dotsIndicator.attachTo(binding.viewPager);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.nextBtn) {
            Intent intent = new Intent(PlansScreen.this, PlansDetailScreen.class);
            intent.putExtra(PLANS_FB, plansList.get(pos));
            startActivity(intent);
        } else if (id == R.id.changePlan) {
            showPlans();
        }
    }

    void showPlans() {

        binding.notActive.setVisibility(View.VISIBLE);
        binding.nextBtn.setVisibility(View.VISIBLE);
        binding.changePlan.setVisibility(View.GONE);
        binding.active.setVisibility(View.GONE);
    }

    void checkActivePlan() {
        customProgressDialogue.showCustomDialog(this);
        database.collection(ACTIVE_PLANS_FB).document(prefManager.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(PlansScreen.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + error.getMessage());
                    customProgressDialogue.hideCustomDialog();
                    return;
                }

                assert value != null;
                activePlan = value.toObject(PlanPaymentRequest.class);
                if (activePlan != null) {
                    Log.d(TAG, "not null: ");
                    setActivePlanDetails(activePlan);
                } else showPlans();
                customProgressDialogue.hideCustomDialog();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void setActivePlanDetails(PlanPaymentRequest activePlan)
    {
        binding.notActive.setVisibility(View.GONE);
        binding.nextBtn.setVisibility(View.GONE);
        binding.active.setVisibility(View.VISIBLE);
        binding.changePlan.setVisibility(View.VISIBLE);
        binding.title.setText(activePlan.getTitle());
        binding.speed.setText(activePlan.getSpeed() + "X speed");
        binding.description.setText(activePlan.getDescription());
        binding.activePrice.setText("PKR: " + activePlan.getAmount());
        customProgressDialogue.hideCustomDialog();
    }

}