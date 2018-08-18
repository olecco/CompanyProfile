package com.olecco.android.companyprofile.ui

import android.content.Context
import android.graphics.*
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.lang.Math.PI

private const val TRANS_LINE_WIDTH_DP = 4
private const val SEGMENTS_GAP_DP = 10
private const val OVERLAY_RADIUS_PART = 0.55f
private const val INNER_RADIUS_PART = 0.5f
private const val NAME_TEXT_SIZE = 24

interface PieChartClickListener {
    fun onSegmentClick(segmentIndex: Int)
}

class PieChartView : View {

    var adapter: PieChartAdapter? = null
        set(value) {
            field = value
            valueSum = if (value == null) 0.0 else calculateValueSum()
            invalidate()
        }
    var pieChartClickListener: PieChartClickListener? = null

    private var valueSum: Double = 0.0

    private val segmentPaint: Paint = Paint()
    private val overlayPaint: Paint = Paint()
    private val transparentLinePaint: Paint = Paint()
    private val transparentFillPaint: Paint
    private val nameTextPaint: Paint = Paint()

    private val segmentPath: Path = Path()
    private val segmentOverlayPath: Path = Path()
    private val segmentRect: RectF = RectF()
    private val segmentOverlayRect: RectF = RectF()

    private val segmentRegions: MutableList<Region> = mutableListOf()
    private val pathRotationMatrix: Matrix = Matrix()
    private val pathRotationRegion: Region = Region()

    private val gestureDetector: GestureDetectorCompat

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(segmentPaint) {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        with(overlayPaint) {
            color = Color.parseColor("#40000000")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        with(transparentLinePaint) {
            color = Color.TRANSPARENT
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = TRANS_LINE_WIDTH_DP.toPx(resources)
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        transparentFillPaint = Paint(transparentLinePaint)
        transparentFillPaint.style = Paint.Style.FILL

        with(nameTextPaint) {
            color = Color.parseColor("#E0FFFFFF") // todo set in attrs
            textSize = NAME_TEXT_SIZE.toPx(resources)
            isAntiAlias = true
        }

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        gestureDetector = GestureDetectorCompat(getContext(), object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val segmentIndex = getClickedRegionIndex(e.x.toInt(), e.y.toInt())
                if (segmentIndex != -1) {
                    notifySegmentClick(segmentIndex)
                    return true
                }
                return false
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        val itemsAdapter = adapter
        if (itemsAdapter != null) {

            val radiusX = (measuredWidth - paddingStart - paddingEnd) / 2
            val radiusY = (measuredHeight - paddingTop - paddingBottom) / 2

            val centerX: Float = (paddingStart + radiusX).toFloat()
            val centerY: Float = (paddingTop + radiusY).toFloat()

            val gap = SEGMENTS_GAP_DP.toPx(resources)
            val radius = Math.min(radiusX, radiusY).toFloat()
            val overlayRadius = radius * OVERLAY_RADIUS_PART

            segmentRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            segmentOverlayRect.set(centerX - overlayRadius, centerY - overlayRadius,
                    centerX + overlayRadius, centerY + overlayRadius)

            val itemCount: Int = itemsAdapter.getSegmentCount()


            var regionRotationAngle = 0.0f
            canvas.save()
            for (i in 0 until itemCount) {
                val itemValuePart = calculateValuePart(i)


                val halfAngle: Float = 360 * itemValuePart / 2

                val dx: Float = (gap * Math.sin(Math.toRadians(halfAngle.toDouble()) + PI / 2)).toFloat()
                val dy: Float = (gap * Math.cos(Math.toRadians(halfAngle.toDouble()) + PI / 2)).toFloat()

                segmentRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

                segmentPaint.color = itemsAdapter.getSegmentColor(i)

                segmentPath.reset()
                segmentPath.moveTo(centerX, centerY)
                segmentPath.lineTo(centerX, (centerY - radius))
                segmentPath.arcTo(segmentRect, 270.0f, 360 * itemValuePart)
                segmentPath.close()

                segmentOverlayPath.reset()
                segmentOverlayPath.moveTo(centerX, centerY)
                segmentOverlayPath.lineTo(centerX, (centerY - overlayRadius))
                segmentOverlayPath.arcTo(segmentOverlayRect, 270.0f, 360 * itemValuePart)
                segmentOverlayPath.close()


                canvas.drawPath(segmentPath, segmentPaint)

                canvas.drawPath(segmentOverlayPath, overlayPaint)

                canvas.drawPath(segmentPath, transparentLinePaint)


                pathRotationMatrix.reset()
                pathRotationMatrix.postRotate(regionRotationAngle, centerX, centerY)
                segmentPath.transform(pathRotationMatrix)

                pathRotationRegion.set(segmentRect.left.toInt(),
                        segmentRect.top.toInt(), segmentRect.right.toInt(), segmentRect.bottom.toInt())
                segmentRegions[i].setPath(segmentPath, pathRotationRegion)
                regionRotationAngle += 360 * itemValuePart


                canvas.rotate(360 * itemValuePart, centerX, centerY)
            }
            canvas.restore()

            canvas.drawCircle(centerX, centerY, INNER_RADIUS_PART * radius, transparentFillPaint)


            canvas.drawTextCentered(itemsAdapter.getChartName(), centerX, centerY, nameTextPaint)

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
       if (gestureDetector.onTouchEvent(event)) {
           return true
       }
        return super.onTouchEvent(event)
    }

    private fun getClickedRegionIndex(x: Int, y: Int): Int {
        for (i in 0 until segmentRegions.size) {
            if (segmentRegions[i].contains(x, y)) {
                return i
            }
        }
        return -1
    }

    private fun calculateValuePart(index: Int) : Float {
        val itemAdapter = adapter
        if (itemAdapter != null) {
            val itemValue = itemAdapter.getSegmentValue(index)
            return (itemValue / valueSum).toFloat()
        }
        return 0.0f
    }

    private fun calculateValueSum(): Double {
        segmentRegions.clear()
        val itemsAdapter = adapter
        if (itemsAdapter != null) {
            val itemCount: Int = itemsAdapter.getSegmentCount()
            var sum = 0.0
            for (i in 0 until itemCount) {
                sum += itemsAdapter.getSegmentValue(i)
                segmentRegions.add(Region())
            }
            return sum
        }
        return 0.0
    }

    fun notifySegmentClick(segmentIndex: Int) {
        pieChartClickListener?.onSegmentClick(segmentIndex)
    }

    interface PieChartAdapter {

        fun getChartName(): String

        fun getSegmentCount(): Int

        fun getSegmentValue(index: Int): Double

        fun getSegmentColor(index: Int): Int

    }

}