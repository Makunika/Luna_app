package ru.pshiblo.services.audio.discord;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.MusicService;
import ru.pshiblo.services.ServiceThread;
import ru.pshiblo.services.ServiceType;

import javax.security.auth.login.LoginException;

public class DiscordMusicService extends ServiceThread implements MusicService {

    private DiscordListener listener;
    private JDA jda;


    @Override
    public void play(String track) {
        listener.play(track);
    }

    @Override
    public void skip() {
        listener.skip();
    }

    @Override
    public void volume(int volume) {
        listener.volume(volume);
    }

    @Override
    public String getPlayingTrack() {
        return listener.getPlayingTrack();
    }

    @Override
    protected void runInThread() throws Exception {
        try {
            listener = new DiscordListener();
            jda = JDABuilder.create(Config.getInstance().getTokenDiscord(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                    .addEventListeners(listener)
                    .setActivity(Activity.listening("aloha"))
                    .setStatus(OnlineStatus.ONLINE)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
            ConsoleOut.alert(e.getMessage());
            throw e;
        }
    }

    @Override
    public void shutdown() {
        listener.shutdown();
        listener = null;
        if (jda != null) {
            jda.shutdown();
        }
        super.shutdown();
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.MUSIC;
    }

    @Override
    public boolean isInitializer() {
        return super.isInitializer() && listener != null && listener.isInit();
    }
}
