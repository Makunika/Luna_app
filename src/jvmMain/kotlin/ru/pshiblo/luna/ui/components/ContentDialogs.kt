package ru.pshiblo.luna.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ContentDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    title: String = "",
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = {
                onClose()
            },
            title = {
                Text(title)
            },
            text = {
                if (content != null) {
                    content()
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
                        }
                    ) {
                        Text("Ок")
                    }
                }
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingDialog(
    visibility: MutableState<Boolean>
) {
    if (visibility.value) {
        AlertDialog(
            onDismissRequest = { },
            buttons = { },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            },
            modifier = Modifier.size(100.dp)
        )
    }
}

