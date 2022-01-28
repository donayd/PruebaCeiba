package com.ceiba.prueba.service

import com.ceiba.prueba.db.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.*

interface APIService {

    @GET("users")
    suspend fun getUsers(): Response<ArrayList<User>>

    @GET("posts")
    suspend fun getPost(@Url url: String): Response<ArrayList<User>>

}