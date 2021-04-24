package ru.pshiblo.services.http;

import com.sun.net.httpserver.HttpServer;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.ServiceThread;
import ru.pshiblo.services.ServiceType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpService extends ServiceThread {

    private HttpServer server;

    @Override
    protected void runInThread() {
        try {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            server = HttpServer.create(new InetSocketAddress("localhost", 5000), 0);
            server.createContext("/track", new CurrentTrackHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            ConsoleOut.println("Сервер для OBS запущен на порту 5000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HTTP;
    }

    @Override
    public void shutdown() {
        if (server != null) {
            server.stop(3);
        }
        super.shutdown();
    }
}
