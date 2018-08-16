package com.olecco.android.companyprofile.ui

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue

fun Int.toPx(resources: Resources): Float {
    val displaymetrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displaymetrics)
}

fun Canvas.drawTextCentered(text: String, centerX: Float, centerY: Float, textPaint: Paint) {
    val textRect: Rect = Rect()
    textPaint.getTextBounds(text, 0, text.length, textRect)
    this.drawText(text, centerX - textRect.width() / 2,centerY + textRect.height() / 2, textPaint)
}