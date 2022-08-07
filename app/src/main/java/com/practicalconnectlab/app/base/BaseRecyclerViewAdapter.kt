package com.practicalconnectlab.app.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<S, T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    // S = Model/Data class , T = RecyclerView.ViewHolder

    // list of all items
    var items = mutableListOf<S>()
        private set

    /**
     * create view holder of recycler view item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        return createItemViewHolder(parent)
    }

    /**
     * bind recycler view item with data
     */
    override fun onBindViewHolder(viewHolder: T, position: Int) {
        bindItemViewHolder(viewHolder, position)
    }

    /**
     * get count for visible count
     */
    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * abstract method to create custom view holder
     */
    protected abstract fun createItemViewHolder(parent: ViewGroup): T

    /**
     * abstract method to bind custom data
     */
    protected abstract fun bindItemViewHolder(viewHolder: T, position: Int)

    /**
     * add all items to list
     */
    fun addAll(items: ArrayList<S>, clearPreviousItems: Boolean = false) {
        if (clearPreviousItems) {
            this.items.clear()
        }
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * add item (at postion - optional)
     */
    fun addItem(item: S, position: Int = items.size, clearPreviousItems: Boolean = false) {
        var adapterPosition = position
        if (clearPreviousItems) {
            this.items.clear()
            adapterPosition = 0 // set position to 0 after items arrayList gets clear
        }
        this.items.add(adapterPosition, item)
        notifyDataSetChanged()
    }

    /**
     * get item at position
     */
    fun getItemAt(position: Int): S? {
        return items[position]
    }
}