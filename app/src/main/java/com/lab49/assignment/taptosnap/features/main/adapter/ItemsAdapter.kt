package com.lab49.assignment.taptosnap.features.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.data.model.request.local.ItemWrapper
import com.lab49.assignment.taptosnap.databinding.RowItemBinding
import com.lab49.assignment.taptosnap.util.Constants

class ItemsAdapter(private val callback: (ItemWrapper) -> Unit) :
    ListAdapter<ItemWrapper, ItemsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bindItem = getItem(position)
        holder.bind(bindItem)
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val tappedItem = getItem(adapterPosition)
                if (tappedItem.canTap()) {
                    callback.invoke(tappedItem)
                }
            }
        }
    }

    class ViewHolder(private var binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemWrapper) {
            binding.tvImageName.text = item.itemName
            binding.ivCaptured.setImageBitmap(item.bitmap)
            when (item.state) {
                Constants.STATE.NOT_STARTED -> {
                    notStarted()
                }
                Constants.STATE.RUNNING -> {
                    running()
                }
                Constants.STATE.FAILED -> {
                    failed()
                }
                Constants.STATE.SUCCESS -> {
                    success()
                }
            }
        }

        private fun notStarted() {
            binding.parent.setBackgroundResource(R.drawable.state_loading)
            binding.tvTryAgain.isGone = true
            binding.progressBar.isGone = true
            binding.ivCamera.isVisible = true
        }

        private fun running() {
            binding.parent.setBackgroundResource(R.drawable.state_loading)
            binding.tvTryAgain.isGone = true
            binding.progressBar.isVisible = true
            binding.ivCamera.isGone = true
        }

        private fun failed() {
            binding.parent.setBackgroundResource(R.drawable.state_failed)
            binding.tvTryAgain.isVisible = true
            binding.progressBar.isGone = true
            binding.ivCamera.isGone = true
        }

        private fun success() {
            binding.parent.setBackgroundResource(R.drawable.state_success)
            binding.tvTryAgain.isGone = true
            binding.progressBar.isGone = true
            binding.ivCamera.isGone = true
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
        override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
            return oldItem == newItem
        }
    }
}