package ru.pshiblo.luna.services.properties

import com.github.philippheuer.events4j.api.IEventManager
import com.github.philippheuer.events4j.core.EventManager
import com.github.philippheuer.events4j.simple.SimpleEventHandler
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.seconds


enum class MusicState {
    LOCAL,
    DISCORD,
    NONE
}

enum class PlatformState {
    YOUTUBE,
    TWITCH,
    NONE
}

data class ApplicationState (
    var platformState: PlatformState = PlatformState.NONE,
    var musicState: MusicState = MusicState.NONE
)

data class UserInfo(
    val username: String,
    val picture: String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Property

object ApplicationProperties {
    @Property
    var maxTimeTrack = 360
    @Property
    var timePostMessageAboutBot = 2
    @Property
    var twitchToken: String = ""
    @Property
    var discordToken: String = ""
    @Property
    private var twitchChannelName: String = ""
    @Property
    private var youtubeVideoId: String = ""
    @Property
    var minTimePoolingChatYoutube = 30.seconds
    @Property
    var obsServerPort = 5000
    @Property
    var version = "1.1.9"

    val applicationState = ApplicationState()
    val eventHandler: SimpleEventHandler = SimpleEventHandler()
    val eventManager: IEventManager = EventManager()

    var userInfo: UserInfo = UserInfo("", "")
    var youtubeLiveChatId: String = ""
    var enabledF12 = false

    var channelName: String
        get() = when(applicationState.platformState) {
            PlatformState.TWITCH -> twitchChannelName
            PlatformState.YOUTUBE -> youtubeVideoId
            else -> error("Not set")
        }
        set(value) {
            when(applicationState.platformState) {
                PlatformState.TWITCH -> twitchChannelName = value
                PlatformState.YOUTUBE -> youtubeVideoId = value
                else -> error("Not set")
            }
        }
}

fun loadSavedProperties() {
    val file = File("config.properties")
    if (!file.exists()) {
        file.createNewFile()
    }
    val propertiesParser = Properties().apply {
        load(file.inputStream())
    }
    val declaredFields = ApplicationProperties.javaClass.declaredFields
    ApplicationProperties.javaClass.declaredFields.forEach { field ->
        if (field.isAnnotationPresent(Property::class.java)) {
            if (field.trySetAccessible()) {
                val propertyName = field.name
                propertiesParser.getProperty(propertyName)?.let { value ->
                    if (field.type.isPrimitive) {
                        if (field.type.simpleName == "int") {
                            field.setInt(ApplicationProperties, value.toInt())
                        }
                    } else if (String::class.java.isAssignableFrom(field.type)) {
                        field.set(ApplicationProperties, value)
                    }
                }
            }
        }
    }
}

fun saveProperties() {
    val file = File("config.properties")
    if (!file.exists()) {
        file.createNewFile()
    }
    val propertiesParser = Properties()
    ApplicationProperties.javaClass.declaredFields.forEach { field ->
        if (field.isAnnotationPresent(Property::class.java)) {
            if (field.trySetAccessible()) {
                val propertyName = field.name
                val value = field.get(ApplicationProperties).toString()
                propertiesParser.setProperty(propertyName, value)
            }
        }
    }
    propertiesParser.store(file.outputStream(), "by Pshiblo")
}