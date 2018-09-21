package fr.linkvalue.notifmediaplayer

import android.content.Context

class MediaPlayerConfig(context: Context) {

    companion object {
        const val DEFAULT_CHANNEL_ID = "defaultChannelID"
        const val DEFAULT_CHANNEL_NAME = "Sound Player"
    }

    var displayIconId: Int = R.drawable.ic_default_display_notification_icon
        private set
    var smallIconId: Int = R.drawable.ic_default_display_notification_icon
        private set
    var albumArtId: Int = R.drawable.ic_default_display_notification_icon
        private set
    var artId: Int = R.drawable.ic_default_display_notification_icon
        private set
    var artPicResId: Int = R.drawable.ic_default_display_notification_icon
        private set
    var playIconResId: Int = R.drawable.ic_play_arrow
        private set
    var stopIconResId: Int = R.drawable.ic_stop
        private set
    var pauseIconResId: Int = R.drawable.ic_pause
        private set
    var forwardIconResId: Int = R.drawable.ic_skip_next
        private set
    var backwardIconResId: Int = R.drawable.ic_skip_previous
        private set
    var channelId: String = DEFAULT_CHANNEL_ID
        private set
    var channelName: String = DEFAULT_CHANNEL_NAME
        private set
    var playLabel: String = context.getString(R.string.lv_player_play_label)
        private set
    var pauseLabel: String = context.getString(R.string.lv_player_pause_label)
        private set
    var notificationTitle: String = ""
        private set
    var notificationSubtitle: String = ""
        private set

    var filePath: String? = null
        private set


    fun displayIcon(iconResId: Int): MediaPlayerConfig {
        this.displayIconId = iconResId
        return this
    }

    fun smallIcon(iconResId: Int): MediaPlayerConfig {
        this.smallIconId = iconResId
        return this
    }

    fun artPicture(artResID: Int): MediaPlayerConfig {
        this.artPicResId = artResID
        return this
    }

    fun albumArt(albumArtId: Int): MediaPlayerConfig {
        this.albumArtId = albumArtId
        return this
    }

    fun channelId(channelId: String): MediaPlayerConfig {
        this.channelId = channelId
        return this
    }

    fun channelName(channelName: String): MediaPlayerConfig {
        this.channelName = channelName
        return this
    }
    fun notificationTitle(title: String): MediaPlayerConfig {
        this.notificationTitle = title
        return this
    }
    fun notificationSubtitle(subtitle: String): MediaPlayerConfig {
        this.notificationSubtitle = subtitle
        return this
    }

    fun playLabel(playLabel: String): MediaPlayerConfig {
        this.playLabel = playLabel
        return this
    }

    fun pauseLabel(pauseLabel: String): MediaPlayerConfig {
        this.pauseLabel = pauseLabel
        return this
    }

    fun playIcon(iconResId: Int): MediaPlayerConfig {
        this.playIconResId = iconResId
        return this
    }

    fun stopIcon(iconResId: Int): MediaPlayerConfig {
        this.stopIconResId = iconResId
        return this
    }

    fun pauseIcon(iconResId: Int): MediaPlayerConfig {
        this.pauseIconResId = iconResId
        return this
    }

    fun forwardIcon(iconResId: Int): MediaPlayerConfig {
        this.forwardIconResId = iconResId
        return this
    }

    fun backwardIcon(iconResId: Int): MediaPlayerConfig {
        this.backwardIconResId = iconResId
        return this
    }

    fun filePath(path: String): MediaPlayerConfig {
        this.filePath = path
        return this
    }



}