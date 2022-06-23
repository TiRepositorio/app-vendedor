package apolo.vendedores.com.configurar

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getStringOrNull
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.clases.Usuario
import apolo.vendedores.com.reportes.AvanceDeComisiones
import apolo.vendedores.com.utilidades.*
import apolo.vendedores.com.ventas.Pedidos
import kotlinx.android.synthetic.main.activity_configurar_usuario.*
import kotlinx.android.synthetic.main.activity_configurar_usuario_2.*
import kotlinx.android.synthetic.main.activity_pedidos.*
import java.lang.Exception

class ConfigurarUsuarioNuevo : AppCompatActivity() {

    lateinit var cursor: Cursor


    override fun onResume() {
        lvUsuarios.invalidateViews()
        super.onResume()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar_usuario_2)

        inicializarBotones()
        traerUsuario()


    }

    private fun inicializarBotones() {

        btnAgregar.setOnClickListener {
            agregarItem()
        }


        btnBorrarTodo.setOnClickListener {
            borrarUsuarios()
        }

        btnGuardarTodo.setOnClickListener {
            guardarTodo()
        }

        btnGuardarSincronizarTodo.setOnClickListener {
            guardarSincronizarTodo()
        }

    }

    private fun guardarTodo() {

        borrarUsuarioBD()

        MainActivity2.listaUsuarios.forEach {

            it.tipo = "U"
            it.activo = "S"
            it.prog_pedido = "0"

            MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario2(it))

        }

        traerUsuario()


    }

    private fun guardarSincronizarTodo() {

        guardarTodo()

        if (MainActivity2.listaUsuarios.size > 0) {

            FuncionesUtiles.usuario["CONF"] = "S"

            //finish()

            //SINCRONIZAR
            Sincronizacion.tipoSinc = "T"
            val menu2 = Intent(this, Sincronizacion::class.java)
            startActivity(menu2)
            finish()

        } else {

            Toast.makeText(this, "Debe configurar al menos un usuario!", Toast.LENGTH_LONG).show()

        }




    }


    private fun borrarUsuarioBD() {
        MainActivity.bd!!.execSQL("delete from usuarios")
    }

    private fun borrarUsuarios() {

        borrarUsuarioBD()
        MainActivity2.listaUsuarios.clear()
        lvUsuarios.invalidateViews()

    }


    private fun agregarItem() {

        //VALIDAR QUE NO SE CREEN DEMASIADOS USUARIOS
        if (MainActivity2.listaUsuarios.size >= MainActivity2.limiteCantidadUsuario) {

            Toast.makeText(this, "Solo de pueden configurar hasta "
                    + MainActivity2.limiteCantidadUsuario
                    + " usuarios", Toast.LENGTH_LONG).show()

        } else {

            MainActivity2.listaUsuarios.add(Usuario())
            lvUsuarios.invalidateViews()

        }

    }


    private fun traerUsuario(){
        cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)

        cursor.moveToFirst()

        MainActivity2.listaUsuarios.clear()

        for (i in 0 until cursor.count){

            val usuario : Usuario = Usuario()

            usuario.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")))
            usuario.nombre = cursor.getStringOrNull(cursor.getColumnIndex("NOMBRE")).toString()
            usuario.login = cursor.getStringOrNull(cursor.getColumnIndex("LOGIN")).toString()
            usuario.tipo = cursor.getStringOrNull(cursor.getColumnIndex("TIPO")).toString()
            usuario.activo = cursor.getStringOrNull(cursor.getColumnIndex("ACTIVO")).toString()
            usuario.cod_empresa = cursor.getStringOrNull(cursor.getColumnIndex("COD_EMPRESA")).toString()
            usuario.version = cursor.getStringOrNull(cursor.getColumnIndex("VERSION")).toString()
            usuario.min_fotos = cursor.getStringOrNull(cursor.getColumnIndex("MIN_FOTOS")).toString()
            usuario.max_fotos = cursor.getStringOrNull(cursor.getColumnIndex("MAX_FOTOS")).toString()
            usuario.cod_persona = cursor.getStringOrNull(cursor.getColumnIndex("COD_PERSONA")).toString()
            usuario.prog_pedido = cursor.getStringOrNull(cursor.getColumnIndex("PROG_PEDIDO")).toString()

            MainActivity2.listaUsuarios.add(usuario)

            cursor.moveToNext()
        }

        val adapter: Adapter.ConfigurarUsuarioNuevo =
            Adapter.ConfigurarUsuarioNuevo(
                this,
                MainActivity2.listaUsuarios,
                lvUsuarios
            )
        lvUsuarios.adapter = adapter

    }


    override fun onBackPressed() {

        if (MainActivity2.listaUsuarios.size > 0) {
            startActivity(Intent(this,MainActivity2::class.java))
        } else {
            finish()
        }


        super.onBackPressed()
    }


}