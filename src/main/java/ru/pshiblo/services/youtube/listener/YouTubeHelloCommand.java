package ru.pshiblo.services.youtube.listener;

import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.ServiceType;
import ru.pshiblo.services.youtube.ChatPostService;
import ru.pshiblo.services.youtube.listener.base.YouTubeListenerCommand;

public class YouTubeHelloCommand extends YouTubeListenerCommand {
    @Override
    protected String getCommand() {
        return "/hello";
    }

    @Override
    protected void handleCommand(String arg, LiveChatMessage liveChatMessage) {
        ((ChatPostService) Context.getService(ServiceType.YOUTUBE_POST)).postMessage("Привет");
    }
}
