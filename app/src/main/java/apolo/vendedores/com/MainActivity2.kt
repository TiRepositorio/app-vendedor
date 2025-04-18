package apolo.vendedores.com

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import apolo.vendedores.com.clases.Usuario
import apolo.vendedores.com.clases.Vendedor
import apolo.vendedores.com.configurar.*
import apolo.vendedores.com.menu.DialogoMenu
import apolo.vendedores.com.reportes.*
import apolo.vendedores.com.utilidades.*
import apolo.vendedores.com.ventas.PromotoresInventario
import apolo.vendedores.com.ventas.asistencia.EnviarMarcacion
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.ven_pri_accesos.*
import kotlinx.android.synthetic.main.ventana_principal.*
import java.io.File
import java.io.IOException


class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var utilidadesBD: UtilidadesBD? = null
        var bd: SQLiteDatabase? = null
        var codPersona : String = ""
        @SuppressLint("StaticFieldLeak")
        val funcion : FuncionesUtiles = FuncionesUtiles()
        var nombre : String = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
        var rooteado : Boolean = false
        @SuppressLint("StaticFieldLeak")
        var conexionWS : ConexionWS = ConexionWS()


        var listaUsuarios : ArrayList<Usuario> = ArrayList()
        var posicionEditaUsuario = 0
        const val limiteCantidadUsuario = 4

        var vendedorActivo = Vendedor()

    }


    private val REQUEST_CODE_PERMISOS = 100

    private val permisos = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        //Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_PHONE_STATE
    )




    private val requestExternalStorage = 1
    private val permissionsStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var telMgr : TelephonyManager
    private lateinit var dispositivo : FuncionesDispositivo
    private lateinit var ubicacion : FuncionesUbicacion
    private lateinit var lm: LocationManager

    private lateinit var grafico : Graficos
    private var subInforme1 = SubVentasPorMarcas()
