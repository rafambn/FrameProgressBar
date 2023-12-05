package com.rafambn.frameprogressbar.managers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.rafambn.frameprogressbar.Marker
import com.rafambn.frameprogressbar.dpToPixel
import com.rafambn.frameprogressbar.drawRectWithOffset

class PointerManager(private var screenScale: Float) {
    var pointer = Marker(color = Color.YELLOW, height = 40, topOffset = 10, width = 10)

    val pointerTotalHeight
        get() = dpToPixel(pointer.height + pointer.topOffset, screenScale)
    fun drawPointer(mViewCenter: Float, canvas: Canvas, paint: Paint) {
        pointer.bitmap?.let { bitmap ->
            paint.alpha = 255
            canvas.drawBitmap(
                bitmap,
                mViewCenter - dpToPixel(pointer.width, screenScale) / 2,
                dpToPixel(pointer.topOffset, screenScale).toFloat(),
                paint
            )
        } ?: run {
            paint.color = pointer.color
            canvas.drawRectWithOffset(
                dpToPixel(pointer.width, screenScale).toFloat(),
                dpToPixel(pointer.height, screenScale).toFloat(),
                dpToPixel(pointer.topOffset, screenScale).toFloat(),
                mViewCenter - dpToPixel(pointer.width, screenScale) / 2,
                paint
            )
        }
    }
}