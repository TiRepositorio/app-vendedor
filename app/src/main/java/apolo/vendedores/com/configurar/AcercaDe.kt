package apolo.vendedores.com.configurar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.ConexionWS
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.ProgressDialog
import kotlinx.android.synthetic.main.activity_acerca_de.*
import java.io.File
import java.io.IOException

class AcercaDe : AppCompatActivity() {

    private lateinit var thread: Thread
    private lateinit var dialogo:ProgressDialog
    private val requestExternalStorage = 1
    private val permissionsStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
        var nombre : String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acerca_de)

        inicializar()
    }

    @SuppressLint("SetTextI18n")
    private fun inicializar(){
        tvBuscarActualizacion.setOnClickListener{ buscarActualizacion() }
        tvVersion.text = "Versión: ${MainActivity.version}.${MainActivity.fechaVersion}.${MainActivity.versionDelDia}"
        tvAutor.text = "Fecha de consulta: ${MainActivity.funcion.getFechaHoraActual()}"
        inicializaETAccion(accion)
        inicializarBoton(btActualizar)
    }

    private fun inicializarBoton(boton:Button){
        boton.setOnClickListener { actualizarVersion() }
    }

    private fun buscarActualizacion(){
        dialogo = ProgressDialog(this)
        thread = Thread{
            runOnUiThread{ dialogo.cargarDialogo("Buscando actualizaciones...",false) }
            Thread.sleep(1500)
            try {
                ConexionWS.resultados = MainActivity2.conexionWS.procesaVersion()
            } catch (e: java.lang.Exception) {
                ConexionWS.resultados = e.message.toString()
            }
            runOnUiThread {
                dialogo.cerrarDialogo()
                if (ConexionWS.resultados.split("-")[0].trim().length > 1) {
                    if (ConexionWS.resultados.indexOf("-Actualice la Version  Gestor:") > -1){
                        if (ConexionWS.resultados.split("-")[1].trim() == MainActivity.version.trim()){
                            MainActivity2.funcion.mensaje(
                                this,
                                "Sin actualizaciones",
                                "Ya se encuentra instalada la versión mas actualizada del sistema."
                            )
                            btActualizar.visibility = View.GONE
                        } else {
                            MainActivity2.funcion.mensaje(
                                this,
                                "¡Actualización disponible!",
                                "Hay una nueva versión de la aplicación.\n" +
                                        "Debe actualizar su sistema para seguir utilizándolo."
                            )
                            btActualizar.visibility = View.VISIBLE
                        }
                    } else {
                        MainActivity2.funcion.mensaje(
                            this,
                            "Error",
                            "Ha ocurrido un error en el proceso de verificación.\n${ConexionWS.resultados}"
                        )
                        btActualizar.visibility = View.GONE
                    }
                } else {
                    MainActivity2.funcion.mensaje(
                        this,
                        "Error",
                        "No se ha podido conectar con el servidor.\n${ConexionWS.resultados}"
                    )
                    btActualizar.visibility = View.GONE
                }
            }
            if (ConexionWS.resultados != "null") {
                return@Thread
            }
            runOnUiThread {
                MainActivity2.funcion.toast(this,"Verifique su conexion a internet y vuelva a intentarlo")
                finish()
            }
        }
        thread.start()
    }

    //ACTUALIZAR VERSION
    private fun actualizarVersion(){
        ActualizarVersion.etAccion = accion
        val dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAccionOpcion("DESCARGAR","",accion,"¿Desea actualizar la versión?","Atención!","SI","NO")
    }

    @Throws(IOException::class)
    private fun crearArchivo() {
        // Crea el archivo para ubicar el instalador
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val archivo = File(storageDir,"apolo_06.apk")
        archivo.createNewFile()
        nombre = archivo.absolutePath
    }

    fun descargarActualizacion(){
        ActualizarVersion.context = this
        ConexionWS.context = this
        ActualizarVersion.etAccion = accion
        crearArchivo()
        val descargar = ActualizarVersion()
        descargar.preparaActualizacion()
    }

    private fun abrir(activity: Activity){
        ActivityCompat.requestPermissions(activity,permissionsStorage,requestExternalStorage)
        val file = File(nombre)
        if (Build.VERSION.SDK_INT >= 24) {
            val fileUri = FileProvider.getUriForFile(baseContext,"apolo.vendedores.com.fileprovider",file)
            val intent = Intent(Intent.ACTION_DEFAULT, fileUri)
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false)
//                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
            intent.data = fileUri
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun verifyStoragePermissions(activity: Activity) {
        // verifica si hay premiso para escribir en el almacenamiento
        val permission = ActivityCompat.checkSelfPermission( activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // solicita permiso para escribir en el almacenamiento interno
            abrir(activity)
        }
    }

    fun abrirInstalador(){
        try {
            verifyStoragePermissions(this)
        } catch (e : Exception){
            MainActivity2.funcion.mensaje(this,"",e.message.toString())
        }
    }

    private fun inicializaETAccion(etAccion: EditText){
        etAccion.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun afterTextChanged(s: Editable?) {
                if (etAccion.text.toString() == "DESCARGAR"){
                    descargarActualizacion()
                    etAccion.setText("")
                    return
                }
                if (etAccion.text.toString() == "ACTUALIZAR"){
                    abrirInstalador()
                    etAccion.setText("")
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

        })
    }


}
