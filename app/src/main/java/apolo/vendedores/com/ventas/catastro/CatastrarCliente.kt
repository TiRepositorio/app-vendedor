//package apolo.vendedores.com.ventas.catastro
//
//import android.app.*
//import android.app.DatePickerDialog.OnDateSetListener
//import android.content.ContentValues
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.pm.ActivityInfo
//import android.database.Cursor
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import android.net.Uri
//import android.os.AsyncTask
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.view.View
//import android.view.ViewGroup
//import android.view.Window
//import android.widget.*
//import android.widget.AdapterView.OnItemClickListener
//import apolo.vendedores.com.R
//import apolo.vendedores.com.utilidades.FuncionesDispositivo
//import apolo.vendedores.com.utilidades.FuncionesUtiles
//import apolo.vendedores.com.utilidades.Mapa
//import kotlinx.android.synthetic.main.activity_catastrar_cliente.*
//import java.io.*
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//class CatastrarCliente : Activity() {
//    //	VARIABLES PARA LISTA DE CONDICION DE VENTA
//    var dialogoFormaPago: Dialog? = null
//    var lvFormaPago: ListView? = null
//    lateinit var codFormaPago: Array<String?>
//    lateinit var descFormaPago: Array<String?>
//    var posicionFormaPago = 0
//    var ultCodFormaPago: String? = ""
//    var cursorFormaPago: Cursor? = null
//    var listaFormaPago: MutableList<HashMap<String, String>>? =
//        null
//    var consulta = false
//
//    //	VARIABLES PARA LISTA DE TIPO DE CLIENTE
//    var dialogoTipoCliente: Dialog? = null
//    var lvTipoCliente: ListView? = null
//    lateinit var codTipoCliente: Array<String?>
//    lateinit var descTipoCliente: Array<String?>
//    var posicionTipoCliente = 0
//    var ultCodTipoCliente: String? = ""
//    var cursorTipoCliente: Cursor? = null
//    var listaTipoCliente: MutableList<HashMap<String, String>>? =
//        null
//
//    //	VARIABLES PARA LISTA DE DIAS DE VISITA
//    var dialogoDiasVisita: Dialog? = null
//    var lvDiasVisita: ListView? = null
//    lateinit var codDiasVisita: Array<String?>
//    lateinit var descDiasVisita: Array<String?>
//    lateinit var frecuenciaDiasVisita: Array<String?>
//    var posicionDiasVisita = 0
//    var ultCodDiasVisita: String? = ""
//    var cursor_datos_dias_visita: Cursor? = null
//    var alist_dias_visita: MutableList<HashMap<String, String>>? =
//        null
//
//    //	VARIABLES PARA LISTA DE DEPARTAMENTOS
//    var dialog_departamentos: Dialog? = null
//    var list_view_departamentos: ListView? = null
//    lateinit var cod_departamentos: Array<String?>
//    lateinit var desc_departamentos: Array<String?>
//    lateinit var cod_pais: Array<String?>
//    lateinit var desc_pais: Array<String?>
//    var ult_cod_dep: String? = ""
//    var ult_cod_pais_dep: String? = ""
//    var save_departamentos = 0
//    var cursor_datos_departamentos: Cursor? = null
//    var alist_departamentos: MutableList<HashMap<String, String>>? =
//        null
//
//    //	VARIABLES PARA LISTA DE CIUDADES
//    var dialog_ciudades: Dialog? = null
//    var list_view_ciudades: ListView? = null
//    lateinit var cod_ciudad: Array<String?>
//    lateinit var desc_ciudad: Array<String?>
//    lateinit var cod_departamentos_ciudad: Array<String?>
//    lateinit var desc_departamentos_ciudad: Array<String?>
//    lateinit var cod_pais_ciudad: Array<String?>
//    lateinit var desc_pais_ciudad: Array<String?>
//    lateinit var frecuencia_ciudad: Array<String?>
//    var save_ciudades = 0
//    var ult_cod_ciudad: String? = ""
//    var cursor_datos_ciudades: Cursor? = null
//    var alist_ciudades: MutableList<HashMap<String, String>>? =
//        null
//    var cod_cliente_comp = 0
//
//    //	VARIABLES PARA BUSCADOR DE CLIENTES CATASTRADOS
//    var dialog_clientes_catastrados: Dialog? = null
//    var list_view_clientes_catastrados: ListView? = null
//    var save_clientes_catastrados = -1
//    var cursor_clientes_catastrados_buscar: Cursor? = null
//    var alist_cliente_catastrado: MutableList<HashMap<String, String>>? = null
//    lateinit var cod_cliente_composicion: Array<String?>
//    lateinit var estado_cliente_catastrado: Array<String?>
//
//    //	VARIABLES PARA LISTA DE CANALES
//    var dialog_lista_precios: Dialog? = null
//    var list_view_lista_precios: ListView? = null
//    lateinit var cod_lista_precios: Array<String?>
//    lateinit var desc_lista_precios: Array<String?>
//    var save_lista_precios = 0
//    var ultCodListaPrecios: String = ""
//    var cursor_datos_lista_precios: Cursor? = null
//    var alist_lista_precios: MutableList<HashMap<String, String>>? =
//        null
//    private var fec_desde: TextView? = null
//    private var fec_hasta: TextView? = null
//    private var mYear = 0
//    private var mMonth = 0
//    private var mDay = 0
//    private var fecha = 0
//
//    //	VARIABLES PARA EL WEB SERVICE
//    var vCliente = ""
//    var codCliente = ""
//    var respuestaWS = ""
//    var name = ""
//    var imagen_fachada: String? = null
//
//    val funcion = FuncionesUtiles(this)
//    private val dispositivo = FuncionesDispositivo(this)
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        setContentView(R.layout.activity_catastrar_cliente)
//
//        var bloquear = "N"
//        var msg = ""
//        if (funcion.getIndPalm(codVendedor.toString()) == "N") {
//            bloquear = "S"
//            msg = "No esta permitido"
//        }
//        if (dispositivo.fechaCorrecta()) {
//            bloquear = "S"
//            msg = "Sincronize primero,o debe pedir una clave en la Empresa."
//        }
//        if (bloquear == "S") {
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("Atención")
//            builder.setMessage(msg)
//            builder.setCancelable(false)
//            builder.setPositiveButton("OK",
//                    DialogInterface.OnClickListener { _, _ ->
//                        finish()
//                        return@OnClickListener
//                    })
//            val alert = builder.create()
//            alert.show()
//        } else {
//            obtieneCodCliente(codVendedor)
//            val codigo = findViewById<View>(R.id.etCodigo) as EditText
//            codigo.setText("$codVendedor-$cod_cliente_comp")
//            codigo.setOnClickListener {
//                val myAlertDialog =
//                    AlertDialog.Builder(this@CatastrarCliente)
//                myAlertDialog.setMessage("Se limpiara el formulario ¿Desea continuar?")
//                myAlertDialog.setPositiveButton(
//                    "Si"
//                ) { _, _ ->
//                    try {
//                        limpiarTodo()
//                        obtieneCodCliente(codVendedor)
//                        codigo.setText("$codVendedor-$cod_cliente_comp")
//                        consultarClientesCatastrados()
//                    } catch (e: Exception) {
//                        funcion.toast(applicationContext, "ERROR " + e.message)
//                    }
//                }
//                myAlertDialog.setNegativeButton("No") { _, _ -> }
//                myAlertDialog.show()
//            }
//            etCiudad.setOnClickListener{ trae_ciudades(etCiudad) }
//            etListaPrecio.setOnClickListener{ trae_lista_precios(etListaPrecio) }
//            etDepartamento.setOnClickListener{ trae_departamentos(etDepartamento) }
//            btVolver.setOnClickListener { finish() }
//            etFormaPago.setOnClickListener { trae_formas_pago(etFormaPago) }
//            etTipoCliente.setOnClickListener{trae_tipo_cliente(etTipoCliente) }
//            etDiasVisita.setOnClickListener { trae_dias_visita(etDiasVisita) }
//            btBuscarEnMapa.setOnClickListener{
////                MenuCombinado._ind_mapa_cliente = "2"
////                MenuCombinado._tvLatitudClienteCaducado = tvLatitud
////                MenuCombinado._tvLongitudClienteCaducado = tvLongitud
//                startActivity(Intent(this, Mapa::class.java))
//            }
//            btLimpiar.setOnClickListener {
//                obtieneCodCliente(codVendedor)
//                etCodigo.setText("$codVendedor-$cod_cliente_comp")
//                limpiarTodo()
//            }
//            btCancelar.setOnClickListener { cancelarCatastro() }
//            btGuardar.setOnClickListener {
//                if (validaCampos()) {
//                    guardarCatastro()
//                }
//            }
//            btEnviar.setOnClickListener {
//                if (validaCampos()) {
//                    guardarCatastro()
//                    vCliente = ""
//                    codCliente = ""
//                    performBackgroundTask2().execute()
//                }
//            }
//            ibtFotoFachada.setOnClickListener {
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                val code = 1
//                name = Environment.getExternalStorageDirectory()
//                    .toString() + "/fachada.jpg"
//                val output = Uri.fromFile(File(name))
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, output)
//                startActivityForResult(intent, code)
//            }
//        }
//    }
//
//    var resultado: String = ""
//    lateinit var pbarDialog: ProgressDialog
//
//    private inner class performBackgroundTask2 :
//        AsyncTask<Void?, Void?, Void?>() {
//        override fun onPreExecute() {
//            try {
//                pbarDialog.dismiss()
//            } catch (e: Exception) {
//            }
//            pbarDialog = ProgressDialog.show(
//                this@CatastrarCliente, "Un momento...",
//                "Comprobando conexion", true
//            )
//        }
//
//        override fun doInBackground(vararg params: Void?): Void? {
//            return try {
////                resultado = MenuCombinado.cWS.onClickProcesaVersion()
//                resultado = "01*ENVIADO CON EXITO"
//                null
//            } catch (e: Exception) {
//                resultado = e.message.toString()
//                null
//            }
//        }
//
//        override fun onPostExecute(unused: Void?) {
//            pbarDialog.dismiss()
//            if (resultado != "null") {
//                try {
//                    generaClienteEnviar()
//                    return
//                } catch (e: Exception) {
//                    var err = e.message
//                    err = err + ""
//                }
//            } else {
//                generaClienteEnviar()
//                return
//            }
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Verifique su conexion a internet y vuelva a intentarlo",
//                Toast.LENGTH_SHORT
//            ).show()
//            return
//        }
//
//    }
//
//    //	GENERA STRING PARA ENVIAR AL WEB SERVICE
//    private fun generaClienteEnviar() {
//        var limit = 0
//        vCliente = "'1'|'" + codVendedor
//        vCliente += "'|'" + etCodigo.text.toString()
//        codCliente = etCodigo.text.toString()
//        limit = if (etRazonSocial.text.length < 100) { etRazonSocial.text.length } else { 100 }
//        vCliente += "'|'" + etRazonSocial.text.toString().substring(0, limit)
//        limit = if (etNombreFantasia.text.length < 100) { etNombreFantasia.text.length } else { 100 }
//        vCliente += "'|'" + etNombreFantasia.text.toString().substring(0, limit)
//        limit = if (etDireccionComercial.text.length < 100) { etDireccionComercial.text.length } else { 100 }
//        vCliente += "'|'" + etDireccionComercial.text.toString().substring(0, limit)
//        vCliente += "'|'$ult_cod_pais_dep"
//        vCliente += "'|'$ult_cod_dep"
//        vCliente += "'|'$ult_cod_ciudad"
//        limit = if (etBarrio.text.length < 100) { etBarrio.text.length } else { 100 }
//        vCliente += "'|'" + etBarrio.text.toString().substring(0, limit)
//        vCliente += "'|'" + etRUC.text.toString()
//        vCliente += "'|'" + etCI.text.toString()
//        vCliente += "'|'" + etCelular.text.toString()
//        vCliente += "'|'" + etLineaBaja.text.toString()
//        vCliente += "'|'$ultCodFormaPago"
//        vCliente += "'|'$ultCodTipoCliente"
//        vCliente += "'|'$ultCodDiasVisita"
//        limit = if (etCercaDe.text.length < 100) { etCercaDe.text.length } else { 100 }
//        vCliente += "'|'" + etCercaDe.text.toString().substring(0, limit)
//        limit = if (etEmail.text.length < 100) { etEmail.text.length } else { 100 }
//        vCliente += "'|'" + etEmail.text.toString().substring(0, limit)
//        vCliente += "'|'" + etLimiteCredito.text.toString()
//        vCliente += "'|'$ultCodListaPrecios"
//        limit = if (etNomRefComercial.text.length < 100) { etNomRefComercial.text.length } else { 100 }
//        vCliente += "'|'" + etNomRefComercial.text.toString().substring(0, limit)
//        limit = if (etTelRefComercial.text.length < 100) { etTelRefComercial.text.length } else { 100 }
//        vCliente += "'|'" + etTelRefComercial.text.toString().substring(0, limit)
//        limit = if (etNomRefBancaria.text.length < 100) { etNomRefBancaria.text.length } else { 100 }
//        vCliente += "'|'" + etNomRefBancaria.text.toString().substring(0, limit)
//        limit = if (etTelRefBancaria.text.length < 100) { etTelRefBancaria.text.length } else { 100 }
//        vCliente += "'|'" + etTelRefBancaria.text.toString().substring(0, limit)
//        limit = if (tvLatitud.text.length < 100) { tvLatitud.text.length } else { 100 }
//        vCliente += "'|'" + tvLatitud.text.toString().substring(0, limit)
//        limit = if (tvLongitud.text.length < 100) { tvLongitud.text.length } else { 100 }
//        vCliente += "'|'" + tvLongitud.text.toString().substring(0, limit)
//        limit = if (etComentario.text.length < 100) { etComentario.text.length } else { 100 }
//        vCliente += "'|'" + etComentario.text.toString().substring(0, limit) + "'"
//        back_enviar().execute()
//    }
//
//    //	PROCESO DE ENVIAR CLIENTE AL WEB SERVICE
//    private inner class back_enviar :
//        AsyncTask<Void?, Void?, Void?>() {
//        private var pbarDialog: ProgressDialog? = null
//        override fun onPreExecute() {
//            try {
//                pbarDialog!!.dismiss()
//            } catch (e: Exception) {
//            }
//            pbarDialog = ProgressDialog.show(
//                this@CatastrarCliente, "Un momento...",
//                "Enviando el catastro al servidor...", true
//            )
//        }
//
//        override fun doInBackground(vararg params: Void?): Void? {
//
////	    		respuestaWS = Aplicacion.cWS.onClickProcesaCatastroCliente(codCliente, vCliente);
//            respuestaWS = MenuCombinado.cWS.onClickProcesaCatastroClienteFinal(
//                codCliente,
//                vCliente,
//                imagen_fachada
//            )
//            return null
//        }
//
//        override fun onPostExecute(unused: Void?) {
//            pbarDialog!!.dismiss()
//            if (respuestaWS.indexOf("01*") >= 0 || respuestaWS.indexOf("03*") >= 0) {
//                val values = ContentValues()
//                values.put("ESTADO", "E")
//                MenuCombinado.bdatos.update(
//                    "svm_catastro_cliente",
//                    values,
//                    "COD_CLIENTE = '$cod_cliente'",
//                    null
//                )
//            }
//            if (respuestaWS.indexOf("Unable to resolve host") > -1) {
//                respuestaWS = "07*" + "Verifique su conexion a internet y vuelva a intentarlo"
//            }
//            val builder =
//                AlertDialog.Builder(this@CatastrarCliente)
//            builder.setMessage(respuestaWS.substring(3))
//                .setCancelable(false)
//                .setPositiveButton(
//                    "OK"
//                ) { dialog, id ->
//                    if (respuestaWS.indexOf("01*") >= 0 || respuestaWS.indexOf("03*") >= 0) {
//                        limpiarTodo()
//                        obtieneCodCliente()
//                        val codigo =
//                            findViewById<View>(R.id.Codigo) as EditText
//                        codigo.setText(
//                            MenuCombinado.et_login.getText().toString()
//                                .toString() + "-" + Integer.toString(cod_cliente_comp)
//                        )
//                    }
//                }
//            val alert = builder.create()
//            alert.show()
//        }
//    }
//
//    // ABRE LA VENTANA PARA BUSCAR CLIENTES CATASTRADOS
//    private fun consultarClientesCatastrados() {
//        try {
//            dialog_clientes_catastrados!!.dismiss()
//        } catch (e: Exception) {
//        }
//        dialog_clientes_catastrados = Dialog(this@CatastrarCliente)
//        dialog_clientes_catastrados!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        if (MenuCombinado._tip_tela === 1) {
//            dialog_clientes_catastrados.setContentView(R.layout.list_clientes_catastrados)
//        }
//        if (MenuCombinado._tip_tela === 2) {
//            dialog_clientes_catastrados.setContentView(R.layout.list_clientes_catastrados2)
//        }
//        if (MenuCombinado._tip_tela === 4) {
//            dialog_clientes_catastrados.setContentView(R.layout.list_clientes_catastrados4)
//        }
//        if (MenuCombinado._tip_tela === 5) {
//            dialog_clientes_catastrados.setContentView(R.layout.list_clientes_catastrados5)
//        }
//        list_view_clientes_catastrados =
//            dialog_clientes_catastrados!!.findViewById<View>(R.id.lvdclientes) as ListView
//        val btBuscar =
//            dialog_clientes_catastrados!!.findViewById<View>(R.id.bt_busca_cliente) as Button
//        btBuscar.setOnClickListener {
//            save_clientes_catastrados = -1
//            busca_cliente_catastrado()
//        }
//        val btModificar =
//            dialog_clientes_catastrados!!.findViewById<View>(R.id.btModificar) as Button
//        btModificar.setOnClickListener {
//            if (save_clientes_catastrados != -1) {
//                if (estado_cliente_catastrado[save_clientes_catastrados] == "P") {
//                    consulta = true
//                    cargar_datos_cliente(
//                        cod_cliente_composicion[save_clientes_catastrados],
//                        true
//                    )
//                    dialog_clientes_catastrados!!.dismiss()
//                    save_clientes_catastrados = -1
//                }
//            }
//        }
//        val btConsultar =
//            dialog_clientes_catastrados!!.findViewById<View>(R.id.btConsultar) as Button
//        btConsultar.setOnClickListener {
//            if (save_clientes_catastrados != -1) {
//                consulta = true
//                cargar_datos_cliente(
//                    cod_cliente_composicion[save_clientes_catastrados],
//                    false
//                )
//                dialog_clientes_catastrados!!.dismiss()
//                save_clientes_catastrados = -1
//            }
//        }
//        val btCancelar =
//            dialog_clientes_catastrados!!.findViewById<View>(R.id.btEliminar) as Button
//        btCancelar.setOnClickListener {
//            if (save_clientes_catastrados != -1) {
//                if (estado_cliente_catastrado[save_clientes_catastrados] == "P") {
//                    val myAlertDialog =
//                        AlertDialog.Builder(this@CatastrarCliente)
//                    myAlertDialog.setMessage("¿Desea cancelar el catastro?")
//                    myAlertDialog.setPositiveButton(
//                        "Si"
//                    ) { arg0, arg1 ->
//                        try {
//                            MenuCombinado.bdatos.delete(
//                                "svm_catastro_cliente",
//                                "COD_CLIENTE = '" +
//                                        cod_cliente_composicion[save_clientes_catastrados] + "'",
//                                null
//                            )
//                            save_clientes_catastrados = -1
//                            busca_cliente_catastrado()
//                            Toast.makeText(
//                                applicationContext,
//                                "Catastro cancelado con exito...",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } catch (e: Exception) {
//                            Toast.makeText(
//                                applicationContext,
//                                "ERROR al CANCELAR",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                    myAlertDialog.setNegativeButton(
//                        "No"
//                    ) { arg0, arg1 -> }
//                    myAlertDialog.show()
//                }
//            }
//        }
//        fec_desde =
//            dialog_clientes_catastrados!!.findViewById<View>(R.id.fec_desde) as EditText
//        fec_hasta =
//            dialog_clientes_catastrados!!.findViewById<View>(R.id.fec_hasta) as EditText
//        val sdf = SimpleDateFormat("dd/MM/yyyy")
//        fec_desde!!.text = sdf.format(Date())
//        fec_hasta!!.text = sdf.format(Date())
//        fec_desde!!.setOnClickListener {
//            val c = Calendar.getInstance()
//            mYear = c[Calendar.YEAR]
//            mMonth = c[Calendar.MONTH]
//            mDay = c[Calendar.DAY_OF_MONTH]
//            showDialog(DATE_DIALOG_ID)
//            fecha = 0
//            fecha_desde()
//        }
//        fec_hasta!!.setOnClickListener {
//            val c = Calendar.getInstance()
//            mYear = c[Calendar.YEAR]
//            mMonth = c[Calendar.MONTH]
//            mDay = c[Calendar.DAY_OF_MONTH]
//            showDialog(DATE_DIALOG_ID)
//            fecha = 1
//            fecha_hasta()
//        }
//        dialog_clientes_catastrados!!.show()
//    }
//
//    //	CONTROLA CALENDARIO Y FECHA
//    override fun onCreateDialog(id: Int): Dialog {
//        when (id) {
//            DATE_DIALOG_ID -> return DatePickerDialog(
//                this,
//                mDateSetListener,
//                mYear, mMonth, mDay
//            )
//        }
//        return null
//    }
//
//    override fun onPrepareDialog(id: Int, dialog: Dialog) {
//        when (id) {
//            DATE_DIALOG_ID -> (dialog as DatePickerDialog).updateDate(
//                mYear,
//                mMonth,
//                mDay
//            )
//        }
//    }
//
//    private val mDateSetListener =
//        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//            mYear = year
//            mMonth = monthOfYear
//            mDay = dayOfMonth
//            if (fecha == 0) {
//                fecha_desde()
//            } else {
//                fecha_hasta()
//            }
//        }
//
//    private fun fecha_desde() {
//        val mes: String
//        mMonth = mMonth + 1
//        mes = if (mMonth <= 9) {
//            "0" + StringBuilder().append(mMonth)
//        } else {
//            "" + StringBuilder().append(mMonth)
//        }
//        val dia: String
//        dia = if (mDay <= 9) {
//            "0" + StringBuilder().append(mDay)
//        } else {
//            "" + StringBuilder().append(mDay)
//        }
//        fec_desde!!.text = StringBuilder()
//            .append(dia).append("/")
//            .append(mes).append("/")
//            .append(mYear).append(" ")
//    }
//
//    private fun fecha_hasta() {
//        val mes: String
//        mMonth = mMonth + 1
//        mes = if (mMonth <= 9) {
//            "0" + StringBuilder().append(mMonth)
//        } else {
//            "" + StringBuilder().append(mMonth)
//        }
//        val dia: String
//        dia = if (mDay <= 9) {
//            "0" + StringBuilder().append(mDay)
//        } else {
//            "" + StringBuilder().append(mDay)
//        }
//        fec_hasta!!.text = StringBuilder()
//            .append(dia).append("/")
//            .append(mes).append("/")
//            .append(mYear).append(" ")
//    }
//
//    //	OBTIENE DATOS DEL CLIENTE SELECCIONADO
//    private fun cargarDatosCliente(codCliente: String?, editable: Boolean) {
//        val cursor: Cursor
//        val sql =
//            ("select a.COD_CLIENTE  , a.RAZONSOCIAL		, a.NOM_FANTASIA   , a.DIR_COMERCIAL		, "
//                    + " a.CERCA_DE           , a.COD_DEPARTAMENTO, a.COD_PAIS   	   , a.COD_CIUDAD    	, "
//                    + " a.BARRIO       		, a.RUC             , a.CI         	   , a.CELULAR			, "
//                    + " a.LINEA_BAJA       	, a.EMAIL			, a.CANAL_SUGERIDO , f.DESC_LISTA_PRECIO, "
//                    + " a.COD_CONDICION_VENTA, a.codTipoCliente, a.codDiasVisita, b.DESC_DEPARTAMENTO  , "
//                    + " b.DESC_CIUDAD        , c.DESCRIPCION  	, d.DESC_CANAL_VENTA as descTipoCliente 	, "
//                    + " e.DESCRIPCION as descDiasVisita		, a.NOM_REF_COMERCIAL, a.TEL_REF_COMERCIAL	, "
//                    + " a.NOM_REF_BANCARIA		, a.TEL_REF_BANCARIA	, a.COMENTARIO       , a.LIMITE_CREDITO		,"
//                    + " a.LATITUD				, a.LONGITUD , a.FOTO_FACHADA  "
//                    + " from svm_catastro_cliente a, svm_ciudades b	, svm_condicion_venta_cliente c, "
//                    + "svm_tipo_cliente d    , svm_dias_visita e, svm_precios_fijos f"
//                    + " where a.COD_CLIENTE = '" + codCliente + "'"
//                    + " and a.COD_PAIS = b.COD_PAIS 	  and a.COD_DEPARTAMENTO = b.COD_DEPARTAMENTO "
//                    + " and a.COD_CIUDAD = b.COD_CIUDAD   and a.COD_CONDICION_VENTA = c.COD_CONDICION_VENTA"
//                    + " and a.codTipoCliente = d.COD_SUBTIPO and a.codDiasVisita = e.CODIGO "
//                    + " and a.CANAL_SUGERIDO = f.COD_LISTA_PRECIO")
//        cursor = funcion.consultar(sql)
//        etRazonSocial.setText(funcion.dato(cursor,"RAZONSOCIAL"))
//        etNombreFantasia.setText(funcion.dato(cursor,"NOM_FANTASIA"))
//        etDireccionComercial.setText(funcion.dato(cursor,"DIR_COMERCIAL"))
//        etCercaDe.setText(funcion.dato(cursor,"CERCA_DE"))
//        etDepartamento.setText(funcion.dato(cursor,"COD_DEPARTAMENTO") + " - " + funcion.dato(cursor,"DESC_DEPARTAMENTO"))
//        etCiudad.setText(funcion.dato(cursor,"COD_CIUDAD") + " - " + funcion.dato(cursor,"DESC_CIUDAD"))
//        etBarrio.setText(funcion.dato(cursor,"BARRIO"))
//        etRUC.setText(funcion.dato(cursor,"RUC"))
//        etCI.setText(funcion.dato(cursor,"CI"))
//        etCelular.setText(funcion.dato(cursor,"CELULAR"))
//        etLineaBaja.setText(funcion.dato(cursor,"LINEA_BAJA"))
//        etEmail.setText(funcion.dato(cursor,"EMAIL"))
//        etLimiteCredito.setText(funcion.dato(cursor,"LIMITE_CREDITO"))
//        etListaPrecio.setText(funcion.dato(cursor,"CANAL_SUGERIDO") + " - " + funcion.dato(cursor,"DESC_LISTA_PRECIO"))
//        etFormaPago.setText(funcion.dato(cursor,"COD_CONDICION_VENTA") + " - " + funcion.dato(cursor,"DESCRIPCION"))
//        etTipoCliente.setText(funcion.dato(cursor,"codTipoCliente") + " - " + funcion.dato(cursor,"descTipoCliente"))
//        etDiasVisita.setText(funcion.dato(cursor,"codDiasVisita") + " - " + funcion.dato(cursor,"descDiasVisita"))
//        etNomRefComercial.setText(funcion.dato(cursor,"NOM_REF_COMERCIAL"))
//        etTelRefComercial.setText(funcion.dato(cursor,"TEL_REF_COMERCIAL"))
//        etNomRefBancaria.setText(funcion.dato(cursor,"NOM_REF_BANCARIA")))
//        etTelRefBancaria.setText(funcion.dato(cursor,"TEL_REF_BANCARIA")))
//        etComentario.setText(funcion.dato(cursor,"COMENTARIO")))
//        tvLatitud.text = funcion.dato(cursor,"LATITUD"))
//        tvLongitud.text = funcion.dato(cursor,"LONGITUD"))
//        etCodigo.setText(funcion.dato(cursor,"COD_CLIENTE")))
//        ultCodListaPrecios = funcion.dato(cursor,"CANAL_SUGERIDO"))
//        ult_cod_ciudad = funcion.dato(cursor,"COD_CIUDAD"))
//        ult_cod_dep = funcion.dato(cursor,"COD_DEPARTAMENTO"))
//        ult_cod_pais_dep = funcion.dato(cursor,"COD_PAIS"))
//        ultCodFormaPago =
//            funcion.dato(cursor,"COD_CONDICION_VENTA"))
//        ultCodDiasVisita = funcion.dato(cursor,"codDiasVisita"))
//        ultCodTipoCliente =
//            funcion.dato(cursor,"codTipoCliente"))
//        imagen_fachada = funcion.dato(cursor,"FOTO_FACHADA"))
//        if (imagen_fachada != null) {
//            try {
//                val img: ByteArray = MenuCombinado.utilidades.stringToByte2(imagen_fachada)
//                val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
//                (findViewById<View>(R.id.ivFachada) as ImageView).setImageBitmap(
//                    bitmap
//                )
//            } catch (e2: Exception) {
//                var err = e2.message
//                err = err + ""
//            }
//        }
//        val btEnviar =
//            findViewById<View>(R.id.btEnviar) as Button
//        btEnviar.isEnabled = editable
//        val btGuardar =
//            findViewById<View>(R.id.btGuardar) as Button
//        btGuardar.isEnabled = editable
//        val btCancelar =
//            findViewById<View>(R.id.btCancelar) as Button
//        btCancelar.isEnabled = editable
//    }
//
//    //	EVENTO AL PRESIONAR BUSCAR
//    private fun busca_cliente_catastrado() {
//        alist_cliente_catastrado =
//            ArrayList()
//        try {
//            val radio0: RadioButton
//            val radio1: RadioButton
//            val radio2: RadioButton
//            radio0 =
//                dialog_clientes_catastrados!!.findViewById<View>(R.id.radio0) as RadioButton
//            radio1 =
//                dialog_clientes_catastrados!!.findViewById<View>(R.id.radio1) as RadioButton
//            radio2 =
//                dialog_clientes_catastrados!!.findViewById<View>(R.id.radio2) as RadioButton
//            var filterEstado = ""
//            if (radio0.isChecked == true) {
//                filterEstado = " = 'P'"
//            } else if (radio1.isChecked == true) {
//                filterEstado = " = 'E'"
//            } else if (radio2.isChecked == true) {
//                filterEstado = " <> 'X'"
//            }
//            var desde: String? = null
//            var hasta: String? = null
//            desde =
//                MenuCombinado.utilidades.convertirFechatoSQLFormat(fec_desde!!.text.toString())
//            hasta =
//                MenuCombinado.utilidades.convertirFechatoSQLFormat(fec_hasta!!.text.toString())
//            val select = ("Select COD_CLIENTE, RAZONSOCIAL, NOM_FANTASIA,"
//                    + "RUC        , CI         , ESTADO  "
//                    + " from svm_catastro_cliente "
//                    + " where ESTADO " + filterEstado
//                    + " and    date(FEC_ALTA)  "
//                    + " BETWEEN  date('" + desde + "') AND date('" + hasta + "')"
//                    + " Order By date(FEC_ALTA) DESC")
//            cursor_clientes_catastrados_buscar = MenuCombinado.bdatos.rawQuery(select, null)
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        val nreg = cursor_clientes_catastrados_buscar!!.count
//        save_clientes_catastrados = if (nreg > 0) {
//            0
//        } else {
//            -1
//        }
//        cod_cliente_composicion = arrayOfNulls(nreg)
//        estado_cliente_catastrado = arrayOfNulls(nreg)
//        cursor_clientes_catastrados_buscar!!.moveToFirst()
//        var cont = 0
//        for (i in 0 until nreg) {
//            val map2 =
//                HashMap<String, String>()
//            map2["COD_CLIENTE"] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("COD_CLIENTE")
//            )
//            cod_cliente_composicion[cont] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("COD_CLIENTE")
//            )
//            map2["RAZONSOCIAL"] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("RAZONSOCIAL")
//            )
//            map2["NOM_FANTASIA"] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("NOM_FANTASIA")
//            )
//            map2["RUC"] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("RUC")
//            )
//            map2["CI"] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("CI")
//            )
//            map2["ESTADO"] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("ESTADO")
//            )
//            estado_cliente_catastrado[cont] = cursor_clientes_catastrados_buscar!!.getString(
//                cursor_clientes_catastrados_buscar
//                    .getColumnIndex("ESTADO")
//            )
//            alist_cliente_catastrado.add(map2)
//            cursor_clientes_catastrados_buscar!!.moveToNext()
//            cont = cont + 1
//        }
//        sd6 = Adapter_lista_catastro_cliente(
//            this@CatastrarCliente, alist_cliente_catastrado,
//            R.layout.list_text_clientes_catastrados, arrayOf<String>(
//                "COD_CLIENTE", "RAZONSOCIAL", "NOM_FANTASIA",
//                "RUC", "CI", "ESTADO"
//            ), intArrayOf(
//                R.id.td1, R.id.td2, R.id.td3,
//                R.id.td4, R.id.td5, R.id.td6
//            )
//        )
//        list_view_clientes_catastrados!!.adapter = sd6
//        list_view_clientes_catastrados!!.onItemClickListener =
//            OnItemClickListener { parent, v, position, id ->
//                save_clientes_catastrados = position
//                list_view_clientes_catastrados!!.invalidateViews()
//            }
//    }
//
//    inner class Adapter_lista_catastro_cliente(
//        this: Context?,
//        items: List<HashMap<String?, String?>?>?,
//        resource: Int,
//        from: Array<String?>?,
//        to: IntArray?
//    ) :
//        SimpleAdapter(this, items, resource, from, to) {
//        var _sqlupdate: String? = null
//        private val colors = intArrayOf(
//            Color.parseColor("#696969"),
//            Color.parseColor("#808080")
//        )
//
//        inner class ViewHolder
//
//        override fun getView(
//            position: Int,
//            convertView: View,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val colorPos = position % colors.size
//            view.setBackgroundColor(colors[colorPos])
//            val holder =
//                ViewHolder()
//            if (save_clientes_catastrados == position) {
//                view.setBackgroundColor(Color.BLUE)
//            }
//            view.tag = holder
//            return view
//        }
//    }
//
//    //	FUNCION QUE VALIDA QUE TODOS LOS CAMPOS ESTEN CARGADOS
//    private fun validaCampos(): Boolean {
//        var tv = findViewById<View>(R.id.etRazonSocial) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 50) {
//                tv.setText(tv.text.toString().substring(0, 50))
//            }
//        }
//        tv = findViewById<View>(R.id.etNombreFantasia) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 50) {
//                tv.setText(tv.text.toString().substring(0, 50))
//            }
//        }
//        tv = findViewById<View>(R.id.etDireccionComercial) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 150) {
//                tv.setText(tv.text.toString().substring(0, 150))
//            }
//        }
//        tv = findViewById<View>(R.id.etCercaDe) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 100) {
//                tv.setText(tv.text.toString().substring(0, 100))
//            }
//        }
//        tv = findViewById<View>(R.id.etDepartamento) as EditText
//        if (tv.text.toString() == "" || ult_cod_pais_dep == "" || ult_cod_dep == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        tv = findViewById<View>(R.id.etCiudad) as EditText
//        if (tv.text.toString() == "" || ult_cod_ciudad == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        tv = findViewById<View>(R.id.etBarrio) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 100) {
//                tv.setText(tv.text.toString().substring(0, 100))
//            }
//        }
//        tv = findViewById<View>(R.id.etRUC) as EditText
//        if (tv.text.toString() == "") {
//            tv = findViewById<View>(R.id.etCI) as EditText
//            if (tv.text.toString() == "") {
//                Toast.makeText(
//                    this@CatastrarCliente,
//                    "Debe completar todos los campos",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return false
//            }
//        } else {
//            if (tv.text.toString().length > 15) {
//                tv.setText(tv.text.toString().substring(0, 15))
//            }
//        }
//        tv = findViewById<View>(R.id.etCI) as EditText
//        if (tv.text.toString() != "") {
//            if (tv.text.toString().length > 15) {
//                tv.setText(tv.text.toString().substring(0, 15))
//            }
//        }
//        tv = findViewById<View>(R.id.etCelular) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 15) {
//                tv.setText(tv.text.toString().substring(0, 15))
//            }
//        }
//        tv = findViewById<View>(R.id.etLineaBaja) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 15) {
//                tv.setText(tv.text.toString().substring(0, 15))
//            }
//        }
//        tv = findViewById<View>(R.id.Email) as EditText
//        if (tv.text.toString() != "") {
//            if (tv.text.toString().length > 30) {
//                tv.setText(tv.text.toString().substring(0, 30))
//            }
//        }
//        tv = findViewById<View>(R.id.LimiteCredito) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        } else {
//            if (tv.text.toString().length > 17) {
//                tv.setText(tv.text.toString().substring(0, 17))
//            }
//        }
//        tv = findViewById<View>(R.id.Canal) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        tv = findViewById<View>(R.id.etFormaPago) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        tv = findViewById<View>(R.id.etTipoCliente) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        tv = findViewById<View>(R.id.etDiasVisita) as EditText
//        if (tv.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        var tv2 = findViewById<View>(R.id.tvLatitud) as TextView
//        if (tv2.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        tv2 = findViewById<View>(R.id.tvLongitud) as TextView
//        if (tv2.text.toString() == "") {
//            Toast.makeText(
//                this@CatastrarCliente,
//                "Debe completar todos los campos",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//        tv = findViewById<View>(R.id.nom_ref_comercial) as EditText
//        if (tv.text.toString() != "") {
//            if (tv.text.toString().length > 50) {
//                tv.setText(tv.text.toString().substring(0, 50))
//            }
//            tv = findViewById<View>(R.id.etTelRefComercial) as EditText
//            if (tv.text.toString() == "") {
//                Toast.makeText(
//                    this@CatastrarCliente,
//                    "Debe completar todos los campos",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return false
//            }
//        }
//        tv = findViewById<View>(R.id.etTelRefComercial) as EditText
//        if (tv.text.toString() != "") {
//            if (tv.text.toString().length > 50) {
//                tv.setText(tv.text.toString().substring(0, 50))
//            }
//            tv = findViewById<View>(R.id.nom_ref_comercial) as EditText
//            if (tv.text.toString() == "") {
//                Toast.makeText(
//                    this@CatastrarCliente,
//                    "Debe completar todos los campos",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return false
//            }
//        }
//        tv = findViewById<View>(R.id.nom_ref_bancaria) as EditText
//        if (tv.text.toString() != "") {
//            if (tv.text.toString().length > 50) {
//                tv.setText(tv.text.toString().substring(0, 50))
//            }
//            tv = findViewById<View>(R.id.etTelRefBancaria) as EditText
//            if (tv.text.toString() == "") {
//                Toast.makeText(
//                    this@CatastrarCliente,
//                    "Debe completar todos los campos",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return false
//            }
//        }
//        tv = findViewById<View>(R.id.etTelRefBancaria) as EditText
//        if (tv.text.toString() != "") {
//            if (tv.text.toString().length > 50) {
//                tv.setText(tv.text.toString().substring(0, 50))
//            }
//            tv = findViewById<View>(R.id.nom_ref_bancaria) as EditText
//            if (tv.text.toString() == "") {
//                Toast.makeText(
//                    this@CatastrarCliente,
//                    "Debe completar todos los campos",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return false
//            }
//        }
//        tv = findViewById<View>(R.id.Comentario) as EditText
//        if (tv.text.toString() != "") {
//            if (tv.text.toString().length > 200) {
//                tv.setText(tv.text.toString().substring(0, 200))
//            }
//        }
//        if (imagen_fachada == null) {
//            Toast.makeText(this@CatastrarCliente, "Debe tomar foto de fachada", Toast.LENGTH_SHORT)
//                .show()
//            return false
//        }
//        return true
//    }
//
//    //	GUARDAR CATASTRO LOCALMENTE
//    private fun guardarCatastro() {
//        val values = ContentValues()
//        values.put("COD_VENDEDOR", MenuCombinado.et_login.getText().toString())
//        values.put("NOM_VENDEDOR", MenuCombinado.n_user.getText().toString())
//        var tv = findViewById<View>(R.id.etRazonSocial) as EditText
//        values.put("RAZONSOCIAL", tv.text.toString())
//        tv = findViewById<View>(R.id.etNombreFantasia) as EditText
//        values.put("NOM_FANTASIA", tv.text.toString())
//        tv = findViewById<View>(R.id.etDireccionComercial) as EditText
//        values.put("DIR_COMERCIAL", tv.text.toString())
//        tv = findViewById<View>(R.id.etCercaDe) as EditText
//        values.put("CERCA_DE", tv.text.toString())
//        values.put("COD_PAIS", ult_cod_pais_dep)
//        tv = findViewById<View>(R.id.etDepartamento) as EditText
//        values.put("COD_DEPARTAMENTO", ult_cod_dep)
//        tv = findViewById<View>(R.id.etCiudad) as EditText
//        values.put("COD_CIUDAD", ult_cod_ciudad)
//        tv = findViewById<View>(R.id.etBarrio) as EditText
//        values.put("BARRIO", tv.text.toString())
//        tv = findViewById<View>(R.id.etRUC) as EditText
//        values.put("RUC", tv.text.toString())
//        tv = findViewById<View>(R.id.etCI) as EditText
//        values.put("CI", tv.text.toString())
//        tv = findViewById<View>(R.id.etCelular) as EditText
//        values.put("CELULAR", tv.text.toString())
//        tv = findViewById<View>(R.id.etLineaBaja) as EditText
//        values.put("LINEA_BAJA", tv.text.toString())
//        tv = findViewById<View>(R.id.Email) as EditText
//        values.put("EMAIL", tv.text.toString())
//        tv = findViewById<View>(R.id.LimiteCredito) as EditText
//        values.put("LIMITE_CREDITO", tv.text.toString())
//        tv = findViewById<View>(R.id.Canal) as EditText
//        values.put("CANAL_SUGERIDO", ult_cod_lista_precios)
//        tv = findViewById<View>(R.id.etFormaPago) as EditText
//        values.put("COD_CONDICION_VENTA", ultCodFormaPago)
//        tv = findViewById<View>(R.id.etTipoCliente) as EditText
//        values.put("codTipoCliente", ultCodTipoCliente)
//        tv = findViewById<View>(R.id.etDiasVisita) as EditText
//        values.put("codDiasVisita", ultCodDiasVisita)
//        tv = findViewById<View>(R.id.nom_ref_comercial) as EditText
//        values.put("NOM_REF_COMERCIAL", tv.text.toString())
//        tv = findViewById<View>(R.id.etTelRefComercial) as EditText
//        values.put("TEL_REF_COMERCIAL", tv.text.toString())
//        tv = findViewById<View>(R.id.nom_ref_bancaria) as EditText
//        values.put("NOM_REF_BANCARIA", tv.text.toString())
//        tv = findViewById<View>(R.id.etTelRefBancaria) as EditText
//        values.put("TEL_REF_BANCARIA", tv.text.toString())
//        tv = findViewById<View>(R.id.Comentario) as EditText
//        values.put("COMENTARIO", tv.text.toString())
//        var tv2 = findViewById<View>(R.id.tvLatitud) as TextView
//        values.put("LATITUD", tv2.text.toString())
//        tv2 = findViewById<View>(R.id.tvLongitud) as TextView
//        values.put("LONGITUD", tv2.text.toString())
//        tv = findViewById<View>(R.id.Codigo) as EditText
//        values.put("COD_CLIENTE", tv.text.toString())
//        val cant =
//            "Select * from svm_catastro_cliente where codCliente = '" + tv.text
//                .toString() + "'"
//        val cursor: Cursor = MenuCombinado.bdatos.rawQuery(cant, null)
//        cursor.moveToFirst()
//        if (cursor.count == 0) {
//            var d1: String? = null
//            val cal = Calendar.getInstance()
//            val dfDate = SimpleDateFormat("yyyy-MM-dd")
//            d1 = dfDate.format(cal.time)
//            values.put("FEC_ALTA", d1)
//            values.put("ESTADO", "P")
//            values.put("COD_CLI_VEND", cod_cliente_comp)
//            values.put("FOTO_FACHADA", imagen_fachada)
//            MenuCombinado.bdatos.insert(
//                "svm_catastro_cliente", null,
//                values
//            )
//        } else {
//            values.put("FOTO_FACHADA", imagen_fachada)
//            MenuCombinado.bdatos.update(
//                "svm_catastro_cliente",
//                values,
//                "COD_CLIENTE = '" + tv.text.toString() + "'",
//                null
//            )
//        }
//        Toast.makeText(this@CatastrarCliente, "Catastro guardado con exito", Toast.LENGTH_SHORT)
//            .show()
//    }
//
//    //	ELIMINA EL CATASTRO ACTUAL
//    private fun cancelarCatastro() {
//        val myAlertDialog =
//            AlertDialog.Builder(this@CatastrarCliente)
//        myAlertDialog.setMessage("¿Desea cancelar el catastro?")
//        myAlertDialog.setPositiveButton(
//            "Si"
//        ) { arg0, arg1 ->
//            try {
//                val codigo = findViewById<View>(R.id.Codigo) as EditText
//                MenuCombinado.bdatos.delete(
//                    "svm_catastro_cliente",
//                    "COD_CLIENTE = '" + codigo.text + "'",
//                    null
//                )
//                finish()
//                Toast.makeText(
//                    applicationContext,
//                    "Catastro cancelado con exito...",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } catch (e: Exception) {
//                Toast.makeText(applicationContext, "ERROR al CANCELAR", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//        myAlertDialog.setNegativeButton(
//            "No"
//        ) { arg0, arg1 -> }
//        myAlertDialog.show()
//    }
//
//    //	LIMPIA TODOS LOS CAMPOS
//    private fun limpiarTodo() {
//        consulta = false
//        var tv = findViewById<View>(R.id.etRazonSocial) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etNombreFantasia) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etDireccionComercial) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etCercaDe) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etDepartamento) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etCiudad) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etBarrio) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etRUC) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etCI) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etCelular) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etLineaBaja) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.Email) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.LimiteCredito) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.Canal) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etFormaPago) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etTipoCliente) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etDiasVisita) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.nom_ref_comercial) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etTelRefComercial) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.nom_ref_bancaria) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.etTelRefBancaria) as EditText
//        tv.setText("")
//        tv = findViewById<View>(R.id.Comentario) as EditText
//        tv.setText("")
//        var tv2 = findViewById<View>(R.id.tvLatitud) as TextView
//        tv2.text = ""
//        tv2 = findViewById<View>(R.id.tvLongitud) as TextView
//        tv2.text = ""
//        ult_cod_ciudad = ""
//        ult_cod_dep = ""
//        ult_cod_pais_dep = ""
//        ultCodFormaPago = ""
//        ultCodDiasVisita = ""
//        ultCodTipoCliente = ""
//        ult_cod_lista_precios = ""
//        imagen_fachada = null
//        val btEnviar =
//            findViewById<View>(R.id.btEnviar) as Button
//        btEnviar.isEnabled = true
//        val btGuardar =
//            findViewById<View>(R.id.btGuardar) as Button
//        btGuardar.isEnabled = true
//        val btCancelar =
//            findViewById<View>(R.id.btCancelar) as Button
//        btCancelar.isEnabled = true
//    }
//
//    //	MUESTRA LA LISTA DE CONDICIONES DE VENTA
//    private fun trae_formas_pago(etFP: EditText) {
//        try {
//            dialogoFormaPago!!.dismiss()
//        } catch (e: Exception) {
//        }
//        if (posicionFormaPago == -1) {
//            return
//        }
//        dialogoFormaPago = Dialog(this)
//        dialogoFormaPago!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        if (MenuCombinado._tip_tela === 1) {
//            dialogoFormaPago.setContentView(R.layout.list_catastro_cliente_items)
//        }
//        if (MenuCombinado._tip_tela === 2) {
//            dialogoFormaPago.setContentView(R.layout.list_catastro_cliente_items2)
//        }
//        if (MenuCombinado._tip_tela === 4) {
//            dialogoFormaPago.setContentView(R.layout.list_catastro_cliente_items4)
//        }
//        if (MenuCombinado._tip_tela === 5) {
//            dialogoFormaPago.setContentView(R.layout.list_catastro_cliente_items5)
//        }
//        val tvTitulo =
//            dialogoFormaPago!!.findViewById<View>(R.id.tvTitulo) as TextView
//        tvTitulo.text = "FORMAS DE PAGO"
//        lvFormaPago =
//            dialogoFormaPago!!.findViewById<View>(R.id.lvdet_catastro_cliente) as ListView
//        trae_lista_forma_pago()
//        val btSeleccionar =
//            dialogoFormaPago!!.findViewById<View>(R.id.btSeleccionar) as Button
//        btSeleccionar.setOnClickListener {
//            ultCodFormaPago = if (posicionFormaPago != -1) {
//                etFP.setText(
//                    codFormaPago[posicionFormaPago]
//                        .toString() + " - " + descFormaPago[posicionFormaPago]
//                )
//                codFormaPago[posicionFormaPago]
//            } else {
//                etFP.setText("")
//                ""
//            }
//            dialogoFormaPago!!.dismiss()
//            posicionFormaPago = 0
//        }
//        val filtrar =
//            dialogoFormaPago!!.findViewById<View>(R.id.btFiltrar) as Button
//        filtrar.setOnClickListener { trae_lista_forma_pago() }
//        val etSearch =
//            dialogoFormaPago!!.findViewById<View>(R.id.tvSearch) as EditText
//        val teclado1 = TecladoAlfaNumerico()
//        etSearch.setOnClickListener { teclado1.showTecAlfNum(etSearch, this@CatastrarCliente) }
//        dialogoFormaPago!!.show()
//    }
//
//    private fun trae_lista_forma_pago() {
//        listaFormaPago = ArrayList()
//        val etSearch =
//            dialogoFormaPago!!.findViewById<View>(R.id.tvSearch) as EditText
//        try {
//            val _where =
//                ("(COD_CONDICION_VENTA LIKE '%" + etSearch.text.toString() + "%' "
//                        + "  or DESCRIPCION LIKE '%" + etSearch.text.toString() + "%')")
//            val sel = ("select COD_CONDICION_VENTA AS CODIGO, DESCRIPCION "
//                    + " from svm_condicion_venta_cliente "
//                    + " Where " + _where
//                    + " Group By COD_CONDICION_VENTA "
//                    + " Order by DESCRIPCION, Cast(COD_CONDICION_VENTA as double)")
//            cursorFormaPago = MenuCombinado.bdatos.rawQuery(sel, null)
//            val btVolver =
//                dialogoFormaPago!!.findViewById<View>(R.id.btn_volver) as Button
//            btVolver.setOnClickListener {
//                try {
//                    dialogoFormaPago!!.dismiss()
//                } catch (e: Exception) {
//                }
//            }
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        val nreg = cursorFormaPago!!.count
//        posicionFormaPago = if (nreg < 1) {
//            -1
//        } else {
//            0
//        }
//        codFormaPago = arrayOfNulls(nreg)
//        descFormaPago = arrayOfNulls(nreg)
//        cursorFormaPago!!.moveToFirst()
//        var cont = 0
//        for (i in 0 until nreg) {
//            val map2 =
//                HashMap<String, String>()
//            map2["CODIGO"] = cursorFormaPago!!.getString(
//                cursorFormaPago
//                    .getColumnIndex("CODIGO")
//            )
//            codFormaPago[cont] = cursorFormaPago!!.getString(
//                cursorFormaPago
//                    .getColumnIndex("CODIGO")
//            )
//            map2["DESCRIPCION"] = cursorFormaPago!!.getString(
//                cursorFormaPago
//                    .getColumnIndex("DESCRIPCION")
//            )
//            descFormaPago[cont] = cursorFormaPago!!.getString(
//                cursorFormaPago
//                    .getColumnIndex("DESCRIPCION")
//            )
//            listaFormaPago.add(map2)
//            cursorFormaPago!!.moveToNext()
//            cont = cont + 1
//        }
//        sd = Adapter_lista_forma_pago(
//            this@CatastrarCliente, listaFormaPago,
//            R.layout.list_text_catastro_cliente_items, arrayOf<String>(
//                "CODIGO", "DESCRIPCION"
//            ), intArrayOf(R.id.td1, R.id.td2)
//        )
//        lvFormaPago!!.adapter = sd
//        lvFormaPago!!.onItemClickListener =
//            OnItemClickListener { parent, v, position, id ->
//                posicionFormaPago = position
//                lvFormaPago!!.invalidateViews()
//            }
//    }
//
//    inner class Adapter_lista_forma_pago(
//        this: Context?,
//        items: List<HashMap<String?, String?>?>?,
//        resource: Int,
//        from: Array<String?>?,
//        to: IntArray?
//    ) :
//        SimpleAdapter(this, items, resource, from, to) {
//        var _sqlupdate: String? = null
//        private val colors = intArrayOf(
//            Color.parseColor("#696969"),
//            Color.parseColor("#808080")
//        )
//
//        inner class ViewHolder
//
//        override fun getView(
//            position: Int,
//            convertView: View,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val colorPos = position % colors.size
//            view.setBackgroundColor(colors[colorPos])
//            val holder =
//                ViewHolder()
//            if (posicionFormaPago == position) {
//                view.setBackgroundColor(Color.BLUE)
//            }
//            view.tag = holder
//            return view
//        }
//    }
//
//    //	MUESTRA LA LISTA DE TIPO DE CLIENTE
//    private fun trae_tipo_cliente(etTC: EditText) {
//        try {
//            dialogoTipoCliente!!.dismiss()
//        } catch (e: Exception) {
//        }
//        if (posicionTipoCliente == -1) {
//            return
//        }
//        dialogoTipoCliente = Dialog(this)
//        dialogoTipoCliente!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        if (MenuCombinado._tip_tela === 1) {
//            dialogoTipoCliente.setContentView(R.layout.list_catastro_cliente_items)
//        }
//        if (MenuCombinado._tip_tela === 2) {
//            dialogoTipoCliente.setContentView(R.layout.list_catastro_cliente_items2)
//        }
//        if (MenuCombinado._tip_tela === 4) {
//            dialogoTipoCliente.setContentView(R.layout.list_catastro_cliente_items4)
//        }
//        if (MenuCombinado._tip_tela === 5) {
//            dialogoTipoCliente.setContentView(R.layout.list_catastro_cliente_items5)
//        }
//        val tvTitulo =
//            dialogoTipoCliente!!.findViewById<View>(R.id.tvTitulo) as TextView
//        tvTitulo.text = "CANAL DE VENTA"
//        lvTipoCliente =
//            dialogoTipoCliente!!.findViewById<View>(R.id.lvdet_catastro_cliente) as ListView
//        trae_lista_tipo_cliente()
//        val btSeleccionar =
//            dialogoTipoCliente!!.findViewById<View>(R.id.btSeleccionar) as Button
//        btSeleccionar.setOnClickListener {
//            ultCodTipoCliente = if (posicionTipoCliente != -1) {
//                etTC.setText(
//                    codTipoCliente[posicionTipoCliente]
//                        .toString() + " - " + descTipoCliente[posicionTipoCliente]
//                )
//                codTipoCliente[posicionTipoCliente]
//            } else {
//                etTC.setText("")
//                ""
//            }
//            dialogoTipoCliente!!.dismiss()
//            posicionTipoCliente = 0
//        }
//        val filtrar =
//            dialogoTipoCliente!!.findViewById<View>(R.id.btFiltrar) as Button
//        filtrar.setOnClickListener { trae_lista_tipo_cliente() }
//        val etSearch =
//            dialogoTipoCliente!!.findViewById<View>(R.id.tvSearch) as EditText
//        val teclado1 = TecladoAlfaNumerico()
//        etSearch.setOnClickListener { teclado1.showTecAlfNum(etSearch, this@CatastrarCliente) }
//        dialogoTipoCliente!!.show()
//    }
//
//    private fun trae_lista_tipo_cliente() {
//        listaTipoCliente = ArrayList()
//        val etSearch =
//            dialogoTipoCliente!!.findViewById<View>(R.id.tvSearch) as EditText
//        try {
//            val _where = ("(CODIGO LIKE '%" + etSearch.text.toString() + "%' "
//                    + " or DESCRIPCION LIKE '%" + etSearch.text.toString() + "%')")
//            val sel =
//                ("select COD_SUBTIPO as CODIGO, DESC_CANAL_VENTA as DESCRIPCION "
//                        + " from svm_tipo_cliente "
//                        + " Where " + _where
//                        + " GROUP BY COD_SUBTIPO, DESC_CANAL_VENTA"
//                        + " Order by Cast(COD_SUBTIPO as double)")
//            cursorTipoCliente = MenuCombinado.bdatos.rawQuery(sel, null)
//            val btVolver =
//                dialogoTipoCliente!!.findViewById<View>(R.id.btn_volver) as Button
//            btVolver.setOnClickListener {
//                try {
//                    dialogoTipoCliente!!.dismiss()
//                } catch (e: Exception) {
//                }
//            }
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        val nreg = cursorTipoCliente!!.count
//        posicionTipoCliente = if (nreg < 1) {
//            -1
//        } else {
//            0
//        }
//        codTipoCliente = arrayOfNulls(nreg)
//        descTipoCliente = arrayOfNulls(nreg)
//        cursorTipoCliente!!.moveToFirst()
//        var cont = 0
//        for (i in 0 until nreg) {
//            val map2 =
//                HashMap<String, String>()
//            map2["CODIGO"] = cursorTipoCliente!!.getString(
//                cursorTipoCliente
//                    .getColumnIndex("CODIGO")
//            )
//            codTipoCliente[cont] = cursorTipoCliente!!.getString(
//                cursorTipoCliente
//                    .getColumnIndex("CODIGO")
//            )
//            map2["DESCRIPCION"] = cursorTipoCliente!!.getString(
//                cursorTipoCliente
//                    .getColumnIndex("DESCRIPCION")
//            )
//            descTipoCliente[cont] = cursorTipoCliente!!.getString(
//                cursorTipoCliente
//                    .getColumnIndex("DESCRIPCION")
//            )
//            listaTipoCliente.add(map2)
//            cursorTipoCliente!!.moveToNext()
//            cont = cont + 1
//        }
//        sd2 = Adapter_lista_tipo_cliente(
//            this@CatastrarCliente, listaTipoCliente,
//            R.layout.list_text_catastro_cliente_items, arrayOf<String>(
//                "CODIGO", "DESCRIPCION"
//            ), intArrayOf(R.id.td1, R.id.td2)
//        )
//        lvTipoCliente!!.adapter = sd2
//        lvTipoCliente!!.onItemClickListener =
//            OnItemClickListener { parent, v, position, id ->
//                posicionTipoCliente = position
//                lvTipoCliente!!.invalidateViews()
//            }
//    }
//
//    inner class Adapter_lista_tipo_cliente(
//        this: Context?,
//        items: List<HashMap<String?, String?>?>?,
//        resource: Int,
//        from: Array<String?>?,
//        to: IntArray?
//    ) :
//        SimpleAdapter(this, items, resource, from, to) {
//        var _sqlupdate: String? = null
//        private val colors = intArrayOf(
//            Color.parseColor("#696969"),
//            Color.parseColor("#808080")
//        )
//
//        inner class ViewHolder
//
//        override fun getView(
//            position: Int,
//            convertView: View,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val colorPos = position % colors.size
//            view.setBackgroundColor(colors[colorPos])
//            val holder =
//                ViewHolder()
//            if (posicionTipoCliente == position) {
//                view.setBackgroundColor(Color.BLUE)
//            }
//            view.tag = holder
//            return view
//        }
//    }
//
//    //	MUESTRA LA LISTA DE TIPO DE CLIENTE
//    private fun trae_lista_precios(etLP: EditText) {
//        try {
//            dialog_lista_precios!!.dismiss()
//        } catch (e: Exception) {
//        }
//        if (save_lista_precios == -1) {
//            return
//        }
//        dialog_lista_precios = Dialog(this)
//        dialog_lista_precios!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        if (MenuCombinado._tip_tela === 1) {
//            dialog_lista_precios.setContentView(R.layout.list_catastro_cliente_items)
//        }
//        if (MenuCombinado._tip_tela === 2) {
//            dialog_lista_precios.setContentView(R.layout.list_catastro_cliente_items2)
//        }
//        if (MenuCombinado._tip_tela === 4) {
//            dialog_lista_precios.setContentView(R.layout.list_catastro_cliente_items4)
//        }
//        if (MenuCombinado._tip_tela === 5) {
//            dialog_lista_precios.setContentView(R.layout.list_catastro_cliente_items5)
//        }
//        val tvTitulo =
//            dialog_lista_precios!!.findViewById<View>(R.id.tvTitulo) as TextView
//        tvTitulo.text = "LISTA DE PRECIO"
//        list_view_lista_precios =
//            dialog_lista_precios!!.findViewById<View>(R.id.lvdet_catastro_cliente) as ListView
//        trae_lista_lista_precios()
//        val btSeleccionar =
//            dialog_lista_precios!!.findViewById<View>(R.id.btSeleccionar) as Button
//        btSeleccionar.setOnClickListener {
//            ult_cod_lista_precios = if (save_lista_precios != -1) {
//                etLP.setText(
//                    cod_lista_precios[save_lista_precios]
//                        .toString() + " - " + desc_lista_precios[save_lista_precios]
//                )
//                cod_lista_precios[save_lista_precios]
//            } else {
//                etLP.setText("")
//                ""
//            }
//            dialog_lista_precios!!.dismiss()
//            save_lista_precios = 0
//        }
//        val filtrar =
//            dialog_lista_precios!!.findViewById<View>(R.id.btFiltrar) as Button
//        filtrar.setOnClickListener { trae_lista_lista_precios() }
//        val etSearch =
//            dialog_lista_precios!!.findViewById<View>(R.id.tvSearch) as EditText
//        val teclado1 = TecladoAlfaNumerico()
//        etSearch.setOnClickListener { teclado1.showTecAlfNum(etSearch, this@CatastrarCliente) }
//        dialog_lista_precios!!.show()
//    }
//
//    private fun trae_lista_lista_precios() {
//        alist_lista_precios = ArrayList()
//        val etSearch =
//            dialog_lista_precios!!.findViewById<View>(R.id.tvSearch) as EditText
//        try {
//            val _where =
//                ("(COD_LISTA_PRECIO LIKE '%" + etSearch.text.toString() + "%' "
//                        + " or DESC_LISTA_PRECIO LIKE '%" + etSearch.text.toString() + "%')")
//            val sel =
//                ("select COD_LISTA_PRECIO as CODIGO, DESC_LISTA_PRECIO as DESCRIPCION "
//                        + " from svm_precios_fijos "
//                        + " Where " + _where
//                        + " Group by COD_LISTA_PRECIO, DESC_LISTA_PRECIO"
//                        + " Order by Cast(COD_LISTA_PRECIO as double) desc "
//                        + " LIMIT 1")
//            cursor_datos_lista_precios = MenuCombinado.bdatos.rawQuery(sel, null)
//            val btVolver =
//                dialog_lista_precios!!.findViewById<View>(R.id.btn_volver) as Button
//            btVolver.setOnClickListener {
//                try {
//                    dialog_lista_precios!!.dismiss()
//                } catch (e: Exception) {
//                }
//            }
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        val nreg = cursor_datos_lista_precios!!.count
//        save_lista_precios = if (nreg < 1) {
//            -1
//        } else {
//            0
//        }
//        cod_lista_precios = arrayOfNulls(nreg)
//        desc_lista_precios = arrayOfNulls(nreg)
//        cursor_datos_lista_precios!!.moveToFirst()
//        var cont = 0
//        for (i in 0 until nreg) {
//            val map2 =
//                HashMap<String, String>()
//            map2["CODIGO"] = cursor_datos_lista_precios!!.getString(
//                cursor_datos_lista_precios
//                    .getColumnIndex("CODIGO")
//            )
//            cod_lista_precios[cont] = cursor_datos_lista_precios!!.getString(
//                cursor_datos_lista_precios
//                    .getColumnIndex("CODIGO")
//            )
//            map2["DESCRIPCION"] = cursor_datos_lista_precios!!.getString(
//                cursor_datos_lista_precios
//                    .getColumnIndex("DESCRIPCION")
//            )
//            desc_lista_precios[cont] = cursor_datos_lista_precios!!.getString(
//                cursor_datos_lista_precios
//                    .getColumnIndex("DESCRIPCION")
//            )
//            alist_lista_precios.add(map2)
//            cursor_datos_lista_precios!!.moveToNext()
//            cont = cont + 1
//        }
//        sd7 = Adapter_lista_lista_precios(
//            this@CatastrarCliente, alist_lista_precios,
//            R.layout.list_text_catastro_cliente_items, arrayOf<String>(
//                "CODIGO", "DESCRIPCION"
//            ), intArrayOf(R.id.td1, R.id.td2)
//        )
//        list_view_lista_precios!!.adapter = sd7
//        list_view_lista_precios!!.onItemClickListener =
//            OnItemClickListener { parent, v, position, id ->
//                save_lista_precios = position
//                list_view_lista_precios!!.invalidateViews()
//            }
//    }
//
//    inner class Adapter_lista_lista_precios(
//        this: Context?,
//        items: List<HashMap<String?, String?>?>?,
//        resource: Int,
//        from: Array<String?>?,
//        to: IntArray?
//    ) :
//        SimpleAdapter(this, items, resource, from, to) {
//        var _sqlupdate: String? = null
//        private val colors = intArrayOf(
//            Color.parseColor("#696969"),
//            Color.parseColor("#808080")
//        )
//
//        inner class ViewHolder
//
//        override fun getView(
//            position: Int,
//            convertView: View,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val colorPos = position % colors.size
//            view.setBackgroundColor(colors[colorPos])
//            val holder =
//                ViewHolder()
//            if (save_lista_precios == position) {
//                view.setBackgroundColor(Color.BLUE)
//            }
//            view.tag = holder
//            return view
//        }
//    }
//
//    //	MUESTRA LA LISTA DE DIAS DE VISITA POSIBLES
//    private fun trae_dias_visita(etDV: EditText) {
//        try {
//            dialogoDiasVisita!!.dismiss()
//        } catch (e: Exception) {
//        }
//        if (posicionDiasVisita == -1) {
//            return
//        }
//        dialogoDiasVisita = Dialog(this)
//        dialogoDiasVisita!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        if (MenuCombinado._tip_tela === 1) {
//            dialogoDiasVisita.setContentView(R.layout.list_dias_visita)
//        }
//        if (MenuCombinado._tip_tela === 2) {
//            dialogoDiasVisita.setContentView(R.layout.list_dias_visita2)
//        }
//        if (MenuCombinado._tip_tela === 4) {
//            dialogoDiasVisita.setContentView(R.layout.list_dias_visita4)
//        }
//        if (MenuCombinado._tip_tela === 5) {
//            dialogoDiasVisita.setContentView(R.layout.list_dias_visita5)
//        }
//        val tvTitulo =
//            dialogoDiasVisita!!.findViewById<View>(R.id.tvTitulo) as TextView
//        tvTitulo.text = "DIAS DE VISITA"
//        lvDiasVisita =
//            dialogoDiasVisita!!.findViewById<View>(R.id.lvdet_catastro_cliente) as ListView
//        trae_lista_dias_visita(consulta)
//        val btSeleccionar =
//            dialogoDiasVisita!!.findViewById<View>(R.id.btSeleccionar) as Button
//        btSeleccionar.setOnClickListener {
//            ultCodDiasVisita = if (posicionDiasVisita != -1) {
//                etDV.setText(
//                    codDiasVisita[posicionDiasVisita]
//                        .toString() + " - " + descDiasVisita[posicionDiasVisita]
//                )
//                codDiasVisita[posicionDiasVisita]
//            } else {
//                etDV.setText("")
//                ""
//            }
//            dialogoDiasVisita!!.dismiss()
//            posicionDiasVisita = 0
//        }
//        val filtrar =
//            dialogoDiasVisita!!.findViewById<View>(R.id.btFiltrar) as Button
//        filtrar.setOnClickListener { trae_lista_dias_visita(consulta) }
//        val etSearch =
//            dialogoDiasVisita!!.findViewById<View>(R.id.tvSearch) as EditText
//        val teclado1 = TecladoAlfaNumerico()
//        etSearch.setOnClickListener { teclado1.showTecAlfNum(etSearch, this@CatastrarCliente) }
//        val btVolver =
//            dialogoDiasVisita!!.findViewById<View>(R.id.btn_volver) as Button
//        btVolver.setOnClickListener {
//            try {
//                dialogoDiasVisita!!.dismiss()
//            } catch (e: Exception) {
//            }
//        }
//        dialogoDiasVisita!!.show()
//    }
//
//    private fun trae_lista_dias_visita(consulta: Boolean) {
//        if (ult_cod_ciudad == "") {
//            posicionDiasVisita = -1
//            return
//        }
//        alist_dias_visita = ArrayList()
//        val etSearch =
//            dialogoDiasVisita!!.findViewById<View>(R.id.tvSearch) as EditText
//        try {
//            val _where = ("(CODIGO LIKE '%" + etSearch.text.toString() + "%' "
//                    + " or DESCRIPCION LIKE '%" + etSearch.text.toString() + "%')")
//            var sel = ""
//            if (consulta == true) {
//                var frec =
//                    ("Select frecuencia from svm_ciudades where cod_pais = '" + ult_cod_pais_dep
//                            + "' and cod_departamento = '" + ult_cod_dep + "' and cod_ciudad = '" + ult_cod_ciudad + "'")
//                val cursor: Cursor = MenuCombinado.bdatos.rawQuery(frec, null)
//                cursor.moveToFirst()
//                frec = cursor.getString(cursor.getColumnIndex("FRECUENCIA"))
//                sel = ("select CODIGO, DESCRIPCION, FRECUENCIA  "
//                        + " from svm_dias_visita "
//                        + " Where " + _where + " and FRECUENCIA = '" + frec + "' "
//                        + " Order by Cast(CODIGO as double)")
//            } else {
//                sel = ("select CODIGO, DESCRIPCION, FRECUENCIA  "
//                        + " from svm_dias_visita "
//                        + " Where " + _where + " and FRECUENCIA = '" + frecuencia_ciudad[save_ciudades] + "' "
//                        + " Order by Cast(CODIGO as double)")
//            }
//            cursor_datos_dias_visita = MenuCombinado.bdatos.rawQuery(sel, null)
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        val nreg = cursor_datos_dias_visita!!.count
//        posicionDiasVisita = if (nreg < 1) {
//            -1
//        } else {
//            0
//        }
//        codDiasVisita = arrayOfNulls(nreg)
//        descDiasVisita = arrayOfNulls(nreg)
//        frecuenciaDiasVisita = arrayOfNulls(nreg)
//        cursor_datos_dias_visita!!.moveToFirst()
//        var cont = 0
//        for (i in 0 until nreg) {
//            val map2 =
//                HashMap<String, String>()
//            map2["CODIGO"] = cursor_datos_dias_visita!!.getString(
//                cursor_datos_dias_visita
//                    .getColumnIndex("CODIGO")
//            )
//            codDiasVisita[cont] = cursor_datos_dias_visita!!.getString(
//                cursor_datos_dias_visita
//                    .getColumnIndex("CODIGO")
//            )
//            map2["DESCRIPCION"] = cursor_datos_dias_visita!!.getString(
//                cursor_datos_dias_visita
//                    .getColumnIndex("DESCRIPCION")
//            )
//            descDiasVisita[cont] = cursor_datos_dias_visita!!.getString(
//                cursor_datos_dias_visita
//                    .getColumnIndex("DESCRIPCION")
//            )
//            map2["FRECUENCIA"] = cursor_datos_dias_visita!!.getString(
//                cursor_datos_dias_visita
//                    .getColumnIndex("FRECUENCIA")
//            )
//            frecuenciaDiasVisita[cont] = cursor_datos_dias_visita!!.getString(
//                cursor_datos_dias_visita
//                    .getColumnIndex("FRECUENCIA")
//            )
//            alist_dias_visita.add(map2)
//            cursor_datos_dias_visita!!.moveToNext()
//            cont = cont + 1
//        }
//        sd3 = Adapter_lista_dias_visita(
//            this@CatastrarCliente, alist_dias_visita,
//            R.layout.list_text_dias_visita, arrayOf<String>(
//                "CODIGO", "DESCRIPCION"
//            ), intArrayOf(R.id.td1, R.id.td2)
//        )
//        lvDiasVisita!!.adapter = sd3
//        lvDiasVisita!!.onItemClickListener =
//            OnItemClickListener { parent, v, position, id ->
//                posicionDiasVisita = position
//                lvDiasVisita!!.invalidateViews()
//            }
//    }
//
//    inner class Adapter_lista_dias_visita(
//        this: Context?,
//        items: List<HashMap<String?, String?>?>?,
//        resource: Int,
//        from: Array<String?>?,
//        to: IntArray?
//    ) :
//        SimpleAdapter(this, items, resource, from, to) {
//        var _sqlupdate: String? = null
//        private val colors = intArrayOf(
//            Color.parseColor("#696969"),
//            Color.parseColor("#808080")
//        )
//
//        inner class ViewHolder
//
//        override fun getView(
//            position: Int,
//            convertView: View,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val colorPos = position % colors.size
//            view.setBackgroundColor(colors[colorPos])
//            val holder =
//                ViewHolder()
//            if (posicionDiasVisita == position) {
//                view.setBackgroundColor(Color.BLUE)
//            }
//            view.tag = holder
//            return view
//        }
//    }
//
//    //	TRAE LA LISTA DE DEPARTAMENTOS
//    private fun trae_departamentos(etD: EditText) {
//        try {
//            dialog_departamentos!!.dismiss()
//        } catch (e: Exception) {
//        }
//        if (save_departamentos == -1) {
//            return
//        }
//        dialog_departamentos = Dialog(this)
//        dialog_departamentos!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        if (MenuCombinado._tip_tela === 1) {
//            dialog_departamentos.setContentView(R.layout.list_departamentos2)
//        }
//        if (MenuCombinado._tip_tela === 2) {
//            dialog_departamentos.setContentView(R.layout.list_departamentos2)
//        }
//        if (MenuCombinado._tip_tela === 4) {
//            dialog_departamentos.setContentView(R.layout.list_departamentos2)
//        }
//        if (MenuCombinado._tip_tela === 5) {
//            dialog_departamentos.setContentView(R.layout.list_departamentos5)
//        }
//        val tvTitulo =
//            dialog_departamentos!!.findViewById<View>(R.id.tvTitulo) as TextView
//        tvTitulo.text = "DEPARTAMENTOS"
//        list_view_departamentos =
//            dialog_departamentos!!.findViewById<View>(R.id.lvdet_catastro_cliente) as ListView
//        trae_lista_departamentos()
//        val btSeleccionar =
//            dialog_departamentos!!.findViewById<View>(R.id.btSeleccionar) as Button
//        btSeleccionar.setOnClickListener {
//            if (save_departamentos != -1) {
//                etD.setText(
//                    cod_departamentos[save_departamentos]
//                        .toString() + " - " + desc_departamentos[save_departamentos]
//                )
//                ult_cod_dep = cod_departamentos[save_departamentos]
//                ult_cod_pais_dep = cod_pais[save_departamentos]
//            } else {
//                etD.setText("")
//                ult_cod_dep = ""
//                ult_cod_pais_dep = ""
//            }
//            val tvCiudad = findViewById<View>(R.id.etCiudad) as TextView
//            tvCiudad.text = ""
//            ult_cod_ciudad = ""
//            val tvDias = findViewById<View>(R.id.etDiasVisita) as TextView
//            tvDias.text = ""
//            ultCodDiasVisita = ""
//            dialog_departamentos!!.dismiss()
//            save_departamentos = 0
//        }
//        val filtrar =
//            dialog_departamentos!!.findViewById<View>(R.id.btFiltrar) as Button
//        filtrar.setOnClickListener { trae_lista_departamentos() }
//        val etSearch =
//            dialog_departamentos!!.findViewById<View>(R.id.tvSearch) as EditText
//        val teclado1 = TecladoAlfaNumerico()
//        etSearch.setOnClickListener { teclado1.showTecAlfNum(etSearch, this@CatastrarCliente) }
//        dialog_departamentos!!.show()
//    }
//
//    private fun trae_lista_departamentos() {
//        alist_departamentos = ArrayList()
//        val etSearch =
//            dialog_departamentos!!.findViewById<View>(R.id.tvSearch) as EditText
//        try {
//            val _where =
//                ("(COD_DEPARTAMENTO LIKE '%" + etSearch.text.toString() + "%' "
//                        + "  or DESC_DEPARTAMENTO LIKE '%" + etSearch.text.toString() + "%')")
//            val sel =
//                ("select COD_DEPARTAMENTO, DESC_DEPARTAMENTO, COD_PAIS, DESC_PAIS "
//                        + " from svm_ciudades "
//                        + " Where " + _where
//                        + " Group by COD_DEPARTAMENTO, DESC_DEPARTAMENTO, COD_PAIS, DESC_PAIS "
//                        + " Order by Cast(COD_DEPARTAMENTO as double)")
//            cursor_datos_departamentos = MenuCombinado.bdatos.rawQuery(sel, null)
//            val btVolver =
//                dialog_departamentos!!.findViewById<View>(R.id.btn_volver) as Button
//            btVolver.setOnClickListener {
//                try {
//                    dialog_departamentos!!.dismiss()
//                } catch (e: Exception) {
//                }
//            }
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        val nreg = cursor_datos_departamentos!!.count
//        save_departamentos = if (nreg < 1) {
//            -1
//        } else {
//            0
//        }
//        cod_departamentos = arrayOfNulls(nreg)
//        desc_departamentos = arrayOfNulls(nreg)
//        cod_pais = arrayOfNulls(nreg)
//        desc_pais = arrayOfNulls(nreg)
//        cursor_datos_departamentos!!.moveToFirst()
//        var cont = 0
//        for (i in 0 until nreg) {
//            val map2 =
//                HashMap<String, String>()
//            map2["COD_DEPARTAMENTO"] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("COD_DEPARTAMENTO")
//            )
//            cod_departamentos[cont] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("COD_DEPARTAMENTO")
//            )
//            map2["DESC_DEPARTAMENTO"] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("DESC_DEPARTAMENTO")
//            )
//            desc_departamentos[cont] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("DESC_DEPARTAMENTO")
//            )
//            map2["COD_PAIS"] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("COD_PAIS")
//            )
//            cod_pais[cont] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("COD_PAIS")
//            )
//            map2["DESC_PAIS"] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("DESC_PAIS")
//            )
//            desc_pais[cont] = cursor_datos_departamentos!!.getString(
//                cursor_datos_departamentos
//                    .getColumnIndex("DESC_PAIS")
//            )
//            alist_departamentos.add(map2)
//            cursor_datos_departamentos!!.moveToNext()
//            cont = cont + 1
//        }
//        sd4 = Adapter_lista_departamentos(
//            this@CatastrarCliente, alist_departamentos,
//            R.layout.list_text_departamentos, arrayOf<String>(
//                "COD_DEPARTAMENTO", "DESC_DEPARTAMENTO", "COD_PAIS",
//                "DESC_PAIS"
//            ), intArrayOf(
//                R.id.td1, R.id.td2, R.id.td3,
//                R.id.td4
//            )
//        )
//        list_view_departamentos!!.adapter = sd4
//        list_view_departamentos!!.onItemClickListener =
//            OnItemClickListener { parent, v, position, id ->
//                save_departamentos = position
//                list_view_departamentos!!.invalidateViews()
//            }
//    }
//
//    inner class Adapter_lista_departamentos(
//        this: Context?,
//        items: List<HashMap<String?, String?>?>?,
//        resource: Int,
//        from: Array<String?>?,
//        to: IntArray?
//    ) :
//        SimpleAdapter(this, items, resource, from, to) {
//        var _sqlupdate: String? = null
//        private val colors = intArrayOf(
//            Color.parseColor("#696969"),
//            Color.parseColor("#808080")
//        )
//
//        inner class ViewHolder
//
//        override fun getView(
//            position: Int,
//            convertView: View,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val colorPos = position % colors.size
//            view.setBackgroundColor(colors[colorPos])
//            val holder =
//                ViewHolder()
//            if (save_departamentos == position) {
//                view.setBackgroundColor(Color.BLUE)
//            }
//            view.tag = holder
//            return view
//        }
//    }
//
//    //	TRAE LA LISTA DE CIUDADES
//    private fun trae_ciudades(etC: EditText) {
//        try {
//            dialog_ciudades!!.dismiss()
//        } catch (e: Exception) {
//        }
//        if (save_ciudades == -1) {
//            return
//        }
//        dialog_ciudades = Dialog(this)
//        dialog_ciudades!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        if (MenuCombinado._tip_tela === 1) {
//            dialog_ciudades.setContentView(R.layout.list_ciudades2)
//        }
//        if (MenuCombinado._tip_tela === 2) {
//            dialog_ciudades.setContentView(R.layout.list_ciudades2)
//        }
//        if (MenuCombinado._tip_tela === 4) {
//            dialog_ciudades.setContentView(R.layout.list_ciudades2)
//        }
//        if (MenuCombinado._tip_tela === 5) {
//            dialog_ciudades.setContentView(R.layout.list_ciudades5)
//        }
//        val tvTitulo =
//            dialog_ciudades!!.findViewById<View>(R.id.tvTitulo) as TextView
//        tvTitulo.text = "CIUDADES"
//        list_view_ciudades =
//            dialog_ciudades!!.findViewById<View>(R.id.lvdet_catastro_cliente) as ListView
//        trae_lista_ciudades()
//        val btSeleccionar =
//            dialog_ciudades!!.findViewById<View>(R.id.btSeleccionar) as Button
//        btSeleccionar.setOnClickListener {
//            ult_cod_ciudad = if (save_ciudades != -1) {
//                etC.setText(
//                    cod_ciudad[save_ciudades].toString() + " - " + desc_ciudad[save_ciudades]
//                )
//                cod_ciudad[save_ciudades]
//            } else {
//                etC.setText("")
//                ""
//            }
//            val tvDias = findViewById<View>(R.id.etDiasVisita) as TextView
//            tvDias.text = ""
//            ultCodDiasVisita = ""
//            dialog_ciudades!!.dismiss()
//            save_ciudades = 0
//        }
//        val filtrar =
//            dialog_ciudades!!.findViewById<View>(R.id.btFiltrar) as Button
//        filtrar.setOnClickListener { trae_lista_ciudades() }
//        val etSearch =
//            dialog_ciudades!!.findViewById<View>(R.id.tvSearch) as EditText
//        val teclado1 = TecladoAlfaNumerico()
//        etSearch.setOnClickListener { teclado1.showTecAlfNum(etSearch, this@CatastrarCliente) }
//        dialog_ciudades!!.show()
//    }
//
//    private fun trae_lista_ciudades() {
//        alist_ciudades = ArrayList()
//        val etSearch =
//            dialog_ciudades!!.findViewById<View>(R.id.tvSearch) as EditText
//        try {
//            val _where =
//                ("(COD_CIUDAD LIKE '%" + etSearch.text.toString() + "%' "
//                        + "  or DESC_CIUDAD LIKE '%" + etSearch.text.toString() + "%') ")
//            val sel = (" select COD_CIUDAD, DESC_CIUDAD, COD_DEPARTAMENTO, "
//                    + "DESC_DEPARTAMENTO, COD_PAIS   , DESC_PAIS       , "
//                    + "FRECUENCIA "
//                    + " from svm_ciudades "
//                    + " Where " + _where + " and COD_DEPARTAMENTO = '" + ult_cod_dep + "' and COD_PAIS = '" + ult_cod_pais_dep + "'"
//                    + " Order by Cast(COD_CIUDAD as double)")
//            cursor_datos_ciudades = MenuCombinado.bdatos.rawQuery(sel, null)
//            val btVolver =
//                dialog_ciudades!!.findViewById<View>(R.id.btn_volver) as Button
//            btVolver.setOnClickListener {
//                try {
//                    dialog_ciudades!!.dismiss()
//                } catch (e: Exception) {
//                }
//            }
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        val nreg = cursor_datos_ciudades!!.count
//        save_ciudades = if (nreg < 1) {
//            -1
//        } else {
//            0
//        }
//        cod_ciudad = arrayOfNulls(nreg)
//        desc_ciudad = arrayOfNulls(nreg)
//        cod_departamentos_ciudad = arrayOfNulls(nreg)
//        desc_departamentos_ciudad = arrayOfNulls(nreg)
//        cod_pais_ciudad = arrayOfNulls(nreg)
//        desc_pais_ciudad = arrayOfNulls(nreg)
//        frecuencia_ciudad = arrayOfNulls(nreg)
//        cursor_datos_ciudades!!.moveToFirst()
//        var cont = 0
//        for (i in 0 until nreg) {
//            val map2 =
//                HashMap<String, String>()
//            map2["COD_CIUDAD"] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("COD_CIUDAD")
//            )
//            cod_ciudad[cont] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("COD_CIUDAD")
//            )
//            map2["DESC_CIUDAD"] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("DESC_CIUDAD")
//            )
//            desc_ciudad[cont] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("DESC_CIUDAD")
//            )
//            map2["COD_DEPARTAMENTO"] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("COD_DEPARTAMENTO")
//            )
//            cod_departamentos_ciudad[cont] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("COD_DEPARTAMENTO")
//            )
//            map2["DESC_DEPARTAMENTO"] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("DESC_DEPARTAMENTO")
//            )
//            desc_departamentos_ciudad[cont] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("DESC_DEPARTAMENTO")
//            )
//            map2["COD_PAIS"] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("COD_PAIS")
//            )
//            cod_pais_ciudad[cont] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("COD_PAIS")
//            )
//            map2["DESC_PAIS"] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("DESC_PAIS")
//            )
//            desc_pais_ciudad[cont] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("DESC_PAIS")
//            )
//            map2["FRECUENCIA"] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("FRECUENCIA")
//            )
//            frecuencia_ciudad[cont] = cursor_datos_ciudades!!.getString(
//                cursor_datos_ciudades
//                    .getColumnIndex("FRECUENCIA")
//            )
//            alist_ciudades.add(map2)
//            cursor_datos_ciudades!!.moveToNext()
//            cont = cont + 1
//        }
//        sd5 = Adapter_lista_ciudades(
//            this@CatastrarCliente, alist_ciudades,
//            R.layout.list_text_ciudades, arrayOf<String>(
//                "COD_CIUDAD", "DESC_CIUDAD", "COD_DEPARTAMENTO",
//                "DESC_DEPARTAMENTO", "COD_PAIS", "DESC_PAIS"
//            ), intArrayOf(
//                R.id.td1, R.id.td2, R.id.td3,
//                R.id.td4, R.id.td5, R.id.td6
//            )
//        )
//        list_view_ciudades!!.adapter = sd5
//        list_view_ciudades!!.onItemClickListener =
//            OnItemClickListener { parent, v, position, id ->
//                save_ciudades = position
//                list_view_ciudades!!.invalidateViews()
//            }
//    }
//
//    inner class Adapter_lista_ciudades(
//        this: Context?,
//        items: List<HashMap<String?, String?>?>?,
//        resource: Int,
//        from: Array<String?>?,
//        to: IntArray?
//    ) :
//        SimpleAdapter(this, items, resource, from, to) {
//        var _sqlupdate: String? = null
//        private val colors = intArrayOf(
//            Color.parseColor("#696969"),
//            Color.parseColor("#808080")
//        )
//
//        inner class ViewHolder
//
//        override fun getView(
//            position: Int,
//            convertView: View,
//            parent: ViewGroup
//        ): View {
//            val view = super.getView(position, convertView, parent)
//            val colorPos = position % colors.size
//            view.setBackgroundColor(colors[colorPos])
//            val holder =
//                ViewHolder()
//            if (save_ciudades == position) {
//                view.setBackgroundColor(Color.BLUE)
//            }
//            view.tag = holder
//            return view
//        }
//    }
//
//    //	FUNCION PARA OBTENER EL PROXIMO CODIGO PARA LA COMPOSICION "COD_VENDEDOR + COD_CLI_VEND"
//    private fun obtieneCodCliente(codVendedor : String) {
//        var codigo = 0
//        var cursor: Cursor
//        val codClienteVendedor = "Select COD_CLI_VEND from svm_vendedor_pedido where cod_vendedor = '" + codVendedor + "'"
//        cursor = funcion.consultar(codClienteVendedor)
//        codigo = if (cursor.count > 0) {
//            try {
//                funcion.dato(cursor,"COD_CLI_VEND").toInt()
//            } catch (e: Exception) {
//                1
//            }
//        } else {
//            1
//        }
//        val max = "select max (Cast(COD_CLI_VEND as double)) as MAXIMO from svm_catastro_cliente"
//        var codigo2 = 0
//        try {
//            cursor =funcion.consultar(max)
//        } catch (e: Exception) {
//            var err = e.message
//            err = "" + err
//        }
//        cursor.moveToFirst()
//        codigo2 = if (cursor.count > 0) {
//            try {
//                funcion.dato(cursor,"MAXIMO").toInt() + 1
//            } catch (e: Exception) {
//                1
//            }
//        } else {
//            1
//        }
//        cod_cliente_comp = if (codigo > codigo2) {
//            codigo
//        } else {
//            codigo2
//        }
//    }
//
//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent
//    ) {
//        if (requestCode == 1) {
//            if (data == null) {
//                var iv: ImageView? = null
//                iv = findViewById<View>(R.id.ivFachada) as ImageView
//                try {
//                    if (File(name).exists()) {
//                        try {
//                            val options = BitmapFactory.Options()
//                            options.inSampleSize = 8
//                            val fis = FileInputStream(name)
//                            var bm = BitmapFactory.decodeStream(fis, null, options)
//                            options.inJustDecodeBounds = true
//                            BitmapFactory.decodeFile(name, options)
//                            var w = 0
//                            var h = 0
//                            w = options.outWidth
//                            h = options.outHeight
//                            bm = if (w < h) {
//                                val y = h.toFloat() / 768
//                                MenuCombinado.utilidades.resizeImage(
//                                    this@CatastrarCliente,
//                                    bm,
//                                    (w / y).toInt(),
//                                    (h / y).toInt()
//                                )
//                            } else {
//                                val x = w.toFloat() / 768
//                                MenuCombinado.utilidades.resizeImage(
//                                    this@CatastrarCliente,
//                                    bm,
//                                    (w / x).toInt(),
//                                    (h / x).toInt()
//                                )
//                            }
//                            var out: FileOutputStream? = null
//                            try {
//                                out = FileOutputStream(name)
//                                bm.compress(
//                                    Bitmap.CompressFormat.JPEG,
//                                    100,
//                                    out
//                                ) // bmp is your Bitmap instance
//                                // PNG is a lossless format, the compression factor (100) is ignored
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            } finally {
//                                try {
//                                    out?.close()
//                                } catch (e: IOException) {
//                                    e.printStackTrace()
//                                }
//                            }
//
////							Bitmap bm = BitmapFactory.decodeFile(name);
//                            iv!!.setImageBitmap(bm)
//                        } catch (e2: Exception) {
//                            var err = e2.message
//                            err = err + ""
//                        }
//                        val output = Uri.fromFile(File(name))
//                        val inputStream: InputStream?
//                        var imagen: ByteArray? = null
//                        var str_imagen = ""
//                        inputStream = contentResolver.openInputStream(output)
//                        imagen = MenuCombinado.utilidades.readBytes(output, inputStream)
//                        str_imagen = MenuCombinado.utilidades.byteToString2(imagen)
//                        imagen_fachada = str_imagen
//                        File(name).delete()
//                    }
//                } catch (e: Exception) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//    companion object {
//        var sd: Adapter_lista_forma_pago? = null
//        var sd2: Adapter_lista_tipo_cliente? = null
//        var sd3: Adapter_lista_dias_visita? = null
//        var sd4: Adapter_lista_departamentos? = null
//        var sd5: Adapter_lista_ciudades? = null
//        var sd6: Adapter_lista_catastro_cliente? = null
//        var sd7: Adapter_lista_lista_precios? = null
//        const val DATE_DIALOG_ID = 1
//        var codVendedor = ""
//    }
//}
//
//
