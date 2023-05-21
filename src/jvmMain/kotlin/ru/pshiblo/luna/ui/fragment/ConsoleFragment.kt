package ru.pshiblo.luna.ui.fragment

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.pshiblo.luna.log.UiLogAppender.Companion.UI_CONSOLE

@Composable
fun ConsoleFragment() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 8.dp)
    ) {
        OutlinedTextField(
            value = UI_CONSOLE.toString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxSize(),
            textStyle = MaterialTheme.typography.body2
        )
    }
}