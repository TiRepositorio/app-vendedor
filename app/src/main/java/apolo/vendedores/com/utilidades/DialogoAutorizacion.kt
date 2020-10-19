package apolo.vendedores.com.utilidades

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText

class DialogoAutorizacion(var context: Context) {

    private var claves : Clave = Clave()
    var funcion : FuncionesUtiles = FuncionesUtiles(context)

    fun dialogoAutorizacion(accion:String, cargaAcion : EditText){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
//        var claveTemp : String = claves.generaClave()
        val claveTemp = "54127854"
        dialogo.setTitle("Solicitar autorizacion")

        dialogo.setMessage(claveTemp)
        val contraClave = EditText(context)
        contraClave.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(contraClave)
        dialogo.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            if (contraClave.text.isEmpty()||contraClave.text.length != 8){
                funcion.mensaje("Error","Clave incorrecta")
            } else {
                if (contraClave.text.toString().trim() == claves.contraClave(claveTemp).trim()){
                    cargaAcion.setText(accion)
                    funcion.mensaje(context,"Correcto","La clave fue aceptada")
                } else {
                    funcion.mensaje(context,"Error","La clave no es valida")
                }
            }
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }

    fun dialogoAutorizacion(accion:String,noAccion:String, cargaAcion : EditText,mensaje:String, titulo:String,codigo:String,mTrue:String,mFalse:String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        val contraClave = EditText(context)
        contraClave.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(contraClave)
        dialogo.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            if (contraClave.text.toString().trim() == codigo){
                cargaAcion.setText(accion)
    //                funcion.mensaje(context,"Atencion!",mTrue)
            } else {
                cargaAcion.setText(noAccion)
                funcion.mensaje(context,"Error!",mFalse)
            }
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }

    fun dialogoAccion(accion:String, cargaAcion : EditText, mensaje:String,titulo:String,boton:String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.setPositiveButton(boton) { _: DialogInterface, _: Int ->
            cargaAcion.setText(accion)
        }
        dialogo.setCancelable(true)
        dialogo.show()
    }

    fun dialogoAccionOpcion(accionAceptar:String,accionCancelar:String, cargaAcion : EditText, mensaje:String,titulo:String,botonAceptar:String,botonCancelar:String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.setPositiveButton(botonAceptar) { _: DialogInterface, _: Int ->
            cargaAcion.setText(accionAceptar)
        }
        dialogo.setNegativeButton(botonCancelar) { _: DialogInterface, _: Int ->
            cargaAcion.setText(accionCancelar)
        }
        dialogo.setCancelable(true)
        dialogo.show()
    }

}