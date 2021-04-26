package ru.pshiblo.services.broadcast.twitch;

import com.github.twitch4j.TwitchClient;
import ru.pshiblo.Config;
import ru.pshiblo.services.ServiceThread;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.broadcast.ChatPostService;

public class TwitchChatPostService extends ServiceThread implements ChatPostService {

    TwitchClient twitchClient;

    public TwitchChatPostService() {
        twitchClient = TwitchAuth.getTwitchClient();
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CHAT_POST;
    }

    @Override
    protected void runInThread() throws Exception {
        while (true) {
            try {
                twitchClient.getChat().sendMessage(Config.getInstance().getTwitchChannelName(), "У нас работает бот для музыки! Для этого введите :/track <название трека/ссылка на музыку из youtube>");
                Thread.sleep(Config.getInstance().getTimeInsert());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void postMessage(String message) {
        twitchClient.getChat().sendMessage(Config.getInstance().getTwitchChannelName(), message);
    }
}
