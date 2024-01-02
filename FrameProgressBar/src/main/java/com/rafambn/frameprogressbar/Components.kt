package com.rafambn.frameprogressbar

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize

@Composable
fun Pointer(
    modifier: Modifier = Modifier,
    pointer: Marker = Marker(
        width = 5.dp,
        height = 40.dp,
        topOffset = 0.dp,
        color = Color.Yellow
    )
) {
    Spacer(
        modifier
            .size(pointer.width, pointer.height + pointer.topOffset)
            .drawBehind {
                pointer.bitmap?.let { bitmap ->
                    drawImage(
                        image = bitmap,
                        topLeft = Offset(
                            pointer.width.toPx(),
                            pointer.topOffset.toPx()
                        )
                    )
                } ?: run {
                    drawRect(
                        color = pointer.color,
                        topLeft = Offset(
                            0F,
                            pointer.topOffset.toPx()
                        ),
                        size = DpSize(pointer.width, pointer.height).toSize()
                    )
                }
            }
    )
}

@Composable
fun Markers(
    modifier: Modifier = Modifier,
    markersList: List<Marker>
) {
    val mOffsets = mutableListOf<Dp>()
    mOffsets.clear()
    var tempOffset: Dp = 0.dp
    markersList.forEach {
        mOffsets.add(tempOffset)
        tempOffset += it.width
    }
    Spacer(
        modifier
            .size(
                markersList.sumOf { it.width.value.toInt() }.dp,
                markersList.maxOf { it.height + it.topOffset }
            )
            .drawBehind {
                markersList.forEachIndexed { index, marker ->
                    marker.bitmap?.let { bitmap ->
                        drawImage(
                            image = bitmap,
                            topLeft = Offset(mOffsets[index].toPx(), marker.topOffset.toPx())
                        )
                    } ?: run {
                        drawRect(
                            color = marker.color,
                            topLeft = Offset(mOffsets[index].toPx(), marker.topOffset.toPx()),
                            size = DpSize(marker.width, marker.height).toSize()
                        )
                    }
                }
            }
    )
}


