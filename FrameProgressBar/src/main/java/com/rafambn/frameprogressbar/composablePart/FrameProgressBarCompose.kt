package com.rafambn.frameprogressbar.composablePart

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.Movement
import com.rafambn.frameprogressbar.enums.PointerSelection
import kotlin.math.max

@Composable
fun FrameProgressBarCompose(
    modifier: Modifier = Modifier,
    movement: Movement = Movement.CONTINUOUS,
    pointerSelection: PointerSelection = PointerSelection.CENTER,
    coercedPointer: CoercePointer = CoercePointer.NOT_COERCED,
    pointer: MarkerCompose = MarkerCompose(
        width = 5.dp,
        height = 40.dp,
        topOffset = 10.dp,
        color = Color.Yellow
    ),
    markers: List<MarkerCompose>,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeStarted: (() -> Unit)?,
    onValueChangeFinished: (() -> Unit)?,
    enabled: Boolean = true,
) {
    val density = LocalDensity.current
    val onValueChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != value) {
            onValueChange(it)
        }
    }

    Layout(
        {
            Box(modifier = Modifier.layoutId(FrameProgressBarComponents.POINTER)) { Pointer(pointer = pointer) }
            Box(modifier = Modifier.layoutId(FrameProgressBarComponents.TRACK)) { Markers(markersList = markers) }
        },
        modifier = modifier
            .minimumInteractiveComponentSize()
            .wrapContentSize()
            .requiredSizeIn(
                minWidth = markers.sumOf { it.width.value.toInt() }.dp,
                minHeight = maxOf(
                    markers.maxOf { it.height + it.topOffset },
                    pointer.height + pointer.topOffset
                )
            )
            .focusable(enabled)
            .background(Color.Magenta)
            .clipToBounds()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    val newValue = value - delta
                    val coercedValue = newValue.coerceIn(0f, with(density) { markers.sumOf { it.width.value.toInt() }.dp.toPx() })
                    onValueChangeState.value.invoke(coercedValue)
                },
                onDragStarted = { onValueChangeStarted?.invoke() },
                onDragStopped = { onValueChangeFinished?.invoke() })
    ) { measurables, constraints ->

        val pointerPlaceable = measurables.first {
            it.layoutId == FrameProgressBarComponents.POINTER
        }.measure(constraints)


        val markersPlaceable = measurables.first {
            it.layoutId == FrameProgressBarComponents.TRACK
        }.measure(constraints)

        val progressBarWidth = markersPlaceable.width
        val progressBarHeight = max(markersPlaceable.height, pointerPlaceable.height)


        val markersOffsetX = progressBarWidth / 2
        val markersOffsetY = 0
        val pointerOffsetX = progressBarWidth / 2 - pointer.width.toPx().toInt() / 2
        val pointerOffsetY = 0

        layout(
            progressBarWidth,
            progressBarHeight
        ) {
            markersPlaceable.placeRelative(
                markersOffsetX - value.toInt(),
                markersOffsetY
            )
            pointerPlaceable.placeRelative(
                pointerOffsetX,
                pointerOffsetY
            )
        }
    }
}