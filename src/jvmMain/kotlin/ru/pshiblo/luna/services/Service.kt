package ru.pshiblo.luna.services

interface Service {
    val isInit: Boolean
    fun start()
    fun shutdown()
}