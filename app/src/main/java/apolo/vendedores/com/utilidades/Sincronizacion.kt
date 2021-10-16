@file:Suppress("DEPRECATION")

package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.ventas.asistencia.EnviarMarcacion
import apolo.vendedores.com.ventas.justificacion.NoVenta
import kotlinx.android.synthetic.main.activity_sincronizacion.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.DecimalFormat
import java.util.*

@Suppress("DEPRECATION", "ClassName")
class Sincronizacion : AppCompatActivity() {

    lateinit var imeiBD: String

    companion object{
        var tipoSinc: String = "T"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var primeraVez = false
        var nf = DecimalFormat("000")
    }

    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    lateinit var enviarMarcacion : EnviarMarcacion
    lateinit var enviarNoventa   : NoVenta
    @Suppress("ClassName")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sincronizacion)
        context = this
        EnviarMarcacion.contexto = context
        NoVenta.context = context
        enviarMarcacion = EnviarMarcacion("","")
        enviarNoventa = NoVenta("","",null,null,"","")
        imeiBD = ""
        if (FuncionesUtiles.usuario["CONF"].equals("N")){
            btFinalizar.visibility = View.VISIBLE
            return
        }

        if (funcion.tiempoTranscurrido(funcion.fechaUltimaSincro(),funcion.getFechaHoraActual()) < 15){
            funcion.toast(this,"Debe esperar 15 minutos para sincronizar.")
//            finish()
        }
        try {
            preparaSincornizacion().execute()
        } catch(e: Exception){
            Log.println(Log.WARN, "Error",e.message!!)
        }
    }

    @Suppress("ClassName")
    @SuppressLint("StaticFieldLeak")
    private inner class preparaSincornizacion: AsyncTask<Void, Void, Void>(){
        lateinit var progressDialog: ProgressDialog
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Consultando disponibilidad")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        @SuppressLint("WrongThread", "SetTextI18n")
        override fun doInBackground(vararg p0: Void?): Void? {
            imeiBD = MainActivity.conexionWS.procesaVersion()
            if (imeiBD.indexOf("Unable to resolve host") > -1 || imeiBD.indexOf("timeout") > -1) {
                progressDialog.dismiss()
                runOnUiThread {
                    Toast.makeText(context,"Verifique su conexion a internet y vuelva a intentarlo",Toast.LENGTH_SHORT).show()
                }
                finish()
                return null
            }

            enviarMarcacion.procesaEnviaMarcaciones()
            enviarNoventa.enviarPendientesDiaAnterior()

            insertarUsuario()
            if (imeiBD.isEmpty()){
                return null
            }
            if(imeiBD.isEmpty()){
                tvImei.text = "Configure correctamente la versión en el servidor."
                return null
            }
            if (imeiBD.length>3 && imeiBD.isNotEmpty()){
                if (imeiBD.split("-").size < 3){
                    tvImei.text = imeiBD
                    return null
                }
                funcion.ejecutar("update usuarios set PROG_PEDIDO = '${imeiBD.split("-")[3]}'",this@Sincronizacion)
                if (!validaVersion(imeiBD.split("-")[0],imeiBD.split("-")[1],imeiBD.split("-")[2])){
                    return null
                }
            } else {
                if (imeiBD.isEmpty()){
                    tvImei.text = "Ocurrio un error"
                    return null
                } else {
                    if (imeiBD.trim() == "X"){
                        return null
                    }
                    if (!validaVersion(imeiBD)){
                        return null
                    }
                    tvImei.text = "Configure correctamente la versión en el servidor."
                    return null
                }
            }
            if (Build.VERSION.SDK_INT >= 30){
                progressDialog.setMessage("Generando Archivos")
            }
            if (tipoSinc == "T"){
//                MainActivity.funcion.ejecutar("update svm_vendedor_pedido set ULTIMA_VEZ = '" + MainActivity.funcion.getFechaActual() + "'",this@Sincronizacion)
                if(!MainActivity.conexionWS.generaArchivos()){
                    runOnUiThread {
                        imeiBD += "\n\nError al generar archivos"
                        tvImei.text = "\n\nError al generar archivos"
                        tvImei.text = "\n\n${ConexionWS.resultados}"
                        Toast.makeText(this@Sincronizacion, "Error al generar archivos", Toast.LENGTH_SHORT).show()
                    }
                }
                if (Build.VERSION.SDK_INT >= 30){
                    progressDialog.setMessage("Obteniendo Archivos")
                }
                if(!MainActivity.conexionWS.obtenerArchivos()){
                    runOnUiThread {
                        if (tvImei.text.toString().indexOf("Espere")>-1){
                            imeiBD += "\n\nError al obtener archivos"
                            tvImei.text = "\n\nError al obtener archivos"
                            tvImei.text = "\n\n${ConexionWS.resultados}"
                            Toast.makeText(this@Sincronizacion, "Error al obtener archivos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            return null
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            runOnUiThread {
                if (tvImei.text.toString().indexOf("Espere")<0){
                    if (imeiBD.trim() == "X"){
                        runOnUiThread {
                            tvImei.text = tvImei.text.toString() + "\n\nConfigure correctamente las versiones en todas las carteras y vuelva a intentar."
                        }
                    }
                    btFinalizar.visibility = View.VISIBLE
                } else {
                    cargarRegistros()
                }
            }
        }
    }

    fun cargarRegistros(){
        if (tipoSinc == "T"){
            sincronizarTodo()
        }
    }

    private fun borrarTablasTodo(listaTablas: ArrayList<String>){
        for (i in 0 until listaTablas.size){
            val sql: String = "DROP TABLE IF EXISTS " + listaTablas[i].split(" ")[5]
            try {
                MainActivity.bd!!.execSQL(sql)
            } catch (e : Exception) {
                return
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sincronizarTodo(){
        val th = Thread {
            runOnUiThread {
                tvImei.text = tvImei.text.toString() + "\n\nSincronizando"
            }
            borrarTablasTodo(MainActivity.tablasSincronizacion.listaSQLCreateTables())
            obtenerArchivosMasivo(
                MainActivity.tablasSincronizacion.listaSQLCreateTables(),
                MainActivity.tablasSincronizacion.listaCamposSincronizacion(),
                MainActivity.tablasSincronizacion.listaSQLCreateIndexes()
            )
        }
        th.start()
    }

    @SuppressLint("SetTextI18n", "SdCardPath")
    private fun obtenerArchivosMasivo(listaSQLCreateTable: ArrayList<String>, listaCampos: ArrayList<ArrayList<String>>,listaIndices: ArrayList<String>):Boolean{
        runOnUiThread {
            pbTabla.progress = 0
            pbProgresoTotal.progress = 0
        }
        for (i in 0 until listaSQLCreateTable.size){
            MainActivity.bd!!.beginTransaction()
            try {

                //Leer el archivo desde la direccion asignada
                var archivo     = File("/data/data/apolo.vendedores.com/" + listaSQLCreateTable[i].split(" ")[5] + ".txt")
                var leeArchivo  = FileReader(archivo)
                var buffer      = BufferedReader(leeArchivo)
                val sql         : String            = listaSQLCreateTable[i]

                try {
                    MainActivity.bd!!.execSQL(sql)
                } catch (e: Exception) {
                    runOnUiThread{
                        tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                    }
                    return false
                }

                //Obtiene cantidad de lineas
                var numeroLineas = 0
                var linea: String? = buffer.readLine()
                while (linea != null){
                    numeroLineas++
                    linea = buffer.readLine()
                }

                archivo     = File("/data/data/apolo.vendedores.com/" + listaSQLCreateTable[i].split(" ")[5] + ".txt")
                leeArchivo  = FileReader(archivo)
                buffer      = BufferedReader(leeArchivo)

                //Extrae valor de los campo e inserta a la BD
                linea = buffer.readLine()
                var cont = 0
                runOnUiThread {
                    tvImei.text = tvImei.text.toString() + "\n${nf.format(i)} - " + listaSQLCreateTable[i].split(" ")[5]
                }
                var sql2 = "insert into " + listaSQLCreateTable[i].split(" ")[5] + "("
                for (j in 0 until listaCampos[i].size){
                    sql2 += if (j == listaCampos[i].size-1){
                        listaCampos[i][j] + ")"
                    } else {
                        listaCampos[i][j] + ","
                    }
                }
                sql2 += " values "
                var contador = 0
                var ins = 0
                while (linea != null){
                    val valores : ArrayList<String> = linea.split("|") as ArrayList<String>
                    sql2 += if (ins > 0){
                        ",("
                    } else {
                        " ("
                    }
                    contador++
                    ins++
                    for (j in 0 until listaCampos[i].size){
                        sql2 += if (valores[j] == "null" || valores[j].isEmpty()) {
                            "' '"
                        } else {
                            "'${valores[j].replace("'","''")}'"
                        }
                        sql2 += if (j == listaCampos[i].size-1){
                            ")"
                        } else {
                            ","
                        }
                    }

                    if (ins == 50){
                            try {
                                MainActivity.bd!!.execSQL(sql2)
                            } catch (e: Exception) {
                                e.message
                                runOnUiThread{
                                    tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                                }
                                return false
                            }
                            sql2 = "insert into " + listaSQLCreateTable[i].split(" ")[5] + "("
                            for (j in 0 until listaCampos[i].size){
                                sql2 += if (j == listaCampos[i].size-1){
                                    listaCampos[i][j] + ")"
                                } else {
                                    listaCampos[i][j] + ","
                                }
                            }
                            sql2 += " values "
                        ins = 0
                    }

                    linea = buffer.readLine()
                    runOnUiThread {
                        cont++
                        var progreso : Double = (100/numeroLineas.toDouble())*(cont)
                        if (cont == numeroLineas){
                            progreso = 100.0
                        }
                        pbTabla.progress = progreso.toInt()
                    }
                }
                try {
                    if (contador>0 && ins > 0){
                        MainActivity.bd!!.execSQL(sql2)
                    }
                } catch (e: Exception) {
                    e.message
                    runOnUiThread{
                        tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                    }
                    return false
                }
            } catch (e: Exception) {
                runOnUiThread {
                    tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                }
                return false
            }
            runOnUiThread {
                pbProgresoTotal.progress = (100/listaSQLCreateTable.size)*(i+1)
            }
            MainActivity.bd!!.setTransactionSuccessful()
            MainActivity.bd!!.endTransaction()//inserta valores en tablas especificas
            if (listaSQLCreateTable[i].split(" ")[5] == "svm_vendedor_pedido") {
                try {
                    MainActivity.bd!!.execSQL("update svm_vendedor_pedido set ULTIMA_SINCRO = '${MainActivity2.funcion.getFechaHoraActual()}'")
                } catch (e:java.lang.Exception){
                    runOnUiThread{
                        tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                    }
                }
            }
        }
        runOnUiThread {
            for (i in 0 until listaIndices.size){
                try {
                    MainActivity2.bd!!.execSQL(listaIndices[i])
                } catch (e : java.lang.Exception){
                    if (e.message.toString().trim() != "" && e.message != null && e.message.toString().trim() != "null"){
                        tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                    }
                }
            }
            pbProgresoTotal.progress = 100
            btFinalizar.visibility = View.VISIBLE
        }
        return true
    }

    override fun onBackPressed() {
        return
    }

    @SuppressLint("SetTextI18n")
    fun cerrar(view: View) {
        view.id
        startActivity(Intent(this,MainActivity2::class.java))
        finish()
    }

    fun insertarUsuario(){
        try {
            val usuarios = SentenciasSQL.insertUsuario(FuncionesUtiles.usuario)
            MainActivity.bd!!.execSQL(usuarios)
        } catch (e : Exception) {
            return
        }
    }

    @SuppressLint("SetTextI18n")
    fun validaVersion(versionUsuario:String, versionSistema:String, versionEstado:String):Boolean{
        if (!FuncionesUtiles.usuario["VERSION"].equals(versionUsuario)){
            tvImei.text = "Version de usuario no corresponde.$versionUsuario"
            return false
        }
        if (versionSistema != MainActivity.version){
            tvImei.text = "Debe actualizar su version para sincronizar."
            return false
        }
        if (versionEstado == "I"){
            tvImei.text = "El usuario se encuentra inactivo."
            borrarTablasTodo(MainActivity.tablasSincronizacion.listaSQLCreateTables())
            borrarTablasTodo(SentenciasSQL.listaSQLCreateTable() )
            btFinalizar.visibility = View.VISIBLE
            return false
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    fun validaVersion(versionUsuario:String):Boolean{
        if (versionUsuario != FuncionesUtiles.usuario["VERSION"]){
            tvImei.text = "Debe actualizar su version para sincronizar."
            btFinalizar.visibility = View.VISIBLE
            return false
        }
        return true
    }

}
