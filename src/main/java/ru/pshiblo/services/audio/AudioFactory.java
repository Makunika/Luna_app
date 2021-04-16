package ru.pshiblo.services.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_BE;
import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.DISCORD_OPUS;

public class AudioFactory {

    private static AudioFactory INSTANCE;

    public static AudioFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AudioFactory();
        }
        return INSTANCE;
    }

    private final AudioPlayerManager playerManager;

    private AudioFactory() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public AudioPlayer createAudioPlayer() {
        return playerManager.createPlayer();
    }

    public void setDiscordConfig() {
        playerManager.getConfiguration().setOutputFormat(DISCORD_OPUS);
    }

    public void setLocalConfig() {
        playerManager.getConfiguration().setOutputFormat(COMMON_PCM_S16_BE);
    }

    public void loadItem(String track, AudioLoadResultHandler handler) {
        if (track.contains("http")) {
            playerManager.loadItem(track, handler);
        } else {
            playerManager.loadItem("ytsearch: " + track, handler);
        }
    }

    public void loadItemOrdered(Object o, String track, AudioLoadResultHandler handler) {
        if (track.contains("http")) {
            playerManager.loadItemOrdered(o, track, handler);
        } else {
            playerManager.loadItemOrdered(o, "ytsearch: " + track, handler);
        }
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}