//    private var subInforme2 = SubVentasPorClientes()
    private var subInforme3 = SubAvanceDeComisiones()
    private var cerrar = false
    private lateinit var enviarMarcacion : EnviarMarcacion

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        utilidadesBD = UtilidadesBD(this, null)
        bd = utilidadesBD!!.writableDatabase
        inicializaElementosReporte()
        cargarUsuarioInicial()

        dispositivo.getAppsForMockLocation(this)

        validarPermisos()

    }


    override fun onResume() {
        super.onResume()
        checkPermissionAlIniciar()
    }


    private fun checkPermissionAlIniciar() {
        val permiso = Manifest.permission.READ_PHONE_STATE

        if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si no tiene permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                // Ya lo negó una vez, pero NO marcó "no volver a preguntar"
                ActivityCompat.requestPermissions(this, arrayOf(permiso), REQUEST_CODE_PERMISOS)
            } else {
                // Si es la primera vez o marcó "no volver a preguntar"
                ActivityCompat.requestPermissions(this, arrayOf(permiso), REQUEST_CODE_PERMISOS)
            }
        }
    }


    private fun validarPermisos() {

        //Log.d("Permisos", "VALIDANDO PERMISOS")
        val permisosDenegados = permisos.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permisosDenegados.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permisosDenegados.toTypedArray(),
                REQUEST_CODE_PERMISOS
            )
        } else {
            //Log.d("Permisos", "Todos los permisos ya fueron concedidos")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISOS) {
            var algunoDenegadoPermanentemente = false

            for (i in permissions.indices) {
                val permiso = permissions[i]
                val resultado = grantResults[i]

                if (resultado == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permisos", "$permiso concedido")
                } else {
                    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)

                    if (!showRationale) {
                        Log.d("Permisos", "$permiso denegado PERMANENTEMENTE")
                        algunoDenegadoPermanentemente = true
                    } else {
                        Log.d("Permisos", "$permiso denegado")
                        Toast.makeText(this, "Debés aceptar el permiso $permiso para continuar", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (algunoDenegadoPermanentemente) {
                mostrarDialogoParaIrAConfiguracion()
            }
        }
    }


    private fun mostrarDialogoParaIrAConfiguracion() {
        AlertDialog.Builder(this)
            .setTitle("Permiso requerido")
            .setMessage("Debés habilitar el permiso desde la configuración de la app para continuar.")
            .setPositiveButton("Ir a configuración") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }



    override fun onBackPressed() {
        if (drawer_layout_aplicacion.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_aplicacion.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun inicializaElementosReporte(){

        crearTablas()
        modificarTablas()
        borrarVistas()

        inicializaETAccion(accion)

        etAccion = accion

        dispositivo = FuncionesDispositivo(this)
        rooteado = dispositivo.verificaRoot()
        ubicacion = FuncionesUbicacion(this)
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


        subInforme1.cargarVentasPorMarcas()
        subInforme1.cargarDatosX("DESC_GTE_MARKETIN")
        subInforme1.cargarDatosY("VENTA_MES2")

        grafico = Graficos(graVentas,subInforme1.datosX,subInforme1.datosY)
        grafico.getGraficoDeBarra("", Color.BLACK,15.toFloat(),Color.WHITE,1500)
        graVentas.setOnClickListener{
            if (dispositivo.fechaCorrecta()) {
                startActivity(Intent(this, VentasPorMarca::class.java))
            }
        }


        subInforme3.cargarDatos()
        subInforme3.cargarDatosX("TIP_COM")
        subInforme3.cargarDatosY("MONTO_VENTA")

        grafico = Graficos(graComision,subInforme3.datosX,subInforme3.datosY)
        grafico.getGraficoDeBarra("", Color.BLACK,15.toFloat(),Color.WHITE,1500)
        graComision.setOnClickListener{
            if (dispositivo.fechaCorrecta()) {
                startActivity(Intent(this, AvanceDeComisiones::class.java))
            }
        }

        ibt1Acceso.setOnClickListener {
            if (dispositivo.fechaCorrecta() &&
                dispositivo.horaAutomatica() &&
                dispositivo.modoAvion() &&
                dispositivo.zonaHoraria() &&
                dispositivo.tarjetaSim(telMgr)) {
                startActivity(Intent(this, ExtractoDeSalario::class.java))
            }
        }
        ibt2Acceso.setOnClickListener {
            if (dispositivo.fechaCorrecta() &&
                dispositivo.horaAutomatica() &&
                dispositivo.modoAvion() &&
                dispositivo.zonaHoraria() &&
                dispositivo.tarjetaSim(telMgr)) {
                startActivity(Intent(this, ComprobantesPendientes::class.java))
            }
        }
        ibt3Acceso.setOnClickListener {
            if (dispositivo.fechaCorrecta() &&
                dispositivo.horaAutomatica() &&
                dispositivo.modoAvion() &&
                dispositivo.zonaHoraria() &&
                dispositivo.tarjetaSim(telMgr)) {
                startActivity(Intent(this, VentasPorCliente::class.java))
            }
        }

        agregarProgPedido()

        nav_view_menu.setNavigationItemSelectedListener(this)
    }

    private fun agregarProgPedido(){
        try {
            bd!!.execSQL("select PROG_PEDIDO FROM usuarios")
        } catch (e:java.lang.Exception){
            if (e.message.toString().indexOf("such column")>-1){
                funcion.ejecutar("alter table usuarios add column PROG_PEDIDO TEXT DEFAULT '0'",this)
            }
//            funcion.mensaje(this,"ERROR!",e.message.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cargarUsuarioInicial():Boolean{
        lateinit var cursor : Cursor
        try {
            funcion.ejecutar(SentenciasSQL.createTableUsuarios(),this)
            cursor = funcion.consultar("SELECT * FROM usuarios")
        } catch(e: Exception){
            e.message
            return false
        }

        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvNombreVend).text = cursor.getString(cursor.getColumnIndex("NOMBRE"))
            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoVend).text = cursor.getString(cursor.getColumnIndex("LOGIN"))
            FuncionesUtiles.usuario["NOMBRE"] = cursor.getString(cursor.getColumnIndex("NOMBRE"))
            FuncionesUtiles.usuario["LOGIN"] = cursor.getString(cursor.getColumnIndex("LOGIN"))
            FuncionesUtiles.usuario["TIPO"] = cursor.getString(cursor.getColumnIndex("TIPO"))
            FuncionesUtiles.usuario["ACTIVO"] = cursor.getString(cursor.getColumnIndex("ACTIVO"))
            FuncionesUtiles.usuario["COD_EMPRESA"] = cursor.getString(cursor.getColumnIndex("COD_EMPRESA"))
            FuncionesUtiles.usuario["VERSION"] = cursor.getString(cursor.getColumnIndex("VERSION"))
            FuncionesUtiles.usuario["COD_PERSONA"] = codPersona()
            FuncionesUtiles.usuario["CONF"] = "S"
            zonaHoraria()
            return true
        } else {
            FuncionesUtiles.usuario["CONF"] = "N"
            if (nav_view_menu.headerCount>0) {
                nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvNombreVend).text = "Ingrese el nombre del promotor"
                nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoVend).text = "12345"
            }
            return false
        }
    }

    private fun crearTablas(){
        for (i in 0 until SentenciasSQL.listaSQLCreateTable().size){
            funcion.ejecutar(SentenciasSQL.listaSQLCreateTable()[i],this)
        }
    }
    private fun modificarTablas(){
        for (i in 0 until SentenciasSQL.listaSQLAlterTable().size){
            funcion.ejecutarB(SentenciasSQL.listaSQLAlterTable()[i],this)
        }
    }

    private fun borrarVistas(){
        for (i in 0 until SentenciasSQL.listaSQLDropView().size){
            funcion.ejecutarB(SentenciasSQL.listaSQLDropView()[i],this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        if (R.id.vendActualizar  != menuItem.itemId &&
            R.id.vendConfigurar  != menuItem.itemId &&
            R.id.vendSincronizar != menuItem.itemId &&
            R.id.vendAcercaDe    != menuItem.itemId &&
            R.id.vendSalir       != menuItem.itemId ) {
                if (!dispositivo.horaAutomatica() ||
                    !dispositivo.modoAvion() ||
                    !dispositivo.zonaHoraria() ||
                    !dispositivo.tarjetaSim(telMgr)) {
                    funcion.toast(this,"Verifique su configuración para continuar.")
                    return false
                }
                if (!dispositivo.fechaCorrecta()) {
                    funcion.toast(this,"Debe sincronizar para continuar.")
                    return false
                }
        }
        val menu = DialogoMenu(this)
        EnviarMarcacion.contexto = this
        EnviarMarcacion.etAccion = accion

        enviarMarcacion = EnviarMarcacion("","")
        when (menuItem.itemId){
            R.id.vendVenta                  -> menu.mostrarMenu(menuItem,R.layout.menu_cab_visitas)
            R.id.vendReportes               -> menu.mostrarMenu(menuItem,R.layout.menu_cab_reportes)
            R.id.vendInformes               -> menu.mostrarMenu(menuItem,R.layout.menu_cab_informes)
            R.id.vendConfigurar             -> menu.mostrarMenu(menuItem,R.layout.menu_cab_configurar)
//            R.id.vendMarcaciones            -> enviarMarcacion.cargarDatosDelDia()

            R.id.vendMarcaciones            -> enviarMarcaciones()
            R.id.vendSalir                  -> finish()
        }

        if (menuItem.itemId == R.id.vendConfigurar){
            CalcularClavePrueba.informe = ConfigurarUsuarioNuevo::class.java
        }

        if (menuItem.itemId == R.id.vendActualizar){
            actualizarVersion()
        }
        if (menuItem.itemId == R.id.vendAcercaDe){
            startActivity(Intent(this@MainActivity2,AcercaDe::class.java))
            accion.setText("")
        }

        if (menuItem.itemId == R.id.inventario_vencimiento){
            startActivity(Intent(Intent(this, PromotoresInventario::class.java)))
            accion.setText("")
        }

        if (menuItem.itemId == R.id.vendSincronizar){
            val dialogo = DialogoAutorizacion(this)
            dialogo.dialogoAccionOpcion("sincronizar","",accion
                ,"¿Desea sincronizar ahora?","¡Atención!"
                ,"SI","NO")
        }

        cerrar = menuItem.itemId == R.id.vendConfigurar

        //valida que el chip este activo y sea de una operadora nacional
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return false
//        }
        mostrarMenu()
        return true
    }

    private fun enviarMarcaciones(){
        val progressDialog = ProgressDialog(this)
        val thread = Thread{
            runOnUiThread { progressDialog.cargarDialogo("Enviando marcaciones...",false) }
            EnviarMarcacion.contexto = this
            EnviarMarcacion.etAccion = accion
            EnviarMarcacion.accion = ""
            enviarMarcacion = EnviarMarcacion("","")
            if (!enviarMarcacion.cargarDatosDelDia()){
                runOnUiThread {
                    progressDialog.cerrarDialogo()
                    funcion.mensaje(this,"Atención!",EnviarMarcacion.resultado)
                }
                return@Thread
            }
            try {
                EnviarMarcacion.resultado = conexionWS.procesaMarcacionAsistenciaAct(FuncionesUtiles.usuario["LOGIN"].toString(),
                    EnviarMarcacion.cadena,
                    FuncionesUtiles.usuario["COD_EMPRESA"].toString()
                )
//                resultado = "01*GRABADO CON EXITO"
            } catch (e: Exception) {
                EnviarMarcacion.resultado = e.message.toString()
            }
            val mensaje: Array<String> = EnviarMarcacion.resultado.split("*").toTypedArray()
            if (mensaje.size == 1) {
                runOnUiThread{
                    progressDialog.cerrarDialogo()
                    funcion.mensaje(EnviarMarcacion.contexto,"Resultado",EnviarMarcacion.resultado)
                }
            } else {
                if (mensaje[0] == "01") {
                    val update = (" UPDATE vt_marcacion_ubicacion SET ESTADO = 'E' " +
                                "  WHERE ESTADO = 'P'" +
                                "    AND FECHA  like '%" + funcion.getFechaActual() + "%'")
                    runOnUiThread { funcion.ejecutar(update, EnviarMarcacion.contexto) }
                    EnviarMarcacion.anomalia = ""
                }
                if ( mensaje[0]=="01"){
                    mensaje[1]="Datos enviados con éxito."
                }
                runOnUiThread {
                    funcion.mensaje(EnviarMarcacion.contexto,"Resultado", mensaje[1])
                    progressDialog.cerrarDialogo()
                }
            }
            runOnUiThread { EnviarMarcacion.etAccion.setText(EnviarMarcacion.accion) }
        }
        thread.start()
    }

    private fun codPersona():String{
        val sql = "SELECT DISTINCT COD_PERSONA FROM svm_vendedor_pedido"
        val cursor : Cursor = funcion.consultar(sql)
        return if (cursor.count < 1){
            codPersona = ""
            ""
        } else {
            codPersona = funcion.dato(cursor,"COD_PERSONA")
            funcion.dato(cursor,"COD_PERSONA")
        }
    }

    private fun zonaHoraria():String{
        val sql = "SELECT DISTINCT ZONA_HORARIA FROM usuarios"
        val cursor : Cursor = funcion.consultar(sql)
        return if (cursor.count < 1){
            AppGlobals.offsetGMT = -3
            ""
        } else {
            try {
                AppGlobals.offsetGMT = funcion.dato(cursor,"ZONA_HORARIA").toInt()
            } catch (e: Exception) {
                AppGlobals.offsetGMT = -3
            }

            funcion.dato(cursor,"ZONA_HORARIA")
        }
    }

    @SuppressLint("Recycle")
    fun verficaTiempoTranscurrido():Int{
        lateinit var cursor : Cursor
        var tiempo = 0
        try {
            cursor = bd!!.rawQuery("SELECT * FROM svm_vendedor_pedido", null)
        } catch(e: Exception){
        }
        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            val fechaSincro =  cursor.getString(cursor.getColumnIndex("ULTIMA_SINCRO"))
            val horaSincro = cursor.getString(cursor.getColumnIndex("HORA"))
            val fechaHoraSincro = "$fechaSincro $horaSincro:00"
            tiempo = funcion.tiempoTranscurrido(fechaHoraSincro, funcion.getFechaHoraActual())
        }
        return tiempo
    }

    //ACTUALIZAR VERSION
    private fun actualizarVersion(){
        val dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAccionOpcion("DESCARGAR","",accion,"¿Desea actualizar la versión?","Atención!","SI","NO")
    }

    fun descargarActualizacion(){
        ActualizarVersion.context = this
        ConexionWS.context = this
        crearArchivo()
        ActualizarVersion.etAccion = accion
        val descargar = ActualizarVersion()
        descargar.preparaActualizacion()
    }

    fun abrirInstalador(){
        try {
            verifyStoragePermissions(this)
        } catch (e : Exception){
            funcion.mensaje(this,"",e.message.toString())
        }
    }

    private fun inicializaETAccion(etAccion: EditText){
        etAccion.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.O)
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
                if (s.toString() == "abrir"){
                    startActivity(DialogoMenu.intent)
                    Companion.etAccion.setText("")
                    if (cerrar){
                        finish()
                    }
                }
                if (s.toString() == "sincronizado"){
                    inicializaElementosReporte()
                    accion.setText("")
                }
                if (s.toString() == "sincronizar"){
                    startActivity(Intent(this@MainActivity2,Sincronizacion::class.java))
                    accion.setText("")
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
        val archivo = File(storageDir,"apolo_06.apk")
        archivo.createNewFile()
        nombre = archivo.absolutePath
    }

    private fun verifyStoragePermissions(activity: Activity) {
        // verifica si hay premiso para escribir en el almacenamiento
        val permission = ActivityCompat.checkSelfPermission( activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // solicita permiso para escribir en el almacenamiento interno
            abrir(activity)
        }
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

    private fun mostrarMenu(){
        if (drawer_layout_aplicacion.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_aplicacion.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout_aplicacion.openDrawer(GravityCompat.START)
        }
    }

//    private fun evolucionDiaria(){
//        val evolucion = ConsultasInicio(this)
//        evolucion.evolucionDiariaDeVenta(lvEvolucionDiariaDeVentas)
//    }

    fun mostrarMenu(view: View) {
        view.id
        if (drawer_layout_aplicacion.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_aplicacion.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout_aplicacion.openDrawer(GravityCompat.START)
        }
    }

}