package com.wonpyohong.android.tagviewgroupexample.tag

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView


class TagViewGroup: ViewGroup {
    internal val tagList = mutableListOf<Tag>()
    private var isFirstOnMeasure = true
    internal var isDragging = false

    private val defaultHorizontalPadding = dp2px(context, 8f)
    private val defaultVerticalPadding = dp2px(context, 4f)

    private val defaultTextSize = 18f

    var onTagClickListener: OnTagClickListener? = null

    private var tagLayoutHelper: TagLayoutHelper

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setOnDragListener(TagViewDragListener(this))
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        tagLayoutHelper = TagLayoutHelper(context, paddingLeft, paddingTop, paddingRight, paddingBottom)

        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val appendTag = tagList.find { it.type == TagType.APPEND }
                if ((appendTag?.view as EditText).isFocused) {
                    val outRect = Rect()
                    appendTag.view.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        appendTag.view.clearFocus()
                    }
                }
            }
            false
        }

        setOnHierarchyChangeListener(object: OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                child!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                child as TextView

                val tag = Tag(child, type = (child.tag ?: TagType.NORMAL) as TagType)
                tagList += tag

                child.setOnClickListener(tag.type.getOnClickListener(tag, onTagClickListener))
                child.setOnLongClickListener(tag.type.getOnLongClickListener(tag))
                child.setOnFocusChangeListener(tag.type.getOnFocusChangeListener(context, this@TagViewGroup))

                setTextViewAttribute(tag)
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                tagList.remove(tagList.find { it.view == child })
                tagList.find { Tag::type == TagType.APPEND } ?: addLastTag()
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        addLastTag()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        tagLayoutHelper.updateRowGroupedTagMap(tagList, widthSize, isDragging)

        if (heightMode == View.MeasureSpec.EXACTLY) {
            val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
            setMeasuredDimension(widthSize, heightSize)
        } else {
            val height = tagLayoutHelper.calculateMeasureHeight()
            setMeasuredDimension(widthSize, height)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        tagLayoutHelper.onLayout()
    }

    fun setTagList(textList: List<String>) {
        removeAllViews()
        addTagList(textList)
    }

    fun addTagList(textList: List<String>) {
        textList.forEach { addTag(it) }
    }

    fun addTag(text: String) {
        val tagView = TextView(context).apply {
            this.text = text
            this.tag = TagType.NORMAL
        }

        addView(tagView)
    }

    fun addLastTag() {
        val tagView = EditText(context).apply {
            setText("추가")
            minWidth = 100
            this.tag = TagType.APPEND

            setOnDragListener { v, event -> true }
            setTextIsSelectable(false)
        }

        addView(tagView)
    }

    private fun setTextViewAttribute(tag: Tag) {
        with (tag.view) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSize)
            setTextColor(tag.type.getTextColor(isSelected))
            setPadding(defaultHorizontalPadding, defaultVerticalPadding, defaultHorizontalPadding, defaultVerticalPadding)

            background = tag.type.getBackground(context)
        }
    }

    internal fun isChangingLayout() = layoutTransition.isChangingLayout

    interface OnTagClickListener {
        fun onTagClick(tag: Tag)
    }
}