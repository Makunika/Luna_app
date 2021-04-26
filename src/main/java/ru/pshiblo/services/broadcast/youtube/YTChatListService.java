package ru.pshiblo.services.broadcast.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.ServiceThread;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.broadcast.ChatListService;
import ru.pshiblo.services.broadcast.listener.BroadcastHelloCommand;
import ru.pshiblo.services.broadcast.listener.BroadcastTrackCommand;
import ru.pshiblo.services.broadcast.listener.BroadcastUpdatedCommand;
import ru.pshiblo.services.broadcast.listener.base.BroadcastListener;
import ru.pshiblo.services.broadcast.listener.base.BroadcastMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class YTChatListService extends ServiceThread implements ChatListService {

    private List<BroadcastListener> listeners;
    private YouTube youtubeService;

    public YTChatListService() {
        listeners = new ArrayList<>();
        //обычные команды
        this.subscribe(new BroadcastTrackCommand());
        this.subscribe(new BroadcastHelloCommand());
        this.subscribe(new BroadcastUpdatedCommand());
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CHAT_LIST;
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

        for (BroadcastListener listener : listeners) {
            listener.handle(messages
                    .stream()
                    .map(
                            (ytMsg -> new BroadcastMessage(
                                            ytMsg.getSnippet().getTextMessageDetails().getMessageText(),
                                            new Date(ytMsg.getSnippet().getPublishedAt().getValue())
                                            )
                            )
                    )
                    .collect(Collectors.toList())
            );
        }
    }

    @Override
    public void subscribe(BroadcastListener listener) {
        if (listeners.stream().anyMatch(l -> l.equals(listener))) {
            throw new IllegalArgumentException("listener already exist");
        }
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(BroadcastListener listener) {
        listeners.removeIf(l -> l.equals(listener));
    }
}
