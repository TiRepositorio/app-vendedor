package apolo.vendedores.com.configurar

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import apolo.vendedores.com.R
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import apolo.vendedores.com.utilidades.Sincronizacion
import apolo.vendedores.com.utilidades.TablasSincronizacion
import kotlinx.android.synthetic.main.activity_configurar_usuario.*
import java.lang.Exception

class ConfigurarUsuario : AppCompatActivity() {

    lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar_usuario)

        inicializarBotones()
    }

    @SuppressLint("ShowToast", "SetTextI18n")
    private fun inicializarBotones(){
        ibtnUsuarioBuscar.setOnClickListener {
            traerUsuario()
        }
        ibtnUsuarioServidor.setOnClickListener {
            borrarUsuario()
        }
        ibtnUsuarioSincronizar.setOnClickListener {
            Toast.makeText(this, "Sincronizar", Toast.LENGTH_SHORT )
            if (usuarioGuardado()){
                try {
                    MainActivity.bd!!.execSQL("UPDATE usuarios " +
                                                    "  SET NOMBRE = '" + etUsuNombre.text.toString().trim() + "'" +
                                                    ", VERSION = '" + etUsuVersion.text.toString().trim() + "' " +
                                                    ", COD_EMPRESA = '" + etCodEmpresa.text.toString().trim() + "' " +
                                                    "    WHERE LOGIN = '" + etUsuCodigo.text.toString().trim() + "' "  )
                } catch (e : Exception) {
                    e.message.toString()
                }
            } else {
                borrarDatos()
                FuncionesUtiles.usuario["COD_EMPRESA"] = etCodEmpresa.text.toString().trim()
                FuncionesUtiles.usuario["NOMBRE"] = etUsuNombre.text.toString().trim()
                FuncionesUtiles.usuario["LOGIN"] = etUsuCodigo.text.toString().trim()
                FuncionesUtiles.usuario["VERSION"] = etUsuVersion.text.toString().trim()
                FuncionesUtiles.usuario["TIPO"] = "U"
                FuncionesUtiles.usuario["ACTIVO"] = "S"
                FuncionesUtiles.usuario["CONF"] = "S"
                Sincronizacion.tipoSinc = "T"
                MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
                val menu2 = Intent(this, Sincronizacion::class.java)
                startActivity(menu2)
                finish()
            }
            etUsuarioMensaje.text = "Sincronizar"
        }
    }

    @SuppressLint("Recycle")
    private fun traerUsuario(){
        cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
        if (cursor.count>0){
            cursor.moveToLast()
            etCodEmpresa.setText(cursor.getString(cursor.getColumnIndex("COD_EMPRESA")))
            etUsuCodigo.setText(cursor.getString(cursor.getColumnIndex("LOGIN")))
            etUsuNombre.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etUsuVersion.setText(cursor.getString(cursor.getColumnIndex("VERSION")))
        } else {
            Toast.makeText(this,"No ha configurado un usuario.",Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Recycle")
    private fun usuarioGuardado():Boolean{
        return try {
            cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
            cursor.moveToLast()
            cursor.count > 0 && cursor.getString(cursor.getColumnIndex("LOGIN")) == etUsuCodigo.text.toString().trim() && cursor.getString(cursor.getColumnIndex("COD_EMPRESA")) == etCodEmpresa.text.toString().trim()
        } catch (e : Exception) {
            e.message
            false
        }
    }

    private fun borrarUsuario(){
        MainActivity.bd!!.execSQL("delete from usuarios")
    }

    private fun borrarDatos(){
        borrarUsuario()
        val noSinc = SentenciasSQL.listaSQLCreateTable()
        for (i in 0 until noSinc.size){
            MainActivity2.funcion.ejecutarB("DELETE FROM " + noSinc[i].split(" ")[5],this)
        }
        val sinc = TablasSincronizacion()
        for (i in 0 until sinc.listaSQLCreateTables().size){
            MainActivity2.funcion.ejecutarB("DELETE FROM " + sinc.listaSQLCreateTables()[i].split(" ")[5],this)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity2::class.java))
        super.onBackPressed()
    }
}
