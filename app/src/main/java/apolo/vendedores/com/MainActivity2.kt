package apolo.vendedores.com

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import apolo.vendedores.com.configurar.ActualizarVersion
import apolo.vendedores.com.menu.DialogoMenu
import apolo.vendedores.com.utilidades.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.ven_pri_evol_diaria_de_venta.*
import java.io.File
import java.io.IOException
import java.lang.Exception

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var utilidadesBD: UtilidadesBD? = null
        var bd: SQLiteDatabase? = null
        var codPersona : String = ""
        val funcion : FuncionesUtiles = FuncionesUtiles()
        val version : String = "1"
        var nombre : String = ""
        lateinit var etAccion : EditText
        var rooteado : Boolean = false
        var conexionWS : ConexionWS = ConexionWS()
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    lateinit var telMgr : TelephonyManager
    lateinit var dispositivo : FuncionesDispositivo

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        utilidadesBD = UtilidadesBD(this, null)
        bd = utilidadesBD!!.writableDatabase
        inicializaElementosReporte()
        cargarUsuarioInicial()
    }



    override fun onBackPressed() {
        if (drawer_layout_aplicacion.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_aplicacion.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun inicializaElementosReporte(){

        crearTablas()

        inicializaETAccion(accion)

        etAccion = accion
        dispositivo = FuncionesDispositivo(this)
        rooteado = !dispositivo.verificaRoot()

        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        dispositivo.verificaRoot()

//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }

        evolucionDiaria()
        nav_view_menu.setNavigationItemSelectedListener(this)

    }

    fun cargarUsuarioInicial():Boolean{
        lateinit var cursor : Cursor
        try {
            funcion.ejecutar(SentenciasSQL.createTableUsuarios(),this)
            cursor = funcion.consultar("SELECT * FROM usuarios")
        } catch(e: Exception){
//            var error = e.message
            return false
        }

        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvNombreVend).text = cursor.getString(cursor.getColumnIndex("NOMBRE"))
            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoVend).text = cursor.getString(cursor.getColumnIndex("LOGIN"))
            FuncionesUtiles.usuario.put("NOMBRE", cursor.getString(cursor.getColumnIndex("NOMBRE")))
            FuncionesUtiles.usuario.put("LOGIN", cursor.getString(cursor.getColumnIndex("LOGIN")))
            FuncionesUtiles.usuario.put("TIPO", cursor.getString(cursor.getColumnIndex("TIPO")))
            FuncionesUtiles.usuario.put("ACTIVO", cursor.getString(cursor.getColumnIndex("ACTIVO")))
            FuncionesUtiles.usuario.put("COD_EMPRESA", cursor.getString(cursor.getColumnIndex("COD_EMPRESA")))
            FuncionesUtiles.usuario.put("VERSION", cursor.getString(cursor.getColumnIndex("VERSION")))
            FuncionesUtiles.usuario.put("COD_PERSONA", codPersona())
            FuncionesUtiles.usuario.put("CONF","S")
            return true
        } else {
            FuncionesUtiles.usuario.put("CONF","N")
            if (nav_view_menu.headerCount>0) {
                nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvNombreVend)
                    .text = "Ingrese el nombre del promotor"
                nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoVend)
                    .text = "12345"
            }
            return false
        }
    }

    fun crearTablas(){
        for (i in 0 until SentenciasSQL.listaSQLCreateTable().size){
//            funcion.ejecutar(SentenciasSQL.listaSQLCreateTable().get(i),this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var menu = DialogoMenu(this)
        when (menuItem.itemId){
            R.id.vendVenta                  -> menu.mostrarMenu(menuItem,R.layout.menu_cab_visitas)
            R.id.vendReportes               -> menu.mostrarMenu(menuItem,R.layout.menu_cab_reportes)
            R.id.vendInformes               -> menu.mostrarMenu(menuItem,R.layout.menu_cab_informes)
            R.id.vendConfigurar             -> menu.mostrarMenu(menuItem,R.layout.menu_cab_configurar)
            R.id.vendSalir                  -> finish()
        }

        if (menuItem.itemId == R.id.vendActualizar){
            actualizarVersion()
        }

        //valida que el chip este activo y sea de una operadora nacional
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return false
//        }
        mostrarMenu()
        return true
    }

    fun codPersona():String{
        var sql : String = "SELECT DISTINCT COD_PERSONA FROM svm_vendedor_pedido"
        var cursor : Cursor = funcion.consultar(sql)
        if (cursor.count < 1){
            codPersona = ""
            return ""
        } else {
            codPersona = funcion.dato(cursor,"COD_PERSONA")
            return funcion.dato(cursor,"COD_PERSONA")
        }
    }

    fun verficaTiempoTranscurrido():Int{
        lateinit var cursor : Cursor
        var tiempo = 0
        try {
            cursor = bd!!.rawQuery("SELECT * FROM svm_vendedor_pedido", null)
        } catch(e: Exception){
            var error = e.message
        }
        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            var fecha_sincro =  cursor.getString(cursor.getColumnIndex("ULTIMA_SINCRO"))
            var hora_sincro = cursor.getString(cursor.getColumnIndex("HORA"))
            var fecha_hora_sincro = fecha_sincro + " " + hora_sincro + ":00"
            tiempo = funcion.tiempoTranscurrido(fecha_hora_sincro, funcion.getFechaHoraActual())
        }
        return tiempo
    }

    //ACTUALIZAR VERSION
    fun actualizarVersion(){
        etAccion = accion
        var dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAccionOpcion("DESCARGAR","",accion,"¿Desea actualizar la versión?","Atención!","SI","NO")
    }

    fun descargarActualizacion(){
        ActualizarVersion.context = this
        ConexionWS.context = this
        etAccion = accion
        crearArchivo()
        var descargar = ActualizarVersion()
        descargar.preparaActualizacion()
    }

    fun abrirInstalador(){
        try {
            verifyStoragePermissions(this)

        } catch (e : Exception){
            funcion.mensaje(this,"",e.message.toString())
        }
    }

    fun inicializaETAccion(etAccion: EditText){
        etAccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etAccion.text.toString().equals("DESCARGAR")){
                    descargarActualizacion()
                    etAccion.setText("")
                    return
                }
                if (etAccion.text.toString().equals("ACTUALIZAR")){
                    abrirInstalador()
                    etAccion.setText("")
                    return
                }
                if (s.toString().equals("abrir")){
                    startActivity(DialogoMenu.intent)
                    Companion.etAccion.setText("")
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

    @Throws(IOException::class)
    private fun crearArchivo() {
        // Crea el archivo para ubicar el instalador
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        var archivo : File = File(storageDir,"apolo_06.apk")
        archivo.createNewFile()
        nombre = archivo.absolutePath
    }

    fun verifyStoragePermissions(activity: Activity) {
        // verifica si hay premiso para escribir en el almacenamiento
        val permission = ActivityCompat.checkSelfPermission( activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // solicita permiso para escribir en el almacenamiento interno
            abrir(activity)
        }
    }

    fun abrir(activity: Activity){
        ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE)
        val file: File = File(nombre)
        if (Build.VERSION.SDK_INT >= 24) {
            val fileUri = FileProvider.getUriForFile(baseContext,"apolo.vendedores.com.fileprovider",file)
            val intent = Intent(Intent.ACTION_DEFAULT, fileUri)
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false)
//                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
            intent.setData(fileUri)
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

    fun mostrarMenu(){
        if (drawer_layout_aplicacion.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_aplicacion.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout_aplicacion.openDrawer(GravityCompat.START)
        }
    }



    fun evolucionDiaria(){
        var evolucion = ConsultasInicio(this)
        evolucion.evolucionDiariaDeVenta(lvEvolucionDiariaDeVentas)
    }



}