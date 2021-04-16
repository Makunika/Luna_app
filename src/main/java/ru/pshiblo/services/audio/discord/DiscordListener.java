package ru.pshiblo.services.audio.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import ru.pshiblo.Config;
import ru.pshiblo.services.audio.AudioFactory;
import ru.pshiblo.services.audio.AudioLoadHandler;
import ru.pshiblo.services.audio.TrackScheduler;

import java.util.List;

public class DiscordListener extends ListenerAdapter {

    private boolean isInit;
    private MessageChannel messageChannel;
    private final AudioPlayer player;
    private final TrackScheduler scheduler;

    public DiscordListener() {
        player = AudioFactory.getInstance().createAudioPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!isInit) {
            Message msg = event.getMessage();
            Guild guild = event.getGuild();

            if (msg.getContentRaw().startsWith("!connect ")) {
                String arg = msg.getContentRaw().substring("!connect ".length());
                List<VoiceChannel> channels = guild.getVoiceChannelsByName(arg, true);
                if (!channels.isEmpty()) {
                    VoiceChannel channel = channels.get(0);
                    AudioManager audioManager = channel.getGuild().getAudioManager();
                    audioManager.openAudioConnection(channel);

                    audioManager.setSendingHandler(new AudioPlayerSendHandler(player));

                    messageChannel = event.getChannel();

                    isInit = true;
                } else {
                    event.getChannel().sendMessage("Такого канала не существует").queue();
                }
            }
        } else {
            event.getChannel().sendMessage("Бот уже инициализирован").queue();
        }
    }

    public void play(String track) {
        if (isInit) {
            AudioFactory.getInstance().loadItemOrdered(player, track, new AudioLoadHandler(track, scheduler));
        }
    }

    public void skip() {
        if (isInit) {
            scheduler.skipTrack();
        }
    }

    public void volume(int volume) {
        player.setVolume(volume);
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public boolean isInit() {
        return isInit;
    }

    public String getPlayingTrack() {
        AudioTrack playingTrack = player.getPlayingTrack();

        if (playingTrack == null)
            return null;

        return playingTrack.getInfo().title;
    }

    public void shutdown() {
        player.destroy();
        isInit = false;
    }
}
