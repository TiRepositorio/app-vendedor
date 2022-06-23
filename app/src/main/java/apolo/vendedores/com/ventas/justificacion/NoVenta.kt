package apolo.vendedores.com.ventas.justificacion

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.telephony.TelephonyManager
import android.text.InputType
import android.widget.*
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.utilidades.*
import apolo.vendedores.com.ventas.ListaClientes
import kotlin.math.roundToInt

class NoVenta(private val codCliente: String, private val codSubcliente:String,
              private val lm:LocationManager?, private val telMgr: TelephonyManager?,
              private val latitud:String, private val longitud: String) {

    companion object{
        var codEmpresa = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var context  : Context
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
        var resultado = ""
        var noVenta  = ""
        var modificacion = false
        var editable = false
        var nuevo = true
        var id = ""
        var fechaMod = ""
        fun actualizaEstadoEnvioMarcacion(idMarcaciones: String) {
            var idMarcacion = idMarcaciones
            val cursorMarcacionesVisitaBuscar: Cursor
            if (idMarcacion == "") {
                val select = ("Select id "
                        + "  from vt_marcacion_visita "
                        + " where COD_VENDEDOR   = '" + ListaClientes.codVendedor + "' "
                        + "   and COD_CLIENTE    = '" + ListaClientes.codCliente + "' "
                        + "   and COD_SUBCLIENTE = '" + ListaClientes.codSubcliente + "' "
                        + "   and COD_EMPRESA    = '$codEmpresa' "
                        + "   and FECHA 	  = '" + MainActivity2.funcion.getFechaActual().substring(0, 10) + "'")
                cursorMarcacionesVisitaBuscar = MainActivity2.funcion.consultar(select)
                cursorMarcacionesVisitaBuscar.moveToFirst()
                if (cursorMarcacionesVisitaBuscar.count > 0) {
                    idMarcacion = MainActivity2.funcion.dato(cursorMarcacionesVisitaBuscar,"id")
                }
            }
            val cv = ContentValues()
            cv.put("ESTADO", "E")
            MainActivity2.bd!!.update("vt_marcacion_visita", cv, "id = '$idMarcacion'", null)
        }
    }

    private lateinit var dialogo: apolo.vendedores.com.utilidades.ProgressDialog
    private lateinit var thread: Thread

    private val ubicacion   = FuncionesUbicacion(context)
    private val dispositivo = FuncionesDispositivo(context)
    private val funcion     = FuncionesUtiles(context)
    private var listaMotivos= ArrayList<HashMap<String,String>>()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun cargarDialogo(){
        if (lm != null) {
            if (!verificaMarcacionCliente() && nuevo){
                return
            }
            if (!validacion("Abrir")) {
                return
            }
        }

        marcarNoVenta()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun validacion(trigger:String) : Boolean {
        ubicacion.obtenerUbicacion(lm!!)
        if (!ubicacion.validaUbicacionSimulada(lm)) { return false }
        if (!dispositivo.horaAutomatica()) { return false }
        if (!dispositivo.modoAvion()){ return false }
        if (!dispositivo.zonaHoraria()){ return false }
        if (!dispositivo.fechaCorrecta()){ return false }
        if (!dispositivo.tarjetaSim(telMgr!!)){ return false }
        if (!ubicacion.ubicacionActivada(lm)){
            ubicacion.latitud  = ""
            ubicacion.longitud = ""
            etAccion.setText("abrirUbicacion")
            return false
        }
        if (latitud.trim() == "" || longitud.trim() == "") {
            // ABRIR EL MAPA
            Mapa.modificarCliente = true
            Mapa.codCliente = codCliente
            Mapa.codSubcliente = codSubcliente
            Mapa.codVendedor = ListaClientes.codVendedor
            etAccion.setText("abrirMapa")
            return false
        } else {
            if (ubicacion.latitud.trim() == "" || ubicacion.longitud.trim() == "") {
                funcion.toast(context,"No se encuentra la ubicacion GPS del telefono")
                return false
            }
            val distanciaCliente : Double = ubicacion.calculaDistanciaCoordenadas(
                ubicacion.latitud.toDouble(),
                latitud.toDouble(),
                ubicacion.longitud.toDouble(),
                longitud.toDouble())
            if (distanciaCliente > funcion.getRangoDistancia()) {
                if(!verificaMarcacionCliente() && nuevo){
                    funcion.toast(context,"No se encuentra en el cliente. Se encuentra a " + distanciaCliente.roundToInt() + " m.")
                    val autorizacion = DialogoAutorizacion(context)
                    autorizacion.dialogoAutorizacion(trigger, etAccion)
                }else{
                    funcion.toast(context, "No se encuentra en el cliente. Se encuentra a ${distanciaCliente.roundToInt()} m.")
                }
                return false
            }
        }
        return true
    }

    private fun verificaMarcacionCliente(): Boolean {
        val sql : String = ("Select COD_CLIENTE, COD_SUBCLIENTE, TRIM(TIPO) TIPO 				"
                + "  from vt_marcacion_ubicacion             			"
                + " where TRIM(TIPO)           IN ('E','S')                 	"
                + "   and TRIM(COD_PROMOTOR)   = '" + ListaClientes.codVendedor.trim() + "' "
                + "   and TRIM(COD_CLIENTE)    = '" + codCliente.trim() + "' "
                + "   and TRIM(COD_SUBCLIENTE) = '" + codSubcliente.trim() + "' "
//                + "   and COD_EMPRESA    = '$codEmpresa' "
                + "   and TRIM(FECHA)          LIKE '${funcion.getFechaActual().trim()}%'"
                + " order by id desc                        				")
        val cursor: Cursor = funcion.consultar(sql)
        cursor.moveToFirst()
        return if (cursor.count > 0) {
            if (funcion.dato(cursor, "TIPO").trim() == "E") {
                true
            } else {
                funcion.toast(context,"Ya marcó salida en el cliente.")
                false
            }
        } else {
            funcion.toast(context,"Debe marcar entrada al cliente")
            false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun marcarNoVenta() {
        val cursorNoVenta: Cursor
        try {
            val fechaModificacion = if (fechaMod.isEmpty()){
                                        funcion.getFechaActual().substring(0, 10)
                                    } else {
                                        fechaMod
                                    }
            val select =
                ("Select id, COD_EMPRESA, COD_SUCURSAL, COD_CLIENTE, COD_SUBCLIENTE, COD_VENDEDOR, COD_MOTIVO, OBSERVACION, FECHA, LATITUD, LONGITUD, ESTADO, HORA_ALTA "
                        + " from vt_marcacion_visita "
                        + " where COD_VENDEDOR   = '" + ListaClientes.codVendedor + "' "
                        + "   and COD_CLIENTE    = '" + codCliente + "' "
                        + "   and COD_SUBCLIENTE = '" + codSubcliente + "' "
                        + "   and FECHA 	     = '" + fechaModificacion + "'"
                        + "   and COD_EMPRESA    = '$codEmpresa' "
//                        + "   and id             = '" + id + "' "
                        + "   and COD_MOTIVO NOT IN ('16')")
            fechaMod = ""
            cursorNoVenta = funcion.consultar(select)
            if (ListaClientes.estado.trim() != "" || cursorNoVenta.count > 0) {
                if(id==""){
                    id = funcion.dato(cursorNoVenta,"id")
                }
//                if(!nuevo) {
                    val sql = ("Select id, COD_EMPRESA, COD_SUCURSAL, COD_CLIENTE, COD_SUBCLIENTE, COD_VENDEDOR, COD_MOTIVO, OBSERVACION, FECHA, LATITUD, LONGITUD, ESTADO, HORA_ALTA "
                            + " from vt_marcacion_visita "
                            + " where COD_VENDEDOR   = '" + ListaClientes.codVendedor + "' "
                            + "   and COD_CLIENTE    = '" + codCliente + "' "
                            + "   and COD_SUBCLIENTE = '" + codSubcliente + "' "
                            + "   and COD_EMPRESA    = '$codEmpresa' "
//                            + "   and FECHA 	     = '" + fechaModificacion + "'"
                            + "   and id             = '" + id + "' "
                            + "   and COD_MOTIVO NOT IN ('16')")
                    val cursorModificacion = funcion.consultar(sql)
                    val estado = funcion.dato(cursorModificacion,"ESTADO")
                    if (estado != "E" || !editable) {
                        modificaMarcacionVisita2(sql, false)
                    } else {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                        builder.setTitle("Atención")
                        builder.setMessage("Ya se cargo la marcación de visita de este cliente")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                        val alert: AlertDialog = builder.create()
                        alert.show()
                    }
//                }
            } else {
                ListaClientes.claveAutorizacion = ""
                val alertMotivos: AlertDialog.Builder = AlertDialog.Builder(context)
                alertMotivos.setTitle("      Justificar la no Venta")
                val fecha: String = funcion.getFechaActual()
                alertMotivos.setMessage("Realizada el $fecha")
                listaMotivos = ArrayList()
                val cursorMotivos: Cursor = funcion.consultar("select * from spm_motivo_no_venta where cod_empresa = '$codEmpresa' order by DESC_MOTIVO")
                funcion.cargarLista(listaMotivos,cursorMotivos)
                val descMotivo = arrayOfNulls<String?>(cursorMotivos.count)
                val conSpinner = Spinner(context)
                val observacion = EditText(context)
                val tvObservacion = TextView(context)
                for (i in 0 until listaMotivos.size){descMotivo[i] = listaMotivos[i]["DESC_MOTIVO"]}
                tvObservacion.text = "Observaciones:"
                observacion.inputType = InputType.TYPE_CLASS_TEXT
                observacion.width = 60
                observacion.setLines(3)
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(conSpinner)
                layout.addView(tvObservacion)
                layout.addView(observacion)
                alertMotivos.setView(layout)
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context, R.layout.simple_spinner_item, descMotivo)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                conSpinner.adapter = adapter
                conSpinner.setSelection(0)
                if (modificacion && editable){
                    alertMotivos.setPositiveButton("Enviar") { _, _ ->
                        enviarNuevo(conSpinner,observacion,fecha,nuevo)
                    }
                }
                if (modificacion && editable) {
                    alertMotivos.setNeutralButton("Guardar") { _, _ ->
                        guardar(conSpinner,observacion,fecha)
                    }
                }
                alertMotivos.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
                val motivos: AlertDialog = alertMotivos.create()
                motivos.show()
            }
        } catch (e: NumberFormatException) {
        }
    }

    private fun enviarNuevo(conSpinner:Spinner, observacion:EditText, fecha:String, nuevos:Boolean){
        val horaAlta: String = funcion.getHoraActual()
        if (nuevos){
            guardaMarcacionVisita(
                codEmpresa,
                ListaClientes.codSucursalCliente,
                codCliente,
                codSubcliente,
                ListaClientes.codVendedor,
                listaMotivos[conSpinner.selectedItemPosition]["COD_MOTIVO"]!!,
                observacion.text.toString(),
                fecha.substring(0, 10),
                ubicacion.latitud,
                ubicacion.longitud,
                horaAlta
            )
            if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].toString().trim() != "") {
                if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].equals("S")) {
                    cerrarSalidaCliente()
                }
            }
            noVenta =
                "'$codEmpresa'," + "'" + ListaClientes.codSucursalCliente + "'," +
                        "'" + ListaClientes.codCliente + "'," + "'" + ListaClientes.codSubcliente + "'," +
                        "'" + ListaClientes.codVendedor + "'," +
                        "'" + listaMotivos[conSpinner.selectedItemPosition]["COD_MOTIVO"]!! + "'," +
                        "'" + observacion.text.toString() + "'," +
                        "to_date('" + fecha + "','DD/MM/YYYY')," +
                        "to_date('" + fecha + " " +
                        horaAlta + "','dd/MM/yyyy hh24:mi:ss')," + "'" +
                        ubicacion.latitud + "'," + "'" + ubicacion.longitud + "'"
            id = maxId()
            EnviarPositivacion2().execute()
        } else {
            if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].toString().trim() != "") {
                if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].equals("S")) {
                    cerrarSalidaCliente()
                }
            }
            EnviarPositivacion2().execute()
        }
    }

    private fun guardar(conSpinner: Spinner, observacion: EditText, fecha: String){
        val horaAlta: String = funcion.getHoraActual()
        guardaMarcacionVisita(
            codEmpresa,
            ListaClientes.codSucursalCliente,
            ListaClientes.codCliente,
            ListaClientes.codSubcliente,
            ListaClientes.codVendedor,
            listaMotivos[conSpinner.selectedItemPosition]["COD_MOTIVO"]!!,
            observacion.text.toString(),
            fecha.substring(0, 10),
            ubicacion.latitud,
            ubicacion.longitud,
            horaAlta
        )
        if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].toString()
                .trim() != ""
        ) {
            if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].equals("S")) {
                cerrarSalidaCliente()
            }
        }
        Toast.makeText(context, "Guardado con exito", Toast.LENGTH_LONG).show()
    }

    private fun maxId():String{
        val sql = "select max(id) id from vt_marcacion_visitas " +
                "where COD_CLIENTE = '${ListaClientes.codCliente}' and COD_SUBCLIENTE = '${ListaClientes.codSubcliente}'"
        return funcion.dato(funcion.consultar(sql),"id")
    }

    @SuppressLint("SetTextI18n")
    private fun modificaMarcacionVisita2(sql: String, consultar: Boolean) {
        try {
            val codMotivo2: String
            val observacion2: String
            val fecha: String
            val horaAlta: String
            var positionMotivo = 0
            val cursorMarcaciones: Cursor = funcion.consultar(sql)
            codMotivo2 = cursorMarcaciones.getString(cursorMarcaciones.getColumnIndex("COD_MOTIVO"))
            observacion2 = cursorMarcaciones.getString(cursorMarcaciones.getColumnIndex("OBSERVACION"))
            fecha = cursorMarcaciones.getString(cursorMarcaciones.getColumnIndex("FECHA"))
            horaAlta = cursorMarcaciones.getString(cursorMarcaciones.getColumnIndex("HORA_ALTA"))
            ListaClientes.claveAutorizacion = ""
            val alertMotivos = AlertDialog.Builder(context)
            alertMotivos.setTitle("         Marcación de visita")
            alertMotivos.setMessage("Realizada el $fecha")
            listaMotivos = ArrayList()
            val cursorMotivos: Cursor = funcion.consultar("select * from spm_motivo_no_venta WHERE COD_EMPRESA = '$codEmpresa' order by DESC_MOTIVO")
            val descMotivo = arrayOfNulls<String>(cursorMotivos.count)
            funcion.cargarLista(listaMotivos,cursorMotivos)
            for (i in 0 until listaMotivos.size){
                descMotivo[i] = listaMotivos[i]["DESC_MOTIVO"]
                if (listaMotivos[i]["COD_MOTIVO"].equals(codMotivo2)){
                    positionMotivo = i
                }
            }
            val conSpinner = Spinner(context)
            val observacion = EditText(context)
            val tvObservacion = TextView(context)
            tvObservacion.text = "Observaciones:"
            observacion.inputType = InputType.TYPE_CLASS_TEXT
            observacion.setText(observacion2)
            observacion.width = 60
            observacion.setLines(3)
            observacion.isEnabled = modificacion && editable
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(conSpinner)
            layout.addView(tvObservacion)
            layout.addView(observacion)
            alertMotivos.setView(layout)
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                context,
                R.layout.simple_spinner_item, descMotivo
            )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            conSpinner.adapter = adapter
            conSpinner.setSelection(1)
            conSpinner.setSelection(positionMotivo)
            conSpinner.isEnabled = nuevo
            if (modificacion && editable) {
                alertMotivos.setPositiveButton("Enviar") { dialog, _ ->
                    if (consultar) {
                        dialog.cancel()
                    } else {
                        noVenta =
                            "'$codEmpresa'," + "'" + ListaClientes.codSucursalCliente + "'," +
                                    "'" + ListaClientes.codCliente + "'," + "'" + ListaClientes.codSubcliente + "'," +
                                    "'" + ListaClientes.codVendedor + "'," +
                                    "'" + listaMotivos[conSpinner.selectedItemPosition]["COD_MOTIVO"] + "'," +
                                    "'" + observacion.text.toString() + "'," +
                                    "to_date('" + fecha.substring(0, 10) + "','DD/MM/YYYY')," +
                                    "to_date('" + fecha.substring(0, 10) + " " +
                                    horaAlta + "','dd/MM/yyyy hh24:mi:ss')," + "'" +
                                    ubicacion.latitud + "'," + "'" + ubicacion.longitud + "'"
                        if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].toString().trim() != "") {
                            if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].equals("S")) {
                                cerrarSalidaCliente()
                            }
                        }
                        EnviarPositivacion2().execute()
                    }
                }
            }
            if (!consultar) {
                if (modificacion && editable) {
                alertMotivos.setNeutralButton("Guardar") { _, _ ->
                        try {
                            actualizaMarcacionVisita(
                                codEmpresa,
                                ListaClientes.codSucursalCliente,
                                codCliente,
                                codSubcliente,
                                ListaClientes.codVendedor,
                                listaMotivos[conSpinner.selectedItemPosition]["COD_MOTIVO"]!!,
                                observacion.text.toString(),
                                fecha.substring(0, 10),
                                ubicacion.latitud,
                                ubicacion.longitud,
                                id
                            )
                        } catch (e: Exception) {
                            val err = e.message
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                        if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].toString().trim() != "") {
                            if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].equals("S")) {
                              cerrarSalidaCliente()
                            }
                        }
                        Toast.makeText(context, "Guardado con exito", Toast.LENGTH_LONG).show()
                    }
                }
            } else
            {
                conSpinner.isEnabled = false
                observacion.isEnabled = false
            }
            alertMotivos.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            val motivos = alertMotivos.create()
            motivos.show()
        } catch (e: Exception) {
            var err = e.message
            err += ""
        }
    }

    private fun guardaMarcacionVisita(codEmpresa: String, codSucursal: String,
                                      codCliente: String, codSubcliente: String,
                                      codVendedor: String, codMotivo: String,
                                      observacion: String, fecha: String,
                                      latitud: String, longitud: String,
                                      horaAlta: String) {
        val cv = ContentValues()
        cv.put("COD_EMPRESA", codEmpresa)
        cv.put("COD_SUCURSAL", codSucursal)
        cv.put("COD_CLIENTE", codCliente)
        cv.put("COD_SUBCLIENTE", codSubcliente)
        cv.put("COD_VENDEDOR", codVendedor)
        cv.put("COD_MOTIVO", codMotivo)
        cv.put("OBSERVACION", observacion)
        cv.put("FECHA", fecha)
        cv.put("LATITUD", latitud)
        cv.put("LONGITUD", longitud)
        cv.put("ESTADO", "P")
        cv.put("HORA_ALTA", horaAlta)
        MainActivity2.bd!!.insert("vt_marcacion_visita", null, cv)
    }



    private fun actualizaMarcacionVisita(codEmpresa: String, codSucursal: String,
                                         codCliente: String, codSubcliente: String,
                                         codVendedor: String, codMotivo: String,
                                         observacion: String, fecha: String,
                                         latitud: String, longitud: String,
                                         id: String) {
        val cv = ContentValues()
        cv.put("COD_EMPRESA", codEmpresa)
        cv.put("COD_SUCURSAL", codSucursal)
        cv.put("COD_CLIENTE", codCliente)
        cv.put("COD_SUBCLIENTE", codSubcliente)
        cv.put("COD_VENDEDOR", codVendedor)
        cv.put("COD_MOTIVO", codMotivo)
        cv.put("OBSERVACION", observacion)
        cv.put("FECHA", fecha)
        cv.put("LATITUD", latitud)
        cv.put("LONGITUD", longitud)
        MainActivity2.bd!!.update("vt_marcacion_visita", cv, "id = '$id'", null)
    }

    private class EnviarPositivacion2 : AsyncTask<Void?, Void?, Void?>()
    {
        private var dialogo: ProgressDialog? = null
        override fun onPreExecute() {
            dialogo = ProgressDialog.show(context, "Un momento...", "Enviando datos al servidor...", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            resultado = MainActivity2.conexionWS.procesaNoVenta(noVenta,ListaClientes.codVendedor, codEmpresa)
//            resultado = "01*GRABADO CON EXITO"
            return null
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(unused: Void?) {
            dialogo!!.dismiss()
            val res: String = resultado
            println(res)
            if (resultado.indexOf("01*") >= 0) {
                resultado = "Marcación de visita enviada con exito!!"
            }
            val builder = AlertDialog.Builder(context)
            builder.setMessage(resultado)
            builder.setCancelable(false)
            builder.setPositiveButton("OK") { _, _ ->
                val values: ContentValues?
                if (resultado == "Marcación de visita enviada con exito!!") {
                        values = ContentValues()
                        values.put("ESTADO", "E")
                        try {
                            MainActivity2.bd!!.update("vt_marcacion_visita", values,
                                " COD_CLIENTE = '" + ListaClientes.codCliente + "'" +
                                        "  and COD_SUBCLIENTE = '" + ListaClientes.codSubcliente + "'" +
                                        "  and id = '$id'" +
                                        "  AND COD_EMPRESA = '$codEmpresa'",
                                null
                            )
                            ListaClientes.estado = "E"
                            actualizaEstadoEnvioMarcacion(id)
                            id = ""
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        etAccion.setText("recargarLista")
                    }
                }
            val alert = builder.create()
            alert.show()
        }

    }

    fun enviarPositivacion(){
        thread = Thread{

        }
    }

    private fun cerrarSalidaCliente() {
        val fecEntrada: String = funcion.getFechaActual() + " " + funcion.getHoraActual()
        // INSERTA CABECERA
        val values = ContentValues()
        values.put("COD_EMPRESA", codEmpresa)
        values.put("COD_PROMOTOR", ListaClientes.codVendedor)
        values.put("COD_CLIENTE", ListaClientes.codCliente)
        values.put("COD_SUBCLIENTE", ListaClientes.codSubcliente)
        values.put("ESTADO", "P")
        values.put("FECHA", fecEntrada)
        values.put("TIPO", "S")
        values.put("LATITUD", ubicacion.latitud)
        values.put("LONGITUD", ubicacion.longitud)
        MainActivity2.bd!!.insert("vt_marcacion_ubicacion", null, values)
    }

    fun enviarPendientesDiaAnterior(){
        val fecha = funcion.getFechaActual()
        val sql = "select * from vt_marcacion_visita where FECHA not like '$fecha%' and ESTADO = 'P' "
        val ca = funcion.consultar(sql)
        for (i in 0 until ca.count){
            noVenta = "'${funcion.dato(ca,"COD_EMPRESA")}'," + "'" + funcion.dato(ca,"COD_SUCURSAL") + "'," +
                    "'" + funcion.dato(ca,"COD_CLIENTE")+
                    "','" + funcion.dato(ca,"COD_SUBCLIENTE") +
                    "','" + funcion.dato(ca,"COD_VENDEDOR")+ "'," +
                    "'" + funcion.dato(ca,"COD_MOTIVO") + "'," +
                    "'" + funcion.dato(ca,"OBSERVACION") + "'," +
                    "to_date('" + funcion.dato(ca,"FECHA") + "','DD/MM/YYYY')," +
                    "to_date('" + funcion.dato(ca,"FECHA") +
                    " " + funcion.dato(ca,"HORA_ALTA") + "','dd/MM/yyyy hh24:mi:ss')," +
                    "'" + funcion.dato(ca,"LATITUD") + "','" + funcion.dato(ca,"LONGITUD") + "'"
            ListaClientes.codVendedor   = funcion.dato(ca,"COD_PROMOTOR")
            ListaClientes.codCliente    = funcion.dato(ca,"COD_CLIENTE")
            ListaClientes.codSubcliente = funcion.dato(ca,"COD_SUBCLIENTE")
            id = funcion.dato(ca,"id")
            try {
                enviarAnteriores()
                ListaClientes.codVendedor   = ""
                ListaClientes.codCliente    = ""
                ListaClientes.codSubcliente = ""
                ca.moveToNext()
                id = funcion.dato(ca,"id")
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun enviarAnteriores(){
        resultado = MainActivity2.conexionWS.procesaNoVenta(noVenta,ListaClientes.codVendedor, codEmpresa)
        if (resultado.indexOf("01*") >= 0) {
            val values: ContentValues?
            values = ContentValues()
            values.put("ESTADO", "E")
            try {
                MainActivity2.bd!!.update(
                    "vt_marcacion_visita", values,
                    " COD_CLIENTE = '" + ListaClientes.codCliente + "'" +
                            "  and COD_SUBCLIENTE = '" + ListaClientes.codSubcliente + "'" +
                            "  and id = '" + id + "'",
                    null
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

}