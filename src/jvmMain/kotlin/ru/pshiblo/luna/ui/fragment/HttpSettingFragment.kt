package ru.pshiblo.luna.ui.fragment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.ServiceContext
import ru.pshiblo.luna.services.ServiceType
import ru.pshiblo.luna.ui.components.BasicButton
import ru.pshiblo.luna.ui.support.guice

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun HttpSettingFragment(
    snackbarHostState: SnackbarHostState,
    loading: MutableState<Boolean>
) {
    val serviceContext: ServiceContext by guice()

    var isRun by remember { mutableStateOf(serviceContext.isInitServices(ServiceType.HTTP)) }
    var port by remember { mutableStateOf(ApplicationProperties.obsServerPort) }

    val scope = rememberCoroutineScope {
        newSingleThreadContext("setting-view")
    }

    suspend fun validate(): Boolean {
        if (ApplicationProperties.obsServerPort <= 1000) {
            loading.value = false
            snackbarHostState.showSnackbar("Порт должен быть больше 1000")
            return false
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
                text = "Сервер для OBS (возвращает играющий трек)",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = port.toString(),
                onValueChange =
                {
                    port = it.toIntOrNull() ?: port
                    ApplicationProperties.obsServerPort = port
                },
                label = {Text(
                    text = "Порт, по которому будет доступен сервер",
                    style = MaterialTheme.typography.body2
                )},
                readOnly = isRun,
                enabled = !isRun,
                modifier = Modifier.size(width = 400.dp, height = TextFieldDefaults.MinHeight)
            )
            if (isRun) {
                Divider()
                Text(
                    text = "Трек можно получить по URL http://localhost:${ApplicationProperties.obsServerPort}/track",
                    style = MaterialTheme.typography.body2
                )
            }
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
                                serviceContext.startServices(ServiceType.HTTP)
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
                            serviceContext.shutdownServices(ServiceType.HTTP)
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