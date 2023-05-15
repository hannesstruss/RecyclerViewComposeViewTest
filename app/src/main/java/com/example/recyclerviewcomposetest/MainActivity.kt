package com.example.recyclerviewcomposetest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewcomposetest.databinding.ActivityMainBinding

// STEPS TO REPRODUCE
// 1. Run app
// 2. Click on any recycler view item
//    This will simulate an item change notification, which causes a view holder to get re-bound.
//    During the onBindViewHolder call we're adding a ComposeView to the RecyclerView
//    item and calling setContent on it. During onBindViewHolder, the RecyclerView item will not
//    have a parent (parent == null), but still be attached to the window (mAttachInfo != null).
//    This causes the ComposeView's onAttachedToWindow to get called, and it will try and walk up
//    the view hierarchy to find a ViewTreeLifecycleOwner, but fail and crash because of the null
//    parent.

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.recycler.adapter = MyAdapter(layoutInflater)
    binding.recycler.layoutManager = LinearLayoutManager(this)
  }
}

class MyViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

class MyAdapter(val inflater: LayoutInflater) : RecyclerView.Adapter<MyViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val frameLayout = FrameLayout(inflater.context).apply {
      layoutParams = LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT,
      )
    }

    frameLayout.setOnClickListener {
      // Simulate an item change:
      notifyItemChanged(0, Any())
    }

    return MyViewHolder(viewGroup = frameLayout)
  }

  override fun getItemCount(): Int = 20

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    holder.viewGroup.removeAllViews()

    val composeView = ComposeView(inflater.context).apply {
      layoutParams = LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT,
      )
    }

    // Error only occurs if ComposeView was added to the RecyclerView item in onBindViewHolder
    holder.viewGroup.addView(composeView)

    composeView.setContent {
      Box(modifier = Modifier.padding(16.dp)) {
        Box(
          modifier = Modifier
            .background(Color(0xFFFFCCFF))
            .size(width = 64.dp, height = 32.dp)
        ) {
          Text("Pos: $position")
        }
      }
    }
  }
}