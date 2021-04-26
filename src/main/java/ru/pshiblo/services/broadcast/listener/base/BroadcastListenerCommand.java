package ru.pshiblo.services.broadcast.listener.base;

import ru.pshiblo.gui.log.ConsoleOut;

import java.util.*;

public abstract class BroadcastListenerCommand implements BroadcastListener {

    abstract protected String getCommand();

    abstract protected void handleCommand(String arg, BroadcastMessage broadcastMessage);

    private Date lastMessageTime = new Date();

    @Override
    public void handle(List<BroadcastMessage> broadcastMessageList) {
        Deque<BroadcastMessage> deque = new ArrayDeque<>();
        for (int i = broadcastMessageList.size() - 1; i >= 0; i--) {
            BroadcastMessage broadcastMessage = broadcastMessageList.get(i);
            String messageText = broadcastMessage.getMessage();
            if (messageText.matches(":?" + getCommand() + ".*")) {
                if (broadcastMessage.getPublishedAt().getTime() <= lastMessageTime.getTime()) {
                    break;
                }
                deque.offerFirst(broadcastMessage);
            }
        }

        if (deque.size() != 0)
            ConsoleOut.printList(deque, "Очередь команд", BroadcastMessage::getMessage);

        while(deque.peekFirst() != null) {
            BroadcastMessage broadcastMessage = deque.pollFirst();
            String messageText = broadcastMessage.getMessage();
            if (messageText.trim().length() == getCommand().length()) {
                handleCommand("", broadcastMessage);
            } else {
                handleCommand(messageText.trim().substring((getCommand() + " ").length()).trim(), broadcastMessage);
            }
        }


        lastMessageTime = broadcastMessageList.size() == 0 ? lastMessageTime : broadcastMessageList.get(broadcastMessageList.size() - 1).getPublishedAt();
    }

    @Override
    public void handleSingle(BroadcastMessage broadcastMessage) {
        String messageText = broadcastMessage.getMessage();
        if (messageText.matches(":?" + getCommand() + ".*")) {
            if (messageText.trim().length() == getCommand().length()) {
                handleCommand("", broadcastMessage);
            } else {
                handleCommand(messageText.trim().substring((getCommand() + " ").length()).trim(), broadcastMessage);
            }
        }
    }
}
