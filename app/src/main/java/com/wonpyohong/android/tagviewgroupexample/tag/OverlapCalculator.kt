package com.wonpyohong.android.tagviewgroupexample.tag

import android.graphics.Rect
import android.util.Log
import kotlin.math.max
import kotlin.math.min

class OverlapCalculator {
    fun isOverlap(rect: Rect, other: Rect): Boolean {
        if (rect.right < other.left || other.right < rect.left) {
            return false
        }

        if (rect.bottom < other.top || other.bottom < rect.top) {
            return false
        }

        return true
    }

    fun getOverlapPercent(rect: Rect, other: Rect): Double {
        Log.d("HWP", "rect: ${rect.left}, other: ${other.left}")
        val overlappedArea = max(0, min(rect.right, other.right) - max(rect.left, other.left)) *
                max(0, min(rect.bottom, other.bottom) - max(rect.top, other.top))
        return overlappedArea.toDouble() / min(getArea(rect), getArea(other))
    }

    private fun getArea(view: Rect) = with (view) { (right - left) * (bottom - top) }
}