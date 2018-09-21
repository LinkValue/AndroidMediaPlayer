package fr.linkvalue.notifmediaplayer

import android.support.annotation.IntegerRes
import java.lang.IllegalStateException

object MediaPlayerManager {

    const val rootID = "fr.linkvalue.notifmediaplayer"
    private var config: MediaPlayerConfig? = null

    fun updateConfig(config: MediaPlayerConfig) {
        this.config = config
    }

    @IntegerRes
    var displayIconId: Int = 0
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.displayIconId
        }

    @IntegerRes
    var smallIconId: Int = 0
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.smallIconId
        }

    @IntegerRes
    var albumArtId: Int = 0
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.smallIconId
        }

    @IntegerRes
    var artPicResId: Int = 0
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.artPicResId
        }

    var channelId: String = ""
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.channelId
        }

    var channelName: String = ""
        private set
        get() {
            return this.config!!.channelName
        }

    var playLabel: String = ""
        private set
        get() {
            return this.config!!.playLabel
        }

    var pauseLabel: String = ""
        private set
        get() {
            return this.config!!.pauseLabel
        }
    var title: String = ""
        private set
        get() {
            return this.config!!.notificationTitle
        }
    var subtitile: String = ""
        private set
        get() {
            return this.config!!.notificationSubtitle
        }

    @IntegerRes
    var playIconResId: Int = 0
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.playIconResId
        }

    @IntegerRes
    var stopIconResId: Int? = null
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.displayIconId
        }

    @IntegerRes
    var pauseIconResId: Int = 0
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.displayIconId
        }

    @IntegerRes
    var forwardIconResId: Int? = null
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.displayIconId
        }
    @IntegerRes
    var backwardIconResId: Int? = null
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.displayIconId
        }
    var filePath: String? = null
        private set
        get() {
            checkIfManagerIsReady()
            return this.config!!.filePath
        }

    private fun checkIfManagerIsReady() {
        if (config == null) {
            throw IllegalStateException("### MediaPlayerManager has no valid configuration. Did you call MediaPlayerManager.updateConfig(config: MediaPlayerConfig) ? ")
        }
    }
}

