package com.wonpyohong.android.tagviewgroupexample.tag

import android.graphics.*
import android.view.View


class TagDragShadowBuilder(draggingView: View) : View.DragShadowBuilder(draggingView) {
    private val scalingFactor = 1.2
    private val paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        this.color = Color.argb(100, 0xff, 0xff, 0xff)
    }

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        outShadowSize?.set((view.width * scalingFactor).toInt(), (view.height * scalingFactor).toInt())
        outShadowTouchPoint?.set((view.width * scalingFactor * 0.8).toInt(), (view.height * scalingFactor * 0.8).toInt())
    }

    override fun onDrawShadow(canvas: Canvas?) {
        canvas?.scale(scalingFactor.toFloat(), scalingFactor.toFloat())
        view.draw(canvas)
        canvas?.drawRect(0f, 0f, (view.width * scalingFactor).toFloat(), (view.height * scalingFactor).toFloat(), paint)
    }
}