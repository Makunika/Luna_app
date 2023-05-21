package ru.pshiblo.luna.ui.fragment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.pshiblo.luna.events.model.SetEnableF12Event
import ru.pshiblo.luna.events.model.SetVolumeEvent
import ru.pshiblo.luna.events.model.SkipTrackEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import ru.pshiblo.luna.services.ServiceContext
import ru.pshiblo.luna.services.ServiceType
import ru.pshiblo.luna.services.audio.MusicServiceProvider
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.MusicState
import ru.pshiblo.luna.services.properties.PlatformState
import ru.pshiblo.luna.services.scope.YouTubeScope
import ru.pshiblo.luna.ui.components.BasicButton
import ru.pshiblo.luna.ui.components.BasicOutlinedButton
import ru.pshiblo.luna.ui.support.applicationEventHandler
import ru.pshiblo.luna.ui.support.applicationState
import ru.pshiblo.luna.ui.support.guice
import ru.pshiblo.luna.utils.exceptionHandler
import kotlin.math.roundToInt

@OptIn(DelicateCoroutinesApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MainSettingFragment(
    snackbarHostState: SnackbarHostState,
    loading: MutableState<Boolean>
) {
    val applicationState by applicationState()
    val eventHandler by applicationEventHandler()
    val serviceContext: ServiceContext by guice()
    val musicServiceProvider: MusicServiceProvider by guice()
    val youTubeScope: YouTubeScope by guice()

    var videoId by remember { mutableStateOf(ApplicationProperties.channelName) }
    var enabledF12 by remember { mutableStateOf(ApplicationProperties.enabledF12) }
    var maxTimeTrack by remember { mutableStateOf(ApplicationProperties.maxTimeTrack) }
    var timePostMessageAboutBot by remember { mutableStateOf(ApplicationProperties.timePostMessageAboutBot) }
    var volumeSlider by remember { mutableStateOf(musicServiceProvider.getMusicServiceByState().volume.toFloat()) }
    var isRun by remember { mutableStateOf(serviceContext.isInitServices(ServiceType.BROADCAST)) }

    val scope = rememberCoroutineScope {
        newSingleThreadContext("setting-view")
    }

    suspend fun validate(): Boolean {
        if (ApplicationProperties.applicationState.musicState == MusicState.DISCORD &&
            !serviceContext.isInitServices(ServiceType.MUSIC)) {
            loading.value = false
            snackbarHostState.showSnackbar(
                "Забыл написать в канале Discord !connect <название канала>", "error")
            return false
        }
        if (videoId.isBlank()) {
            loading.value = false
            snackbarHostState.showSnackbar(
                "Забыл написать id трансляции/название канала", "error")
            return false
        }
        if (ApplicationProperties.applicationState.platformState == PlatformState.YOUTUBE) {
            try {
                ApplicationProperties.youtubeLiveChatId =
                    youTubeScope.getLiveChatId(ApplicationProperties.channelName)
            } catch (e: Exception) {
                loading.value = false
                exceptionHandler(snackbarHostState, e)
                return false
            }
        } else {
            snackbarHostState.showSnackbar(
                "${getPlaceholderChannelName()} нельзя проверить, если что перезапустите сервис")
        }
        return true
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Обязательные настройки перед запуском",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (applicationState.musicState == MusicState.DISCORD) {
                Text(
                    text = "Перед запуском необходимо написать в дискорд текстовом канале !connect <название голосового канал>",
                    style = MaterialTheme.typography.body2
                )
            }
            OutlinedTextField(
                value = videoId,
                onValueChange =
                {
                    videoId = it
                    ApplicationProperties.channelName = videoId
                },
                label = {Text(
                    text = getPlaceholderChannelName(),
                    style = MaterialTheme.typography.body2
                )},
                readOnly = isRun,
                enabled = !isRun,
                modifier = Modifier.width(500.dp)
            )
            Divider()
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = maxTimeTrack.toString(),
                onValueChange =
                {
                    maxTimeTrack = it.toIntOrNull() ?: maxTimeTrack
                    ApplicationProperties.maxTimeTrack = maxTimeTrack
                },
                label = {Text(
                    text = "Максимальное время одного трека (в cекундах)",
                    style = MaterialTheme.typography.body2
                )},
                modifier = Modifier.width(500.dp)
            )
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = timePostMessageAboutBot.toString(),
                onValueChange =
                {
                    timePostMessageAboutBot = it.toIntOrNull() ?: timePostMessageAboutBot
                    ApplicationProperties.timePostMessageAboutBot = timePostMessageAboutBot
                },
                label = {Text(
                    text = "Задержка между сообщениями о боте в чат (в минутах)",
                    style = MaterialTheme.typography.body2
                )},
                modifier = Modifier.width(500.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = enabledF12,
                    onCheckedChange =
                    {
                        enabledF12 = it
                        eventHandler.publish(SetEnableF12Event(it))
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIconDefaults.Hand)
                )
                Text("Использовать кнопку F12 для пропуска музыки")
            }
            Divider()
            Text("Громкость музыки ${volumeSlider.roundToInt()}%", style = MaterialTheme.typography.body2)
            Slider(
                value = volumeSlider,
                onValueChange = {
                    volumeSlider = it
                    eventHandler.publish(SetVolumeEvent(it.roundToInt()))
                },
                valueRange = 0f..100f,
                steps = 99,
                colors = SliderDefaults.colors(
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                modifier = Modifier.width(500.dp)
            )
            Divider()
            BasicOutlinedButton(
                text = "Пропустить трек",
                onClick = {
                    eventHandler.publish(SkipTrackEvent())
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (!isRun) {
                BasicButton(
                    text = "Запустить",
                    onClick = {
                        loading.value = true
                        scope.launch {
                            if (validate()) {
                                serviceContext.startServices(ServiceType.BROADCAST)
                                isRun = true
                            }
                            loading.value = false
                        }
                    }
                )
            } else {
                BasicButton(
                    text = "Остановить",
                    onClick = {
                        loading.value = true
                        scope.launch {
                            serviceContext.shutdownServices(ServiceType.BROADCAST)
                            loading.value = false
                            isRun = false
                        }
                    },
                    buttonColors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)
                )
            }
        }
    }
}

private fun getPlaceholderChannelName() =
    when (ApplicationProperties.applicationState.platformState) {
        PlatformState.YOUTUBE -> "ID трансляции YouTube"
        PlatformState.TWITCH -> "Канал Twitch"
        else -> error("Not set")
    }