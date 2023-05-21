package ru.pshiblo.luna.services.scope

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.IRCMessageEvent
import com.google.inject.Singleton
import ru.pshiblo.luna.exception.AuthException
import kotlinx.coroutines.delay
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.PlatformState
import ru.pshiblo.luna.services.properties.UserInfo

@Singleton
class TwitchScope() : Scope {
    private var _twitchClient: TwitchClient? = null
    private var _isAuth = false

    val twitchClient: TwitchClient
        get() = _twitchClient ?: error("twitch not enabled")

    override suspend fun auth() {
        if (isAuth) {
            return
        }
        if (ApplicationProperties.twitchToken.isEmpty()) {
            throw IllegalCallerException("Токен пустой");
        }

        val credential = OAuth2Credential(
            "twitch",
            ApplicationProperties.twitchToken
        )

        _twitchClient = TwitchClientBuilder.builder()
            .withChatAccount(credential)
            .withEnableChat(true)
            .build().apply {
                eventManager.onEvent(IRCMessageEvent::class.java) {
                    _isAuth = true
                }
            }

        delay(1500)

        if (!_isAuth) {
            throw AuthException(PlatformState.TWITCH)
        }

        ApplicationProperties.userInfo = UserInfo(
            "Twitch",
            "http://pngimg.com/uploads/twitch/small/twitch_PNG49.png"
        )
    }

    override val isAuth: Boolean
        get() = _isAuth

}