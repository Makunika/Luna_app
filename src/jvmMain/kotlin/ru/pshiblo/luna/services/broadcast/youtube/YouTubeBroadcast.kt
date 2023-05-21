package ru.pshiblo.luna.services.broadcast.youtube

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.LiveChatMessage
import com.google.api.services.youtube.model.LiveChatMessageSnippet
import com.google.api.services.youtube.model.LiveChatTextMessageDetails
import com.google.inject.Inject
import com.google.inject.Singleton
import ru.pshiblo.luna.events.model.BroadcastMessageEvent
import kotlinx.coroutines.*
import mu.KotlinLogging
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.ServiceThread
import ru.pshiblo.luna.services.broadcast.BroadcastGetService
import ru.pshiblo.luna.services.broadcast.BroadcastPostService
import ru.pshiblo.luna.services.scope.YouTubeScope
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

private val log = KotlinLogging.logger{}

@Singleton
class YouTubeBroadcastGetService : ServiceThread(), BroadcastGetService {

    @Inject
    private lateinit var youTubeScope: YouTubeScope

    private var lastPageToken: String? = null
    private var pollingIntervalMillis = 0L

    override suspend fun runInThread() {
        while (true) {
            log.info { "read messages, delay is ${maxOf(pollingIntervalMillis.milliseconds, ApplicationProperties.minTimePoolingChatYoutube)}" }
            nextMessages()
            delay(
                maxOf(pollingIntervalMillis.milliseconds,
                    ApplicationProperties.minTimePoolingChatYoutube)
            )
        }
    }

    private fun nextMessages() {
        val request: YouTube.LiveChatMessages.List = youTubeScope.youtubeService.liveChatMessages()
            .list(ApplicationProperties.youtubeLiveChatId, listOf("snippet"))
        lastPageToken?.let { request.pageToken = it }

        val response = request.execute()
        lastPageToken = response.nextPageToken
        pollingIntervalMillis = response.pollingIntervalMillis

        val items = response.items
        log.info { "read messages ${items.size} and $items" }
        items
            .filter { BroadcastMessageEvent.isMappable(it) }
            .map { BroadcastMessageEvent.map(it) }
            .forEach { ApplicationProperties.eventHandler.publish(it) }
    }
}

@Singleton
class YouTubeBroadcastPostService : ServiceThread(), BroadcastPostService {

    @Inject
    private lateinit var youTubeScope: YouTubeScope
    private val log = KotlinLogging.logger {}

    override fun publish(message: String) {
        log.info { "Send message yt with init $isInit" }
        if (!isInit) {
            return
        }
        scope.launch {
            val youtubeService = youTubeScope.youtubeService
            youtubeService.liveChatMessages()
                .insert(
                    listOf("snippet"),
                    LiveChatMessage().apply {
                        snippet = LiveChatMessageSnippet().apply {
                            liveChatId = ApplicationProperties.youtubeLiveChatId
                            textMessageDetails = LiveChatTextMessageDetails().apply {
                                messageText = message
                            }
                            type = "textMessageEvent"
                        }
                    }
                ).execute()
        }
    }

    override suspend fun runInThread() {
        while (true) {
            publish("У нас работает бот для музыки! Для этого введите !track <название трека/ссылка на музыку из youtube>")
            delay(ApplicationProperties.timePostMessageAboutBot.minutes)
        }
    }
}