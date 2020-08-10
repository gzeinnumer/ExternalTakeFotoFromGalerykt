package com.gzeinnumer.externaltakefotofromgalerykt

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gzeinnumer.externaltakefotofromgalerykt.helper.FunctionGlobalDir
import com.gzeinnumer.externaltakefotofromgalerykt.helper.imagePicker.FileCompressor
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity_"

    private val REQUEST_GALLERY_PHOTO = 2
    lateinit var mPhotoFile: File
    private lateinit var mCompressor: FileCompressor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = TAG

        mCompressor = FileCompressor(this)
        mCompressor.setDestinationDirectoryPath(FunctionGlobalDir.getStorageCard + FunctionGlobalDir.appFolder)

        btn_galery.setOnClickListener { dispatchGalleryIntent() }
    }

    private fun dispatchGalleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_PHOTO) {
                data?.let {d->
                    val selectedImage = d.data
                    try {
                        selectedImage?.let {
                            mPhotoFile = mCompressor.compressToFile(File(getRealPathFromUri(it)))
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    Glide.with(this@MainActivity).load(mPhotoFile).into(img)
                }
            }
        }
    }

    private fun getRealPathFromUri(contentUri: Uri): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }
}