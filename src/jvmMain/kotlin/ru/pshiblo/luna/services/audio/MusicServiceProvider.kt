package ru.pshiblo.luna.services.audio

import com.google.inject.Inject
import com.google.inject.Singleton
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.MusicState
import ru.pshiblo.luna.services.audio.discord.DiscordMusicService
import ru.pshiblo.luna.services.audio.local.LocalMusicService

@Singleton
class MusicServiceProvider {
    @Inject
    private lateinit var localMusicService: LocalMusicService
    @Inject
    private lateinit var discordMusicService: DiscordMusicService

    fun getMusicServiceByState(): MusicService =
        when(ApplicationProperties.applicationState.musicState) {
            MusicState.LOCAL -> localMusicService
            MusicState.DISCORD -> discordMusicService
            else -> error("not found music service")
        }
}