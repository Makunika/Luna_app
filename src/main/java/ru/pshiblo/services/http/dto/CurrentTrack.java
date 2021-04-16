package ru.pshiblo.services.http.dto;

public class CurrentTrack {
    private String track;

    public CurrentTrack() {
        track = null;
    }

    public CurrentTrack(String track) {
        this.track = track;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
