package apolo.vendedores.com.ventas.inventario_vencimiento

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.FuncionesUtiles


class EnviarInventarioVencimiento {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        var sinc : Boolean = false
        var respuesta:String = ""
        var cadena2 : String = ""
        var codCliente : String = ""
        var codSubcliente : String = ""
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
    }

    private var registros : Int = 0

    fun enviarPendientes() : Boolean {
        cargarDetalle(funcion.consultar(sqlDetallePendientes()))
        return if (registros == 0) {
//            Toast.makeText(context,"No hay registros pendientes de envio",Toast.LENGTH_SHORT).show()
            false
        } else {
            procesaEnviarPendientes()
//            Enviar().execute()
            true
        }
    }

    fun enviar() : Boolean {
        sinc = false
        cargarDetalle(funcion.consultar(sqlDetalle()))
        return if (registros == 0) {
            Toast.makeText(context,"No hay registros pendientes de envio",Toast.LENGTH_SHORT).show()
            false
        } else {
//            Enviar().execute()
            true
        }
    }

    private fun sqlDetallePendientes(): String {
        return ("SELECT COD_EMPRESA      , FEC_INVENTARIO   , COD_CLIENTE , COD_SUBCLIENTE, "
                + "        COD_ARTICULO     , FEC_VENCIMIENTO  , COD_UNID_MED , "
                + "        CANT_DEP         , CANT_GOND "
                + "   FROM svm_inventario_art_cliente "
                + "  WHERE ESTADO		     = 'P' ")
    }

    private fun sqlDetalle(): String {
        return ("SELECT COD_EMPRESA      , FEC_INVENTARIO   , COD_CLIENTE , COD_SUBCLIENTE, "
                + "        COD_ARTICULO     , FEC_VENCIMIENTO  , COD_UNID_MED , "
                + "        CANT_DEP         , CANT_GOND "
                + "   FROM svm_inventario_art_cliente "
                + "  WHERE COD_CLIENTE  	 = '$codCliente'    "
                + "    AND COD_SUBCLIENTE    = '$codSubcliente' "
                + "    AND ESTADO		     = 'P' ")
    }

    private fun cargarDetalle(cursor: Cursor){
        cadena2 = "INSERT ALL"
        val detalle = (" INTO st_inventario_art_cliente ("
                                                        + "cod_empresa	    ,"
                                                        + "cod_vendedor	    ,"
                                                        + "fec_inventario	,"
                                                        + "cod_cliente		,"
                                                        + "cod_subcliente	,"
                                                        + "cod_articulo		,"
                                                        + "fec_vencimiento	,"
                                                        + "cod_unidad_med	,"
                                                        + "cant_dep		    ,"
                                                        + "cant_gond	    ,"
                                                        + "fec_alta	        )"
                                                        + " VALUES(")
        for (i in 0 until cursor.count) {
            val codEmpresa     : String = FuncionesUtiles.usuario["COD_EMPRESA"].toString()
            val codVendedor    : String = FuncionesUtiles.usuario["LOGIN"].toString()
            val fecInventario  : String = funcion.dato(cursor,"FEC_INVENTARIO")
            val codCliente     : String = funcion.dato(cursor,"COD_CLIENTE")
            val codSubcliente  : String = funcion.dato(cursor,"COD_SUBCLIENTE")
            val codArticulo    : String = funcion.dato(cursor,"COD_ARTICULO")
            val fecVencimiento : String = funcion.dato(cursor,"FEC_VENCIMIENTO")
            val codUnidMed     : String = funcion.dato(cursor,"COD_UNID_MED")
            val catDep         : String = funcion.dato(cursor,"CANT_DEP")
            val catGond        : String = funcion.dato(cursor,"CANT_GOND")
            val fecAlta                 = "sysdate"
            cadena2 += "$detalle'$codEmpresa','$codVendedor',to_date('$fecInventario','dd/MM/yyyy'),'$codCliente"
            cadena2 += "','$codSubcliente','$codArticulo',to_date('$fecVencimiento','dd/MM/yyyy')"
            cadena2 += ",'$codUnidMed','$catDep','$catGond',$fecAlta)"
            cursor.moveToNext()
        }
        cadena2 += " SELECT * FROM DUAL "
        registros += cursor.count
    }

    private fun procesaEnviarPendientes(){
        try {
            respuesta = MainActivity.conexionWS.procesaEnviaInventarioVencimiento(
                FuncionesUtiles.usuario["LOGIN"].toString().trim(), cadena2)

            if (respuesta.split("*").size != 1) {    //==> Si cant de caracteres de "mensaje" no es = 1 o Si retorna mensaje
                if (respuesta.split("*")[0] == "01") {
                    val sql = ("UPDATE svm_inventario_art_cliente set ESTADO = 'E' WHERE ESTADO = 'P' ")
                    MainActivity.bd!!.execSQL(sql)
                    MainActivity.bd!!.execSQL(sql)
                    MainActivity.bd!!.execSQL(sql)
                }
            }
        } catch (e:java.lang.Exception){
            e.message
        }
    }

}