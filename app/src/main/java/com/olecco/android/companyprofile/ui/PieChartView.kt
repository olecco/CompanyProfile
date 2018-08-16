package com.olecco.android.companyprofile.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

private const val TRANS_LINE_WIDTH_DP = 4
private const val SEGMENTS_GAP_DP = 10

class PieChartView : View {

    var adapter: PieChartAdapter? = null
        set(value) {
            field = value
            valueSum = if (value == null) 0.0 else calculateValueSum()
            invalidate()
        }
    private var valueSum: Double = 0.0

    private val linePaint: Paint = Paint()
    private val segmentPaint: Paint = Paint()
    private val transparentPaint: Paint = Paint()

    private val segmentPath: Path = Path()
    private val rect: RectF = RectF()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(linePaint) {
            color = Color.YELLOW
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        with(segmentPaint) {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        with(transparentPaint) {
            color = Color.TRANSPARENT
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = TRANS_LINE_WIDTH_DP.toPx(resources)
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        val itemsAdapter = adapter
        if (itemsAdapter != null) {

            val radiusX = (measuredWidth - paddingStart - paddingEnd) / 2
            val radiusY = (measuredHeight - paddingTop - paddingBottom) / 2

            val centerX: Float = (paddingStart + radiusX).toFloat()
            val centerY: Float = (paddingTop + radiusY).toFloat()

            val gap = SEGMENTS_GAP_DP.toPx(resources)
            val radius = Math.min(radiusX, radiusY).toFloat()// + gap

            rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

            val itemCount: Int = itemsAdapter.getSegmentCount()



            canvas.save()
            for (i in 0 until itemCount) {
                val itemValuePart = calculateValuePart(i)


                val halfAngle: Float = 360 * itemValuePart / 2

                val dx: Float = (gap * Math.sin(Math.toRadians(halfAngle.toDouble()))).toFloat()
                val dy: Float = (gap * Math.cos(Math.toRadians(halfAngle.toDouble()))).toFloat()

                rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)


                val count: Int = canvas.save()
                //canvas.translate(dx, -dy)

                segmentPaint.color = itemsAdapter.getSegmentColor(i)

                segmentPath.reset()
                segmentPath.moveTo(centerX, centerY)
                segmentPath.lineTo(centerX, (centerY - radius))
                segmentPath.arcTo(rect, 270.0f, 360 * itemValuePart)
                segmentPath.close()





                canvas.drawPath(segmentPath, segmentPaint)
                canvas.drawPath(segmentPath, transparentPaint)


                //canvas.restoreToCount(count)

//                canvas.drawLine(centerX, centerY,
//                        centerX, (centerY - radius), transparentPaint)

                //canvas.drawCircle(centerX, centerY, 4*TRANS_LINE_WIDTH_DP.toPx(resources), transparentPaint)

                canvas.rotate(360 * itemValuePart, centerX, centerY)
            }
            canvas.restore()

            canvas.drawCircle(centerX, centerY, gap, linePaint)

        }
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
        val itemsAdapter = adapter
        if (itemsAdapter != null) {
            val itemCount: Int = itemsAdapter.getSegmentCount()
            var sum = 0.0
            for (i in 0 until itemCount) {
                sum += itemsAdapter.getSegmentValue(i)
            }
            return sum
        }
        return 0.0
    }

    interface PieChartAdapter {

        fun getSegmentCount(): Int

        fun getSegmentValue(index: Int): Double

        fun getSegmentColor(index: Int): Int

    }

}