package com.ceiba.prueba.ui.rvPost

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ceiba.prueba.databinding.ItemPostBinding
import com.ceiba.prueba.db.Post

class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemPostBinding.bind(view)

    fun bind(post: Post) {
        binding.tvTitle.text = post.title
        binding.tvBody.text = post.body
    }

}
