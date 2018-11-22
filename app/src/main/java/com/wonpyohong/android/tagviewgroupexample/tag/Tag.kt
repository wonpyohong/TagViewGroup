package com.wonpyohong.android.tagviewgroupexample.tag

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup

data class Tag(val view: View, var rowIndex: Int = 0) {
    companion object {
        fun createDummyTag(context: Context, draggingView: View, lastViewInRow: View, rowIndex: Int): Tag {
            val dummyView = View(context).apply {
                val dummyViewWidth = draggingView.width + (lastViewInRow.parent as View).right - lastViewInRow.right
                this.layoutParams = ViewGroup.LayoutParams(dummyViewWidth, lastViewInRow.height)
                this.tag = "dummy"
                this.background = ColorDrawable(Color.LTGRAY)
            }

            return Tag(dummyView, rowIndex)
        }
    }

    fun isDummyTag() = view.tag == "dummy"
}