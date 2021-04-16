package ru.pshiblo.services.audio.local;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ru.pshiblo.services.MusicService;
import ru.pshiblo.services.ServiceThread;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.audio.AudioFactory;
import ru.pshiblo.services.audio.AudioLoadHandler;
import ru.pshiblo.services.audio.TrackScheduler;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_BE;

public class LocalMusicService extends ServiceThread implements MusicService {

    private final AudioPlayer player;
    private final TrackScheduler scheduler;

    public LocalMusicService() {
        AudioFactory.getInstance().setLocalConfig();
        player = AudioFactory.getInstance().createAudioPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    @Override
    protected void runInThread() {
        try {

            AudioDataFormat format = AudioFactory.getInstance().getPlayerManager().getConfiguration().getOutputFormat();
            AudioInputStream stream = AudioPlayerInputStream.createStream(player, format, 10000L, false);
            SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, stream.getFormat());
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(stream.getFormat());
            line.start();

            byte[] buffer = new byte[COMMON_PCM_S16_BE.maximumChunkSize()];
            int chunkSize;

            while ((chunkSize = stream.read(buffer)) >= 0) {
                if (!scheduler.queueIsEmpty()) {
                    line.write(buffer, 0, chunkSize);
                }
            }
        }catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void play(String track) {
        AudioFactory.getInstance().loadItemOrdered(player, track, new AudioLoadHandler(track, scheduler));
    }

    @Override
    public void skip() {
        scheduler.skipTrack();
    }

    @Override
    public void volume(int volume) {
        player.setVolume(volume);
    }

    @Override
    public String getPlayingTrack() {
        AudioTrack playingTrack = player.getPlayingTrack();

        if (playingTrack == null)
            return null;

        return playingTrack.getInfo().title;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.MUSIC;
    }

    @Override
    public void shutdown() {
        player.destroy();
        super.shutdown();
    }
}
