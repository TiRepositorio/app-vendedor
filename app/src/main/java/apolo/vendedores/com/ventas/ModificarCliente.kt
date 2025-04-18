package apolo.vendedores.com.ventas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesDispositivo
import apolo.vendedores.com.utilidades.FuncionesUbicacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_modificar_cliente.*

class ModificarCliente : AppCompatActivity() {

    companion object{
        var codCliente : String = ""
        var codSubcliente : String = ""
        var codEmpresa : String = ""
        var editable = false
        var indVenta = false
    }

    var funcion : FuncionesUtiles = FuncionesUtiles()
    private var idCat : String = ""
    var tipo  : String = ""
    var nombre : String = ""

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_cliente)

        funcion = FuncionesUtiles(this)
        inicializarElementos()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun inicializarElementos(){
        val dispositivo = FuncionesDispositivo(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MainActivity2.rooteado = dispositivo.verificaRoot()
        }
        val ubicacion = FuncionesUbicacion(this)
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val telMgr : TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (!dispositivo.horaAutomatica() ||
            !dispositivo.modoAvion() ||
            !dispositivo.zonaHoraria() ||
            !dispositivo.tarjetaSim(telMgr) ||
            !ubicacion.validaUbicacionSimulada(lm)||
            !ubicacion.validaUbicacionSimulada2(lm)){
            MainActivity2.funcion.toast(this,"Verifique su configuración para continuar.")
            finish()
        }
        codCliente = ListaClientes.codCliente
        codSubcliente = ListaClientes.codSubcliente
        codEmpresa = ListaClientes.codEmpresa
        cargar(funcion.consultar(sqlModificar()),funcion.consultar(sqlCliente()))
        inicializarEt(etTel1,cbConfirmado1)
        inicializarEt(etTel2,cbConfirmado2)
        inicializarEt(etDireccion,cbConfirmado3)
        inicializarEt(etCercaDe,cbConfirmado4)
        inicializarEt(etCorreo,cbConfirmado5)
        btnAceptar.setOnClickListener{registrar()}
        btnCancelar.setOnClickListener{finish()}
        inicializaETAccion(accion)
        habilitar(editable)
    }

    private fun sqlModificar():String{
        return ("Select id, COD_EMPRESA, COD_CLIENTE, COD_SUBCLIENTE, TELEFONO1, TELEFONO2, DIRECCION, CERCA_DE, FOTO_FACHADA, TIPO, EMAIL "
                    + " from svm_modifica_catastro "
                    + " WHERE COD_CLIENTE    = '" + codCliente + "'"
                    + "   and COD_SUBCLIENTE = '" + codSubcliente + "'"
                    + "   AND COD_EMPRESA = '" + codEmpresa + "' "
                    + "   and ESTADO         = 'P' ")
    }

    private fun sqlCliente():String{
        return "SELECT COD_EMPRESA, COD_CLIENTE,COD_SUBCLIENTE,TELEFONO,TELEFONO2,DIRECCION,CERCA_DE,FOTO_FACHADA, EMAIL " +
                           "  FROM svm_cliente_vendedor " +
                           " WHERE COD_CLIENTE    = '" + codCliente + "' " +
                           "   AND COD_SUBCLIENTE = '" + codSubcliente + "' " +
                           "   AND COD_EMPRESA = '" + codEmpresa + "' " +
                           " GROUP BY COD_CLIENTE,COD_SUBCLIENTE,TELEFONO,TELEFONO2,DIRECCION,CERCA_DE,FOTO_FACHADA, EMAIL "
    }

    private fun cargar(modificar: Cursor, cliente:Cursor){
        if (modificar.count == 0){
            etTel1.setText(funcion.dato(cliente,"TELEFONO").replace("null","").trim())
            etTel2.setText(funcion.dato(cliente,"TELEFONO2").replace("null","").trim())
            etDireccion.setText(funcion.dato(cliente,"DIRECCION").replace("null","").trim())
            etCercaDe.setText(funcion.dato(cliente,"CERCA_DE").replace("null","").trim())
            etCorreo.setText(funcion.dato(cliente,"EMAIL").replace("null","").trim())
            idCat = ""
            tipo = ""
        } else {
            etTel1.setText(funcion.dato(modificar,"TELEFONO1").replace("null","").trim())
            etTel2.setText(funcion.dato(modificar,"TELEFONO2").replace("null","").trim())
            etDireccion.setText(funcion.dato(modificar,"DIRECCION").replace("null","").trim())
            etCercaDe.setText(funcion.dato(modificar,"CERCA_DE").replace("null","").trim())
            etCorreo.setText(funcion.dato(modificar,"EMAIL").replace("null","").trim())
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
        var validacion : Boolean = (cbConfirmado1.isChecked && cbConfirmado2.isChecked && cbConfirmado3.isChecked and cbConfirmado5.isChecked)
        if (!validacion){
            funcion.mensaje(this,"Atención!","Debe confirmar todos los campos.")
        } else {

            if (etTel1.text.toString().trim().isEmpty()) {
                funcion.mensaje(this,"Atención!","El campo de Telefono 1 es obligatorio")
                validacion = false
            } else {

                if (etCorreo.text.toString().trim().isEmpty()) {
                    funcion.mensaje(this,"Atención!","El campo de correo es obligatorio")
                    validacion = false
                } else {


                    //val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
                    val regex = "([0-9]+)?".toRegex()
                    if (!etTel1.text.toString().matches(regex)) {
                        funcion.mensaje(this,"Atención!","El campo telefono 1 solo debe tener numeros")
                        validacion = false
                    }  else {

                        if (!etTel2.text.toString().matches(regex) && etTel2.text.toString() != "") {
                            funcion.mensaje(this,"Atención!","El campo telefono 2 solo debe tener numeros")
                            validacion = false
                        }

                    }





                }

            }

        }
        return validacion
    }

    private fun registrar(){
        if (validaDatos()){
            if (idCat == "") {
                val cv = ContentValues()
                cv.put("COD_EMPRESA", codEmpresa)
                cv.put("COD_CLIENTE", codCliente)
                cv.put("COD_SUBCLIENTE", codSubcliente)
                cv.put("TELEFONO1", etTel1.text.toString())
                cv.put("TELEFONO2", etTel2.text.toString())
                cv.put("DIRECCION", etDireccion.text.toString().trim())
                cv.put("CERCA_DE", etCercaDe.text.toString())
                cv.put("EMAIL", etCorreo.text.toString())
                cv.put("LATITUD", "")
                cv.put("LONGITUD", "")
                cv.put("FECHA", funcion.getFechaActual())
                cv.put("ESTADO", "P")
                cv.put("TIPO", "D")
                funcion.insertar("svm_modifica_catastro", cv)
            } else {
                val cv = ContentValues()
                cv.put("COD_EMPRESA", codEmpresa)
                cv.put("COD_CLIENTE", codCliente)
                cv.put("COD_SUBCLIENTE", codSubcliente)
                cv.put("TELEFONO1", etTel1.text.toString())
                cv.put("TELEFONO2", etTel2.text.toString())
                cv.put("DIRECCION", etDireccion.text.toString())
                cv.put("CERCA_DE", etCercaDe.text.toString())
                cv.put("EMAIL", etCorreo.text.toString().trim())
                cv.put("ESTADO", "P")
                if (tipo == "G") {
                    cv.put("TIPO", "A")
                } else {
                    cv.put("TIPO", tipo)
                }
                funcion.actualizar("svm_modifica_catastro",cv, " id = '$idCat'")
            }

            val cv = ContentValues()
            cv.put("IND_CADUCADO", "N")
            funcion.actualizar("svm_cliente_vendedor",cv, "     cod_empresa = '$codEmpresa' " +
                                                                      " AND cod_cliente = '$codCliente'" +
                                                                      " AND cod_subcliente = '$codSubcliente'")

            try {
                FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["IND_CADUCADO"] = "N"
            } catch (e: Exception) {
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
        val enviarModificacion = EnviarModificacion()
        enviarModificacion.enviar()
        procesoEnviar()
    }

    private fun procesoEnviar(){
        val progressDialog =  apolo.vendedores.com.utilidades.ProgressDialog(EnviarModificacion.context)
        val thread = Thread{
            runOnUiThread{ progressDialog.cargarDialogo("Comprobando conexion",false) }
            EnviarModificacion.respuesta = try {
                MainActivity2.conexionWS.procesaVersion()
            } catch (e: Exception) {
                e.message.toString()
            }
            runOnUiThread { progressDialog.cerrarDialogo() }
            if (EnviarModificacion.respuesta != "null") {
                try {
                    runOnUiThread { progressDialog.cargarDialogo("Enviando la actualizacion al servidor...",false) }
                    EnviarModificacion.respuesta = MainActivity.conexionWS.procesaActualizaDatosClienteFinal(ListaClientes.codVendedor,
                        EnviarModificacion.vCliente,
                        EnviarModificacion.fotoFachada
                    )
                    if (EnviarModificacion.respuesta.indexOf("01*") >= 0 || EnviarModificacion.respuesta.indexOf("03*") >= 0) {
                        val values = ContentValues()
                        values.put("ESTADO", "E")
                        MainActivity.bd!!.update("svm_modifica_catastro",values,
                            " COD_CLIENTE = '${EnviarModificacion.codCliente}' and COD_SUBCLIENTE = '${EnviarModificacion.codSubcliente}' and ESTADO = 'P'",
                            null)
                    }
                    if (EnviarModificacion.respuesta.indexOf("Unable to resolve host") > -1) {
                        EnviarModificacion.respuesta = "07*" + "Verifique su conexion a internet y vuelva a intentarlo"
                    }
                    runOnUiThread {
                        val gatillo = DialogoAutorizacion(EnviarModificacion.context)
                        gatillo.dialogoAccion("cerrar",
                            EnviarModificacion.accion,
                            EnviarModificacion.respuesta,"","OK")
                        progressDialog.cerrarDialogo()
                    }
                    return@Thread
                } catch (e: Exception) {
                    runOnUiThread { progressDialog.cerrarDialogo() }
                    e.message
                }
            } else {
                try {
                    runOnUiThread { progressDialog.cargarDialogo("Enviando la actualizacion al servidor...",false) }
                    EnviarModificacion.respuesta = MainActivity.conexionWS.procesaActualizaDatosClienteFinal(ListaClientes.codVendedor,
                        EnviarModificacion.vCliente,
                        EnviarModificacion.fotoFachada
                    )
                    if (EnviarModificacion.respuesta.indexOf("01*") >= 0 || EnviarModificacion.respuesta.indexOf("03*") >= 0) {
                        val values = ContentValues()
                        values.put("ESTADO", "E")
                        MainActivity.bd!!.update("svm_modifica_catastro",values,
                            " COD_CLIENTE = '${EnviarModificacion.codCliente}' and COD_SUBCLIENTE = '${EnviarModificacion.codSubcliente}' and ESTADO = 'P'",
                            null)
                    }
                    if (EnviarModificacion.respuesta.indexOf("Unable to resolve host") > -1) {
                        EnviarModificacion.respuesta = "07*" + "Verifique su conexion a internet y vuelva a intentarlo"
                    }
                    runOnUiThread {
                        val gatillo = DialogoAutorizacion(EnviarModificacion.context)
                        gatillo.dialogoAccion("cerrar",
                            EnviarModificacion.accion,
                            EnviarModificacion.respuesta,"","OK")
                        progressDialog.cerrarDialogo()
                    }
                    return@Thread
                } catch (e: Exception) {
                    runOnUiThread { progressDialog.cerrarDialogo() }
                    e.message
                }
            }
            runOnUiThread {
                progressDialog.cerrarDialogo()
                Toast.makeText(
                    EnviarModificacion.context,"Verifique su conexion a internet y vuelva a intentarlo",
                    Toast.LENGTH_SHORT).show()
            }
        }
        thread.start()

    }

    private fun habilitar(estado: Boolean){
        etTel1.isEnabled = estado
        cbConfirmado1.isEnabled = estado
        etTel2.isEnabled = estado
        cbConfirmado2.isEnabled = estado
        etDireccion.isEnabled = estado
        cbConfirmado3.isEnabled = estado
        etCercaDe.isEnabled = estado
        cbConfirmado4.isEnabled = estado
        etCorreo.isEnabled = estado
        cbConfirmado5.isEnabled = estado
        btnAceptar.isEnabled = estado
        btnCancelar.isEnabled = estado
    }

}
