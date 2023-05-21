package ru.pshiblo.luna.services.broadcast

import ru.pshiblo.luna.services.Service

interface BroadcastPostService : Service {
    fun publish(message: String)
}