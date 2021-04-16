package ru.pshiblo.services;

public interface Service {

    ServiceType getServiceType();

    boolean isInitializer();

    void start();

    void shutdown();
}
