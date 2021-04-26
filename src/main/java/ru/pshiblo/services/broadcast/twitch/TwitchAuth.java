package ru.pshiblo.services.broadcast.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.google.api.services.oauth2.model.Userinfo;
import ru.pshiblo.Config;

import java.util.concurrent.atomic.AtomicBoolean;

public class TwitchAuth {
    private static TwitchClient twitchClient;

    public static TwitchClient getTwitchClient() {
        if (twitchClient == null)
            throw new IllegalStateException("not auth!");
        return twitchClient;
    }

    public static boolean auth() {
        if (twitchClient != null)
            twitchClient.close();

        AtomicBoolean check = new AtomicBoolean(false);
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();
        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                Config.getInstance().getTokenTwitch()
        );

        twitchClient = clientBuilder
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withEnableChat(true)
                /*
                 * Build the TwitchClient Instance
                 */
                .build();

        twitchClient.getEventManager().onEvent(IRCMessageEvent.class, event -> {
            check.set(true);
        });
        twitchClient.getChat().sendMessage("twitch4jdfdfdfdfdfdfasdasdwqesdwqdsadwq", "Hey!");
        try {
            Thread.sleep(2000);
            Userinfo userinfo = new Userinfo();
            userinfo.setName("Twitch");
            userinfo.setPicture("http://pngimg.com/uploads/twitch/small/twitch_PNG49.png");
            Config.getInstance().setUserinfo(userinfo);
            return check.get();
        }catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
