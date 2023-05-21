package ru.pshiblo.luna.ui.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.Angry
import compose.icons.fontawesomeicons.solid.ArrowLeft
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.PlatformState
import ru.pshiblo.luna.services.ServiceContext
import ru.pshiblo.luna.services.scope.YouTubeScope
import ru.pshiblo.luna.ui.components.BasicOutlinedButton
import ru.pshiblo.luna.ui.components.ExternalImage
import ru.pshiblo.luna.ui.components.Tabs
import ru.pshiblo.luna.ui.fragment.ConsoleFragment
import ru.pshiblo.luna.ui.fragment.HttpSettingFragment
import ru.pshiblo.luna.ui.fragment.MainSettingFragment
import ru.pshiblo.luna.ui.support.guice
import ru.pshiblo.luna.ui.views.state.StateView

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SettingView(
    snackbarHostState: SnackbarHostState,
    loading: MutableState<Boolean>,
    changeView: (StateView) -> Unit
) {
    val scope = rememberCoroutineScope {
        newSingleThreadContext("setting-view")
    }

    val youtubeScope: YouTubeScope by guice()
    val serviceContext: ServiceContext by guice()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = ApplicationProperties.userInfo.username,
                    fontSize = MaterialTheme.typography.h5.fontSize
                )

                ExternalImage(
                    url = ApplicationProperties.userInfo.picture,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)                       // clip to the circle shape
                        .border(1.dp, Color.LightGray, CircleShape)
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Не удалось загрузить аватар")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BasicOutlinedButton(
                    text = "Назад",
                    onClick = {
                        loading.value = true
                        scope.launch {
                            serviceContext.shutdownAllServices()
                            changeView(StateView.SET_MUSIC_VIEW)
                            loading.value = false
                        }
                    },
                    icon = FontAwesomeIcons.Solid.ArrowLeft,
                    sizeIcon = 24.dp,
                    iconLeft = true
                )
                if (ApplicationProperties.applicationState.platformState == PlatformState.YOUTUBE) {
                    BasicOutlinedButton(
                        text = "Выйти из аккаунта",
                        onClick = {
                            loading.value = true
                            scope.launch {
                                serviceContext.shutdownAllServices()
                                youtubeScope.exit()
                                changeView(StateView.LOGIN)
                                loading.value = false
                            }
                        },
                        icon = FontAwesomeIcons.Regular.Angry,
                        sizeIcon = 24.dp,
                        iconLeft = true
                    )
                }
            }
        }

        Divider(modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .padding(vertical = 8.dp))

        Tabs(
            modifier = Modifier.padding(8.dp),
            tabs = listOf("Настройки", "Сервер для OBS", "Консоль")
        ) {
            when (it) {
                "Настройки" -> MainSettingFragment(snackbarHostState, loading)
                "Сервер для OBS" -> HttpSettingFragment(snackbarHostState, loading)
                "Консоль" -> ConsoleFragment()
            }
        }
    }
}