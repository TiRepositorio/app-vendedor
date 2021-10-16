package apolo.vendedores.com.utilidades

import android.app.Dialog
import android.content.Context
import apolo.vendedores.com.R
import kotlinx.android.synthetic.main.progres_dialog.*

class ProgressDialog (val context:Context) {

    lateinit var dialogo: Dialog

    fun cargarDialogo(mensaje:String,cancelable:Boolean){
        dialogo = Dialog(context)
        dialogo.setContentView(R.layout.progres_dialog)
        dialogo.setCancelable(cancelable)
        dialogo.tvTexto.text = mensaje
        dialogo.show()
    }

    fun cerrarDialogo(){
        dialogo.dismiss()
    }


}