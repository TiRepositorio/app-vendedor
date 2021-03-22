package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.*
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.*
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.agregarPromocion
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.btAgregarPromocion
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.btCancelar
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.spReferenciaPromo
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.trCombo
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.tvDetalleArticulo
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.tvdCantidadCombo
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.tvdCantidadPromo
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.tvdPrecioReferenciaPromo
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.tvdTotalPromo
import kotlinx.android.synthetic.main.dialogo_descuento.*
import java.text.NumberFormat
import kotlin.math.floor

class DialogoPromocion(
    var context: Context,
    var order: String,
    private var condicion: String,
    var descripcion: String
) {

    var lista: ArrayList<HashMap<String,String>> = ArrayList()
    var listaDetalle: ArrayList<HashMap<String,String>> = ArrayList()
    var funcion = FuncionesUtiles()
    var campos : String = ""
    private var descuento = 0
    private lateinit var listaDetallesInsertar : ArrayList<ContentValues>
    lateinit var adapter: Adapter.AdapterDialogoPromociones
    lateinit var dialogo: Dialog
    lateinit var fspReferencia : FuncionesSpinner

    companion object{
        var posicion = 0
    }

    fun dialogoComboBonificacion(nroPromocion: String){
        posicion = 0
        funcion = FuncionesUtiles(context)
        dialogo = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_bonificacion_combo)
        buscar(nroPromocion)
        mostrar(dialogo.lvPromocionBonificacion)
        if (lista[0]["IND_COMBO"].toString() == "S"){
            dialogo.trCombo.visibility = View.VISIBLE
            dialogo.tvdCantidadPromo.isEnabled = false
        }

        dialogo.btAgregarPromocion.setOnClickListener{
//            dialogo.agregarPromocion.setText("agregarPromocion")
            agregaBonificacion()
        }
        dialogo.agregarPromocion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (dialogo.agregarPromocion.text.toString().isEmpty()){
                   return
                } else {
                    agregarPromocion()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        dialogo.btCancelar.setOnClickListener{dialogo.dismiss()}
        dialogo.tvdCantidadPromo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()){
                    dialogo.tvdTotalPromo.text = "0"
                    lista[posicion]["CANTIDAD"] = "0"
                } else {
                    val subtotal = dialogo.tvdPrecioReferenciaPromo.text.toString().replace(".","").toInt() * s.toString().toInt()
                    dialogo.tvdTotalPromo.text = funcion.entero(subtotal)
                    lista[posicion]["CANTIDAD"] = s.toString()
                    lista[posicion]["SUBTOTAL"] = dialogo.tvdTotalPromo.text.toString()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        dialogo.tvdCantidadCombo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()){
                    dialogo.tvdTotalPromo.text = "0"
                    for (i in 0 until lista.size){
                        lista[i]["CANTIDAD"] = "0"
                    }
                } else {
                    for (i in 0 until lista.size){
                        val cantidad = lista[i]["CANT_VENTA"].toString().toInt() * s.toString().toInt()
                        val subtotal = lista[i]["PREC_CAJA"].toString().replace(".","").toInt() * cantidad
                        dialogo.tvdTotalPromo.text = funcion.entero(subtotal)
                        lista[i]["CANTIDAD"] = cantidad.toString()
                        lista[i]["SUBTOTAL"] = subtotal.toString()
                    }
                    dialogo.tvdCantidadPromo.setText(lista[posicion]["CANTIDAD"])
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

//        seleccionar(dialogo.btAgregarPromocion,tvResultado)
        dialogo.show()
    }

    fun dialogoDescuentoM(nroPromocion: String){
        funcion = FuncionesUtiles(context)
        dialogo = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_descuento)
        buscar(nroPromocion)
        mostrarDescuento(dialogo.lvPromocionDescuento)
        if (lista[0]["IND_COMBO"].toString() == "S" && lista[0]["IND_TIPO"] != "M"){
            dialogo.trCombo.visibility = View.VISIBLE
            dialogo.tvdCantidadPromo.isEnabled = false
        }

        dialogo.btAgregarPromocion.setOnClickListener{
//            dialogo.agregarPromocion.setText("agregarPromocion")
            cargaDatosDetalleDescuentoF()
        }
        dialogo.agregarPromocion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (dialogo.agregarPromocion.text.toString().isEmpty()){
                    return
                } else {
//                    agregarPromocion()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        dialogo.btCancelar.setOnClickListener{dialogo.dismiss()}
        dialogo.tvdCantidadPromo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()){
                    dialogo.tvdTotalPromo.text = "0"
                    lista[posicion]["CANTIDAD"] = "0"
                    lista[posicion]["PREC_CAJA"] = "0"
                } else {
                    val subtotal = dialogo.tvdPrecioReferenciaPromo.text.toString().replace(".","").toInt() * s.toString().toInt()
                    dialogo.tvdTotalPromo.text = funcion.entero(subtotal)
                    lista[posicion]["CANTIDAD"] = s.toString()
                    lista[posicion]["SUBTOTAL"] = dialogo.tvdTotalPromo.text.toString()
                    lista[posicion]["PREC_CAJA"] = dialogo.tvdPrecioReferenciaPromo.text.toString()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        dialogo.tvdCantidadCombo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()){
                    dialogo.tvdTotalPromo.text = "0"
                    for (i in 0 until lista.size){
                        lista[i]["CANTIDAD"] = "0"
                    }
                } else {
                    for (i in 0 until lista.size){
                        val subtotal = dialogo.tvdPrecioReferenciaPromo.text.toString().replace(".","").toInt() * s.toString().toInt()
                        val cantidad = lista[i]["CANT_VENTA"].toString().toInt() * s.toString().toInt()
                        dialogo.tvdTotalPromo.text = funcion.entero(subtotal)
                        lista[i]["CANTIDAD"] = cantidad.toString()
                        lista[i]["SUBTOTAL"] = subtotal.toString()
                    }
                    dialogo.tvdCantidadPromo.setText(lista[posicion]["CANTIDAD"])
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        if (lista[0]["IND_TIPO"].toString().trim() == "M"){
            dialogo.tvTituloPrecio.visibility = View.GONE
        }

//        seleccionar(dialogo.btAgregarPromocion,tvResultado)
        dialogo.show()
    }

    private fun spReferencias(position:Int){
        val campos : String = " a.REFERENCIA , a.MULT, a.DIV        , a.IND_BASICO      , " +
                " a.COD_IVA    , a.PORC_IVA           , a.COD_UNIDAD_REL  , " +
                " (b.CANT_MINIMA/a.mult) CANT_MINIMA  , " +
                " CASE WHEN a.COD_UNIDAD_REL = '01' THEN b.PREC_UNID ELSE b.PREC_CAJA END AS PRECIO "
        val tabla = " svm_st_articulos a, svm_articulos_precios b "
        val where : String = "     a.COD_ARTICULO = '" + lista[position]["COD_ARTICULO"] + "' " +
                " and a.COD_ARTICULO = b.COD_ARTICULO and b.COD_VENDEDOR = '" + ListaClientes.codVendedor + "' " +
                " and b.COD_LISTA_PRECIO = '" + Pedidos.spListaPrecios.getDato("COD_LISTA_PRECIO") + "' "
        val whereOpcional = ""
        val group = ""
        val order = ""
        fspReferencia = FuncionesSpinner(context,dialogo.spReferenciaPromo)
        fspReferencia.generaSpinner(campos,tabla,where,whereOpcional,group,order,"REFERENCIA","")
        dialogo.spReferenciaPromo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) { return }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dialogo.tvdPrecioReferenciaPromo.text = fspReferencia.getDato("PRECIO")
            }
        }
        dialogo.spReferenciaPromo.setSelection(0)
        dialogo.tvdPrecioReferenciaPromo.text = fspReferencia.getDato("PRECIO")
    }

    private fun buscar(nroPromocion:String){
        var sql : String = "SELECT DISTINCT a.COD_ARTICULO, a.DESC_ARTICULO, IFNULL(a.COD_UNIDAD_MEDIDA,'01') COD_UNIDAD_MEDIDA, a.REFERENCIA, a.CANT_VENTA" +
//                ", CASE WHEN IFNULL(a.COD_UNIDAD_MEDIDA,'01') = '01' OR TRIM(a.COD_UNIDAD_MEDIDA) = '' THEN b.PREC_UNID ELSE b.PREC_CAJA END AS PREC_CAJA " +
                ", (IFNULL(PREC_CAJA,1)/IFNULL(b.MULT,1)) * a.MULT PREC_CAJA " +
                ", b.MULT APMULT, IFNULL(MAX(a.MULT),1) MULT, b.CANT_MINIMA, a.IND_COMBO, a.IND_TIPO, a.NRO_PROMOCION, b.PORC_IVA "
        sql +=  "  FROM svm_promociones_art_cab a, svm_articulos_precios b " +
                " WHERE a.COD_EMPRESA  = b.COD_EMPRESA  " +
                "   AND a.COD_ARTICULO = b.COD_ARTICULO " +
                "   AND (a.COD_LISTA_PRECIO = '${Promociones.codListaPrecio}' or trim(a.COD_LISTA_PRECIO) = '' or a.COD_LISTA_PRECIO IS NULL ) " +
                "   AND (b.COD_LISTA_PRECIO = '${Promociones.codListaPrecio}' or trim(b.COD_LISTA_PRECIO) = '' or b.COD_LISTA_PRECIO IS NULL ) " +
                "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                "   AND (a.COD_CONDICION_VENTA = '${Promociones.condicionVenta}' or trim(a.COD_CONDICION_VENTA) = '' or a.COD_CONDICION_VENTA IS NULL ) " +
                "   AND  a.NRO_PROMOCION = '$nroPromocion' " +
                "   AND (a.TIP_CLIENTE   = '${ListaClientes.tipCliente}' or trim(a.TIP_CLIENTE) = '' or a.TIP_CLIENTE IS NULL ) " +
                " GROUP BY a.COD_ARTICULO "

        if (condicion != ""){
            sql += condicion
        }
        sql += " ORDER BY $order"

        lista = ArrayList()
        cargarDatos(funcion.consultar(sql),lista)
    }

    private fun cargarDatos(cursor: Cursor,lista:ArrayList<HashMap<String,String>>){
        for (i in 0 until cursor.count){
            val datos = HashMap<String,String>()
            for (j in 0 until cursor.columnCount){
                datos[cursor.getColumnName(j)] = funcion.dato(cursor,cursor.getColumnName(j))
            }
            datos["PREC_CAJA"] = funcion.entero(funcion.dato(cursor,"PREC_CAJA"))
            datos["CANTIDAD"] = "0"
            datos["SUBTOTAL"] = "0"
            lista.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrar(lvBusqueda: ListView){
        posicion = 0
        funcion.vistas = intArrayOf(R.id.tvb1,R.id.tvb2,R.id.tvb3,R.id.tvb4)
        funcion.valores = arrayOf("COD_ARTICULO","DESC_ARTICULO","CANT_VENTA","PREC_CAJA")
        adapter = Adapter.AdapterDialogoPromociones(context
                ,lista
                ,R.layout.promocion_bonificacion_lista
                ,funcion.vistas
                ,funcion.valores)
        lvBusqueda.adapter = adapter
        lvBusqueda.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = posicion
            posicion = position
            lvBusqueda.invalidateViews()
            cargarDetalles(position)
        }
        fspReferencia = FuncionesSpinner(context,dialogo.spReferenciaPromo)
        cargarDetalles(0)
    }

    private fun mostrarDescuento(lvBusqueda: ListView){
        posicion = 0
        funcion.vistas = intArrayOf(R.id.tvb1,R.id.tvb2,R.id.tvb3)
        funcion.valores = arrayOf("COD_ARTICULO","DESC_ARTICULO","PREC_CAJA")
        adapter = Adapter.AdapterDialogoPromociones(context
            ,lista
            ,R.layout.promocion_descuento_lista
            ,funcion.vistas
            ,funcion.valores)
        lvBusqueda.adapter = adapter
        lvBusqueda.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = posicion
            posicion = position
            lvBusqueda.invalidateViews()
            if (lista[position]["IND_TIPO"] == "M"){
                cargarDetallesM(position)
            } else {
                cargarDetalles(position)
            }
        }
        fspReferencia = FuncionesSpinner(context,dialogo.spReferenciaPromo)
        if (lista.size>0){
            if (lista[0]["IND_TIPO"] == "M"){
                cargarDetallesM(0)
            } else {
                cargarDetalles(0)
            }
        }
    }

    private fun cargarDetalles(position:Int){
        dialogo.tvDetalleArticulo.text = lista[position]["DESC_ARTICULO"]
        dialogo.tvdPrecioReferenciaPromo.text = lista[position]["PREC_CAJA"]
        dialogo.tvdCantidadPromo.setText(lista[position]["CANTIDAD"])
        dialogo.tvdTotalPromo.text = lista[position]["SUBTOTAL"]
        fspReferencia.cargarSpinner(arrayOf(lista[position]["REFERENCIA"].toString()))
    }

    private fun cargarDetallesM(position:Int){
        dialogo.tvDetalleArticulo.text = lista[position]["DESC_ARTICULO"]
        dialogo.tvdPrecioReferenciaPromo.text = lista[position]["PREC_CAJA"]
        dialogo.tvdCantidadPromo.setText(lista[position]["CANTIDAD"])
        dialogo.tvdTotalPromo.text = lista[position]["SUBTOTAL"]
        spReferencias(position)
    }

    @SuppressLint("SetTextI18n")
    private fun agregarPromocion(){
        if (lista[posicion]["IND_TIPO"] == "B"){
            agregaBonificacion()
            dialogo.dismiss()
            Promociones.etAccionPromo.setText("CERRAR")
        }
    }

    private fun agregaBonificacion(){
        if (lista[posicion]["IND_COMBO"] == "S"){
            if (dialogo.tvdCantidadCombo.text.toString() == "0"){
                funcion.mensaje(context,"¡Atención!","La cantidad debe ser mayor a 0.")
                return
            }
            if (dialogo.tvdCantidadCombo.text.toString().isEmpty()){
                funcion.mensaje(context,"¡Atención!","La cantidad no puede ser nula.")
                return
            }
        } else {
            if (dialogo.tvdCantidadPromo.text.toString() == "0"){
                funcion.mensaje(context,"¡Atención!","La cantidad debe ser mayor a 0.")
                return
            }
            if (dialogo.tvdCantidadPromo.text.toString().isEmpty()){
                funcion.mensaje(context,"¡Atención!","La cantidad no puede ser nula.")
                return
            }
        }
        cargaDatosDetalle()
    }

    private fun maxPedido(){
        val sql = "SELECT MAX(NUMERO) MAXIMO from vt_pedidos_cab where COD_VENDEDOR = '${ListaClientes.codVendedor}'"
        val cursor: Cursor = funcion.consultar(sql)
        if (cursor.moveToFirst()) {
            Pedidos.maximo = if (funcion.datoEntero(cursor,"MAXIMO") > funcion.ultPedidoVenta(ListaClientes.codVendedor)) {
                funcion.datoEntero(cursor,"MAXIMO")
            } else {
                funcion.ultPedidoVenta(ListaClientes.codVendedor)
            }
            Pedidos.etFechaPedido.setText(funcion.getFechaActual())
            Pedidos.fechaInt = funcion.fecha(Pedidos.etFechaPedido.text.toString()).toString()
        }
        Pedidos.maximo = Pedidos.maximo + 1
    }

    private fun cargaDatosCabecera(){
        val values = ContentValues()
        values.put("COD_EMPRESA", "1")
        values.put("COD_CLIENTE", ListaClientes.codCliente)
        values.put("COD_SUBCLIENTE", ListaClientes.codSubcliente)
        values.put("COD_VENDEDOR", ListaClientes.codVendedor)
        values.put("COD_LISTA_PRECIO", Promociones.codListaPrecio)
        values.put("FECHA", Pedidos.etFechaPedido.text.toString())
        values.put("FECHA_INT", Pedidos.fechaInt)
        values.put("ESTADO", "P")
        values.put("COD_CONDICION_VENTA", Promociones.condicionVenta)
        values.put("DIAS_INICIAL", Pedidos.spCondicionDeVenta.getDato("DIAS_INICIAL"))
        values.put("COD_MONEDA", Pedidos.spListaPrecios.getDato("COD_MONEDA"))
        values.put("DECIMALES", Pedidos.spListaPrecios.getDato("DECIMALES"))
        values.put("TIP_CAMBIO", "1")
        values.put("DESC_CLIENTE", ListaClientes.descCliente)
        values.put("NRO_AUTORIZACION", Pedidos.claveAutorizacion)
        values.put("LATITUD", " ")
        values.put("LONGITUD", " ")
        values.put("IND_PRESENCIAL", "N")
        values.put("HORA_ALTA", funcion.getHoraActual())
        values.put("FEC_ALTA", funcion.getFechaActual())
        if (Pedidos.spListaPrecios.getDato("DECIMALES") == "0") {
            var totalComprobante: Int
            totalComprobante = Pedidos.etTotalPedidos.text.toString().replace(".", "").toInt()
            if (totalComprobante == 0){
                totalComprobante = adapter.getTotalEntero("SUBTOTAL")
            } else {
                totalComprobante += adapter.getTotalEntero("SUBTOTAL")
            }

            values.put( "TOT_COMPROBANTE", totalComprobante.toString())
            funcion.entero(totalComprobante)
            Pedidos.totalPedido = funcion.entero(totalComprobante)
        } else {
            var total: String = Pedidos.etTotalPedidos.text.toString().replace(".", "")
            total = if (total.toInt() == 0){
                adapter.getTotalDecimal("SUBTOTAL").toString()
            } else {
                (total.toFloat() + adapter.getTotalDecimal("SUBTOTAL").toFloat()).toString()
            }
            total = total.replace(",",".")
            Pedidos.totalComprobanteDecimal = total.toDouble()
            values.put("TOT_COMPROBANTE", Pedidos.totalComprobanteDecimal)
            val nf: NumberFormat = NumberFormat.getInstance()
            nf.minimumFractionDigits = Pedidos.spListaPrecios.getDato("DECIMALES").toInt()
            nf.maximumFractionDigits = Pedidos.spListaPrecios.getDato("DECIMALES").toInt()
            nf.format(Pedidos.totalComprobanteDecimal)
            Pedidos.totalPedido = nf.format(Pedidos.totalComprobanteDecimal)
        }
        if (Pedidos.nuevo) {
            maxPedido()
            values.put("NUMERO", Pedidos.maximo)
            try {
                funcion.insertar("vt_pedidos_cab", values)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            values.put("NUMERO", Pedidos.maximo)
            values.put("NRO_ORDEN_COMPRA", Pedidos.etNroOrdenCompra.text.toString())
            try {
                funcion.actualizar("vt_pedidos_cab", values, "NUMERO = '${Pedidos.maximo}' and COD_VENDEDOR = '${ListaClientes.codVendedor}'")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        Pedidos.etSubtotales.setText(Pedidos.totalPedido)
        Pedidos.etTotalPedidos.setText(Pedidos.totalPedido)
        Pedidos.etNroPedidos.setText(Pedidos.maximo.toString())
        Pedidos.nuevo = false
    }

    private fun cargarDetalle(lista:ArrayList<HashMap<String,String>>){
        for (i in 0 until lista.size){
            if (lista[i]["CANTIDAD"] != "0"){
                val values = ContentValues()
                values.put("COD_EMPRESA", "1")
                values.put("NUMERO", Pedidos.maximo)
                values.put("COD_VENDEDOR", ListaClientes.codVendedor)
                values.put("COD_ARTICULO", lista[i]["COD_ARTICULO"])
                values.put("CANTIDAD", lista[i]["CANTIDAD"])
                values.put("COD_UNIDAD_MEDIDA", lista[i]["COD_UNIDAD_MEDIDA"])
                values.put("COD_IVA", lista[i]["COD_IVA"])
                values.put("PORC_IVA", lista[i]["PORC_IVA"])
                // values2.put("porc_desc_var",_porc_descuento); // Contact
                values.put("IND_DEPOSITO", "V")
                try {
                    if (Pedidos.spListaPrecios.getDato("DECIMALES") == "0") {
                        values.put("PRECIO_UNITARIO", lista[i]["PREC_CAJA"].toString().replace(".",""))
                        values.put("MONTO_TOTAL", lista[i]["SUBTOTAL"].toString().replace(".",""))
                        values.put("TOTAL_IVA", "0")
                        values.put("PRECIO_UNITARIO_C_IVA", lista[i]["PREC_CAJA"].toString().replace(".",""))
                        values.put("MONTO_TOTAL_CONIVA", lista[i]["SUBTOTAL"].toString().replace(".",""))
                        values.put("IND_BLOQUEADO", "N")
                        values.put("PRECIO_LISTA", "0")
                    } else {
                        val nf: NumberFormat = NumberFormat.getInstance()
                        nf.minimumFractionDigits = Pedidos.spListaPrecios.getDato("DECIMALES").toInt()
                        nf.maximumFractionDigits = Pedidos.spListaPrecios.getDato("DECIMALES").toInt()
                        var total: String = lista[i]["PREC_CAJA"].toString().replace(".", "")
                        total = total.replace(",", ".")
                        values.put("PRECIO_UNITARIO", total)
                        total = lista[i]["SUBTOTAL"].toString().replace(".", "")
                        total = total.replace(",", ".")
                        values.put("MONTO_TOTAL", total)
                        values.put("TOTAL_IVA", "0")
                        values.put("PRECIO_UNITARIO_C_IVA", lista[i]["PREC_CAJA"].toString().replace(",", "."))
                        values.put("MONTO_TOTAL_CONIVA", total)
                        values.put("IND_BLOQUEADO", "N")
                        values.put("PRECIO_LISTA", lista[i]["PREC_CAJA"].toString().replace(".", "").replace(",", "."))
                    }
                } catch (e: java.lang.Exception) {
                    values.put("PORC_DESC_VAR", "0")
                    values.put("DESCUENTO_VAR", "0")
                }
                values.put("MULT", Pedidos.spReferencias.getDato("MULT"))
                values.put("TIP_COMPROBANTE_REF", "PRO")
                values.put("SER_COMPROBANTE_REF", ListaClientes.codVendedor)
                values.put("NRO_COMPROBANTE_REF", Pedidos.etNroPedidos.text.toString())
                values.put("ORDEN_REF", Pedidos.etNroOrdenCompra.text.toString())
                values.put("IND_SISTEMA", "S")
                values.put("IND_TRANSLADO", Pedidos.vent)
                values.put("IND_BLOQUEADO", "N")
                try {
                    values.put("PROMOCION", lista[i]["NRO_PROMOCION"])
                    values.put("TIP_PROMOCION", lista[i]["IND_TIPO"])
                    values.put("NRO_PROMOCION", lista[i]["NRO_PROMOCION"])
                    values.put("NRO_AUTORIZACION", Pedidos.claveAutorizacion)
                    values.put("MONTO_DESC_TC", "0")
                    Pedidos.claveAutorizacion = ""
                } catch (e: java.lang.Exception) {
                    funcion.mensaje(context,"Error!",e.message.toString())
                    e.printStackTrace()
                }
                listaDetallesInsertar.add(values)
            }
        }
    }

    private fun cargarDetalleF(lista:ArrayList<HashMap<String,String>>){
        val cantidad: Int
        var precioFinal = 0
        if (lista[0]["IND_TIPO"].toString().trim() == "F" && (listaDetalle[0]["IND_TIPO"] == "M" || listaDetalle[0]["IND_TIPO"] == "P" || listaDetalle[0]["IND_TIPO"] == "C")){
            cantidad = adapter.getTotalEntero("CANTIDAD")
            precioFinal = 0
        } else {
            cantidad = adapter.getTotalEntero("SUBTOTAL")
        }
        descuento = 0
        for (i in 0 until listaDetalle.size){
            val desde = listaDetalle[i]["CANT_DESDE"].toString().trim().toInt()
            val hasta = listaDetalle[i]["CANT_HASTA"].toString().trim().toInt()
            if (cantidad in desde..hasta){
                descuento = listaDetalle[i]["DESCUENTO"].toString().trim().toInt()
                if (listaDetalle[i]["IND_TIPO"] == "C"){
                    if (Pedidos.spListaPrecios.getDato("DECIMALES") == "0"){
                        precioFinal = listaDetalle[i]["PRECIO_GS"].toString().toInt()
                        if (listaDetalle[i]["IND_TIPO"] == "C"){
                            for (j in 0 until lista.size){
                                lista[j]["PREC_CAJA"] = "$precioFinal"
                            }
                        }
                    } else {
                        precioFinal = listaDetalle[i]["PRECIO_RS"].toString().toDouble().toInt()
                        if (listaDetalle[i]["IND_TIPO"] == "C"){
                            for (j in 0 until lista.size){
                                lista[j]["PREC_CAJA"] = "$precioFinal"
                            }
                        }
                    }
                }
            }
        }
        if (descuento == 0){
            if (lista[0]["IND_TIPO"] == "M") {
                funcion.mensaje(context,"¡Atención!","No alcanzó el monto mínimo para acceder a la promoción.\nTotal: $cantidad")
            } else {
                funcion.mensaje(context,"¡Atención!","La cantidad vendida no posee ninguna promoción.\nTotal: $cantidad")
            }
            return
        } else {
            if (listaDetalle[0]["IND_TIPO"] == "M") {
                funcion.mensaje(context,"¡Atención!","Con esta cantidad obtiene un descuento de $descuento en cada producto.")
            }
            if (listaDetalle[0]["IND_TIPO"] == "P") {
                funcion.mensaje(context,"¡Atención!","Con esta cantidad obtiene un descuento del $descuento%.")
            }
            if (listaDetalle[0]["IND_TIPO"] == "C") {
                funcion.mensaje(context,"¡Atención!","Con esta cantidad el precio de cada articulo es $precioFinal.")
            }
            if (listaDetalle[0]["IND_TIPO"] == "D") {
                funcion.mensaje(context,"¡Atención!","Con este monto obtienes un descuento del $descuento%.")
            }
            if (listaDetalle[0]["IND_TIPO"] == "F") {
                funcion.mensaje(context,"¡Atención!","Con esta cantidad obtiene un descuento del $descuento%.")
            }
        }
        for (i in 0 until lista.size){
            if (lista[i]["CANTIDAD"] != "0"){
                val values = ContentValues()
                values.put("COD_EMPRESA", "1")
                values.put("NUMERO", Pedidos.maximo)
                values.put("COD_VENDEDOR", ListaClientes.codVendedor)
                values.put("COD_ARTICULO", lista[i]["COD_ARTICULO"])
                values.put("CANTIDAD", lista[i]["CANTIDAD"])
                values.put("COD_UNIDAD_MEDIDA", lista[i]["COD_UNIDAD_MEDIDA"])
                values.put("COD_IVA", lista[i]["COD_IVA"])
                values.put("PORC_IVA", lista[i]["PORC_IVA"])
                // values2.put("porc_desc_var",_porc_descuento); // Contact
                values.put("IND_DEPOSITO", "V")
                try {
                    if (Pedidos.spListaPrecios.getDato("DECIMALES") == "0") {
                        var precio = lista[i]["PREC_CAJA"].toString().replace(".","").trim().toDouble()
                        if (listaDetalle[0]["IND_TIPO"] != "C" && listaDetalle[0]["IND_TIPO"] != "M"){
                            precio = floor(precio - ((precio * descuento)/100))
                        } else {
                            if (listaDetalle[0]["IND_TIPO"] == "M"){
                                precio = floor(precio - listaDetalle[0]["DESCUENTO"].toString().replace(".","").trim().toDouble())
                            }
                        }
                        val subtotal = precio * lista[i]["CANTIDAD"].toString().trim().toInt()
                        values.put("PRECIO_UNITARIO", precio)
                        values.put("MONTO_TOTAL", subtotal)
                        values.put("TOTAL_IVA", "0")
                        values.put("PRECIO_UNITARIO_C_IVA", precio)
                        values.put("MONTO_TOTAL_CONIVA", subtotal)
                        values.put("IND_BLOQUEADO", "N")
                        values.put("PRECIO_LISTA", "0")
                    } else {
                        var precio = lista[i]["PREC_CAJA"].toString().replace(".","").replace(",",".").trim().toDouble()
                        if (listaDetalle[0]["IND_TIPO"] != "C"){
                            precio = floor(precio - ((precio * descuento)/100))
                        }
                        val subtotal = precio * lista[i]["CANTIDAD"].toString().trim().toInt()
                        val nf: NumberFormat = NumberFormat.getInstance()
                        nf.minimumFractionDigits = Pedidos.spListaPrecios.getDato("DECIMALES").toInt()
                        nf.maximumFractionDigits = Pedidos.spListaPrecios.getDato("DECIMALES").toInt()
                        var total: String = lista[i]["PREC_CAJA"].toString().replace(".", "")
                        total = total.replace(",", ".")
                        values.put("PRECIO_UNITARIO", precio)
                        total = lista[i]["SUBTOTAL"].toString().replace(".", "")
                        total = total.replace(",", ".")
                        values.put("MONTO_TOTAL", subtotal)
                        values.put("TOTAL_IVA", "0")
                        values.put("PRECIO_UNITARIO_C_IVA", precio)
                        values.put("MONTO_TOTAL_CONIVA", subtotal)
                        values.put("IND_BLOQUEADO", "N")
                        values.put("PRECIO_LISTA", lista[i]["PREC_CAJA"].toString().replace(".", "").replace(",", "."))
                    }
                } catch (e: java.lang.Exception) {
                    values.put("PORC_DESC_VAR", "0")
                    values.put("DESCUENTO_VAR", "0")
                }
                values.put("MULT", Pedidos.spReferencias.getDato("MULT"))
                values.put("TIP_COMPROBANTE_REF", "PRO")
                values.put("SER_COMPROBANTE_REF", ListaClientes.codVendedor)
                values.put("NRO_COMPROBANTE_REF", Pedidos.etNroPedidos.text.toString())
                values.put("ORDEN_REF", Pedidos.etNroOrdenCompra.text.toString())
                values.put("IND_SISTEMA", "S")
                values.put("IND_TRANSLADO", Pedidos.vent)
                values.put("IND_BLOQUEADO", "N")
                try {
                    values.put("PROMOCION", lista[i]["NRO_PROMOCION"])
                    values.put("TIP_PROMOCION", lista[i]["IND_TIPO"])
                    values.put("NRO_PROMOCION", lista[i]["NRO_PROMOCION"])
                    values.put("NRO_AUTORIZACION", Pedidos.claveAutorizacion)
                    values.put("MONTO_DESC_TC", "0")
                    Pedidos.claveAutorizacion = ""
                } catch (e: java.lang.Exception) {
                    funcion.mensaje(context,"Error!",e.message.toString())
                    e.printStackTrace()
                }
                listaDetallesInsertar.add(values)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cargaDatosDetalle(){
        listaDetallesInsertar = ArrayList()
        var multiplo = 1000
        cargaDatosCabecera()
        var cantidad = 0.0
        if (lista[0]["IND_COMBO"]=="S"){
            cantidad = dialogo.tvdCantidadCombo.text.toString().toDouble()
        } else {
            for (i in 0 until lista.size){
                cantidad += funcion.entero(lista[i]["CANTIDAD"].toString()).toDouble()
            }
        }
        for (i in 0 until lista.size){
            var bonificado : Double = cantidad / lista[i]["CANT_VENTA"].toString().toDouble()
            if (lista[0]["IND_COMBO"] == "S"){
                bonificado = cantidad
            }
            if (bonificado<1){
                funcion.mensaje(context,"¡Atención!",
                        "Debe vender un mínimo de ${lista[i]["CANT_VENTA"]} del artículo ${lista[i]["COD_ARTICULO"]}.")
                return
            } else {
                if (bonificado < multiplo){
                    multiplo = floor(bonificado).toInt()
                }
            }
            if (!verificaCargado(i)){
                return
            }
        }
        cargarDetalle(lista)
        val sql = "SELECT DISTINCT a.COD_ARTICULO, a.CANT_DESDE, a.COD_UNIDAD_MEDIDA, '1' MULT, a.IND_MULTIPLE, a.NRO_PROMOCION, a.IND_TIPO, " +
                "                         b.COD_IVA, b.PORC_IVA " +
                "  FROM svm_promociones_art_det a, svm_st_articulos b " +
                " WHERE a.NRO_PROMOCION = '${lista[0]["NRO_PROMOCION"]}' AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' " +
                "   AND a.COD_ARTICULO = b.COD_ARTICULO " +
                "   AND a.COD_EMPRESA  = b.COD_EMPRESA "
        listaDetalle = funcion.cargarDatos(funcion.consultar(sql))
        for (i in 0 until listaDetalle.size){
            val cantidad = listaDetalle[i]["CANT_DESDE"].toString().toInt()
            if (listaDetalle[i]["IND_MULTIPLE"].toString().trim() == "S"){
                listaDetalle[i]["CANTIDAD"] = (cantidad * multiplo).toString()
            } else {
                listaDetalle[i]["CANTIDAD"] = (cantidad).toString()
            }
            listaDetalle[i]["PREC_CAJA"] = "0"
            listaDetalle[i]["SUBTOTAL"] = "0"
        }

        cargarDetalle(listaDetalle)
        insertarBonificacion()
        dialogo.dismiss()
        Pedidos.etAccionPedidos.setText("actualizarDetalle")
        Pedidos.etAccionPedidos.setText("recalcularTotal")
//            funcion.insertar("vt_pedidos_det", values)
    }

    @SuppressLint("SetTextI18n")
    private fun cargaDatosDetalleDescuentoF(){
        listaDetallesInsertar = ArrayList()
        cargaDatosCabecera()
        val sql = "SELECT DISTINCT a.COD_ARTICULO, a.CANT_DESDE, a.CANT_HASTA, a.COD_UNIDAD_MEDIDA, '1' MULT, " +
                "                         a.IND_MULTIPLE, a.NRO_PROMOCION, a.IND_TIPO, " +
                "                         b.COD_IVA, b.PORC_IVA, a.DESCUENTO, a.PRECIO_GS, a.PRECIO_RS " +
                "  FROM svm_promociones_art_det a, svm_st_articulos b " +
                " WHERE trim(a.NRO_PROMOCION) = '${lista[0]["NRO_PROMOCION"]}' " +
                "   AND trim(a.COD_VENDEDOR)  = '${ListaClientes.codVendedor}' " +
//                "   AND trim(a.COD_ARTICULO)  = trim(b.COD_ARTICULO) " +
                "   AND ifnull(trim(a.COD_EMPRESA),'1')  = ifnull(trim(b.COD_EMPRESA),'1') "
        listaDetalle = funcion.cargarDatos(funcion.consultar(sql))
        cargarDetalleF(lista)
//        cargarDetalle(listaDetalle)
        if (descuento == 0){
            return
        }
        for (i in 0 until lista.size){
            if (!verificaCargado(i)){
                return
            }
        }
        insertarBonificacion()
        dialogo.dismiss()
        Pedidos.etAccionPedidos.setText("actualizarDetalle")
//            funcion.insertar("vt_pedidos_det", values)
    }

    private fun insertarBonificacion(){
        for (i in 0 until listaDetallesInsertar.size){
            funcion.insertar("vt_pedidos_det", listaDetallesInsertar[i])
        }
    }

    private fun verificaCargado(position:Int):Boolean{
        val sql : String = ("SELECT DISTINCT COD_ARTICULO FROM vt_pedidos_det "
                +  " WHERE NUMERO = '${Pedidos.maximo}' "
                +  "   AND COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                +  "   AND COD_ARTICULO = '${lista[position]["COD_ARTICULO"]}' "
                +  " ")
        if (funcion.consultar(sql).count > 0 && lista[position]["CANTIDAD"].toString().toInt() > 0){
            funcion.mensaje(context,"¡Atención!","El artícuolo '${lista[position]["COD_ARTICULO"]}' ya fue agregado al pedido.")
            return false
        }
        return true
    }


}