package ru.pshiblo.luna.ui.support

import androidx.compose.runtime.State
import com.github.philippheuer.events4j.api.service.IEventHandler
import ru.pshiblo.luna.services.properties.ApplicationState
import ru.pshiblo.luna.services.properties.ApplicationProperties

fun applicationEventHandler(): State<IEventHandler> = object : State<IEventHandler> {
    override val value: IEventHandler
        get() = ApplicationProperties.eventHandler
}

fun applicationState(): State<ApplicationState> = object : State<ApplicationState> {
    override val value: ApplicationState
        get() = ApplicationProperties.applicationState
}