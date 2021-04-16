package ru.pshiblo.services;

import ru.pshiblo.services.Service;

public interface MusicService extends Service {
    void play(String track);
    void skip();
    void volume(int volume);
    String getPlayingTrack();
}
