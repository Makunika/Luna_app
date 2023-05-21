package ru.pshiblo.luna.events

import com.google.inject.AbstractModule
import ru.pshiblo.luna.events.listeners.ChatListener
import ru.pshiblo.luna.events.listeners.StateListener
import ru.pshiblo.luna.services.properties.ApplicationProperties

class EventModule : AbstractModule() {
    override fun configure() {
        val eventManager = ApplicationProperties.eventManager
        val eventHandler = ApplicationProperties.eventHandler
        eventManager.registerEventHandler(eventHandler)

        val stateListener = StateListener()
        val chatListener = ChatListener()

        bind(StateListener::class.java).toInstance(stateListener)
        bind(ChatListener::class.java).toInstance(chatListener)

        eventHandler.registerListener(stateListener)
        eventHandler.registerListener(chatListener)

    }
}