package com.rafambn.frameprogressbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Dimension
import com.rafambn.frameprogressbar.api.FrameProgressBarAPI
import com.rafambn.frameprogressbar.api.MarkersAPI
import com.rafambn.frameprogressbar.api.PointerAPI
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.Movement
import com.rafambn.frameprogressbar.enums.PointerSelection
import com.rafambn.frameprogressbar.managers.MarkerManager
import com.rafambn.frameprogressbar.managers.PointerManager

/**
 *
 */
class FrameProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs), FrameProgressBarAPI, MarkersAPI, PointerAPI {
    private val mScreenScale = context.resources.displayMetrics.density
    private val mMarkerManager = MarkerManager(mScreenScale)
    private val mPointerManager = PointerManager(mScreenScale)
    private val mPaint = Paint()

    @Dimension(unit = Dimension.PX)
    private var mCurrentOffset = 0F

    @Dimension(unit = Dimension.PX)
    private var mStartOffset = 0F

    @Dimension(unit = Dimension.PX)
    private var mCoercedtOffset = 0

    @Dimension(unit = Dimension.PX)
    private var mViewCenter = 0F
    private var mSelectedIndex = 0

    private var mMovement: Movement = Movement.CONTINUOUS
    private var mPointerDirection: PointerSelection = PointerSelection.CENTER
    private var mCorcedPointer: CoercePointer = CoercePointer.NOT_COERCED

    private var mInitialTouchX = 0F
    private var mStartTouchOffset = 0F
    private var mMovableDistance = 0F

    init {
        mMarkerManager.createMarkers(10)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mMarkerManager.drawMarkers(mCurrentOffset, canvas, mPaint)
        mPointerManager.drawPointer(mViewCenter, canvas, mPaint)
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

        mStartOffset = when (mPointerDirection) {
            PointerSelection.LEFT -> mViewCenter - mPointerManager.pointerWidth / 2
            PointerSelection.CENTER -> mViewCenter
            PointerSelection.RIGHT -> mViewCenter + mPointerManager.pointerWidth / 2
        }

        mCurrentOffset = mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)

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
                mInitialTouchX = event.x
                mStartTouchOffset = mCurrentOffset
                mCoercedtOffset = if (mCorcedPointer == CoercePointer.COERCED) //TODO improve coerce
                    when (mPointerDirection) {
                        PointerSelection.LEFT -> 0
                        PointerSelection.CENTER -> mPointerManager.pointerWidth / 2
                        PointerSelection.RIGHT -> mPointerManager.pointerWidth
                    }
                else
                    0
                mMovableDistance = if (mCorcedPointer == CoercePointer.NOT_COERCED)
                    mMarkerManager.markerWidth.toFloat()
                else
                    mMarkerManager.markerWidth - mPointerManager.pointerWidth.toFloat()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                var distanceMoved = mStartTouchOffset + event.x - mInitialTouchX
                distanceMoved = distanceMoved.coerceIn(mStartOffset - mMovableDistance - mCoercedtOffset, mStartOffset - mCoercedtOffset)

                mSelectedIndex = mMarkerManager.findIndexTroughOffset(
                    mViewCenter - distanceMoved - when (mPointerDirection) {
                        PointerSelection.LEFT -> mPointerManager.pointerWidth / 2
                        PointerSelection.CENTER -> 0
                        PointerSelection.RIGHT -> -mPointerManager.pointerWidth / 2
                    }
                )
                mCurrentOffset = if (mMovement == Movement.DISCRETE)
                    mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)
                else
                    distanceMoved

                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun setNumberFrames(numberFrames: Int) {
        mMarkerManager.createMarkers(numberFrames)
        invalidate()
        requestLayout()
    }

    override fun getNumberFrames(): Int {
        return mMarkerManager.numberOfMarkers
    }

    override fun setIndex(index: Int) {
        mSelectedIndex = index
        mCurrentOffset = mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)
        invalidate()
    }

    override fun getIndex(): Int {
        return mSelectedIndex
    }

    override fun setMovement(movement: Movement) {
        mMovement = movement
        invalidate()
    }

    override fun getMovement(): Movement {
        return mMovement
    }

    override fun setPointerSelection(pointerSelection: PointerSelection) {
        mPointerDirection = pointerSelection
        invalidate()
        requestLayout()
    }

    override fun getPointerSelection(): PointerSelection {
        return mPointerDirection
    }

    override fun setCoercePointer(coercePointer: CoercePointer) {
        mCorcedPointer = coercePointer
        invalidate()
    }

    override fun getCoercePointer(): CoercePointer {
        return mCorcedPointer
    }

    override fun setOffset(offset: Float) {
        mCurrentOffset = -(offset - mStartOffset)
        mSelectedIndex = mMarkerManager.findIndexTroughOffset(offset)
        invalidate()
    }

    override fun getOffset(): Float {
        return -(mCurrentOffset - mStartOffset)
    }

    override fun setMarkersWidth(width: Int) {
        mMarkerManager.setMarkersWidth(width)
        invalidate()
        requestLayout()
    }

    override fun setMarkersWidth(listWidth: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersWidth(listWidth)
        invalidate()
        requestLayout()
    }

    override fun setMarkersHeight(height: Int) {
        mMarkerManager.setMarkersHeight(height)
        invalidate()
        requestLayout()
    }

    override fun setMarkersHeight(listHeight: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersHeight(listHeight)
        invalidate()
        requestLayout()
    }

    override fun setMarkersTopOffset(topOffset: Int) {
        mMarkerManager.setMarkersTopOffset(topOffset)
        invalidate()
        requestLayout()
    }

    override fun setMarkersTopOffset(listTopOffset: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersTopOffset(listTopOffset)
        invalidate()
        requestLayout()
    }

    override fun setMarkersColor(color: Int) {
        mMarkerManager.setMarkersColor(color)
        invalidate()
    }

    override fun setMarkersColor(listColor: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersColor(listColor)
        invalidate()
    }

    override fun setMarkersDrawable(drawable: Drawable?) {
        mMarkerManager.setMarkersDrawable(drawable)
        invalidate()
    }

    override fun setMarkersDrawable(listDrawable: List<Pair<Int, Drawable?>>) {
        mMarkerManager.setMarkersDrawable(listDrawable)
        invalidate()
    }

    override fun setMarkersBitmap(bitmap: Bitmap?) {
        mMarkerManager.setMarkersBitmap(bitmap)
        invalidate()
    }

    override fun setMarkersBitmap(listBitmap: List<Pair<Int, Bitmap?>>) {
        mMarkerManager.setMarkersBitmap(listBitmap)
        invalidate()
    }

    override fun setPointerWidth(width: Int) {
        mPointerManager.setPointerWidth(width)
        invalidate()
        requestLayout()
    }

    override fun setPointerHeight(height: Int) {
        mPointerManager.setPointerHeight(height)
        invalidate()
        requestLayout()
    }

    override fun setPointerTopOffset(topOffset: Int) {
        mPointerManager.setPointerTopOffset(topOffset)
        invalidate()
        requestLayout()
    }

    override fun setPointerColor(color: Int) {
        mPointerManager.setPointerColor(color)
        invalidate()
    }

    override fun setPointerDrawable(drawable: Drawable?) {
        mPointerManager.setPointerDrawable(drawable)
        invalidate()
    }

    override fun setPointerBitmap(bitmap: Bitmap?) {
        mPointerManager.setPointerBitmap(bitmap)
        invalidate()
    }
}