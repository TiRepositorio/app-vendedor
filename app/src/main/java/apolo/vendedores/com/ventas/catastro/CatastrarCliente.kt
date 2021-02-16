package apolo.vendedores.com.ventas.catastro

import android.annotation.SuppressLint
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.FileProvider
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.*
import kotlinx.android.synthetic.main.activity_catastrar_cliente.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CatastrarCliente : Activity() {
    //	VARIABLES PARA LISTA DE CONDICION DE VENTA
    var ultCodFormaPago: String? = ""
    var consulta = false

    private var fotoFachada : String = ""
    private var tipoFoto : String = ""
    var nombre : String = ""

    //	VARIABLES PARA LISTA DE TIPO DE CLIENTE
    var ultCodTipoCliente: String? = ""

    //	VARIABLES PARA LISTA DE DIAS DE VISITA
    var ultCodDiasVisita: String? = ""

    //	VARIABLES PARA LISTA DE DEPARTAMENTOS
    var ultCodDep: String? = ""
    var ultCodPaisDep: String? = ""

    var ultCodCiudad: String? = ""
    var codClienteComp = 0

    //	VARIABLES PARA BUSCADOR DE CLIENTES CATASTRADOS
    var dialog_clientes_catastrados: Dialog? = null
    var list_view_clientes_catastrados: ListView? = null
    var save_clientes_catastrados = -1
    var cursor: Cursor? = null
    private lateinit var alist_cliente_catastrado: ArrayList<HashMap<String, String>>
    lateinit var cod_cliente_composicion: Array<String?>
    lateinit var estado_cliente_catastrado: Array<String?>

    var ultCodListaPrecios: String = ""
    private var fecDesde: TextView? = null
    private var fecHasta: TextView? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var fecha = 0

    //	VARIABLES PARA EL WEB SERVICE
    var vCliente = ""
    var codCliente = ""
    var respuestaWS = ""
    var name = ""
    var imagenFachada: String? = null

    val funcion = FuncionesUtiles(this)
    private val dispositivo = FuncionesDispositivo(this)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_catastrar_cliente)

        tvmLatitud = tvLatitud
        tvmLongitud = tvLongitud
        foto = FuncionesFoto(this)

        obtieneCodCliente(codVendedor)
        val imgBuscarCliente = findViewById<View>(R.id.imgBuscarCliente) as ImageButton
        val codigo = findViewById<View>(R.id.etCodigo) as EditText
        codigo.setText("$codVendedor-$codClienteComp")
        imgBuscarCliente.setOnClickListener {
            val myAlertDialog =
                AlertDialog.Builder(this@CatastrarCliente)
            myAlertDialog.setMessage("Se limpiara el formulario ¿Desea continuar?")
            myAlertDialog.setPositiveButton("Si") { _, _ ->
                try {
                    limpiarTodo()
                    obtieneCodCliente(codVendedor)
                    codigo.setText("$codVendedor-$codClienteComp")
                    consultarClientesCatastrados()
                } catch (e: Exception) {
                    funcion.toast(applicationContext, "ERROR " + e.message)
                }
            }
            myAlertDialog.setNegativeButton("No") { _, _ -> }
            myAlertDialog.show()
        }
        etCiudad.setOnClickListener{ buscarCiudad() }
        etListaPrecio.setOnClickListener{ buscarListaDePrecio() }
        etDepartamento.setOnClickListener{ buscarDepartamento() }
        btVolver.setOnClickListener { finish() }
        etFormaPago.setOnClickListener { buscarFormaDePago() }
        etTipoCliente.setOnClickListener{ buscarTipoCliente() }
        etDiasVisita.setOnClickListener { buscarDiaDeVisita() }
        etNomRefComercial.setOnClickListener{ funcion.dialogoEntradaContacto(
            etNomRefComercial,
            etTelRefComercial,
            this
        ) }
        etTelRefComercial.setOnClickListener{ funcion.dialogoEntradaContacto(
            etNomRefComercial,
            etTelRefComercial,
            this
        ) }
        etNomRefBancaria.setOnClickListener{ funcion.dialogoEntradaContacto(
            etNomRefBancaria,
            etTelRefBancaria,
            this
        ) }
        etTelRefBancaria.setOnClickListener{ funcion.dialogoEntradaContacto(
            etNomRefBancaria,
            etTelRefBancaria,
            this
        ) }
        etComentario.setOnClickListener{ funcion.dialogoEntrada(etComentario, this)}
        btBuscarEnMapa.setOnClickListener{
            MapaCatastro.modificarCliente = true
            startActivity(Intent(this, MapaCatastro::class.java))
        }
        btLimpiar.setOnClickListener {
            obtieneCodCliente(codVendedor)
            etCodigo.setText("$codVendedor-$codClienteComp")
            limpiarTodo()
        }
        btCancelar.setOnClickListener { cancelarCatastro() }
        btGuardar.setOnClickListener {
            if (validaCampos()) {
                guardarCatastro()
            }
        }
        btEnviar.setOnClickListener {
            if (validaCampos()) {
                guardarCatastro()
                vCliente = ""
                codCliente = ""
                ProbarConexion().execute()
            }
        }
        ibtFotoFachada.setOnClickListener {
            sacarFoto()
        }
    }

    var resultado: String = ""
    lateinit var pbarDialog: ProgressDialog

    private inner class ProbarConexion :
        AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            try {
                pbarDialog.dismiss()
            } catch (e: Exception) {
            }
            pbarDialog = ProgressDialog.show(
                this@CatastrarCliente, "Un momento...",
                "Comprobando conexion", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                resultado = MainActivity2.conexionWS.procesaVersion()
                resultado = "01*ENVIADO CON EXITO"
                null
            } catch (e: Exception) {
                resultado = e.message.toString()
                null
            }
        }

        override fun onPostExecute(unused: Void?) {
            pbarDialog.dismiss()
            if (resultado != "null") {
                try {
                    generaClienteEnviar()
                    return
                } catch (e: Exception) {
                    var err = e.message
                    err += ""
                }
            } else {
                generaClienteEnviar()
                return
            }
            Toast.makeText(
                this@CatastrarCliente,
                "Verifique su conexion a internet y vuelva a intentarlo",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

    }

    //	GENERA STRING PARA ENVIAR AL WEB SERVICE
    private fun generaClienteEnviar() {
        vCliente = "'1'|'" + FuncionesUtiles.usuario["LOGIN"]
        vCliente += "'|'" + etCodigo.text.toString()
        codCliente = etCodigo.text.toString()
        var limit: Int = if (etRazonSocial.text.length < 100) { etRazonSocial.text.length } else { 100 }
        vCliente += "'|'" + etRazonSocial.text.toString().toUpperCase().substring(0, limit)
        limit = if (etNombreFantasia.text.length < 100) { etNombreFantasia.text.length } else { 100 }
        vCliente += "'|'" + etNombreFantasia.text.toString().toUpperCase().substring(0, limit)
        limit = if (etDireccionComercial.text.length < 100) { etDireccionComercial.text.length } else { 100 }
        vCliente += "'|'" + etDireccionComercial.text.toString().toUpperCase().substring(0, limit)
        vCliente += "'|'PAR"
        vCliente += "'|'${etDepartamento.text.toString().split("-")[0].trim().toUpperCase()}"
        vCliente += "'|'${etCiudad.text.toString().split("-")[0].trim().toUpperCase()}"
        limit = if (etBarrio.text.length < 100) { etBarrio.text.length } else { 100 }
        vCliente += "'|'" + etBarrio.text.toString().toUpperCase().substring(0, limit)
        vCliente += "'|'" + etRUC.text.toString().toUpperCase()
        vCliente += "'|'" + etCI.text.toString().toUpperCase()
        vCliente += "'|'" + etCelular.text.toString().toUpperCase()
        vCliente += "'|'" + etLineaBaja.text.toString().toUpperCase()
        vCliente += "'|'${etFormaPago.text.toString().toUpperCase().split("-")[0].trim()}"
        vCliente += "'|'${etTipoCliente.text.toString().toUpperCase().split("-")[0].trim()}"
        vCliente += "'|'${etDiasVisita.text.toString().toUpperCase().split(" ")[0].trim()}"
        limit = if (etCercaDe.text.length < 100) { etCercaDe.text.length } else { 100 }
        vCliente += "'|'" + etCercaDe.text.toString().toUpperCase().substring(0, limit)
        limit = if (etEmail.text.length < 100) { etEmail.text.length } else { 100 }
        vCliente += "'|'" + etEmail.text.toString().toUpperCase().substring(0, limit)
        limit = if (etLimiteCredito.text.length < 100) { etLimiteCredito.text.length } else { 100 }
        vCliente += "'|'" + etLimiteCredito.text.toString().toUpperCase().substring(0, limit)
        vCliente += "'|'${etListaPrecio.text.toString().toUpperCase().split("-")[0].trim()}"
        limit = if (etNomRefComercial.text.length < 100) { etNomRefComercial.text.length } else { 100 }
        vCliente += "'|'" + etNomRefComercial.text.toString().toUpperCase().substring(0, limit)
        limit = if (etTelRefComercial.text.length < 100) { etTelRefComercial.text.length } else { 100 }
        vCliente += "'|'" + etTelRefComercial.text.toString().toUpperCase().substring(0, limit)
        limit = if (etNomRefBancaria.text.length < 100) { etNomRefBancaria.text.length } else { 100 }
        vCliente += "'|'" + etNomRefBancaria.text.toString().toUpperCase().substring(0, limit)
        limit = if (etTelRefBancaria.text.length < 100) { etTelRefBancaria.text.length } else { 100 }
        vCliente += "'|'" + etTelRefBancaria.text.toString().toUpperCase().substring(0, limit)
        limit = if (tvLatitud.text.length < 100) { tvLatitud.text.length } else { 100 }
        vCliente += "'|'" + tvLatitud.text.toString().toUpperCase().substring(0, limit)
        limit = if (tvLongitud.text.length < 100) { tvLongitud.text.length } else { 100 }
        vCliente += "'|'" + tvLongitud.text.toString().toUpperCase().substring(0, limit)
        limit = if (etComentario.text.length < 200) { etComentario.text.length } else { 200 }
        vCliente += "'|'" + etComentario.text.toString().toUpperCase().substring(0, limit) + "'"


        /* vCliente = "'1'|'$codVendedor"
        vCliente += "'|'" + etCodigo.text.toString()
        codCliente = etCodigo.text.toString()
        var limit: Int = if (etRazonSocial.text.length < 100) { etRazonSocial.text.length } else { 100 }
        vCliente += "'|'" + etRazonSocial.text.toString().substring(0, limit)
        limit = if (etNombreFantasia.text.length < 100) { etNombreFantasia.text.length } else { 100 }
        vCliente += "'|'" + etNombreFantasia.text.toString().substring(0, limit)
        limit = if (etDireccionComercial.text.length < 100) { etDireccionComercial.text.length } else { 100 }
        vCliente += "'|'" + etDireccionComercial.text.toString().substring(0, limit)
        vCliente += "'|'PAR"
        vCliente += "'|'${etDepartamento.text.toString().split("-")[0].trim()}"
        vCliente += "'|'${etCiudad.text.toString().split("-")[0].trim()}"
        limit = if (etBarrio.text.length < 100) { etBarrio.text.length } else { 100 }
        vCliente += "'|'" + etBarrio.text.toString().substring(0, limit)
        vCliente += "'|'" + etRUC.text.toString()
        vCliente += "'|'" + etCI.text.toString()
        vCliente += "'|'" + etCelular.text.toString()
        vCliente += "'|'" + etLineaBaja.text.toString()
        vCliente += "'|'${etFormaPago.text.toString().split("-")[0].trim()}"
        vCliente += "'|'${etTipoCliente.text.toString().split("-")[0].trim()}"
        vCliente += "'|'${etDiasVisita.text.toString().split("-")[0].trim()}"
        limit = if (etCercaDe.text.length < 100) { etCercaDe.text.length } else { 100 }
        vCliente += "'|'" + etCercaDe.text.toString().substring(0, limit)
        limit = if (etEmail.text.length < 100) { etEmail.text.length } else { 100 }
        vCliente += "'|'" + etEmail.text.toString().substring(0, limit)
        vCliente += "'|'" + etLimiteCredito.text.toString()
        vCliente += "'|'${etListaPrecio.text.toString().split("-")[0].trim()}"
        limit = if (etNomRefComercial.text.length < 100) { etNomRefComercial.text.length } else { 100 }
        vCliente += "'|'" + etNomRefComercial.text.toString().substring(0, limit)
        limit = if (etTelRefComercial.text.length < 100) { etTelRefComercial.text.length } else { 100 }
        vCliente += "'|'" + etTelRefComercial.text.toString().substring(0, limit)
        limit = if (etNomRefBancaria.text.length < 100) { etNomRefBancaria.text.length } else { 100 }
        vCliente += "'|'" + etNomRefBancaria.text.toString().substring(0, limit)
        limit = if (etTelRefBancaria.text.length < 100) { etTelRefBancaria.text.length } else { 100 }
        vCliente += "'|'" + etTelRefBancaria.text.toString().substring(0, limit)
        limit = if (tvLatitud.text.length < 100) { tvLatitud.text.length } else { 100 }
        vCliente += "'|'" + tvLatitud.text.toString().substring(0, limit)
        limit = if (tvLongitud.text.length < 100) { tvLongitud.text.length } else { 100 }
        vCliente += "'|'" + tvLongitud.text.toString().substring(0, limit)
        limit = if (etComentario.text.length < 100) { etComentario.text.length } else { 100 }
        vCliente += "'|'" + etComentario.text.toString().substring(0, limit) + "'"*/
        Enviar().execute()
    }

    //	PROCESO DE ENVIAR CLIENTE AL WEB SERVICE
    private inner class Enviar :
        AsyncTask<Void?, Void?, Void?>() {
        private var pbarDialog: ProgressDialog? = null
        override fun onPreExecute() {
            try {
                pbarDialog!!.dismiss()
            } catch (e: Exception) {
            }
            pbarDialog = ProgressDialog.show(
                this@CatastrarCliente, "Un momento...",
                "Enviando el catastro al servidor...", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            respuestaWS = MainActivity2.conexionWS.procesaCatastroClienteFinal(
                codCliente,
                vCliente,
                imagenFachada.toString()
//            ""
            )
            return null
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(unused: Void?) {
            pbarDialog!!.dismiss()
            if (respuestaWS.indexOf("01*") >= 0 || respuestaWS.indexOf("03*") >= 0) {
                val values = ContentValues()
                values.put("ESTADO", "E")
                MainActivity2.bd!!.update(
                    "svm_catastro_cliente",
                    values,
                    "COD_CLIENTE = '$codCliente'",
                    null
                )
            }
            if (respuestaWS.indexOf("Unable to resolve host") > -1) {
                respuestaWS = "07*" + "Verifique su conexion a internet y vuelva a intentarlo"
            }
            val builder = AlertDialog.Builder(this@CatastrarCliente)
            builder.setMessage(respuestaWS.substring(3))
            builder.setCancelable(false)
            builder.setPositiveButton("OK") { _, _ ->
                if (respuestaWS.indexOf("01*") >= 0 || respuestaWS.indexOf("03*") >= 0) {
                    limpiarTodo()
                    obtieneCodCliente(codVendedor)
                    etCodigo.setText("$codVendedor-$codClienteComp")
                }
            }
            val alert = builder.create()
            alert.show()
        }
    }

    // ABRE LA VENTANA PARA BUSCAR CLIENTES CATASTRADOS
    private fun consultarClientesCatastrados() {
        try {
            dialog_clientes_catastrados!!.dismiss()
        } catch (e: Exception) {
        }
        dialog_clientes_catastrados = Dialog(this@CatastrarCliente)
        dialog_clientes_catastrados!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_clientes_catastrados!!.setContentView(R.layout.list_clientes_catastrados5)

        list_view_clientes_catastrados =
            dialog_clientes_catastrados!!.findViewById<View>(R.id.lvdClientes) as ListView
        val imgBuscar = dialog_clientes_catastrados!!.findViewById<View>(R.id.imgBuscar) as ImageButton
        imgBuscar.setOnClickListener {
            save_clientes_catastrados = -1
            buscaClienteCatastrado()
        }
        val btModificar = dialog_clientes_catastrados!!.findViewById<View>(R.id.btModificar) as Button
        btModificar.setOnClickListener {
            if (save_clientes_catastrados != -1) {
                if (estado_cliente_catastrado[save_clientes_catastrados] == "P") {
                    consulta = true
                    cargarDatosCliente(cod_cliente_composicion[save_clientes_catastrados]!!, true)
                    dialog_clientes_catastrados!!.dismiss()
                    save_clientes_catastrados = -1
                }
            }
        }
        val btConsultar = dialog_clientes_catastrados!!.findViewById<View>(R.id.btConsultar) as Button
        btConsultar.setOnClickListener {
            if (save_clientes_catastrados != -1) {
                consulta = true
                cargarDatosCliente(cod_cliente_composicion[save_clientes_catastrados]!!, false)
                dialog_clientes_catastrados!!.dismiss()
                save_clientes_catastrados = -1
            }
        }
        val btCancelar = dialog_clientes_catastrados!!.findViewById<View>(R.id.btEliminar) as Button
        btCancelar.setOnClickListener {
            if (save_clientes_catastrados != -1) {
                if (estado_cliente_catastrado[save_clientes_catastrados] == "P") {
                    val myAlertDialog =
                        AlertDialog.Builder(this@CatastrarCliente)
                    myAlertDialog.setMessage("¿Desea cancelar el catastro?")
                    myAlertDialog.setPositiveButton(
                        "Si"
                    ) { _, _ ->
                        try {
                            MainActivity2.bd!!.delete(
                                "svm_catastro_cliente",
                                "COD_CLIENTE = '" + cod_cliente_composicion[save_clientes_catastrados] + "'",
                                null
                            )
                            save_clientes_catastrados = -1
                            buscaClienteCatastrado()
                            Toast.makeText(
                                applicationContext,
                                "Catastro cancelado con exito...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                applicationContext,
                                "ERROR al CANCELAR",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    myAlertDialog.setNegativeButton(
                        "No"
                    ) { _, _ -> }
                    myAlertDialog.show()
                }
            }
        }
        fecDesde = dialog_clientes_catastrados!!.findViewById<View>(R.id.etDesde) as EditText
        fecHasta = dialog_clientes_catastrados!!.findViewById<View>(R.id.fecHasta) as EditText
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        fecDesde!!.text = sdf.format(Date())
        fecHasta!!.text = sdf.format(Date())
        fecDesde!!.setOnClickListener {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            showDialog(DATE_DIALOG_ID)
            fecha = 0
            fechaDesde()
        }
        fecHasta!!.setOnClickListener {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            showDialog(DATE_DIALOG_ID)
            fecha = 1
            fechaHasta()
        }
        dialog_clientes_catastrados!!.show()
    }

    //	CONTROLA CALENDARIO Y FECHA
    override fun onCreateDialog(id: Int): DatePickerDialog? {
        when (id) {
            DATE_DIALOG_ID -> return DatePickerDialog(
                this,
                mDateSetListener,
                mYear, mMonth, mDay
            )
        }
        return null
    }

    override fun onPrepareDialog(id: Int, dialog: Dialog) {
        when (id) {
            DATE_DIALOG_ID -> (dialog as DatePickerDialog).updateDate(
                mYear,
                mMonth,
                mDay
            )
        }
    }

    private val mDateSetListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            if (fecha == 0) {
                fechaDesde()
            } else {
                fechaHasta()
            }
        }

    private fun fechaDesde() {
        mMonth += 1
        val mes: String = if (mMonth <= 9) {
            "0" + StringBuilder().append(mMonth)
        } else {
            "" + StringBuilder().append(mMonth)
        }
        val dia: String = if (mDay <= 9) {
            "0" + StringBuilder().append(mDay)
        } else {
            "" + StringBuilder().append(mDay)
        }
        fecDesde!!.text = StringBuilder()
            .append(dia).append("/")
            .append(mes).append("/")
            .append(mYear).append(" ")
    }

    private fun fechaHasta() {
        mMonth += 1
        val mes: String = if (mMonth <= 9) {
            "0" + StringBuilder().append(mMonth)
        } else {
            "" + StringBuilder().append(mMonth)
        }
        val dia: String = if (mDay <= 9) {
            "0" + StringBuilder().append(mDay)
        } else {
            "" + StringBuilder().append(mDay)
        }
        fecHasta!!.text = StringBuilder()
            .append(dia).append("/")
            .append(mes).append("/")
            .append(mYear).append(" ")
    }

    //	OBTIENE DATOS DEL CLIENTE SELECCIONADO
    @SuppressLint("SetTextI18n")
    private fun cargarDatosCliente(codCliente: String, editable: Boolean) {
        val cursor: Cursor
        val sql =
            ("select a.COD_CLIENTE  , a.RAZON_SOCIAL		, a.NOM_FANTASIA   , a.DIR_COMERCIAL		, "
                    + " a.CERCA_DE           , a.COD_DEPARTAMENTO, a.COD_PAIS   	   , a.COD_CIUDAD    	, "
                    + " a.BARRIO       		, a.RUC             , a.CI         	   , a.CELULAR			, "
                    + " a.LINEA_BAJA       	, a.EMAIL			, a.CANAL_SUGERIDO , f.DESC_LISTA_PRECIO, "
                    + " a.COD_CONDICION_VENTA, a.COD_TIPO_CLIENTE, a.COD_DIAS_VISITA, b.DESC_DEPARTAMENTO  , "
                    + " b.DESC_CIUDAD        , c.DESCRIPCION  	, d.DESC_CANAL_VENTA as DESC_TIPO_CLIENTE 	, "
                    + " e.DESCRIPCION as DESC_DIAS_VISITA		, a.NOM_REF_COMERCIAL, a.TEL_REF_COMERCIAL	, "
                    + " a.NOM_REF_BANCARIA		, a.TEL_REF_BANCARIA	, a.COMENTARIO       , a.LIMITE_CREDITO		,"
                    + " a.LATITUD				, a.LONGITUD , a.FOTO_FACHADA  "
                    + " from svm_catastro_cliente a, svm_ciudades b	, svm_condicion_venta_cliente c, "
                    + "svm_tipo_cliente d    , svm_tabla_visitas e, svm_precios_fijos f"
                    + " where trim(a.COD_CLIENTE) = '" + codCliente.trim() + "'"
                    + " and a.COD_PAIS = b.COD_PAIS 	  and a.COD_DEPARTAMENTO = b.COD_DEPARTAMENTO "
                    + " and a.COD_CIUDAD = b.COD_CIUDAD   and a.COD_CONDICION_VENTA = c.COD_CONDICION_VENTA"
                    + " and a.COD_TIPO_CLIENTE = d.COD_SUBTIPO and a.COD_DIAS_VISITA = e.CODIGO "
                    + " and a.CANAL_SUGERIDO = f.COD_LISTA_PRECIO")
        cursor = funcion.consultar(sql)
        etRazonSocial.setText(funcion.dato(cursor, "RAZON_SOCIAL"))
        etNombreFantasia.setText(funcion.dato(cursor, "NOM_FANTASIA"))
        etDireccionComercial.setText(funcion.dato(cursor, "DIR_COMERCIAL"))
        etCercaDe.setText(funcion.dato(cursor, "CERCA_DE"))
        etDepartamento.setText(
            funcion.dato(cursor, "COD_DEPARTAMENTO") + " - " + funcion.dato(
                cursor,
                "DESC_DEPARTAMENTO"
            )
        )
        etCiudad.setText(
            funcion.dato(cursor, "COD_CIUDAD") + " - " + funcion.dato(
                cursor,
                "DESC_CIUDAD"
            )
        )
        etBarrio.setText(funcion.dato(cursor, "BARRIO"))
        etRUC.setText(funcion.dato(cursor, "RUC"))
        etCI.setText(funcion.dato(cursor, "CI"))
        etCelular.setText(funcion.dato(cursor, "CELULAR"))
        etLineaBaja.setText(funcion.dato(cursor, "LINEA_BAJA"))
        etEmail.setText(funcion.dato(cursor, "EMAIL"))
        etLimiteCredito.setText(funcion.dato(cursor, "LIMITE_CREDITO"))
        etListaPrecio.setText(
            funcion.dato(cursor, "CANAL_SUGERIDO") + " - " + funcion.dato(
                cursor,
                "DESC_LISTA_PRECIO"
            )
        )
        etFormaPago.setText(
            funcion.dato(cursor, "COD_CONDICION_VENTA") + " - " + funcion.dato(
                cursor,
                "DESCRIPCION"
            )
        )
        etTipoCliente.setText(
            funcion.dato(cursor, "COD_TIPO_CLIENTE") + " - " + funcion.dato(
                cursor,
                "DESC_TIPO_CLIENTE"
            )
        )
        etDiasVisita.setText(
            funcion.dato(cursor, "COD_DIAS_VISITA") + " - " + funcion.dato(
                cursor,
                "DESC_DIAS_VISITA"
            )
        )
        etNomRefComercial.setText(funcion.dato(cursor, "NOM_REF_COMERCIAL"))
        etTelRefComercial.setText(funcion.dato(cursor, "TEL_REF_COMERCIAL"))
        etNomRefBancaria.setText(funcion.dato(cursor, "NOM_REF_BANCARIA"))
        etTelRefBancaria.setText(funcion.dato(cursor, "TEL_REF_BANCARIA"))
        etComentario.setText(funcion.dato(cursor, "COMENTARIO"))
        tvLatitud.text = funcion.dato(cursor, "LATITUD")
        tvLongitud.text = funcion.dato(cursor, "LONGITUD")
        etCodigo.setText(funcion.dato(cursor, "COD_CLIENTE"))
        ultCodListaPrecios = funcion.dato(cursor, "CANAL_SUGERIDO")
        ultCodCiudad = funcion.dato(cursor, "COD_CIUDAD")
        ultCodDep = funcion.dato(cursor, "COD_DEPARTAMENTO")
        ultCodPaisDep = funcion.dato(cursor, "COD_PAIS")
        ultCodFormaPago = funcion.dato(cursor, "COD_CONDICION_VENTA")
        ultCodDiasVisita = funcion.dato(cursor, "COD_DIAS_VISITA")
        ultCodTipoCliente = funcion.dato(cursor, "COD_TIPO_CLIENTE")
        imagenFachada = funcion.dato(cursor, "FOTO_FACHADA")
        if (imagenFachada != null) {
            try {
                val img: ByteArray = foto.stringToByte2(imagenFachada!!)!!
                val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
                (findViewById<View>(R.id.ivFachada) as ImageView).setImageBitmap(
                    bitmap
                )
            } catch (e2: Exception) {
                var err = e2.message
                err += ""
            }
        }
        btEnviar.isEnabled = editable
        btGuardar.isEnabled = editable
        btCancelar.isEnabled = editable
    }

    //	EVENTO AL PRESIONAR BUSCAR
    private fun buscaClienteCatastrado() {
        alist_cliente_catastrado = ArrayList()
        try {
            val rbPendiente: RadioButton = dialog_clientes_catastrados!!.findViewById<View>(R.id.rbPendiente) as RadioButton
            val rbEnviado: RadioButton = dialog_clientes_catastrados!!.findViewById<View>(R.id.rbEnviado) as RadioButton
            val rbTodo: RadioButton = dialog_clientes_catastrados!!.findViewById<View>(R.id.rbTodo) as RadioButton
            var filterEstado = ""
            if (rbPendiente.isChecked) {
                filterEstado = " = 'P'"
            } else if (rbEnviado.isChecked) {
                filterEstado = " = 'E'"
            } else if (rbTodo.isChecked) {
                filterEstado = " <> 'X'"
            }
            val desde: String? = funcion.convertirFechatoSQLFormat(fecDesde!!.text.toString())
            val hasta: String? = funcion.convertirFechatoSQLFormat(fecHasta!!.text.toString())
            val select = ("Select COD_CLIENTE, RAZON_SOCIAL, NOM_FANTASIA,"
                    + "RUC        , CI         , ESTADO  "
                    + " from svm_catastro_cliente "
                    + " where ESTADO " + filterEstado
                    + " and    date(FEC_ALTA)  "
                    + " BETWEEN  date('" + desde + "') AND date('" + hasta + "')"
                    + " Order By date(FEC_ALTA) DESC")
            cursor = MainActivity2.bd!!.rawQuery(select, null)
        } catch (e: Exception) {
            var err = e.message
            err = "" + err
        }
        val nreg = cursor!!.count
        save_clientes_catastrados = if (nreg > 0) {
            0
        } else {
            -1
        }
        cod_cliente_composicion = arrayOfNulls(nreg)
        estado_cliente_catastrado = arrayOfNulls(nreg)
        cursor!!.moveToFirst()
        var cont = 0
        for (i in 0 until nreg) {
            val map2 =
                HashMap<String, String>()
            map2["COD_CLIENTE"] = cursor!!.getString(
                cursor!!
                    .getColumnIndex("COD_CLIENTE")
            )
            cod_cliente_composicion[cont] = cursor!!.getString(cursor!!.getColumnIndex("COD_CLIENTE"))
            map2["RAZON_SOCIAL"] = cursor!!.getString(cursor!!.getColumnIndex("RAZON_SOCIAL"))
            map2["NOM_FANTASIA"] = cursor!!.getString(cursor!!.getColumnIndex("NOM_FANTASIA"))
            map2["RUC"] = cursor!!.getString(cursor!!.getColumnIndex("RUC"))
            map2["CI"] = cursor!!.getString(cursor!!.getColumnIndex("CI"))
            map2["ESTADO"] = cursor!!.getString(cursor!!.getColumnIndex("ESTADO"))
            estado_cliente_catastrado[cont] = cursor!!.getString(cursor!!.getColumnIndex("ESTADO"))
            alist_cliente_catastrado.add(map2)
            cursor!!.moveToNext()
            cont += 1
        }
        sd6 = Adapter_lista_catastro_cliente(
            this@CatastrarCliente, alist_cliente_catastrado,
            R.layout.list_text_clientes_catastrados, arrayOf(
                "COD_CLIENTE", "RAZON_SOCIAL", "NOM_FANTASIA",
                "RUC", "CI", "ESTADO"
            ), intArrayOf(
                R.id.td1, R.id.td2, R.id.td3,
                R.id.td4, R.id.td5, R.id.td6
            )
        )
        list_view_clientes_catastrados!!.adapter = sd6
        list_view_clientes_catastrados!!.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                save_clientes_catastrados = position
                list_view_clientes_catastrados!!.invalidateViews()
            }
    }

    inner class Adapter_lista_catastro_cliente(
        context: Context,
        items: ArrayList<HashMap<String, String>>,
        resource: Int,
        from: Array<String>,
        to: IntArray?
    ) :
        SimpleAdapter(this, items, resource, from, to) {
        var _sqlupdate: String? = null
        private val colors = intArrayOf(
            Color.parseColor("#696969"),
            Color.parseColor("#808080")
        )

        inner class ViewHolder

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val colorPos = position % colors.size
            view.setBackgroundColor(colors[colorPos])
            val holder =
                ViewHolder()
            if (save_clientes_catastrados == position) {
                view.setBackgroundColor(Color.BLUE)
            }
            view.tag = holder
            return view
        }
    }

    //	FUNCION QUE VALIDA QUE TODOS LOS CAMPOS ESTEN CARGADOS
    private fun validaCampo(tv: EditText, limite: Int):Boolean{
        return if (tv.text.toString() == "") {
            Toast.makeText(
                this@CatastrarCliente,
                "Debe completar todos los campos",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else {
            if (tv.text.toString().length > limite) {
                tv.setText(tv.text.toString().substring(0, limite))
            }
            true
        }
    }
    private fun validaCampo(tv: TextView, limite: Int):Boolean{
        return if (tv.text.toString() == "") {
            Toast.makeText(
                this@CatastrarCliente,
                "Debe completar todos los campos",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else {
            if (tv.text.toString().length > limite) {
                tv.setText(tv.text.toString().substring(0, limite))
            }
            true
        }
    }
    private fun validaCampos(): Boolean {
        if (!validaCampo(etRazonSocial, 50)) {return false}
        if (!validaCampo(etNombreFantasia, 50)) {return false}
        if (!validaCampo(etDireccionComercial, 150)) {return false}
        if (!validaCampo(etCercaDe, 50)) {return false}
        if (!validaCampo(etDepartamento, 50)) {return false}
        if (!validaCampo(etCiudad, 50)) {return false}
        if (!validaCampo(etBarrio, 100)) {return false}
        if (!validaCampo(etRUC, 15)) {return false}
        if (!validaCampo(etCI, 15)) {return false}
        if (!validaCampo(etCelular, 15)) {return false}
        if (!validaCampo(etLineaBaja, 15)) {return false}
        if (!validaCampo(etEmail, 30)) {return false}
        if (!validaCampo(etLimiteCredito, 17)) {return false}
        if (!validaCampo(etListaPrecio, 50)) {return false}
        if (!validaCampo(etFormaPago, 50)) {return false}
        if (!validaCampo(etTipoCliente, 50)) {return false}
        if (!validaCampo(etDiasVisita, 50)) {return false}
        if (!validaCampo(tvLatitud, 50)) {return false}
        if (!validaCampo(tvLongitud, 50)) {return false}
        if (!validaCampo(etNomRefComercial, 50)) {return false}
        if (!validaCampo(etTelRefComercial, 50)) {return false}
        if (!validaCampo(etNomRefBancaria, 50)) {return false}
        if (!validaCampo(etTelRefBancaria, 50)) {return false}
        if (!validaCampo(etComentario, 200)) {return false}
        if (imagenFachada == null) {
            Toast.makeText(this@CatastrarCliente, "Debe tomar foto de fachada", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //	GUARDAR CATASTRO LOCALMENTE
    @SuppressLint("SimpleDateFormat", "Recycle")
    private fun guardarCatastro() {
        val values = ContentValues()
        values.put("COD_VENDEDOR", codVendedor)
        values.put("NOM_VENDEDOR", FuncionesUtiles.usuario["NOMBRE"])
        values.put("RAZON_SOCIAL", etRazonSocial.text.toString())
        values.put("NOM_FANTASIA", etNombreFantasia.text.toString())
        values.put("DIR_COMERCIAL", etDireccionComercial.text.toString())
        values.put("CERCA_DE", etCercaDe.text.toString())
        values.put("COD_PAIS", "PAR")
        values.put("COD_DEPARTAMENTO", etDepartamento.text.toString().split("-")[0].trim())
        values.put("COD_CIUDAD", etCiudad.text.toString().split("-")[0].trim())
        values.put("BARRIO", etBarrio.text.toString())
        values.put("RUC", etRUC.text.toString())
        values.put("CI", etCI.text.toString())
        values.put("CELULAR", etCelular.text.toString())
        values.put("LINEA_BAJA", etLineaBaja.text.toString())
        values.put("EMAIL", etEmail.text.toString())
        values.put("LIMITE_CREDITO", etLimiteCredito.text.toString())
        values.put("CANAL_SUGERIDO", etListaPrecio.text.toString().split("-")[0].trim())
        values.put("COD_CONDICION_VENTA", etFormaPago.text.toString().split("-")[0].trim())
        values.put("COD_TIPO_CLIENTE", etTipoCliente.text.toString().split("-")[0].trim())
        values.put("COD_DIAS_VISITA", etDiasVisita.text.toString().split(" ")[0].trim())
        values.put("NOM_REF_COMERCIAL", etNomRefComercial.text.toString())
        values.put("TEL_REF_COMERCIAL", etTelRefComercial.text.toString())
        values.put("NOM_REF_BANCARIA", etNomRefBancaria.text.toString())
        values.put("TEL_REF_BANCARIA", etTelRefBancaria.text.toString())
        values.put("COMENTARIO", etComentario.text.toString())
        values.put("LATITUD", tvLatitud.text.toString())
        values.put("LONGITUD", tvLongitud.text.toString())
        values.put("COD_CLIENTE", etCodigo.text.toString().trim())
        val cant = "Select * from svm_catastro_cliente where COD_CLIENTE = '" + etCodigo.text.toString() + "'"
        val cursor: Cursor = MainActivity2.bd!!.rawQuery(cant, null)
        cursor.moveToFirst()
        if (cursor.count == 0) {
            val d1: String?
            val cal = Calendar.getInstance()
            val dfDate = SimpleDateFormat("yyyy-MM-dd")
            d1 = dfDate.format(cal.time)
            values.put("FEC_ALTA", d1)
            values.put("ESTADO", "P")
            values.put("COD_CLI_VEND", codClienteComp)
            values.put("FOTO_FACHADA", imagenFachada)
            MainActivity2.bd!!.insert("svm_catastro_cliente", null, values)
        } else {
            values.put("FOTO_FACHADA", imagenFachada)
            MainActivity2.bd!!.update(
                "svm_catastro_cliente", values,
                "COD_CLIENTE = '" + etCodigo.text.toString() + "'", null
            )
        }
        Toast.makeText(this@CatastrarCliente, "Catastro guardado con exito", Toast.LENGTH_SHORT).show()
    }

    //	ELIMINA EL CATASTRO ACTUAL
    private fun cancelarCatastro() {
        val myAlertDialog = AlertDialog.Builder(this@CatastrarCliente)
        myAlertDialog.setMessage("¿Desea cancelar el catastro?")
        myAlertDialog.setPositiveButton("Si") { _, _ ->
            try {
                val codigo = findViewById<View>(R.id.etCodigo) as EditText
                MainActivity2.bd!!.delete(
                    "svm_catastro_cliente",
                    "COD_CLIENTE = '" + codigo.text + "'",
                    null
                )
                Toast.makeText(
                    applicationContext,
                    "Catastro cancelado con exito...",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "ERROR al CANCELAR", Toast.LENGTH_SHORT).show()
            }
        }
        myAlertDialog.setNegativeButton("No") { _, _ -> }
        myAlertDialog.show()
    }

    //	LIMPIA TODOS LOS CAMPOS
    private fun limpiarTodo() {
        consulta = false
        etRazonSocial.setText("")
        etNombreFantasia.setText("")
        etDireccionComercial.setText("")
        etCercaDe.setText("")
        etDepartamento.setText("")
        etCiudad.setText("")
        etBarrio.setText("")
        etRUC.setText("")
        etCI.setText("")
        etCelular.setText("")
        etLineaBaja.setText("")
        etEmail.setText("")
        etLimiteCredito.setText("")
        etListaPrecio.setText("")
        etFormaPago.setText("")
        etTipoCliente.setText("")
        etDiasVisita.setText("")
        etNomRefComercial.setText("")
        etTelRefComercial.setText("")
        etNomRefBancaria.setText("")
        etTelRefBancaria.setText("")
        etComentario.setText("")
        tvLatitud.text = ""
        tvLongitud.text = ""
        ultCodCiudad = ""
        ultCodDep = ""
        ultCodPaisDep = ""
        ultCodFormaPago = ""
        ultCodDiasVisita = ""
        ultCodTipoCliente = ""
        ultCodListaPrecios = ""
        imagenFachada = null
        btEnviar.isEnabled = true
        btGuardar.isEnabled = true
        btCancelar.isEnabled = true
        ivFachada.setImageBitmap(null)
    }

    //	MUESTRA LA LISTA DE CONDICIONES DE VENTA
    private fun buscarDepartamento(){
        val dialogo = DialogoBusqueda(
            this, "svm_ciudades", "COD_DEPARTAMENTO",
            "DESC_DEPARTAMENTO", "COD_DEPARTAMENTO", "",
            etDepartamento, null
        )
        dialogo.cargarDialogo(false)
    }
    private fun buscarCiudad(){
        if (etDepartamento.text.toString().isEmpty()){
            funcion.toast(this, "No ha especificado el departamento.")
            return
        }
        val dialogo = DialogoBusqueda(
            this, "svm_ciudades", "COD_CIUDAD",
            "DESC_CIUDAD", "COD_CIUDAD",
            " AND COD_DEPARTAMENTO = '${etDepartamento.text.toString().split("-")[0].trim()}'",
            etCiudad, null
        )
        dialogo.cargarDialogo(false)
    }
    private fun buscarTipoCliente(){
        val dialogo = DialogoBusqueda(
            this, "svm_tipo_cliente", "COD_SUBTIPO",
            "DESC_CANAL_VENTA", "COD_SUBTIPO", "",
            etTipoCliente, null
        )
        dialogo.cargarDialogo(false)
    }
    private fun buscarListaDePrecio(){
        val dialogo = DialogoBusqueda(
            this, "svm_precios_fijos", "COD_LISTA_PRECIO",
            "DESC_LISTA_PRECIO", "Cast(COD_LISTA_PRECIO as double) desc LIMIT 1", "",
            etListaPrecio, null
        )
        dialogo.cargarDialogo(false)
    }
    private fun buscarFormaDePago(){
        val dialogo = DialogoBusqueda(
            this, "svm_condicion_venta_cliente", "COD_CONDICION_VENTA",
            "DESCRIPCION", " DESCRIPCION, Cast(COD_CONDICION_VENTA as double) ", "",
            etFormaPago, null
        )
        dialogo.cargarDialogo(false)
    }
    private fun buscarDiaDeVisita(){
        if (etCiudad.text.toString().isEmpty()){
            funcion.toast(this, "No ha especificado la ciudad.")
            return
        }
        val sql = "SELECT FRECUENCIA FROM svm_ciudades " +
                " WHERE COD_DEPARTAMENTO = '${etDepartamento.text.toString().split("-")[0].trim()}' " +
                "   AND COD_CIUDAD = '${etCiudad.text.toString().split("-")[0].trim()}' "
        val frecuencia = funcion.dato(funcion.consultar(sql), "FRECUENCIA")
        val dialogo = DialogoBusqueda(
            this, "svm_tabla_visitas", "CODIGO",
            "DESCRIPCION", " Cast(CODIGO as double) ", " AND FRECUENCIA = '${frecuencia.trim()}' ",
            etDiasVisita, null
        )
        dialogo.cargarDialogo(false)
    }
    private fun obtieneCodCliente(codVendedor: String) {
        val codigo: Int
        var cursor: Cursor
        val codClienteVendedor = "Select COD_CLI_VEND from svm_vendedor_pedido where cod_vendedor = '$codVendedor'"
        cursor = funcion.consultar(codClienteVendedor)
        codigo = if (cursor.count > 0) {
            try {
                funcion.dato(cursor, "COD_CLI_VEND").toInt()
            } catch (e: Exception) {
                1
            }
        } else {
            1
        }
        val max = "select max (Cast(COD_CLI_VEND as double)) as MAXIMO from svm_catastro_cliente"
        val codigo2: Int
        try {
            cursor =funcion.consultar(max)
        } catch (e: Exception) {
            var err = e.message
            err = "" + err
        }
        cursor.moveToFirst()
        codigo2 = if (cursor.count > 0) {
            try {
                funcion.dato(cursor, "MAXIMO").toInt() + 1
            } catch (e: Exception) {
                1
            }
        } else {
            1
        }
        codClienteComp = if (codigo > codigo2) {
            codigo
        } else {
            codigo2
        }
    }

    /*override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        if (requestCode == 1) {
            if (data == null) {
                try {
                    if (File(name).exists()) {
                        try {
                            val options = BitmapFactory.Options()
                            options.inSampleSize = 8
                            val fis = FileInputStream(name)
                            var bm = BitmapFactory.decodeStream(fis, null, options)
                            options.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(name, options)
                            val w: Int
                            val h: Int
                            w = options.outWidth
                            h = options.outHeight
                            bm = if (w < h) {
                                val y = h.toFloat() / 768
                                foto.resizeImage(this@CatastrarCliente, bm!!, (w / y).toInt(), (h / y).toInt())
                            } else {
                                val x = w.toFloat() / 768
                                foto.resizeImage(this@CatastrarCliente, bm!!, (w / x).toInt(), (h / x).toInt())
                            }
                            var out: FileOutputStream? = null
                            try {
                                out = FileOutputStream(name)
                                bm!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
                                // bmp is your Bitmap instance
                                // PNG is a lossless format, the compression factor (100) is ignored
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                try {
                                    out?.close()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }

//							Bitmap bm = BitmapFactory.decodeFile(name);
                            ivFachada.setImageBitmap(bm)
                        } catch (e2: Exception) {
                            var err = e2.message
                            err += ""
                        }
                        val output = Uri.fromFile(File(name))
                        val inputStream: InputStream?
                        val imagen: ByteArray?
                        val strImagen: String
                        inputStream = contentResolver.openInputStream(output)
                        imagen = foto.readBytes(output, inputStream!!)
                        strImagen = foto.byteToString2(imagen!!).toString()
                        imagenFachada = strImagen
                        File(name).delete()
                    }
                } catch (e: Exception) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }
    }*/

    companion object {
        lateinit var sd6 : Adapter_lista_catastro_cliente
        lateinit var tvmLatitud  : TextView
        lateinit var tvmLongitud : TextView
        const val DATE_DIALOG_ID = 1
        var codVendedor = ""
        lateinit var foto : FuncionesFoto
    }

    private fun sacarFoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val code = 1
        tipoFoto = "1"

        try {
            nombre = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/fachada.jpg"

            lateinit var output : Uri

            if (Build.VERSION.SDK_INT >= 25){
                sacarFoto25()
            } else {
                output = Uri.fromFile(File(nombre))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output)
                startActivityForResult(intent, code)
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun crearImagen(): File {
        // Crea el archivo para ubicar la foto
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("fachada", ".jpg", storageDir).apply {
            // Save a file: path for use with ACTION_VIEW intents
            nombre = absolutePath
        }
    }

    private fun sacarFoto25() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    crearImagen()
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
                photoFile?.also {
                    val output : Uri = FileProvider.getUriForFile(
                        this,
                        "apolo.vendedores.com.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, output)
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagenFachada = foto.foto1(requestCode, resultCode, data, nombre, ivFachada, tipoFoto)
        if (imagenFachada!!.isEmpty()){
            imagenFachada = foto.foto2(requestCode, resultCode, data, ivFachada, ivFachada, nombre, tipoFoto)
        }
    }

}


