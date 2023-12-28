package com.rafambn.frameprogressbar.composablePart

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.Movement
import com.rafambn.frameprogressbar.enums.PointerSelection
import kotlinx.coroutines.coroutineScope
import kotlin.math.max
import kotlin.math.min

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
    var localValue by remember { mutableFloatStateOf(value) }
    val onValueChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != value) {
            onValueChange(it)
        }
    }

    LaunchedEffect(value) {
        localValue = value
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
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newValue = localValue + dragAmount.x
                    val coercedValue = newValue.coerceIn(0f, markers.sumOf { it.width.value.toInt() }.dp.toPx())
                    onValueChangeState.value.invoke(coercedValue)
                }
            }
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
                markersOffsetX - localValue.toInt(),
                markersOffsetY
            )
            pointerPlaceable.placeRelative(
                pointerOffsetX,
                pointerOffsetY
            )
        }
    }
}