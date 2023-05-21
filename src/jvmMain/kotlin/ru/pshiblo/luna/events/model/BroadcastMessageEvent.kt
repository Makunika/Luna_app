package ru.pshiblo.luna.events.model

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.google.api.services.youtube.model.LiveChatMessage
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class BroadcastMessageEvent(
    var command: String,
    var arg: String,
    var message: String,
    var username: String,
    var publishedAt: LocalDateTime
) {
    companion object {

        private val PATTERN_MSG = "^[/!:](\\S+)\\s*(\\S*.*)$".toRegex()

        fun isMappable(ytMsg: LiveChatMessage) = PATTERN_MSG.matches(ytMsg.snippet.textMessageDetails.messageText)
        fun isMappable(twMsg: ChannelMessageEvent) = PATTERN_MSG.matches(twMsg.message)

        fun map(ytMsg: LiveChatMessage) =
            map(
                ytMsg.snippet.textMessageDetails.messageText,
                LocalDateTime.ofInstant(Date(ytMsg.snippet.publishedAt.value).toInstant(), ZoneOffset.UTC),
                "youtube"
            )

        fun map(twMsg: ChannelMessageEvent) =
            map(
                twMsg.message,
                LocalDateTime.now(),
                twMsg.user.name
            )

        private fun map(text: String, time: LocalDateTime, username: String) =
            PATTERN_MSG.find(text)?.let {
                BroadcastMessageEvent(
                    message = it.groupValues[0],
                    command = it.groupValues[1],
                    arg = it.groupValues[2],
                    username = username,
                    publishedAt = time,
                )
            }
    }
}


