package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.R
import kotlinx.android.synthetic.main.activity_sincronizacion.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

class Sincronizacion : AppCompatActivity() {

    lateinit var imeiBD: String

    companion object{
        var tipoSinc: String = "T"
        lateinit var context: Context
    }

    var funcion : FuncionesUtiles = FuncionesUtiles(this)
//    lateinit var enviarMarcacion : EnviarMarcacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sincronizacion)
        context = this
//        EnviarMarcacion.context = context
//        enviarMarcacion = EnviarMarcacion()
        imeiBD = ""
        if (FuncionesUtiles.usuario.get("CONF").equals("N")){
            btFinalizar.visibility = View.VISIBLE
            return
        }

        try {
            preparaSincornizacion().execute()
        } catch(e: Exception){
            Log.println(Log.WARN, "Error",e.message)
        }
    }

    private inner class preparaSincornizacion: AsyncTask<Void, Void, Void>(){
        lateinit var progressDialog: ProgressDialog
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Consultando disponibilidad")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg p0: Void?): Void? {
            imeiBD = MainActivity.conexionWS.procesaVersion(FuncionesUtiles.usuario.get("LOGIN").toString())
            if (imeiBD.indexOf("Unable to resolve host") > -1) {
                progressDialog.dismiss()
                runOnUiThread(Runnable {
                    Toast.makeText(context,"Verifique su conexion a internet y vuelva a intentarlo",Toast.LENGTH_SHORT).show()
                })
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
                    tvImei.setText("Ocurrio un error")
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
            if (tipoSinc.equals("T")){
//                MainActivity.funcion.ejecutar("update svm_vendedor_pedido set ULTIMA_VEZ = '" + MainActivity.funcion.getFechaActual() + "'",this@Sincronizacion)
                if(MainActivity.conexionWS.generaArchivos()){
                    imeiBD = imeiBD + "\n\nError al generar archivos"
                }
                if (Build.VERSION.SDK_INT >= 26){
                    progressDialog.setMessage("Obteniendo Archivos")
                }
                if(!MainActivity.conexionWS.obtenerArchivos()){
                    imeiBD = imeiBD + "\n\nError al obtener archivos"
                }
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            runOnUiThread(Runnable {
                if (tvImei.text.toString().indexOf("Espere")<0){
                    btFinalizar.visibility = View.VISIBLE
                } else {
                    cargarRegistros()
                }
            })
        }
    }

    fun cargarRegistros(){
        if (tipoSinc.equals("T")){
            sincronizarTodo()
        }
    }

    fun borrarTablasTodo(listaTablas: Vector<String>){
        for (i in 0 until listaTablas.size){
            var sql: String = "DROP TABLE IF EXISTS " + listaTablas[i].split(" ")[5]
            try {
                MainActivity.bd!!.execSQL(sql)
            } catch (e : Exception) {
                var error = e.message
                return
            }
        }
    }

    fun sincronizarTodo(){
        var th : Thread = Thread(Runnable {
            runOnUiThread(Runnable {
                tvImei.text = tvImei.text.toString() + "\n\nSincronizando"
            })
            borrarTablasTodo(MainActivity.tablasSincronizacion!!.listaSQLCreateTables())
            obtenerArchivosTodo(MainActivity.tablasSincronizacion.listaSQLCreateTables(),
                MainActivity.tablasSincronizacion.listaCamposSincronizacion())
        })
        th.start()
    }

    fun obtenerArchivosTodo(listaSQLCreateTable: Vector<String>, listaCampos: Vector<Vector<String>>):Boolean{
        runOnUiThread(Runnable {
            pbTabla.setProgress(0)
            pbProgresoTotal.setProgress(0)
        })
        for (i in 0 until listaSQLCreateTable.size){
                MainActivity.bd!!.beginTransaction()
                try {

                    //Leer el archivo desde la direccion asignada
                    var archivo     : File              = File("/data/data/apolo.promotor.com/" + listaSQLCreateTable[i].split(" ")[5] + ".txt")
                    var leeArchivo  : FileReader        = FileReader(archivo)
                    var buffer      : BufferedReader    = BufferedReader(leeArchivo)
                    var sql         : String            = listaSQLCreateTable[i]

                    try {
                        MainActivity.bd!!.execSQL(sql)
                    } catch (e: Exception) {
                        var error = e.message
                        return false
                    }

                    //Obtiene cantidad de lineas
                    var numeroLineas : Int = 0
                    var linea: String? = buffer.readLine()
                    while (linea != null){
                        numeroLineas++
                        linea = buffer.readLine()
                    }

                    archivo     = File("/data/data/apolo.promotor.com/" + listaSQLCreateTable[i].split(" ")[5] + ".txt")
                    leeArchivo  = FileReader(archivo)
                    buffer      = BufferedReader(leeArchivo)

                    //Extrae valor de los campo e inserta a la BD
                    linea = buffer.readLine()
                    var cont : Int = 0
                    while (linea != null){
                        runOnUiThread(Runnable {
                            tvImei.text = tvImei.text.toString() + "\n" + listaSQLCreateTable[i].split(" ")[5]
                        })
                        var valores : ArrayList<String> = linea.split("|") as ArrayList<String>
                        var values  : ContentValues  = ContentValues()
                        for (j in 0 until valores.size){
                            if (valores[j].toString().equals("null")||valores[j].toString().equals("")||valores[j].toString().isEmpty()){
                                values.put(listaCampos.get(i)[j], " ")
                            } else {
                                values.put(listaCampos.get(i)[j], valores[j])
                            }

                        }

                        //inserta valores en tablas especificas
                        if (listaSQLCreateTable[i].split(" ")[5].equals("svm_vendedor_pedido")) {
                            values.put("ULTIMA_SINCRO",values.get("FECHA").toString())
                        }

                        try {
                            MainActivity.bd!!.insert(listaSQLCreateTable[i].split(" ")[5],null,values)
                        } catch (e: Exception) {
                            var error = e.message
                            return false
                        }
                        linea = buffer.readLine()
                        runOnUiThread(Runnable {
                            cont++
                            var progreso : Double = (100/numeroLineas.toDouble())*(cont)
                            if (cont == numeroLineas){
                                progreso = 100.0
                            }
                            pbTabla.setProgress(progreso.toInt())
                        })
                    }

                    var sum : Double = 0.0

                } catch (e: Exception) {
                    var error = e.message
                    runOnUiThread(Runnable {
                        tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                    })
                    return false
                }
                runOnUiThread(Runnable {
                    pbProgresoTotal.setProgress((100/listaSQLCreateTable.size)*(i+1))
                })
                MainActivity.bd!!.setTransactionSuccessful()
                MainActivity.bd!!.endTransaction()
        }
        runOnUiThread(Runnable {
            pbProgresoTotal.setProgress(100)
            btFinalizar.visibility = View.VISIBLE
        })
        if (tipoSinc.equals("T")){
//            cargarSvm_vendedor_pedido_venta()
        }
        return true
    }

    override fun onBackPressed() {
        return
    }

    fun cerrar(view: View) {
        finish()
    }

    fun insertarUsuario(){
        try {
            MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
        } catch (e : Exception) {
            return
        }
    }

    fun validaVersion(versionUsuario:String,versionSistema:String, versionEstado:String):Boolean{
        if (!FuncionesUtiles.usuario.get("VERSION").equals(versionUsuario)){
                tvImei.setText("Version de usuario no corresponde.")
                btFinalizar.visibility = View.VISIBLE
            return false
        }
        if (!versionSistema.equals(MainActivity.version)){
            tvImei.setText("Debe actualizar su version para sincronizar.")
            btFinalizar.visibility = View.VISIBLE
            return false
        }
        if (versionEstado.equals("I")){
            tvImei.setText("El usuario se encuentra inactivo.")
            borrarTablasTodo(MainActivity.tablasSincronizacion!!.listaSQLCreateTables())
            borrarTablasTodo(SentenciasSQL.listaSQLCreateTable() )
            btFinalizar.visibility = View.VISIBLE
            return false
        }
        return true
    }

    fun validaVersion(versionUsuario:String):Boolean{
        if (!versionUsuario.equals(FuncionesUtiles.usuario.get("VERSION"))){
            tvImei.setText("Debe actualizar su version para sincronizar.")
            btFinalizar.visibility = View.VISIBLE
            return false
        }
        return true
    }

}
