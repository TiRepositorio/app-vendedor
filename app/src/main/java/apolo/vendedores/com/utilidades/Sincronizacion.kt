package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
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
        lateinit var context: Context
        var primeraVez = false
        var contador = 0
        var nf = DecimalFormat("000")
    }

    var funcion : FuncionesUtiles = FuncionesUtiles(this)
//    lateinit var enviarMarcacion : EnviarMarcacion

    @Suppress("ClassName")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sincronizacion)
        context = this
//        EnviarMarcacion.context = context
//        enviarMarcacion = EnviarMarcacion()
        imeiBD = ""
        if (FuncionesUtiles.usuario["CONF"].equals("N")){
            btFinalizar.visibility = View.VISIBLE
            return
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
            imeiBD = MainActivity.conexionWS.procesaVersion(FuncionesUtiles.usuario["LOGIN"].toString())
            if (imeiBD.indexOf("Unable to resolve host") > -1 || imeiBD.indexOf("timeout") > -1) {
                progressDialog.dismiss()
                runOnUiThread {
                    Toast.makeText(context,"Verifique su conexion a internet y vuelva a intentarlo",Toast.LENGTH_SHORT).show()
                }
                finish()
                return null
            }

//            enviarMarcacion.procesaEnviaMarcaciones()

            insertarUsuario()
            if (imeiBD.length>3 && imeiBD.isNotEmpty()){
                if (!validaVersion(imeiBD.split("-")[0],imeiBD.split("-")[1],imeiBD.split("-")[2])){
                    return null
                }
            } else {
                if (imeiBD.isEmpty()){
                    tvImei.text = "Ocurrio un error"
                    return null
                } else {
                    if (!validaVersion(imeiBD)){
                        return null
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= 26){
                progressDialog.setMessage("Generando Archivos")
            }
            if (tipoSinc == "T"){
//                MainActivity.funcion.ejecutar("update svm_vendedor_pedido set ULTIMA_VEZ = '" + MainActivity.funcion.getFechaActual() + "'",this@Sincronizacion)
                if(!MainActivity.conexionWS.generaArchivos()){
                    runOnUiThread {
                        imeiBD += "\n\nError al generar archivos"
                        tvImei.text = "\n\nError al generar archivos"
                        Toast.makeText(this@Sincronizacion, "Error al generar archivos", Toast.LENGTH_SHORT).show()
                    }
                }
                if (Build.VERSION.SDK_INT >= 26){
                    progressDialog.setMessage("Obteniendo Archivos")
                }
                if(!MainActivity.conexionWS.obtenerArchivos()){
                    runOnUiThread {
                        if (tvImei.text.toString().indexOf("Espere")>-1){
                            imeiBD += "\n\nError al obtener archivos"
                            tvImei.text = "\n\nError al obtener archivos"
                            Toast.makeText(this@Sincronizacion, "Error al obtener archivos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            runOnUiThread {
                if (tvImei.text.toString().indexOf("Espere")<0){
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

    private fun borrarTablasTodo(listaTablas: Vector<String>){
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
        val th = Thread(Runnable {
            runOnUiThread {
                tvImei.text = tvImei.text.toString() + "\n\nSincronizando"
            }
            borrarTablasTodo(MainActivity.tablasSincronizacion.listaSQLCreateTables())
            obtenerArchivosTodo(MainActivity.tablasSincronizacion.listaSQLCreateTables(),
                MainActivity.tablasSincronizacion.listaCamposSincronizacion())
        })
        th.start()
    }

    @SuppressLint("SetTextI18n", "SdCardPath")
    private fun obtenerArchivosTodo(listaSQLCreateTable: Vector<String>, listaCampos: Vector<Vector<String>>):Boolean{
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
                    while (linea != null){
                        val valores : ArrayList<String> = linea.split("|") as ArrayList<String>
                        val values = ContentValues()
                        for (j in 0 until listaCampos[i].size){
                            if (valores[j] == "null" || valores[j] == "" || valores[j].isEmpty()){
                                values.put(listaCampos[i][j], " ")
                            } else {
                                values.put(listaCampos[i][j], valores[j])
                            }

                        }

                        //inserta valores en tablas especificas
                        if (listaSQLCreateTable[i].split(" ")[5] == "svm_vendedor_pedido") {
                            values.put("ULTIMA_SINCRO",values.get("FECHA").toString())
                        }

                        try {
                            MainActivity.bd!!.insert(listaSQLCreateTable[i].split(" ")[5],null,values)
                        } catch (e: Exception) {
                            return false
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
                MainActivity.bd!!.endTransaction()
        }
        runOnUiThread {
            pbProgresoTotal.progress = 100
            btFinalizar.visibility = View.VISIBLE
        }
        if (tipoSinc == "T"){
//            cargarSvm_vendedor_pedido_venta()
        }
        return true
    }

    override fun onBackPressed() {
        return
    }

    fun cerrar(view: View) {
        if (primeraVez){
            startActivity(Intent(this,MainActivity2::class.java))
            primeraVez = false
        }

        finish()
    }

    fun insertarUsuario(){
        try {
            MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
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
