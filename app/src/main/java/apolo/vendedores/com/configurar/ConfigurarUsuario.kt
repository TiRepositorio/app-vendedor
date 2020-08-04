package apolo.vendedores.com.configurar

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import apolo.vendedores.com.R
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.Sincronizacion
import kotlinx.android.synthetic.main.activity_configurar_usuario.*
import java.lang.Exception

class ConfigurarUsuario : AppCompatActivity() {

    lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar_usuario)

        inicializarBotones()
    }

    fun inicializarBotones(){
        ibtnUsuarioBuscar.setOnClickListener(View.OnClickListener {
            traerUsuario()
//            Toast.makeText(this, "Buscar y traer datos acutales", Toast.LENGTH_SHORT )
//            etUsuarioMensaje.setText("Buscar y traer datos acutales")
        })
        ibtnUsuarioServidor.setOnClickListener(View.OnClickListener {
            borrarUsuario()
//            Toast.makeText(this, "Borrar datos de usuario de la BD", Toast.LENGTH_SHORT )
//            etUsuarioMensaje.setText("Borrar datos de usuario de la BD")
        })
        ibtnUsuarioSincronizar.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Sincronizar", Toast.LENGTH_SHORT )
            if (usuarioGuardado()){
                try {
                    MainActivity.bd!!.execSQL("UPDATE usuarios SET NOMBRE = '" + etUsuNombre.text.toString().trim() + "'" +
                            ", VERSION = '" + etUsuVersion.text.toString().trim() + "' " +
                            "    WHERE LOGIN = '" + etUsuCodigo.text.toString().trim() + "' "  )
                } catch (e : Exception) {
                    var error = e.message
                    error = error + ""
                }
            } else {
                FuncionesUtiles.usuario.put("NOMBRE",etUsuNombre.text.toString().trim())
                FuncionesUtiles.usuario.put("LOGIN",etUsuCodigo.text.toString().trim())
                FuncionesUtiles.usuario.put("VERSION",etUsuVersion.text.toString().trim())
                FuncionesUtiles.usuario.put("TIPO","U")
                FuncionesUtiles.usuario.put("ACTIVO","S")
                FuncionesUtiles.usuario.put("COD_EMPRESA","1")
                FuncionesUtiles.usuario.put("CONF","S")
                Sincronizacion.tipoSinc = "T"
//                MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
                var menu2 = Intent(this, Sincronizacion::class.java)
                startActivity(menu2)
                finish()
            }
            etUsuarioMensaje.setText("Sincronizar")
        })
    }

    fun traerUsuario(){
        cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
        if (cursor.count>0){
            cursor.moveToLast()
            etUsuCodigo.setText(cursor.getString(cursor.getColumnIndex("LOGIN")))
            etUsuNombre.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etUsuVersion.setText(cursor.getString(cursor.getColumnIndex("VERSION")))
        } else {
            Toast.makeText(this,"No ha configurado un usuario.",Toast.LENGTH_SHORT).show()
        }
    }

    fun usuarioGuardado():Boolean{
        try {
            cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
            cursor.moveToLast()
            if (cursor.count > 0 && cursor.getString(cursor.getColumnIndex("LOGIN")).equals(etUsuCodigo.text.toString().trim())){
                return true
            } else {
                return false
            }
        } catch (e : Exception) {
            var error = e.message
            return false
        }
    }

    fun borrarUsuario(){
        MainActivity.bd!!.execSQL("delete from usuarios")
    }
}
