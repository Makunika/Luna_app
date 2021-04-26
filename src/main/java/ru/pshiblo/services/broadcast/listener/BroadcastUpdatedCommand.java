package ru.pshiblo.services.broadcast.listener;

import ru.pshiblo.services.Context;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.broadcast.ChatPostService;
import ru.pshiblo.services.broadcast.listener.base.BroadcastMessage;
import ru.pshiblo.services.broadcast.listener.base.BroadcastListenerCommand;

public class BroadcastUpdatedCommand extends BroadcastListenerCommand {

    @Override
    protected String getCommand() {
        return UpdatedCommand.getInstance().getCommand();
    }

    @Override
    protected void handleCommand(String arg, BroadcastMessage broadcastMessage) {
        ((ChatPostService) Context.getService(ServiceType.CHAT_POST)).postMessage(UpdatedCommand.getInstance().getAnswer());
    }
}
