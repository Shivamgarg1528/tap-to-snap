package com.lab49.assignment.taptosnap.features.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lab49.assignment.taptosnap.R
import com.lab49.assignment.taptosnap.data.model.request.local.ItemWrapper
import com.lab49.assignment.taptosnap.databinding.RowItemBinding
import com.lab49.assignment.taptosnap.util.Constants
import com.lab49.assignment.taptosnap.util.gone
import com.lab49.assignment.taptosnap.util.visible


class ItemsAdapter(private val callback: (ItemWrapper) -> Unit) :
    ListAdapter<ItemWrapper, ItemsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bindItem = getItem(position)
        holder.bind(bindItem)
        if (bindItem.isImageSaved()) {
            holder.itemView.setOnClickListener(null)
        } else {
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
    }

    class ViewHolder(private var binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemWrapper) {
            binding.tvImageName.text = item.item.name
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
            applyColor(R.color.gradient_btm)
            binding.tvTryAgain.gone()
            binding.progressBar.gone()
            binding.ivCamera.visible()
        }

        private fun running() {
            applyColor(R.color.gradient_btm)
            binding.tvTryAgain.gone()
            binding.progressBar.visible()
            binding.ivCamera.gone()
        }

        private fun failed() {
            applyColor(R.color.red)
            binding.tvTryAgain.visible()
            binding.progressBar.gone()
            binding.ivCamera.gone()
        }

        private fun success() {
            applyColor(R.color.green)
            binding.tvTryAgain.gone()
            binding.progressBar.gone()
            binding.ivCamera.gone()
        }

        private fun applyColor(@ColorRes stroke: Int) {
            val context = binding.parent.context
            binding.parent.apply {
                strokeColor = ContextCompat.getColor(context, stroke)
            }
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