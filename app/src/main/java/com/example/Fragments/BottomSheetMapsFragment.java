package com.example.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.Models.AreaModel;
import com.example.R;
import com.example.databinding.MapsBottomSheetFragmentBinding;

public class BottomSheetMapsFragment extends BottomSheetDialogFragment implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private MapsBottomSheetFragmentBinding binding;
    private AreaModel areaModel;
    private SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_bottom_sheet_fragment, container, false);
        binding = MapsBottomSheetFragmentBinding.bind(view);
        init();
        return view;
    }

    private void init() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.bottomSheetMap);
        mapFragment.getMapAsync(this);
        if (getArguments() != null) {
            areaModel = (AreaModel) getArguments().getSerializable("location");
        }
        binding.closeBtn.setOnClickListener(this);
        binding.title.setText(areaModel.getTitle());
        binding.description.setText(areaModel.getDescription());
        binding.address.setText(areaModel.getAddress());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // You already have areaModel from the Bundle
        // Add a marker and move the camera
        Log.d(TAG, "onMapReady: " + areaModel.getLat());
        LatLng location = new LatLng(areaModel.getLat(), areaModel.getLng());
        mMap.addMarker(new MarkerOptions().position(location).title(areaModel.getTitle()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeBtn:
                dismiss();
                break;
        }
    }
}