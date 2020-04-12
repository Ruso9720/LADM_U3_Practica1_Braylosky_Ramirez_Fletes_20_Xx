package mx.edu.ittepic.ladm_u3_practica_1_braylosky_ramirez_fletes

import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_ventana2.*
import java.lang.Exception

class Ventana2 : AppCompatActivity() {
    var nombreBD = "practica7"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana2)

        var extras = intent.extras
        var idEliminar = extras?.getString("id").toString()

        cargarRegistro(idEliminar)

        eliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("ELIMINAR")
                .setMessage("¿Desea eliminar la actividad?")
                .setPositiveButton("SI"){d,i->
                    eliminarRegistro(idEliminar)
                    var otroActivity = Intent(this, MainActivity :: class.java)
                    startActivity(otroActivity)
                }
                .setNegativeButton("NO", DialogInterface.OnClickListener {
                        dialog, id ->
                })
                .show()
        }

        atras.setOnClickListener {
            finish()
        }
    }

    fun cargarRegistro(id : String) {
        try {
            var baseDatos = BaseDatos(this, nombreBD, null, 1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM ACTIVIDADES WHERE IDACTIVIDAD = ?"
            var parametros = arrayOf(id)
            var cursor = select.rawQuery(SQL, parametros)

            if(cursor.moveToFirst()){
                //SI HAY RESULTADO
                ID.setText("ID:" + cursor.getString(0))
                eDescripcion.setText("Descripcion: " + cursor.getString(1))
                eFC.setText("Fecha Captura: " + cursor.getString(2))
                eFE.setText("Fecha Entrega: " + cursor.getString(3))
            } else {
                //NO HAY RESULTADO
                mensaje("NO SE ENCONTRO COINCIDENCIA")
            }
            select.close()
            baseDatos.close()

            recuperarImg(id)
        } catch (error : SQLiteException){
            mensaje(error.message.toString())
        }
    }

    fun recuperarImg(id : String) {
        var nulo : ByteArray? = null
        var evidencia = Evidencias("",nulo)
        evidencia!!.asignarPuntero(this)
        var imagenes = evidencia.buscarImagen(id)
        var img = ArrayList<ImageView>()

        g.setInAnimation(this, android.R.anim.slide_in_left)
        g.setInAnimation(this, android.R.anim.slide_out_right)

        try {
            (0..imagenes.size-1).forEach {
                var imgNew = ImageView(this)
                val bitmap = Formato.getImage(imagenes[it])
                imgNew.setImageBitmap(bitmap)
                g.addView(imgNew)
            }
        } catch (error : Exception){
        }
    }

    fun eliminarRegistro(id : String) {
        try {
            var base = BaseDatos(this, nombreBD, null, 1)
            var eliminar = base.writableDatabase
            var idEliminar = arrayOf(id.toString())
            var respuesta = eliminar.delete("EVIDENCIAS", "IDACTIVIDAD = ?", idEliminar)

            if(respuesta.toInt() == 0) {
                mensaje("NO SE HA ELIMINADO")
            }

            var respuesta2 = eliminar.delete("ACTIVIDADES", "IDACTIVIDAD = ?", idEliminar)

            if(respuesta2.toInt() == 0) {
                mensaje("NO SE HA ELIMINADO")
            }

            eliminar.close()
            base.close()
        } catch (e : SQLiteException) {
            mensaje(e.message.toString())
        }
    }

    fun mensaje(s : String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK") {d , i -> }
            .show()
    }
}
