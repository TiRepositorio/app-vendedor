package apolo.vendedores.com.utilidades

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText

class DialogoAutorizacion {

    constructor(context: Context){
        this.context = context
        this.funcion = FuncionesUtiles(context)
    }

    var claves : Clave = Clave()
    var funcion : FuncionesUtiles

    var context: Context

    fun dialogoAutorizacion(accion:String, cargaAcion : EditText){
        var dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
//        var claveTemp : String = claves.generaClave()
        var claveTemp : String = "54127854"
        dialogo.setTitle("Solicitar autorizacion")

        dialogo.setMessage(claveTemp)
        var contraClave:EditText = EditText(context)
        contraClave.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(contraClave)
        dialogo.setPositiveButton("OK", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            if (contraClave.text.isEmpty()||contraClave.text.length != 8){
                funcion.mensaje("Error","Clave incorrecta")
            } else {
                if (contraClave.text.toString().trim().equals(claves.contraClave(claveTemp).toString().trim())){
                    cargaAcion.setText(accion)
                    funcion.mensaje("Correcto","La clave fue aceptada")
                } else {
                    funcion.mensaje("Error","La clave no es valida")
                }
            }
        })
        dialogo.setCancelable(false)
        dialogo.show()
    }

    fun dialogoAutorizacion(accion:String,noAccion:String, cargaAcion : EditText,mensaje:String, titulo:String,codigo:String,mTrue:String,mFalse:String){
        var dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        var contraClave:EditText = EditText(context)
        contraClave.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(contraClave)
        dialogo.setPositiveButton("OK", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            if (contraClave.text.toString().trim().equals(codigo)){
                cargaAcion.setText(accion)
//                funcion.mensaje(context,"Atencion!",mTrue)
            } else {
                cargaAcion.setText(noAccion)
                funcion.mensaje(context,"Error!",mFalse)
            }
        })
        dialogo.setCancelable(false)
        dialogo.show()
    }

    fun dialogoAccion(accion:String, cargaAcion : EditText, mensaje:String,titulo:String,boton:String){
        var dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.setPositiveButton(boton, DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            cargaAcion.setText(accion)
        })
        dialogo.setCancelable(true)
        dialogo.show()
    }

    fun dialogoAccionOpcion(accionAceptar:String,accionCancelar:String, cargaAcion : EditText, mensaje:String,titulo:String,botonAceptar:String,botonCancelar:String){
        var dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.setPositiveButton(botonAceptar, DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            cargaAcion.setText(accionAceptar)
        })
        dialogo.setNegativeButton(botonCancelar, DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            cargaAcion.setText(accionCancelar)
        })
        dialogo.setCancelable(true)
        dialogo.show()
    }

}