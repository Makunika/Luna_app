package ru.pshiblo.luna.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(snackbarHostState, modifier = Modifier.offset(y = (-8).dp, x = 8.dp)) { snackbarData ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            Snackbar(
                modifier = Modifier.width(maxWidth / 3),
                backgroundColor = when (snackbarData.actionLabel) {
                    "error" -> MaterialTheme.colors.error
                    else -> MaterialTheme.colors.primary
                },
                contentColor = when (snackbarData.actionLabel) {
                    "error" -> MaterialTheme.colors.onError
                    else -> MaterialTheme.colors.onPrimary
                }
            ) {
                Text(snackbarData.message)
            }
        }
    }
}