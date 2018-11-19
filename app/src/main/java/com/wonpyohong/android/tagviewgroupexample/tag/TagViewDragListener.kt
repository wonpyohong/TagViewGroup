package com.wonpyohong.android.tagviewgroupexample.tag

import android.graphics.Rect
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import java.util.*

class TagViewDragListener(val tagViewGroup: TagViewGroup): View.OnDragListener {
    private val overlapCalculator = OverlapCalculator()

    private var prevX = 0f

    override fun onDrag(draggingView: View?, event: DragEvent): Boolean {
        val draggingTextView = event.localState as TextView
        when (event.action) {
            DragEvent.ACTION_DRAG_LOCATION -> {
                if (tagViewGroup.isChangingLayout()) {
                    return true
                }

                shouldSwap(draggingTextView, event)?.let {(draggingViewIndex, index) ->
                    Collections.swap(tagViewGroup.tagList, draggingViewIndex, index)

                    tagViewGroup.requestLayout()
                }

                prevX = event.x
            }
            DragEvent.ACTION_DROP -> {
                draggingTextView.visibility = View.VISIBLE
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggingTextView.visibility = View.VISIBLE
            }
        }

        return true
    }

    private fun shouldSwap(draggingTextView: TextView, event: DragEvent): Pair<Int, Int>? {
        val isRight = prevX - event.x < 0
        val isLeft = prevX - event.x > 0

        val draggingRect = Rect(
            (event.x - draggingTextView.width / 2).toInt(),
            (event.y - draggingTextView.height / 2).toInt(),
            (event.x + draggingTextView.width / 2).toInt(),
            (event.y + draggingTextView.height / 2).toInt()
        )

        val viewList = tagViewGroup.tagList.map { it.view }
        for ((index, view) in viewList.withIndex()) {
            val childViewRect = getRect(view)
            if (draggingTextView.text != view.text.toString() && overlapCalculator.isOverlap(draggingRect, childViewRect)) {
                val draggingViewIndex = viewList.indexOfFirst { it.text.toString() == draggingTextView.text.toString() }

                if ((isRight && draggingViewIndex < index && draggingRect.right > childViewRect.right)
                    || (isLeft && draggingViewIndex > index && draggingRect.left < childViewRect.left)) {
                    return Pair(draggingViewIndex, index)
                }
            }
        }

        return null
    }

    private fun getRect(view: View) = Rect(view.left, view.top, view.right, view.bottom)
}