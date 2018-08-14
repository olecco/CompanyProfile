package com.olecco.android.companyprofile.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PieChartView : View {

    var adapter: PieChartAdapter? = null
        set(value) {
            field = value
            valueSum = if (value == null) 0.0 else calculateValueSum()
            invalidate()
        }
    private var valueSum: Double = 0.0
    private val linePaint: Paint = Paint()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(linePaint) {
            color = Color.RED
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        val itemsAdapter = adapter
        if (itemsAdapter != null) {

            val radiusX = (measuredWidth - paddingStart - paddingEnd) / 2
            val radiusY = (measuredHeight - paddingTop - paddingBottom) / 2

            val centerX: Float = (paddingStart + radiusX).toFloat()
            val centerY: Float = (paddingTop + radiusY).toFloat()

            val radius = Math.min(radiusX, radiusY).toFloat()

            val itemCount: Int = itemsAdapter.getItemCount()
            canvas.save()
            for (i in 0..itemCount) {
                val itemValuePart = calculateValuePart(i)
                canvas.drawLine(centerX, centerY,
                        centerX, (centerY - radius), linePaint)
                canvas.rotate(360 * itemValuePart, centerX, centerY)
                canvas.drawCircle(centerX, centerY, radius, linePaint)
            }
            canvas.restore()
        }
    }

    private fun calculateValuePart(index: Int) : Float {
        val itemAdapter = adapter
        if (itemAdapter != null) {
            val itemValue = itemAdapter.getItemValue(index)
            return (itemValue / valueSum).toFloat()
        }
        return 0.0f
    }

    private fun calculateValueSum(): Double {
        val itemsAdapter = adapter
        if (itemsAdapter != null) {
            val itemCount: Int = itemsAdapter.getItemCount()
            var sum = 0.0
            for (i in 0..itemCount) {
                sum += itemsAdapter.getItemValue(i)
            }
            return sum
        }
        return 0.0
    }

    interface PieChartAdapter {

        fun getItemCount(): Int

        fun getItemValue(index: Int): Double

    }

}