package com.wonpyohong.android.tagviewgroupexample.tag

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min
import android.animation.LayoutTransition
import android.graphics.Rect
import android.util.Log
import android.widget.TextView
import java.util.*


class TagViewGroup: ViewGroup {

    private val tagList = mutableListOf<Tag>()
    private var isFirstOnLayout = true
    private val overlapCalculator = OverlapCalculator()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setOnDragListener()
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    var prevX = 0f

    private fun setOnDragListener() {
        setOnDragListener { destinationView, event ->
            val draggingTextView = event.localState as TextView
            when (event.action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
                    if (layoutTransition.isChangingLayout) {
                        return@setOnDragListener true
                    }

                    val isRight = prevX - event.x < 0
                    val isLeft = prevX - event.x > 0

                    val draggingRect = Rect((event.x - draggingTextView.width / 2).toInt(), (event.y - draggingTextView.height / 2).toInt(),
                        (event.x + draggingTextView.width / 2).toInt(), (event.y + draggingTextView.height / 2).toInt()
                    )

                    var pair: Pair<Int, Int>? = null
                    for ((index, tag) in tagList.withIndex()) {
                        if (draggingTextView.text != tag.view.text.toString() && overlapCalculator.isOverlap(draggingRect, tag.rect)) {
                            val draggingViewIndex = tagList.indexOfFirst { it.view.text.toString() == draggingTextView.text.toString() }

                            if ((isRight && draggingViewIndex < index && draggingRect.right > tag.rect.right)
                            || (isLeft && draggingViewIndex > index && draggingRect.left < tag.rect.left)) {
                                pair = Pair(draggingViewIndex, index)

                                break
                            }
                        }
                    }

                    if (pair != null) {
                        Collections.swap(tagList, pair.first, pair.second)

                        requestLayout()
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

            true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isFirstOnLayout) {
            var childLeft = 0
            for (childIndex in 0 .. (childCount - 1)) {
                val child = getChildAt(childIndex) as TextView
                with(child) {
                    val rect = Rect(childLeft, t, childLeft + measuredWidth, b)
                    tagList.add(Tag(this, rect))
                    childLeft += measuredWidth

                    layout(rect.left, rect.top, rect.right, rect.bottom)
                }
            }

            isFirstOnLayout = false
        } else {
            var childLeft = 0
            tagList.forEach { tag ->
                with(tag.view) {
                    tag.rect = Rect(childLeft, t, childLeft + measuredWidth, b)
                    childLeft += measuredWidth

                    layout(tag.rect.left, tag.rect.top, tag.rect.right, tag.rect.bottom)
                }
            }
        }
    }
}