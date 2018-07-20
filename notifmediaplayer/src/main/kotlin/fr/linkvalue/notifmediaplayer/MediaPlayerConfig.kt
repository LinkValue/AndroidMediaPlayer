package fr.linkvalue.notifmediaplayer

class MediaPlayerConfig(builder: ConfigBuilder) {

    private var appIconResId: Int? = null
    private var artPicResId: Int? = null
    private var channelID: String? = null
    private var channelName: String? = null

    init {
        this.appIconResId = builder.appIconResId
        this.artPicResId = builder.artPicResId
        this.channelID = builder.channelId
        this.channelName = builder.channelName
    }

    inner class ConfigBuilder {


        var appIconResId: Int? = null
            private set
        var artPicResId: Int? = null
            private set
        var channelId: String? = null
            private set
        var channelName: String? = null
            private set
        var playIconResId: Int? = null
            private set
        var stopIconResId: Int? = null
            private set
        var pauseIconResId: Int? = null
            private set
        var forwardIconResId: Int? = null
            private set
        var backwardIconResId: Int? = null
            private set


        fun build(): MediaPlayerConfig {
            return MediaPlayerConfig(this)
        }

        fun appIcon(iconResId: Int): ConfigBuilder {
            this.appIconResId = iconResId
            return this
        }

        fun artPicture(artResID: Int): ConfigBuilder {
            this.artPicResId = artResID
            return this
        }

        fun channelId(channelId: String): ConfigBuilder {
            this.channelId = channelId
            return this
        }

        fun channelName(channelName: String): ConfigBuilder {
            this.channelName = channelName
            return this
        }

        fun playIcon(iconResId: Int): ConfigBuilder {
            this.playIconResId = iconResId
            return this
        }

        fun stopIcon(iconResId: Int): ConfigBuilder {
            this.stopIconResId = iconResId
            return this
        }

        fun pauseIcon(iconResId: Int): ConfigBuilder {
            this.pauseIconResId = iconResId
            return this
        }

        fun forwardIcon(iconResId: Int): ConfigBuilder {
            this.forwardIconResId = iconResId
            return this
        }

        fun backwardIcon(iconResId: Int): ConfigBuilder {
            this.backwardIconResId = iconResId
            return this
        }


    }
}