package ru.pshiblo.luna.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Google
import compose.icons.fontawesomeicons.brands.Twitch
import ru.pshiblo.luna.events.model.SetPlatformEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import org.kohsuke.github.GHRelease
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.PlatformState
import ru.pshiblo.luna.services.scope.TwitchScope
import ru.pshiblo.luna.services.scope.YouTubeScope
import ru.pshiblo.luna.services.gihub.UpdateApplicationService
import ru.pshiblo.luna.ui.components.BasicButton
import ru.pshiblo.luna.ui.components.BasicOutlinedButton
import ru.pshiblo.luna.ui.components.ContentDialog
import ru.pshiblo.luna.ui.components.LinkText
import ru.pshiblo.luna.ui.components.TwitchTokenDialog
import ru.pshiblo.luna.ui.support.applicationEventHandler
import ru.pshiblo.luna.ui.support.guice
import ru.pshiblo.luna.ui.views.state.StateView
import ru.pshiblo.luna.utils.exceptionHandler

@OptIn(ExperimentalUnitApi::class, DelicateCoroutinesApi::class)
@Composable
fun LoginView(
    snackbarHostState: SnackbarHostState,
    loading: MutableState<Boolean>,
    changeView: (StateView) -> Unit
) {
    var openDialogTwitch by remember { mutableStateOf(false) }
    val tokenTwitch = remember { mutableStateOf(ApplicationProperties.twitchToken) }
    var githubRelease: GHRelease? by rememberSaveable { mutableStateOf(null) }
    var showUpdate by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope {
        newSingleThreadContext("my")
    }

    val eventHandler by applicationEventHandler()
    val updateApplicationService: UpdateApplicationService by guice()
    val twitchScope: TwitchScope by guice()
    val youTubeScope: YouTubeScope by guice()

    LaunchedEffect(true) {
        githubRelease = async {
            updateApplicationService.getUpdate()
        }.await()
        if (githubRelease != null) {
            showUpdate = true
        }
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(50.dp),
                verticalAlignment = Alignment.Top
            ) {
                BasicButton(
                    text = "Войти",
                    icon = FontAwesomeIcons.Brands.Google,
                    onClick =
                    {
                        loading.value = true
                        scope.launch {
                            runCatching {
                                youTubeScope.auth()
                            }.onSuccess {
                                loading.value = false
                                eventHandler.publish(SetPlatformEvent(PlatformState.YOUTUBE))
                                changeView(StateView.SET_MUSIC_VIEW)
                            }.onFailure { thr ->
                                loading.value = false
                                exceptionHandler(snackbarHostState, thr)
                            }
                        }
                    },
                )

                BasicButton(
                    text = "Войти",
                    icon = FontAwesomeIcons.Brands.Twitch,
                    onClick =
                    {
                        openDialogTwitch = true
                    },
                )
            }

            githubRelease?.let {
                BasicOutlinedButton(
                    text = "Новое обновление!",
                    sizeText = 10f,
                    onClick = {
                        showUpdate = true
                    }
                )
            }

            Divider(modifier = Modifier.padding(horizontal = 300.dp))

            LinkText(
                text = "Условия использования YouTube",
                url = "https://www.youtube.com/t/terms"
            )

            LinkText(
                text = "Политика конфиденциальности Google",
                url = "https://policies.google.com/privacy"
            )

            LinkText(
                text = "Страница настроек безопасности Google",
                url = "https://myaccount.google.com/permissions"
            )

            TwitchTokenDialog(
                isOpen = openDialogTwitch,
                onClose = {openDialogTwitch = false},
                token = tokenTwitch
            ) {
                loading.value = true
                ApplicationProperties.twitchToken = it
                scope.launch {
                    runCatching {
                        twitchScope.auth()
                    }.onSuccess {
                        loading.value = false
                        eventHandler.publish(SetPlatformEvent(PlatformState.TWITCH))
                        changeView(StateView.SET_MUSIC_VIEW)
                    }.onFailure { thr ->
                        loading.value = false
                        exceptionHandler(snackbarHostState, thr)
                    }
                }
            }

            ContentDialog(
                isOpen = showUpdate,
                onClose = {
                          showUpdate = false
                },
                title = "Доступно новое обновление!",
                modifier = Modifier.width(400.dp)
            ) {
                githubRelease?.let {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(it.body)
                        Text(ApplicationProperties.version)
                        LinkText(
                            it.htmlUrl.toExternalForm(),
                            "Скачать"
                        )
                    }
                }
            }
        }
    }
}