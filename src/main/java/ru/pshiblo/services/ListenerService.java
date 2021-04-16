package ru.pshiblo.services;

public interface ListenerService<T extends Listener> extends Service {

    void subscribe(T listener);

    void unsubscribe(T listener);

}
