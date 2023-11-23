package com.example.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.Models.AreaModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Adapters.locationAdapter;
import com.example.Fragments.MapsFragment;
import com.example.R;
import com.example.databinding.ActivityShowPoinsScreenBinding;

import java.util.ArrayList;

public class ShowPoinsScreen extends AppCompatActivity {
    private static final String TAG = "ShowPoinsScreen";
    private ActivityShowPoinsScreenBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<AreaModel> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowPoinsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initiate();

        getLocationList();

    }

    private void getLocationList() {
        firebaseFirestore.collection("location").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AreaModel areaModel = document.toObject(AreaModel.class);
                                arrayList.add(areaModel);
                            }

                            if (arrayList != null) {
                                Log.d(TAG, "onComplete: " + arrayList.size());
                                binding.recLocation.setAdapter(new locationAdapter(ShowPoinsScreen.this, arrayList));
                                loadFragment(new MapsFragment(ShowPoinsScreen.this, arrayList));
                            } else {
                                Toast.makeText(ShowPoinsScreen.this, "null", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    private void initiate() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
    }

    // method to load fragment
    void loadFragment(Fragment fragment) {

        if (fragment != null) {
            /// loading fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapsLayout, fragment)
                    .commit();
        }
    }

}