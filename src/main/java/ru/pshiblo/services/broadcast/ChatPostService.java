package ru.pshiblo.services.broadcast;

import ru.pshiblo.services.Service;

public interface ChatPostService extends Service {
    void postMessage(String message);
}
