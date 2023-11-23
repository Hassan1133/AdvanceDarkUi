package com.example.Activities;

import static com.example.utils.Constants.REF_TUTORIAL;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Models.TutorialModel;
import com.example.R;
import com.example.databinding.ActivityYoutubePlayerScreenBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class YoutubePlayerScreen extends AppCompatActivity {
    private static final String TAG = "YoutubePlayerScreen";
    ActivityYoutubePlayerScreenBinding binding;
    YouTubePlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYoutubePlayerScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(playerView);

        try {
            TutorialModel model = (TutorialModel) getIntent().getSerializableExtra(REF_TUTORIAL);
            if (model != null) {
                playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(model.getUrl(), 0);
                    }
                });
            }

        } catch (Exception e) {
        }
    }


}