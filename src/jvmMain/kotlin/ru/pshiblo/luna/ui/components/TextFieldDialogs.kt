package ru.pshiblo.luna.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@Composable
fun TwitchTokenDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    token: MutableState<String>,
    saveToken: (String) -> Unit
) {
    PasswordTextFieldDialog(
        isOpen = isOpen,
        onClose = onClose,
        password = token,
        savePassword = saveToken,
        placeholderTextField = "Введите токен Twitch"
    ) {
        LinkText(
            text = "Откуда взять токен Twitch",
            url = "https://twitchapps.com/tmi"
        )
        Text("Пожалуйста, используйте не свой главный аккаунт")
    }
}

@Composable
fun DiscordTokenDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    token: MutableState<String>,
    saveToken: (String) -> Unit
) {
    PasswordTextFieldDialog(
        isOpen = isOpen,
        onClose = onClose,
        password = token,
        savePassword = saveToken,
        placeholderTextField = "Введите токен Discord"
    ) {
        LinkText(
            text = "Что это?",
            url = "https://twitchapps.com/tmi"
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PasswordTextFieldDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    password: MutableState<String>,
    savePassword: (String) -> Unit,
    placeholderTextField: String,
    additional: @Composable (() -> Unit)? = null
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = {
                onClose()
            },
            title = {
                Text("")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PasswordTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        placeholder = { Text(placeholderTextField) }
                    )
                    if (additional != null) {
                        additional()
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth()
                            .pointerHoverIcon(PointerIconDefaults.Hand),
                        onClick = {
                            onClose()
                            savePassword(password.value)
                        }
                    ) {
                        Text("Продолжить")
                    }
                }
            }
        )
    }
}

