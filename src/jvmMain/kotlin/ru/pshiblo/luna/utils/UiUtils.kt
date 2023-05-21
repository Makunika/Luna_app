package ru.pshiblo.luna.utils

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import java.awt.Desktop
import java.net.URI

fun openDocument(url: String) {
    Desktop.getDesktop().browse(URI(url))
}

suspend inline fun exceptionHandler(hostState: SnackbarHostState, throwable: Throwable) {
    throwable.printStackTrace()
    hostState.showSnackbar(throwable.message ?: "Произошла ошибка", "error")
}

fun loadTypography(): Typography {
    val robotoFont = FontFamily(
        Font(getResourceAsFile("/font/Roboto-Black.ttf")!!, FontWeight.Black),
        Font(getResourceAsFile("/font/Roboto-BlackItalic.ttf")!!, FontWeight.Black, FontStyle.Italic),
        Font(getResourceAsFile("/font/Roboto-Bold.ttf")!!, FontWeight.Bold),
        Font(getResourceAsFile("/font/Roboto-BoldItalic.ttf")!!, FontWeight.Bold, FontStyle.Italic),
        Font(getResourceAsFile("/font/Roboto-Italic.ttf")!!, style = FontStyle.Italic),
        Font(getResourceAsFile("/font/Roboto-Light.ttf")!!, FontWeight.Light),
        Font(getResourceAsFile("/font/Roboto-LightItalic.ttf")!!, FontWeight.Light, FontStyle.Italic),
        Font(getResourceAsFile("/font/Roboto-Medium.ttf")!!, FontWeight.Medium),
        Font(getResourceAsFile("/font/Roboto-MediumItalic.ttf")!!, FontWeight.Medium, FontStyle.Italic),
        Font(getResourceAsFile("/font/Roboto-Regular.ttf")!!),
        Font(getResourceAsFile("/font/Roboto-Thin.ttf")!!, FontWeight.Thin),
        Font(getResourceAsFile("/font/Roboto-ThinItalic.ttf")!!, FontWeight.Thin, FontStyle.Italic),
    )
    return Typography(
        defaultFontFamily = robotoFont
    )
}
