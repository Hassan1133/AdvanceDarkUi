package com.example.Activities;

import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.NOTIFICATION_FB;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.Adapters.NotificationsAdp;
import com.example.Models.Notifications;
import com.example.utils.CustomProgressDialogue;
import com.example.databinding.ActivityNotificationsScreenBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

public class NotificationsScreen extends AppCompatActivity {
    private static final String TAG = "NotificationsScreen";
ActivityNotificationsScreenBinding binding;
    FirebaseFirestore database;
    CustomProgressDialogue customProgressDialogue;
    LinkedList<Notifications> list;
    NotificationsAdp adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        fetchNotifications();
    }

    void init() {
        database = FirebaseFirestore.getInstance();
        customProgressDialogue = new CustomProgressDialogue();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void fetchNotifications() {
        list = new LinkedList<>();
        customProgressDialogue.showCustomDialog(this);
        database.collection(NOTIFICATION_FB).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    customProgressDialogue.hideCustomDialog();
                    Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                assert value != null;
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        list.add(dc.getDocument().toObject(Notifications.class));
                    }
                }
                if (list.isEmpty()) binding.empty.setVisibility(View.VISIBLE);
                else {
                    Collections.sort(list, new Comparator<Notifications>() {
                        @SuppressLint("SimpleDateFormat")
                        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                        public int compare(Notifications obj1, Notifications obj2) {
                            try {
                                return Objects.requireNonNull(dateFormat.parse(obj2.getDate())).compareTo(dateFormat.parse(obj1.getDate()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    binding.empty.setVisibility(View.GONE);
                }
                adp = new NotificationsAdp(NotificationsScreen.this, list);
                binding.notiRecycler.setAdapter(adp);
                customProgressDialogue.hideCustomDialog();
            }
        });
    }
}
