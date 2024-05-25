package com.tasnimulhasan.common.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class DataBoundViewHolder<out T : ViewBinding> constructor(val binding: T) : RecyclerView.ViewHolder(binding.root)
