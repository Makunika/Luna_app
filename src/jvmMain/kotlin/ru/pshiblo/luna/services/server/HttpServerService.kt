package ru.pshiblo.luna.services.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Singleton
import com.sun.net.httpserver.HttpServer
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.Service
import ru.pshiblo.luna.services.audio.MusicServiceProvider
import ru.pshiblo.luna.services.toast.ToastHolderService
import java.net.InetSocketAddress


@Singleton
class HttpServerService: Service {

    @Inject
    private lateinit var musicServiceProvider: MusicServiceProvider
    @Inject
    private lateinit var toastHolderService: ToastHolderService

    private lateinit var server: HttpServer
    private val objectMapper = ObjectMapper()
    private var _isInit = false

    override val isInit: Boolean
        get() = _isInit

    override fun start() {
        if (isInit) {
            return
        }
        server = HttpServer.create()
        server.bind(InetSocketAddress("localhost", ApplicationProperties.obsServerPort), 0)
        server.createContext("/track") { exchange ->
            if ("GET" == exchange.requestMethod) {
                val currentTrack = CurrentTrack(
                    musicServiceProvider.getMusicServiceByState().playingTrack ?: ""
                )
                val json = objectMapper.writeValueAsString(currentTrack)
                exchange.sendResponseHeaders(200, json.length.toLong())
                exchange.responseBody?.apply {
                    write(json.encodeToByteArray())
                    flush()
                    close()
                }
            }
        }
        server.start()
        _isInit = true
        toastHolderService.showMessage("Сервер для OBS запущен на порту ${ApplicationProperties.obsServerPort}")
    }

    override fun shutdown() {
        server.stop(0)
        _isInit = false
        toastHolderService.showMessage("Сервер для OBS остановлен")
    }
}

data class CurrentTrack(val track: String)