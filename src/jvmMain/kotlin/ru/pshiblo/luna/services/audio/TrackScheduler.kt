package ru.pshiblo.luna.services.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.*

class TrackScheduler(
    private val player: AudioPlayer,
    private val onTrackException: (AudioTrack, FriendlyException) -> Unit
) : AudioEventAdapter() {

    init {
        player.addListener(this)
    }

    private val tracks: Queue<AudioTrack> = ArrayDeque()

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        super.onTrackStart(player, track)
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (!queueIsEmpty() && endReason!!.mayStartNext) {
            nextTrack()
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
    }

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
        if (track != null && exception != null) {
            onTrackException(track, exception)
        }
        super.onTrackException(player, track, exception)
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        super.onTrackStuck(player, track, thresholdMs)
    }

    fun queue(track: AudioTrack?) {
        if (queueIsEmpty()) {
            tracks.offer(track)
            nextTrack()
        } else {
            tracks.offer(track)
        }
    }

    fun skipTrack() = nextTrack()
    fun queueIsEmpty() = tracks.isEmpty() && player.playingTrack == null
    private fun nextTrack() = player.playTrack(tracks.poll())
}