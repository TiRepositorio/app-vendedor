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
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
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
        var codEmpresa = ""
    }

    private val funcion = FuncionesUtiles(this)
    private val dispositivo = FuncionesDispositivo(this)
    private val ubicacion   = FuncionesUbicacion(this)
    private lateinit var lm : LocationManager
    private lateinit var lm2 : LocationManager
    private lateinit var telMgr : TelephonyManager
    private lateinit var progressDialog: ProgressDialog
    private lateinit var thread: Thread

    //Dialog Marcacion
    private lateinit var dialogMarcarPresenciaCliente: Dialog
    private lateinit var listaMarcaciones: ArrayList<HashMap<String, String>>
    private lateinit var adaptador: Adapter.AdapterGenericoDetalle2
    private var saveMarcacion = 0
    private var autorizacion = ""

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcacion)
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm2 = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (!validacion("Abrir")){
            if (verificaMarcacionCliente()){
//                val dialogoAutorizacion = DialogoAutorizacion(this)
//                dialogoAutorizacion.dialogoAutorizacion("Abrir","noAbrir",accion,"NO SE ENCUENTRA EN EL CLIENTE.")
            } else {
                finish()
            }
        }
        inicializaElementos()
        cargarMarcaciones()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun validacion(trigger: String) : Boolean {
        if (!ubicacion.validaUbicacionSimulada(lm)) {
            EnviarMarcacion.anomalia = "\nAtención! La ubicación simulada fue utilizada en otra aplicación."
            return false
        }

        var aplicacionBloqueador = dispositivo.aplicacionBloqueada()
        if (aplicacionBloqueador != "") {
            //funcion.toast(this, "La aplicacion $aplicacionBloqueador se encuentra en conflicto con la aplicacion de vendedor" )
            funcion.mensaje(this,"Atencion", "La aplicacion $aplicacionBloqueador se encuentra en conflicto con la aplicacion de vendedor" )
            return false

        }

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
        ubicacion.obtenerUbicacion(lm, lm2)
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
                    if (trigger.trim() == "PRE-SALIDA" || trigger.trim() == "PRE-ENTRADA"){
                        val autorizacionDialogo = DialogoAutorizacion(this)
                        autorizacionDialogo.dialogoAutorizacionCod(trigger, accion)
                        return false
                    }
                    if (trigger.trim() == "Entrada" || trigger.trim() == "Salida"){
                        return false
                    }
                    return true
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
        autorizacion = ""
        funcion.ejecutar(
            "update vt_marcacion_ubicacion set COD_EMPRESA = TRIM(COD_EMPRESA)," +
                    " COD_PROMOTOR = TRIM(COD_PROMOTOR), COD_CLIENTE = TRIM(COD_CLIENTE), COD_SUBCLIENTE = TRIM(COD_SUBCLIENTE)," +
                    " TIPO = TRIM(TIPO), ESTADO = TRIM(ESTADO)", this
        )
//        if (verificaMarcacionClienteEmergencia()){
//            funcion.ejecutar(
//                "insert into vt_marcacion_ubicacion " +
//                        "(COD_EMPRESA, COD_PROMOTOR, COD_CLIENTE, COD_SUBCLIENTE, TIPO, ESTADO, FECHA, LATITUD, LONGITUD) VALUES " +
//                        "('1','6601', '757', '1', 'S','P','05/05/2021 10:23:36','-24.976981','-54.910484')",
//                this
//            )
//        }
        ListaClientes.indPresencial = "S"
        inicializar()
    }

    private fun verificaMarcacionCliente(): Boolean {
        var estado = false
        val sql : String = ("Select COD_CLIENTE, COD_SUBCLIENTE, TIPO 	"
                + "  from vt_marcacion_ubicacion             			"
                + " where TRIM(TIPO)           IN ('E','S')             "
                + "   and TRIM(COD_CLIENTE)    = '" + codCliente.trim()      + "' "
                + "   and TRIM(COD_SUBCLIENTE) = '" + codSubcliente.trim()   + "' "
                + "   and COD_EMPRESA          = '$codEmpresa' "
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
    /*private fun verificaMarcacionClienteEmergencia(): Boolean {
        var estado = false
        val sql : String = ("Select COD_CLIENTE, COD_SUBCLIENTE, TIPO 	"
                + "  from vt_marcacion_ubicacion             			"
                + " where TRIM(TIPO)           IN ('E','S')             "
                + "   and TRIM(COD_CLIENTE)    = '757' "
                + "   and TRIM(COD_SUBCLIENTE) = '1' "
                + "   and FECHA LIKE '05/05/2021%' "
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
*/
    private fun validaVisitaCliente(): Boolean {
        var sql = ("Select *" + " from vt_pedidos_cab "
                + " where TRIM(COD_CLIENTE) = '" + ListaClientes.codCliente.trim() + "'"
                + " and TRIM(COD_SUBCLIENTE) = '" + ListaClientes.codSubcliente.trim() + "'"
                + " and TRIM(FEC_ALTA) = '" + funcion.getFechaActual().trim() + "'"
                + " and TRIM(COD_EMPRESA) = '$codEmpresa' "
                + " and TRIM(ESTADO) IN ('P','E')")
        var cursor: Cursor = funcion.consultar(sql)
        var nreg = cursor.count
        return if (nreg == 0) {
            sql = ("Select *" + " from vt_marcacion_visita "
                    + " where TRIM(COD_VENDEDOR)   = '" + ListaClientes.codVendedor.trim() + "' "
                    + "   and TRIM(COD_CLIENTE)    = '" + ListaClientes.codCliente.trim() + "'"
                    + "   and TRIM(COD_SUBCLIENTE) = '" + ListaClientes.codSubcliente.trim() + "'"
                    + "   and TRIM(FECHA) = '" + funcion.getFechaActual().trim() + "'"
                    + "   and TRIM(COD_EMPRESA) = '$codEmpresa'"
                    + "   and TRIM(ESTADO) IN ('P','E')")
            cursor = funcion.consultar(sql)
            nreg = cursor.count
            if (nreg == 0) {
                funcion.toast(
                    this,
                    "Debe realizar una venta o justificar la no venta a este cliente"
                )
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
//        btnEnviar.setOnClickListener { enviar() }
        btnEnviar.setOnClickListener { enviarMarcacion() }
        btnCancelar.setOnClickListener { finish() }
        ibtnAgregar.setOnClickListener{ agregar() }
        ibtnEliminar.setOnClickListener{

            /*// ELIMINAR MARCACION ****************************************************************
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
//				myAlertDialog.show();*/

        }

    }

    private fun cargarMarcaciones() {
        val cursor: Cursor
        val select =
            ("Select a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, case when a.TIPO = 'E' then 'ENTRADA' else 'SALIDA' end TIPO, a.ESTADO, a.LATITUD, a.LONGITUD, b.DESC_SUBCLIENTE  "
                    + "  from vt_marcacion_ubicacion a,"
                    + "		  svm_cliente_vendedor b "
                    + "  where TRIM(a.COD_CLIENTE)    = '${codCliente.trim()}'"
                    + "    and TRIM(a.COD_SUBCLIENTE) = '${codSubcliente.trim()}'"
                    + "    and TRIM(a.COD_CLIENTE)    = TRIM(b.COD_CLIENTE)"
                    + "    and TRIM(a.COD_SUBCLIENTE) = TRIM(b.COD_SUBCLIENTE) "
                    + "    and TRIM(a.COD_EMPRESA)    = TRIM(b.COD_EMPRESA) "
                    + "    and TRIM(a.COD_EMPRESA)    = '$codEmpresa' "
                    + "    and ( TRIM(a.TIPO) = 'E' or TRIM(a.TIPO) = 'S' ) "
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
                + "  where TRIM(TIPO) in ('E','S') "
                + "    and FECHA LIKE '${funcion.getFechaActual()}%' "
                + "    AND COD_EMPRESA = '$codEmpresa' "
                + "    and trim(COD_CLIENTE)||'-'||trim(COD_SUBCLIENTE) <> '${codCliente.trim()}-${codSubcliente.trim()}' "
                + "  order by id desc ")
        val cursor: Cursor = funcion.consultar(select)
        val cantidad = cursor.count%2 == 0
        if (cursor.count > 0) {
            val codCliente = funcion.dato(cursor, "COD_CLIENTE")
            val codSubcliente = funcion.dato(cursor, "COD_SUBCLIENTE")
            if (funcion.dato(cursor, "TIPO").trim() != "S" && !cantidad) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun marcar(cb: CheckBox){



        var estadoSim = false

        try {
            var fechaOnline = funcion.obtenerHoraActualDeInternet()
            estadoSim = true
        } catch (e: Exception) {
            estadoSim = dispositivo.validaEstadoSim(telMgr);
        }



        var paquetesUbicacionSimulada = ""
        try{
            paquetesUbicacionSimulada = dispositivo.getAppsForMockLocation(this)
        } catch (e: Exception) {
        }




        val fecha: String = funcion.getFechaActual() + " " + funcion.getHoraActual()
        val tipo = if (cb.id == dialogMarcarPresenciaCliente.chkEntrada.id) { "E" } else { "S" }
        cb.text = if(autorizacion != ""){getHoraDeEntrada()} else {fecha}
        dialogMarcarPresenciaCliente.chkSalida.isEnabled = cb.id == dialogMarcarPresenciaCliente.chkEntrada.id
        //INSERTA CABECERA
        val values = ContentValues()
        values.put("COD_EMPRESA", codEmpresa)
        values.put("COD_PROMOTOR", ListaClientes.codVendedor.trim())
        values.put("COD_CLIENTE", codCliente.trim())
        values.put("COD_SUBCLIENTE", codSubcliente.trim())
        values.put("ESTADO", "P")
        values.put("TIPO", tipo.trim())
        values.put("LATITUD", ubicacion.latitud)
        values.put("LONGITUD", ubicacion.longitud)
        var observacion = ""

        if (autorizacion != ""){
            values.put("FECHA", getHoraDeEntrada())
            observacion = "Autorizacion: $autorizacion. Version de Sistema: ${MainActivity.version}.${MainActivity.fechaVersion}"
        } else {
            values.put("FECHA", fecha)
        }
        if (!estadoSim) {
            observacion = "$observacion . El chip no se encuentra habilitado o no posee señal"
        }

        if (paquetesUbicacionSimulada != "") {
            observacion = "$observacion . $paquetesUbicacionSimulada"
        }

        values.put("OBSERVACION", observacion)


        funcion.insertar("vt_marcacion_ubicacion", values)
    }

    private fun desmarcar(cb: CheckBox){
        val tipo = if (cb.id == dialogMarcarPresenciaCliente.chkEntrada.id) { "E" } else { "S" }
        val select = (" SELECT id, COD_EMPRESA, COD_PROMOTOR, COD_CLIENTE, COD_SUBCLIENTE, "
                + "   ESTADO, FECHA, TIPO, LATITUD, LONGITUD "
                + " FROM vt_marcacion_ubicacion  "
                + " WHERE TRIM(COD_CLIENTE)       = '" + codCliente.trim()       + "'"
                + "   AND TRIM(COD_SUBCLIENTE)    = '" + codSubcliente.trim()    + "'"
                + "   AND TRIM(TIPO) = '${tipo.trim()}' "
                + "   AND COD_EMPRESA = '$codEmpresa' "
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

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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

    private fun getHoraDeEntrada(): String {
        val sql = ("Select COD_CLIENTE, COD_SUBCLIENTE, TIPO, FECHA    	"
                + "  from vt_marcacion_ubicacion             			"
                + " where TIPO           = ('E')                 	    "
                + "   and COD_CLIENTE    = '" + codCliente + "' "
                + "   and COD_SUBCLIENTE = '" + codSubcliente + "' "
                + "   AND COD_EMPRESA    = '$codEmpresa' "
                + " order by id desc                        				")
        val cursor: Cursor = funcion.consultar(sql)
        cursor.moveToFirst()
        val nreg = cursor.count
        if (nreg > 0) {
            return funcion.dato(cursor,"FECHA")
        }
        return funcion.getFechaHoraActual()
    }

    private fun validaEntrada() : Boolean{
        val select = ("Select FECHA, TIPO "
                + "  from vt_marcacion_ubicacion "
                + "  where TRIM(COD_CLIENTE) 	  = '" + codCliente.trim()+ "'"
                + "    and TRIM(COD_SUBCLIENTE) = '" + codSubcliente.trim() + "'"
                + "    and TRIM(TIPO) in ('S','E') "
                + "    AND COD_EMPRESA = '$codEmpresa' "
                + "  order by id desc ")
        val cursor: Cursor = funcion.consultar(select)
        if (cursor.count == 0) {
            funcion.toast(this, "Debe marcar la entrada al cliente")
            dialogMarcarPresenciaCliente.chkSalida.isChecked = false
            return false
        } else {
            if (funcion.dato(cursor, "TIPO").trim() != "E") {
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
                + "  where TRIM(a.COD_EMPRESA)    = TRIM(b.COD_EMPRESA) "
                + "    and TRIM(a.COD_CLIENTE)    = '" + codCliente.trim() + "'"
                + "    and TRIM(a.COD_SUBCLIENTE) = '" + codSubcliente.trim() + "'"
                + "    and TRIM(a.COD_CLIENTE)    = TRIM(b.COD_CLIENTE)  "
                + "    and TRIM(a.COD_SUBCLIENTE) = TRIM(b.COD_SUBCLIENTE) "
                + "    and ( TRIM(a.TIPO) = 'E' or TRIM(a.TIPO) = 'S' ) "
                + "    AND a.COD_EMPRESA          = '$codEmpresa' "
                + "    and FECHA LIKE '%${funcion.getFechaActual()}%' "
                + "  GROUP BY a.COD_CLIENTE, a.COD_SUBCLIENTE, a.FECHA, a.COD_PROMOTOR, a.TIPO, "
                + "           a.ESTADO, a.LATITUD, a.LONGITUD, b.DESC_SUBCLIENTE  "
                + "  order by a.id desc, cast(FECHA AS DATE) DESC")
        val cursor: Cursor = funcion.consultar(sql)
        val cod = "$codCliente - $codSubcliente"
        val desc = descCliente
        if (cursor.count == 0) {
            dialogMarcarPresenciaCliente.chkSalida.isChecked = false
            dialogMarcarPresenciaCliente.chkEntrada.isChecked = false
            dialogMarcarPresenciaCliente.chkSalida.isEnabled = false
            dialogMarcarPresenciaCliente.chkEntrada.isEnabled = true
        } else {
            cursor.moveToFirst()
            if (cursor.count % 2 == 0 && funcion.dato(cursor, "TIPO").trim() == "S"){
                dialogMarcarPresenciaCliente.chkSalida.isChecked = false
                dialogMarcarPresenciaCliente.chkEntrada.isChecked = false
                dialogMarcarPresenciaCliente.chkSalida.isEnabled = false
                dialogMarcarPresenciaCliente.chkEntrada.isEnabled = true
            } else {
                cursor.moveToFirst()
                if (funcion.dato(cursor, "TIPO").trim() == "E"){
                    dialogMarcarPresenciaCliente.chkEntrada.isChecked = true
                    dialogMarcarPresenciaCliente.chkEntrada.isEnabled = false
                    dialogMarcarPresenciaCliente.chkEntrada.text = cursor.getString(
                        cursor.getColumnIndex("FECHA")
                    )
                    dialogMarcarPresenciaCliente.chkSalida.isEnabled = true
                    dialogMarcarPresenciaCliente.chkSalida.isChecked = false
                }
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

    /*private fun enviar(){
        val enviar = EnviarMarcacion(codCliente, codSubcliente)
        EnviarMarcacion.contexto = this
        EnviarMarcacion.etAccion = accion
        EnviarMarcacion.accion = "cargarMarcaciones"
//        enviar.enviar()
    }*/

    private fun enviarMarcacion(){
        thread = Thread{
            progressDialog = ProgressDialog(this)
            runOnUiThread { progressDialog.cargarDialogo("Enviando marcaciones...",false) }
            val enviar = EnviarMarcacion(codCliente, codSubcliente)
            EnviarMarcacion.contexto = this
            EnviarMarcacion.etAccion = accion
            EnviarMarcacion.accion = "cargarMarcaciones"
            if (!enviar.enviar()){
                runOnUiThread {
                    funcion.toast(this,EnviarMarcacion.resultado)
                    progressDialog.cerrarDialogo()
                }
                return@Thread
            }
            try {
                EnviarMarcacion.resultado = if (EnviarMarcacion.dia == "HOY"){
                    MainActivity2.conexionWS.procesaMarcacionAsistenciaAct(FuncionesUtiles.usuario["LOGIN"].toString(),
                        EnviarMarcacion.cadena,
                        FuncionesUtiles.usuario["COD_EMPRESA"].toString()
                    )
                } else {
                    MainActivity2.conexionWS.procesaMarcacionAsistencia(ListaClientes.codVendedor,
                        EnviarMarcacion.cadena,
                        FuncionesUtiles.usuario["COD_EMPRESA"].toString()
                    )
                }
//                resultado = "01*GRABADO CON EXITO"
            } catch (e: Exception) {
                EnviarMarcacion.resultado = e.message.toString()
            }
            val mensaje: Array<String> = EnviarMarcacion.resultado.split("*").toTypedArray()
            if (mensaje.size == 1) {
                runOnUiThread{
                    progressDialog.cerrarDialogo()
                    funcion.mensaje(EnviarMarcacion.contexto,"Resultado",EnviarMarcacion.resultado)
                }
            } else {
                if (mensaje[0] == "01") {
                    val update = if (EnviarMarcacion.dia == "HOY"){
                        (" UPDATE vt_marcacion_ubicacion SET ESTADO = 'E' " +
                                "  WHERE ESTADO = 'P'" +
                                "    AND COD_EMPRESA = '$codEmpresa' " +
                                "    AND FECHA like '%" + MainActivity2.funcion.getFechaActual() + "%'")
                    } else {
                        ("update vt_marcacion_ubicacion set ESTADO = 'E' " +
                                " where ESTADO         = 'P' " +
                                "   AND COD_EMPRESA = '$codEmpresa' " +
                                "   and COD_PROMOTOR   = '" + ListaClientes.codVendedor + "'" +
                                "   and COD_CLIENTE    = '" + EnviarMarcacion.stCodCliente + "'" +
                                "   and COD_SUBCLIENTE = '" + EnviarMarcacion.stCodSubcliente + "'" +
                                "   and TIPO in ('E','S') ")
                    }
                    runOnUiThread { funcion.ejecutar(update, EnviarMarcacion.contexto) }
                    EnviarMarcacion.anomalia = ""
                }
                if ( mensaje[0]=="01"){
                    mensaje[1]="Datos enviados con éxito."
                }
                runOnUiThread {
                    funcion.mensaje(EnviarMarcacion.contexto,"Resultado", mensaje[1])
                    progressDialog.cerrarDialogo()
                }
            }
            runOnUiThread { EnviarMarcacion.etAccion.setText(EnviarMarcacion.accion) }
        }
        thread.start()
    }

    private fun inicializaETAccion(et: EditText){
        et.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n", "NewApi")
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "cargarMarcaciones") {
                    cargarMarcaciones()
                    et.setText("")
                }
                if (s.toString().split("*")[0].trim() == "Abrir") {
                    et.setText("")
                }
                if (s.toString().split("*")[0].trim() == "noAbrir") {
                    finish()
                }
                if (s.toString().split("*")[0].trim() == "Entrada") {
                    dialogMarcarPresenciaCliente.chkEntrada.isChecked = true
                    marcar(dialogMarcarPresenciaCliente.chkEntrada)
                    et.setText("")
                }
                if (s.toString().split("*")[0].trim() == "noEntrada") {
                    et.setText("")
                }
                if (s.toString().split("*")[0].trim() == "PRE-SALIDA") {
                    if (s.toString().split("*").size > 1) {
                        autorizacion = s.toString().split("*")[1]
                    }
                    dialogMarcarPresenciaCliente.chkSalida.isChecked = true
                    marcar(dialogMarcarPresenciaCliente.chkSalida)
                    et.setText("cargarMarcaciones")
                    et.setText("")
                    autorizacion = ""
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