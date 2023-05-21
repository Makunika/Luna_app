package ru.pshiblo.luna.services

import kotlinx.coroutines.*
import mu.KotlinLogging

abstract class ServiceThread : Service {

    private val log = KotlinLogging.logger {  }

    @OptIn(DelicateCoroutinesApi::class)
    protected val scope =
        CoroutineScope(newSingleThreadContext(javaClass.simpleName))

    private lateinit var job: Job

    private val handlersExceptions: MutableList<(Throwable) -> Unit> = mutableListOf({
        e -> log.error(e) { "error in service ${javaClass.simpleName}" }
    })

    override val isInit: Boolean
        get() = if (this::job.isInitialized) job.isActive else false

    override fun start() {
        if (isInit) {
            return
        }
        log.info { "Сервис ${javaClass.simpleName} запускается" }
        job = scope.launch(
            CoroutineExceptionHandler { _, ex ->
                handlersExceptions.forEach { action -> action(ex) }
            }
        ) {
            runInThread()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun shutdown() {
        log.info { "Сервис ${javaClass.simpleName} останавливается" }
        GlobalScope.launch {
            if (isInit) {
                job.cancelAndJoin()
            }
        }
    }

    fun subscribeExceptionHandler(action: (Throwable) -> Unit) {
        handlersExceptions.add(action)
    }

    abstract suspend fun runInThread()
}