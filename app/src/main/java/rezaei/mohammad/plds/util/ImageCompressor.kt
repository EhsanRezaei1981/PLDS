package rezaei.mohammad.plds.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import rezaei.mohammad.plds.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File

object ImageCompressor {
    fun compressImage(imageFile: File, maxImageSize: Long): ByteArray {
        if (maxImageSize >= 10000 && imageFile.length() > maxImageSize) {
            return runBlocking(Dispatchers.Default) {
                var streamLength = maxImageSize
                var compressQuality = 105
                val bmpStream = ByteArrayOutputStream()
                while (streamLength >= maxImageSize && compressQuality > 5) {
                    bmpStream.use {
                        it.flush()
                        it.reset()
                    }

                    compressQuality -= 5
                    val bitmap =
                        BitmapFactory.decodeFile(imageFile.absolutePath, BitmapFactory.Options())
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                    val bmpPicByteArray = bmpStream.toByteArray()
                    streamLength = bmpPicByteArray.size.toLong()
                    if (BuildConfig.DEBUG) {
                        Log.d("test upload", "Quality: $compressQuality")
                        Log.d("test upload", "Size: $streamLength")
                    }
                }

                return@runBlocking bmpStream.toByteArray()
            }
        } else
            return imageFile.readBytes()
    }
}