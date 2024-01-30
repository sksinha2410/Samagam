package com.ingray.samagam

import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class AutoScrollManager(private val recyclerView: RecyclerView) {

    private val handler = Handler()
    private val scrollDelay: Long = 3000 // Time delay in milliseconds

    private val scrollRunnable = object : Runnable {
        override fun run() {
            smoothScrollToNext()
            handler.postDelayed(this, scrollDelay)
        }
    }

    fun startAutoScroll() {
        handler.postDelayed(scrollRunnable, scrollDelay)
    }

    fun stopAutoScroll() {
        handler.removeCallbacks(scrollRunnable)
    }

    private fun smoothScrollToNext() {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val nextPosition = (firstVisibleItemPosition + 1) % layoutManager.itemCount
            smoothScrollToPosition(nextPosition)
        }
    }

    private fun smoothScrollToPosition(position: Int) {
        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = position
        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
    }
}
