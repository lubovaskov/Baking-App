package com.udacity.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.udacity.bakingapp.model.RecipeStep;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepDetailFragment extends Fragment implements Player.EventListener {

    public static final String RECIPE_STEP_ARGUMENT_NAME = "recipe_step";
    private static final String MEDIA_SESSION_TAG = "RecipeStepMediaSession";
    private static final String USERAGENT_APPLICATION_NAME = "BakingApp";

    private RecipeStep recipeStep;

    private Unbinder unbinder;

    private Context mContext;

    @BindView(R.id.player_recipe_step_video)
    PlayerView playerView;
    @BindView(R.id.image_view_recipe_step_image)
    ImageView ivThumbnail;
    @BindView(R.id.textview_recipestep_description)
    TextView tvDescription;
    @BindBool(R.bool.use_two_pane)
    boolean useTwoPane;

    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(RECIPE_STEP_ARGUMENT_NAME)) {
            recipeStep = getArguments().getParcelable(RECIPE_STEP_ARGUMENT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView =
                inflater.inflate(R.layout.fragment_recipestep_detail, container, false);

        mContext = getActivity();

        //initialize butterknife bindings
        unbinder = ButterKnife.bind(this, fragmentView);

        if (recipeStep != null) {
            if (recipeStep.videoURL != null && !recipeStep.videoURL.isEmpty()) {
                setViewVisibility(ivThumbnail, false);

                //initialize ExoPlayer
                initializeMediaSession();
                initializePlayer(Uri.parse(recipeStep.videoURL));

                int orientation = getResources().getConfiguration().orientation;

                if (orientation == Configuration.ORIENTATION_LANDSCAPE && !useTwoPane) {
                    fullscreenVideo(playerView);
                    hideSystemUI();
                }
                setViewVisibility(playerView, true);
            } else {
                setViewVisibility(playerView, false);
                setViewVisibility(ivThumbnail, true);
                if (recipeStep.thumbnailURL != null && !recipeStep.thumbnailURL.isEmpty()) {
                    Picasso.with(mContext)
                        .load(recipeStep.thumbnailURL)
                        .error(R.drawable.image_placeholder)
                        .placeholder(R.drawable.image_placeholder)
                        .into(ivThumbnail);
                } else {
                    Picasso.with(mContext)
                        .load(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .placeholder(R.drawable.image_placeholder)
                        .into(ivThumbnail);
                }
            }

            tvDescription.setText(recipeStep.description);
        }

        return fragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //release butterknife bindings
        unbinder.unbind();
        mContext = null;
    }

    private void setViewVisibility(View view, boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void initializePlayer(Uri uri) {
        if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            playerView.setPlayer(exoPlayer);
            exoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getActivity(), USERAGENT_APPLICATION_NAME);
            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(getActivity(), userAgent);

            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);

            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }

        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
    }

    private void initializeMediaSession() {

        mediaSession = new MediaSessionCompat(mContext, MEDIA_SESSION_TAG);

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setMediaButtonReceiver(null);
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                exoPlayer.setPlayWhenReady(true);
            }

            @Override
            public void onPause() {
                exoPlayer.setPlayWhenReady(false);
            }

            @Override
            public void onSkipToPrevious() {
                exoPlayer.seekTo(0);
            }
        });
        mediaSession.setActive(true);
    }


    private void fullscreenVideo(PlayerView exoPlayer) {
        exoPlayer.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        exoPlayer.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private void hideSystemUI() {
        int vis = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            vis = vis | View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        getActivity().getWindow().getDecorView().setSystemUiVisibility(vis);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, exoPlayer.getCurrentPosition(),
                    1f);
        } else if ((playbackState == Player.STATE_READY)) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, exoPlayer.getCurrentPosition(),
                    1f);
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }
}
