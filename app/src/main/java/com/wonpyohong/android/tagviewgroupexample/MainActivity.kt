package com.wonpyohong.android.tagviewgroupexample

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView1.setOnLongClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                textView1.startDragAndDrop(null, View.DragShadowBuilder(textView1), textView1, 0)
            } else {
                textView1.startDrag(null, View.DragShadowBuilder(textView1), textView1, 0)
            }

            textView1.visibility = View.INVISIBLE

            true
        }
    }
}
