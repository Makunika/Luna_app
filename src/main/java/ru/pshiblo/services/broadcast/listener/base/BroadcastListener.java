package ru.pshiblo.services.broadcast.listener.base;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.services.Listener;

import java.util.List;

public interface BroadcastListener extends Listener<List<BroadcastMessage>> {

    void handle(List<BroadcastMessage> broadcastMessageList);

    void handleSingle(BroadcastMessage broadcastMessage);
}
