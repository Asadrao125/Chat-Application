package com.asad.chatapplication.activities

import android.graphics.Point
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.adapters.WallpaperAdapter
import com.asad.chatapplication.models.ChatModel
import com.asad.chatapplication.utils.DataProccessor
import com.asad.chatapplication.utils.RecyclerItemClickListener
import com.asad.chatapplication.utils.StaticFunctions.Companion.GetWallpapperList
import com.google.firebase.auth.FirebaseAuth

class ChatWallpapper : AppCompatActivity() {
    var wallpaperList: ArrayList<Int> = ArrayList()
    var recyclerView: RecyclerView? = null
    var adapter: WallpaperAdapter? = null
    var dataProcessor: DataProccessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_wallpapper)

        setTitle("Chat Wallpapper")

        dataProcessor = DataProccessor(this)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        recyclerView = findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)

        adapter = WallpaperAdapter(this, GetWallpapperList())
        adapter!!.selectedPosition = dataProcessor!!.getInt("wallpapper_pos")
        recyclerView!!.adapter = adapter

        recyclerView?.addOnItemTouchListener(
            RecyclerItemClickListener(this,
                recyclerView!!, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        adapter!!.selectedPosition = position
                        adapter!!.notifyDataSetChanged()
                        dataProcessor!!.setInt("wallpapper_pos", position)
                        onBackPressed()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {}
                })
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}