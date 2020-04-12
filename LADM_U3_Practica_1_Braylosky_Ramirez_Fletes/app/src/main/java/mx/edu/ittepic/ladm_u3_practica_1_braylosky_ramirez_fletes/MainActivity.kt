package mx.edu.ittepic.ladm_u3_practica_1_braylosky_ramirez_fletes

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_ventana2.view.*

class MainActivity : AppCompatActivity() {
    var listaID = ArrayList<String>()
    var imagenes = ArrayList<ImageView>()
    var nombreBaseDatos = "practica7"

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cargarLista()

        camara.setOnClickListener() {
            abrirGaleria()
        }

        buscar.setOnClickListener() {
            cargarLista()
        }

        agregar.setOnClickListener() {
            insertarActividad()
            cargarLista()
        }
    }

    fun abrirGaleria() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 10)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null) {
            val pickedImage = data.data
            galeria.background = null
            galeria.setImageURI(pickedImage)
        }
    }

    fun cargarLista() {
        try {
            var baseDatos = BaseDatos(this, nombreBaseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM ACTIVIDADES"
            var cursor = select.rawQuery(SQL, null)
            listaID = ArrayList<String>()
            if (cursor.count > 0) {
                var arreglo = ArrayList<String>()
                this.listaID = ArrayList<String>()
                cursor.moveToFirst()
                var cantidad = cursor.count - 1

                (0..cantidad).forEach {
                    var data =
                        "ID: ${cursor.getString(0)}\nDescripcion: ${cursor.getString(1)}\nFecha captura: ${cursor.getString(
                            2
                        )}\nFecha entrega: ${cursor.getString(3)}"
                    arreglo.add(data)
                    listaID.add(cursor.getString(0))
                    cursor.moveToNext()
                }

                Lista.adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_expandable_list_item_1,
                    arreglo
                )
                Lista.setOnItemClickListener { parent, view, position, id ->
                    llamarOtroIntent(listaID[position])
                }
            }

            select.close()
            baseDatos.close()
        } catch (error: SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    fun llamarOtroIntent(id: String) {
        var otroActivity = Intent(this, Ventana2::class.java)
        otroActivity.putExtra("id", id)
        startActivity(otroActivity)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun insertarActividad() {
        try {
            var baseDatos = BaseDatos(this, nombreBaseDatos, null, 1)
            var insertar = baseDatos.writableDatabase
            var SQL =
                "INSERT INTO ACTIVIDADES VALUES(NULL, '${Cnombre.text.toString()}', '${CfeCap.text.toString()}', '${CfeEnt.text.toString()}')"

            insertar.execSQL(SQL)
            insertar.close()
            baseDatos.close()

            insertarEvidencia()
        } catch (error: SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun insertarEvidencia() {
        try {
            val bitmap = (galeria.drawable as BitmapDrawable).bitmap
            var evidencia = Evidencias(ultimoID(), Formato.getBytes(bitmap))
            evidencia.asignarPuntero(this)
            var resultado = evidencia.insertarImagen()
            if (resultado == true) {
                mensaje("SE GUARDÓ LA EVIDENCIA")
            } else {
                when (evidencia.error) {
                    1 -> {
                        mensaje("error en tabla, no se creó o no se conectó a la base de datos")
                    }
                    2 -> {
                        mensaje("error no se pudo insertar en la tabla")
                    }
                }
            }
            limpiarCampos()
        } catch (error: SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    fun ultimoID(): String {
        try {
            var baseDatos = BaseDatos(this, nombreBaseDatos, null, 1)
            var select = baseDatos.readableDatabase
            var columnas = arrayOf("IDACTIVIDAD")

            var cursor = select.query("ACTIVIDADES", columnas, null, null, null, null, null)

            if (cursor.moveToLast()) {
                return cursor.getString(0)
            }

            select.close()
            baseDatos.close()
        } catch (error: SQLiteException) {
            mensaje(error.message.toString())
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun limpiarCampos() {
        Cnombre.setText("")
        CfeCap.setText("")
        CfeEnt.setText("")
        imagenes = ArrayList<ImageView>()
        galeria.setImageURI(null)
        galeria.background = getDrawable(R.drawable.ic_photo_camera_black_24dp)
    }

    fun mensaje(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}
