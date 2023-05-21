package ru.pshiblo.luna.services.audio

import com.google.inject.Inject
import com.google.inject.Singleton
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import ru.pshiblo.luna.services.broadcast.BroadcastServiceProvider

@Singleton
class MusicManager {

    @Inject
    private lateinit var broadcastServiceProvider: BroadcastServiceProvider

    val musicManager = DefaultAudioPlayerManager().apply {
        AudioSourceManagers.registerLocalSource(this)
        AudioSourceManagers.registerRemoteSources(this)
    }
    val musicPlayer: AudioPlayer = musicManager.createPlayer()
    val trackScheduler = TrackScheduler(musicPlayer)
    { track, _ ->
        broadcastServiceProvider.getBroadcastPostService().publish("Я не смог проиграть трек ${track.info.title}")
    }

    fun setLocalConfig() {
        musicManager.configuration.outputFormat = StandardAudioDataFormats.COMMON_PCM_S16_BE
    }

    fun setDiscordConfig() {
        musicManager.configuration.outputFormat = StandardAudioDataFormats.DISCORD_OPUS
    }

    fun loadTrack(track: String) {
        if (track.contains("http")) {
            musicManager.loadItemOrdered(musicPlayer, track, createResultHandler(track))
        } else {
            musicManager.loadItemOrdered(musicPlayer, "ytsearch: $track", createResultHandler(track))
        }
    }

    private fun createResultHandler(track: String) =
        FunctionalResultHandler(
            trackScheduler::queue,
            { playlist ->
                var firstTrack = playlist!!.selectedTrack
                if (firstTrack == null) {
                    firstTrack = playlist.tracks[0]
                }
                trackScheduler.queue(firstTrack)
            },
            {
                broadcastServiceProvider.getBroadcastPostService().publish("Трек $track не найден")
            },
            {
                broadcastServiceProvider.getBroadcastPostService().publish("Я не смог загрузить трек $track")
                it.printStackTrace()
            }
        )
}