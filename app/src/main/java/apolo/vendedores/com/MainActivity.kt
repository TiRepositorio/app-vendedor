package apolo.vendedores.com

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import apolo.vendedores.com.utilidades.*
import kotlinx.android.synthetic.main.activity_configurar_usuario.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        var utilidadesBD: UtilidadesBD? = null
        var bd: SQLiteDatabase? = null
        val conexionWS: ConexionWS = ConexionWS()
        var codPersona : String = ""
        val tablasSincronizacion: TablasSincronizacion = TablasSincronizacion()
        lateinit var funcion : FuncionesUtiles
        val version : String = "60"
        var nombre : String = ""
        lateinit var etAccion : EditText
        var rooteado : Boolean = false
        lateinit var dispositivo : FuncionesDispositivo
        lateinit var telMgr : TelephonyManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializarElementos()
    }

    private fun inicializarElementos(){
        funcion = FuncionesUtiles(this)
        utilidadesBD = UtilidadesBD(this, null)
        bd = utilidadesBD!!.writableDatabase
        crearTablas()
        cargarUsuarioInicial()
        btComenzar.setOnClickListener{comenzar()}
        etAccion()
        dispositivo = FuncionesDispositivo(this)
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        mostrarImei()
    }

    private fun comenzar(){
        if (etCodigoUsuario.text.isEmpty() || etNombreUsuario.text.isEmpty() || etVersionUsuario.text.isEmpty()){
            funcion.mensaje(this,"","Todos los campos son obligatorios")
            return
        }
        var dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAutorizacion("comenzar",accion)
    }

    private fun etAccion(){
        etAccion = accion
        accion.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "comenzar"){
                    iniciar()
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
            funcion.ejecutar(SentenciasSQL.createTableUsuarios(),this)
            cursor = funcion.consultar("SELECT * FROM usuarios")
        } catch(e:Exception){
            var error = e.message
            return false
        }

        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            etNombreUsuario.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etCodigoUsuario.setText(cursor.getString(cursor.getColumnIndex("LOGIN")))
            FuncionesUtiles.usuario.put("NOMBRE", cursor.getString(cursor.getColumnIndex("NOMBRE")))
            FuncionesUtiles.usuario.put("LOGIN", cursor.getString(cursor.getColumnIndex("LOGIN")))
            FuncionesUtiles.usuario.put("TIPO", cursor.getString(cursor.getColumnIndex("TIPO")))
            FuncionesUtiles.usuario.put("ACTIVO", cursor.getString(cursor.getColumnIndex("ACTIVO")))
            FuncionesUtiles.usuario.put("COD_EMPRESA", cursor.getString(cursor.getColumnIndex("COD_EMPRESA")))
            FuncionesUtiles.usuario.put("VERSION", cursor.getString(cursor.getColumnIndex("VERSION")))
            FuncionesUtiles.usuario.put("COD_PERSONA", " ")
            FuncionesUtiles.usuario.put("CONF","S")
            startActivity(Intent(this,MainActivity2::class.java))
            finish()
            return true
        } else {
            FuncionesUtiles.usuario.put("CONF","N")
            return false
        }
    }

    fun iniciar(){
        if (usuarioGuardado()){
            try {
                funcion.ejecutar("UPDATE usuarios SET NOMBRE = '" + etUsuNombre.text.toString().trim() + "'" +
                        ", VERSION = '" + etUsuVersion.text.toString().trim() + "' " +
                        "    WHERE LOGIN = '" + etUsuCodigo.text.toString().trim() + "' "  ,this)
            } catch (e : java.lang.Exception) {
                var error = e.message
                error = error + ""
            }
        } else {
            FuncionesUtiles.usuario.put("NOMBRE",etNombreUsuario.text.toString().trim())
            FuncionesUtiles.usuario.put("LOGIN",etCodigoUsuario.text.toString().trim())
            FuncionesUtiles.usuario.put("VERSION",etVersionUsuario.text.toString().trim())
            FuncionesUtiles.usuario.put("TIPO","U")
            FuncionesUtiles.usuario.put("ACTIVO","S")
            FuncionesUtiles.usuario.put("COD_EMPRESA","1")
            FuncionesUtiles.usuario.put("CONF","S")
            Sincronizacion.tipoSinc = "T"
            Sincronizacion.primeraVez = true
            var menu2 = Intent(this, Sincronizacion::class.java)
            startActivity(menu2)
            finish()
        }
    }

    fun usuarioGuardado():Boolean{
        try {
            var cursor = funcion.consultar("SELECT * FROM usuarios")
            cursor.moveToLast()
            if (cursor.count > 0 && cursor.getString(cursor.getColumnIndex("LOGIN")).equals(etUsuCodigo.text.toString().trim())){
                return true
            } else {
                return false
            }
        } catch (e : java.lang.Exception) {
            var error = e.message
            return false
        }
    }

    fun mostrarImei(){
        funcion.mensaje(this,"IMEI", dispositivo.imei(telMgr))
    }

}