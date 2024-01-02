package com.rafambn.frameprogressbar

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.ComponentType
import com.rafambn.frameprogressbar.enums.Movement
import com.rafambn.frameprogressbar.enums.PointerSelection
import kotlin.math.floor
import kotlin.math.max

@Composable
fun FrameProgressBar(
    modifier: Modifier = Modifier,
    pointerSelection: PointerSelection = PointerSelection.CENTER,
    coercedPointer: CoercePointer = CoercePointer.NOT_COERCED,
    pointer: Marker = Marker(
        width = 8.dp,
        height = 40.dp,
        topOffset = 5.dp,
        color = Color.Yellow
    ),
    markers: List<Marker>,
    value: Float,
    onValueChange: (Float) -> Unit,  //TODO add range
    onValueChangeStarted: (() -> Unit)? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null //TODO test this
) {
    FrameProgressBarBase(
        modifier = modifier,
        movement = Movement.CONTINUOUS,
        pointerSelection = pointerSelection,
        coercedPointer = coercedPointer,
        pointer = pointer,
        markers = markers,
        value = value,
        onValueChange = onValueChange,
        onValueChangeStarted = onValueChangeStarted,
        onValueChangeFinished = onValueChangeFinished,
        enabled = enabled,
        interactionSource = interactionSource,
    )
}

@Composable
fun FrameProgressBar(
    modifier: Modifier = Modifier,
    pointerSelection: PointerSelection = PointerSelection.CENTER,
    pointer: Marker = Marker(
        width = 8.dp,
        height = 40.dp,
        topOffset = 5.dp,
        color = Color.Yellow
    ),
    markers: List<Marker>,
    index: Int,
    onIndexChange: (Int) -> Unit,
    onIndexChangeStarted: (() -> Unit)? = null,
    onIndexChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
) {
    val onIndexChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != index.toFloat()) {
            onIndexChange(it.toInt())
        }
    }

    FrameProgressBarBase(
        modifier = modifier,
        movement = Movement.DISCRETE,
        pointerSelection = pointerSelection,
        coercedPointer = CoercePointer.NOT_COERCED,
        pointer = pointer,
        markers = markers,
        value = index.toFloat(),
        onValueChange = onIndexChangeState.value,
        onValueChangeStarted = onIndexChangeStarted,
        onValueChangeFinished = onIndexChangeFinished,
        enabled = enabled,
        interactionSource = interactionSource,
    )
}

@Composable
private fun FrameProgressBarBase(
    modifier: Modifier = Modifier,
    movement: Movement = Movement.CONTINUOUS,
    pointerSelection: PointerSelection = PointerSelection.CENTER,
    coercedPointer: CoercePointer = CoercePointer.NOT_COERCED,
    pointer: Marker = Marker(
        width = 8.dp,
        height = 40.dp,
        topOffset = 5.dp,
        color = Color.Yellow
    ),
    markers: List<Marker>,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeStarted: (() -> Unit)? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
) {
    val density = LocalDensity.current

    val mOffsets = mutableListOf<Float>()
    mOffsets.clear()
    var tempOffset = 0F
    markers.forEach {
        mOffsets.add(tempOffset)
        tempOffset += with(density) { it.width.toPx() }
    }
    val pressedOffset = remember { mutableFloatStateOf(0F) }
    val rawOffset = remember { mutableFloatStateOf(with(density) { findOffsetTroughIndex(value, markers).dp.toPx() }) }
    val onValueChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != value) {
            onValueChange(it)
        }
    }

    val draggableState = remember {
        DraggableState { delta ->
            val preValue = rawOffset.floatValue - delta
            val coercedValue = preValue.coerceIn(0f, with(density) {
                markers.sumOf { it.width.value.toInt() }.dp.toPx() -
                        if (coercedPointer == CoercePointer.COERCED) pointer.width.toPx()
                        else 0F
            })
            val newValue = if (movement == Movement.CONTINUOUS)
                coercedValue
            else
                findIndexTroughOffset(coercedValue, mOffsets)

            onValueChangeState.value.invoke(newValue)
            rawOffset.floatValue = coercedValue
        }
    }

    Layout(
        {
            Box(modifier = Modifier.layoutId(ComponentType.POINTER)) { Pointer(pointer = pointer) }
            Box(modifier = Modifier.layoutId(ComponentType.TRACK)) { Markers(markersList = markers) }
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
            .border(1.dp, Color.Magenta)
            .clipToBounds()
            .let { modifier1 ->
                if (enabled) modifier1.draggable(
                    interactionSource = interactionSource,
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStarted = {
                        pressedOffset.floatValue = 0F
                        onValueChangeStarted?.invoke()
                    },
                    onDragStopped = {
                        pressedOffset.floatValue = 0F
                        onValueChangeFinished?.invoke()
                    })
                else modifier1
            }
            .focusable(enabled)
    ) { measures, constraints ->

        val pointerPlaceable = measures.first {
            it.layoutId == ComponentType.POINTER
        }.measure(constraints)


        val markersPlaceable = measures.first {
            it.layoutId == ComponentType.TRACK
        }.measure(constraints)

        val progressBarWidth = markersPlaceable.width
        val progressBarHeight = max(markersPlaceable.height, pointerPlaceable.height)
        val halfPointerWidth = floor(pointer.width.toPx() / 2).toInt()
        val halfProgressBarWidth = progressBarWidth / 2

        val pointerOffsetX = halfProgressBarWidth - halfPointerWidth
        val pointerOffsetY = 0

        val markersOffsetX = pointerOffsetX + when (pointerSelection) {
            PointerSelection.LEFT -> 0
            PointerSelection.CENTER -> halfPointerWidth
            PointerSelection.RIGHT -> pointer.width.toPx().toInt()
        }
        val markersOffsetY = 0

        layout(
            progressBarWidth,
            progressBarHeight
        ) {
            markersPlaceable.placeRelative(
                markersOffsetX
                        -
                        if (movement == Movement.CONTINUOUS) {
                            value.coerceIn(
                                0F, markers.sumOf { it.width.value.toInt() }.dp.toPx() -
                                        if (coercedPointer == CoercePointer.COERCED) pointer.width.toPx()
                                        else 0F
                            ).toInt()
                        } else {
                            findOffsetTroughIndex(value, markers).dp.toPx().toInt()
                        }
                        - if (coercedPointer == CoercePointer.COERCED) when (pointerSelection) {
                    PointerSelection.LEFT -> 0
                    PointerSelection.CENTER -> halfPointerWidth
                    PointerSelection.RIGHT -> pointer.width.toPx().toInt()
                } else 0,
                markersOffsetY
            )
            pointerPlaceable.placeRelative(
                pointerOffsetX,
                pointerOffsetY
            )
        }
    }
}

fun findIndexTroughOffset(offset: Float, listOffset: List<Float>): Float {
    val index = listOffset.indexOfLast { offset >= it }
    return if (index != -1) index.toFloat() else 0F
}

fun findOffsetTroughIndex(selectedIndex: Float, markers: List<Marker>): Float {
    var starOffset = 0F
    markers.forEachIndexed { index, marker ->
        if (selectedIndex == index.toFloat()) {
            starOffset += marker.width.value / 2
            return starOffset
        } else starOffset += marker.width.value
    }
    return starOffset
}