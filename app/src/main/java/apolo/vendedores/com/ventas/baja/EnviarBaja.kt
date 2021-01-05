package apolo.vendedores.com.ventas.baja

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.CountDownTimer
import android.widget.EditText
import android.widget.Toast
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.FuncionesUtiles

class EnviarBaja() {

    companion object{
        lateinit var context : Context
        lateinit var accion : EditText
        var resultado = ""
        var cliente = ""
        var comentario = ""
        var fotoFachada = ""
    }

    fun cargar(){
        val sql = "select * from svm_cliente_vendedor " +
                "   where COD_VENDEDOR   = '${Baja.codVendedor}'   " +
                "     and COD_CLIENTE    = '${Baja.codCliente}'    " +
                "     and COD_SUBCLIENTE = '${Baja.codSubcliente}' "
        cargarCliente(MainActivity2.funcion.consultar(sql))
    }

    private fun cargarCliente(cursor:Cursor){
        cliente = "INSERT INTO cc_clientes_baja_prov " +
                "(COD_EMPRESA" +
                ",COD_VENDEDOR" +
                ",DESC_VENDEDOR" +
                ",COD_CLIENTE" +
                ",COD_SUBCLIENTE" +
                ",DESC_CLIENTE" +
                ",DESC_SUBCLIENTE" +
                ",COMENTARIO" +
                ",ESTADO" +
                ",FOTO_FACHADA) " +
                "VALUES " +
                "(" +
                "'${FuncionesUtiles.usuario["COD_EMPRESA"]}'," +
                "'${Baja.codVendedor}'," +
                "'${FuncionesUtiles.usuario["NOMBRE"]}'," +
                "'${Baja.codCliente}'," +
                "'${Baja.codSubcliente}'," +
                "'${MainActivity2.funcion.dato(cursor,"DESC_CLIENTE")}'," +
                "'${MainActivity2.funcion.dato(cursor,"DESC_SUBCLIENTE")}'," +
                "'$comentario'," +
                "'P'," +
                "'$fotoFachada'" +
                ")"
        MainActivity2.funcion.ejecutar(cliente, context)
        cliente = "${FuncionesUtiles.usuario["COD_EMPRESA"]}" +
                  "|${Baja.codVendedor}" +
                  "|${FuncionesUtiles.usuario["NOMBRE"]}" +
                  "|${Baja.codCliente}" +
                  "|${Baja.codSubcliente}" +
                  "|${MainActivity2.funcion.dato(cursor,"DESC_CLIENTE")}" +
                  "|${MainActivity2.funcion.dato(cursor,"DESC_SUBCLIENTE")}" +
                  "|$comentario" +
                  "|P" +
                  "|$fotoFachada"
        Enviar().execute()
    }

    private class Enviar : AsyncTask<Void?, Void?, Void?>() {
        var pbarDialog: ProgressDialog? = null
        override fun onPreExecute() {
            try {
                pbarDialog!!.dismiss()
            } catch (e: java.lang.Exception) {
            }
            pbarDialog = ProgressDialog.show(context, "Un momento...", "Comprobando conexion", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                resultado = MainActivity2.conexionWS.enviarBaja(Baja.codVendedor,Baja.codCliente,Baja.codSubcliente, cliente, fotoFachada)
                null
            } catch (e: java.lang.Exception) {
                resultado = e.message.toString()
                null
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(unused: Void?) {
            if (resultado.split("*")[0].trim() == "01") {
                MainActivity2.funcion.mensaje(
                    context,
                    "Atención",
                    "Se guardó correctamente"
                )
            } else {
                MainActivity2.funcion.mensaje(
                    context,
                    "Error al intentar enviar",
                    "No se ha podido enviar el archivo, intente más tarde.\n$resultado"
                )
            }

            //---------------------------------------------//
            object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //startActivity(new Intent(Conf_Planilla.this, ActMapaPrueba.class));
                }

                override fun onFinish() {
                    if (resultado.split("*")[0].trim() == "01") {
                        accion.setText("finish")
                    }
                }
            }.start()
            //---------------------------------------------//
            pbarDialog!!.dismiss()
            if (resultado != "null") {
                return
            }
            Toast.makeText(context, "Verifique su conexion a internet y vuelva a intentarlo", Toast.LENGTH_SHORT).show()
            accion.setText("finish")
            return
        }
    }

}