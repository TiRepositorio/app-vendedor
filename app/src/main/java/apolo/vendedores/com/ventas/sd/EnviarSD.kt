package apolo.vendedores.com.ventas.sd

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.ventas.ListaClientes

class EnviarSD {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        var respuesta:String = ""
        var cadena : String = ""
        var cadena2 : String = ""
        var codCliente : String = ""
        var codSubcliente : String = ""
        var codEmpresa : String = ""
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
    }

    private var nroRegistroRef = 0
    private var registros : Int = 0

    fun enviar():Boolean {
        nroRegistroRef = nroRegistroRef()
        registrarCabecera()
        cargarCabecera(funcion.consultar(sqlCabecera()))
        cargarDetalle(funcion.consultar(sqlDetalle()))

        return if (registros == 0) {
            Toast.makeText(context,"No existe nunguna solicitud pendiente de envio",Toast.LENGTH_SHORT).show()
            false
        } else {
//            Enviar().execute()
            true
        }
    }

    private fun registrarCabecera(){
        val sql : String = ("SELECT COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,"
                + "       COD_CLIENTE,COD_SUBCLIENTE,FECHA "
                + "  FROM svm_solicitud_dev_det "
                + "  WHERE GRABADO_CAB = 'N'  "
                + "    AND COD_CLIENTE    = '" + codCliente + "' "
                + "    AND COD_SUBCLIENTE = '" + codSubcliente + "' "
                + "    AND NRO_PLANILLA   = '" + ListaClientes.codVendedor + "' "
                + "    AND FECHA    	  = '${funcion.getFechaActual()}' "
                + "    AND EST_ENVIO	  = 'N' "
                + "    AND COD_EMPRESA	  = '$codEmpresa' "
                + "  GROUP BY COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,"
                + "		   COD_CLIENTE,COD_SUBCLIENTE,FECHA ")
        insertarCabecera(funcion.consultar(sql))
    }

    private fun nroRegistroRef():Int{
        var sql = "select VERSION " +
                "    from svm_vendedor_pedido " +
                "   where COD_EMPRESA = '$codEmpresa' " +
                "     AND COD_VENDEDOR = '${ListaClientes.codVendedor}'"
        var cursor = funcion.consultar(sql)
        var servidor = 0
        if (cursor.count > 0){
            servidor = funcion.datoEntero(cursor,"VERSION")
        }

        sql = "select max(NRO_REGISTRO_REF) NRO_REGISTRO_REF from svm_solicitud_dev_cab " +
                "   where COD_EMPRESA = '$codEmpresa' " +
                "     AND COD_VENDEDOR = '${ListaClientes.codVendedor}'"
        cursor = funcion.consultar(sql)
        var telefono = 0
        if (cursor.count > 0){
            telefono = funcion.datoEntero(cursor,"NRO_REGISTRO_REF")
        }

        return if (telefono > servidor){
            telefono + 1
        } else {
            servidor + 1
        }
    }

    private fun insertarCabecera(cursor: Cursor){
//        val registroRef = nroRegistroRef()
        for (i in 0 until cursor.count) {
            try {
                val valores = ContentValues()
                valores.put("COD_EMPRESA", funcion.dato(cursor,"COD_EMPRESA"))
                valores.put("NRO_PLANILLA",funcion.dato(cursor,"NRO_PLANILLA"))
                valores.put("COD_VENDEDOR", ListaClientes.codVendedor)
//                valores.put("NRO_REGISTRO_REF", "0")
                valores.put("NRO_REGISTRO_REF", "${nroRegistroRef()}")
                valores.put("COD_CLIENTE", funcion.dato(cursor,"COD_CLIENTE"))
                valores.put("COD_SUBCLIENTE",funcion.dato(cursor,"COD_SUBCLIENTE"))
                valores.put("EST_ENVIO", "N")
                valores.put("FECHA", funcion.dato(cursor,"FECHA"))

                val campos: String  = ("COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,NRO_REGISTRO_REF,COD_CLIENTE,COD_SUBCLIENTE,EST_ENVIO,FECHA")
                MainActivity.bd!!.beginTransaction()
                MainActivity.bd!!.insert("svm_solicitud_dev_cab",campos,valores)

                val sql = ("update svm_solicitud_dev_det "
                        + "    set GRABADO_CAB 		 = 'S', "
                        + "        NRO_REGISTRO_REF  = '${nroRegistroRef}' "
                        + "  where COD_CLIENTE       = '$codCliente'"
                        + "    and COD_SUBCLIENTE 	 = '$codSubcliente'"
                        + "    and COD_EMPRESA  	 = '$codEmpresa'"
                        + "    and NRO_PLANILLA   	 = '${ListaClientes.codVendedor}'"
                        + "    and FECHA ='${funcion.getFechaActual()}' "
                        + "    and NRO_REGISTRO_REF ='0' ")
                funcion.ejecutar(sql, context)
                MainActivity.bd!!.setTransactionSuccessful()
                MainActivity.bd!!.endTransaction()
            } catch (e: java.lang.Exception) {
                funcion.mensaje(context,"Error en tabla de configuración","Error al insertar la configuración !  " + e.message)
            }
            cursor.moveToNext()
        }

    }

    private fun sqlCabecera():String {
        return ("SELECT COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,COD_CLIENTE,COD_SUBCLIENTE,NRO_REGISTRO_REF,id "
                +  "  FROM svm_solicitud_dev_cab "
                +  " WHERE COD_CLIENTE  	= '$codCliente' "
                +  "   AND COD_SUBCLIENTE 	= '$codSubcliente' "
                +  "   AND NRO_PLANILLA 	= '${ListaClientes.codVendedor}' "
                +  "   AND FECHA         	= '${funcion.getFechaActual()}' "
                +  "   AND NRO_REGISTRO_REF = '$nroRegistroRef' "
                +  "   AND COD_EMPRESA      = '$codEmpresa' "
                +  "   AND EST_ENVIO		= 'N' ")
    }

    private fun sqlDetalle():String{
        return ("  SELECT COD_EMPRESA      , NRO_PLANILLA   , COD_VENDEDOR , NRO_REGISTRO_REF, "
               + "        COD_CLIENTE      , COD_SUBCLIENTE , COD_ARTICULO , "
               + "        COD_UNIDAD_REL   , CANTIDAD       , COD_PENALIDAD, PAGO "
               + "   FROM svm_solicitud_dev_det "
               + "  WHERE COD_CLIENTE  	   = '$codCliente' "
               + "    AND COD_SUBCLIENTE   = '$codSubcliente' "
               + "    AND NRO_PLANILLA 	   = '${ListaClientes.codVendedor}' "
               + "    AND FECHA 		   = '${funcion.getFechaActual()}' "
               + "    AND EST_ENVIO		   = 'N' "
               +  "   AND COD_EMPRESA      = '$codEmpresa' "
               + "    AND NRO_REGISTRO_REF = '$nroRegistroRef' ")
    }

    private fun cargarCabecera(cursor: Cursor){

        cadena = "INSERT ALL"
        val cabecera = (" INTO vt_solicitud_cab_prov ("
                + "cod_empresa		,"
                + "nro_planilla		,"
                + "cod_repartidor	,"
                + "cod_cliente		,"
                + "cod_subcliente	,"
                + "nro_registro_ref	,"
                + "ncr_sol			,"
                + "tot_comprobante	,"
                + "vendedor_persona	,"
                + "ind_solicitud )"
                + " VALUES(")

        for (i : Int in 0 until cursor.count) {
            val codEmpresa = codEmpresa
            val nroRegistroRef: String = funcion.dato(cursor,"NRO_REGISTRO_REF")
            val codVendedor: String = ListaClientes.codVendedor

            cadena += "$cabecera '$codEmpresa','${codVendedor}','1','$codCliente"
            cadena += "','$codSubcliente','$nroRegistroRef','0','0','${FuncionesUtiles.usuario["COD_PERSONA"]}','V') "
            cursor.moveToNext()
        }

        cadena += " SELECT * FROM DUAL "
        registros = cursor.count
    }

    private fun cargarDetalle(cursor: Cursor){
        cadena2 = "INSERT ALL"
        val detalle = (" INTO vt_solicitud_det_prov ("
                + "cod_empresa			,"
                + "nro_planilla			,"
                + "cod_repartidor		,"
                + "nro_registro_ref		,"
                + "cod_cliente			,"
                + "cod_subcliente		,"
                + "cod_articulo			,"
                + "cod_unidad_medida	,"
                + "cantidad				,"
                + "cod_motivo			,"
                + "precio_unitario_c_iva,"
                + "cod_vendedor			,"
                + "cod_supervisor		,"
                + "supervisor_persona	,"
                + "nro_registro) "
                + " VALUES( ")
        for (i in 0 until cursor.count) {
            //aca
            val codEmpresa = codEmpresa //FuncionesUtiles.usuario[COD_EMPRESA].toString()
            val nroPlanilla : String = ListaClientes.codVendedor
            val codRepartidor = "1"
            val nroRegistroRef : String = funcion.dato(cursor,"NRO_REGISTRO_REF")
            val codArticulo : String = funcion.dato(cursor,"COD_ARTICULO")
            val codUnidadMedida : String = funcion.dato(cursor,"COD_UNIDAD_REL")
            val cantidad = funcion.dato(cursor,"CANTIDAD").trim()
            val codMotivo =
                if (funcion.dato(cursor,"COD_PENALIDAD").trim() == "") {
                    "01"
                } else {
                    funcion.dato(cursor,"COD_PENALIDAD").trim()
                }
            //			monto			= cursor2.getString(cursor2.getColumnIndex("PAGO")).replace(",", "").replace(".", "").trim();
            val codVendedor = ListaClientes.codVendedor
            cadena2 += "$detalle'$codEmpresa','$nroPlanilla','$codRepartidor"
            cadena2 += "','$nroRegistroRef','$codCliente','$codSubcliente"
            cadena2 += "','$codArticulo','$codUnidadMedida','$cantidad"
            cadena2 += "','$codMotivo', 0 ,'$codVendedor"
            cadena2 += "','" + ListaClientes.codVendedor + "','" + FuncionesUtiles.usuario["COD_PERSONA"]
            cadena2 += ("',( select nvl(max(nro_registro), 1) " +
                    "  from vt_solicitud_cab_prov" +
                    " where trim(cod_cliente) = '$codCliente'" +
                    "   and trim(cod_subcliente) = '$codSubcliente'" +
                    "   and trim(cod_empresa) = '$codEmpresa'" +
                    "   and trim(cod_repartidor) = '$codRepartidor'" +
                    "   and trim(nro_planilla) = '$nroPlanilla'" +
                    "   and trim(vendedor_persona) = '${FuncionesUtiles.usuario["COD_PERSONA"]}') )")
            cursor.moveToNext()
        }

        cadena2 += " SELECT * FROM DUAL "
        registros += cursor.count

    }
    
}