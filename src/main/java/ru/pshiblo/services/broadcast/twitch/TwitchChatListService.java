package ru.pshiblo.services.broadcast.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.api.domain.IDisposable;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import ru.pshiblo.Config;
import ru.pshiblo.services.*;
import ru.pshiblo.services.broadcast.ChatListService;
import ru.pshiblo.services.broadcast.listener.BroadcastHelloCommand;
import ru.pshiblo.services.broadcast.listener.BroadcastTrackCommand;
import ru.pshiblo.services.broadcast.listener.BroadcastUpdatedCommand;
import ru.pshiblo.services.broadcast.listener.base.BroadcastListener;
import ru.pshiblo.services.broadcast.listener.base.BroadcastMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TwitchChatListService implements ChatListService {

    private final TwitchClient twitchClient;
    private final List<BroadcastListener> broadcastListenerList;
    private boolean isInit;

    public TwitchChatListService() {
        broadcastListenerList = new ArrayList<>();
        twitchClient = TwitchAuth.getTwitchClient();
        isInit = false;
        //обычные команды
        this.subscribe(new BroadcastTrackCommand());
        this.subscribe(new BroadcastHelloCommand());
        this.subscribe(new BroadcastUpdatedCommand());
    }

    @Override
    public void start() {
        if (!twitchClient.getChat().isChannelJoined(Config.getInstance().getTwitchChannelName())) {
            twitchClient.getChat().joinChannel(Config.getInstance().getTwitchChannelName());
        }
        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        eventHandler.onEvent(ChannelMessageEvent.class, event -> {
            System.out.println(event.getMessage());
            for (BroadcastListener broadcastListener : broadcastListenerList) {
                broadcastListener.handleSingle(new BroadcastMessage(event.getMessage(), new Date()));
            }
        });
        isInit = true;
    }

    @Override
    public void shutdown() {
        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
        eventHandler.getConsumerBasedHandlers().clear();
        isInit = false;
    }

    @Override
    public void subscribe(BroadcastListener listener) {
        broadcastListenerList.add(listener);
    }

    @Override
    public void unsubscribe(BroadcastListener listener) {
        broadcastListenerList.remove(listener);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CHAT_LIST;
    }

    @Override
    public boolean isInitializer() {
        return isInit;
    }
}
