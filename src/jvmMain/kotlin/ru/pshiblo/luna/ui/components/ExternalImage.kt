package ru.pshiblo.luna.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.skia.Image
import java.net.URL
import androidx.compose.foundation.Image as ComposeImage

fun loadPicture(url: String): Result<ImageBitmap> {
    return try {
        Result.success(
            Image.makeFromEncoded(URL(url).readBytes())
                .toComposeImageBitmap()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}

@Composable
fun ExternalImage(url: String, modifier: Modifier = Modifier, onFail: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var hasFail by remember { mutableStateOf(false) }
    var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }

    LaunchedEffect(url) {
        isLoading = true
        println(url)
        loadPicture(url)
            .onSuccess {
                imageBitmap = it
            }
            .onFailure {
                hasFail = true
            }
        isLoading = false
    }

    when {
        isLoading -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }
        hasFail -> {
            onFail()
        }
        else -> {
            imageBitmap?.let { bitmap ->

                ComposeImage(
                    bitmap = bitmap,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                )

            }
                ?:
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}