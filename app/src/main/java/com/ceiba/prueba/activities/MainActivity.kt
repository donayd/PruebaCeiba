package com.ceiba.prueba.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
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
import com.ceiba.prueba.ui.rvUser.UserAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var userDao: UserDao
    private lateinit var progressDialog: ProgressDialog

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter

    private val fullList: MutableList<User> = mutableListOf()
    private val userList: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBD()
        initRecycleView()
    }

    private fun initBD() {
        val db = AppDatabase.invoke(this)
        userDao = db.userDao()
        CoroutineScope(Dispatchers.IO).launch {
            //userDao.nukeTable()
            fullList.addAll(userDao.getAll())
            runOnUiThread {
                if (fullList.isEmpty()) {
                    progressDialog = ProgressDialog(this@MainActivity)
                    progressDialog.setTitle("Descargando información")
                    progressDialog.setMessage("Los usuarios estan siendo descargados por favor espere")
                    progressDialog.show()
                    searchUsers()
                } else {
                    userList.addAll(fullList)
                    adapter.notifyDataSetChanged()
                    hideKeyboard()
                }
            }
        }
    }

    private fun initRecycleView() {
        binding.svUser.setOnQueryTextListener(this)
        binding.svUser.queryHint = "Buscar usuario"
        adapter = UserAdapter(userList)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
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
                fullList.clear()
                fullList.addAll(userDao.getAll())
                runOnUiThread {
                    userList.addAll(fullList)
                    adapter.notifyDataSetChanged()
                    progressDialog.dismiss()
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
        userList.addAll(fullList.filter { it.name?.toLowerCase()!!.contains(query) })
        binding.tvList.visibility = if (userList.isEmpty()) View.VISIBLE else View.GONE
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
        hideKeyboard()
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchByUser(query)
        }
        return true
    }

}