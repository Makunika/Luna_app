package ru.pshiblo.luna.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import ru.pshiblo.luna.ui.components.BasicButton
import ru.pshiblo.luna.ui.components.DiscordTokenDialog
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Discord
import ru.pshiblo.luna.events.model.SetMusicEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.MusicState
import ru.pshiblo.luna.services.scope.DiscordScope
import ru.pshiblo.luna.ui.support.applicationEventHandler
import ru.pshiblo.luna.ui.support.guice
import ru.pshiblo.luna.ui.views.state.StateView
import ru.pshiblo.luna.utils.exceptionHandler

@OptIn(ExperimentalUnitApi::class, ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
@Composable
fun SetMusicView(
    snackbarHostState: SnackbarHostState,
    loading: MutableState<Boolean>,
    changeView: (StateView) -> Unit
) {

    var openDialogDiscord by remember { mutableStateOf(false) }
    val tokenDiscord = remember { mutableStateOf(ApplicationProperties.discordToken) }

    val discordScope: DiscordScope by guice()
    val eventHandler by applicationEventHandler()

    val scope = rememberCoroutineScope {
        newSingleThreadContext("my")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = "Luna",
                color = Color(0xFF4DA5F6),
                fontSize = TextUnit(36f, TextUnitType.Sp),
            )

            BasicButton(
                text = "Использовать локальную музыку",
                onClick =
                {
                    eventHandler.publish(SetMusicEvent(MusicState.LOCAL))
                    changeView(StateView.SETTING)
                },
            )

            Divider(modifier = Modifier.padding(horizontal = 300.dp))

            BasicButton(
                text = "Использовать дискорд бота для музыки",
                icon = FontAwesomeIcons.Brands.Discord,
                onClick =
                {
                    openDialogDiscord = true
                },
            )

            IconButton(
                onClick = {
                    changeView(StateView.LOGIN)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    modifier = Modifier
                        .pointerHoverIcon(PointerIconDefaults.Hand)
                        .size(32.dp)
                        .padding(4.dp)
                )
            }

            DiscordTokenDialog(
                isOpen = openDialogDiscord,
                onClose = {openDialogDiscord = false},
                token = tokenDiscord
            ) {
                loading.value = true
                ApplicationProperties.discordToken = it
                scope.launch {
                    runCatching {
                        discordScope.auth()
                    }.onSuccess {
                        loading.value = false
                        eventHandler.publish(SetMusicEvent(MusicState.DISCORD))
                        changeView(StateView.SETTING)
                    }.onFailure { thr ->
                        loading.value = false
                        exceptionHandler(snackbarHostState, thr)
                    }
                }
            }
        }
    }
}