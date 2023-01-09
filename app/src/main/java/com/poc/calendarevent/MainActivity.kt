package com.poc.calendarevent

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.poc.calendarevent.ICALParser.Companion.TAG
import com.poc.calendarevent.databinding.ActivityMainBinding
import java.io.File
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnImport.setOnClickListener {
            try {
                startActivity(
                    ICALParser(
                        getFileInputStream("sample.ics")
                    ).buildIntent()
                )
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't parse", e)
            }

        }
    }

    private fun getFileInputStream(fileName: String): InputStream {
        return this.assets.open(fileName)
    }

    //http://stackoverflow.com/a/23750660
    private fun getFilePathFromUri(c: Context, uri: Uri): String? {
        var filePath: String? = null
        if ("content" == uri.scheme) {
            val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
            val contentResolver: ContentResolver = c.contentResolver
            val cursor: Cursor? = contentResolver.query(uri, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0]) ?: 0
            filePath = cursor?.getString(columnIndex)
            cursor?.close()
        } else if ("file" == uri.scheme) {
            filePath = uri.path?.let { File(it).absolutePath }
        }
        return filePath
    }
}