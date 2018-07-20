package fr.linkvalue.notifmediaplayer

class MediaPlayerManager private constructor() {

    companion object {
        private var instance: MediaPlayerManager? = null
        fun getInstance(): MediaPlayerManager {
            if (instance == null) {
                throw IllegalStateException("MediaPlayerManager has not been initialized. You must call MediaplayerManager.init() before using it !")
            }
            return instance!!
        }
    }

    private lateinit var config: MediaPlayerConfig

    var appiCon

}