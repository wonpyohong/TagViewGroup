package com.wonpyohong.android.tagviewgroupexample.tag

import android.graphics.Point
import android.graphics.Rect
import android.view.DragEvent
import android.view.View

class TagViewDragListener(val tagViewGroup: TagViewGroup): View.OnDragListener {
    private var prevX = 0f
    private var prevY = 0f

    var lastLeftMostX = 0f
    var lastRightMostX = 0f
    var lastUpMostX = 0f
    var lastDownMostX = 0f

    override fun onDrag(destinationView: View, event: DragEvent): Boolean {
        val draggingTag = event.localState as Tag
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                tagViewGroup.isDragging = true
                lastLeftMostX = 0f
                lastRightMostX = tagViewGroup.width.toFloat()
                lastUpMostX = 0f
                lastDownMostX = tagViewGroup.height.toFloat()
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                if (tagViewGroup.isChangingLayout()) {
                    return true
                }

                shouldRearrange(draggingTag, event)?.let { (dragginViewIndex, targetIndex) ->
                    var indexToAdd = targetIndex

                    val isDownDragging = draggingTag.rowIndex < tagViewGroup.tagList[targetIndex].rowIndex
                    val isUpDragging = draggingTag.rowIndex > tagViewGroup.tagList[targetIndex].rowIndex

                    if (isDownDragging) {
                        val tagCountOnDraggingTagRow = tagViewGroup.tagList.count { it.rowIndex == draggingTag.rowIndex }
                        if (tagCountOnDraggingTagRow == 1) {
                            indexToAdd = targetIndex
                        } else {
                            indexToAdd = targetIndex -1
                        }

                        draggingTag.rowIndex = tagViewGroup.tagList[targetIndex].rowIndex
                    } else if (isUpDragging) {
                        draggingTag.rowIndex = tagViewGroup.tagList[targetIndex].rowIndex
                    }

                    tagViewGroup.tagList.removeAt(dragginViewIndex)
                    tagViewGroup.tagList.add(indexToAdd, draggingTag)

                    tagViewGroup.requestLayout()
                }

                prevX = event.x
                prevY = event.y
            }

            DragEvent.ACTION_DROP, DragEvent.ACTION_DRAG_ENDED -> {
                tagViewGroup.isDragging = false
                draggingTag.view.alpha = 1f

                tagViewGroup.requestLayout()
            }
        }

        return true
    }

    private fun shouldRearrange(draggingTag: Tag, event: DragEvent): Pair<Int, Int>? {
        val isRight = prevX - event.x < 0
        val isLeft = prevX - event.x > 0
        val isUp = prevY - event.y > 0
        val isDown = prevY - event.y < 0

        val draggingCenter = Point(event.x.toInt(), event.y.toInt())

        tagViewGroup.tagList.forEachIndexed { targetIndex, tag ->
            if (draggingCenter in tag.view.getRect()) {
                val draggingViewIndex = tagViewGroup.tagList.indexOfFirst { it == draggingTag }

                return if (draggingTag.rowIndex == tag.rowIndex) {
                    if (isLeft) {
                        lastLeftMostX = event.x
                        if (draggingViewIndex > targetIndex && lastRightMostX - event.x > 100) {
                            Pair(draggingViewIndex, targetIndex)
                        } else {
                            null
                        }
                    } else if (isRight) {
                        lastRightMostX = event.x
                        if (draggingViewIndex < targetIndex && event.x - lastLeftMostX > 100) {
                            Pair(draggingViewIndex, targetIndex)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } else if (draggingTag.rowIndex != tag.rowIndex) {
                    if (isUp) {
                        lastUpMostX = event.y
                        if (draggingTag.rowIndex > tag.rowIndex && lastDownMostX - event.y > 100) {
                            Pair(draggingViewIndex, targetIndex)
                        } else {
                            null
                        }
                    } else if (isDown) {
                        lastDownMostX = event.y
                        if (draggingTag.rowIndex < tag.rowIndex && event.y - lastUpMostX > 100) {
                            Pair(draggingViewIndex, targetIndex)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }

        return null
    }

    fun View.getRect() = Rect(left, top, right, bottom)

    operator fun Rect.contains(point: Point) = contains(point.x, point.y)
}