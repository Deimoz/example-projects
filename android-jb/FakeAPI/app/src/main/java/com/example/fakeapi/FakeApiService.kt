package com.example.fakeapi

import retrofit2.Call
import retrofit2.http.*

interface FakeApiService {

    @GET("posts")
    fun listPosts(): Call<List<PostClass>>

    @FormUrlEncoded
    @POST("posts")
    fun post(@Field("title") title : String, @Field("body") body : String, @Field("userId") userId : Int) : Call<PostClass>

    @DELETE("posts/{id}")
    fun del(@Path("id") id: String) : Call<PostClass>
}