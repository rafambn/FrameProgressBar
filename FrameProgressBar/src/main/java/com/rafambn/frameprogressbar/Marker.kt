package com.rafambn.frameprogressbar

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Marker(
    var width: Dp = 5.dp,
    var height: Dp = 5.dp,
    var topOffset: Dp = 0.dp,
    var color: Color = Color.Gray,
    var bitmap: ImageBitmap? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Marker) return false

        if (width != other.width) return false
        if (height != other.height) return false
        if (topOffset != other.topOffset) return false
        if (color != other.color) return false
        if (bitmap != other.bitmap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + topOffset.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + (bitmap?.hashCode() ?: 0)
        return result
    }
}

