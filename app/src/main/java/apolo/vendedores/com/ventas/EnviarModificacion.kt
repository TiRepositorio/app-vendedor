package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.widget.EditText
import apolo.vendedores.com.utilidades.FuncionesUtiles

class EnviarModificacion {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        @SuppressLint("StaticFieldLeak")
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
        return ("SELECT id, COD_EMPRESA, COD_CLIENTE, COD_SUBCLIENTE, TELEFONO1, TELEFONO2, DIRECCION, CERCA_DE, LATITUD, LONGITUD, FOTO_FACHADA, TIPO "
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
        //siempre debe mandar por empresa 1
        val codEmpresa: String = "1" //funcion.dato(cursor,"COD_EMPRESA")

        vCliente = "'" + codEmpresa + "'|'" + codCliente + "'|'" +
                   codSubcliente + "'|'" + telefono1 + "'|'" + telefono2 + "'|'" +
                   direccion + "'|'" + cercaDe + "'|'" + latitud + "'|'" + longitud + "'|'" + tipo + "'"
    }
}