package com.shahidshaadi.matchmate.ui.main.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shahidshaadi.matchmate.databinding.ItemLoadingBinding

class LoadingAdapter : RecyclerView.Adapter<LoadingAdapter.LoadingViewHolder>() {

    var isVisible = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    notifyItemInserted(0)
                } else {
                    notifyItemRemoved(0)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingViewHolder {
        val binding = ItemLoadingBinding.inflate(parent.context.getSystemService(android.view.LayoutInflater::class.java), parent, false)
        return LoadingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, position: Int) {}

    override fun getItemCount(): Int = if (isVisible) 1 else 0

    class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)
}