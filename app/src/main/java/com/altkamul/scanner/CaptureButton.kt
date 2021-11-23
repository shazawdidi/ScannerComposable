package com.altkamul.scanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
 import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.*
import androidx.compose.material.icons.outlined.Lens
import androidx.compose.material.icons.sharp.FlipCameraIos
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.google.accompanist.glide.GlideImage

@Composable
fun CaptureButton(
    cameraViewModel: MainViewModel,
    takePhoto: () -> Unit,
    flipCamera: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val latestImageUri: String by cameraViewModel.latestImageUri.observeAsState("")

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
//

        Icon(
            Icons.Outlined.Lens,
            contentDescription = "Localized description",
            modifier = Modifier
                .size(80.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    takePhoto();
                },
            tint = Color.White
        )
        Icon(
            Icons.Sharp.FlipCameraIos,
            contentDescription = "Localized description",
            modifier = Modifier
                .size(30.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    flipCamera();
                },
            tint = Color.White
        )
    }
}

