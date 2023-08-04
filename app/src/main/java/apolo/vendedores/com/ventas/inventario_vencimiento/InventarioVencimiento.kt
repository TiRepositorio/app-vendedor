package apolo.vendedores.com.ventas.inventario_vencimiento

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity.Companion.dispositivo
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.*
import kotlinx.android.synthetic.main.activity_inventario_vencimiento.*

class InventarioVencimiento : AppCompatActivity() {
    private var posProducto : Int = 0
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var etCodClienteS : EditText
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
        var fecha : String = ""
        var codUnidadRelacion : String = ""
        lateinit var cursor: Cursor
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
        private const val CODIGO_PERMISOS_CAMARA = 1
        private const val CODIGO_INTENT = 2


        var codVendedor : String = ""
        var codEmpresa = ""
    }

    private lateinit var buscar : DialogoBusquedaInventario
    private var permisoCamaraConcedido = false
    private var permisoSolicitadoDesdeBoton = false
    private lateinit var thread : Thread
    private lateinit var progressDialog : ProgressDialog
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario_vencimiento)
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo)

        if (!dispositivo.modoAvion() ){
            return
        }

        if(!dispositivo.zonaHoraria()){
            return
        }

        if(!dispositivo.fechaCorrecta()){
            return
        }

        if (!dispositivo.tarjetaSim(getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)){
            return
        }

        if(!dispositivo.horaAutomatica()){
            return
        }

        inicializarElementos()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun inicializarElementos(){
        funcion = FuncionesUtiles(this, imgTitulo, tvTitulo, spBuscar, etBuscar, btBuscar, accion)
        funcion.addItemSpinner(this,"Codigo-Descripcion-Cod. Barra","a.COD_ARTICULO-a.DESC_ARTICULO-a.COD_BARRA")
        funcion.cargarTitulo(R.drawable.ic_ruteo_prog,"Inventario de Vencimiento")
        funcion.ejecutar("DROP VIEW IF EXISTS svm_inventario_vencimiento",this)
        funcion.ejecutar(SentenciasSQL.createViewInventarioVencimiento(),this)
        btBuscar.setOnClickListener{ buscar() }

        verificarYPedirPermisosDeCamara()

        buscar = DialogoBusquedaInventario(this,
            "svm_cliente_vendedor",
            "COD_CLIENTE",
            "COD_SUBCLIENTE",
            "DESC_SUBCLIENTE",
            "",
            "CAST(COD_CLIENTE AS NUMBER),CAST(COD_SUBCLIENTE AS NUMBER)",
            "",
            etCodCliente,
            etDescCliente)
        buscar.cargarDialogo(false)
        etfecha.setText( funcion.getFechaActual() )
        btAgregar.setOnClickListener{ insertaActualizaInventario()  }
        etfecha.setOnClickListener{abrirCalendario()}
        etCodCliente()
        etAccion()
        etDetCantidadDeposito.setOnClickListener{funcion.dialogoEntradaNumero(etDetCantidadDeposito,etDetCantidadGondola, this,"guardar",etAccion)}
        etDetCantidadGondola.setOnClickListener{funcion.dialogoEntradaNumero(etDetCantidadDeposito,etDetCantidadGondola, this,"guardar",etAccion)}
        etCodCliente.setOnClickListener{ buscar.cargarDialogo(false) }
        btEnviar.setOnClickListener{enviarInventarioVencimiento()}
        etAddArticulo.setOnClickListener{buscarArticulo()}
    }

    private fun etCodCliente(){
        etCodClienteS = etCodCliente
        etCodCliente.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                cargar()
                mostrar()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    fun cargar(){
        FuncionesUtiles.listaCabecera = ArrayList()
        val sql : String = ( " SELECT * FROM ( "
                + "           SELECT DISTINCT a.id,a.COD_EMPRESA	,a.COD_ARTICULO	,a.DESC_ARTICULO	, "
                + " 		         a.COD_UNIDAD_REL	            ,a.REFERENCIA	,a.IND_BASICO		, "
                + " 		         a.COD_BARRA                    ,'0' ORDEN      ,b.COD_CLIENTE      , "
                + " 		         b.COD_SUBCLIENTE                                                     "
                + "             FROM svm_st_articulos_prom a, svm_inventario_art_cliente b "
                + "            WHERE a.MEN_UN_VTA = 'S'"
                + "              AND b.COD_EMPRESA    = a.COD_EMPRESA "
                + "              AND b.COD_ARTICULO   = a.COD_ARTICULO "
                + "              AND b.COD_CLIENTE    = '" + etCodCliente.text.toString().split('-')[0].trim()  + "' "
                + "              AND b.COD_SUBCLIENTE = '" + etCodCliente.text.toString().split('-')[1].trim()  + "' "
                + "              AND a.COD_EMPRESA    = '${codEmpresa}' "
                + "         GROUP BY a.id,a.COD_EMPRESA	,a.COD_ARTICULO	,a.DESC_ARTICULO	, "
                + " 		         a.COD_UNIDAD_REL	,a.REFERENCIA	,a.IND_BASICO		, "
                + "			         a.COD_BARRA        ,b.COD_CLIENTE  ,b.COD_SUBCLIENTE     "
                + ") a "
                + "         ORDER BY a.ORDEN, a.COD_ARTICULO ASC "
                )
        FuncionesUtiles.listaCabecera = ArrayList()
        FuncionesUtiles.listaDetalle  = ArrayList()
        FuncionesUtiles.subListaDetalle = ArrayList()
        funcion.cargarListaSubItem(FuncionesUtiles.listaCabecera
            ,FuncionesUtiles.subListaDetalle
            ,"svm_inventario_vencimiento"
            ,"COD_ARTICULO-COD_CLIENTE-COD_SUBCLIENTE-COD_EMPRESA"
            ,funcion.consultar(sql))
    }

    fun mostrar(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3)
        funcion.valores = arrayOf("COD_ARTICULO","DESC_ARTICULO","COD_BARRA")
        funcion.subVistas  = intArrayOf(R.id.tvs2,R.id.tvs4,R.id.tvs5, R.id.tvs6, R.id.tvs7) //R.id.tvs1, R.id.tvs3,
        funcion.subValores = arrayOf("FEC_VENCIMIENTO","CANT_DEP_ANT","CANT_GOND_ANT","CANT_DEP","CANT_GOND") // "FEC_INVENTARIO","COD_UNID_MED",
        val adapter:Adapter.ListaConSubitem =
            Adapter.ListaConSubitem(
                this,
                FuncionesUtiles.listaCabecera,
                FuncionesUtiles.subListaDetalle,
                R.layout.inventario_vencimiento_lista,
                R.layout.inventario_vencimiento_sub_lista,
                funcion.vistas,
                funcion.valores,
                funcion.subVistas,
                funcion.subValores,
                R.id.lvSubtabla,
                lvRuteoProgramado,
                accion,
                "cargarDetalle"
            )
        lvRuteoProgramado.adapter = adapter
        lvRuteoProgramado.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle = -1
            posProducto = position
        }
        if (FuncionesUtiles.listaCabecera.size>0){
            cargarDetalle(0)
        }
    }

    fun cargarDetalle(position:Int){
        etDetCodArticulo.setText(FuncionesUtiles.listaCabecera[position]["COD_ARTICULO"])
        etDetCantidadDeposito.setText("0")
        etDetCantidadGondola.setText("0")
        etDetFechaInventario.setText(funcion.getFechaActual())
        etDetReferencia.setText(FuncionesUtiles.listaCabecera[posProducto]["REFERENCIA"])
        etfecha.setText(funcion.getFechaActual())
    }

    fun abrirCalendario(){
        val calendario = DialogoCalendario(this)
        calendario.onCreateDialog(1,etfecha,etfecha)!!.show()
    }

    fun etAccion(){
        etAccion = accion
        accion.addTextChangedListener(object : TextWatcher{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "cargarDetalle") {
                    cargarDetalle(FuncionesUtiles.posicionCabecera)
                    lvRuteoProgramado.invalidateViews()
                    return
                }
                if (s.toString() == "detalleSublista") {
                    detalleSublista(FuncionesUtiles.posicionCabecera,FuncionesUtiles.posicionDetalle)
                    lvRuteoProgramado.invalidateViews()
                    return
                }
                if (s.toString() == "guardar") {
                    insertaActualizaInventario()
                    etAccion.setText("")
                    return
                }
                if (s.toString() == "lector") {
                    escanear()
                    accion.setText("")
                    return
                }
                if (s.toString().isNotEmpty()){
                    etDetCodArticulo.setText(etAccion.text.toString().split("-")[0].trim())
                    codUnidadRel(etDetCodArticulo.text.toString().trim())
                    funcion.dialogoEntradaNumero(etDetCantidadDeposito,etDetCantidadGondola,this@InventarioVencimiento,"guardar",etAccion)
                    abrirCalendario()
                    etAccion.setText("")
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun codUnidadRel(codArticulo:String){
        val sql = "SELECT DISTINCT COD_UNIDAD_REL  FROM svm_st_articulos_prom a " +
                " WHERE a.MEN_UN_VTA = 'S' AND a.COD_ARTICULO = '$codArticulo' "
        codUnidadRelacion = funcion.dato(funcion.consultar(sql),"COD_UNIDAD_REL")
    }

    fun insertaActualizaInventario(){

        if (!dispositivo.modoAvion() ){
            return
        }
//
        if(!dispositivo.zonaHoraria()){
            return
        }

        if(!dispositivo.fechaCorrecta()){
            return
        }

//        if (!dispositivo.tarjetaSim(getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)){
//            return
//        }
//
        if(!dispositivo.horaAutomatica()){
            return
        }

        // SELECT
        val sql = "SELECT * FROM svm_inventario_art_cliente "                                                 +
                  " WHERE COD_CLIENTE     = '" + etCodCliente.text.toString().split('-')[0].trim() +
                  "'  AND COD_SUBCLIENTE  = '" + etCodCliente.text.toString().split('-')[1].trim() +
                  "'  AND COD_ARTICULO    = '" + etDetCodArticulo.text.toString()                             +
                  "'  AND FEC_INVENTARIO  = '" + funcion.getFechaActual()                                     +
                  "'  AND FEC_VENCIMIENTO = '" + etfecha.text.toString()                                      +
                  "'"
        cursor = funcion.consultar(sql)
        if( cursor.count > 0){
            // UPDATE
            val sql1 = ("UPDATE svm_inventario_art_cliente SET "                                                   +
                    "       CANT_DEP        = '" + etDetCantidadDeposito.text.toString()                         +
                    "'    , CANT_GOND       = '" + etDetCantidadGondola.text.toString()                          +
                    "'    , ESTADO          = 'P'" +
                    " WHERE COD_CLIENTE     = '" + etCodCliente.text.toString().split('-')[0].trim() +
                    "'  AND COD_SUBCLIENTE  = '" + etCodCliente.text.toString().split('-')[1].trim() +
                    "'  AND COD_ARTICULO    = '" + etDetCodArticulo.text.toString()                             +
                    "'  AND FEC_INVENTARIO  = '" + funcion.getFechaActual()                                     +
                    "'  AND FEC_VENCIMIENTO = '" + etfecha.text.toString()                                      +
                    "'")
            funcion.ejecutar(sql1,this)
        }else{
            // INSERT
            val sql2 = ("INSERT INTO svm_inventario_art_cliente (COD_EMPRESA,COD_CLIENTE,COD_SUBCLIENTE, COD_ARTICULO, FEC_VENCIMIENTO, COD_UNID_MED, CANT_DEP, CANT_GOND,FEC_INVENTARIO) VALUES (" +
                    "'"   + codEmpresa +
                    "','" + etCodCliente.text.toString().split('-')[0].trim() +
                    "','" + etCodCliente.text.toString().split('-')[1].trim() +
                    "','" + etDetCodArticulo.text.toString() +
                    "','" + etfecha.text.toString() +
                    "','" + codUnidadRelacion +
                    "','" + etDetCantidadDeposito.text.toString()  +
                    "','" + etDetCantidadGondola.text.toString()   +
                    "','" + funcion.getFechaActual()               +
                    "')")
            funcion.ejecutar(sql2,this)
        }
        accion.setText("")
        cargar()
        mostrar()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buscar(){
        /*if (!dispositivo.validaEstadoSim()){
            return
        }*/
        val sql : String = (" SELECT DISTINCT	a.id,a.COD_EMPRESA	,a.COD_ARTICULO	,a.DESC_ARTICULO	, "
                + " 		                    a.COD_UNIDAD_REL	,a.REFERENCIA	,a.IND_BASICO		, "
                + " 		                    a.COD_BARRA         ,b.COD_CLIENTE  ,b.COD_SUBCLIENTE     "
                + "             FROM svm_st_articulos_prom a, svm_inventario_art_cliente b "
                + "            WHERE a.MEN_UN_VTA = 'S' "
                + "              AND a.COD_ARTICULO   = b.COD_ARTICULO "
                + "              AND a.COD_EMPRESA    = b.COD_EMPRESA "
                + "              AND b.COD_CLIENTE    = '${etCodCliente.text.toString().split("-")[0]}' "
                + "              AND b.COD_SUBCLIENTE = '${etCodCliente.text.toString().split("-")[1]}' "
                + "              AND ${funcion.valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]} LIKE '%${etBuscar.text}%'   "
                + "              AND a.COD_EMPRESA = '${codEmpresa}' "
                + "            GROUP BY a.id,a.COD_EMPRESA	,a.COD_ARTICULO	,a.DESC_ARTICULO	, "
                + " 		            a.COD_UNIDAD_REL	,a.REFERENCIA	,a.IND_BASICO		, "
                + "			            a.COD_BARRA         ,b.COD_CLIENTE  ,b.COD_SUBCLIENTE"
                + "            ORDER BY a.DESC_ARTICULO asc")
        FuncionesUtiles.listaCabecera = ArrayList()
        FuncionesUtiles.listaDetalle  = ArrayList()
        FuncionesUtiles.subListaDetalle = ArrayList()
        funcion.cargarListaSubItem(FuncionesUtiles.listaCabecera
            ,FuncionesUtiles.subListaDetalle
            ,"svm_inventario_vencimiento"
            ,"COD_ARTICULO-COD_CLIENTE-COD_SUBCLIENTE-COD_EMPRESA"
            ,funcion.consultar(sql))
        mostrar()
    }

    fun detalleSublista(posCab:Int,posDet:Int){
        etDetCodArticulo.setText(FuncionesUtiles.listaCabecera[posCab]["COD_ARTICULO"])
        etfecha.setText(FuncionesUtiles.subListaDetalle[posCab][posDet]["FEC_VENCIMIENTO"])
//        etDetCantidadGondolaAnt.setText(FuncionesUtiles.subListaDetalle.get(posCab).get(posDet).get("CANT_GOND"))
//        etDetCantidadDepositoAnt.setText(FuncionesUtiles.subListaDetalle.get(posCab).get(posDet).get("CANT_DEP"))
        etDetReferencia.setText(FuncionesUtiles.listaCabecera[posCab]["REFERENCIA"])
        etDetFechaInventario.setText(FuncionesUtiles.subListaDetalle[posCab][posDet]["FEC_INVENTARIO"])
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun enviarInventarioVencimiento(){
        if (!dispositivo.modoAvion() ){
            return
        }

        if(!dispositivo.zonaHoraria()){
            return
        }

        if(!dispositivo.fechaCorrecta()){
            return
        }

        if(!dispositivo.horaAutomatica()){
            return
        }

        /*if (!dispositivo.validaEstadoSim()){
            return
        }*/

        EnviarInventarioVencimiento.context = this@InventarioVencimiento
        EnviarInventarioVencimiento.sinc = false
        EnviarInventarioVencimiento.codCliente = etCodCliente.text.split("-")[0].trim()
        EnviarInventarioVencimiento.codSubcliente = etCodCliente.text.split("-")[1].trim()
        val enviarInventarioVencimiento = EnviarInventarioVencimiento()
        if (enviarInventarioVencimiento.enviar()){
            enviar()
        }
    }

    private fun enviar(){
        thread = Thread {
            progressDialog = ProgressDialog(this)
            runOnUiThread { progressDialog.cargarDialogo("Enviando inventario", false) }
            try {
                EnviarInventarioVencimiento.respuesta = MainActivity.conexionWS.procesaEnviaInventarioVencimiento(
                    codVendedor,
                    EnviarInventarioVencimiento.cadena2
                )
            } catch (e: Exception) {
                EnviarInventarioVencimiento.respuesta = e.message.toString()
            }
            if (EnviarInventarioVencimiento.respuesta.split("*").size != 1) {    //==> Si cant de caracteres de "mensaje" no es = 1 o Si retorna mensaje
                if (EnviarInventarioVencimiento.respuesta.split("*")[0] == "01") {
                    val sql = ("UPDATE svm_inventario_art_cliente set ESTADO = 'E' "
                            +  " WHERE COD_CLIENTE  	= '${EnviarInventarioVencimiento.codCliente}'    "
                            +  "   AND COD_SUBCLIENTE 	= '${EnviarInventarioVencimiento.codSubcliente}' "
                            +  "   AND ESTADO 		    = 'P' ")
                    Thread.sleep(1000)
                    runOnUiThread { MainActivity.bd!!.execSQL(sql) }
                    runOnUiThread { MainActivity.bd!!.execSQL(sql) }
                    runOnUiThread { MainActivity.bd!!.execSQL(sql) }
                    runOnUiThread {
                        progressDialog.cerrarDialogo()
                        val dialogo = DialogoAutorizacion(EnviarInventarioVencimiento.context)
                        dialogo.dialogoAccion("", etAccion, EnviarInventarioVencimiento.respuesta,
                            "Operación existosa!","ok")
                    }
                } else {
                    runOnUiThread {
                        progressDialog.cerrarDialogo()
                        funcion.mensaje(this,"Atención", "No se ha podido enviar la información")
                    }
                }
            } else {
                runOnUiThread {
                    progressDialog.cerrarDialogo()
                    funcion.mensaje(this,"Error",EnviarInventarioVencimiento.respuesta)
                }
            }
        }
        thread.start()
    }

    private fun buscarArticulo(){
        val dialogo = DialogoBusquedaInventario(this,
            "svm_st_articulos_prom",
            "COD_ARTICULO",
            "",
            "DESC_ARTICULO",
            "COD_BARRA",
            "",
            "DESC_ARTICULO",
            " AND MEN_UN_VTA = 'S' AND COD_EMPRESA = '${codEmpresa}'" +
                    " AND UPPER(DESC_ARTICULO) NOT LIKE '%RRHH%'",
            accion,
            null)
        dialogo.cargarDialogo(true)
    }

    private fun escanear() {
        val i = Intent(this, Escanear::class.java)
        startActivityForResult(i, CODIGO_INTENT)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val codigo = data.getStringExtra("codigo")
                    etBuscar.setText(codigo)
                    buscarArticulo()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CODIGO_PERMISOS_CAMARA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // Escanear directamente solo si fue pedido desde el botón
                if (permisoSolicitadoDesdeBoton) {
                    escanear()
                }
                permisoCamaraConcedido = true
            } else {
                permisoDeCamaraDenegado()
            }
        }
    }

    private fun verificarYPedirPermisosDeCamara() {
        val estadoDePermiso =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoCamaraConcedido = true
        } else {
            // Si no, pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                CODIGO_PERMISOS_CAMARA
            )
        }
    }

    private fun permisoDeCamaraDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(
            this,
            "No puedes escanear si no das permiso",
            Toast.LENGTH_SHORT
        ).show()
    }

}
