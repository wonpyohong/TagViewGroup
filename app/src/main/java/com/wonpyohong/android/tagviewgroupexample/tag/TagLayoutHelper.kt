package com.wonpyohong.android.tagviewgroupexample.tag

import android.content.Context
import android.util.TypedValue
import android.view.View

internal class TagLayoutHelper(private val context: Context, private val paddingLeft: Int,
                               private val paddingTop: Int, private val paddingRight: Int, private val paddingBottom: Int) {

    private var rowIndexGroupedTagMap: Collection<List<Tag>>? = null

    private val defaultHorizontalSpacing = dp2px(5f)
    private val defaultVerticalSpacing = dp2px(5f)

    internal fun updateRowGroupedTagMap(tagList: List<Tag>, widthSize: Int, isDragging: Boolean) {
        var rowIndex = 0
        var rowWidth = 0

        tagList.forEach { tag ->
            val childWidth = tag.view.measuredWidth

            if (tag.view.visibility != View.GONE) {
                if ((isDragging && tag.rowIndex > rowIndex)
                    || rowWidth + childWidth > widthSize - (paddingLeft + paddingRight)
                ) {
                    rowWidth = 0
                    rowIndex++
                }

                tag.rowIndex = rowIndex
                rowWidth += childWidth + defaultHorizontalSpacing
            }
        }

        rowIndexGroupedTagMap = tagList.groupBy { it.rowIndex }.values
    }

    internal fun calculateMeasureHeight(): Int {
        val maxHeightInRowList = rowIndexGroupedTagMap!!.map { tagListInRow ->
            getMaxHeightInRow(tagListInRow)
        }

        return maxHeightInRowList.sum() + paddingTop + paddingBottom
    }

    private fun getMaxHeightInRow(tagListInRow: List<Tag>): Int {
        val maxHeightTag = tagListInRow.maxBy { it.view.measuredHeight }!!
        return maxHeightTag.view.measuredHeight + defaultVerticalSpacing
    }

    internal fun onLayout() {
        var childTop = paddingTop

        rowIndexGroupedTagMap!!.forEach {tagListInRow ->
            val maxHeightInRow = getMaxHeightInRow(tagListInRow)

            var childLeft = paddingLeft
            tagListInRow.forEach {tag ->
                val offsetForCenterGravity = (maxHeightInRow - defaultVerticalSpacing - tag.view.measuredHeight) / 2
                tag.view.layout(childLeft,
                    childTop + offsetForCenterGravity,
                    childLeft + tag.view.measuredWidth,
                    childTop + offsetForCenterGravity + tag.view.measuredHeight)

                childLeft += tag.view.measuredWidth + defaultHorizontalSpacing
            }

            childTop += maxHeightInRow
        }
    }

    private fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}