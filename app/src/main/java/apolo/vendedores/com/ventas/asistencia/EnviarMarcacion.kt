package apolo.vendedores.com.ventas.asistencia

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.widget.EditText
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.ventas.ListaClientes

class EnviarMarcacion(private val codCliente : String, private val codSubcliente : String) {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var contexto : Context
        var cadena = ""
        var resultado = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion: EditText
        var accion = ""
        var anomalia = ""
        var dia = ""
        var stCodCliente = ""
        var stCodSubcliente = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var dialogoPro:apolo.vendedores.com.utilidades.ProgressDialog
    }

    fun enviar():Boolean{
        stCodCliente = codCliente
        stCodSubcliente = codSubcliente
        return cargarDatos(MainActivity2.funcion.consultar(sqlMarcaciones()))
    }

    private fun sqlMarcaciones() : String {
        return  ( "Select a.id          , a.COD_CLIENTE , a.COD_SUBCLIENTE  "
                + "   , a.FECHA         , a.COD_PROMOTOR, a.TIPO            "
                + "   , a.ESTADO        , a.LATITUD     , a.LONGITUD        "
                + "   , ifnull(a.OBSERVACION,'') OBSERVACION                "
                + "   , a.COD_EMPRESA "
                + "  from vt_marcacion_ubicacion a "
                + "  where a.ESTADO         = 'P' "
                + "    and a.COD_PROMOTOR   = '" + ListaClientes.codVendedor + "'"
                + "    and a.COD_CLIENTE    = '" + codCliente + "'"
                + "    and a.COD_SUBCLIENTE = '" + codSubcliente + "'"
                + "    and a.TIPO in ('E','S') "
                + "  group by a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, a.OBSERVACION, a.COD_EMPRESA  "
                + "  order by a.id desc ")
    }

    private fun detalle() : String {
        return " INTO fv_marcacion_asistencia (  cod_empresa	, cod_vendedor	, cod_cliente 	, cod_subcliente    ," +
                                                "tipo	    	, fec_asistencia , latitud    	, longitud     	    ," +
                                                "cod_persona    , comentario   )  " +
                "VALUES ("
    }

    private fun cargarDatos(cursor: Cursor):Boolean {
        cadena = "INSERT ALL"
        for (i in 0 until cursor.count) {
            val codEmpresa = cursor.getString(cursor.getColumnIndex("COD_EMPRESA"))
            val codVendedor: String = ListaClientes.codVendedor
            val codClienteC: String = cursor.getString(cursor.getColumnIndex("COD_CLIENTE"))
            val codSubclienteC: String = cursor.getString(cursor.getColumnIndex("COD_SUBCLIENTE"))
            val tipo: String = cursor.getString(cursor.getColumnIndex("TIPO"))
            val fecha: String = cursor.getString(cursor.getColumnIndex("FECHA"))
            val latitud: String = cursor.getString(cursor.getColumnIndex("LATITUD"))
            val longitud: String = cursor.getString(cursor.getColumnIndex("LONGITUD"))
            var observacion: String = cursor.getString(cursor.getColumnIndex("OBSERVACION")) + "v:${MainActivity.version}.${MainActivity.fechaVersion}" + anomalia
            if (MainActivity2.rooteado){
                observacion = "El teléfono está rooteado.\n$observacion"
            }
            cadena += "${detalle()}'$codEmpresa','$codVendedor','$codClienteC','$codSubclienteC"
            cadena += "','" + tipo + "',to_date('" + fecha + "','dd/MM/yyyy hh24:mi:ss'),'" + latitud + "','" + longitud + "','" + FuncionesUtiles.usuario["LOGIN"] + "','" + observacion + "') "
            cursor.moveToNext()
        }
        cadena += " SELECT * FROM dual "
        return if (cursor.count == 0) {
            resultado = "No existe ninguna marcacion pendiente de envio"
            false
        } else {
            dia = ""
            true
        }
    }

    fun cargarDatosDelDia():Boolean{
        cadena = " "
        val date = MainActivity2.funcion.getFechaActual()
        val sql  = ("Select a.id, a.COD_EMPRESA, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD   "
                + "  from vt_marcacion_ubicacion a "
                + " where a.FECHA like '$date%'"
                + " group by a.id, a.COD_EMPRESA, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD "
                + "  order by a.id desc ")
        val cursor: Cursor = MainActivity2.funcion.consultar(sql)
        cursor.moveToFirst()
        if(cursor.count > 0){
            val tipo =  cursor.getString(cursor.getColumnIndex("TIPO"))
            if(tipo == "E"){
                resultado = ("Debe marcar salida del cliente " +
                        "${MainActivity2.funcion.dato(cursor,"COD_CLIENTE")}-" +
                        "${MainActivity2.funcion.dato(cursor,"COD_SUBCLIENTE")}.")
//                MainActivity2.funcion.mensaje(contexto,"Atención!","Debe marcar salida del cliente " +
//                                                                       "${MainActivity2.funcion.dato(cursor,"COD_CLIENTE")}-" +
//                                                                       "${MainActivity2.funcion.dato(cursor,"COD_SUBCLIENTE")}.")
                return false
            }
        }
        val query =
            ("Select a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, "
                 + " a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, ifnull(a.OBSERVACION,'') OBSERVACION ,  "
                 + " a.COD_EMPRESA "
                 + "  from vt_marcacion_ubicacion a "
                 + "  where a.ESTADO = 'P' "
                 + "    and a.FECHA like '%$date%'"
                 + "  group by a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, a.OBSERVACION, a.COD_EMPRESA  "
                 + "  order by a.id desc ")

        val cursor1: Cursor = MainActivity2.funcion.consultar(query)
        cursor1.moveToFirst()
        for (i in 1..cursor1.count) {
            val codEmpresa = MainActivity2.funcion.dato(cursor1,"COD_EMPRESA")
            val codVendedor: String = MainActivity2.funcion.dato(cursor1,"COD_PROMOTOR")
            val codCliente: String = MainActivity2.funcion.dato(cursor1,"COD_CLIENTE")
            val codSubcliente: String = MainActivity2.funcion.dato(cursor1,"COD_SUBCLIENTE")
            val tipo: String = MainActivity2.funcion.dato(cursor1,"TIPO")
            val fecha: String = MainActivity2.funcion.dato(cursor1,"FECHA")
            val latitud: String = MainActivity2.funcion.dato(cursor1,"LATITUD")
            val longitud: String = MainActivity2.funcion.dato(cursor1,"LONGITUD")
            val observacion: String = MainActivity2.funcion.dato(cursor1,"OBSERVACION") + "\nv: ${MainActivity.version}.${MainActivity.fechaVersion}"

            cadena += "'$codEmpresa','$codVendedor','$codCliente','$codSubcliente"
            cadena += "','$tipo',to_date('$fecha','dd/MM/yyyy hh24:mi:ss'),'$latitud','$longitud','$observacion';"

            cursor1.moveToNext()
        }

        return if(cursor1.count > 0){
            dia = "HOY"
            true
        } else {
            resultado = "No existe ninguna marcacion pendiente de envio"
            false
        }
    }

    //envia todas las marcaciones del dia anterior
    fun procesaEnviaMarcaciones() {
        val funcion = FuncionesUtiles(contexto)
        var cadena = " "
        val date = MainActivity2.funcion.getFechaActual()
        val sql  = ("Select a.id, a.COD_EMPRESA, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD   "
                + "  from vt_marcacion_ubicacion a "
                + " where a.FECHA not like '%"
                + date
                + "%'"
                + " group by a.id, a.COD_EMPRESA, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD "
                + "  order by a.id desc ")
        val cursor: Cursor = funcion.consultar(sql)
        cursor.moveToFirst()
        if(cursor.count > 0){
            val tipo =  cursor.getString(cursor.getColumnIndex("TIPO"))
            if(tipo == "E"){
                val valores = ContentValues()
                val columnas: String = ( " COD_EMPRESA ,FECHA    ,COD_PROMOTOR ,COD_CLIENTE ,COD_SUBCLIENTE ,TIPO,"
                        + " ESTADO      ,LATITUD  ,LONGITUD"     )
                valores.put("COD_EMPRESA", funcion.dato(cursor,"COD_EMPRESA"))
                valores.put("FECHA", funcion.dato(cursor,"FECHA"))
                valores.put("COD_PROMOTOR", funcion.dato(cursor,"COD_PROMOTOR"))
                valores.put("COD_CLIENTE", funcion.dato(cursor,"COD_CLIENTE"))
                valores.put("COD_SUBCLIENTE", funcion.dato(cursor,"COD_SUBCLIENTE"))
                valores.put("TIPO", "S")
                valores.put("ESTADO", "P")
                valores.put("LATITUD", "")
                valores.put("LONGITUD", "")
                funcion.insertar("vt_marcacion_ubicacion", columnas, valores)
            }
        }

        val query =
            ("Select a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, "
                 + " a.ESTADO, a.LATITUD, a.LONGITUD, ifnull(a.OBSERVACION,'') OBSERVACION , "
                 + " a.COD_EMPRESA "
                 + "  from vt_marcacion_ubicacion a "
                 + "  where a.ESTADO = 'P' "
                 + "    and a.FECHA not like '%" + date + "%'"
                 + "  group by a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, a.OBSERVACION,a.COD_EMPRESA "
                 + "  order by a.id desc ")

        val cursor1: Cursor = funcion.consultar(query)
        cursor1.moveToFirst()
        for (i in 1..cursor1.count) {
            val codEmpresa = funcion.dato(cursor1,"COD_EMPRESA")
            val codVendedor: String = funcion.dato(cursor1,"COD_PROMOTOR")
            val codCliente: String = funcion.dato(cursor1,"COD_CLIENTE")
            val codSubcliente: String = funcion.dato(cursor1,"COD_SUBCLIENTE")
            val tipo: String = funcion.dato(cursor1,"TIPO")
            val fecha: String = funcion.dato(cursor1,"FECHA")
            val latitud: String = funcion.dato(cursor1,"LATITUD")
            val longitud: String = funcion.dato(cursor1,"LONGITUD")
            val observacion: String = funcion.dato(cursor1,"OBSERVACION") + "\nv: ${MainActivity.version}.${MainActivity.fechaVersion}"

            cadena += "'$codEmpresa','$codVendedor','$codCliente','$codSubcliente"
            cadena += "','$tipo',to_date('$fecha','dd/MM/yyyy hh24:mi:ss'),'$latitud','$longitud','$observacion';"

            cursor1.moveToNext()
        }

        if(cursor1.count > 0){
            val mensaje: Array<String> = MainActivity2.conexionWS.procesaMarcacionAsistenciaAct (
                FuncionesUtiles.usuario["LOGIN"].toString() , cadena, FuncionesUtiles.usuario["COD_EMPRESA"].toString() ).split("*").toTypedArray()
            if (mensaje.size != 1) {
                if (mensaje[0] == "01") {
                    val update = " UPDATE vt_marcacion_ubicacion SET ESTADO = 'E' " +
                            "  WHERE ESTADO = 'P'" +
                            "    AND FECHA not like '%" + date + "%'"
                    funcion.ejecutar(update, contexto)
                }
            }
        }
    }

}