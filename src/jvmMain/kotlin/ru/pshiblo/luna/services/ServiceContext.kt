package ru.pshiblo.luna.services

import com.google.inject.Inject
import com.google.inject.Singleton
import ru.pshiblo.luna.services.audio.MusicServiceProvider
import ru.pshiblo.luna.services.broadcast.BroadcastServiceProvider
import ru.pshiblo.luna.services.keypress.GlobalKeyPressService
import ru.pshiblo.luna.services.server.HttpServerService

@Singleton
class ServiceContext {
    @Inject
    private lateinit var musicServiceProvider: MusicServiceProvider
    @Inject
    private lateinit var broadcastServiceProvider: BroadcastServiceProvider
    @Inject
    private lateinit var globalKeyPressService: GlobalKeyPressService
    @Inject
    private lateinit var httpServerService: HttpServerService

    private val startedServices = mutableMapOf<ServiceType, List<Service>>()

    fun startServices(serviceType: ServiceType) {
        if (startedServices.contains(serviceType)) {
            return
        }
        val services = when (serviceType) {
            ServiceType.MUSIC -> listOf(musicServiceProvider.getMusicServiceByState())
            ServiceType.BROADCAST -> listOf(
                broadcastServiceProvider.getBroadcastGetService(),
                broadcastServiceProvider.getBroadcastPostService()
            )
            ServiceType.KEYPRESS -> listOf(globalKeyPressService)
            ServiceType.HTTP -> listOf(httpServerService)
        }
        services.forEach { it.start() }
        startedServices[serviceType] = services
    }

    fun shutdownServices(serviceType: ServiceType) {
        startedServices[serviceType]?.let {
            it.forEach { service -> service.shutdown() }
            startedServices.remove(serviceType)
        }
    }

    fun shutdownAllServices() {
        for (serviceType in ServiceType.values()) {
            shutdownServices(serviceType)
        }
    }

    fun isInitServices(serviceType: ServiceType) =
        startedServices[serviceType]
            ?.map { it.isInit }
            ?.reduce { a1, a2 -> a1 && a2 }
            ?: false
}

enum class ServiceType {
    MUSIC,
    HTTP,
    KEYPRESS,
    BROADCAST
}