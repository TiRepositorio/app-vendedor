package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.*
import apolo.vendedores.com.utilidades.Adapter
import kotlinx.android.synthetic.main.activity_pedidos.*
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.ven_ped_lista_pedidos_producto.view.*
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.round

class Pedidos : AppCompatActivity() {

    companion object{
        var id : String = ""
        var nuevo = true
        var posProducto : Int = 0
        var posDetalle : Int = 0
        var maximo = 0
        var claveAutorizacion : String  = ""
        var articulosDetalle : String  = ""
        var fechaInt : String = ""
        var totalPedido : String = ""
        var totalComprobanteDecimal = 0.0
        var depo : String = "V"
        var vent : String = "N"
        var indBloqueado = "N"
        var indPresencial = ""
        var pedidoBloqCond : String = "N"
        lateinit var etAccionPedidos   : EditText
        lateinit var etFechaPedido   : EditText
        lateinit var etTotalPedidos  : EditText
        lateinit var etNroOrdenCompra: EditText
        lateinit var etNroPedidos    : EditText
        lateinit var etSubtotales    : EditText
        lateinit var etObservacionPedido   : EditText
        lateinit var etDescVariosPedidos   : EditText
        lateinit var etDescFinancPedidos   : EditText
        lateinit var etTotalDescPedidos    : EditText
        lateinit var spListaPrecios     : FuncionesSpinner
        lateinit var spCondicionDeVenta : FuncionesSpinner
        lateinit var spReferencias : FuncionesSpinner
    }

    lateinit var funcion : FuncionesUtiles
    private lateinit var dispositivo : FuncionesDispositivo
    private lateinit var lm: LocationManager
    private lateinit var telMgr : TelephonyManager
    private var listaProductos : ArrayList<HashMap<String, String>> = ArrayList()
    private var listaDetalles : ArrayList<HashMap<String, String>> = ArrayList()
    private var ultimaCondicion : Int = 0
    private var modificacion : String = "N"
    var indBloqueado = "N"
    var descVarAnterior = "0"
    var descFinAnterior = "0"
    var sumaDescuento : Float = "0".toFloat()
    private var cantidadMinima: Int = 1
    private var porcDescuentos = 0.0
    var codListaPrecio = ""
    var codMoneda = ""
    private var diasInicial = 0
    var decimales = ""
    var indActualizado = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        funcion = FuncionesUtiles(
            this,
            llTitulo,
            llBotonVendedores,
            llBuscar,
            spBuscar,
            etBuscar,
            btBuscar
        ) //solo buscador
        inicializarElementos()
        if (!nuevo){
            cargarDetalle()
        }
        inicializarElementosCierre()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    fun inicializarElementos(){
        posProducto = 0
        posDetalle = 0
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        tvCodCliente.text = ListaClientes.codCliente + "-" + ListaClientes.codSubcliente
        tvDescCliente.text = ListaClientes.descCliente
//        funcion.addItemSpinner(this,"Codigo-Descripcion-Cod. Barra","cod_articulo-cod_articulo-cod_articulo")
        funcion.addItemSpinner(
            this,
            "Codigo-Descripcion-Cod. Barra",
            "a.COD_ARTICULO-a.DESC_ARTICULO-a.COD_BARRA"
        )
        dispositivo = FuncionesDispositivo(this)
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        listaPrecio()
        generaSpinVenta()
        condicionDeVenta()
        btBuscar.setOnClickListener{buscarProducto()}
        spReferenciasListener()
        inicializaET(tvdCantidad)
        inicializaET(tvdDesc)
        actualizaIndiceDePromocion()
        buscarProducto()
        inicializaETAccion(accionPedido)
        inicializaETAccion(accion)
        tvdCantidad.setOnClickListener{funcion.dialogoEntradaNumero(tvdCantidad, this)}
        tvdDesc.setOnClickListener{funcion.dialogoEntradaNumero(tvdDesc, this)}
        llPedidos.visibility = View.VISIBLE
        btVender.setOnClickListener{insertarPedido()}
        btnPromociones.setOnClickListener{promociones(btnPromociones)}
        etFechaPedido  = etFecha
        etTotalPedidos = etTotalPedido
        etNroOrdenCompra = etOrdenCompra
        etNroPedidos = etNroPedido
        etSubtotales = etSubtotal
        etAccionPedidos = accion
        etDescVariosPedidos = etDescVarios
        etDescFinancPedidos = etDescFin
        etObservacionPedido = etObservacion
        etTotalDescPedidos  = etTotalDesc
        habilitarSpinnersCabecera(nuevo)
        cargarAtriculosDetalle()
//        spVenta.isEnabled == (ListaClientes.indDirecta.trim() == "S")
        spVenta.isEnabled = ListaClientes.indDirecta == "S"
        if (nuevo){
            articulosDetalle = ""
//            lvProductos.invalidateViews()
        }
    }

    //Mostrar los layouts correspondientes
    @RequiresApi(Build.VERSION_CODES.N)
    fun mostrarContenido(view: View) {
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        tvPedido.setBackgroundColor(Color.parseColor("#757575"))
        tvDetalle.setBackgroundColor(Color.parseColor("#757575"))
        tvCerrar.setBackgroundColor(Color.parseColor("#757575"))
        view.setBackgroundColor(Color.parseColor("#116600"))
        llPedidos.visibility = View.GONE
        llDetalles.visibility = View.GONE
        llCerrar.visibility = View.GONE
        when(view.id){
            tvPedido.id -> llPedidos.visibility = View.VISIBLE
            tvDetalle.id -> llDetalles.visibility = View.VISIBLE
            tvCerrar.id -> llCerrar.visibility = View.VISIBLE
        }
        cargarDetalle()
    }

