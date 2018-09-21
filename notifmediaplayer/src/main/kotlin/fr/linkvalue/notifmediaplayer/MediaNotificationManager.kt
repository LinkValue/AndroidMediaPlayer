/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yourewelcome.smith.feature.relax

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.RemoteException
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle

import android.util.Log
import fr.linkvalue.notifmediaplayer.MediaPlayerManager

/**
 * Keeps track of a notification and updates it automatically for a given
 * MediaSession. Maintaining a visible notification (usually) guarantees that the music service
 * won't be killed during playback.
 */
class MediaNotificationManager @Throws(RemoteException::class)
constructor(private val ywMusicService: MusicService) : BroadcastReceiver() {
    private var sessionToken: MediaSessionCompat.Token? = null
    private var controller: MediaControllerCompat? = null
    private var mTransportControls: MediaControllerCompat.TransportControls? = null

    private var mPlaybackState: PlaybackStateCompat? = null
    private var mMetadata: MediaMetadataCompat? = null

    private lateinit var notificationManager: NotificationManager

    private val playIntent: PendingIntent
    private val pauseIntent: PendingIntent
    private val stopIntent: PendingIntent

    private var mStarted = false

    private val mediacontrollerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            mPlaybackState = state
            if (state.state == PlaybackStateCompat.STATE_STOPPED || state.state == PlaybackStateCompat.STATE_NONE) {
                stopNotification()
            } else {
                createNotification().let {
                    notificationManager.notify(NOTIFICATION_ID, it)
                }
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            try {
                updateSessionToken()
            } catch (e: RemoteException) {
                Log.e(TAG, "could not connect media controller + ${e.message}")
            }

        }
    }

    init {
        updateSessionToken()

        notificationManager = ywMusicService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pkg = ywMusicService.packageName
        pauseIntent = PendingIntent.getBroadcast(ywMusicService, REQUEST_CODE,
                Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)
        playIntent = PendingIntent.getBroadcast(ywMusicService, REQUEST_CODE,
                Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)
        stopIntent = PendingIntent.getBroadcast(ywMusicService, REQUEST_CODE,
                Intent(ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll()
    }

    /**
     * Posts the notification and starts tracking the session to keep it
     * updated. The notification will automatically be removed if the session is
     * destroyed before [.stopNotification] is called.
     */
    fun displayNotification() {
        if (!mStarted) {
            mMetadata = controller!!.metadata
            mPlaybackState = controller!!.playbackState

            // The notification must be updated after setting started to true
            val notification = createNotification()
            if (notification != null) {
                controller!!.registerCallback(mediacontrollerCallback)
                val filter = IntentFilter()
                filter.addAction(ACTION_PAUSE)
                filter.addAction(ACTION_PLAY)
                ywMusicService.registerReceiver(this, filter)

                ywMusicService.startForeground(NOTIFICATION_ID, notification)
                mStarted = true
            }
        }
    }

    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    fun stopNotification() {
        if (mStarted) {
            mStarted = false
            controller!!.unregisterCallback(mediacontrollerCallback)
            try {
                notificationManager.cancel(NOTIFICATION_ID)
                ywMusicService.unregisterReceiver(this)
            } catch (ex: IllegalArgumentException) {
                // ignore if the receiver is not registered.
            }

            ywMusicService.stopForeground(true)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            ACTION_PAUSE -> mTransportControls!!.pause()
            ACTION_PLAY -> mTransportControls!!.play()
        }
    }

    /**
     * Update the state based on a change on the session token. Called either when
     * we are running for the first time or when the media session owner has destroyed the session
     * (see [android.media.session.MediaController.Callback.onSessionDestroyed])
     */
    @Throws(RemoteException::class)
    private fun updateSessionToken() {
        val freshToken = ywMusicService.sessionToken
        if (sessionToken == null && freshToken != null || sessionToken != null && sessionToken != freshToken) {
            if (controller != null) {
                controller!!.unregisterCallback(mediacontrollerCallback)
            }
            sessionToken = freshToken
            if (sessionToken != null) {
                controller = MediaControllerCompat(ywMusicService, sessionToken!!)
                mTransportControls = controller!!.transportControls
                if (mStarted) {
                    controller!!.registerCallback(mediacontrollerCallback)
                }
            }
        }
    }

    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(ywMusicService, MusicActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        return PendingIntent.getActivity(ywMusicService, REQUEST_CODE, openUI,
                PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun createNotification(): Notification? {
        if (mMetadata == null || mPlaybackState == null) {
            return null
        }

        val description = mMetadata!!.description
        val art = BitmapFactory.decodeResource(ywMusicService.resources, MediaPlayerManager.displayIconId)

        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(ywMusicService, MediaPlayerManager.channelId)

        val playPauseButtonPosition = addActions(notificationBuilder)
        notificationBuilder
                .setStyle(MediaStyle()
                        // show only play/pause in compact view
                        .setShowActionsInCompactView(playPauseButtonPosition)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopIntent)
                        .setMediaSession(sessionToken))
                .setDeleteIntent(stopIntent)
                .setSmallIcon(MediaPlayerManager.smallIconId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(createContentIntent())
                .setContentTitle(description.title)
                .setContentText(description.subtitle)
                .setLargeIcon(art)
        setNotificationPlaybackState(notificationBuilder)

        return notificationBuilder.build()
    }

    private fun addActions(notificationBuilder: NotificationCompat.Builder): Int {
        val playPauseButtonPosition = 0

        // Play or pause button, depending on the current state.
        val label: String
        val icon: Int
        val intent: PendingIntent
        if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING) {
            label = MediaPlayerManager.pauseLabel
            icon = MediaPlayerManager.pauseIconResId
            intent = pauseIntent
        } else {
            label = MediaPlayerManager.playLabel
            icon = MediaPlayerManager.playIconResId
            intent = playIntent
        }
        notificationBuilder.addAction(NotificationCompat.Action(icon, label, intent))

        return playPauseButtonPosition
    }

    private fun setNotificationPlaybackState(builder: NotificationCompat.Builder) {
        if (mPlaybackState == null || !mStarted) {
            ywMusicService.stopForeground(true)
            return
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING)
    }


    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(MediaPlayerManager.channelId) == null) {
            val notificationChannel = NotificationChannel(MediaPlayerManager.channelId, MediaPlayerManager.channelName,
                    NotificationManager.IMPORTANCE_LOW)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private val TAG = MediaNotificationManager::class.java.canonicalName

        private const val NOTIFICATION_ID = 412
        private const val REQUEST_CODE = 100

        private const val ACTION_PAUSE = "fr.linkvalue.mediaplayer.pause"
        private const val ACTION_PLAY = "fr.linkvalue.mediaplayer.play"
        private const val ACTION_STOP = "fr.linkvalue.mediaplayer.stop"

    }
}
