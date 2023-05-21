package ru.pshiblo.luna.services.audio

import ru.pshiblo.luna.services.Service

interface MusicService : Service {
    fun play(track: String)
    fun skip()
    var volume: Int
    val playingTrack: String?
}