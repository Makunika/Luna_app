package ru.pshiblo.luna.services.audio.local

import com.google.inject.Inject
import com.google.inject.Singleton
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import kotlinx.coroutines.yield
import ru.pshiblo.luna.services.ServiceThread
import ru.pshiblo.luna.services.audio.MusicManager
import ru.pshiblo.luna.services.audio.MusicService
import ru.pshiblo.luna.services.toast.ToastHolderService
import java.io.IOException
import javax.sound.sampled.*

@Singleton
class LocalMusicService : ServiceThread(), MusicService {

    @Inject
    private lateinit var musicManager: MusicManager
    @Inject
    private lateinit var toastHolderService: ToastHolderService

    override suspend fun runInThread() {
        try {
            musicManager.setLocalConfig()
            toastHolderService.showMessage("Локальная музыка инициализирована")

            val format = musicManager.musicManager.configuration.outputFormat
            val stream = AudioPlayerInputStream.createStream(
                musicManager.musicPlayer,
                format,
                10000L,
                true
            )
            val info = DataLine.Info(SourceDataLine::class.java, stream.format)
            val line = AudioSystem.getLine(info) as SourceDataLine
            line.open(stream.format)
            line.start()
            val buffer = ByteArray(StandardAudioDataFormats.COMMON_PCM_S16_BE.maximumChunkSize())
            var chunkSize: Int
            while (stream.read(buffer).also { chunkSize = it } >= 0) {
                yield()
                if (!musicManager.trackScheduler.queueIsEmpty()) {
                    line.write(buffer, 0, chunkSize)
                }
            }
        } catch (e: LineUnavailableException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

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
}