package ru.pshiblo.services.broadcast.listener.base;

import java.util.Date;

public class BroadcastMessage {

    private String message;
    private Date publishedAt;

    public BroadcastMessage(String message, Date publishedAt) {
        this.message = message;
        this.publishedAt = publishedAt;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
