package com.wonpyohong.android.tagviewgroupexample.tag

import android.content.Context
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun showKeyboard(context: Context, editText: EditText) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(editText, 0)
}

fun hideKeyboard(context: Context, editText: EditText) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0)
}

fun dp2px(context: Context, dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
}
