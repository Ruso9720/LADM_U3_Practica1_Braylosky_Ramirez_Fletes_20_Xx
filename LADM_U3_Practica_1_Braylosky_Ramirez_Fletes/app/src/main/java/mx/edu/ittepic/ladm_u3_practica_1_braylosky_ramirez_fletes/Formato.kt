package mx.edu.ittepic.ladm_u3_practica_1_braylosky_ramirez_fletes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object Formato {
    fun getBytes(bitmap: Bitmap) : ByteArray {
        var stream= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
        return stream.toByteArray()
    }

    fun getImage(image : ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image,0,image.size)
    }
}
