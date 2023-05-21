package ru.pshiblo.luna.services.keypress

import com.github.kwhat.jnativehook.GlobalScreen
import com.google.inject.Inject
import com.google.inject.Singleton
import ru.pshiblo.luna.services.Service
import ru.pshiblo.luna.services.toast.ToastHolderService

@Singleton
class GlobalKeyPressService : Service {
    @Inject
    private lateinit var skipMusicKeyListener: SkipMusicKeyListener
    @Inject
    private lateinit var toastHolderService: ToastHolderService
    private var _isInit = false

    override val isInit: Boolean
        get() = _isInit

    override fun start() {
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(skipMusicKeyListener)
        _isInit = true
        toastHolderService.showMessage("Скип музыки по кнопке F12 включен")
    }

    override fun shutdown() {
        GlobalScreen.removeNativeKeyListener(skipMusicKeyListener)
        _isInit = false
        toastHolderService.showMessage("Скип музыки по кнопке F12 выключен")
    }
}