package ru.pshiblo.luna.services.toast

import androidx.compose.material.SnackbarHostState
import com.google.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging

@Singleton
class ToastHolderService {
    lateinit var snackbarHostState: SnackbarHostState

    @OptIn(DelicateCoroutinesApi::class)
    private val scope =
        CoroutineScope(newSingleThreadContext("toasts"))
    private val log = KotlinLogging.logger {  }

    fun showMessage(
        message: String,
        actionLabel: String? = null,
    ) {
        log.info { message }
        scope.launch {
            snackbarHostState.showSnackbar(message, actionLabel)
        }
    }
}