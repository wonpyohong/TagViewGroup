package com.wonpyohong.android.tagviewgroupexample.tag

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
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

    private val defaultHorizontalSpacing = dp2px(5f)
    private val defaultVerticalSpacing = dp2px(5f)

    private val defaultHorizontalPadding = dp2px(8f)
    private val defaultVerticalPadding = dp2px(4f)

    private val defaultTextSize = 18f

    private val defaultTextColor = Color.RED

    private val defaultBackgroundColor = Color.WHITE
    private val defaultBorderColor = Color.RED

    private val defaultRadius = dp2px(24f)

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
                setTextViewAttribute(tag.view as TextView)
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
                    height += rowMaxHeight + defaultVerticalSpacing
                    rowMaxHeight = childHeight

                    rowIndex++
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight)
                }

                rowWidth += defaultHorizontalSpacing
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
                        childTop += rowMaxHeight + defaultVerticalSpacing
                        rowMaxHeight = childHeight

                        rowIndex++
                    } else {
                        rowMaxHeight = max(rowMaxHeight, childHeight)
                    }

                    tag.rowIndex = rowIndex
                    this.view.layout(childLeft, childTop, childLeft + view.measuredWidth, childTop + view.measuredHeight)
                    childLeft += childWidth + defaultHorizontalSpacing
                }
            }
        }
    }

    fun addTag(text: String) {
        val tagView = TextView(context).apply {
            this.text = text
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

    fun setTextViewAttribute(textView: TextView) {
        with (textView) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSize)
            setTextColor(defaultTextColor)
            setPadding(defaultHorizontalPadding, defaultVerticalPadding, defaultHorizontalPadding, defaultVerticalPadding)
            setBackgroundResource(R.drawable.default_tag_background)

            val gradientDrawable = background as GradientDrawable
            gradientDrawable.setColor(defaultBackgroundColor)
            gradientDrawable.setStroke(dp2px(1f), defaultBorderColor)
            gradientDrawable.cornerRadius = defaultRadius.toFloat()
        }
    }

    private fun startDragCompat(tag: Tag) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            tag.view.startDragAndDrop(null, TagDragShadowBuilder(tag.view), tag, 0)
        } else {
            tag.view.startDrag(null, TagDragShadowBuilder(tag.view), tag, 0)
        }
    }

    internal fun isChangingLayout() = layoutTransition.isChangingLayout

    private fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
    }
}