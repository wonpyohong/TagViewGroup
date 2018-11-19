package com.wonpyohong.android.tagviewgroupexample.tag

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TagViewGroup: ViewGroup {

    internal val tagList = mutableListOf<Tag>()
    private var isFirstOnLayout = true

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setOnDragListener(TagViewDragListener(this))
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isFirstOnLayout) {      // xml에 있는 뷰를 tagList에 추가하기 위함
            val viewList = (0..(childCount - 1)).map { getChildAt(it) as TextView }
            tagList += viewList.map { Tag(it) }

            isFirstOnLayout = false
        }

        var childLeft = 0
        for (tagView in tagList.map { it.view }) {
            tagView.layout(childLeft, t, childLeft + tagView.measuredWidth, b)
            childLeft += tagView.measuredWidth
        }
    }

    internal fun isChangingLayout() = layoutTransition.isChangingLayout
}