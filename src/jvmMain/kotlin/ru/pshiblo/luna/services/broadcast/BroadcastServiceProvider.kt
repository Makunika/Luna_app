package ru.pshiblo.luna.services.broadcast

import com.google.inject.Inject
import com.google.inject.Singleton
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.PlatformState
import ru.pshiblo.luna.services.broadcast.twitch.TwitchBroadcastGetService
import ru.pshiblo.luna.services.broadcast.twitch.TwitchBroadcastPostService
import ru.pshiblo.luna.services.broadcast.youtube.YouTubeBroadcastGetService
import ru.pshiblo.luna.services.broadcast.youtube.YouTubeBroadcastPostService

@Singleton
class BroadcastServiceProvider {
    @Inject
    private lateinit var youTubeBroadcastGetService: YouTubeBroadcastGetService
    @Inject
    private lateinit var youTubeBroadcastPostService: YouTubeBroadcastPostService
    @Inject
    private lateinit var twitchBroadcastGetService: TwitchBroadcastGetService
    @Inject
    private lateinit var twitchBroadcastPostService: TwitchBroadcastPostService

    fun getBroadcastPostService(): BroadcastPostService =
        when(ApplicationProperties.applicationState.platformState) {
            PlatformState.YOUTUBE -> youTubeBroadcastPostService
            PlatformState.TWITCH -> twitchBroadcastPostService
            else -> error("not found broadcast service")
        }

    fun getBroadcastGetService(): BroadcastGetService =
        when(ApplicationProperties.applicationState.platformState) {
            PlatformState.YOUTUBE -> youTubeBroadcastGetService
            PlatformState.TWITCH -> twitchBroadcastGetService
            else -> error("not found broadcast service")
        }
}