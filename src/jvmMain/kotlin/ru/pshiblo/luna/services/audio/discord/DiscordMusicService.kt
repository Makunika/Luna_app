package ru.pshiblo.luna.services.audio.discord

import com.google.inject.Inject
import com.google.inject.Singleton
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import ru.pshiblo.luna.services.Service
import ru.pshiblo.luna.services.audio.MusicManager
import ru.pshiblo.luna.services.audio.MusicService
import ru.pshiblo.luna.services.scope.DiscordScope
import ru.pshiblo.luna.services.toast.ToastHolderService
import java.nio.ByteBuffer

@Singleton
class DiscordMusicService : Service, MusicService {

    @Inject
    private lateinit var musicManager: MusicManager
    @Inject
    private lateinit var discordScope: DiscordScope
    @Inject
    private lateinit var toastHolderService: ToastHolderService
    private var _isInit = false

    override fun play(track: String) {
        if (isInit)
            musicManager.loadTrack(track)
    }

    override fun skip() {
        if (isInit)
            musicManager.trackScheduler.skipTrack()
    }

    override var volume: Int
        get() {
            return musicManager.musicPlayer.volume
        }
        set(value) {
            if (isInit)
                musicManager.musicPlayer.volume = value
        }

    override val playingTrack: String?
        get() = musicManager.musicPlayer.playingTrack?.info?.title

    override val isInit: Boolean
        get() = _isInit && discordScope.isAuth

    override fun start() {
        if (!discordScope.isAuth) {
            error("discord not auth")
        }
        discordScope.jda.listener<MessageReceivedEvent> {
            val guild = it.guild
            val channel = it.channel
            val message = it.message.contentDisplay
            if (message.startsWith("!connect ")) {
                if (isInit) {
                    channel.sendMessage("Бот уже инициализирован").queue()
                    return@listener
                }
                message.substringAfter("!connect ").let { voiceChannelStr ->
                    val voiceChannel = guild
                        .getVoiceChannelsByName(voiceChannelStr, true)
                        .getOrNull(0)
                    if (voiceChannel != null) {
                        val audioManager = voiceChannel.guild.audioManager
                        audioManager.openAudioConnection(voiceChannel)
                        musicManager.setDiscordConfig()
                        audioManager.sendingHandler = AudioPlayerSendHandler(musicManager.musicPlayer)
                        channel.sendMessage("Я готов!").queue()
                        _isInit = true
                        toastHolderService.showMessage("Discord инициализирован в канале $voiceChannelStr")
                    } else {
                        channel.sendMessage("Такого канала не существует").queue()
                    }
                }
            }
        }
    }

    override fun shutdown() {
        discordScope.exit()
        _isInit = false
    }
}


class AudioPlayerSendHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {
    private var lastFrame: AudioFrame? = null

    override fun canProvide(): Boolean {
        lastFrame = audioPlayer.provide()
        return lastFrame != null
    }

    override fun provide20MsAudio(): ByteBuffer? {
        return ByteBuffer.wrap(lastFrame!!.data)
    }

    override fun isOpus(): Boolean {
        return true
    }
}
