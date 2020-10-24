package apolo.vendedores.com.ventas.asistencia

import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.widget.EditText
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.ventas.ListaClientes

class EnviarMarcacion(private val codCliente : String, private val codSubcliente : String) {

    companion object{
        lateinit var contexto : Context
        var cadena = ""
        var resultado = ""
        private lateinit var dialogo : ProgressDialog
        lateinit var etAccion: EditText
        var accion = ""
    }

    fun enviar(){
        cargarDatos(MainActivity2.funcion.consultar(sqlMarcaciones()))
    }

    private fun sqlMarcaciones() : String {
        return ("Select a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, ifnull(a.OBSERVACION,'') OBSERVACION   "
                + "  from vt_marcacion_ubicacion a "
                + "  where a.ESTADO         = 'P' "
                + "    and a.COD_PROMOTOR   = '" + ListaClientes.codVendedor + "'"
                + "    and a.COD_CLIENTE    = '" + codCliente + "'"
                + "    and a.COD_SUBCLIENTE = '" + codSubcliente + "'"
                + "    and a.TIPO in ('E','S') "
                + "  group by a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, a.OBSERVACION  "
                + "  order by a.id desc ")
    }

    private fun detalle() : String {
        return " INTO fv_marcacion_asistencia (  cod_empresa	, cod_vendedor	, cod_cliente 	, cod_subcliente    ," +
                                                "tipo	    	, fec_asistencia , latitud    	, longitud     	    ," +
                                                "cod_persona    , comentario   )  " +
                "VALUES ("
    }

    private fun cargarDatos(cursor: Cursor) {
        cadena = "INSERT ALL"
        for (i in 0 until cursor.count) {
            val codEmpresa = "1"
            val codVendedor: String = ListaClientes.codVendedor
            val codClienteC: String = cursor.getString(cursor.getColumnIndex("COD_CLIENTE"))
            val codSubclienteC: String = cursor.getString(cursor.getColumnIndex("COD_SUBCLIENTE"))
            val tipo: String = cursor.getString(cursor.getColumnIndex("TIPO"))
            val fecha: String = cursor.getString(cursor.getColumnIndex("FECHA"))
            val latitud: String = cursor.getString(cursor.getColumnIndex("LATITUD"))
            val longitud: String = cursor.getString(cursor.getColumnIndex("LONGITUD"))
            val observacion: String = cursor.getString(cursor.getColumnIndex("OBSERVACION"))
            cadena += "${detalle()}'$codEmpresa','$codVendedor','$codClienteC','$codSubclienteC"
            cadena += "','" + tipo + "',to_date('" + fecha + "','dd/MM/yyyy hh24:mi:ss'),'" + latitud + "','" + longitud + "','" + FuncionesUtiles.usuario["LOGIN"] + "','" + observacion + "') "
            cursor.moveToNext()
        }
        cadena += " SELECT * FROM dual "
        if (cursor.count == 0) {
            MainActivity2.funcion.toast(contexto, "No existe ninguna marcacion pendiente de envio")
        } else {
            Enviar().execute()
        }
    }

    private class Enviar : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            try {
                dialogo.dismiss()
            } catch (e: Exception) {
            }
            dialogo = ProgressDialog.show(contexto, "Un momento...", "Enviando Marcaciones", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                resultado = MainActivity2.conexionWS.procesaMarcacionAsistencia(ListaClientes.codVendedor, cadena)
//                resultado = "01*GRABADO CON EXITO"
                null
            } catch (e: Exception) {
                resultado = e.message.toString()
                null
            }
        }

        override fun onPostExecute(unused: Void?) {
            dialogo.dismiss()
            val mensaje: Array<String> = resultado.split("*").toTypedArray()
            if (mensaje.size == 1) {
                MainActivity2.funcion.mensaje(contexto,"Resultado", resultado)
            } else {
                if (mensaje[0] == "01") {
                    val update = "update vt_marcacion_ubicacion set ESTADO = 'E' where ESTADO = 'P' and TIPO in ('E','S')"
                    MainActivity2.funcion.ejecutar(update, contexto)
                }
                MainActivity2.funcion.mensaje(contexto,"Resultado", mensaje[1])
            }
            etAccion.setText(accion)
        }
    }
}