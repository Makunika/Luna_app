package ru.pshiblo.luna.events.model

import ru.pshiblo.luna.services.properties.MusicState

data class SetMusicEvent(
    val musicState: MusicState
)
