package apolo.vendedores.com.ventas.sd

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.widget.Toast
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.ventas.ListaClientes

class EnviarSD {

    companion object{
        lateinit var context : Context
        var respuesta:String = ""
        var cadena : String = ""
        var cadena2 : String = ""
        var codCliente : String = ""
        var codSubcliente : String = ""
        var funcion : FuncionesUtiles = FuncionesUtiles()
    }
    
    private var registros : Int = 0

    fun enviar() {
        registrarCabecera()
        cargarCabecera(funcion.consultar(sqlCabecera()))
        cargarDetalle(funcion.consultar(sqlDetalle()))

        if (registros == 0) {
            Toast.makeText(context,"No existe nunguna solicitud pendiente de envio",Toast.LENGTH_SHORT).show()
        } else {
            Enviar().execute()
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
                + "    AND EST_ENVIO	  = 'N' "
                + "  GROUP BY COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,"
                + "		   COD_CLIENTE,COD_SUBCLIENTE,FECHA ")
        insertarCabecera(funcion.consultar(sql))
    }

    private fun insertarCabecera(cursor: Cursor){
        for (i in 0 until cursor.count) {
            try {
                val valores = ContentValues()
                valores.put("COD_EMPRESA", funcion.dato(cursor,"COD_EMPRESA"))
                valores.put("NRO_PLANILLA",funcion.dato(cursor,"NRO_PLANILLA"))
                valores.put("COD_VENDEDOR", ListaClientes.codVendedor)
                valores.put("NRO_REGISTRO_REF", "0")
                valores.put("COD_CLIENTE", funcion.dato(cursor,"COD_CLIENTE"))
                valores.put("COD_SUBCLIENTE",funcion.dato(cursor,"COD_SUBCLIENTE"))
                valores.put("EST_ENVIO", "N")
                valores.put("FECHA", funcion.dato(cursor,"FECHA"))

                val campos: String  = ("COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,NRO_REGISTRO_REF,COD_CLIENTE,COD_SUBCLIENTE,EST_ENVIO,FECHA")
                MainActivity.bd!!.beginTransaction()
                MainActivity.bd!!.insert("svm_solicitud_dev_cab",campos,valores)

                var xid = "0"
                val cursor2 = funcion.consultar("SELECT max(id) xid from svm_solicitud_dev_cab")
                if (cursor2.count > 0) {
                    xid = funcion.dato(cursor2,"xid")
                }
                val sql = ("update svm_solicitud_dev_det set "
                        + " GRABADO_CAB 		 = 'S', "
                        + " NRO_REGISTRO_REF 	 = '$xid' "
                        + " where COD_CLIENTE    = '$codCliente'"
                        + " and COD_SUBCLIENTE 	 = '$codSubcliente'"
                        + " and NRO_PLANILLA   	 = '${ListaClientes.codVendedor}'"
                        + " and NRO_REGISTRO_REF ='0' ")
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
        return ("SELECT COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,COD_CLIENTE,COD_SUBCLIENTE,id "
                +  "  FROM svm_solicitud_dev_cab "
                +  " WHERE COD_CLIENTE  	= '$codCliente' "
                +  "   AND COD_SUBCLIENTE 	= '$codSubcliente' "
                +  "   AND NRO_PLANILLA 	= '${ListaClientes.codVendedor}' "
                +  "   AND EST_ENVIO		= 'N' ")
    }

    private fun sqlDetalle():String{
        return ("SELECT COD_EMPRESA      , NRO_PLANILLA   , COD_VENDEDOR , NRO_REGISTRO_REF, "
                       + "        COD_CLIENTE      , COD_SUBCLIENTE , COD_ARTICULO , "
                       + "        COD_UNIDAD_REL   , CANTIDAD       , COD_PENALIDAD, PAGO "
                       + "   FROM svm_solicitud_dev_det "
                       + "  WHERE COD_CLIENTE  	 = '$codCliente' "
                       + "    AND COD_SUBCLIENTE = '$codSubcliente' "
                       + "    AND NRO_PLANILLA 	 = '${ListaClientes.codVendedor}' "
                       + "    AND EST_ENVIO		 = 'N' ")
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
            val codEmpresa = "1"
            val nroRegistroRef: String = funcion.dato(cursor,"id")
            val codVendedor: String = ListaClientes.codVendedor

            cadena += "$cabecera '$codEmpresa','${codVendedor}','1','$codCliente"
            cadena += "','$codSubcliente','$nroRegistroRef','0','0','${FuncionesUtiles.usuario["COD_PERSONA"]}','S') "
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
                + "supervisor_persona	) "
                + " VALUES(")
        for (i in 0 until cursor.count) {
            //aca
            val codEmpresa = "1"
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
            cadena2 += "','" + ListaClientes.codVendedor + "','" + FuncionesUtiles.usuario["COD_PERSONA"] + "')"
            cursor.moveToNext()
        }

        cadena2 += " SELECT * FROM DUAL "
        registros += cursor.count

    }
    
    private class Enviar : AsyncTask<Void?, Void?, Void?>() {
        var dialogo : ProgressDialog? = null
        override fun onPreExecute() {
            dialogo = ProgressDialog.show(context,"Un momento...","Comprobando conexion",true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                respuesta = MainActivity.conexionWS.procesaEnviaSolicitudSD(ListaClientes.codVendedor, cadena, cadena2)
//                respuesta = "01*Enviado con exito"
                null
            } catch (e: Exception) {
                respuesta = e.message.toString()
                null
            }
        }

        override fun onPostExecute(unused: Void?) {
            try {
                dialogo!!.dismiss()
            } catch (e: Exception) {
//					String aei;
//					aei=e.getMessage();
            }
            if (respuesta.split("*").size != 1) {    //==> Si cant de caracteres de "mensaje" no es = 1 o Si retorna mensaje
                if (respuesta.split("*")[0] == "01") {
                    var sql : String = ("UPDATE svm_solicitud_dev_det set EST_ENVIO = 'S' "
                            +  " WHERE COD_CLIENTE  	= '$codCliente' "
                            +  "   AND COD_SUBCLIENTE 	= '$codSubcliente' "
                            +  "   AND NRO_PLANILLA 	= '${ListaClientes.codVendedor}' "
                            +  "   AND EST_ENVIO 		= 'N' ")
                    funcion.ejecutar(sql, context)

                    sql = ("UPDATE svm_solicitud_dev_cab set EST_ENVIO = 'S' "
                            +  " WHERE COD_CLIENTE  	= '$codCliente' "
                            +  "   AND COD_SUBCLIENTE 	= '$codSubcliente' "
                            +  "   AND NRO_PLANILLA 	= '${ListaClientes.codVendedor}' "
                            +  "   AND EST_ENVIO 		= 'N' ")
                    funcion.ejecutar(sql, context)
//                    funcion.mensaje(context,"Operación existosa!", respuesta)
                    val dialogo = DialogoAutorizacion(context)
                    dialogo.dialogoAccion("ACTUALIZAR", SolicitudDevolucion.etAccion, respuesta,"Operación existosa!","ok")
                } else {
                    funcion.mensaje(context,"Atención", "No se ha podido enviar la información\n$respuesta")
                }
            } else {
                funcion.mensaje(context,"Error", respuesta)
            }
            return
        }

    }

}