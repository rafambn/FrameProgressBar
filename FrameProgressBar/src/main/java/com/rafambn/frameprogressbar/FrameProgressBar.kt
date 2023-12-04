package com.rafambn.frameprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.Movement
import com.rafambn.frameprogressbar.enums.PointerSelection
import com.rafambn.frameprogressbar.managers.MarkerManager
import com.rafambn.frameprogressbar.managers.PointerManager

class FrameProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs), FrameProgressBarApi {
    private val mScreenScale = context.resources.displayMetrics.density
    private val mMarkerManager = MarkerManager(mScreenScale)
    private val mPointerManager = PointerManager(mScreenScale)
    private val paint = Paint()

    private var mCurrentOffset = 0F
    private var mStartOffset = 0F
    private var mSelectedIndex = 0
    private var mViewCenter = 0F

    private var movement: Movement = Movement.CONTINUOUS
    private var pointerDirection: PointerSelection = PointerSelection.CENTER
    private var corcedPointer: CoercePointer = CoercePointer.NOT_COERCED

    private var initialTouchX = 0F
    private var startTouchOffset = 0F
    private var movableDistance = if (corcedPointer == CoercePointer.NOT_COERCED)
        mMarkerManager.markerWidth
    else
        mMarkerManager.markerWidth - dpToPixel(mPointerManager.pointer.width, mScreenScale)

    init {
        mMarkerManager.createMarkers(10)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.BLUE
        canvas.drawPaint(paint)
        mMarkerManager.drawMarkers(mCurrentOffset, canvas, paint)
        mPointerManager.drawPointer(mViewCenter, canvas, paint)
        paint.color = Color.BLACK
        canvas.drawRectWithOffset(dpToPixel(1, mScreenScale), dpToPixel(40, mScreenScale), 0F, mViewCenter - dpToPixel(1, mScreenScale) / 2, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidthInPixels = mMarkerManager.markerWidth.toInt()
        val desiredHeightInPixels = maxOf(mMarkerManager.markerTotalHeight, mPointerManager.pointerTotalHeight).toInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidthInPixels.coerceAtMost(widthSize)
            else -> desiredWidthInPixels
        }

        mViewCenter = (width / 2).toFloat()

        mStartOffset = when (pointerDirection) {
            PointerSelection.LEFT -> mViewCenter - dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
            PointerSelection.CENTER -> mViewCenter
            PointerSelection.RIGHT -> mViewCenter + dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
        }

        mCurrentOffset = mStartOffset

        val height: Int = when (heightMode) {
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
                startTouchOffset = mCurrentOffset
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                var newOffset = startTouchOffset + event.x - initialTouchX
                mCurrentOffset = newOffset.coerceIn(mViewCenter - movableDistance, mViewCenter) //TODO bo aqui

//                selectedIndex = findIndexTroughOffset(currentOffset)

//                if (movement == Movement.DISCRETE)
//                    currentOffset = viewCenter - offsets[selectedIndex]

                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }
}