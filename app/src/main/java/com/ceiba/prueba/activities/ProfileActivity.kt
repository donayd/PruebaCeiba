package com.ceiba.prueba.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceiba.prueba.databinding.ActivityProfileBinding
import com.ceiba.prueba.db.Post
import com.ceiba.prueba.service.APIService
import com.ceiba.prueba.ui.rvPost.PostAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var adapter: PostAdapter

    private lateinit var userId: String

    private val postList: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        userId = intent.getStringExtra("userId").toString()
        binding.tvName.text = intent.getStringExtra("userName").toString()
        binding.tvPhone.text = intent.getStringExtra("userPhone").toString()
        binding.tvEmail.text = intent.getStringExtra("userEmail").toString()

        initRecycleView()
        searchPost()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initRecycleView() {
        adapter = PostAdapter(postList)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
    }

    private fun searchPost() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java)
                .getUserPost("posts?userId=$userId")
            val posts = call.body()
            if (call.isSuccessful) {
                if (posts != null) {
                    postList.clear()
                    postList.addAll(posts)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            } else {
                runOnUiThread {
                    showError()
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

}