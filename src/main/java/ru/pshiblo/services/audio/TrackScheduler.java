package ru.pshiblo.services.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {

    private final Queue<AudioTrack> tracks;
    private final AudioPlayer player;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.tracks = new ArrayDeque<>();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        try {
            ConsoleOut.println("Гугл хром замьютен, играет : " + track.getInfo().title);
            Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Mute Google");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (!queueIsEmpty()) {
            nextTrack();
        } else {
            try {
                ConsoleOut.println("Гугл хром размьютен");
                Runtime.getRuntime().exec(Config.getInstance().getPath() + "\\SoundVolumeView.exe /Unmute Google");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    private void nextTrack() {
        player.playTrack(tracks.poll());
        //player.startTrack(tracks.poll(), false);
    }

    public void skipTrack() {
        player.stopTrack();
    }

    public void queue(AudioTrack track) {
        if (queueIsEmpty()) {
            tracks.offer(track);
            nextTrack();
        } else {
            tracks.offer(track);
        }
//        if (!player.startTrack(track, true)) {
//            tracks.offer(track);
//        }
    }

    public boolean queueIsEmpty() {
        return tracks.isEmpty() && player.getPlayingTrack() == null;
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        ConsoleOut.println("exp : " + exception.getMessage());
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        ConsoleOut.println("stuck");
    }
}
