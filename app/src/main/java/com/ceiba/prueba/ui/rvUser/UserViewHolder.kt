package com.ceiba.prueba.ui.rvUser

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ceiba.prueba.activities.ProfileActivity
import com.ceiba.prueba.databinding.ItemUserBinding
import com.ceiba.prueba.db.User

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var context: Context
    private val binding = ItemUserBinding.bind(view)

    fun bind(user: User) {

        context = itemView.context

        binding.tvName.text = user.name
        binding.tvPhone.text = user.phone
        binding.tvEmail.text = user.email

        binding.btnViewPublications.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("userId", user.id.toString())
            intent.putExtra("userName", user.name)
            intent.putExtra("userPhone", user.phone)
            intent.putExtra("userEmail", user.email)
            context.startActivity(intent)
        }
    }

}
