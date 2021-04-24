package ru.pshiblo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class ServiceThread implements Service {

    private ExecutorService executor;
    private List<Consumer<Exception>> handleExceptions;

    public ServiceThread() {
        handleExceptions = new ArrayList<>();
    }

    public void reRun() {
        shutdown();
        start();
    }

    @Override
    public void start() {
        if (executor != null)
            throw new IllegalCallerException("executor is run!");

        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                runInThread();
            } catch (Exception e) {
                for (Consumer<Exception> handleException : handleExceptions) {
                    handleException.accept(e);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        try {
            System.out.println("attempt to shutdown executor for " + getServiceType());
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted for " + getServiceType());
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks for " + getServiceType());
            }
            executor.shutdownNow();
            System.out.println("shutdown finished for " + getServiceType());
            executor = null;
        }
    }

    @Override
    public boolean isInitializer() {
        return executor != null;
    }

    public void subscribeException(Consumer<Exception> handler) {
        handleExceptions.add(handler);
    }

    protected abstract void runInThread() throws Exception;
}
