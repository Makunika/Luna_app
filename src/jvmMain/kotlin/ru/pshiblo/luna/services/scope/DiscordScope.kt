package ru.pshiblo.luna.services.scope

import com.google.inject.Singleton
import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import ru.pshiblo.luna.exception.AuthException
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.MusicState
import java.lang.IllegalArgumentException

@Singleton
class DiscordScope: Scope {

    private lateinit var _jda: JDA
    private var _isAuth = false

    val jda: JDA
        get() = if (isAuth) _jda else error("not auth")

    override suspend fun auth() {
        if (isAuth) {
            return
        }
        try {
            _jda = light(ApplicationProperties.discordToken, enableCoroutines = true) {
                intents += listOf(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.MESSAGE_CONTENT
                )
                setStatus(OnlineStatus.ONLINE)
                setActivity(Activity.listening("aloha"))
                enableCache(CacheFlag.VOICE_STATE)
            }
            _isAuth = true
        } catch (_: InvalidTokenException) {
            throw AuthException(MusicState.DISCORD)
        } catch (_: IllegalArgumentException) {
            throw AuthException(MusicState.DISCORD)
        }
    }

    fun exit() {
        _jda.shutdown()
        _isAuth = false
    }

    override val isAuth: Boolean
        get() = _isAuth
}