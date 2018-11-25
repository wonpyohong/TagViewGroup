package com.wonpyohong.android.tagviewgroupexample.tag

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.Log
import android.util.StateSet
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.wonpyohong.android.tagviewgroupexample.R

data class Tag(val view: TextView, var rowIndex: Int = 0, val type: TagType = TagType.NORMAL)

enum class TagType {
    NORMAL {
        override fun getOnClickListener(tag: Tag, onTagClickListener: TagViewGroup.OnTagClickListener?): View.OnClickListener {
            return getOnNormalTagClickListener(onTagClickListener, tag)
        }

        override fun getOnLongClickListener(tag: Tag): View.OnLongClickListener {
            return getOnNormalLongClickListener(tag)
        }

        override fun getTextColor(isSelected: Boolean) = if (isSelected) defaultSelectedTextColor else defaultTextColor

        override fun getBackground(context: Context) = getNormalTagBackground(context)
    },
    APPEND {
        override fun getOnFocusChangeListener(context: Context, tagViewGroup: TagViewGroup): View.OnFocusChangeListener {
            return View.OnFocusChangeListener { view, hasFocus ->
                view as EditText

                if (hasFocus) {
                    Log.d("HWP", "has focus")
                    view.setText("")
                    showKeyboard(context, view)
                } else {
                    Log.d("HWP", "lose focus")
                    if (view.text.isNotEmpty()) {
                        tagViewGroup.addTag(view.text.toString())
                        tagViewGroup.removeView(view)

                        tagViewGroup.requestLayout()
                    }

                    hideKeyboard(context, view)
                }
            }
        }

        override fun getTextColor(isSelected: Boolean) = Color.LTGRAY

        override fun getBackground(context: Context) = getAppendTagBackground(context)
    }
    ;

    protected val defaultTextColor = Color.RED
    protected val defaultSelectedTextColor = Color.WHITE

    private val defaultBackgroundColor = Color.WHITE
    private val defaultBorderColor = Color.RED
    private val defaultSelectedBackgroundColor = Color.RED

    abstract fun getBackground(context: Context): Drawable
    abstract fun getTextColor(isSelected: Boolean = false): Int
    open fun getOnClickListener(tag: Tag, onTagClickListener: TagViewGroup.OnTagClickListener?): View.OnClickListener? = null
    open fun getOnLongClickListener(tag: Tag): View.OnLongClickListener? = null
    open fun getOnFocusChangeListener(context: Context, tagViewGroup: TagViewGroup): View.OnFocusChangeListener? = null

    protected fun getNormalTagBackground(context: Context): StateListDrawable {
        val defaultRadius = dp2px(context, 24f)

        val selectedDrawable = context.getDrawable(R.drawable.selected_tag_background) as GradientDrawable
        selectedDrawable.setColor(defaultSelectedBackgroundColor)
        selectedDrawable.cornerRadius = defaultRadius.toFloat()

        val defaultDrawable = context.getDrawable(R.drawable.default_tag_background) as GradientDrawable
        defaultDrawable.setColor(defaultBackgroundColor)
        defaultDrawable.setStroke(dp2px(context, 1f), defaultBorderColor)
        defaultDrawable.cornerRadius = defaultRadius.toFloat()

        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable)
        return stateListDrawable
    }

    protected fun getAppendTagBackground(context: Context): GradientDrawable {
        val defaultDrawable = context.getDrawable(R.drawable.default_tag_background) as GradientDrawable

        with (defaultDrawable) {
            setColor(defaultBackgroundColor)
            setStroke(dp2px(context, 1f), defaultBorderColor, dp2px(context, 5f).toFloat(), dp2px(context, 3f).toFloat())

            val defaultRadius = dp2px(context, 24f)
            cornerRadius = defaultRadius.toFloat()
        }

        return defaultDrawable
    }

    protected fun getOnNormalTagClickListener(onTagClickListener: TagViewGroup.OnTagClickListener?, tag: Tag): View.OnClickListener {
        return View.OnClickListener { view ->
            view as TextView
            view.isSelected = !view.isSelected
            view.setTextColor(getTextColor(view.isSelected))

            onTagClickListener?.onTagClick(tag)
        }
    }

    protected fun getOnNormalLongClickListener(tag: Tag): View.OnLongClickListener {
        return View.OnLongClickListener { view ->
            startDragCompat(tag)
            view.alpha = 0.5f

            true
        }
    }

    fun startDragCompat(tag: Tag) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            tag.view.startDragAndDrop(null, TagDragShadowBuilder(tag.view), tag, 0)
        } else {
            tag.view.startDrag(null, TagDragShadowBuilder(tag.view), tag, 0)
        }
    }
}