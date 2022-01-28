package com.ceiba.prueba

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceiba.prueba.databinding.ActivityMainBinding
import com.ceiba.prueba.db.AppDatabase
import com.ceiba.prueba.db.User
import com.ceiba.prueba.db.UserDao
import com.ceiba.prueba.service.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var userDao: UserDao

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private val fullList: MutableList<User> = mutableListOf()
    private val userList: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.invoke(this)
        userDao = db.userDao()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svUser.setOnQueryTextListener(this)
        initRecycleView()
    }

    private fun initRecycleView() {
        binding.svUser.queryHint = "Buscar usuario"
        adapter = UserAdapter(userList)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
        searchUsers()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getUsers()
            val users = call.body()
            if (call.isSuccessful) {
                if (users != null) {
                    userDao.insertAll(users)
                }
                userList.clear()
                fullList.clear()
                fullList.addAll(userDao.getAll())
                userList.addAll(userDao.getAll())
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                    hideKeyboard()
                }
            } else {
                runOnUiThread {
                    showError()
                    hideKeyboard()
                }
            }
        }
    }

    private fun searchByUser(query: String) {
        userList.clear()
        userList.addAll(fullList)
        userList.retainAll { it.name?.toLowerCase()!!.contains(query) }
        adapter.notifyDataSetChanged()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchByUser(query)
        }
        return true
    }

}