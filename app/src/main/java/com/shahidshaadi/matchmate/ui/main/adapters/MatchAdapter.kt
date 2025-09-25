package com.shahidshaadi.matchmate.ui.main.adapters

import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shahidshaadi.matchmate.R
import com.shahidshaadi.matchmate.databinding.ItemMatchCardBinding
import com.shahidshaadi.matchmate.model.MatchProfile

class MatchAdapter(
    private val onAccept: (String) -> Unit,
    private val onDecline: (String) -> Unit
) : ListAdapter<MatchProfile, MatchAdapter.ViewHolder>(MatchDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMatchCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: MatchProfile) {
            binding.apply {
                tvName.text = profile.name
                tvAge.text = itemView.context.getString(R.string.age_label, profile.age)
                tvGender.text = itemView.context.getString(R.string.gender_label, profile.gender)
                tvLocation.text = profile.fullLocation

                Glide.with(itemView.context)
                    .load(profile.photoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfile)

                btnAccept.setOnClickListener { onAccept(profile.id) }
                btnDecline.setOnClickListener { onDecline(profile.id) }

                when (profile.status) {
                    "accepted" -> {
                        btnAccept.isVisible = false
                        btnDecline.isVisible = false
                        statusText.isVisible = true
                        statusText.text = itemView.context.getString(R.string.member_accepted)
                        statusText.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_status_accepted)
                    }
                    "declined" -> {
                        btnAccept.isVisible = false
                        btnDecline.isVisible = false
                        statusText.isVisible = true
                        statusText.text = itemView.context.getString(R.string.member_declined)
                        statusText.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_status_declined)
                    }
                    else -> {
                        btnAccept.isVisible = true
                        btnDecline.isVisible = true
                        statusText.text = ""
                        statusText.isVisible = false
                    }
                }
            }
        }
    }

    class MatchDiffCallback : DiffUtil.ItemCallback<MatchProfile>() {
        override fun areItemsTheSame(oldItem: MatchProfile, newItem: MatchProfile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MatchProfile, newItem: MatchProfile): Boolean {
            return oldItem == newItem
        }
    }
}