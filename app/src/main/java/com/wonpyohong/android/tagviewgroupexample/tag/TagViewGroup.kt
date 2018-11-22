package com.wonpyohong.android.tagviewgroupexample.tag

import android.animation.LayoutTransition
import android.content.ClipData
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.max


class TagViewGroup: ViewGroup {
    internal val tagList = mutableListOf<Tag>()
    private var isFirstOnLayout = true
    internal var isDragging = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setOnDragListener(TagViewDragListener(this))
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(
            if (widthMode == View.MeasureSpec.EXACTLY) widthSize else width,
            if (heightMode == View.MeasureSpec.EXACTLY) heightSize else height
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isFirstOnLayout) {      // to add Views in xml
            val viewList = (0..(childCount - 1)).map { getChildAt(it) as TextView }
            tagList += viewList.map { Tag(it) }
            tagList.forEach { tag ->
                tag.view.setOnLongClickListener { view ->
                    startDragCompat(tag)
                    view.alpha = 0.5f

                    true
                }
            }

            isFirstOnLayout = false
        }

        val parentLeft = paddingLeft
        val parentRight = r - l - paddingRight
        val parentTop = paddingTop

        var childLeft = parentLeft
        var childTop = parentTop

        var rowMaxHeight = 0

        var rowIndex = 0
        for (tag in tagList) {
            if (tag.view.visibility != View.GONE) {
                with (tag) {
                    if ((isDragging && tag.rowIndex > rowIndex) || childLeft + view.measuredWidth > parentRight) {
                        childLeft = parentLeft
                        childTop += view.measuredHeight
                        rowMaxHeight = view.measuredHeight

                        rowIndex++
                    } else {
                        rowMaxHeight = max(rowMaxHeight, view.measuredHeight)
                    }

                    tag.rowIndex = rowIndex
                    this.view.layout(childLeft, childTop, childLeft + view.measuredWidth, childTop + view.measuredHeight)
                    childLeft += view.measuredWidth
                }
            }
        }
    }

    private fun startDragCompat(tag: Tag) {
        val clipData = ClipData.newPlainText("originalIndex", tagList.indexOf(tag).toString())
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            tag.view.startDragAndDrop(clipData, DragShadowBuilder(tag.view), tag, 0)
        } else {
            tag.view.startDrag(clipData, DragShadowBuilder(tag.view), tag, 0)
        }
    }

    internal fun isChangingLayout() = layoutTransition.isChangingLayout
}