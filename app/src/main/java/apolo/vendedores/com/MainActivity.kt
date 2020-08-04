package apolo.vendedores.com

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import apolo.vendedores.com.configurar.ConfigurarUsuario
import apolo.vendedores.com.utilidades.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        var utilidadesBD: UtilidadesBD? = null
        var bd: SQLiteDatabase? = null
        val conexionWS: ConexionWS = ConexionWS()
        var codPersona : String = ""
        val tablasSincronizacion: TablasSincronizacion = TablasSincronizacion()
        lateinit var funcion : FuncionesUtiles
        val version : String = "1"
        var nombre : String = ""
        lateinit var etAccion : EditText
//        lateinit var dispositivo : FuncionesDispositivo
        var rooteado : Boolean = false
//        lateinit var dispositivo : FuncionesDispositivo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializarElementos()
    }

    private fun inicializarElementos(){
        funcion = FuncionesUtiles(this)
        utilidadesBD = UtilidadesBD(this, null)
//        crearTablas()
        cargarUsuarioInicial()
        btComenzar.setOnClickListener{comenzar()}
        etAccion()
    }

    private fun comenzar(){
        var dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAutorizacion("comenzar",accion)
    }

    private fun etAccion(){
        etAccion = accion
        accion.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "comenzar"){
                    startActivity(Intent(this@MainActivity,ConfigurarUsuario::class.java))
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun crearTablas(){
        for (i in 0 until SentenciasSQL.listaSQLCreateTable().size){
            funcion.ejecutar(SentenciasSQL.listaSQLCreateTable().get(i),this)
        }
    }

    fun cargarUsuarioInicial():Boolean{
        lateinit var cursor : Cursor

        try {
            bd!!.execSQL(SentenciasSQL.createTableUsuarios())
            cursor = bd!!.rawQuery("SELECT * FROM usuarios", null)
        } catch(e:Exception){
            var error = e.message
            return false
        }

        if (cursor.moveToFirst()) {
            cursor.moveToLast()
//            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvNombreSup).text = cursor.getString(cursor.getColumnIndex("NOMBRE"))
//            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoSup).text = cursor.getString(cursor.getColumnIndex("PASS"))
            etNombreUsuario.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etCodigoUsuario.setText(cursor.getString(cursor.getColumnIndex("PASS")))
            FuncionesUtiles.usuario.put("NOMBRE", cursor.getString(cursor.getColumnIndex("NOMBRE")))
            FuncionesUtiles.usuario.put("PASS", cursor.getString(cursor.getColumnIndex("PASS")))
            FuncionesUtiles.usuario.put("TIPO", cursor.getString(cursor.getColumnIndex("TIPO")))
            FuncionesUtiles.usuario.put("ACTIVO", cursor.getString(cursor.getColumnIndex("ACTIVO")))
            FuncionesUtiles.usuario.put("COD_EMPRESA", cursor.getString(cursor.getColumnIndex("COD_EMPRESA")))
            FuncionesUtiles.usuario.put("VERSION", cursor.getString(cursor.getColumnIndex("VERSION")))
            FuncionesUtiles.usuario.put("COD_PERSONA", cursor.getString(cursor.getColumnIndex("COD_PERSONA")))
            FuncionesUtiles.usuario.put("CONF","S")
            return true
        } else {
            FuncionesUtiles.usuario.put("CONF","N")
            if (etNombreUsuario.text.toString().length == 0) {
                etNombreUsuario.setText("Ingrese el nombre del supervisor")
                etCodigoUsuario.setText("12345")
            }
            return false
        }
    }

}