package ru.pshiblo.luna.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalUnitApi::class)
@Composable
fun BasicButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    sizeIcon: Dp = 32.dp,
    sizeText: Float = 18f,
    modifier: Modifier = Modifier,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .pointerHoverIcon(PointerIconDefaults.Hand),
        colors = buttonColors
    ) {
        Text(
            text = text,
            fontSize = TextUnit(sizeText, TextUnitType.Sp)
        )

        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier
                    .size(sizeIcon)
                    .padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalUnitApi::class)
@Composable
fun BasicOutlinedButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    sizeIcon: Dp = 32.dp,
    sizeText: Float = 18f,
    modifier: Modifier = Modifier,
    iconLeft: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .pointerHoverIcon(PointerIconDefaults.Hand)
    ) {
        if (iconLeft && icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier
                    .size(sizeIcon)
                    .padding(4.dp)
            )
        }
        Text(
            text = text,
            fontSize = TextUnit(sizeText, TextUnitType.Unspecified)
        )
        if (!iconLeft && icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier
                    .size(sizeIcon)
                    .padding(4.dp)
            )
        }
    }
}