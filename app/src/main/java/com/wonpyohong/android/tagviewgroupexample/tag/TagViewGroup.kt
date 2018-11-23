package com.wonpyohong.android.tagviewgroupexample.tag

import android.animation.LayoutTransition
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wonpyohong.android.tagviewgroupexample.R
import kotlin.math.max


class TagViewGroup: ViewGroup {
    internal val tagList = mutableListOf<Tag>()
    private var isFirstOnMeasure = true
    internal var isDragging = false

    private val horizontalSpacing = 30
    private val verticalSpacing = 30

    private val horizontalPadding = dp2px(10f)
    private val verticalPadding = dp2px(4f)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setOnDragListener(TagViewDragListener(this))
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isFirstOnMeasure) {      // to add Views in xml
            val viewList = (0..(childCount - 1)).map { getChildAt(it) as TextView }
            tagList += viewList.map { Tag(it) }
            tagList.forEach { tag ->
                tag.view.setBackgroundResource(R.drawable.round_rect_white_button)
                tag.view.setOnLongClickListener { view ->
                    startDragCompat(tag)
                    view.alpha = 0.5f

                    true
                }
            }

            isFirstOnMeasure = false
        }

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        var width = 0
        var height = 0

        var rowIndex = 0
        var rowWidth = 0
        var rowMaxHeight = 0

        tagList.forEach {
            val childView = it.view
            val childWidth = childView.measuredWidth
            val childHeight = childView.measuredHeight

            if (childView.visibility != View.GONE) {
                rowWidth += childWidth
                if (rowWidth > widthSize) {
                    rowWidth = childWidth
                    height += rowMaxHeight + verticalSpacing
                    rowMaxHeight = childHeight

                    rowIndex++
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight)
                }

                rowWidth += horizontalSpacing
            }
        }
        height += rowMaxHeight
        height += paddingTop + paddingBottom

        if (rowIndex == 0) {
            width = rowWidth
            width += paddingLeft + paddingRight
        } else {
            width = widthSize
        }

        setMeasuredDimension(
            if (widthMode == View.MeasureSpec.EXACTLY) widthSize else width,
            if (heightMode == View.MeasureSpec.EXACTLY) heightSize else height
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
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
                    val childWidth = view.measuredWidth
                    val childHeight = view.measuredHeight

                    if ((isDragging && tag.rowIndex > rowIndex) || childLeft + childWidth > parentRight) {
                        childLeft = parentLeft
                        childTop += rowMaxHeight + verticalSpacing
                        rowMaxHeight = childHeight

                        rowIndex++
                    } else {
                        rowMaxHeight = max(rowMaxHeight, childHeight)
                    }

                    tag.rowIndex = rowIndex
                    this.view.layout(childLeft, childTop, childLeft + view.measuredWidth, childTop + view.measuredHeight)
                    childLeft += childWidth + horizontalSpacing
                }
            }
        }
    }

    fun addTag(text: String) {
        val tagView = TextView(context).apply {
            this.text = text
            textSize = sp2px(10f)
            setPadding(horizontalPadding.toInt(), verticalPadding.toInt(),
                horizontalPadding.toInt(), verticalPadding.toInt()
            )
            setBackgroundResource(R.drawable.round_rect_white_button)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tag = Tag(this)
            setOnLongClickListener { view ->
                startDragCompat(tag)
                view.alpha = 0.5f

                true
            }
        }

        addView(tagView)
    }

    private fun startDragCompat(tag: Tag) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            tag.view.startDragAndDrop(null, DragShadowBuilder(tag.view), tag, 0)
        } else {
            tag.view.startDrag(null, DragShadowBuilder(tag.view), tag, 0)
        }
    }

    internal fun isChangingLayout() = layoutTransition.isChangingLayout

    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    private fun sp2px(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
    }
}