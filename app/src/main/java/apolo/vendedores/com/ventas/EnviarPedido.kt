@file:Suppress("DEPRECATION")

package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesDispositivo
import apolo.vendedores.com.utilidades.FuncionesUbicacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class EnviarPedido(
    var context: Context,
    private var lm: LocationManager,
    private var telMgr: TelephonyManager,
    private var cabeceraHash: HashMap<String, String>
) {

    companion object{
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
    

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun enviarPedido() {
        if (!dispositivo.modoAvion()) {
            return
        }
        if (!dispositivo.zonaHoraria()) {
            return
        }
//        if (!dispositivo.fechaCorrecta()) {
////            return
//        }
        if (!dispositivo.tarjetaSim(telMgr)) {
            return
        }
		if (!ubicacion.validaUbicacionSimulada(lm)) {
			return
		}
        if (Pedidos.etTotalPedidos.toString() == "" || Pedidos.etTotalPedidos.toString() == "0") {
            return
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
            vIntoCab = ("'" + "1" + "'" // cod_empresa
                    + ",'" + ListaClientes.codSucursalCliente
                    + "'" // cod_sucursal
                    + ",to_date('" + Pedidos.etFechaPedido.text
                    + "','dd/MM/yyyy')" // fec_comprobante
                    + "," + "'" + "PRO" + "'" // tip_comprobante
                    + ",'" + ListaClientes.codVendedor + "'" // ser_comprobante
                    // +","+ et_tot_pedido.getText().toString().replace(".", "")
                    + ",'" + ListaClientes.codVendedor + "'" // cod_Vendedor
                    + ",'" + ListaClientes.codCliente + "'" // cod_Cliente
                    + ",'" + ListaClientes.codSubcliente + "'" // cod_Subcliente
                    + ",'" + cabeceraHash["COD_CONDICION_VENTA"] + "'" // cod_Condicion_Venta
                    + ",'" + cabeceraHash["COD_LISTA_PRECIO"] + "'" // cod_Lista_Precio
                    + ",'" + cabeceraHash["COD_MONEDA"] + "'" // cod_Moneda
                    + ",'" + "P" + "'" // estado
                    + ",'" + "PRO" + "'" // tip_Comprobante_Ref
                    + ",'" + ListaClientes.codVendedor + "'" // ser_Comprobante_Ref
                    + ",'" + Pedidos.etNroPedidos.text.toString() + "'" // nro_Comprobante_ref
//                    					+ ",'" + Aplicacion._lati + "'" // latitud
//                    					+ ",'" + Aplicacion._longi + "'" // longitud
                    + ",'" + ubicacion.latitud  + "'" // latitud
                    + ",'" + ubicacion.longitud + "'" // longitud
//                    + ",''" // latitud
//                    + ",''" // longitud
                    + ",'" + Pedidos.etNroOrdenCompra.text.toString() + "'" // nro_Orden_Compra
                    + ",'" + Pedidos.depo + "'" // ind_Deposito
                    + ",'" + "S" + "'" // ind_Sistema
                    + ",'" + descVarios.replace(",",".") + "'" // porc_Desc_Var
                    + ",'" + descFin.replace(",",".") + "'") // descuento_fin
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
                    + ",to_date('" + funcion.getFechaActual() + " " + funcion.getHoraActual() + "','dd/MM/yyyy hh24:mi:ss')" // "fecha y hora de alta
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
                vIntoDet = ("'1'," //cod_empresa
                        + "'" + ListaClientes.codSucursalCliente + "'," //cod_sucursal
                        + "'PRO'," //tip_comprobante
                        + "'" + ListaClientes.codVendedor + "'," //ser_comprobante
                        + "'" + cont.toString() + "'," //orden
                        + "'" + funcion.dato(cursor,"COD_ARTICULO") + "'," //cod_articulo
                        + funcion.dato(cursor,"CANTIDAD") + "," //cantidad
                        + "'" + funcion.dato(cursor,"COD_UNIDAD_MEDIDA") + "'," //cod_unidad_medida
                        + "'" + funcion.dato(cursor,"COD_IVA") + "'," //cod_iva
                        + funcion.dato(cursor,"PORC_IVA")) //porc_iva
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
                cursor.moveToNext()
            }
            try {
                val t: Double = Pedidos.etTotalPedidos.text.toString().replace(".", "").replace(",", ".").toDouble()
                if (t < funcion.minVenta(ListaClientes.codVendedor) && !cabeceraHash["COD_CONDICION"].equals("99") && Pedidos.spListaPrecios.getDato("DECIMALES") == "0") {
                    val nf = NumberFormat.getInstance()
                    nf.minimumFractionDigits = 0
                    nf.maximumFractionDigits = 0
                    Toast.makeText(context,"El monto minimo para una venta es: " + nf.format(funcion.minVenta(ListaClientes.codVendedor)),Toast.LENGTH_LONG).show()
                    return
                }
            } catch (e: java.lang.Exception) {
            }
            Enviar().execute()
        } catch (e: java.lang.Exception) {
            error = e.message.toString()
            funcion.mensaje(context,"Error",e.message.toString())
        }
    }

    @Suppress("DEPRECATION")
    private class Enviar : AsyncTask<Void?, Void?, Void?>() {
        private var pbarDialog: ProgressDialog? = null
        override fun onPreExecute() {
            try {
                pbarDialog!!.dismiss()
            } catch (e: java.lang.Exception) {
            }
            pbarDialog = ProgressDialog.show(contexto,"Un momento...", "Enviando el pedido al servidor...", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            resultado = MainActivity.conexionWS.enviarPedido(cabecera, detalles,Pedidos.maximo.toString(),ListaClientes.codVendedor).toString()
//            resultado = "01*Enviado con exito"
            return null
        }

        @SuppressLint("SetTextI18n", "Recycle", "SimpleDateFormat")
        override fun onPostExecute(unused: Void?) {
            var ult = 0
            var cantidad: String
            var codigo = ""
            pbarDialog!!.dismiss()
            if (resultado.indexOf("03*") >= 0) {
                resultado = resultado.replace("03*", "")

                // Limpiar la existencia de todos los productos del detalle
                var values = ContentValues()
                values.put("existencia_actual", "")
                try {
                    MainActivity.bd!!.update("vt_pedidos_det", values,
                        " NUMERO = '" + Pedidos.maximo
                            .toString() + "' and COD_VENDEDOR = '" + ListaClientes.codVendedor + "'", null)
                } catch (e: java.lang.Exception) {
                }

                // Muestra la existencia de los productos con corte
                while (resultado.indexOf(";") > 0) {
                    codigo = resultado.substring(ult, resultado.indexOf("/"))
                    ult = resultado.indexOf(";")
                    cantidad = resultado.substring(resultado.indexOf("/") + 1, ult)
                    resultado = resultado.replaceFirst(
                        "$codigo/$cantidad;",
                        ""
                    )
                    ult = 0
                    values = ContentValues()
                    values.put("existencia_actual", cantidad)
                    try {
                        MainActivity.bd!!.update("vt_pedidos_det", values,
                            (" NUMERO = '"
                                    + Pedidos.maximo
                                    ) + "'" + " and cod_articulo = '" + codigo + "'" + " and COD_VENDEDOR = '" + ListaClientes.codVendedor + "'", null)
                        codigo = ""
                    } catch (e: java.lang.Exception) {
                        resultado = "2"
                        e.printStackTrace()
                    }
                }
                if (codigo != "") {
                    codigo = resultado.substring(ult, resultado.indexOf("/"))
                    cantidad = resultado.substring(
                        resultado.indexOf("/") + 1,
                        resultado.length
                    )
                    values = ContentValues()
                    values.put("existencia_actual", cantidad)
                    try {
                        MainActivity.bd!!.update("vt_pedidos_det",values,
                            ("NUMERO = '" + Pedidos.maximo ) +
                                    "'" + " and cod_articulo = '" + codigo + "'" +
                                    " and COD_VENDEDOR = '" + ListaClientes.codVendedor + "'", null
                        )
                    } catch (e: java.lang.Exception) {
                        resultado = "2"
                        e.printStackTrace()
                    }
                }
                resultado =  "Corte de Stock!! favor verificar los productos sin stock  y vuelva a intentarlo."
                Pedidos.etAccionPedidos.setText("cargarDetallePedido")
            }
            if (resultado.indexOf("01*") >= 0) {
                val values = ContentValues()
                values.put("FECHA", Pedidos.etFechaPedido.text.toString())
                values.put("FECHA_INT", Pedidos.fechaInt)
                values.put("NRO_ORDEN_COMPRA",Pedidos.etNroOrdenCompra.text.toString())
                values.put("ESTADO", "E")
                values.put("COMENTARIO", Pedidos.etObservacionPedido.text.toString())
                values.put("porc_desc_fin", descFin.replace(",","."))
                values.put("porc_desc_var", descVarios.replace(",","."))
                values.put("NRO_AUTORIZACION_DESC", Pedidos.claveAutorizacion)
                values.put("tot_descuento", Pedidos.etTotalDescPedidos.text.toString().replace(".", ""))
                values.put("TOT_COMPROBANTE", Pedidos.etTotalPedidos.text.toString().replace(".", ""))
                val d2: String?
                val cal2: Calendar = Calendar.getInstance()
                val dfDate2 = SimpleDateFormat("dd/MM/yyyy")
                d2 = dfDate2.format(cal2.time)
                values.put("FEC_ALTA", d2)
                try {
                    MainActivity.bd!!.update("vt_pedidos_cab", values, "NUMERO = '" + Pedidos.maximo
                            .toString() + "' and COD_VENDEDOR = '" + ListaClientes.codVendedor + "'", null )
                } catch (e: java.lang.Exception) {
                    resultado = "Error al grabar! Intente otra vez!!"
                    e.printStackTrace()
                }
                val sqlUpdate: String
                var porDescuento = 0.toFloat()
                if (descFin != "") {
                    porDescuento = descFin.replace(",",".").toFloat()
                }
                if (descVarios != "") {
                    porDescuento = (porDescuento + descVarios.replace(",",".").toFloat())
                }
                sqlUpdate = if (porDescuento == 0f) {
                    ("update vt_pedidos_det  set "
                            + " monto_total = (precio_unitario * cantidad) "
                            + " where NUMERO = '"
                            + Pedidos.maximo + "'"
                            + " and COD_VENDEDOR = '" + ListaClientes.codVendedor + "'")
                } else {
                    ("update vt_pedidos_det  set "
                            + "monto_total = (precio_unitario*cantidad) -"
                            + "((precio_unitario * cantidad) * "
                            + porDescuento / 100
                            + ") where NUMERO = '"
                            + Pedidos.maximo + "'"
                            + " and COD_VENDEDOR = '" + ListaClientes.codVendedor + "'")
                }
                try {
                    MainActivity.bd!!.rawQuery(sqlUpdate, null)
                } catch (e: java.lang.Exception) {
                    resultado = e.message.toString()
                    e.printStackTrace()
                }
                resultado = "Pedido enviado con exito!!"
            }
            if (resultado != "Pedido enviado con exito!!"){
                MainActivity.funcion.mensaje(contexto,"", resultado)
            } else {
                val dialogo = DialogoAutorizacion(contexto)
                dialogo.dialogoAccion("cerrarTodo",Pedidos.etAccionPedidos, resultado,"","OK")
            }
        }

    }

    init {
        ubicacion.obtenerUbicacion(lm)
    }


}