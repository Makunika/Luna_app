package ru.pshiblo.luna.events.listeners

import com.github.philippheuer.events4j.simple.domain.EventSubscriber
import com.google.inject.Inject
import ru.pshiblo.luna.events.model.SetEnableF12Event
import ru.pshiblo.luna.events.model.SetMusicEvent
import ru.pshiblo.luna.events.model.SetPlatformEvent
import ru.pshiblo.luna.events.model.SetVolumeEvent
import ru.pshiblo.luna.events.model.SkipTrackEvent
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.ServiceContext
import ru.pshiblo.luna.services.ServiceType
import ru.pshiblo.luna.services.audio.MusicServiceProvider

class StateListener {

    @Inject
    private lateinit var serviceContext: ServiceContext
    @Inject
    private lateinit var musicServiceProvider: MusicServiceProvider

    @EventSubscriber
    fun onPlatformSet(event: SetPlatformEvent) {
        ApplicationProperties.applicationState.platformState = event.platformState
    }

    @EventSubscriber
    fun onMusicSet(event: SetMusicEvent) {
        ApplicationProperties.applicationState.musicState = event.musicState
        serviceContext.startServices(ServiceType.MUSIC)
    }

    @EventSubscriber
    fun onEnableF12Set(event: SetEnableF12Event) {
        ApplicationProperties.enabledF12 = event.enabled
        if (event.enabled) {
            serviceContext.startServices(ServiceType.KEYPRESS)
        } else {
            serviceContext.shutdownServices(ServiceType.KEYPRESS)
        }
    }

    @EventSubscriber
    fun onSkipTrackEvent(event: SkipTrackEvent) {
        musicServiceProvider.getMusicServiceByState().skip()
    }

    @EventSubscriber
    fun onSetVolumeEvent(event: SetVolumeEvent) {
        musicServiceProvider.getMusicServiceByState().volume = event.volume
    }

}