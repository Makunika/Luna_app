package ru.pshiblo.services.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.ListenerService;
import ru.pshiblo.services.ServiceThread;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.youtube.listener.YouTubeHelloCommand;
import ru.pshiblo.services.youtube.listener.YouTubeTrackCommand;
import ru.pshiblo.services.youtube.listener.YouTubeUpdatedCommand;
import ru.pshiblo.services.youtube.listener.base.YouTubeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatListService extends ServiceThread implements ListenerService<YouTubeListener>  {

    private List<YouTubeListener> listeners;
    private YouTube youtubeService;

    public ChatListService() {
        listeners = new ArrayList<>();
        //обычные команды
        this.subscribe(new YouTubeTrackCommand());
        this.subscribe(new YouTubeHelloCommand());
        this.subscribe(new YouTubeUpdatedCommand());
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.YOUTUBE_LIST;
    }

    @Override
    protected void runInThread() {
        try {
            youtubeService = YouTubeAuth.getYoutubeService();
            while (true) {
                listMessage();
                Thread.sleep(Config.getInstance().getTimeList());
            }
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleOut.alert(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void listMessage() throws IOException {
        YouTube.LiveChatMessages.List request = youtubeService.liveChatMessages()
                .list(Config.getInstance().getLiveChatId(), List.of("snippet"));
        LiveChatMessageListResponse responseLiveChat = request.execute();
        List<LiveChatMessage> messages = responseLiveChat.getItems();

        ConsoleOut.println("Прочитано сообщений с чата: " + messages.size());
        for (YouTubeListener listener : listeners) {
            listener.handle(messages);
        }
    }

    @Override
    public void subscribe(YouTubeListener listener) {
        if (listeners.stream().anyMatch(l -> l.equals(listener))) {
            throw new IllegalArgumentException("listener already exist");
        }
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(YouTubeListener listener) {
        listeners.removeIf(l -> l.equals(listener));
    }
}
