package com.example.fakeapi

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapter(var posts: MutableList<PostClass>, val application: Application, val viewModel: PostsViewModel) : RecyclerView.Adapter<PostAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ContactViewHolder {
        val holder = ContactViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.post, parent, false)
        )

        holder.root.del_button.setOnClickListener {
            if (holder.adapterPosition == -1) {
                return@setOnClickListener
            }
            delItem(holder.adapterPosition)
        }
        return holder
    }

    override fun getItemCount() = posts.size

    public fun setNewPosts(newPosts: MutableList<PostClass>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) = holder.bind(posts[position])

    fun delItem(pos: Int) {
        MyApp.retrofit.del(posts[pos].id.toString()).enqueue(object : Callback<PostClass> {
            override fun onFailure(call: Call<PostClass>, t: Throwable) {
                Toast.makeText(
                    application.applicationContext,
                    "Problems with internet connection",
                    Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<PostClass>, response: Response<PostClass>) {
                val mes : String
                if (response.isSuccessful()) {
                    mes = "Post deleted"
                } else {
                    mes = "Error ${response.code()}"
                }
                Toast.makeText(
                    application.applicationContext,
                    mes,
                    Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.delete(posts[pos])
    }

    fun addItem(title : String, body : String, userId : Int) {
        MyApp.retrofit.post(title, body, userId).enqueue(object : Callback<PostClass> {
            override fun onFailure(call: Call<PostClass>, t: Throwable) {
                Toast.makeText(
                    application.applicationContext,
                    "Problems with internet connection",
                    Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<PostClass>, response: Response<PostClass>) {
                var mes = "Item posted"
                if (response.isSuccessful()) {
                    response.body()?.let { viewModel.insert(it) }
                } else {
                    mes = "Error ${response.code()}"
                }
                Toast.makeText(
                    application.applicationContext,
                    mes,
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    inner class ContactViewHolder(val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(post: PostClass) {
            with(root) {
                post_name.text = post.title
                post_text.text = post.body
            }
        }
    }
}