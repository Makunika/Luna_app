package ru.pshiblo.luna.services.broadcast.twitch

import com.github.philippheuer.events4j.simple.SimpleEventHandler
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.google.inject.Inject
import com.google.inject.Singleton
import ru.pshiblo.luna.events.model.BroadcastMessageEvent
import kotlinx.coroutines.delay
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.ServiceThread
import ru.pshiblo.luna.services.broadcast.BroadcastGetService
import ru.pshiblo.luna.services.broadcast.BroadcastPostService
import ru.pshiblo.luna.services.scope.TwitchScope
import ru.pshiblo.luna.services.toast.ToastHolderService
import kotlin.time.Duration.Companion.minutes

@Singleton
class TwitchBroadcastGetService : BroadcastGetService {

    @Inject
    private lateinit var twitchScope: TwitchScope
    @Inject
    private lateinit var toastHolderService: ToastHolderService
    private var _isInit = false

    override val isInit = _isInit

    override fun start() {
        if (twitchScope.twitchClient.chat.isChannelJoined(ApplicationProperties.channelName))
            twitchScope.twitchClient.chat.joinChannel(ApplicationProperties.channelName)

        if (!twitchScope.twitchClient.chat.isChannelJoined(ApplicationProperties.channelName)) {
            throw IllegalCallerException("Канала ${ApplicationProperties.channelName} не существует")
        }

        val eventHandler = twitchScope.twitchClient.eventManager.getEventHandler(SimpleEventHandler::class.java)

        eventHandler.onEvent(ChannelMessageEvent::class.java) { msg ->
            if (BroadcastMessageEvent.isMappable(msg)) {
                ApplicationProperties.eventHandler.publish(BroadcastMessageEvent.map(msg))
            }
        }
        toastHolderService.showMessage("Получение сообщений от Twitch инициализированы")
        _isInit = true
    }

    override fun shutdown() {
        val eventHandler = twitchScope.twitchClient.eventManager.getEventHandler(SimpleEventHandler::class.java)
        eventHandler.consumerBasedHandlers.clear()
        _isInit = false
    }
}

@Singleton
class TwitchBroadcastPostService : ServiceThread(), BroadcastPostService {

    @Inject
    private lateinit var twitchScope: TwitchScope

    override fun publish(message: String) {
        if (isInit)
            twitchScope.twitchClient.chat.sendMessage(ApplicationProperties.channelName, message)
    }

    override suspend fun runInThread() {
        while (true) {
            publish("У нас работает бот для музыки! Для этого введите !track <название трека/ссылка на музыку из youtube>")
            delay(10.minutes)
        }
    }
}