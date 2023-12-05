package com.rafambn.frameprogressbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Dimension
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

    @Dimension(unit = Dimension.PX)
    private var mCurrentOffset = 0F

    @Dimension(unit = Dimension.PX)
    private var mStartOffset = 0F

    @Dimension(unit = Dimension.PX)
    private var mCoercedtOffset = 0

    @Dimension(unit = Dimension.PX)
    private var mViewCenter = 0F
    private var mSelectedIndex = 0

    private var movement: Movement = Movement.CONTINUOUS
    private var pointerDirection: PointerSelection = PointerSelection.RIGHT
    private var corcedPointer: CoercePointer = CoercePointer.COERCED

    private var initialTouchX = 0F
    private var startTouchOffset = 0F
    private var movableDistance = 0F

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
        canvas.drawRectWithOffset(dpToPixel(1, mScreenScale).toFloat(), dpToPixel(40, mScreenScale).toFloat(), 0F, mViewCenter, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidthInPixels = mMarkerManager.markerWidth.toFloat()
        val desiredHeightInPixels = maxOf(mMarkerManager.markerTotalHeight.toFloat(), mPointerManager.pointerTotalHeight.toFloat())

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidthInPixels.coerceAtMost(widthSize.toFloat()).toInt()
            else -> desiredWidthInPixels.toInt()
        }

        mViewCenter = (width / 2).toFloat()

        mStartOffset = when (pointerDirection) {
            PointerSelection.LEFT -> mViewCenter - dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
            PointerSelection.CENTER -> mViewCenter
            PointerSelection.RIGHT -> mViewCenter + dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
        }

        mCurrentOffset = if (movement == Movement.DISCRETE)
            mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)
        else if (corcedPointer == CoercePointer.COERCED)
            mStartOffset - when (pointerDirection) {
                PointerSelection.LEFT -> 0
                PointerSelection.CENTER -> dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
                PointerSelection.RIGHT -> dpToPixel(mPointerManager.pointer.width, mScreenScale)
            }
        else
            mStartOffset


        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeightInPixels.coerceAtMost(widthSize.toFloat()).toInt()
            else -> desiredHeightInPixels.toInt()
        }

        setMeasuredDimension(width, height)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.x
                startTouchOffset = mCurrentOffset
                mCoercedtOffset = if (corcedPointer == CoercePointer.COERCED)
                    when (pointerDirection) {
                        PointerSelection.LEFT -> 0
                        PointerSelection.CENTER -> dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
                        PointerSelection.RIGHT -> dpToPixel(mPointerManager.pointer.width, mScreenScale)
                    }
                else
                    0
                movableDistance = if (corcedPointer == CoercePointer.NOT_COERCED)
                    mMarkerManager.markerWidth.toFloat()
                else
                    mMarkerManager.markerWidth - dpToPixel(mPointerManager.pointer.width, mScreenScale).toFloat()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                var distanceMoved = startTouchOffset + event.x - initialTouchX
                distanceMoved = distanceMoved.coerceIn(mStartOffset - movableDistance - mCoercedtOffset, mStartOffset - mCoercedtOffset)

                mSelectedIndex = mMarkerManager.findIndexTroughOffset(
                    mViewCenter - distanceMoved - when (pointerDirection) {
                        PointerSelection.LEFT -> dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
                        PointerSelection.CENTER -> 0
                        PointerSelection.RIGHT -> +dpToPixel(mPointerManager.pointer.width, mScreenScale) / 2
                    }
                )

                mCurrentOffset = if (movement == Movement.DISCRETE)
                    mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)
                else
                    distanceMoved

                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }
}