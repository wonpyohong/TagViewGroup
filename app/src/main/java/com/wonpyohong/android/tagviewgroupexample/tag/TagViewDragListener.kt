package com.wonpyohong.android.tagviewgroupexample.tag

import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.DragEvent
import android.view.View

class TagViewDragListener(val tagViewGroup: TagViewGroup): View.OnDragListener {
    private var lastSwapInfo: SwapInfo? = null

    override fun onDrag(destinationView: View, event: DragEvent): Boolean {
        val draggingTag = event.localState as Tag
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                tagViewGroup.isDragging = true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                if (tagViewGroup.isChangingLayout()) {
                    return true
                }

                shouldSwap(draggingTag, event)?.let { (dragginViewIndex, targetIndex) ->
                    var indexToAdd = targetIndex

                    val isDownDragging = draggingTag.rowIndex < tagViewGroup.tagList[targetIndex].rowIndex
                    val isUpDragging = draggingTag.rowIndex > tagViewGroup.tagList[targetIndex].rowIndex

                    if (isDownDragging) {
                        indexToAdd = targetIndex - 1
                        draggingTag.rowIndex++
                    } else if (isUpDragging) {
                        draggingTag.rowIndex--
                    }

                    tagViewGroup.tagList.removeAt(dragginViewIndex)
                    tagViewGroup.tagList.add(indexToAdd, draggingTag)

                    tagViewGroup.requestLayout()
                }
            }

            DragEvent.ACTION_DROP -> {
                tagViewGroup.isDragging = false
                draggingTag.view.alpha = 1f

                tagViewGroup.tagList
                    .filter { it.isDummyTag() }
                    .forEach {
                        tagViewGroup.removeView(it.view)
                        tagViewGroup.tagList.remove(it)
                    }

                tagViewGroup.requestLayout()
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                draggingTag.view.alpha = 1f
            }
        }

        return true
    }

    private fun shouldSwap(draggingTag: Tag, event: DragEvent): Pair<Int, Int>? {
        val draggingCenter = Point(event.x.toInt(), event.y.toInt())

        tagViewGroup.tagList.forEachIndexed { targetIndex, tag ->
            if (draggingCenter in tag.view.getRect()) {
                val draggingViewIndex = tagViewGroup.tagList.indexOfFirst { it == draggingTag }

                val swapPair = Pair(draggingViewIndex, targetIndex)
                if (lastSwapInfo == null || lastSwapInfo != SwapInfo(swapPair)) {
                    lastSwapInfo = SwapInfo(swapPair)
                    return swapPair
                }
            }
        }
        return null
    }

    fun View.getRect() = Rect(left, top, right, bottom)

    operator fun Rect.contains(point: Point) = contains(point.x, point.y)

    data class SwapInfo(val swapPair: Pair<Int, Int>) {
        override fun equals(other: Any?): Boolean {
            if (other !is SwapInfo) {
                return false
            }
            return (swapPair.first == other.swapPair.first && swapPair.second == other.swapPair.second)
                    || (swapPair.first == other.swapPair.second && swapPair.second == other.swapPair.first)
        }
    }
}