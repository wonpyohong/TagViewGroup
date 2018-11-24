package com.wonpyohong.android.tagviewgroupexample.tag

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

data class Tag(val view: TextView, var rowIndex: Int = 0)