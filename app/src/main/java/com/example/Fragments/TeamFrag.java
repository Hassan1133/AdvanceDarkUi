package com.example.Fragments;

import static com.example.utils.Constants.COUNTER;
import static com.example.utils.Constants.TEAM_FB;
import static com.example.utils.Constants.USER_FB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Activities.TeamDetailScreen;
import com.example.Adapters.TeamAdp;
import com.example.Models.User;
import com.example.R;
import com.example.utils.ClickInterface;
import com.example.utils.CustomProgressDialogue;
import com.example.utils.PrefManager;
import com.example.databinding.FragmentTeamBinding;

import java.util.LinkedList;
//
public class TeamFrag extends Fragment implements ClickInterface,BottomSheetFragment.BottomSheetListener, View.OnClickListener {
    private static final String TAG = "TeamFragment";
    FragmentTeamBinding binding;
    LinkedList<User> list;
    TeamAdp adp;
    int selectedPosition;
    PrefManager prefManager;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTeamBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        prefManager = new PrefManager(getContext());
        customProgressDialogue = new CustomProgressDialogue();
        //setTeams();
        binding.back.setOnClickListener(this);
        fetchTeam();

        return binding.getRoot();
    }


    @Override
    public void clickItem(int position) {
//        openBottomSheet(position);
//        selectedPosition = position;
        Intent intent = new Intent(getContext(), TeamDetailScreen.class);
        intent.putExtra(TEAM_FB, list.get(position));
        intent.putExtra(COUNTER, 1);
        startActivity(intent);
    }

    void openBottomSheet(int position) {
        BottomSheetFragment bottomFilterFragment = new BottomSheetFragment(list.get(position));
        bottomFilterFragment.show(getChildFragmentManager(), "bottomFilterFragment");
    }

    // bottom sheet listener
    @Override
    public void onClicked(String item) {
      //  TODO: to show bottom sheet for blance
//        if (item.equals("p")) {
//            Intent intent = new Intent(getContext(), TeamDetailScreen.class);
//            intent.putExtra(TEAM_FB, list.get(selectedPosition));
//            intent.putExtra(COUNTER, 0);
//            startActivity(intent);
//        } else
//            startActivity(new Intent(getContext(), SendBalanceScreen.class));
    }


    void fetchTeam() {
        customProgressDialogue.showCustomDialog(getContext());
        list = new LinkedList<>();
        database.collection(USER_FB).whereEqualTo("refId", prefManager.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    customProgressDialogue.hideCustomDialog();
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        Log.d(TAG, "onEvent: " + dc.getDocument().toObject(User.class).getName());
                        list.add(dc.getDocument().toObject(User.class));
                    }
                }
                adp = new TeamAdp(list, getContext(), TeamFrag.this);
                binding.teamRecycler.setAdapter(adp);
                customProgressDialogue.hideCustomDialog();
                binding.teamSize.setText("Team size: " + list.size());
                if (list.isEmpty()) {
                    binding.empty.setVisibility(View.VISIBLE);
                } else binding.empty.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back)
        {
            getActivity().onBackPressed();
        }
    }
}