package ru.pshiblo.luna.events.listeners

import com.github.philippheuer.events4j.simple.domain.EventSubscriber
import com.google.inject.Inject
import ru.pshiblo.luna.events.model.BroadcastMessageEvent
import mu.KotlinLogging
import ru.pshiblo.luna.services.audio.MusicServiceProvider
import ru.pshiblo.luna.services.broadcast.BroadcastServiceProvider

class ChatListener {

    @Inject
    private lateinit var musicServiceProvider: MusicServiceProvider
    @Inject
    private lateinit var broadcastServiceProvider: BroadcastServiceProvider
    private val log = KotlinLogging.logger {}

    @EventSubscriber
    fun helloCommand(broadcastMessageEvent: BroadcastMessageEvent) {
        if (Command.HELLO.notMatch(broadcastMessageEvent)) {
            return
        }
        log.info { "Hello command! $broadcastMessageEvent" }
        broadcastServiceProvider.getBroadcastPostService().publish("Привет!")
    }

    @EventSubscriber
    fun trackCommand(broadcastMessageEvent: BroadcastMessageEvent) {
        if (Command.TRACK.notMatch(broadcastMessageEvent)) {
            return
        }
        log.info { "Track command! $broadcastMessageEvent" }
        musicServiceProvider.getMusicServiceByState().play(broadcastMessageEvent.arg)
    }

    @EventSubscriber
    fun infoCommand(broadcastMessageEvent: BroadcastMessageEvent) {
        if (Command.INFO.notMatch(broadcastMessageEvent)) {
            return
        }
        log.info { "Info command! $broadcastMessageEvent" }
        broadcastServiceProvider.getBroadcastPostService().publish("Информация")
    }

    enum class Command(private val command: String) {
        HELLO("hello"),
        TRACK("track"),
        INFO("info");

        fun notMatch(broadcastMessageEvent: BroadcastMessageEvent) =
            !command.contentEquals(broadcastMessageEvent.command)
    }
}