package com.rafambn.frameprogressbar.composablePart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MarkerCompose(
    var width: Dp = 5.dp,
    var height: Dp = 5.dp,
    var topOffset: Dp = 0.dp,
    var color: Color = Color.Gray,
    var bitmap: ImageBitmap? = null
)