package com.wonpyohong.android.tagviewgroupexample.tag

import android.view.DragEvent
import android.view.View
import com.wonpyohong.android.tagviewgroupexample.tag.SensitivityAdjuster.*

class TagViewDragListener(val tagViewGroup: TagViewGroup): View.OnDragListener {
    private val sensitivityAdjuster = SensitivityAdjuster()

    override fun onDrag(destinationView: View, event: DragEvent): Boolean {
        val draggingTag = event.localState as Tag
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                tagViewGroup.isDragging = true

                sensitivityAdjuster.init()
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                if (tagViewGroup.isChangingLayout()) {
                    return true
                }

                getRearrangePair(draggingTag, event)?.let { (dragginViewIndex, targetIndex) ->
                    var indexToAdd = targetIndex

                    val isDownDragging = draggingTag.rowIndex < tagViewGroup.tagList[targetIndex].rowIndex
                    val isUpDragging = draggingTag.rowIndex > tagViewGroup.tagList[targetIndex].rowIndex

                    if (isDownDragging) {
                        indexToAdd = getIndexToAdd(draggingTag, targetIndex)
                        draggingTag.rowIndex = tagViewGroup.tagList[targetIndex].rowIndex
                    } else if (isUpDragging) {
                        draggingTag.rowIndex = tagViewGroup.tagList[targetIndex].rowIndex
                    }

                    tagViewGroup.tagList.removeAt(dragginViewIndex)
                    tagViewGroup.tagList.add(indexToAdd, draggingTag)

                    tagViewGroup.requestLayout()
                }

                sensitivityAdjuster.setPrevPoint(event.x, event.y)
            }

            DragEvent.ACTION_DROP, DragEvent.ACTION_DRAG_ENDED -> {
                tagViewGroup.isDragging = false
                draggingTag.view.alpha = 1f

                tagViewGroup.requestLayout()
            }
        }

        return true
    }

    private fun getRearrangePair(draggingTag: Tag, event: DragEvent): Pair<Int, Int>? {
        val targetTag = tagViewGroup.tagList.find { isPointOnView(it, event) }
        targetTag?.let {
            val draggingViewIndex = tagViewGroup.tagList.indexOf(draggingTag)
            val targetIndex = tagViewGroup.tagList.indexOf(targetTag)

            val shouldRearrange = if (draggingTag.rowIndex == targetTag.rowIndex) {
                val direction = sensitivityAdjuster.isEnoughHorizontalMove(event.x)

                val isEnoughLeftMove = direction == DIRECTION.LEFT && draggingViewIndex > targetIndex
                val isEnoughRightMove = direction == DIRECTION.RIGHT && draggingViewIndex < targetIndex

                isEnoughLeftMove || isEnoughRightMove
            }
            else {
                val direction = sensitivityAdjuster.isEnoughVerticalMove(event.y)
                val isEnoughUpMove = direction == DIRECTION.UP && draggingTag.rowIndex > targetTag.rowIndex
                val isEnoughDownMove = direction == DIRECTION.DOWN && draggingTag.rowIndex < targetTag.rowIndex

                isEnoughUpMove || isEnoughDownMove
            }

            if (shouldRearrange) {
                return Pair(draggingViewIndex, targetIndex)
            }
        }

        return null
    }

    private fun isPointOnView(tag: Tag, event: DragEvent): Boolean {
        return with (tag.view) {
            left <= event.x && event.x <= right && top <= event.y && event.y <= bottom
        }
    }

    private fun getIndexToAdd(draggingTag: Tag, targetIndex: Int): Int {
        val indexToAdd: Int
        val tagCountOnDraggingTagRow = tagViewGroup.tagList.count { it.rowIndex == draggingTag.rowIndex }
        indexToAdd = if (tagCountOnDraggingTagRow == 1) {
            targetIndex
        } else {
            targetIndex - 1
        }
        return indexToAdd
    }
}