package ru.pshiblo.services.broadcast.listener;

import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.broadcast.listener.base.BroadcastMessage;
import ru.pshiblo.services.broadcast.listener.base.BroadcastListenerCommand;

public class BroadcastTrackCommand extends BroadcastListenerCommand {
    @Override
    protected String getCommand() {
        return "/track";
    }

    @Override
    protected void handleCommand(String arg, BroadcastMessage broadcastMessage) {
        ConsoleOut.println("Запускаем трек " + arg);
        Context.getMusicService().play(arg);
    }
}
