package com.asad.chatapplication.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
    var switchButton: Switch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_wallpapper)

        setTitle("Chat Background")

        dataProcessor = DataProccessor(this)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        clearImageLayout = findViewById(R.id.clearImageLayout)
        switchButton = findViewById(R.id.switchButton)

        clearImageLayout?.setOnClickListener {
            dataProcessor!!.removeValue("wallpapper_pos")
            onBackPressed()
        }

        if (dataProcessor?.getStr("Theme").equals("Dark")) {
            switchButton?.isChecked = true
        } else if (dataProcessor?.getStr("Theme").equals("Light")) {
            switchButton?.isChecked = false
        } else {
            switchButton?.isChecked = true
        }

        switchButton?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                dataProcessor?.setStr("Theme", "Dark")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                dataProcessor?.setStr("Theme", "Light")
            }
        })

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