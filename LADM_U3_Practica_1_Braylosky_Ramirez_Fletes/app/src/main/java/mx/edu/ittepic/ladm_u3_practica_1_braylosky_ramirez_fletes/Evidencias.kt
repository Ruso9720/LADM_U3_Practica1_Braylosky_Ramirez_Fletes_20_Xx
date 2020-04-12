package mx.edu.ittepic.ladm_u3_practica_1_braylosky_ramirez_fletes

import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI
import java.sql.Blob

class Evidencias(idAct:String, fot : ByteArray?){
    var foto = fot
    var idActividad = idAct
    var idEvidencia = 0
    var error = -1

    val nombreBaseDatos = "practica7"
    var puntero: Context? = null

    fun asignarPuntero(p: Context) {
        puntero = p
    }

    fun insertarImagen(): Boolean {
        try {
            var base = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var insertar = base.writableDatabase
            var datos = ContentValues()
            datos.put("IDACTIVIDAD", idActividad)
            datos.put("FOTO", foto)
            var respuesta = insertar.insert("EVIDENCIAS", "IDEVIDENCIA", datos)

            if (respuesta.toInt() == -1) {
                error = 2
                return false
            }

            insertar.close()
            base.close()
        } catch (e: SQLiteException) {
            //Error en la conexion
            error = 1
            return false
        }
        return true
    }

    fun buscarImagen(id : String) : ArrayList<ByteArray> {
        var arreglo = ArrayList<ByteArray>()
        try {
            var baseDatos = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM EVIDENCIAS WHERE IDACTIVIDAD = ?"
            var parametros = arrayOf(id)
            var cursor = select.rawQuery(SQL, parametros)
            if (cursor.moveToFirst()){
                do{
                    arreglo.add(cursor.getBlob(cursor.getColumnIndex("FOTO")))
                } while (cursor.moveToNext())
            }
            baseDatos.close()
            select.close()
        } catch (error : SQLiteException){

        }
        return arreglo
    }
}


