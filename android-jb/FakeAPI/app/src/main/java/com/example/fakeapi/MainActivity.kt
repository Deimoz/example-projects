package com.example.fakeapi

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    //private var viewModel: PostsViewModel? = null
    private lateinit var viewModelRef: WeakReference<PostsViewModel?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel: PostsViewModel? = ViewModelProvider(this, ViewModelFactory(this.application)).get(PostsViewModel::class.java)
        viewModelRef = WeakReference(viewModel)
        PostsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PostAdapter(mutableListOf<PostClass>(), application, viewModel!!)
        }

        main_button.setOnClickListener() {
            val li = LayoutInflater.from(this)
            val dialogView = li.inflate(R.layout.dialog_post, null)
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setView(dialogView)
            val titleInput = dialogView.findViewById<EditText>(R.id.title_input)
            val bodyInput = dialogView.findViewById<EditText>(R.id.body_input)
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                    addPost(titleInput.text.toString(), bodyInput.text.toString(), 111)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                })
            val dialog = dialogBuilder.create()
            dialog.show()
        }

        refresh_button.setOnClickListener {
            loadList()
        }

        viewModel!!.getAll()?.observe(this, object : Observer<List<PostClass>?> {
            override fun onChanged(posts: List<PostClass>?) {
                if (posts == null || posts.size == 0) {
                    loadList()
                } else {
                    posts.let { refreshRecyclerView(it as MutableList<PostClass>) }
                }
            }
        })
    }

    private fun refreshRecyclerView(newPosts: MutableList<PostClass>) {
        (PostsRecyclerView.adapter as PostAdapter).setNewPosts(newPosts)
    }

    private fun loadList() {
        progress_bar.visibility = ProgressBar.VISIBLE
        MyApp.retrofit.listPosts().enqueue(object : Callback<List<PostClass>> {
            override fun onFailure(call: Call<List<PostClass>>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Problems with internet connection",
                    Toast.LENGTH_LONG).show()
                progress_bar.visibility = ProgressBar.INVISIBLE
            }

            override fun onResponse(
                call: Call<List<PostClass>>,
                response: Response<List<PostClass>>
            ) {
                if (response.isSuccessful()) {
                    viewModelRef.get()?.deleteAll()
                    response.body()?.forEach {
                        viewModelRef.get()?.insert(it)
                    }
                }
                progress_bar.visibility = ProgressBar.INVISIBLE
            }
        })
    }

    private fun addPost(title: String, body: String, userId: Int) {
        (PostsRecyclerView.adapter as PostAdapter).addItem(title, body, userId)
    }
}