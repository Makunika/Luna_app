package ru.pshiblo.luna.events.model

import ru.pshiblo.luna.services.properties.PlatformState

data class SetPlatformEvent(
    val platformState: PlatformState
)
