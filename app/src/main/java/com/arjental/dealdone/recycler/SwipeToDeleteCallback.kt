package com.arjental.dealdone.recycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R
import com.arjental.dealdone.recycler.viewholders.TaskViewHolder
import com.arjental.dealdone.recycler.viewholders.TaskWithTimeViewHolder

abstract class SwipeToDeleteCallback(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_recycler_icon)
    private val intrinsicWidth = deleteIcon!!.intrinsicWidth
    private val intrinsicHeight = deleteIcon!!.intrinsicHeight

    private val doneIcon = ContextCompat.getDrawable(context, R.drawable.ic_done_recycler_item)
    private val intrinsicWidthDone = doneIcon!!.intrinsicWidth
    private val intrinsicHeightDone = doneIcon!!.intrinsicHeight

    private val background = ColorDrawable()
    private val backgroundColor = ResourcesCompat.getColor(
        context.resources,
        R.color.color_light_green,
        context.theme
    )
    private val backgroundColorRed = ResourcesCompat.getColor(
        context.resources,
        R.color.color_light_red,
        context.theme
    )
    private val clearPaint =
        Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        /**
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */
//        if (viewHolder?.absoluteAdapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
            return
        }

        if (dX > 0) {
            background.color = backgroundColor
            background.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            background.draw(c)
        } else {
            background.color = backgroundColorRed
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)
        }

        // Calculate position of delete icon

        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = intrinsicWidth * 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        // Calculate position of done icon

        val doneIconTop = itemView.top + (itemHeight - intrinsicHeightDone) / 2
        val doneIconMargin = (itemHeight - intrinsicHeightDone) / 2
        val doneIconLeft = itemView.left + deleteIconMargin
        val doneIconRight = itemView.left + deleteIconMargin + intrinsicWidthDone
        val doneIconBottom = deleteIconTop + intrinsicHeightDone

        // Draw the delete icon

        if (dX < deleteIconLeft - itemView.right) {
            deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon!!.draw(c)
        }

        if (dX > doneIconRight) {
            doneIcon!!.setBounds(doneIconLeft, doneIconTop, doneIconRight, doneIconBottom)
            doneIcon!!.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is TaskViewHolder || viewHolder is TaskWithTimeViewHolder) {
             super.getSwipeDirs(recyclerView, viewHolder)
        } else {
            0
        }
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}