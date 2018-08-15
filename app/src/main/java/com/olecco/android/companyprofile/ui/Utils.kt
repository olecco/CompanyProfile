package com.olecco.android.companyprofile.ui

import android.content.res.Resources
import android.util.TypedValue

fun Int.toPx(resources: Resources): Float {
    val displaymetrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displaymetrics)
}