package com.wonpyohong.android.tagviewgroupexample

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.Toast
import com.wonpyohong.android.tagviewgroupexample.tag.Tag
import com.wonpyohong.android.tagviewgroupexample.tag.TagViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tagViewGroup.addTag("55555344444")
        tagViewGroup.addTag("111111")
        tagViewGroup.addTagList(listOf("element1", "element2"))

        tagViewGroup.onTagClickListener = object: TagViewGroup.OnTagClickListener {
            override fun onTagClick(tag: Tag) {
                Toast.makeText(this@MainActivity, "${tag.view.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
