package com.asad.chatapplication.activities

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.adapters.ImageAdapter
import com.asad.chatapplication.utils.StaticFunctions

class UploadImage : AppCompatActivity() {
    var selectedFilesList: ArrayList<Uri> = ArrayList()
    var recyclerView: RecyclerView? = null

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        val btn: Button = findViewById(R.id.btn)
        recyclerView = findViewById(R.id.recyclerView)

        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)

        btn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 11)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11
            && resultCode == RESULT_OK
            && data != null
        ) {
            selectedFilesList.clear()
            if (data.data != null) {
                selectedFilesList.add(data.data!!)
            } else {
                val count: Int = data.getClipData()!!.getItemCount()
                if (count > 1) {
                    for (i in 0 until count) {
                        val imageUri: Uri = data.getClipData()!!.getItemAt(i).getUri()
                        selectedFilesList.add(imageUri)
                    }
                } else {
                    selectedFilesList.add(data.data!!)
                }
            }

            recyclerView?.adapter = ImageAdapter(applicationContext, selectedFilesList)
        }
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return path
    }
}