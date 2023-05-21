package ru.pshiblo.luna.services.scope

interface Scope {
    suspend fun auth()
    val isAuth: Boolean
}