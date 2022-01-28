package com.ceiba.prueba.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ceiba.prueba.databinding.ItemUserBinding
import com.ceiba.prueba.db.User

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemUserBinding.bind(view)

    fun bind(user: User) {
        binding.tvName.text = user.name
        binding.tvPhone.text = user.phone
        binding.tvEmail.text = user.email

        binding.btnViewPublications.setOnClickListener {

        }
    }

}
