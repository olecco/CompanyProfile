package com.olecco.android.companyprofile.ui.piechart

import android.content.Context
import android.graphics.*
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.olecco.android.companyprofile.ui.drawTextCentered
import com.olecco.android.companyprofile.ui.getTextHeight
import com.olecco.android.companyprofile.ui.toPx
import java.lang.Math.PI

private const val TRANS_LINE_WIDTH_DP = 4
private const val OVERLAY_RADIUS_PART = 0.55f
private const val INNER_RADIUS_PART = 0.5f
private const val TEXT_RADIUS_PART = 0.75f
private const val NAME_TEXT_SIZE = 24
private const val SEGMENT_TEXT_SIZE = 16

private const val LEGEND_LINE_GAP_DP = 10
private const val LEGEND_MARKER_SIZE_DP = 24

interface PieChartAdapter {

    fun getChartName(): String

    fun getSegmentCount(): Int

    fun getSegmentValue(index: Int): Double

    fun getSegmentColor(index: Int): Int

    fun getSegmentName(index: Int): String

}

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
    private val segmentTextPaint: Paint

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

        segmentTextPaint = Paint(nameTextPaint)
        segmentTextPaint.textSize = SEGMENT_TEXT_SIZE.toPx(resources)

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
            val radiusY = (measuredHeight - paddingTop - paddingBottom - getLegendHeight()) / 2

            val radius = Math.min(radiusX, radiusY).toFloat()
            val overlayRadius = radius * OVERLAY_RADIUS_PART

            val centerX: Float = (paddingStart + (measuredWidth - paddingStart - paddingEnd) / 2.0f)
            val centerY: Float = (paddingTop + radius)

            segmentRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            segmentOverlayRect.set(centerX - overlayRadius, centerY - overlayRadius,
                    centerX + overlayRadius, centerY + overlayRadius)

            val itemCount: Int = itemsAdapter.getSegmentCount()


            var regionRotationAngle = 0.0f
            for (i in 0 until itemCount) {
                val itemValuePart = calculateValuePart(i)

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

                pathRotationMatrix.reset()
                pathRotationMatrix.postRotate(regionRotationAngle, centerX, centerY)
                segmentPath.transform(pathRotationMatrix)
                segmentOverlayPath.transform(pathRotationMatrix)

                regionRotationAngle += 360 * itemValuePart

                pathRotationRegion.set(segmentRect.left.toInt(),
                        segmentRect.top.toInt(), segmentRect.right.toInt(), segmentRect.bottom.toInt())
                segmentRegions[i].setPath(segmentPath, pathRotationRegion)

                canvas.drawPath(segmentPath, segmentPaint)
                canvas.drawPath(segmentOverlayPath, overlayPaint)
                canvas.drawPath(segmentPath, transparentLinePaint)
            }

            canvas.drawCircle(centerX, centerY, INNER_RADIUS_PART * radius, transparentFillPaint)

            canvas.drawTextCentered(itemsAdapter.getChartName(), centerX, centerY, nameTextPaint)

            drawSegmentLabels(canvas, radius, centerX, centerY)

            drawLegend(canvas)
        }
    }

    private fun drawSegmentLabels(canvas: Canvas, radius: Float, centerX: Float, centerY: Float) {
        val itemsAdapter = adapter
        if (itemsAdapter != null) {

            val itemCount: Int = itemsAdapter.getSegmentCount()

            var regionRotationAngle = 0.0f
            var textAngle: Float
            for (i in 0 until itemCount) {
                val itemValuePart = calculateValuePart(i)

                textAngle = regionRotationAngle + 360 * itemValuePart / 2
                regionRotationAngle += 360 * itemValuePart

                val gap = TEXT_RADIUS_PART * radius
                val textAngleRad = Math.toRadians(textAngle.toDouble())

                val dx: Float = (gap * Math.sin(textAngleRad)).toFloat()
                val dy: Float = (gap * Math.cos(textAngleRad + PI)).toFloat()

                canvas.drawTextCentered("${Math.round(itemValuePart * 1000.0f) / 10.0f}%",
                        centerX + dx, centerY + dy, segmentTextPaint)

            }
        }
    }

    private fun drawLegend(canvas: Canvas) {
        val itemsAdapter = adapter
        if (itemsAdapter != null) {
            val markerSize = LEGEND_MARKER_SIZE_DP.toPx(resources)
            val textGap = LEGEND_LINE_GAP_DP.toPx(resources).toInt()
            val x = paddingLeft
            var y = height - paddingBottom
            for (i in itemsAdapter.getSegmentCount() - 1 downTo  0) {

                segmentPaint.color = itemsAdapter.getSegmentColor(i)

                canvas.drawRect(x.toFloat(), y - markerSize, x + markerSize, y.toFloat(), segmentPaint)

                val segmentName = itemsAdapter.getSegmentName(i)
                val textHeight = segmentTextPaint.getTextHeight(segmentName)
                canvas.drawText(segmentName, x + markerSize + textGap, y - markerSize / 2 + textHeight / 2, segmentTextPaint)
                y -= Math.max(markerSize.toInt(), textHeight) + textGap
            }
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

    private fun getLegendHeight(): Int {
        val itemsAdapter = adapter
        if (itemsAdapter != null) {
            val lineHeight = Math.max(segmentTextPaint.getTextHeight("#"), LEGEND_MARKER_SIZE_DP.toPx(resources).toInt())
            val count = itemsAdapter.getSegmentCount()
            return count * lineHeight + (count - 1) * LEGEND_LINE_GAP_DP.toPx(resources).toInt()
        }
        return 0
    }

}