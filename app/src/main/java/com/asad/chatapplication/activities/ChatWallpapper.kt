package com.asad.chatapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.adapters.WallpaperAdapter
import com.asad.chatapplication.utils.DataProccessor
import com.asad.chatapplication.utils.RecyclerItemClickListener
import com.asad.chatapplication.utils.StaticFunctions.Companion.GetWallpapperList

class ChatWallpapper : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var adapter: WallpaperAdapter? = null
    var dataProcessor: DataProccessor? = null
    var clearImageLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_wallpapper)

        setTitle("Chat Background")

        dataProcessor = DataProccessor(this)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        clearImageLayout = findViewById(R.id.clearImageLayout)
        clearImageLayout?.setOnClickListener {
            dataProcessor!!.removeValue("wallpapper_pos")
            onBackPressed()
        }

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