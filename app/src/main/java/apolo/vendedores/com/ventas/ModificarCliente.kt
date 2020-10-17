package apolo.vendedores.com.ventas

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_modificar_cliente.*

class ModificarCliente : AppCompatActivity() {

    companion object{
        var codCliente : String = ""
        var codSubcliente : String = ""
    }

    var funcion : FuncionesUtiles = FuncionesUtiles()
    private var idCat : String = ""
    var tipo  : String = ""
    var nombre : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_cliente)

        funcion = FuncionesUtiles(this)
        inicializarElementos()
    }

    fun inicializarElementos(){
        codCliente = ListaClientes.codCliente
        codSubcliente = ListaClientes.codSubcliente
        cargar(funcion.consultar(sqlModificar()),funcion.consultar(sqlCliente()))
        inicializarEt(etTel1,cbConfirmado1)
        inicializarEt(etTel2,cbConfirmado2)
        inicializarEt(etDireccion,cbConfirmado3)
        inicializarEt(etCercaDe,cbConfirmado4)
        btnAceptar.setOnClickListener{registrar()}
        btnCancelar.setOnClickListener{finish()}
        inicializaETAccion(accion)
    }

    private fun sqlModificar():String{
        return ("Select id, COD_CLIENTE, COD_SUBCLIENTE, TELEFONO1, TELEFONO2, DIRECCION, CERCA_DE, FOTO_FACHADA, TIPO "
                    + " from svm_modifica_catastro "
                    + " WHERE COD_CLIENTE    = '" + codCliente + "'"
                    + "   and COD_SUBCLIENTE = '" + codSubcliente + "'"
                    + "   and ESTADO         = 'P' ")
    }

    private fun sqlCliente():String{
        return "SELECT COD_CLIENTE,COD_SUBCLIENTE,TELEFONO,TELEFONO2,DIRECCION,CERCA_DE,FOTO_FACHADA " +
                           "  FROM svm_cliente_vendedor " +
                           " WHERE COD_CLIENTE    = '" + codCliente + "' " +
                           "   AND COD_SUBCLIENTE = '" + codSubcliente + "' " +
                           " GROUP BY COD_CLIENTE,COD_SUBCLIENTE,TELEFONO,TELEFONO2,DIRECCION,CERCA_DE,FOTO_FACHADA"
    }

    private fun cargar(modificar: Cursor, cliente:Cursor){
        if (modificar.count == 0){
            etTel1.setText(funcion.dato(cliente,"TELEFONO").replace("null",""))
            etTel2.setText(funcion.dato(cliente,"TELEFONO2").replace("null",""))
            etDireccion.setText(funcion.dato(cliente,"DIRECCION").replace("null",""))
            etCercaDe.setText(funcion.dato(cliente,"CERCA_DE").replace("null",""))
            idCat = ""
            tipo = ""
        } else {
            etTel1.setText(funcion.dato(modificar,"TELEFONO1").replace("null",""))
            etTel2.setText(funcion.dato(modificar,"TELEFONO2").replace("null",""))
            etDireccion.setText(funcion.dato(modificar,"DIRECCION").replace("null",""))
            etCercaDe.setText(funcion.dato(modificar,"CERCA_DE").replace("null",""))
            idCat = funcion.dato(modificar,"id").replace("null","")
            tipo = "D"
        }
    }

    private fun inicializarEt(editText: EditText, checkBox: CheckBox){
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                checkBox.isChecked = editText.text.toString().isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    private fun validaDatos():Boolean{
        val validacion : Boolean = (cbConfirmado1.isChecked && cbConfirmado2.isChecked && cbConfirmado3.isChecked and cbConfirmado4.isChecked)
        if (!validacion){
            funcion.mensaje(this,"Atención!","Debe confirmar todos los campos.")
        }
        return validacion
    }

    private fun registrar(){
        if (validaDatos()){
            if (idCat == "") {
                val cv = ContentValues()
                cv.put("COD_EMPRESA", "1")
                cv.put("COD_CLIENTE", codCliente)
                cv.put("COD_SUBCLIENTE", codSubcliente)
                cv.put("TELEFONO1", etTel1.text.toString())
                cv.put("TELEFONO2", etTel2.text.toString())
                cv.put("DIRECCION", etDireccion.text.toString())
                cv.put("CERCA_DE", etCercaDe.text.toString())
                cv.put("LATITUD", "")
                cv.put("LONGITUD", "")
                cv.put("FECHA", funcion.getFechaActual())
                cv.put("ESTADO", "P")
                cv.put("TIPO", "D")
                funcion.insertar("svm_modifica_catastro", cv)
            } else {
                val cv = ContentValues()
                cv.put("COD_EMPRESA", "1")
                cv.put("COD_CLIENTE", codCliente)
                cv.put("COD_SUBCLIENTE", codSubcliente)
                cv.put("TELEFONO1", etTel1.text.toString())
                cv.put("TELEFONO2", etTel2.text.toString())
                cv.put("DIRECCION", etDireccion.text.toString())
                cv.put("CERCA_DE", etCercaDe.text.toString())
                cv.put("ESTADO", "P")
                if (tipo == "G") {
                    cv.put("TIPO", "A")
                } else {
                    cv.put("TIPO", tipo)
                }
                funcion.actualizar("svm_modifica_catastro",cv, " id = '$idCat'")
            }
            val gatillo = DialogoAutorizacion(this)
            gatillo.dialogoAccionOpcion("enviar","cerrar",accion,"Desea enviar los datos al servidor?","Guardado con exito","Si","No")
        }
    }

    private fun inicializaETAccion(etAccion:EditText){
        etAccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etAccion.text.toString() == "cerrar"){
                    etAccion.setText("")
                    finish()
                    return
                }
                if (etAccion.text.toString() == "enviar"){
                    etAccion.setText("")
                    enviar()
                    return
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    fun enviar(){
        EnviarModificacion.context = this
        EnviarModificacion.codCliente = codCliente
        EnviarModificacion.codSubcliente = codSubcliente
        EnviarModificacion.accion = accion
        var enviarModificacion : EnviarModificacion = EnviarModificacion()
        enviarModificacion.enviar()
    }

}
