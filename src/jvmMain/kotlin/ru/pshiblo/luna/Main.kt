package ru.pshiblo.luna

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.github.kwhat.jnativehook.GlobalScreen
import com.google.inject.Guice
import ru.pshiblo.luna.events.EventModule
import ru.pshiblo.luna.services.properties.loadSavedProperties
import ru.pshiblo.luna.services.properties.saveProperties
import ru.pshiblo.luna.services.toast.ToastHolderService
import ru.pshiblo.luna.ui.components.CustomSnackbarHost
import ru.pshiblo.luna.ui.components.LoadingDialog
import ru.pshiblo.luna.ui.support.DI
import ru.pshiblo.luna.ui.support.guice
import ru.pshiblo.luna.ui.views.LoginView
import ru.pshiblo.luna.ui.views.SetMusicView
import ru.pshiblo.luna.ui.views.SettingView
import ru.pshiblo.luna.ui.views.state.StateView
import ru.pshiblo.luna.utils.loadTypography
import kotlin.reflect.KClass

@Composable
@Preview
fun App() {
    var stateView by remember { mutableStateOf(StateView.LOGIN) }
    val snackbarHostState = remember { SnackbarHostState() }
    val loading = remember { mutableStateOf(false) }
    val toastHolderService: ToastHolderService by guice()
    toastHolderService.snackbarHostState = snackbarHostState
    loadSavedProperties()

    MaterialTheme(
        colors = MaterialTheme.colors.copy(primary = Color(0xFF1976D2)),
        typography = loadTypography()
    ) {
        when (stateView) {
            StateView.LOGIN -> LoginView(snackbarHostState, loading) { stateView = it }
            StateView.SET_MUSIC_VIEW -> SetMusicView(snackbarHostState, loading) { stateView = it }
            StateView.SETTING -> SettingView(snackbarHostState, loading) { stateView = it }
        }
        CustomSnackbarHost(snackbarHostState)
        LoadingDialog(loading)
    }
}

fun main() {
    val guice = Guice.createInjector(EventModule())
    DI.dicontainer = object : DI.DIContainer {
        override fun <T : Any> getInstance(type: KClass<T>)
                = guice.getInstance(type.java)
    }

    application {
        Window(
            onCloseRequest =
            {
                saveProperties()
                GlobalScreen.unregisterNativeHook()
                exitApplication()
            },
            title = "Luna",
            resizable = false,
            state = WindowState().apply {
                size = DpSize(1100.dp, 700.dp)
                position = WindowPosition(Alignment.Center)
            },
            icon = BitmapPainter(useResource("icon.png", ::loadImageBitmap))
        ) {
            App()
        }
    }
}
