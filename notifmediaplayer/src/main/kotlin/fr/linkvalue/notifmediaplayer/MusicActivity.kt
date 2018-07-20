package com.yourewelcome.smith.feature.relax

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.yourewelcome.smith.R
import com.yourewelcome.smith.feature.base.YWBaseActivity
import fr.linkvalue.notifmediaplayer.R
import kotlinx.android.synthetic.main.activity_relax.*

abstract class MusicActivity : AppCompatActivity() {

    companion object {
        private val TAG = this::class.java.canonicalName
    }

    protected enum class PlayerState {
        STOPPED,
        PAUSED,
        PLAYING,
        UNKNOWN
    }

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            updatePlaybackState(state)
        }
    }

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            try {
                connectToSession(mediaBrowser.sessionToken)
            } catch (e: RemoteException) {
                Log.e(TAG, "could not connect media controller " + e.message)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaBrowser = MediaBrowserCompat(this, ComponentName(this, YWMusicService::class.java),
                connectionCallback, null)
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        val controllerCompat = MediaControllerCompat.getMediaController(this)
        controllerCompat?.unregisterCallback(mediaControllerCallback)
        mediaBrowser.disconnect()
    }

    @Throws(RemoteException::class)
    private fun connectToSession(token: MediaSessionCompat.Token) {
        val mediaController = MediaControllerCompat(
                this@MusicActivity, token)
        if (mediaController.metadata == null) {
            finish()
            return
        }
        MediaControllerCompat.setMediaController(this@MusicActivity, mediaController)
        mediaController.registerCallback(mediaControllerCallback)
        val state = mediaController.playbackState
        updatePlaybackState(state)

    }

    private fun updatePlaybackState(state: PlaybackStateCompat?) {
        if (state == null) {
            return
        }
        when (state.state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                onPlayerStateChanged(PlayerState.PLAYING)
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                onPlayerStateChanged(PlayerState.PAUSED)
            }
            PlaybackStateCompat.STATE_NONE,
            PlaybackStateCompat.STATE_STOPPED -> {
                onPlayerStateChanged(PlayerState.STOPPED)
            }
        }
    }

    protected fun togglePlayPause(): PlayerState {
        with(MediaControllerCompat.getMediaController(this)) {
            return when (playbackState?.state) {
                PlaybackStateCompat.STATE_PAUSED,
                PlaybackStateCompat.STATE_STOPPED,
                null -> {
                    transportControls.play()
                    PlayerState.PLAYING
                }

                PlaybackStateCompat.STATE_PLAYING -> {
                    if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                        transportControls.pause()

                    }
                    PlayerState.PAUSED
                }
                else -> {
                    PlayerState.UNKNOWN
                }
            }
        }
    }

    protected abstract fun onPlayerStateChanged(playerState: PlayerState)
}
