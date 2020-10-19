package apolo.vendedores.com.ventas

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.widget.EditText
import android.widget.Toast
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesUtiles

class EnviarModificacion {

    companion object{
        lateinit var context : Context
        lateinit var accion : EditText
        var codCliente : String = ""
        var codSubcliente : String = ""
        var vCliente : String = ""
        var respuesta : String = ""
        var fotoFachada : String = ""
    }

    var funcion : FuncionesUtiles = FuncionesUtiles(context)

    fun enviar(){
        cargar(funcion.consultar(sql()))
    }

    fun sql():String{
        return ("SELECT id, COD_CLIENTE, COD_SUBCLIENTE, TELEFONO1, TELEFONO2, DIRECCION, CERCA_DE, LATITUD, LONGITUD, FOTO_FACHADA, TIPO "
                    + " FROM svm_modifica_catastro "
                    + " WHERE COD_CLIENTE    = '" + codCliente + "'"
                    + "   AND COD_SUBCLIENTE = '" + codSubcliente + "'"
                    + "   AND ESTADO 		 = 'P' ")
    }

    private fun cargar (cursor: Cursor){
        val telefono1: String = funcion.dato(cursor,"TELEFONO1")
        val telefono2: String = funcion.dato(cursor,"TELEFONO2")
        val direccion: String = funcion.dato(cursor,"DIRECCION")
        val cercaDe: String = funcion.dato(cursor,"CERCA_DE")
        val latitud: String = funcion.dato(cursor,"LATITUD")
        val longitud: String = funcion.dato(cursor,"LONGITUD")
        val tipo: String = funcion.dato(cursor,"TIPO")

        vCliente = "'1'|'" + codCliente + "'|'" + codSubcliente + "'|'" + telefono1 + "'|'" + telefono2 + "'|'" +
                   direccion + "'|'" + cercaDe + "'|'" + latitud + "'|'" + longitud + "'|'" + tipo + "'"

        ProbarConexion().execute()
    }

    class ProbarConexion :  AsyncTask<Void?, Void?, Void?>() {
        private lateinit var dialogo : ProgressDialog
        override fun onPreExecute() {
            dialogo = ProgressDialog.show(context, "Un momento...","Comprobando conexion", true)
        }
        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                respuesta = MainActivity2.conexionWS.procesaVersion(FuncionesUtiles.usuario["LOGIN"].toString())
                null
            } catch (e: Exception) {
                respuesta = e.message.toString()
                null
            }
        }
        override fun onPostExecute(unused: Void?) {
            dialogo.dismiss()
            if (respuesta != "null") {
                try {
                    enviarDatos().execute()
                    return
                } catch (e: Exception) {
                    e.message
                }
            } else {
                enviarDatos().execute()
                return
            }
            Toast.makeText(context,"Verifique su conexion a internet y vuelva a intentarlo",Toast.LENGTH_SHORT).show()
            return
        }
    }

    private class enviarDatos : AsyncTask<Void?, Void?, Void?>() {
        private lateinit var dialogo: ProgressDialog
        override fun onPreExecute() {
            try {
                dialogo.dismiss()
            } catch (e: java.lang.Exception) {
            }
            dialogo= ProgressDialog.show(context, "Un momento...","Enviando la actualizacion al servidor...", true)
        }
        override fun doInBackground(vararg params: Void?): Void? {
            respuesta = MainActivity.conexionWS.procesaActualizaDatosClienteFinal(ListaClientes.codVendedor,vCliente, fotoFachada).toString()
//            respuesta = "01*Enviado con exito"
            return null
        }
        override fun onPostExecute(unused: Void?) {
            dialogo.dismiss()
            if (respuesta.indexOf("01*") >= 0 || respuesta.indexOf("03*") >= 0) {
                val values = ContentValues()
                values.put("ESTADO", "E")
                MainActivity.bd!!.update("svm_modifica_catastro",values,
                    " COD_CLIENTE = '$codCliente' and COD_SUBCLIENTE = '$codSubcliente' and ESTADO = 'P'",
                                         null)
            }
            if (respuesta.indexOf("Unable to resolve host") > -1) {
                respuesta = "07*" + "Verifique su conexion a internet y vuelva a intentarlo"
            }
            val gatillo = DialogoAutorizacion(context)
            gatillo.dialogoAccion("cerrar",accion, respuesta,"","OK")
        }
    }

}