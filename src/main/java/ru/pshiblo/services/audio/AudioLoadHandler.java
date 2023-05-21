package ru.pshiblo.services.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.broadcast.ChatPostService;

public class AudioLoadHandler implements AudioLoadResultHandler {

    private final String track;
    private final TrackScheduler scheduler;

    public AudioLoadHandler(String track, TrackScheduler scheduler) {
        this.track = track;
        this.scheduler = scheduler;
    }


    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        ConsoleOut.println("Трек " + audioTrack.getInfo().title + " загружен");
        scheduler.queue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        ConsoleOut.println("Треки " + playlist.getTracks().get(0).getInfo().title + " загружены");
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        scheduler.queue(firstTrack);
    }

    @Override
    public void noMatches() {
        ConsoleOut.println("Трек " + track + " не найден");
        ((ChatPostService) Context.getService(ServiceType.CHAT_POST)).postMessage("Трек " + track + " не найден");
    }

    @Override
    public void loadFailed(FriendlyException e) {
        ConsoleOut.println("Ошибка при загрузке трека:  " + e.getMessage());
    }
}
