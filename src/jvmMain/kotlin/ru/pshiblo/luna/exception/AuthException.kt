package ru.pshiblo.luna.exception

import ru.pshiblo.luna.services.properties.MusicState
import ru.pshiblo.luna.services.properties.PlatformState

class AuthException : Exception{

    constructor(platformState: PlatformState) : super(
        when (platformState) {
            PlatformState.YOUTUBE -> "Не смог авторизовать через Google"
            PlatformState.TWITCH -> "Токен Twitch неверный, проверьте его"
            PlatformState.NONE -> "Auth is failed"
        }
    )

    constructor(musicState: MusicState) : super(
        when (musicState) {
            MusicState.DISCORD -> "Токен Discord неверный, проверьте его"
            else -> "Auth is failed"
        }
    )

}