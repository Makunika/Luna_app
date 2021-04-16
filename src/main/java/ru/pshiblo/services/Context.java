package ru.pshiblo.services;

import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static final ConcurrentHashMap<ServiceType, Service> services = new ConcurrentHashMap<>();

    public static void addServiceAndStart(Service service) {
        if (!services.containsKey(service.getServiceType())) {
            services.put(service.getServiceType(), service);
            service.start();
        } else {
            throw new IllegalArgumentException("component: " + service.getServiceType() + " already exist");
        }
    }

    public static Service getService(ServiceType serviceType) {
        Service service = services.getOrDefault(serviceType, null);

        if (service == null) {
            throw new IllegalArgumentException("component: " + serviceType + " not exist");
        }

        if (!service.isInitializer()) {
            throw new IllegalStateException("service not is init! " + serviceType);
        }

        return service;
    }

    public static void removeService(ServiceType serviceType) {
        services.remove(serviceType);
    }

    public static boolean isInitService(ServiceType serviceType) {
        try {
            return getService(serviceType).isInitializer();
        } catch (Throwable e) {
            return false;
        }
    }

    public static void shutdownService(ServiceType serviceType) {
        try {
            getService(serviceType).shutdown();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void shutdownAllService() {
        services.values().forEach((Service::shutdown));
    }

    public static MusicService getMusicService() {
        return ((MusicService) getService(ServiceType.MUSIC));
    }

}
