package apolo.vendedores.com.ventas.asistencia

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.AdapterView.OnItemClickListener
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.*
import apolo.vendedores.com.ventas.ListaClientes
import kotlinx.android.synthetic.main.activity_marcacion.*
import kotlinx.android.synthetic.main.ven_asis_dialogo_marcacion_cliente.*
import kotlin.math.roundToInt


class Marcacion : AppCompatActivity() {
    companion object{
        lateinit var latitud : String
        lateinit var longitud : String
        lateinit var codCliente : String
        lateinit var codSubcliente : String
        lateinit var descCliente: String
        lateinit var tiempoMin : String
        lateinit var tiempoMax  : String
    }

    private val funcion = FuncionesUtiles(this)
    private val dispositivo = FuncionesDispositivo(this)
    private val ubicacion   = FuncionesUbicacion(this)
    private lateinit var lm : LocationManager
    private lateinit var lm2 : LocationManager
    private lateinit var telMgr : TelephonyManager

    //Dialog Marcacion
    private lateinit var dialogMarcarPresenciaCliente: Dialog
    private lateinit var listaMarcaciones: ArrayList<HashMap<String, String>>
    private lateinit var adaptador: Adapter.AdapterGenericoDetalle2
    private var saveMarcacion = 0

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcacion)
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm2 = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (!validacion("Abrir")){
            finish()
        }
        inicializaElementos()
        cargarMarcaciones()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun validacion(trigger: String) : Boolean {
        if (!ubicacion.validaUbicacionSimulada(lm)) { return false }
        if (!dispositivo.horaAutomatica()) { return false }
        if (!dispositivo.modoAvion()){ return false }
        if (!dispositivo.zonaHoraria()){ return false }
        if (!dispositivo.fechaCorrecta()){ return false }
        if (!dispositivo.tarjetaSim(telMgr)){ return false }
        if (!ubicacion.ubicacionActivada(lm)){
            ubicacion.latitud  = ""
            ubicacion.longitud = ""
            val configurarUbicacion = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(configurarUbicacion)
            return false
        }
        ubicacion.latitud  = ""
        ubicacion.longitud = ""
        ubicacion.obtenerUbicacion(lm,lm2)
        if (latitud.trim() == "" || longitud.trim() == "") {
            // ABRIR EL MAPA
            Mapa.modificarCliente = true
            Mapa.codCliente = codCliente
            Mapa.codSubcliente = codSubcliente
            Mapa.codVendedor = ListaClientes.codVendedor
            startActivity(Intent(this, Mapa::class.java))
            finish()
            return false
        } else {
            if (ubicacion.latitud.trim() == "" || ubicacion.longitud.trim() == "") {
                funcion.toast(this, "No se encuentra la ubicacion GPS del telefono")
                return false
            }
            val distanciaCliente : Double = ubicacion.calculaDistanciaCoordenadas(
                ubicacion.latitud.toDouble(),
                latitud.toDouble(),
                ubicacion.longitud.toDouble(),
                longitud.toDouble()
            )
            if (distanciaCliente > funcion.getRangoDistancia()) {
                if(verificaMarcacionCliente()){
                    funcion.toast(
                        this,
                        "No se encuentra en el cliente. Se encuentra a " + distanciaCliente.roundToInt() + " m."
                    )
                    if (trigger.trim() == "Abrir"){
                        return false
                    }
                    val autorizacion = DialogoAutorizacion(this)
                        autorizacion.dialogoAutorizacion(trigger, accion)
                }else{
                    funcion.toast(
                        this,
                        "No se encuentra en el cliente. Se encuentra a ${distanciaCliente.roundToInt()} m."
                    )
                }
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun inicializaElementos(){
        ListaClientes.indPresencial = "S"
        inicializar()
    }

    private fun verificaMarcacionCliente(): Boolean {
        var estado = false
        val sql : String = ("Select COD_CLIENTE, COD_SUBCLIENTE, TIPO 	"
                + "  from vt_marcacion_ubicacion             			"
                + " where TIPO           IN ('E','S')                 	"
                + "   and COD_CLIENTE    = '" + codCliente      + "' "
                + "   and COD_SUBCLIENTE = '" + codSubcliente   + "' "
                + " order by id desc                        		 ")
        val cursor: Cursor = funcion.consultar(sql)
        cursor.moveToFirst()
        if (cursor.count > 0) {
            if (cursor.getString(cursor.getColumnIndex("TIPO")) == "E") {
                estado = true
            }
        }
        return estado
    }

    private fun validaVisitaCliente(): Boolean {
        var sql = ("Select *" + " from vt_pedidos_cab "
                + " where COD_CLIENTE = '" + ListaClientes.codCliente + "'"
                + " and COD_SUBCLIENTE = '" + ListaClientes.codSubcliente + "'"
                + " and FEC_ALTA = '" + funcion.getFechaActual() + "'"
                + " and ESTADO IN ('P','E')")
        var cursor: Cursor = funcion.consultar(sql)
        var nreg = cursor.count
        return if (nreg == 0) {
            sql = ("Select *" + " from vt_marcacion_visita "
                    + " where COD_VENDEDOR   = '" + ListaClientes.codVendedor + "' "
                    + "   and COD_CLIENTE    = '" + ListaClientes.codCliente + "'"
                    + "   and COD_SUBCLIENTE = '" + ListaClientes.codSubcliente + "'"
                    + "   and FECHA = '" + funcion.getFechaActual() + "'"
                    + "   and ESTADO IN ('P','E')")
            cursor = funcion.consultar(sql)
            nreg = cursor.count
            if (nreg == 0) {
                funcion.toast(this, "Debe realizar una venta o justificar la no venta a este cliente")
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun inicializar() {
        /*VALIDAR EL GPS DEL CELULAR*/
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val activarGPS = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(activarGPS)
            return
        }
        inicializaETAccion(accion)
        cargarMarcaciones()
        tvCliente.text = "$codCliente-$codSubcliente $descCliente"
        tvTiempoMin.text = "$tiempoMin min."
        tvTiempoMax.text = "$tiempoMax min."
        btnEnviar.setOnClickListener { enviar() }
        btnCancelar.setOnClickListener { finish() }
        ibtnAgregar.setOnClickListener{ agregar() }
        ibtnEliminar.setOnClickListener{

            // ELIMINAR MARCACION ****************************************************************
//				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(List_clientes.this);
//				myAlertDialog.setMessage("¿Desea cancelar esta visita?");
//				myAlertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface arg0, int arg1) {
//						if (saveMarcacion > id.length - 1) {
//							return;
//						}
//
//						if (saveMarcacion > 0) {
//							Toast.makeText(List_clientes.this, "Solo se puede eliminar la ultima marcacion", Toast.LENGTH_SHORT).show();
//							return;
//						}
//
//
//						String sel = "Select ESTADO from vt_marcacion_ubicacion where id = '" + id[saveMarcacion] + "'";
//						Cursor cursor = MenuCombinado.bdatos.rawQuery(sel, null);
//
//						cursor.moveToFirst();
//						int nreg = cursor.getCount();
//
//						if (nreg > 0) {
//							String estado = cursor.getString(cursor.getColumnIndex("ESTADO"));
//
//							if (estado.equals("P")) {
//								String update = "delete from vt_marcacion_ubicacion where id = '" + id[saveMarcacion] + "'";
//								MenuCombinado.bdatos.execSQL(update);
//								cargarMarcaciones();
//							}
//						}
//					}
//				});
//				myAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface arg0, int arg1) {
//
//					}
//				});
//				myAlertDialog.show();
        }

    }

    private fun cargarMarcaciones() {
        val cursor: Cursor
        val select =
            ("Select a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, case when a.TIPO = 'E' then 'ENTRADA' else 'SALIDA' end TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, b.DESC_SUBCLIENTE  "
                    + "  from vt_marcacion_ubicacion a,"
                    + "		  svm_cliente_vendedor b "
                    + "  where a.COD_CLIENTE    = '$codCliente'"
                    + "    and a.COD_SUBCLIENTE = '$codSubcliente'"
                    + "    and a.COD_CLIENTE    = b.COD_CLIENTE"
                    + "    and a.COD_SUBCLIENTE = b.COD_SUBCLIENTE "
                    + "    and ( a.TIPO = 'E' or a.TIPO = 'S' ) "
                    + "    and a.FECHA like '%${funcion.getFechaActual()}%' "
                    + "  group by a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, b.DESC_SUBCLIENTE "
                    + "  order by a.id desc ")
        cursor = funcion.consultar(select)
        listaMarcaciones = ArrayList()
        funcion.cargarLista(listaMarcaciones, cursor)
        mostrar()
    }

    fun mostrar(){
        funcion.vistas = intArrayOf(R.id.td1, R.id.td2, R.id.td3)
        funcion.valores = arrayOf("FECHA", "TIPO", "ESTADO")
        adaptador = Adapter.AdapterGenericoDetalle2(
            this,
            listaMarcaciones,
            R.layout.ven_asis_lv_lista_marcaciones,
            funcion.vistas,
            funcion.valores
        )
        lvListaMarcaciones.adapter = adaptador
        lvListaMarcaciones.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            saveMarcacion = position
            FuncionesUtiles.posicionDetalle2 = position
            lvListaMarcaciones.invalidateViews()
        }
    }

    private fun validaMarcacionPendiente():Boolean{
        val select = ("Select COD_CLIENTE, COD_SUBCLIENTE, FECHA, TIPO "
                + "  from vt_marcacion_ubicacion "
                + "  where TIPO in ('E','S')"
                + "  order by id desc ")
        val cursor: Cursor = funcion.consultar(select)
        if (cursor.count > 0) {
            val codCliente = funcion.dato(cursor, "COD_CLIENTE")
            val codSubcliente = funcion.dato(cursor, "COD_SUBCLIENTE")
            if (funcion.dato(cursor, "TIPO") != "S") {
                Toast.makeText(
                    this,
                    "Debe marcar la salida del cliente $codCliente - $codSubcliente",
                    Toast.LENGTH_SHORT
                ).show()
                dialogMarcarPresenciaCliente.chkEntrada.isChecked = false
                return false
            }
        }
        return true
    }

    private fun marcar(cb: CheckBox){
        val fecha: String = funcion.getFechaActual() + " " + funcion.getHoraActual()
        val tipo = if (cb.id == dialogMarcarPresenciaCliente.chkEntrada.id) { "E" } else { "S" }
        cb.text = fecha
        dialogMarcarPresenciaCliente.chkSalida.isEnabled = cb.id == dialogMarcarPresenciaCliente.chkEntrada.id
        //INSERTA CABECERA
        val values = ContentValues()
        values.put("COD_EMPRESA", "1")
        values.put("COD_PROMOTOR", ListaClientes.codVendedor)
        values.put("COD_CLIENTE", codCliente)
        values.put("COD_SUBCLIENTE", codSubcliente)
        values.put("ESTADO", "P")
        values.put("FECHA", fecha)
        values.put("TIPO", tipo)
        values.put("LATITUD", ubicacion.latitud)
        values.put("LONGITUD", ubicacion.longitud)
        funcion.insertar("vt_marcacion_ubicacion", values)
    }

    private fun desmarcar(cb: CheckBox){
        val tipo = if (cb.id == dialogMarcarPresenciaCliente.chkEntrada.id) { "E" } else { "S" }
        val select = (" SELECT id, COD_EMPRESA, COD_PROMOTOR, COD_CLIENTE, COD_SUBCLIENTE, "
                + "   ESTADO, FECHA, TIPO, LATITUD, LONGITUD "
                + " FROM vt_marcacion_ubicacion  "
                + " WHERE COD_CLIENTE       = '" + codCliente       + "'"
                + "   AND COD_SUBCLIENTE    = '" + codSubcliente    + "'"
                + "   AND TIPO = '$tipo' "
                + " ORDER BY id DESC ")
        val cursor: Cursor = funcion.consultar(select)
        if (cursor.count > 0)
        {
            cb.text = ""
            dialogMarcarPresenciaCliente.chkSalida.isEnabled = cb.id == dialogMarcarPresenciaCliente.chkSalida.id
            val id = funcion.dato(cursor, "id")
            val update = "delete from vt_marcacion_ubicacion where id = $id"
            funcion.ejecutar(update, this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun marcarEntrada(cb: CheckBox){
        if (cb.isChecked)
        {
            if (!validacion("Entrada")){
                cb.isChecked =  false
//                return
            }
            if (!validaMarcacionPendiente()){
                cb.isChecked =  false
                return
            }
            marcar(cb)
        }
        else {
            desmarcar(cb)
        }
        cargarMarcaciones()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun marcarSalida(cb: CheckBox){
        if (cb.isChecked) {
            if (!validacion("Salida")){
                cb.isChecked =  false
                return
            }
            if (!validaEntrada()){
                cb.isChecked =  false
                return
            }
            if (!validaVisitaCliente()){
                cb.isChecked =  false
                return
            }
            marcar(cb)
        } else {
            desmarcar(cb)
        }
        cargarMarcaciones()
    }

    private fun validaEntrada() : Boolean{
        val select = ("Select FECHA, TIPO "
                + "  from vt_marcacion_ubicacion "
                + "  where COD_CLIENTE 	  = '" + codCliente+ "'"
                + "    and COD_SUBCLIENTE = '" + codSubcliente + "'"
                + "    and TIPO in ('S','E')"
                + "  order by id desc ")
        val cursor: Cursor = funcion.consultar(select)
        if (cursor.count == 0) {
            funcion.toast(this, "Debe marcar la entrada al cliente")
            dialogMarcarPresenciaCliente.chkSalida.isChecked = false
            return false
        } else {
            if (funcion.dato(cursor, "TIPO") != "E") {
                funcion.toast(this, "Debe marcar la entrada al cliente")
                dialogMarcarPresenciaCliente.chkSalida.isChecked = false
                return false
            }
            try {
                val diff = funcion.tiempoTranscurrido(
                    funcion.dato(cursor, "FECHA"),
                    funcion.getFechaHoraActual()
                )
                val minLapso: Double = tiempoMin.toDouble()
                if (diff < minLapso) {
                    funcion.toast(
                        this,
                        "DEBE PERMANECER UN MINIMO DE $tiempoMin min. EN EL CLIENTE"
                    )
                    dialogMarcarPresenciaCliente.chkSalida.isChecked = false
                    return false
                }
            } catch (e: Exception) {
                // Toast.makeText(List_clientes.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun agregar(){
        try {
            dialogMarcarPresenciaCliente.dismiss()
        } catch (e: Exception) {
        }
        dialogMarcarPresenciaCliente = Dialog(this)
        dialogMarcarPresenciaCliente.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogMarcarPresenciaCliente.setContentView(R.layout.ven_asis_dialogo_marcacion_cliente)
        dialogMarcarPresenciaCliente.btnVolver.setOnClickListener {
            dialogMarcarPresenciaCliente.dismiss()
        }
        val sql = ("Select DISTINCT a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, "
                + "a.ESTADO, a.LATITUD, a.LONGITUD, b.DESC_SUBCLIENTE  "
                + "  from vt_marcacion_ubicacion a,"
                + "		  svm_cliente_vendedor b "
                + "  where a.COD_EMPRESA    = b.COD_EMPRESA "
                + "    and a.COD_CLIENTE    = '" + codCliente + "'"
                + "    and a.COD_SUBCLIENTE = '" + codSubcliente + "'"
                + "    and a.COD_CLIENTE    = b.COD_CLIENTE  "
                + "    and a.COD_SUBCLIENTE = b.COD_SUBCLIENTE "
                + "    and ( a.TIPO = 'E' or a.TIPO = 'S' ) "
                + "  GROUP BY a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, "
                + "           a.ESTADO, a.LATITUD, a.LONGITUD, b.DESC_SUBCLIENTE  "
                + "  order by a.id desc ")
        val cursor: Cursor = funcion.consultar(sql)
        val cod = "$codCliente - $codSubcliente"
        val desc = descCliente
        if (cursor.count == 0) {
            dialogMarcarPresenciaCliente.chkSalida.isChecked = false
            dialogMarcarPresenciaCliente.chkEntrada.isChecked = false
            dialogMarcarPresenciaCliente.chkSalida.isEnabled = false
            dialogMarcarPresenciaCliente.chkEntrada.isEnabled = true
        } else {
            if (cursor.count % 2 == 0){
                dialogMarcarPresenciaCliente.chkSalida.isChecked = false
                dialogMarcarPresenciaCliente.chkEntrada.isChecked = false
                dialogMarcarPresenciaCliente.chkSalida.isEnabled = false
                dialogMarcarPresenciaCliente.chkEntrada.isEnabled = true
            } else {
                dialogMarcarPresenciaCliente.chkEntrada.isChecked = true
                dialogMarcarPresenciaCliente.chkEntrada.isEnabled = false
                dialogMarcarPresenciaCliente.chkEntrada.text = cursor.getString(
                    cursor.getColumnIndex(
                        "FECHA"
                    )
                )
                dialogMarcarPresenciaCliente.chkSalida.isEnabled = true
                dialogMarcarPresenciaCliente.chkSalida.isChecked = false
            }
        }
        dialogMarcarPresenciaCliente.tvCodCliente.text = cod
        dialogMarcarPresenciaCliente.tvDescCliente.text = desc
        dialogMarcarPresenciaCliente.chkEntrada.setOnClickListener {
            if (!validacion("PRE-ENTRADA")){
                return@setOnClickListener
            }
            marcarEntrada(dialogMarcarPresenciaCliente.chkEntrada)
        }
        dialogMarcarPresenciaCliente.chkSalida.setOnClickListener{
            if (!validacion("PRE-SALIDA")){
                dialogMarcarPresenciaCliente.chkSalida.isChecked = false
                return@setOnClickListener
            }
            marcarSalida(dialogMarcarPresenciaCliente.chkSalida)
        }
        dialogMarcarPresenciaCliente.setCanceledOnTouchOutside(false)
        dialogMarcarPresenciaCliente.show()
    }

    private fun enviar(){
        val enviar = EnviarMarcacion(codCliente, codSubcliente)
        EnviarMarcacion.contexto = this
        EnviarMarcacion.etAccion = accion
        EnviarMarcacion.accion = "cargarMarcaciones"
        enviar.enviar()
    }

    private fun inicializaETAccion(et: EditText){
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "cargarMarcaciones") {
                    cargarMarcaciones()
                    et.setText("")
                }
                if (s.toString() == "Abrir") {
                    et.setText("")
                }
                if (s.toString() == "noAbrir") {
                    finish()
                }
                if (s.toString() == "Entrada") {
                    marcar(dialogMarcarPresenciaCliente.chkEntrada)
                    et.setText("")
                }
                if (s.toString() == "noEntrada") {
                    et.setText("")
                }
                if (s.toString() == "Salida") {
                    marcar(dialogMarcarPresenciaCliente.chkSalida)
                    et.setText("")
                }
                if (s.toString() == "noSalida") {
                    et.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}