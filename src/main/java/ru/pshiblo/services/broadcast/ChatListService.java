package ru.pshiblo.services.broadcast;

import ru.pshiblo.services.ListenerService;
import ru.pshiblo.services.Service;
import ru.pshiblo.services.broadcast.listener.base.BroadcastListener;

public interface ChatListService extends ListenerService<BroadcastListener>, Service {

}