    //PEDIDOS
    @RequiresApi(Build.VERSION_CODES.N)
    fun listaPrecio(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        val campos = " COD_LISTA_PRECIO,DESC_LISTA,IND_DEFECTO,COD_MONEDA,DECIMALES "
        val tabla = " cliente_list_prec "
        val where : String = " COD_CLIENTE        = '" + ListaClientes.codCliente + "' " +
                             " AND COD_SUBCLIENTE =  '" + ListaClientes.codSubcliente + "' " +
                             " AND COD_VENDEDOR   = '" + ListaClientes.codVendedor + "' "
        val whereOpcional = ""
        val group = ""
        val order = ""
        spListaPrecios = FuncionesSpinner(this, spListaPrecio)
        spListaPrecios.generaSpinner(
            campos,
            tabla,
            where,
            whereOpcional,
            group,
            order,
            "COD_LISTA_PRECIO",
            "LP: "
        )
        spListaPrecio.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                codListaPrecio = spListaPrecios.getDato("COD_LISTA_PRECIO")
                codMoneda = spListaPrecios.getDato("COD_MONEDA")
                decimales = spListaPrecios.getDato("DECIMALES")
                actualizaIndiceDePromocion()
            }
        }
        codListaPrecio = spListaPrecios.getDato("COD_LISTA_PRECIO")
        codMoneda = spListaPrecios.getDato("COD_MONEDA")
        decimales = spListaPrecios.getDato("DECIMALES")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun spReferencias(){
        val campos : String = " distinct a.REFERENCIA , a.MULT, a.DIV        , a.IND_BASICO      , " +
                              " a.COD_IVA    , a.PORC_IVA           , a.COD_UNIDAD_REL  , " +
                              " (b.CANT_MINIMA/a.mult) CANT_MINIMA  , (IFNULL(b.PREC_CAJA,1)/IFNULL(b.MULT,1)) * a.MULT " +
                              " /*CASE  WHEN TRIM(a.IND_BASICO) = 'S' THEN " +
                              "            ((CASE WHEN CAST(a.MULT AS NUMBER) = 1 THEN b.PREC_UNID ELSE CAST(b.PREC_CAJA AS NUMBER) / CAST(${listaProductos[posProducto]["MULT"]} AS NUMBER) END) * CAST(a.MULT AS NUMBER) )" +
                              "       ELSE CASE WHEN CAST(a.COD_UNIDAD_REL AS NUMBER) > 2 THEN CAST(b.PREC_CAJA AS NUMBER) / CAST(${listaProductos[posProducto]["MULT"]} AS NUMBER) * CAST(a.MULT AS NUMBER)  ELSE b.PREC_CAJA END END */ " +
                              " AS PRECIO" //+
        val tabla = " svm_st_articulos a, svm_articulos_precios b "
        val where : String = "     a.COD_ARTICULO = '" + listaProductos[posProducto]["COD_ARTICULO"] + "' " +
                             " and a.COD_ARTICULO = b.COD_ARTICULO and b.COD_VENDEDOR = '" + ListaClientes.codVendedor + "' " +
                             " and b.COD_LISTA_PRECIO = '" + codListaPrecio + "' "
        val whereOpcional = ""
        val group = ""
        val order = ""
        spReferencias = FuncionesSpinner(this, spReferencia)
        spReferencias.generaSpinner(
            campos,
            tabla,
            where,
            whereOpcional,
            group,
            order,
            "REFERENCIA",
            ""
        )
        cbReferencias(campos, tabla, where, whereOpcional)
    }

    private fun cbReferencias(campos: String, tabla: String, where: String, whereOpcional: String){
        var sql : String = "SELECT " + campos +
                "  FROM " + tabla +
                " WHERE " + where
        if (whereOpcional.trim() != ""){ sql += whereOpcional }
        val lista : ArrayList<HashMap<String, String>> = ArrayList()
        esconderCB()
        funcion.cargarLista(lista, funcion.consultar(sql))
        for (i in 0 until lista.size){
            when (i){
                0 -> {
                    tvUM1.text = funcion.entero(lista[i]["PRECIO"].toString().toInt())
                    cbUM1.text = lista[i]["REFERENCIA"]
                    tvUM1.visibility = View.VISIBLE
                    cbUM1.visibility = View.VISIBLE
                }
                1 -> {
                    tvUM2.text = funcion.entero(lista[i]["PRECIO"].toString().toInt())
                    cbUM2.text = lista[i]["REFERENCIA"]
                    tvUM2.visibility = View.VISIBLE
                    cbUM2.visibility = View.VISIBLE
                }
                2 -> {
                    tvUM3.text = funcion.entero(lista[i]["PRECIO"].toString().toInt())
                    cbUM3.text = lista[i]["REFERENCIA"]
                    tvUM3.visibility = View.VISIBLE
                    cbUM3.visibility = View.VISIBLE
                }
                3 -> {
                    tvUM4.text = funcion.entero(lista[i]["PRECIO"].toString().toInt())
                    cbUM4.text = lista[i]["REFERENCIA"]
                    tvUM4.visibility = View.VISIBLE
                    cbUM4.visibility = View.VISIBLE
                }
            }
        }
        cbUM1.setOnClickListener {
            spReferencia.setSelection(0)
            cbUM2.isChecked = false
            cbUM3.isChecked = false
            cbUM4.isChecked = false
            if (!cbUM1.isChecked) {cbUM1.isChecked = true}
        }
        cbUM2.setOnClickListener {
            spReferencia.setSelection(1)
            cbUM1.isChecked = false
            cbUM3.isChecked = false
            cbUM4.isChecked = false
            if (!cbUM2.isChecked) {cbUM2.isChecked = true}
        }
        cbUM3.setOnClickListener {
            spReferencia.setSelection(2)
            cbUM1.isChecked = false
            cbUM2.isChecked = false
            cbUM4.isChecked = false
            if (!cbUM3.isChecked) {cbUM3.isChecked = true}
        }
        cbUM4.setOnClickListener {
            spReferencia.setSelection(2)
            cbUM1.isChecked = false
            cbUM2.isChecked = false
            cbUM3.isChecked = false
            if (!cbUM4.isChecked) {cbUM4.isChecked = true}
        }
        cbUM1.isChecked = true
        cbUM2.isChecked = false
        cbUM3.isChecked = false
        cbUM4.isChecked = false
    }

    private fun esconderCB(){
        tvUM1.visibility = View.GONE
        tvUM2.visibility = View.GONE
        tvUM3.visibility = View.GONE
        tvUM4.visibility = View.GONE
        cbUM1.visibility = View.GONE
        cbUM2.visibility = View.GONE
        cbUM3.visibility = View.GONE
        cbUM4.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun spReferenciasListener(){
        spReferencia.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                cargarDetalleProducto()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun generaSpinVenta() {
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        val lista : ArrayList<HashMap<String, String>> = ArrayList()
        val cursor: Cursor = funcion.consultar(
            "Select IND_DIRECTA  from svm_cliente_vendedor " + " where cod_cliente  = '"
                    + ListaClientes.codCliente + "' " + " and cod_subcliente = '"
                    + ListaClientes.codSubcliente + "'   and COD_VENDEDOR = '" + ListaClientes.codVendedor + "' "
        )
        cursor.moveToFirst()
        val indDirecta = cursor.getString(cursor.getColumnIndex("IND_DIRECTA"))
        if (indDirecta == "N") {
            spVenta.isEnabled = false
        }
        val ven : Array<String> = arrayOf("VN", "VD")
        val map = HashMap<String, String>()
        map["referencia"] = "N"
        lista.add(map)
        map["referencia"] = "V"
        lista.add(map)

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.support_simple_spinner_dropdown_item, ven
        )
        spVenta.adapter = adapter
        if (vent == "N") {
            spVenta.setSelection(0)
        } else {
            spVenta.setSelection(1)
        }
        spVenta.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
                vent = ven[position]
                vent = if (vent == "VN") {
                    "N"
                } else {
                    "V"
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun condicionDeVenta(){
        val campos = " COD_CONDICION_VENTA,DESCRIPCION,TIPO_CONDICION,DIAS_INICIAL,ABREVIATURA,PORC_DESC,MONTO_MIN_DESC "
        val tabla = " svm_condicion_venta "
        val where : String = " cod_condicion_venta in " +
                             " ( select COD_CONDICION_VENTA from svm_cond_venta_vendedor " +
                             "    where COD_LISTA_PRECIO = '" + codListaPrecio + "' " +
                             "      and COD_CLIENTE      = '" + ListaClientes.codCliente    + "' " +
                             "      and COD_SUBCLIENTE   = '" + ListaClientes.codSubcliente + "' " +
                             "      and COD_VENDEDOR     = '" + ListaClientes.codVendedor   + "') "
        val whereOpcional : String = if (ListaClientes.indEspecial.trim() == "S") {
                                        " AND (tipo_condicion = '" + ListaClientes.tipCondicion + "' or tipo_condicion = 'S' "
                                     } else { "AND (tipo_condicion = '" + ListaClientes.tipCondicion + "') " }
        val group = ""
        val order = " DESCRIPCION "
        spCondicionDeVenta = FuncionesSpinner(this, spCondicionVenta)
        spCondicionDeVenta.generaSpinner(
            campos,
            tabla,
            where,
            whereOpcional,
            group,
            order,
            "DESCRIPCION",
            ""
        )
        spCondicionVenta.setSelection(
            spCondicionDeVenta.getIndex(
                "COD_CONDICION_VENTA",
                ListaClientes.codCondicion
            )
        )
        ultimaCondicion = spCondicionVenta.selectedItemPosition
        diasInicial = spCondicionDeVenta.getDato("DIAS_INICIAL").toInt()
        spCondicionVenta.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                seleccionarCondicion()
            }
        }
        modificacion = "S"
        tvCondicionVenta.text = "CV: ${spCondicionVenta.selectedItem}"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun inicializaETAccion(etAccion: EditText){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        etAccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etAccion.text.toString() == "condicion") {
                    accionCondicion()
                    return
                }
                if (etAccion.text.toString().trim() == "noCondicion") {
                    noAccionCondicion()
                    return
                }
                if (etAccion.text.toString().trim().indexOf("guardarDetalle2") > -1) {
                    claveAutorizacion = etAccion.text.toString().split("*")[1]
                    indBloqueado = "S"
                    guardaDetalle2()

                    return
                }
                if (etAccion.text.toString().trim() == "noGuardarDetalle2") {
                    indBloqueado = "S"
                    tvdDesc.setText(tvdPrecioReferencia.text)
                    return
                }
                if (etAccion.text.toString().trim() == "eliminarDetalle") {
                    eliminarDetalle()
                    cargarDetalle()
                    return
                }
                if (etAccion.text.toString().trim() == "ventaRapida") {
                    ventaRapida()
                    return
                }
                if (etAccion.text.toString().trim() == "promociones") {
                    promociones(etAccion)
                    return
                }
                if (etAccion.text.toString().trim() == "actualizarDetalle") {
                    cargarDetalle()
                    return
                }
                if (etAccion.text.toString().trim() == "recalcularTotal") {
                    recalcularTotal()
                    return
                }
                if (etAccion.text.toString().trim() == "eliminarDetallePromocion") {
                    eliminarDetallePromocion()
                    cargarDetalle()
                    return
                }
                if (etAccion.text.toString().split("*")[0].trim() == "noDescuentosVarios") {
                    noAutorizaDescuentosVarios()
                    claveAutorizacion = etAccion.text.toString().split("*")[1].trim()
                    recalcularTotal()
                    return
                }
                if (etAccion.text.toString().split("*")[0].trim() == "descuentosVarios") {
                    autorizaDescuentosVarios()
                    claveAutorizacion = etAccion.text.toString().split("*")[1].trim()
                    recalcularTotal()
                    return
                }
                if (etAccion.text.toString().split("*")[0].trim() == "descuentosVariosDecimal") {
                    autorizaDescuentosVariosDecimal()
                    claveAutorizacion = etAccion.text.toString().split("*")[1].trim()
                    recalcularTotal()
                    return
                }
                if (etAccion.text.toString().split("*")[0].trim() == "cargarDetallePedido") {
                    cargarDetalle()
                    return
                }
                if (etAccion.text.toString().split("*")[0].trim() == "cerrarTodo") {
                    finish()
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun seleccionarCondicion(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        if (spCondicionDeVenta.getIndex("COD_CONDICION_VENTA", ListaClientes.codCondicion).toString() != spCondicionVenta.selectedItemPosition.toString()
            && spCondicionVenta.selectedItemPosition != ultimaCondicion){
            val dialogo = DialogoAutorizacion(this)
            dialogo.dialogoAutorizacion(
                "condicion",
                "noCondicion",
                accionPedido,
                "Ingrese su codigo de vendedor",
                "¿Seguro que desea modificar la condicion de venta?",
                ListaClientes.codVendedor,
                "Pedido bloqueado por la condicion de venta!",
                "El codigo de vendedor ingresado es incorrecto"
            )
        } else {
            ultimaCondicion = spCondicionVenta.selectedItemPosition
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun accionCondicion(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        ultimaCondicion = spCondicionVenta.selectedItemPosition
        pedidoBloqCond = "N"
        if (spCondicionDeVenta.getDato("DIAS_INICIAL").toInt() <= ListaClientes.diasInicial.toInt()){
            pedidoBloqCond = "S"
        } else {
            if (modificacion == "S"){
                etDescFin.setText("")
                etDescVarios.setText("")
                etTotalDesc.setText("")
                funcion.mensaje(this, "Atencion!", "Pedido bloqueado por la condicion de venta!")
            }
        }
        diasInicial = spCondicionDeVenta.getDato("DIAS_INICIAL").toInt()
        accionPedido.setText("")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun noAccionCondicion(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        spCondicionVenta.setSelection(ultimaCondicion)
        accionPedido.setText("")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buscarProducto(){
        val campos = " a.*,  b.TIP_SURTIDO "
        val groupBy = ""
        val orderBy = " a.DESC_ARTICULO ASC "
//        val tabla = " $svmArticulosPrecios a left join $svmSurtidoEficiente b " +
        val tabla = " svm_articulos_precios a LEFT JOIN svm_surtido_eficiente b " +
                    "    ON   a.COD_EMPRESA		= b.COD_EMPRESA  " +
                    "   AND   a.COD_ARTICULO	= b.COD_ARTICULO " +
                    "   AND   b.COD_CLIENTE     = '${ListaClientes.codCliente}' " +
                    "   AND   b.COD_SUBCLIENTE  = '${ListaClientes.codSubcliente}' " +
                    "   AND   b.TIP_CLIENTE     = '${ListaClientes.tipCliente}' "
        val where : String = "  AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                             "  AND a.COD_LISTA_PRECIO = '${codListaPrecio}' "
        cargarLista(funcion.buscar(tabla, campos, groupBy, orderBy, where))
        mostrar()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun cargarLista(cursor: Cursor){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        listaProductos = ArrayList()
        for (i in 0 until cursor.count){
            val dato : HashMap<String, String> = HashMap()
            for (j in 0 until cursor.columnCount){
                try {
                    dato[cursor.getColumnName(j).toUpperCase(Locale.ROOT)] =
                        funcion.dato(cursor, cursor.getColumnName(j))
                } catch (e: Exception){}
            }
            dato["PREC_UNID"] = funcion.entero(dato["PREC_UNID"].toString())
            dato["PREC_CAJA"] = funcion.entero(dato["PREC_CAJA"].toString())
            listaProductos.add(dato)
            cursor.moveToNext()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun mostrar(){
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        posProducto = 0
        funcion.vistas  = intArrayOf(
            R.id.tv1,
            R.id.tv2,
            R.id.tv3,
            R.id.tv4,
            R.id.tv5,
            R.id.tv6,
            R.id.tv7
        )
        funcion.valores = arrayOf(
            "DESC_ARTICULO", "PREC_CAJA", "PREC_UNID",
            "REFERENCIA", "COD_ARTICULO", "COD_BARRA", "TIP_SURTIDO"
        )
        val adapter =
                 AdapterProducto(
                     this, listaProductos, funcion.vistas, funcion.valores, accion
                 )
        lvProductos.adapter = adapter
        lvProductos.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
                posProducto = position
                FuncionesUtiles.posicionDetalle  = 0
                lvProductos.invalidateViews()
                spReferencias()
                cargarDetalleProducto()
        }
        if (listaProductos.size>0){
            spReferencias()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun verificaCargado(position: Int):Boolean{
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return false
//        }
        if (articulosDetalle.indexOf("|${listaProductos[position]["COD_ARTICULO"]}|") > -1){
            funcion.toast(this, "El producto ya se ha cargado al pedido.")
            return false
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun cargarDetalleProducto(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        tvdCod.text = listaProductos[posProducto]["COD_ARTICULO"]
        btCodigo.text = "Cod.: ${listaProductos[posProducto]["COD_ARTICULO"]}"
        tvdPrecioReferencia.text = funcion.entero(spReferencias.getDato("PRECIO"))
        tvdDesc.setText(tvdPrecioReferencia.text)
        btCantMinima.text = "Cant. Min.: " + spReferencias.getDato("CANT_MINIMA")
        cantidadMinima = spReferencias.getDato("CANT_MINIMA").toInt()
        if (spReferencias.getDato("CANT_MINIMA").trim() == "" || spReferencias.getDato("CANT_MINIMA").toInt() == 0) {
            btCantMinima.text = "Cant. Min.: " + 1
            cantidadMinima = 1
        }
        tvdCantidad.setText(cantidadMinima.toString())
        calcularSubtotal()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun calcularSubtotal(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        tvdTotal.text = funcion.entero(
            (tvdDesc.text.toString().replace(".", "").toInt()) * (tvdCantidad.text.toString()
                .replace(
                    ".",
                    ""
                )).toInt()
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun inicializaET(et: EditText){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (et.text.toString().trim().isNotEmpty()) {
                    calcularSubtotal()
                } else {
                    tvdTotal.text = "0"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

        })

        et.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                if (tvdCantidad.text.toString().isEmpty() || tvdCantidad.text.toString() == ""){
                    tvdCantidad.setText(spReferencias.getDato("cant_minima"))
                }
                if (tvdDesc.text.toString().isEmpty() || tvdDesc.text.toString() == ""){
                    tvdDesc.setText(spReferencias.getDato("precio"))
                }
            }
        }
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun actualizaIndiceDePromocion(){
        if (indActualizado){
            return
        }
        val codCondicion : String = spCondicionDeVenta.getDato("cod_condicion_venta")
        val lista : String = spListaPrecios.getDato("COD_LISTA_PRECIO")
//        val tabla : String = "svm_promociones_art_cab_s" + ListaClientes.tipCliente.replace(".","") + codListaPrecio + ListaClientes.codVendedor
        val tablaCab = "svm_promociones_art_cab"
        var sql = ("SELECT distinct COD_ARTICULO FROM svm_promociones_art_cab c "
                + "   WHERE (c.COD_CONDICION_VENTA = '" + codCondicion + "' or TRIM(COD_CONDICION_VENTA) = '')"
                + "    and (c.TIP_CLIENTE = '" + ListaClientes.tipCliente + "' or TRIM(TIP_CLIENTE) = '')"
                + "    and (c.COD_LISTA_PRECIO = '" + lista + "' or TRIM(COD_LISTA_PRECIO) = '')")

        var cursorProm: Cursor = MainActivity2.bd!!.rawQuery(sql, null)

        var nreg = cursorProm.count
        cursorProm.moveToFirst()
        var `in`: String

        `in` = if (nreg > 0) {
            "'" + cursorProm.getString(cursorProm.getColumnIndex("COD_ARTICULO")) + "'"
        } else {
            return
        }
        for (i in 0 until nreg) {
            `in` = `in` + ",'" + cursorProm.getString(cursorProm.getColumnIndex("COD_ARTICULO")) + "'"
            cursorProm.moveToNext()
        }

        sql = ("SELECT distinct NRO_PROMOCION FROM svm_promociones_art_cab c "
                + "   WHERE (c.COD_CONDICION_VENTA = '" + codCondicion + "' or TRIM(c.COD_CONDICION_VENTA) = '')"
                + "    and (c.TIP_CLIENTE = '" + ListaClientes.tipCliente + "' or TRIM(c.TIP_CLIENTE) = '')"
                + "    and (c.COD_LISTA_PRECIO = '" + lista + "' or TRIM(c.COD_LISTA_PRECIO) = '')"
                + "    and (c.IND_ART = 'S')")

        cursorProm = MainActivity2.bd!!.rawQuery(sql, null)

        nreg = cursorProm.count
        cursorProm.moveToFirst()

        var inNro = "'" + cursorProm.getString(cursorProm.getColumnIndex("NRO_PROMOCION"))
            .toString() + "'"

        for (i in 0 until nreg) {
            inNro =
                inNro + ",'" + cursorProm.getString(cursorProm.getColumnIndex("NRO_PROMOCION")) + "'"
            cursorProm.moveToNext()
        }

        sql = ("SELECT distinct COD_ARTICULO FROM svm_promociones_art_det b "
                + "   WHERE NRO_PROMOCION IN (" + inNro + ")")

        cursorProm = MainActivity2.bd!!.rawQuery(sql, null)

        nreg = cursorProm.getCount()
        cursorProm.moveToFirst()

        `in` += ",'" + cursorProm.getString(cursorProm.getColumnIndex("COD_ARTICULO"))
            .toString() + "'"

        for (i in 0 until nreg) {
            `in` =
                `in` + ",'" + cursorProm.getString(cursorProm.getColumnIndex("COD_ARTICULO")) + "'"
            cursorProm.moveToNext()
        }

        sql = "UPDATE svm_articulos_precios SET IND_PROMO_ACT = 'N'"

        try {
            MainActivity2.bd!!.execSQL(sql)
        } catch (e: java.lang.Exception) {
            funcion.mensaje(this, "Error", "No se pudo actualizar el campo${e.message}".trimIndent()
            )
        }

        sql = ("UPDATE svm_articulos_precios SET IND_PROMO_ACT = 'S' WHERE cod_articulo in ($`in`)")

        try {
            MainActivity2.bd!!.execSQL(sql)
            indActualizado = true
        } catch (e: java.lang.Exception) {
            funcion.mensaje(this,"Error", "No se pudo actualizar el campo${e.message}".trimIndent())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun insertarPedido(){
        if (!validaciones()){ return }
//        if (porcDescuentos < 0.0){
//            porcDescuentos = funcion.maxDescuento()
//        }
        val porcDescuento : Double = 100 - (funcion.aEntero(tvdDesc.text.toString()) * 100)/(funcion.aEntero(
            tvdPrecioReferencia.text.toString()
        )).toDouble()
        if (porcDescuento>porcDescuentos) {
            if (verificarDescuentosVarios() && verificarPromocion()) {
                val mensaje : String = ("El descuento solicitado es de " + funcion.decimal(
                    porcDescuento
                ) + "%\n" +
                        "CLIENTE: \t${ListaClientes.codCliente} - ${ListaClientes.codSubcliente}\n" +
                        "ARTICULO: \t${listaProductos[posProducto]["COD_ARTICULO"]}\n" +
                        "UM: \t${spReferencias.getDato("COD_UNIDAD_REL")}\n" +
                        "CANTIDAD: \t${tvdCantidad.text}")
                val dialogo = DialogoAutorizacion(this)
                dialogo.dialogoAutorizacion("guardarDetalle2", "noGuardarDetalle2", accion, mensaje)
                funcion.mensaje(this, "Descuento", "sin descuento")
            }
        } else {
            guardaDetalle2()
        }
        cargarAtriculosDetalleVen()
//        cargarDetalle()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun guardaDetalle2() {
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        if (tvdCantidad.text.toString() == "") {
            return
        }
        if (tvdCantidad.text.toString().toInt()%cantidadMinima != 0){
            funcion.toast(
                this,
                "La cantidad minima es de $cantidadMinima articulos.\nSólo se pueden cargar múltiplos de $cantidadMinima."
            )
            return
        }
        if (!verificaCargado(posProducto)){
            return
        }
        cargaDatosCabecera()
        cargaDatosDetalle()

        tvdCantidad.setText("0")
        tvdTotal.text = "0"
        totalPedido = "0"
        var nf: NumberFormat = NumberFormat.getInstance()
        nf.minimumFractionDigits = decimales.toInt()
        nf.maximumFractionDigits = decimales.toInt()
        var desc = 0.toFloat()
        if (etDescFin.text.toString() != "") {
            desc += etDescVarios.text.toString().replace(",", ".").toFloat()
        }
        if (etDescVarios.text.toString() != "") {
            desc += etDescVarios.text.toString().replace(",", ".").toFloat()
        }
        if (desc > 0) {
            if (decimales == "0") {
                try {
                    totalPedido = nf.format(totalPedido)
                    val totDesc: Float = totalPedido.replace(".", "").toInt() * desc / 100
                    nf = NumberFormat.getInstance()
                    etTotalDesc.setText(nf.format(round(totDesc.toDouble()).toInt()))
//                    etTotalPedido.setText(nf.format(calc))
                    etSubtotal.setText(totalPedido)
                } catch (e: java.lang.Exception) {
                    etTotalPedido.setText(totalPedido)
                    etTotalDesc.setText("")
                    etTotalPedido.setText("")
                }
            } else {
                try {
                    totalPedido = nf.format(totalPedido)
                    val calcDes = totalComprobanteDecimal
                    var totDesc = calcDes * desc / 100
                    nf = NumberFormat.getInstance()
                    nf.minimumFractionDigits = decimales.toInt()
                    nf.maximumFractionDigits = decimales.toInt()
                    etTotalDesc.setText(nf.format(totDesc))
                    totDesc = calcDes - calcDes * desc / 100
                    etTotalPedido.setText(nf.format(totDesc))
                    etSubtotal.setText(totalPedido)
                } catch (e: java.lang.Exception) {
                    etTotalPedido.setText(totalPedido)
                    etTotalDesc.setText("")
                    etTotalPedido.setText("")
                }
            }
        }
        posDetalle = -1
        tvdPrecioReferencia.isEnabled = false
        cargarDetalleProducto()
        lvProductos.invalidateViews()
        totalPedido = etTotalPedido.text.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun maxPedido(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        val sql = "SELECT MAX(NUMERO) MAXIMO from vt_pedidos_cab where COD_VENDEDOR = '${ListaClientes.codVendedor}'"
        val cursor: Cursor = funcion.consultar(sql)
        if (cursor.moveToFirst()) {
            maximo = if (funcion.datoEntero(cursor, "MAXIMO") > funcion.ultPedidoVenta(ListaClientes.codVendedor)) {
                funcion.datoEntero(cursor, "MAXIMO")
            } else {
                funcion.ultPedidoVenta(ListaClientes.codVendedor)
            }
            etFecha.setText(funcion.getFechaActual())
            fechaInt = funcion.fecha(etFecha.text.toString()).toString()
        } else {
            maximo = funcion.ultPedidoVenta(ListaClientes.codVendedor)
        }
        maximo += 1
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun validaciones():Boolean{
        if (!dispositivo.modoAvion()){return false}
        if (!dispositivo.fechaCorrecta()){return false}
        if (tvdCantidad.text.toString().trim().toInt() == 0){
            funcion.toast(this, "La cantidad debe ser mayor a 0.")
            return false
        }
        if (!dispositivo.zonaHoraria()){return false}
        if (!dispositivo.tarjetaSim(telMgr)){return false}
//        if (!dispositivo.validaEstadoSim(telMgr)){return false}
        if (!dispositivo.horaAutomatica()){return false}
//        if (!ubicacion.validaUbicacionSimulada(lm)){return false}
        return true
    }

    private fun verificarDescuentosVarios():Boolean{
        var descVarios = true
        var ventaDirecta = true
        val porcentaje: Double
        try {
            if (etDescVarios.text.isNotEmpty()){
                porcentaje = etDescVarios.text.toString().replace(".", "").replace(",", ".").toDouble()
                if (porcentaje > 0.0){
                    descVarios = false
                }
            }
        } catch (e: java.lang.Exception){
            e.printStackTrace()
        }
        if (vent == "N"){
            ventaDirecta = false
        }
        if (!ventaDirecta && !descVarios){
            funcion.mensaje(
                this,
                "",
                "No se puede aplicar descuento a artículos teniendo descuentos varios"
            )
            return false
        }
        return true
    }

    private fun verificarPromocion(): Boolean {
        return try {
            val sql = ("SELECT id FROM vt_pedidos_det "
                    + "  WHERE NRO_PROMOCION > 0 "
                    + "    AND NUMERO		 = '${numeroPedido()}'"
                    + "    AND COD_VENDEDOR = '${ListaClientes.codVendedor}'")
            val cursor: Cursor = funcion.consultar(sql)
            cursor.moveToFirst()
            val nreg = cursor.count
            var ventaDirecta = true
            if (vent == "N") {
                ventaDirecta = false
            }
            if (nreg > 0 && !ventaDirecta) {
                funcion.mensaje(this, "", "No se puede aplicar descuentos con promociones!")
                false
            } else {
                true
            }
        } catch (e: java.lang.Exception) {
            false
        }
    }

    private fun numeroPedido():Int{
        return if (id.isEmpty()){
            val sql = ("SELECT MAX(NUMERO) AS NUMERO "
                    + "  FROM vt_pedidos_cab "
                    + " WHERE COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                    + "   AND COD_CLIENTE  = '${ListaClientes.codCliente}' "
                    + "   AND COD_SUBCLIENTE = '${ListaClientes.codSubcliente}' ")
            val cursor : Cursor = funcion.consultar(sql)
            if(cursor.count==0){
                1
            } else {
                funcion.datoEntero(cursor, "NUMERO")
            }
        } else {
            id.toInt()
        }
    }

    private fun habilitarSpinnersCabecera(estado: Boolean){
        spListaPrecio.isEnabled = estado
        spVenta.isEnabled = estado
        spCondicionVenta.isEnabled = estado
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun cargaDatosCabecera(){
        val values = ContentValues()
        habilitarSpinnersCabecera(false)
        values.put("COD_EMPRESA", "1")
        values.put("COD_CLIENTE", ListaClientes.codCliente)
        values.put("COD_SUBCLIENTE", ListaClientes.codSubcliente)
        values.put("COD_VENDEDOR", ListaClientes.codVendedor)
//        values.put("COD_LISTA_PRECIO", codListaPrecio)
        values.put("COD_LISTA_PRECIO", codListaPrecio)
        values.put("FECHA", etFecha.text.toString())
        values.put("FECHA_INT", fechaInt)
        values.put("ESTADO", "P")
//        values.put("COD_CONDICION_VENTA", spCondicionDeVenta.getDato("COD_CONDICION_VENTA"))
//        values.put("DIAS_INICIAL", spCondicionDeVenta.getDato("DIAS_INICIAL"))
//        values.put("COD_MONEDA", spListaPrecios.getDato("COD_MONEDA"))
//        values.put("DECIMALES", spListaPrecios.getDato("DECIMALES"))
        values.put("COD_CONDICION_VENTA", spCondicionDeVenta.getDato("COD_CONDICION_VENTA"))
        values.put("DIAS_INICIAL", diasInicial.toString())
        values.put("COD_MONEDA", codMoneda)
        values.put("DECIMALES", decimales)
        values.put("TIP_CAMBIO", "1")
        values.put("DESC_CLIENTE", ListaClientes.descCliente)
        values.put("NRO_AUTORIZACION", claveAutorizacion)
        values.put("LATITUD", " ")
        values.put("LONGITUD", " ")
        values.put("IND_PRESENCIAL", indPresencial)
        values.put("HORA_ALTA", funcion.getHoraActual())
        values.put("FEC_ALTA", funcion.getFechaActual())
        if (decimales == "0") {
            var totalComprobante: Int
            totalComprobante = etTotalPedido.text.toString().replace(".", "").toInt()
            if (totalComprobante == 0){
                totalComprobante = tvdTotal.text.toString().replace(".", "").toInt()
            } else {
                totalComprobante += tvdTotal.text.toString().replace(".", "").toInt()
            }

            values.put("TOT_COMPROBANTE", totalComprobante.toString())
            funcion.entero(totalComprobante)
            totalPedido = funcion.entero(totalComprobante)
        } else {
            var total: String = etTotalPedido.text.toString().replace(".", "")
            total = if (total.toInt() == 0){
                tvdTotal.text.toString().replace(".", "")
            } else {
                (total.toFloat() + tvdTotal.text.toString().replace(".", "").toFloat()).toString()
            }
            total = total.replace(",", ".")
            totalComprobanteDecimal = total.toDouble()
            values.put("TOT_COMPROBANTE", totalComprobanteDecimal)
            val nf: NumberFormat = NumberFormat.getInstance()
            nf.minimumFractionDigits = decimales.toInt()
            nf.maximumFractionDigits = decimales.toInt()
            nf.format(totalComprobanteDecimal)
            totalPedido = nf.format(totalComprobanteDecimal)
        }
        if (nuevo) {
            maxPedido()
            values.put("NUMERO", maximo)
            try {
                funcion.insertar("vt_pedidos_cab", values)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            values.put("NUMERO", maximo)
            values.put("NRO_ORDEN_COMPRA", etOrdenCompra.text.toString())
            try {
                funcion.actualizar(
                    "vt_pedidos_cab",
                    values,
                    "NUMERO = '$maximo' and COD_VENDEDOR = '${ListaClientes.codVendedor}'"
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        etSubtotal.setText(totalPedido)
        etTotalPedido.setText(totalPedido)
        etNroPedido.setText(maximo.toString())
        nuevo = false
    }

    private fun cargaDatosDetalle(){
        val values = ContentValues()
        values.put("COD_EMPRESA", "1")
        values.put("NUMERO", maximo)
        values.put("COD_VENDEDOR", ListaClientes.codVendedor)
        values.put("COD_ARTICULO", tvdCod.text.toString().trim())
        values.put("CANTIDAD", tvdCantidad.text.toString().replace(".", ""))
        values.put("COD_UNIDAD_MEDIDA", spReferencias.getDato("COD_UNIDAD_REL"))
        values.put("COD_IVA", listaProductos[posProducto]["COD_IVA"])
        values.put("PORC_IVA", listaProductos[posProducto]["PORC_IVA"])
        values.put("IND_DEPOSITO", "V")
        try {
            if (decimales == "0") {
                values.put("PRECIO_UNITARIO", tvdDesc.text.toString().replace(".", ""))
                values.put("MONTO_TOTAL", tvdTotal.text.toString().replace(".", ""))
                values.put("TOTAL_IVA", "0")
                values.put("PRECIO_UNITARIO_C_IVA", tvdDesc.text.toString().replace(".", ""))
                values.put("MONTO_TOTAL_CONIVA", tvdTotal.text.toString().replace(".", ""))
                values.put("IND_BLOQUEADO", indBloqueado)
                values.put("PRECIO_LISTA", tvdPrecioReferencia.text.toString().replace(".", ""))
            } else {
                val nf: NumberFormat = NumberFormat.getInstance()
                nf.minimumFractionDigits = decimales.toInt()
                nf.maximumFractionDigits = decimales.toInt()
                var total: String = tvdDesc.text.toString().replace(".", "")
                total = total.replace(",", ".")
                values.put("PRECIO_UNITARIO", total)
                total = tvdTotal.text.toString().replace(".", "")
                total = total.replace(",", ".")
                values.put("MONTO_TOTAL", total)
                values.put("TOTAL_IVA", "0")
                values.put("PRECIO_UNITARIO_C_IVA", tvdDesc.text.toString().replace(",", "."))
                values.put("MONTO_TOTAL_CONIVA", total)
                values.put("IND_BLOQUEADO", indBloqueado)
                values.put(
                    "PRECIO_LISTA", tvdPrecioReferencia.text.toString().replace(".", "").replace(
                        ",",
                        "."
                    )
                )
            }
        } catch (e: java.lang.Exception) {
            tvdTotal.text = "0"
            values.put("PORC_DESC_VAR", "0")
            values.put("DESCUENTO_VAR", "0")
        }
        values.put("MULT", spReferencias.getDato("MULT"))
        values.put("TIP_COMPROBANTE_REF", "PRO")
        values.put("SER_COMPROBANTE_REF", ListaClientes.codVendedor)
        values.put("NRO_COMPROBANTE_REF", etNroPedido.text.toString())
        values.put("ORDEN_REF", etOrdenCompra.text.toString())
        values.put("IND_SISTEMA", "S")
        values.put("IND_TRANSLADO", vent)
        values.put("IND_BLOQUEADO", indBloqueado)
        try {
            values.put("PROMOCION", "")
            values.put("TIP_PROMOCION", "")
            values.put("NRO_PROMOCION", "")
            values.put("NRO_AUTORIZACION", claveAutorizacion)
            values.put("MONTO_DESC_TC", "0")
            claveAutorizacion = ""
            funcion.insertar("vt_pedidos_det", values)
            recalcularTotal()

        } catch (e: java.lang.Exception) {
            funcion.mensaje(this, "Error!", e.message.toString())
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun ventaRapida(){
        val textBuscar = etBuscar.text
        etBuscar.setText("")
        val campos = " distinct a.*,  b.TIP_SURTIDO "
        val groupBy = ""
        val orderBy = " a.desc_articulo asc "
        val tabla = " svm_articulos_precios a left join svm_surtido_eficiente b " +
                "    on   a.COD_EMPRESA		= b.COD_EMPRESA  " +
                "   and   a.COD_ARTICULO	= b.COD_ARTICULO " +
                "   AND   b.COD_CLIENTE     = '${ListaClientes.codCliente}' " +
                "   AND   b.COD_SUBCLIENTE  = '${ListaClientes.codSubcliente}' " +
                "   AND   b.TIP_CLIENTE     = '${ListaClientes.tipCliente}' "
        val where = " and a.COD_FAMILIA      = '${listaProductos[posProducto]["COD_FAMILIA"]}' " +
                           " and a.COD_LINEA        = '${listaProductos[posProducto]["COD_LINEA"]}' " +
                           " and a.COD_LISTA_PRECIO = '${codListaPrecio}' " +
                           " and a.PREC_CAJA        = '${listaProductos[posProducto]["PREC_CAJA"].toString().replace(
                               ".",
                               ""
                           )}' "
        cargarLista(funcion.buscar(tabla, campos, groupBy, orderBy, where))
        posProducto = 0
        mostrar()
        etBuscar.text = textBuscar
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun promociones(view: View){
        Promociones.posPromocion = 0
        if (view.id == btnPromociones.id){
            Promociones.codArticulo = ""
        } else {
            Promociones.codArticulo = listaProductos[posProducto]["COD_ARTICULO"].toString()
        }
        Promociones.codListaPrecio = codListaPrecio
        Promociones.condicionVenta = spCondicionDeVenta.getDato("COD_CONDICION_VENTA")
        val promociones = Intent(this, Promociones::class.java)
        startActivity(promociones)
    }

    //DETALLES
    @RequiresApi(Build.VERSION_CODES.N)
    fun cargarDetalle(){
        if (nuevo){
            articulosDetalle = ""
            lvProductos.invalidateViews()
            return
        }
        indBloqueado = "S"
        val sql : String = ("SELECT DISTINCT a.*, b.DESC_ARTICULO FROM vt_pedidos_det a, svm_articulos_precios b "
                         +  " WHERE a.NUMERO = '$maximo' "
                         +  "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                         +  "   AND a.COD_EMPRESA  = b.COD_EMPRESA "
                         +  "   AND a.COD_ARTICULO = b.COD_ARTICULO "
                         +  "   AND a.COD_VENDEDOR = b.COD_VENDEDOR "
                         +  "   AND b.COD_LISTA_PRECIO  = '${spListaPrecios.getDato("COD_LISTA_PRECIO")}'"
                         +  " ")
        listaDetalles = funcion.cargarDatos(funcion.consultar(sql))
        for (i in 0 until listaDetalles.size){
            depo = listaDetalles[i]["IND_DEPOSITO"].toString()
            if (listaDetalles[i]["IND_BLOQUEADO"].toString() == "S"){
                indBloqueado = "N"
            }
            listaDetalles[i]["PRECIO_UNITARIO"] = funcion.numero("0",listaDetalles[i]["PRECIO_UNITARIO"].toString())
            listaDetalles[i]["MONTO_TOTAL"] = funcion.numero("0",listaDetalles[i]["MONTO_TOTAL"].toString())
        }
        mostrarDetalle()
        cargarAtriculosDetalle()
        lvProductos.invalidateViews()
        habilitaDescuentoFinanciero()
        if (listaDetalles.isNotEmpty()){
            recalcularTotal()
        }
    }

    private fun cargarAtriculosDetalle(){
//        if (nuevo) return
        val sql = ("SELECT DISTINCT COD_ARTICULO FROM vt_pedidos_det "
                +  " WHERE NUMERO       = '$maximo' "
                +  "   AND COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                +  " ")
        val cursor = funcion.consultar(sql)
        articulosDetalle = ""
        for (i in 0 until cursor.count){
            articulosDetalle += "|" + funcion.dato(cursor, "COD_ARTICULO") + "|"
            cursor.moveToNext()
        }
    }

    private fun cargarAtriculosDetalleVen(){
//        var sql = ("SELECT DISTINCT COD_ARTICULO FROM vt_pedidos_det "
//                +  " WHERE NUMERO       = '${Pedidos.maximo}' "
//                +  "   AND COD_VENDEDOR = '${ListaClientes.codVendedor}' "
//                +  " ")
//        var cursor = funcion.consultar(sql)
//        articulosDetalle = ""
//        for (i in 0 until cursor.count){
            articulosDetalle += "|" + tvdCod.text.toString().trim() + "|"
//            cursor.moveToNext()
//        }
    }

    private fun mostrarDetalle(){
        if (listaDetalles.size>0){
            posDetalle = 0
        }
        funcion.vistas  = intArrayOf(
            R.id.tv1,
            R.id.tv2,
            R.id.tv3,
            R.id.tv4,
            R.id.tv5,
            R.id.tv6,
            R.id.tv7
        )
        funcion.valores = arrayOf(
            "COD_ARTICULO", "DESC_ARTICULO", "EXISTENCIA_ACTUAL", "CANTIDAD",
            "PRECIO_UNITARIO", "MONTO_TOTAL", "NRO_PROMOCION"
        )
        val adapter: Adapter.AdapterDetallePedido =
            Adapter.AdapterDetallePedido(
                this,
                listaDetalles,
                R.layout.ven_ped_lista_pedidos_detalles,
                funcion.vistas,
                funcion.valores,
                accion,
                "eliminarDetalle"
            )
        lvDetalle.adapter = adapter
        lvDetalle.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            posDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvDetalle.invalidateViews()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun eliminarDetalle(){
        val sql: String
        if (listaDetalles[posDetalle]["NRO_PROMOCION"].toString().trim().replace("null", "") == ""){
            sql = "DELETE FROM  vt_pedidos_det WHERE id = ${listaDetalles[posDetalle]["id"]}"
            funcion.ejecutar(sql, this)
            recalcularTotal()
        } else {
            val dialogoAutorizacion = DialogoAutorizacion(this)
            dialogoAutorizacion.dialogoAccionOpcion(
                "eliminarDetallePromocion", "", accion,
                "El artículo pertenece a una promocion.\nSe eliminarán todos los artículos de la promoción.\n¿Desea continuar?",
                "¡Atención!", "SI", "NO"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun eliminarDetallePromocion(){
//        if (!dispositivo.validaEstadoSim(telMgr)){
//            return
//        }
        val sql = "DELETE FROM  vt_pedidos_det " +
                " WHERE NRO_PROMOCION = '${listaDetalles[posDetalle]["NRO_PROMOCION"]}' " +
                "   AND NUMERO = '$maximo' "
        funcion.ejecutar(sql, this)
        recalcularTotal()
    }

    private fun recalcularTotal(){
        val sql = ("SELECT SUM(CAST(MONTO_TOTAL_CONIVA AS INTEGER)) MONTO_TOTAL FROM vt_pedidos_det " + " WHERE NUMERO = '$maximo' AND COD_VENDEDOR = '${ListaClientes.codVendedor}' ")
        val cursor = funcion.consultar(sql)
        val total = funcion.entero(funcion.dato(cursor, "MONTO_TOTAL"))
        etSubtotal.setText(total)
        var desc: String = if (etTotalDesc.text.toString() == ""){"0"}else{ etTotalDesc.text.toString().replace(
            ".",
            ""
        ) }
        desc = desc.replace(",", ".")
        etTotalPedido.setText(
            funcion.entero(
                (total.replace(".", "").toInt() - desc.toInt()).toString()
            )
        )
        funcion.ejecutar(
            "UPDATE vt_pedidos_cab SET TOT_COMPROBANTE = '${
                funcion.dato(
                    cursor,
                    "MONTO_TOTAL"
                )
            }' WHERE NUMERO = '$maximo' AND COD_VENDEDOR = '${ListaClientes.codVendedor}' ", this
        )
    }

    //CIERRE
    @RequiresApi(Build.VERSION_CODES.N)
    fun inicializarElementosCierre(){
        if (!nuevo){
            cargarCabeceraCierre()
        } else {
            etOrdenCompra.setText("0")
            etTotalPedido.setText("0")
            etFecha.setText(funcion.getFechaActual())
        }
        dialogoCalendario()
        btEnviar.setOnClickListener{enviarPedido()}
        btCancelar.setOnClickListener{finish()}
        habilitaDescuentoFinanciero()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun dialogoCalendario(){
        val calendario = DialogoCalendario(this)
        etFechaHoy.setText(funcion.getFechaActual())
        etFecha.setOnClickListener{calendario.onCreateDialog(1, etFecha, etFechaHoy)!!.show()}
        inicializaETFecha()
        inicializaETObservacion()
        inicializaETDescuentosVarios()
        inicializaETDescuentoFinanciero()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun inicializaETFecha(){
        etFecha.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SimpleDateFormat")
            override fun afterTextChanged(s: Editable?) {
                val dfDate = SimpleDateFormat("dd/MM/yyyy")
                var d: Date? = null
                var d1: Date? = null
                try {
                    d = dfDate.parse(etFechaHoy!!.text.toString())
                    d1 = dfDate.parse(etFecha.text.toString()) // Returns
                    // 15/10/2012
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val diffInDays = ((d1!!.time - d!!.time) / (1000 * 60 * 60 * 24)).toInt()
                println(diffInDays.toString())
                if (diffInDays > 2) {
                    funcion.mensaje(
                        this@Pedidos,
                        "¡Atención!",
                        "La fecha no puede ser mayor a dos días."
                    )
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    etFecha.setText(sdf.format(Date()))
                }
                val sql = "update vt_pedidos_cab set FECHA = '${s.toString()}' " +
                        "WHERE NUMERO           = '$maximo' AND COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                        "  AND COD_CLIENTE      = '${ListaClientes.codCliente}' " +
                        "  AND COD_SUBCLIENTE   = '${ListaClientes.codSubcliente}' "
                funcion.ejecutar(sql, this@Pedidos)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun inicializaETObservacion(){
        etObservacion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val sql = "update vt_pedidos_cab set COMENTARIO = '${s.toString()}' " +
                        "WHERE NUMERO           = '$maximo' AND COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                        "  AND COD_CLIENTE      = '${ListaClientes.codCliente}' " +
                        "  AND COD_SUBCLIENTE   = '${ListaClientes.codSubcliente}' "
                funcion.ejecutar(sql, this@Pedidos)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun inicializaETDescuentosVarios(){
        etDescVarios.setOnClickListener{
            if (etDescVarios.text.isEmpty()){
                etDescVarios.setText("0")
            }
            descVarAnterior = etDescVarios.text.toString()
            funcion.dialogoEntradaNumeroDecimal(etDescVarios, this)
        }
        etDescVarios.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etDescVarios.text.toString() != "") {
                    if (etDescVarios.text.toString() == descVarAnterior) {
                        return
                    }
                    if (etDescVarios.text.toString() == "0" || etDescVarios.text.toString() == "") {
                        return
                    }
                    val descVarios = etDescVarios.text.toString().replace(",", ".")
                    if (descVarios.toFloat() > 100) {
                        funcion.mensaje(
                            this@Pedidos,
                            "Atención",
                            "El % de descuento supera al maximo permitido!"
                        )
                        etDescVarios.setText(descVarAnterior)
                        return
                    }
                    val d2: String
                    d2 = if (etDescVarios.text.toString() == "") {
                        "0"
                    } else {
                        descVarios
                    }

                    val d1: String = if (etDescFin.text.toString() == "") {
                        "0"
                    } else {
                        etDescFin.text.toString().replace(",", ".")
                    }

                    if (d1.toFloat() + d2.toFloat() > 100) {
                        funcion.mensaje(this@Pedidos, "Atención", "% no permitido !! ")
                        etDescVarios.setText(descVarAnterior)
                    } else {
                        if (verificaDescuentosArticulos(maximo) && verificaPromocionExis()) {
                            if (decimales == "0") {
                                val calcDesc =
                                    etTotalPedido.text.toString().replace(".", "").toInt()

                                val nf = NumberFormat.getInstance()
                                nf.minimumFractionDigits = 2
                                nf.maximumFractionDigits = 2

                                val nf0 = NumberFormat.getInstance()
                                nf0.minimumFractionDigits = 0
                                nf0.maximumFractionDigits = 0

                                val autorizacion = DialogoAutorizacion(this@Pedidos)
                                val mensaje = "El descuento solicitado es de " + nf.format(
                                    etDescVarios.text.toString().replace(
                                        ",",
                                        "."
                                    ).toDouble()
                                ) + "%\n" +
                                        "CLIENTE: \t${ListaClientes.codCliente} - ${ListaClientes.codSubcliente}\n" +
                                        "LISTA: \t${codListaPrecio}\n" +
                                        "TOTAL PEDIDO: \t" + nf0.format(calcDesc) + "\n"
                                autorizacion.dialogoAutorizacion(
                                    "descuentosVarios",
                                    "noDescuentosVarios",
                                    accion,
                                    mensaje
                                )

                            } else {

                                var tot = totalPedido.replace(".", "")
                                tot = tot.replace(",", ".")
                                val calcDesc = tot.toDouble()

                                val nf = NumberFormat.getInstance()
                                nf.minimumFractionDigits = 2
                                nf.maximumFractionDigits = 2

                                val autorizacion = DialogoAutorizacion(this@Pedidos)
                                val mensaje = "El descuento solicitado es de " + nf.format(
                                    etDescVarios.text.toString().replace(
                                        ",",
                                        "."
                                    ).toDouble()
                                ) + "%\n" +
                                        "CLIENTE: \t${ListaClientes.codCliente} - ${ListaClientes.codSubcliente}\n" +
                                        "LISTA: \t${codListaPrecio}\n" +
                                        "TOTAL PEDIDO: \t" + nf.format(calcDesc) + "\n"
                                autorizacion.dialogoAutorizacion(
                                    "descuentosVariosDecimal",
                                    "noDescuentosVarios",
                                    accion,
                                    mensaje
                                )

                            }
                        } else {
                            etDescVarios.setText("")
                            if (decimales == "0") {
                                try {
                                    etSubtotal.setText(totalPedido)
                                    val calcDes = totalPedido.replace(".", "").toInt()
                                    sumaDescuento = "0".toFloat()
                                    if (etDescFin.text.toString() != "") {
                                        sumaDescuento =
                                            etDescFin.text.toString().replace(",", ".").toFloat()
                                    }
                                    var totalDesc = (calcDes * (sumaDescuento / 100))
                                    val nf = NumberFormat.getInstance()

                                    var calc = round(totalDesc.toDouble()).toInt()
                                    etTotalDesc.setText(nf.format(calc))
                                    totalDesc = calcDes - (calcDes * (sumaDescuento / 100))
                                    calc = round(totalDesc.toDouble()).toInt()
                                    etTotalPedido.setText(nf.format(calc))
                                } catch (e: Exception) {
                                    etTotalDesc.setText("")
                                    etTotalPedido.setText("")
                                }
                            }
                        }
                    }
                } else {
//                    etDescVarios.setText("")
                    if (spListaPrecios.getDato("DECIMALES") == "0") {
                        try {
                            etSubtotal.setText(totalPedido)
                            val calcDesc = Integer.parseInt(totalPedido.replace(".", ""))
                            sumaDescuento = "0".toFloat()
                            if (etDescFin.text.toString() != "") {
                                sumaDescuento =
                                    etDescFin.text.toString().replace(",", ".").toFloat()
                            }
                            var totalDesc = (calcDesc * (sumaDescuento / 100))
                            val nf = NumberFormat.getInstance()
                            var calc = round(totalDesc.toDouble()).toInt()
                            etTotalDesc.setText(nf.format(calc))
                            totalDesc = calcDesc - (calcDesc * (sumaDescuento / 100))
                            calc = round(totalDesc.toDouble()).toInt()
                            etTotalPedido.setText(nf.format(calc))
                        } catch (e: Exception) {
                            etTotalDesc.setText("")
                            etTotalPedido.setText("")
                        }
                    }
                }
                totalPedido = etTotalPedido.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun inicializaETDescuentoFinanciero(){
        etDescFin.setOnClickListener{
            if (etDescFin.text.isEmpty()){
                etDescFin.setText("0")
            }
            descFinAnterior = etDescFin.text.toString()
            funcion.dialogoEntradaNumeroDecimal(etDescFin, this)
        }
        etDescFin.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                if (etDescFin.text.toString() != "") {
                    if (etDescFin.text.toString() == descFinAnterior) {
                        return
                    }
                    var descFin = etDescFin.text.toString().replace(".", "")
                    descFin = descFin.replace(",", ".")
                    if (descFin.toFloat() > spCondicionDeVenta.getDato("PORC_DESC").toFloat()) {
                        etDescFin.setText("0")
                        funcion.mensaje(
                            this@Pedidos,
                            "Atención",
                            "El % de descuento supera al de la condición!"
                        )
                        return
                    } else {
                        val d2: String = if (etDescVarios.text.toString() == "") {
                            "0"
                        } else {
                            etDescVarios.text.toString().replace(",", ".")
                        }
                        val d1: String = if (etDescFin.text.toString() == "") {
                            "0"
                        } else {
                            descFin
                        }
                        if (d1.toFloat() + d2.toFloat() > 100) {
                            etDescFin.setText(descFinAnterior)
                            funcion.mensaje(this@Pedidos, "Atención", "% no permido !! ")
                        } else {
                            if (decimales == "0") {
                                try {
//                                    etSubtotal.setText(etTotalPedido.text.toString())
                                    val calcDesc =
                                        etSubtotal.text.toString().replace(".", "").toInt()
                                    sumaDescuento = "0".toFloat()
                                    if (etDescVarios.text.toString() != "") {
                                        sumaDescuento = etDescVarios.text.toString().replace(
                                            ",",
                                            "."
                                        ).toFloat()
                                    }
                                    sumaDescuento += descFin.toFloat()
                                    var totalDesc = (calcDesc * (sumaDescuento / 100))
                                    val nf = NumberFormat.getInstance()
                                    var calc = round(totalDesc.toDouble()).toInt()
                                    etTotalDesc.setText(nf.format(calc))
                                    totalDesc = calcDesc - (calcDesc * (sumaDescuento / 100))
                                    calc = round(totalDesc.toDouble()).toInt()
                                    etTotalPedido.setText(nf.format(calc))
                                } catch (e: Exception) {
                                    etTotalDesc.setText("")
                                    etTotalPedido.setText("88")
                                }
                            } else {
                                try {
//                                    etSubtotal.setText(totalPedido.toString())
                                    var total = etSubtotal.text.toString().replace(".", "")
                                    total = total.replace(",", ".")
                                    val calcDesc = total.toDouble()
                                    sumaDescuento = "0".toFloat()
                                    if (etDescVarios.text.toString() != "") {
                                        sumaDescuento = etDescVarios.text.toString().replace(
                                            ",",
                                            "."
                                        ).toFloat()
                                    }
                                    sumaDescuento += descFin.toFloat()
                                    var totalDesc = (calcDesc * (sumaDescuento / 100))

                                    val nf = NumberFormat.getInstance()
                                    nf.minimumFractionDigits = Integer
                                        .parseInt(decimales)
                                    nf.maximumFractionDigits = Integer.parseInt("DECIMALES")
                                    var calc2 = totalDesc
                                    etTotalDesc.setText(nf.format(calc2))
                                    totalDesc = calcDesc - (calcDesc * (sumaDescuento / 100))
                                    calc2 = totalDesc
                                    etTotalPedido.setText(nf.format(calc2))
                                } catch (e: Exception) {
                                    etTotalDesc.setText("")
                                    etTotalPedido.setText("88")
                                }
                            }
                        }
                    }
                } else {
//                    etDescFin.setText("")
                    if (decimales == "0") {
                        try {
//                            etSubtotal.setText(totalPedido.toString())
                            val calcDesc = etSubtotal.text.toString().replace(".", "").toInt()
                            sumaDescuento = "0".toFloat()
                            if (etDescVarios.text.toString() != "") {
                                sumaDescuento =
                                    etDescVarios.text.toString().replace(",", ".").toFloat()
                            }
                            var totalDesc = (calcDesc * (sumaDescuento / 100)).toInt()
                            val nf = NumberFormat.getInstance()
                            nf.minimumFractionDigits = Integer.parseInt(decimales)
                            nf.maximumFractionDigits = Integer.parseInt(decimales)
                            var calc = round(totalDesc.toDouble()).toInt()
                            etTotalDesc.setText(nf.format(calc))
                            totalDesc = calcDesc - (calcDesc * (sumaDescuento / 100)).toInt()
                            calc = round(totalDesc.toDouble()).toInt()
                            etTotalPedido.setText(nf.format(calc))
                        } catch (e: Exception) {
                            etTotalDesc.setText("")
                            etTotalPedido.setText(e.toString())
                        }
                    } else {
                        try {
//                            etSubtotal.setText(totalPedido.toString())
                            val calcDesc = etSubtotal.text.toString().replace(",", ".").toDouble()
                            var sumaDescuento2 = 0.00
                            if (etDescVarios.text.toString() != "") {
                                sumaDescuento2 =
                                    etDescVarios.text.toString().replace(",", ".").toDouble()
                            }
                            var totalDesc = (calcDesc * (sumaDescuento2 / 100))

                            val nf = NumberFormat.getInstance()
                            nf.minimumFractionDigits = Integer.parseInt(decimales)
                            nf.maximumFractionDigits = Integer.parseInt(decimales)
                            var calc2 = totalDesc
                            etTotalDesc.setText(nf.format(calc2))
                            totalDesc = calcDesc - (calcDesc * (sumaDescuento / 100))
                            calc2 = totalDesc
                            etTotalPedido.setText(nf.format(calc2))
                        } catch (e: Exception) {
                            etTotalDesc.setText("")
                            etTotalPedido.setText(e.toString())
                        }
                    }
                }
                totalPedido = etTotalPedido.text.toString()
                val desc = etDescFin.text.toString().replace(".", "")
                val totDesc = etTotalDesc.text.toString().replace(".", "")
                val sql = "update vt_pedidos_cab set PORC_DESC_FIN = '${desc.replace(",", ".")}' " +
                        " , DESCUENTO_FIN        = '$totDesc' " +
                        " , TOT_DESCUENTO        = '${
                            etTotalDesc.text.toString().replace(".", "")
                        }' " +
                        " WHERE NUMERO           = '$maximo' " +
                        "   AND COD_VENDEDOR     = '${ListaClientes.codVendedor}' " +
                        "   AND COD_CLIENTE      = '${ListaClientes.codCliente}' " +
                        "   AND COD_SUBCLIENTE   = '${ListaClientes.codSubcliente}' "
                funcion.ejecutar(sql, this@Pedidos)
                recalcularTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun habilitaDescuentoFinanciero(){
        val montoMinimo = spCondicionDeVenta.getDato("MONTO_MIN_DESC").trim().toFloat()
        var monto       = etSubtotal.text.toString().trim().replace(".", "")
        monto = monto.replace(",", ".")
        if (monto.isEmpty() || monto.trim() == ""){ monto = "0" }
        val montoTotal = monto.toFloat()
        if (montoMinimo > montoTotal){
            etDescFin.setText("0")
            etDescFin.setOnClickListener { funcion.toast(
                this, "No a alcanzado el monto mínimo de " +
                        "${
                            funcion.numero(
                                "0",
                                montoMinimo.toString()
                            )
                        } " +
                        "para acceder al descuento."
            ) }
        } else {
            inicializaETDescuentoFinanciero()
        }
    }

    fun autorizaDescuentosVarios(){
        try {
//            etSubtotal.setText(totalPedido.toString())
            val calcDesc = etSubtotal.text.toString().replace(".", "")
            sumaDescuento = "0".toFloat()
            if (etDescVarios.text.toString() != "") {
                sumaDescuento = etDescFin.text.toString().replace(",", ".").toFloat()
                if (sumaDescuento.toString() == "0.00"){
                    sumaDescuento = "0".toFloat()
                }
            }
            sumaDescuento += etDescVarios.text.toString().replace(",", ".").toFloat()
            var totDesc = (calcDesc.toFloat() * (sumaDescuento / 100))
            val nf = NumberFormat.getInstance()
            var calc =  round(totDesc.toDouble())
            etTotalDesc.setText(nf.format(calc))
            totDesc = calcDesc.toFloat() - (calcDesc.toFloat() * (sumaDescuento / 100))
            calc = round(totDesc.toDouble())
            etTotalPedido.setText(nf.format(calc))
            val sql = "update vt_pedidos_cab set DESCUENTO_VAR = '${etDescVarios.text.toString().replace(
                ".",
                ""
            ).replace(",", ".")}' " +
                    " , TOT_DESCUENTO        = '${etTotalDesc.text.toString().replace(".", "")}' " +
                    " WHERE NUMERO           = '$maximo' AND COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                    "   AND COD_CLIENTE      = '${ListaClientes.codCliente}' " +
                    "   AND COD_SUBCLIENTE   = '${ListaClientes.codSubcliente}' "
            funcion.ejecutar(sql, this@Pedidos)
        } catch (e: Exception) {
            etTotalDesc.setText("")
            etTotalPedido.setText("")
        }
    }

    fun autorizaDescuentosVariosDecimal(){
        try {
            etSubtotal.setText(totalPedido)
            var total = totalPedido.replace(".", "")
            total = total.replace(",", ".")

            val calcDesc = total.toFloat()
            var sumaDescuento2 = "0.00".toDouble()

            if (etDescFin.text.toString() != "") {
                sumaDescuento2 = etDescFin.text.toString().toDouble()
            }
            sumaDescuento2 += etDescVarios.text.toString().toDouble()
            var totalDesc = (calcDesc * (sumaDescuento2 / 100))
            val nf = NumberFormat.getInstance()

            nf.minimumFractionDigits = Integer.parseInt(decimales)
            nf.maximumFractionDigits = Integer.parseInt(decimales)

            var calc2 = totalDesc
            etTotalDesc.setText(nf.format(calc2))
            totalDesc = calcDesc - (calcDesc * (sumaDescuento2 / 100))
            calc2 = totalDesc
            etTotalPedido.setText(nf.format(calc2))
            val sql = "update vt_pedidos_cab set DESCUENTO_VAR = '${etDescVarios.text.toString().replace(
                ",",
                "."
            ).toDouble()}' " +
                    " , TOT_DESCUENTO        = '$calc2' " +
                    " WHERE NUMERO           = '$maximo' AND COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                    "   AND COD_CLIENTE      = '${ListaClientes.codCliente}' " +
                    "   AND COD_SUBCLIENTE   = '${ListaClientes.codSubcliente}' "
            funcion.ejecutar(sql, this@Pedidos)
        } catch (e: Exception) {
            etTotalDesc.setText("")
            etTotalPedido.setText("")
        }
    }

    fun noAutorizaDescuentosVarios(){
        etDescVarios.setText(descVarAnterior)
    }

    private fun verificaDescuentosArticulos(nroPedido: Int): Boolean {
        var okDescArticulo = true
        try {
            val sql = ("SELECT PRECIO_UNITARIO, PRECIO_LISTA FROM vt_pedidos_det WHERE NUMERO = $nroPedido "
                        + " AND COD_VENDEDOR = '${ListaClientes.codVendedor}'")
            val rs: Cursor = funcion.consultar(sql)
            rs.moveToFirst()
            val nreg = rs.count
            if (nreg > 0) {
                for (i in 0 until nreg) {
                    val precioUnitario = rs.getDouble(rs.getColumnIndex("PRECIO_UNITARIO"))
                    val precioLista = rs.getDouble(rs.getColumnIndex("PRECIO_LISTA"))
                    if (precioUnitario < precioLista) {
                        okDescArticulo = false
                        break
                    }
                    rs.moveToNext()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        var okVentaDirecta = true
        if (vent == "N") {
            okVentaDirecta = false
        }
        return if (!okDescArticulo && !okVentaDirecta) {
            funcion.mensaje(
                this,
                "",
                "No se puede aplicar descuentos varios teniendo descuentos en artículos"
            )
            false
        } else {
            true
        }
    }

    private fun verificaPromocionExis(): Boolean {
        return try {
            val sql = ("SELECT id FROM vt_pedidos_det "
                    + "  WHERE NRO_PROMOCION > 0 "
                    + "    AND NUMERO		 = '$maximo'"
                    + "    AND COD_VENDEDOR = '${ListaClientes.codVendedor}'")
            val cursor: Cursor = funcion.consultar(sql)
            cursor.moveToFirst()
            val nreg = cursor.count
            var okVentaDirecta = true
            if (vent =="N") {
                okVentaDirecta = false
            }
            if (nreg > 0 && !okVentaDirecta) {
                funcion.mensaje(this, "", "No se puede aplicar descuentos con promociones!")
                false
            } else {
                true
            }
        } catch (e: java.lang.Exception) {
            false
        }
    }

    private fun cargarCabeceraCierre(){
        val sql = "SELECT * FROM vt_pedidos_cab " +
                " WHERE NUMERO = '$maximo' " +
                "   AND COD_CLIENTE        = '${ListaClientes.codCliente}'      " +
                "   AND COD_SUBCLIENTE     = '${ListaClientes.codSubcliente}'   " +
                "   AND COD_VENDEDOR       = '${ListaClientes.codVendedor}'     " +
                "   AND COD_EMPRESA        = '1'                                " +
                ""
        val lista = funcion.cargarDatos(funcion.consultar(sql))
        etSubtotal.setText(lista[0]["TOT_COMPROBANTE"])
        etTotalDesc.setText(lista[0]["TOT_DESCUENTO"])
        etDescFin.setText(lista[0]["DESCUENTO_FIN"])
        etDescVarios.setText(lista[0]["DESCUENTO_VAR"])
        etFecha.setText(lista[0]["FECHA"])
        etObservacion.setText(lista[0]["COMENTARIO"])
        etNroPedido.setText(maximo.toString())
        val desc =
            if (lista[0]["TOT_DESCUENTO"].toString().trim() != "" && lista[0]["TOT_DESCUENTO"].toString().trim() != "null"){
                lista[0]["TOT_DESCUENTO"].toString().replace(".", "").toInt()
            } else {
                0
            }
        val subtotal =
            if (lista[0]["TOT_COMPROBANTE"].toString().trim() != "" && lista[0]["TOT_COMPROBANTE"].toString().trim() != "null"){
                lista[0]["TOT_COMPROBANTE"].toString().replace(".", "").toInt()
            } else {
                0
            }
        val totalCab = subtotal - desc
        etTotalPedido.setText(funcion.entero(totalCab))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun enviarPedido(){
        val sql = "SELECT * FROM vt_pedidos_cab " +
                " WHERE TRIM(NUMERO)             = '$maximo'                                 " +
                "   AND TRIM(COD_CLIENTE)        = '${ListaClientes.codCliente.trim()}'      " +
                "   AND TRIM(COD_SUBCLIENTE)     = '${ListaClientes.codSubcliente.trim()}'   " +
                "   AND TRIM(COD_VENDEDOR)       = '${ListaClientes.codVendedor.trim()}'     " +
                "   AND TRIM(COD_EMPRESA)        = '1'                                       " +
                ""
        val lista = funcion.cargarDatos(funcion.consultar(sql))
        if (lista.size == 0){
            return
        }
        val total = lista[0]["TOT_COMPROBANTE"].toString().replace(".", "").toDouble()
        if (total < funcion.minVenta(ListaClientes.codVendedor)){
            funcion.toast(
                this, "El monto minimo para realizar la venta es ${
                    funcion.minVenta(
                        ListaClientes.codVendedor
                    )
                }."
            )
            return
        }
        val enviarPedido = EnviarPedido(this, lm, telMgr, lista[0])
        enviarPedido.enviarPedido()
    }

    class AdapterProducto(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val vistas: IntArray,
        private val valores: Array<String>,
        private val etAccion: EditText
    ) : BaseAdapter()
    {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("SetTextI18n", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.ven_ped_lista_pedidos_producto, parent, false)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                    if (!verificaCargado(position)){
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#FF0000"))
                    }
                } catch (e: java.lang.Exception){
                    e.printStackTrace()
                }
            }

            rowView.ibtn_se0.setBackgroundResource(R.drawable.border_textview)
            rowView.ibtn_se1.setBackgroundResource(R.drawable.border_textview)
            rowView.ibtn_se2.setBackgroundResource(R.drawable.border_textview)
            rowView.ibtn_se3.setBackgroundResource(R.drawable.border_textview)
            rowView.ibtn_se.setBackgroundResource(R.drawable.border_textview)
            rowView.ibtn_tactico.setBackgroundResource(R.drawable.border_textview)

            when(dataSource[position]["TIP_SURTIDO"]){
                "0" -> rowView.ibtn_se0.visibility = View.VISIBLE
                "1" -> rowView.ibtn_se1.visibility = View.VISIBLE
                "2" -> rowView.ibtn_se2.visibility = View.VISIBLE
                "3" -> rowView.ibtn_se3.visibility = View.VISIBLE
                else -> rowView.ibtn_se.visibility = View.VISIBLE
            }

            rowView.imgVentaRapida.setBackgroundResource(R.drawable.border_textview)
            rowView.imgVentaRapida.setOnClickListener{
                posProducto = position
                etAccion.setText("ventaRapida")
            }

            if (!dataSource[position]["IND_PROMO_ACT"].equals("N")){
                rowView.ibtn_tactico.visibility = View.VISIBLE
            }

            rowView.ibtn_tactico.setOnClickListener {
                posProducto = position
                etAccion.setText("promociones")
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (posProducto == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        private fun verificaCargado(position: Int):Boolean{
            if (articulosDetalle.indexOf("|${dataSource[position]["COD_ARTICULO"]}|") > -1){
                return false
            }
            return true
        }
    }


}
