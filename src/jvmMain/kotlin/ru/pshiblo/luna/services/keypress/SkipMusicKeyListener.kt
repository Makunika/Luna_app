package ru.pshiblo.luna.services.keypress

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.google.inject.Inject
import com.google.inject.Singleton
import ru.pshiblo.luna.services.audio.MusicServiceProvider

@Singleton
class SkipMusicKeyListener: NativeKeyListener {
    @Inject
    private lateinit var musicServiceProvider: MusicServiceProvider

    override fun nativeKeyPressed(nativeEvent: NativeKeyEvent?) {
        nativeEvent?.let {
            if (it.keyCode == NativeKeyEvent.VC_F12) {
                println("Пошул нахуй")
                musicServiceProvider.getMusicServiceByState().skip()
            }
        }
    }
}