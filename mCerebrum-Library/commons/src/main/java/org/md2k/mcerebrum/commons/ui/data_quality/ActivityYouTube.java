/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.commons.ui.data_quality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.md2k.mcerebrum.commons.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Youtube Activity
 */
public class ActivityYouTube extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    FancyButton back;

    /** Request code constant for the recovery dialog. */
    private static final int RECOVERY_DIALOG_REQUEST = 10;
    private static final String API_KEY = "AIzaSyCxbfW38QiMdp5St96IhYUtDCSvDO8cNfA";
    private String VIDEO_ID = null;

    /**
     * Initializes the YouTube player.
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        String video_link = getIntent().getStringExtra("video_link");
        String title = getIntent().getStringExtra("title");
        ((TextView) findViewById(R.id.textview_title)).setText(title);
        VIDEO_ID = video_link;

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(API_KEY, this);
        back = (FancyButton) findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            /**
             * Finishes the activity.
             * @param view Android view.
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Asks the user to recover the error if possible.
     * @param provider The YouTube player content provider.
     * @param errorReason Reason the initialization failed.
     */
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("YouTube Error (%1$s)",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * If the activity was not restored, the video loads.
     * @param provider The YouTube player content provider.
     * @param player The YouTube player.
     * @param wasRestored Whether the activity was restored or not.
     */
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
            player.loadVideo(VIDEO_ID);
        }
    }

    /**
     * Initializes a YouTube player.
     * @param requestCode Request code for the activity result.
     * @param resultCode Result code for the activity result.
     * @param data Android intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    /**
     * Returns a <code>YouTubePlayer</code>.
     * @return A <code>YouTubePlayer</code>.
     */
    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    /**
     * Nested class for fullscreen video listener.
     */
    private class FullScreenListener implements YouTubePlayer.OnFullscreenListener{

        @Override
        public void onFullscreen(boolean isFullscreen) {
            //Called when fullscreen mode changes.
        }

    }

    /**
     * Nested class for a playback listener.
     */
    private class PlaybackListener implements YouTubePlayer.PlaybackEventListener{

        @Override
        public void onBuffering(boolean isBuffering) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to pause() or user action.
        }

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to play() or user action.
        }

        @Override
        public void onSeekTo(int newPositionMillis) {
            // Called when a jump in playback position occurs,
            //either due to the user scrubbing or a seek method being called
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
        }
    }

    /**
     * Nested class for a player state listener.
     */
    private class PlayerStateListener implements YouTubePlayer.PlayerStateChangeListener{

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason reason) {
            // Called when an error occurs.
        }

        @Override
        public void onLoaded(String arg0) {
            // Called when a video has finished loading.
        }

        @Override
        public void onLoading() {
            // Called when the player begins loading a video and is not ready to accept commands affecting playback
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }
    }

    /**
     * Nested class for a playlist listener.
     */
    private class PlayListListener implements YouTubePlayer.PlaylistEventListener{

        @Override
        public void onNext() {
            // Called before the player starts loading the next video in the playlist.
        }

        @Override
        public void onPlaylistEnded() {
            // Called when the last video in the playlist has ended.
        }

        @Override
        public void onPrevious() {
            // Called before the player starts loading the previous video in the playlist.
        }
    }
}
