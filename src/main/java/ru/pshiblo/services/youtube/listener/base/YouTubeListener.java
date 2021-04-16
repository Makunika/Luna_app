package ru.pshiblo.services.youtube.listener.base;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.services.Listener;

import java.util.List;

public interface YouTubeListener extends Listener<List<LiveChatMessage>> {
    void handle(List<LiveChatMessage> liveChatMessageList);
}
