package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.location.LocationManager
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.FuncionesDispositivo
import apolo.vendedores.com.utilidades.FuncionesUbicacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
import java.text.NumberFormat
import kotlin.math.roundToInt

class EnviarPedido(
    var context: Context,
    private var lm: LocationManager,
    private var telMgr: TelephonyManager,
    private var cabeceraHash: HashMap<String, String>,
    private var codEmpresa: String
) {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var contexto : Context
        var cabecera = ""
        var detalles = ""
        var resultado = ""
        var descVarios = Pedidos.etDescVariosPedidos.text.toString().trim()
        var descFin = Pedidos.etDescFinancPedidos.text.toString().trim()
    }

    var error = ""
    private var dispositivo = FuncionesDispositivo(context)
    private var ubicacion = FuncionesUbicacion(context)
    var funcion = FuncionesUtiles(context)
    private var totalPedido = 0
    lateinit var cursor : Cursor

    @SuppressLint("Recycle", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun enviarPedido():Boolean {
        if (!dispositivo.modoAvion()) {
            return false
        }
        if (!dispositivo.zonaHoraria()) {
            return false
        }
        if (!dispositivo.tarjetaSim(telMgr)) {
            return false
        }
		if (!ubicacion.validaUbicacionSimulada(lm)) {
			return false
		}
        if (Pedidos.etTotalPedidos.toString() == "" || Pedidos.etTotalPedidos.toString() == "0") {
            return false
        }
        descVarios = Pedidos.etDescVariosPedidos.text.toString().trim()
        descFin = Pedidos.etDescFinancPedidos.text.toString().trim()
        if (descVarios == "0"){descVarios = ""}
        if (descFin == "0"){descFin=""}
        contexto = context
        if (MainActivity2.rooteado){
            cabeceraHash["COMENTARIO"] = "El telefono esta rooteado." + "\n" + cabeceraHash["COMENTARIO"]
        }
        try {
            var vIntoCab: String

            // 25 campos
            ubicacion.obtenerUbicacion(lm)
            vIntoCab = ("'$codEmpresa'" // cod_empresa
                    + ",'${ListaClientes.codSucursalCliente}'" // cod_sucursal
                    + ",to_date('${Pedidos.etFechaPedido.text}','dd/MM/yyyy')" // fec_comprobante
                    + ",'PRO'" // tip_comprobante
                    + ",'${ListaClientes.codVendedor}'" // ser_comprobante
                    // +","+ et_tot_pedido.getText().toString().replace(".", "")
                    + ",'${ListaClientes.codVendedor}'" // cod_Vendedor
                    + ",'${ListaClientes.codCliente}'" // cod_Cliente
                    + ",'${ListaClientes.codSubcliente}'" // cod_Subcliente
                    + ",'${cabeceraHash["COD_CONDICION_VENTA"]}'" // cod_Condicion_Venta
                    + ",'${cabeceraHash["COD_LISTA_PRECIO"]}'" // cod_Lista_Precio
                    + ",'${cabeceraHash["COD_MONEDA"]}'" // cod_Moneda
                    + ",'P'" // estado
                    + ",'PRO'" // tip_Comprobante_Ref
                    + ",'${ListaClientes.codVendedor}'" // ser_Comprobante_Ref
                    + ",'${Pedidos.etNroPedidos.text}'" // nro_Comprobante_ref
//                    					+ ",'" + Aplicacion._lati + "'" // latitud
//                    					+ ",'" + Aplicacion._longi + "'" // longitud
                    + ",'${ubicacion.latitud}'" // latitud
                    + ",'${ubicacion.longitud}'" // longitud
//                    + ",''" // latitud
//                    + ",''" // longitud
                    + ",'${Pedidos.etNroOrdenCompra.text}'" // nro_Orden_Compra
                    + ",'${Pedidos.depo}'" // ind_Deposito
                    + ",'S'" // ind_Sistema
                    + ",'${descVarios.replace(",",".")}'" // porc_Desc_Var
                    + ",'${descFin.replace(",",".")}'") // descuento_fin
            if (Pedidos.spListaPrecios.getDato("DECIMALES") == "0") {
                vIntoCab = if (descVarios != "") {
                    val desVar: Float = "0".toFloat()
                    (vIntoCab + ",'" + desVar.roundToInt() + "'") // descuento_var
                } else {
                    "$vIntoCab,0" // descuento_var
                }
                vIntoCab = if (descFin != "") {
                    val desFin: Float = "0".toFloat()
                    (vIntoCab + ",'"
                            + desFin.roundToInt() + "'") // descuento_fin
                } else {
                    "$vIntoCab,0" // descuento_fin
                }
            } else {
                vIntoCab = if (descVarios != "") {
                    val desVar: Double = Pedidos.totalPedido.replace(",", ".").toDouble() * descVarios.replace(",",".").toDouble() / 100
                    "$vIntoCab,'$desVar'" // descuento_var
                } else {
                    "$vIntoCab,0" // descuento_var
                }
                vIntoCab = if (descFin != "") {
                    val desFin: Double = Pedidos.totalPedido.replace(",", ".").toDouble() * descFin.replace(",",".").toDouble() / 100
                    "$vIntoCab,'$desFin'" // descuento_fin
                } else {
                    "$vIntoCab,0" // descuento_fin
                }
            }
            vIntoCab = "$vIntoCab,'${Pedidos.indBloqueado}'" // bloqueado_x_prec
            vIntoCab = (vIntoCab + "," + "'" + Pedidos.pedidoBloqCond + "'" // bloqueado_x_cond
                    + ",'" + Pedidos.vent + "'" // ind_venta
                    + ",'" + cabeceraHash["COMENTARIO"] + "'" // comentario
                    + ",'" + cabeceraHash["NRO_AUTORIZACION"] + "'" // "nro_autorizacion
                    + ",'" + cabeceraHash["IND_PRESENCIAL"].toString().replace("null","") + "'" // "indica venta presencial
                    + ",to_date('" + cabeceraHash["FEC_ALTA"] + " " + cabeceraHash["HORA_ALTA"] + "','dd/MM/yyyy hh24:mi:ss')" // "fecha y hora de alta
                    + ",'" + Pedidos.claveAutorizacion + "'") //numero de autorizacion descuentos varios
            cabecera = vIntoCab
            var vIntoDet: String
            var where = (" NUMERO=" + "'" + Pedidos.maximo.toString() + "' "
                    + "   AND TRIM(COD_ARTICULO) <> '' "
                    + "   and COD_VENDEDOR = '" + ListaClientes.codVendedor + "'")
            where = "$where "
            cursor = MainActivity.bd!!.query(
                "vt_pedidos_det", arrayOf(
                    "COD_EMPRESA", "ORDEN", "COD_ARTICULO", "CANTIDAD",
                    "COD_UNIDAD_MEDIDA", "COD_IVA", "PORC_IVA", "PRECIO_UNITARIO_C_IVA",
                    "MONTO_TOTAL_CONIVA", "PORC_DESC_VAR", "DESCUENTO_VAR", "PRECIO_LISTA",
                    "MULT", "ORDEN_REF", "IND_SISTEMA", "IND_TRANSLADO",
                    "IND_DEPOSITO", "IND_BLOQUEADO", "NRO_PROMOCION", "TIP_PROMOCION",
                    "NRO_AUTORIZACION", "MONTO_DESC_TC"
                ), where,  // null,
                null, null, null, null
            )
            val nreg: Int = cursor.count
            cursor.moveToFirst()
            totalPedido = 0
            var cont = 1
            detalles = ""
            var sql2: String
            for (i in 0 until nreg) {
                vIntoDet = ("'$codEmpresa'," //cod_empresa
                        + "'" + ListaClientes.codSucursalCliente + "'," //cod_sucursal
                        + "'PRO'," //tip_comprobante
                        + "'" + ListaClientes.codVendedor + "'," //ser_comprobante
                        + "'" + cont.toString() + "'," //orden
                        + "'" + funcion.dato(cursor,"COD_ARTICULO") + "'," //cod_articulo
                        +       funcion.dato(cursor,"CANTIDAD") + "," //cantidad
                        + "'" + funcion.dato(cursor,"COD_UNIDAD_MEDIDA") + "'," //cod_unidad_medida
                        + "'" + funcion.dato(cursor,"COD_IVA") + "'," //cod_iva
                        +       funcion.dato(cursor,"PORC_IVA")) //porc_iva
                if (Pedidos.spListaPrecios.getDato("DECIMALES") == "0") {
                    vIntoDet = (vIntoDet + "," + funcion.dato(cursor,"PRECIO_UNITARIO_C_IVA").replace(",", "").replace(".","") + "") //precio_unitario_c_iva
                    vIntoDet = if (descVarios != "") { (vIntoDet + "," + descVarios.replace(",",".").toFloat()) //porc_desc_var
                    } else {
                        "$vIntoDet,0"
                    }
                    vIntoDet = if (descFin != "") {
                        (vIntoDet + "," + descFin.replace(",",".").toFloat()) //porc_desc_fin
                    } else {
                        "$vIntoDet,0"
                    }
                    vIntoDet = (vIntoDet + "," + funcion.dato(cursor,"PRECIO_LISTA")) //precio_list.replace(".", ""))
                } else {
                    vIntoDet = (vIntoDet + "," + funcion.dato(cursor,"PRECIO_UNITARIO_C_IVA")) //precio_unitario_c_iva
                        .replace(",", ".")
                    vIntoDet = if (descVarios != "") {
                        (vIntoDet + "," + descVarios.replace(",",".").toDouble()) //porc_desc_var
                    } else {
                        "$vIntoDet,0"
                    }
                    vIntoDet = if (descFin != "") {
                        (vIntoDet + "," + descFin.replace(",",".").toDouble()) //porc_desc_fin
                    } else {
                        "$vIntoDet,0"
                    }
                    vIntoDet = (vIntoDet + "," + funcion.dato(cursor,"PRECIO_LISTA")) //precio_lista
                        .replace(",", ".")
                }
                vIntoDet = (vIntoDet
                        + "," + funcion.dato(cursor,"MULT") + "," //mult
                        + "'PRO'," //tip_comprobante_ref
                        + "'" + ListaClientes.codVendedor + "'," //ser_comprobante_ref
                        + "'" + Pedidos.etNroPedidos.text.toString() + "'," //nro_comprobante_ref
                        + "'" + cont.toString() + "'," //nro_orden_ref
                        + "'" + funcion.dato(cursor,"IND_SISTEMA") + "'," //ind_sistema
                        + "'" + funcion.dato(cursor,"IND_BLOQUEADO") + "'," //ind_bloqueado
                        + "'" + funcion.dato(cursor,"NRO_PROMOCION") + "'," //nro_promocion
                        + "'" + funcion.dato(cursor,"TIP_PROMOCION") + "'," //tipo_promocion
                        + "'" + funcion.dato(cursor,"NRO_AUTORIZACION") + "'," //nro_autorizacion
                        + "'" + funcion.dato(cursor,"MONTO_DESC_TC") + "'") //monto_desc_a_favor
                cont += 1
                sql2 = vIntoDet
                detalles = if (detalles == "") {
                    "$sql2;"
                } else {
                    "$detalles$sql2;"
                }
                val articuloVal = funcion.dato(cursor,"COD_ARTICULO")
                val codUnidadMedidaVal = funcion.dato(cursor,"COD_UNIDAD_MEDIDA")
                val precioUnitarioConIva = funcion.dato(cursor,"PRECIO_UNITARIO_C_IVA")
                val nroPromocionVal = funcion.dato(cursor,"NRO_PROMOCION").trim()
                if (funcion.dato(cursor,"NRO_PROMOCION").trim().isEmpty()){
                    if (!validarPedido(funcion.dato(cursor,"COD_ARTICULO"),
                            cabeceraHash["COD_LISTA_PRECIO"].toString(),
                            funcion.dato(cursor,"COD_UNIDAD_MEDIDA"),
                            funcion.dato(cursor,"PRECIO_UNITARIO_C_IVA")))
                    {
                        Pedidos.etAccionPedidos.setText("validarPedido*"+funcion.dato(cursor,"COD_ARTICULO"))
                        return false
                    }
                }
                cursor.moveToNext()
            }
            try {
                val t: Double = Pedidos.etTotalPedidos.text.toString().replace(".", "").replace(",", ".").toDouble()
                if (t < funcion.minVenta(ListaClientes.codVendedor) && !cabeceraHash["COD_CONDICION"].equals("99") && Pedidos.spListaPrecios.getDato("DECIMALES") == "0") {
                    val nf = NumberFormat.getInstance()
                    nf.minimumFractionDigits = 0
                    nf.maximumFractionDigits = 0
                    Toast.makeText(context,"El monto minimo para una venta es: " + nf.format(funcion.minVenta(ListaClientes.codVendedor)),Toast.LENGTH_LONG).show()
                    return false
                }
            } catch (e: java.lang.Exception) {
            }
            if (cabeceraHash["COD_CONDICION"].equals("99")){
                Toast.makeText(context,"No se permite la condicion de Venta 99 (Bonificación)",Toast.LENGTH_LONG).show()
                return false
            }
            return true
        } catch (e: java.lang.Exception) {
            error = e.message.toString()
            funcion.mensaje(context,"Error",e.message.toString())
            return false
        }
    }
    private fun validarPedido(codArticulo:String,codListaPrecio:String,um:String,precio:String):Boolean{
        val sql = " select  distinct a.REFERENCIA , a.MULT, a.DIV        , a.IND_BASICO      , " +
                " a.COD_IVA    , a.PORC_IVA           , a.COD_UNIDAD_REL  , " +
                " (CAST(b.CANT_MINIMA as integer)/CAST(a.mult as integer)) CANT_MINIMA  , " +
                " CAST((CAST(IFNULL(b.PREC_CAJA,1) AS DOUBLE)/CAST(IFNULL(b.MULT,1) AS DOUBLE)) * CAST(IFNULL(a.MULT,1) AS DOUBLE) AS DOUBLE) PRECIO " +
                " from svm_st_articulos a, svm_articulos_precios b " +
                "  where   a.COD_ARTICULO = '" + codArticulo + "' " +
                " and a.COD_ARTICULO = b.COD_ARTICULO and b.COD_VENDEDOR = '" + ListaClientes.codVendedor + "' " +
                " and b.COD_LISTA_PRECIO = '" + codListaPrecio + "' " +
                " and a.COD_UNIDAD_REL = '" + um + "' "
        val curPrecio = funcion.consultar(sql)
        val precioArt = funcion.numero(Pedidos.decimales,funcion.dato(curPrecio,"PRECIO"),false).replace(".","")
        return(precioArt == precio)
    }

    init {
        ubicacion.obtenerUbicacion(lm)
    }

}