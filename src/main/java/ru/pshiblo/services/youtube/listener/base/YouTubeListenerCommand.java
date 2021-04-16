package ru.pshiblo.services.youtube.listener.base;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.LiveChatMessage;
import ru.pshiblo.gui.log.ConsoleOut;

import java.util.*;

public abstract class YouTubeListenerCommand implements YouTubeListener {

    abstract protected String getCommand();

    abstract protected void handleCommand(String arg, LiveChatMessage liveChatMessage);

    private DateTime lastMessageTime = new DateTime(new Date());

    @Override
    public void handle(List<LiveChatMessage> liveChatMessageList) {
        Deque<LiveChatMessage> deque = new ArrayDeque<>();
        for (int i = liveChatMessageList.size() - 1; i >= 0; i--) {
            LiveChatMessage liveChatMessage = liveChatMessageList.get(i);
            String messageText = liveChatMessage.getSnippet().getTextMessageDetails().getMessageText();
            if (messageText.matches(getCommand() + ".*")) {
                if (liveChatMessage.getSnippet().getPublishedAt().getValue() <= lastMessageTime.getValue()) {
                    break;
                }
                deque.offerFirst(liveChatMessage);
            }
        }

        if (deque.size() != 0)
            ConsoleOut.printList(deque, "Очередь команд", (t) -> t.getSnippet().getTextMessageDetails().getMessageText());

        while(deque.peekFirst() != null) {
            LiveChatMessage liveChatMessage = deque.pollFirst();
            String messageText = liveChatMessage.getSnippet().getTextMessageDetails().getMessageText();
            if (messageText.trim().length() == getCommand().length()) {
                handleCommand("", liveChatMessage);
            } else {
                handleCommand(messageText.trim().substring((getCommand() + " ").length()), liveChatMessage);
            }
        }


        lastMessageTime = liveChatMessageList.size() == 0 ? lastMessageTime : liveChatMessageList.get(liveChatMessageList.size() - 1).getSnippet().getPublishedAt();
    }



}
