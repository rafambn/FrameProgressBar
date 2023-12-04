package com.rafambn.frameprogressbar

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class FrameProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs), FrameProgressBarApi {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var startOffset = currentOffset
        markersList.forEach { marker ->
            marker.image?.let { bitmap ->
                paint.alpha = 255
                canvas.drawBitmap(
                    bitmap,
                    startOffset.toFloat(),
                    dpToPixel(marker.topOffsetInDp, screenScale).toFloat(),
                    paint
                )
            } ?: run {
                paint.color = marker.color
                canvas.drawRectOffset(
                    dpToPixel(marker.widthInDp, screenScale),
                    dpToPixel(marker.heightInDp, screenScale),
                    dpToPixel(marker.topOffsetInDp, screenScale),
                    startOffset,
                    paint
                )
            }
            startOffset += dpToPixel(marker.widthInDp, screenScale)
        }

        pointer.image?.let { bitmap ->
            paint.alpha = 255
            canvas.drawBitmap(
                bitmap,
                viewCenter.toFloat(),
                dpToPixel(pointer.topOffsetInDp, screenScale).toFloat(),
                paint
            )
        } ?: run {
            paint.color = pointer.color
            canvas.drawRectOffset(
                dpToPixel(pointer.widthInDp, screenScale),
                dpToPixel(pointer.heightInDp, screenScale),
                dpToPixel(pointer.topOffsetInDp, screenScale),
                when (pointerDirection) {
                    PointerDirection.LEFT -> viewCenter
                    PointerDirection.CENTER -> viewCenter - dpToPixel(pointer.widthInDp, screenScale)/2
                    PointerDirection.RIGHT -> viewCenter - dpToPixel(pointer.widthInDp, screenScale)
                },
                paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidthInPixels = dpToPixel(markersList.sumOf { it.widthInDp }, screenScale)
        val desiredHeightInPixels =
            dpToPixel(maxOf(markersList.maxOf { it.heightInDp + it.topOffsetInDp }, pointer.heightInDp + pointer.topOffsetInDp), screenScale)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidthInPixels.coerceAtMost(widthSize)
            else -> desiredWidthInPixels
        }

        viewCenter = width / 2

        currentOffset = viewCenter - offsets[selectedIndex]

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeightInPixels.coerceAtMost(heightSize)
            else -> desiredHeightInPixels
        }

        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.x
                startTouchOffset = currentOffset
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val newOffset = (startTouchOffset + event.x - initialTouchX).toInt()
                currentOffset = newOffset.coerceIn(viewCenter - movedDistance, viewCenter)

                selectedIndex = findIndexTroughOffset(currentOffset)
                Log.e("Select", selectedIndex.toString())

                if (movement == Movement.DISCRETE)
                    currentOffset = viewCenter - offsets[selectedIndex]

                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun findIndexTroughOffset(offset: Int): Int {
        val amountMoved = viewCenter - offset
        offsets.forEachIndexed { index, currentOffset ->
            if (amountMoved <= currentOffset)
                return index
        }
        return 0
    }

    private fun createMarkers() {
        markersList.clear()
        for (i in 0 until numberOfMarkers) {
            markersList.add(Marker())
        }

        markersList.forEachIndexed { index, marker ->
            if (index == 0 || index == markersList.size - 1) {
                marker.color = Color.WHITE
                marker.heightInDp = marker.heightInDp * 2
            } else if (index % 2 == 0)
                marker.color = Color.GRAY
            else
                marker.color = Color.TRANSPARENT
        }

        updateMarkers()
    }

    private fun updateMarkers() {
        movedDistance = dpToPixel(markersList.asReversed().dropLast(1).sumOf { it.widthInDp }, screenScale)

        offsets.clear()
        var tempOffset = 0
        markersList.forEach {
            offsets.add(tempOffset)
            tempOffset += dpToPixel(it.widthInDp, screenScale)
        }
        invalidate()
    }