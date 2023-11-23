package com.example.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.Models.AreaModel;
import com.example.R;

import java.util.ArrayList;

public class MapsFragment extends Fragment {
    private static final String TAG = "MapsFragment";
    Activity activity;
    ArrayList<AreaModel> arrayList;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (arrayList != null) {
                for (AreaModel areaModel : arrayList) {
                    LatLng sydney = new LatLng(areaModel.getLat(), areaModel.getLng());
                    googleMap.addMarker(new MarkerOptions().position(sydney).title(areaModel.getTitle()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 8.0f));
                }
            }

        }
    };

    public MapsFragment() {
    }

    public MapsFragment(Activity activity, ArrayList<AreaModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}