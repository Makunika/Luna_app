package ru.pshiblo.luna.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.pshiblo.luna.utils.openDocument

data class LinkTextData(
    val text: String,
    val tag: String? = null,
    val annotation: String? = null,
    val onClick: ((str: AnnotatedString.Range<String>) -> Unit)? = null,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LinkText(
    url: String,
    text: String,
) {
    var decoration by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ClickableText(
        text = AnnotatedString(
            text = text,
            spanStyle = SpanStyle(
                color = MaterialTheme.colors.primary,
                textDecoration = if (decoration) TextDecoration.Underline else TextDecoration.None
            )
        ),
        onClick = {
            scope.launch {
                withContext(Dispatchers.IO) {
                    openDocument(url)
                }
            }
        },
        modifier = Modifier.pointerHoverIcon(PointerIconDefaults.Hand)
            .onPointerEvent(PointerEventType.Enter) {
                decoration = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                decoration = false
            }
            .padding(5.dp)
    )
}