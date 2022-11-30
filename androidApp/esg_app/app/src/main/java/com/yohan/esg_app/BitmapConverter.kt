package com.yohan.esg_app

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.util.*

class BitmapConverter {
    //bitmap->string
    @RequiresApi(Build.VERSION_CODES.O)
    fun bitmapToString(bitmap: Bitmap): String {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)

        val bytes = stream.toByteArray()

        return Base64.getEncoder().encodeToString(bytes)
    }
}