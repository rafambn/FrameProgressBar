package com.rafambn.frameprogressbar.managers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.rafambn.frameprogressbar.Marker
import com.rafambn.frameprogressbar.dpToPixel
import com.rafambn.frameprogressbar.drawRectWithOffset

internal class MarkerManager(private var screenScale: Float) {
    private var mMarkersList = mutableListOf<Marker>()
    private var mOffsets = mutableListOf<Float>()
    val markerWidth
        get() = dpToPixel(mMarkersList.sumOf { it.width }, screenScale)
    val markerTotalHeight
        get() = dpToPixel(mMarkersList.maxOf { it.height + it.topOffset }, screenScale)

    internal fun drawMarkers(currentOffset: Float, canvas: Canvas, paint: Paint) {
        mMarkersList.forEachIndexed { index, marker ->
            marker.bitmap?.let { bitmap ->
                paint.alpha = 255
                canvas.drawBitmap(
                    bitmap,
                    currentOffset,
                    dpToPixel(marker.topOffset, screenScale).toFloat(),
                    paint
                )
            } ?: run {
                paint.color = marker.color
                canvas.drawRectWithOffset(
                    dpToPixel(marker.width, screenScale).toFloat(),
                    dpToPixel(marker.height, screenScale).toFloat(),
                    dpToPixel(marker.topOffset, screenScale).toFloat(),
                    currentOffset + mOffsets[index],
                    paint
                )
            }
        }
    }


    fun createMarkers(numberOfMarkers: Int) {
        mMarkersList.clear()
        for (i in 0 until numberOfMarkers) {
            mMarkersList.add(Marker(width = 20, height = 10))
        }

        mMarkersList.forEachIndexed { index, marker ->
            if (index == 0 || index == mMarkersList.size - 1) {
                marker.color = Color.WHITE
                marker.height = marker.height * 2
            } else if (index % 2 == 0)
                marker.color = Color.GRAY
            else
                marker.color = Color.TRANSPARENT
        }

        updateMarkers()
    }

    private fun updateMarkers() {
        mOffsets.clear()
        var tempOffset = 0F
        mMarkersList.forEach {
            mOffsets.add(tempOffset)
            tempOffset += dpToPixel(it.width, screenScale)
        }
    }

    fun findIndexTroughOffset(offset: Float): Int {
        val index = mOffsets.indexOfLast { offset >= it }
        return if (index != -1) index else 0
    }

    fun findOffsetTroughIndex(mSelectedIndex: Int): Float {
        var starOffset = 0F
        mMarkersList.forEachIndexed { index, marker ->
            if (mSelectedIndex == index) {
                starOffset += dpToPixel(marker.width, screenScale) / 2
                return starOffset
            } else starOffset += dpToPixel(marker.width, screenScale)
        }
        return starOffset
    }
}