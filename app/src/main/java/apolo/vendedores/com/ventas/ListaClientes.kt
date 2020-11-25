package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.Mapa
import apolo.vendedores.com.ventas.asistencia.Marcacion
import apolo.vendedores.com.ventas.justificacion.NoVenta
import kotlinx.android.synthetic.main.activity_lista_clientes.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class ListaClientes : AppCompatActivity() {

    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var funcion : FuncionesUtiles
        var codVendedor : String = ""
        var codCliente : String = ""
        var codSubcliente : String = ""
        var descCliente : String = ""
        var tipCliente : String = ""
        var indEspecial : String = ""
        var tipCondicion : String = ""
        var codCondicion : String = ""
        var diasInicial : String = ""
        var codSucursalCliente : String = ""
        var indDirecta : String = ""
        var estado  : String = ""
        var latitud : String = ""
        var longitud: String = ""
        var lista : ArrayList<HashMap<String,String>> = ArrayList()
        var indPresencial = "N"
        var claveAutorizacion = ""
        lateinit var etAccion: EditText
    }


    private lateinit var lm : LocationManager
    private lateinit var telMgr : TelephonyManager

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_clientes)

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //solo con titulo
        funcion = FuncionesUtiles(imgTitulo,tvTitulo)
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,llBuscar,spBuscar,etBuscar,btBuscar)

        inicializarElementos()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun inicializarElementos(){
        funcion.addItemSpinner(this,"Codigo-Nombre-Ciudad","COD_CLIENTE-DESC_SUBCLIENTE-desc_ciudad")
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_persona,"Lista de clientes")
        btBuscar.setOnClickListener{buscar(" AND TRIM(FEC_VISITA) = '' ")}
        btDeuda.setOnClickListener{deuda()}
        ibtnMapa.setOnClickListener{mapa()}
        inicializaETAccion(accion)
        btModificar.setOnClickListener{modificar()}
        btEntradaSalida.setOnClickListener { entradaSalida() }
        btJustificarNoVenta.setOnClickListener { noVenta() }
        btSD.setOnClickListener{sd()}
        btVender.setOnClickListener{vender()}
        buscarRuteo()
        cbNoAtendidos.setOnClickListener{buscarBloqueado()}
        cbRuteoDelDia.setOnClickListener{buscarRuteo()}
        cbTodos.setOnClickListener{buscarTodo()}
    }

    fun buscar(condicion:String){
        val campos = " * "
        val groupBy = ""
        val orderBy = "COD_CLIENTE"
        val tabla = " svm_cliente_vendedor "
//        var where = " AND COD_VENDEDOR = '" + codVendedor +
        val where = " AND COD_VENDEDOR = '$codVendedor' $condicion "
        cargarLista(funcion.buscar(tabla,campos,groupBy,orderBy,where))
        mostrar()
    }

    private fun buscarRuteo(){
        cbTodos.isChecked = false
        cbNoAtendidos.isChecked = false
        if (cbRuteoDelDia.isChecked){
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        } else {
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        }
    }

    private fun buscarBloqueado(){
        cbRuteoDelDia.isChecked = false
        cbTodos.isChecked = false
        if (cbNoAtendidos.isChecked){
            buscar(" AND IND_VISITA = 'B' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        } else {
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        }
    }

    private fun buscarTodo(){
        cbRuteoDelDia.isChecked = false
        cbNoAtendidos.isChecked = false
        if (cbTodos.isChecked){
            buscar(" AND TRIM(FEC_VISITA) = '' ")
        } else {
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        }
    }

    fun cargarLista(cursor: Cursor){
        FuncionesUtiles.listaDetalle = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            for (j in 0 until cursor.columnCount){
                try {
                    datos[cursor.getColumnName(j)] = funcion.dato(cursor,cursor.getColumnName(j))
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }
            datos["LIMITE_CREDITO"] = funcion.enteroCliente(datos["LIMITE_CREDITO"].toString())
            datos["TOT_DEUDA"] = funcion.enteroCliente(datos["TOT_DEUDA"].toString())
            datos["SALDO"] = funcion.enteroCliente(datos["SALDO"].toString())
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.vistas  = intArrayOf(R.id.tv1 ,R.id.tv2 ,R.id.tv3 ,R.id.tv4 ,R.id.tv5 ,R.id.tv6 ,
                                     R.id.tv7 ,R.id.tv8 ,R.id.tv9 ,R.id.tv10,R.id.tv11,R.id.tv12,
                                     R.id.tv13,R.id.tv14,R.id.tv15,R.id.tv16,R.id.tv17 )
        funcion.valores = arrayOf("TIP_CAUSAL"      , "CATEGORIA"       ,"COD_CLIENTE"      , "COD_SUBCLIENTE"  , "TIP_CLIENTE"    ,
                                  "DESC_CLIENTE"    , "DESC_SUBCLIENTE" ,"DESC_CIUDAD"      , "DIRECCION"       , "RUC"            ,
                                  "DESC_ZONA"       , "TELEFONO"        ,"DESC_CONDICION"   , "LIMITE_CREDITO"  , "TOT_DEUDA"      ,
                                  "SALDO"           , "FEC_VISITA"      )
        val adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(this
                ,FuncionesUtiles.listaDetalle
                ,R.layout.ven_cli_lista_clientes
                ,funcion.vistas
                ,funcion.valores)
        lvClientes.adapter = adapter
        lvClientes.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle  = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvClientes.invalidateViews()
            cargarDatos(position)
        }
        if (FuncionesUtiles.listaDetalle.size>0){
            cargarDatos(0)
        }
    }

    private fun cargarDatos(posicion : Int){
        codCliente          = FuncionesUtiles.listaDetalle[posicion]["COD_CLIENTE"].toString()
        codSubcliente       = FuncionesUtiles.listaDetalle[posicion]["COD_SUBCLIENTE"].toString()
        descCliente         = FuncionesUtiles.listaDetalle[posicion]["DESC_SUBCLIENTE"].toString()
        tipCliente          = FuncionesUtiles.listaDetalle[posicion]["TIP_CLIENTE"].toString()
        indEspecial         = FuncionesUtiles.listaDetalle[posicion]["IND_ESP"].toString()
        tipCondicion        = FuncionesUtiles.listaDetalle[posicion]["TIPO_CONDICION"].toString()
        codCondicion        = FuncionesUtiles.listaDetalle[posicion]["COD_CONDICION"].toString()
        diasInicial         = FuncionesUtiles.listaDetalle[posicion]["DIAS_INICIAL"].toString()
        indDirecta          = FuncionesUtiles.listaDetalle[posicion]["IND_DIRECTA"].toString()
        codSucursalCliente  = FuncionesUtiles.listaDetalle[posicion]["COD_SUCURSAL"].toString()
        estado              = FuncionesUtiles.listaDetalle[posicion]["ESTADO"].toString()
        latitud             = FuncionesUtiles.listaDetalle[posicion]["LATITUD"].toString()
        longitud            = FuncionesUtiles.listaDetalle[posicion]["LONGITUD"].toString()
    }

    private fun deuda(){
        if (FuncionesUtiles.listaDetalle.size > 0){
            Deuda.codigo = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["COD_CLIENTE"].toString() + "-" +
                           FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["COD_SUBCLIENTE"].toString()
            Deuda.nombre = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["DESC_CLIENTE"].toString()
            if (FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["TOT_DEUDA"].equals("0")){
                funcion.mensaje(this,"Atención!","El cliente no tiene deudas.")
                return
            }
            val deuda = Intent(this,Deuda::class.java)
            startActivity(deuda)
        }
    }

    fun verCliente(){
        Mapa.lista = ArrayList()
        Mapa.lista.add(FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle])
        Mapa.modificarCliente = false
        val intent = Intent(this,Mapa::class.java)
        startActivity(intent)
    }

    fun verRuteo(){
        Mapa.lista = ArrayList()
        Mapa.lista = FuncionesUtiles.listaDetalle
        Mapa.modificarCliente = false
        val intent = Intent(this,Mapa::class.java)
        startActivity(intent)
    }

    private fun inicializaETAccion(et: EditText){
        etAccion = et
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (et.text.toString() == "cliente"){
                    verCliente()
                    accion.setText("")
                    return
                }
                if (et.text.toString().trim() == "ruteo"){
                    verRuteo()
                    accion.setText("")
                    return
                }
                if (et.text.toString().trim() == "abrirUbicacion"){
                    abrirUbicacion()
                    return
                }
                if (et.text.toString().trim() == "abrirMapa") {
                    startActivity(Intent(this@ListaClientes, Mapa::class.java))
                    return
                }
                if (et.text.toString().trim() == "recargar") {
                    buscar("")
                    return
                }
                if (et.text.toString().trim() == "vender") {
                    Pedidos.nuevo = true
                    startActivity(Intent(this@ListaClientes, Pedidos::class.java))
                    return
                }
                if (et.text.toString().trim() == "venderNoPresencial") {
                    Pedidos.nuevo = true
                    Pedidos.indPresencial = "N"
                    startActivity(Intent(this@ListaClientes, Pedidos::class.java))
                    return
                }
                if (et.text.toString().trim() == "marcacion") {
                    entradaSalida()
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

        })
    }

    private fun mapa(){
        val dialogo = DialogoAutorizacion(this)
        dialogo.dialogoMapa("",accion)
    }

    private fun modificar(){
        if (FuncionesUtiles.listaDetalle.size==0){return}
        ModificarCliente.editable = true
        val modifcar = Intent(this,ModificarCliente::class.java)
        startActivity(modifcar)
    }

    @SuppressLint("SetTextI18n")
    private fun vender(){
        if (FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["TIP_CAUSAL"].toString().trim() == "B"){
            val dialogo = DialogoAutorizacion(this)
            dialogo.dialogoAutorizacion("vender",accion)
            return
        }
        if (validaMarcacionSalida()){
            funcion.toast(this,"Este cliente ya tiene marcación de salida.")
            return
        }
        if (!verificaMarcacionCliente()){
            val dialogo = DialogoAutorizacion(this)
            dialogo.dialogoAccionOpcion("marcacion"
                                       ,"venderNoPresencial",accion
                                       ,"¿Se encuentra en el cliente?",""
                                       ,"SI","NO")
            return
        }
        Pedidos.indPresencial = "S"
        accion.setText("vender")
    }

    private fun sd(){
        val sd = Intent(this,apolo.vendedores.com.ventas.sd.SolicitudDevolucion::class.java)
        startActivity(sd)
    }

    private fun validaMarcacionSalida():Boolean{
        val sql = ("Select id "
                + "  from vt_marcacion_ubicacion "
                + " where COD_CLIENTE    = '" + codCliente + "' "
                + "   and COD_SUBCLIENTE = '" + codSubcliente + "' "
                + "   and TIPO           IN ('S','E')  "
                + "   and FECHA 	     = '" + funcion.getFechaActual() + "'")
        val cursor = funcion.consultar(sql)
        if (cursor.count > 0) {
            if (funcion.dato(cursor, "TIPO") == "S") {
                return true
            }
        } else {
            return false
        }
        return false
    }

    private fun verificaMarcacionCliente(): Boolean {
        val sql : String = ("Select COD_CLIENTE, COD_SUBCLIENTE, TIPO 	"
                         +  "  from vt_marcacion_ubicacion             			"
                         +  " where TIPO           IN ('E')                 	"
                         +  "   and COD_CLIENTE    = '" + codCliente + "' "
                         +  "   and COD_SUBCLIENTE = '" + codSubcliente + "' "
                         +  "   and FECHA          = '${funcion.getFechaActual()}'} "
                         +  " order by id desc     ")
        val cursor: Cursor = funcion.consultar(sql)
        cursor.moveToFirst()
        if (cursor.count > 0) {
            if (funcion.dato(cursor, "TIPO") == "E") {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun noVenta(){
        NoVenta.modificacion = true
        NoVenta.editable     = true
        NoVenta.nuevo        = true
        NoVenta.etAccion     = accion
        NoVenta.context      = this
        val noVenta = NoVenta(codCliente, codSubcliente, lm, telMgr, latitud, longitud)
        noVenta.cargarDialogo()
    }

    private fun entradaSalida(){
        Marcacion.latitud       = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["LATITUD"].toString()
        Marcacion.longitud      = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["LONGITUD"].toString()
        Marcacion.codCliente    = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["COD_CLIENTE"].toString()
        Marcacion.codSubcliente = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["COD_SUBCLIENTE"].toString()
        Marcacion.descCliente   = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["DESC_CLIENTE"].toString()
        Marcacion.tiempoMin     = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["TIEMPO_MIN"].toString()
        Marcacion.tiempoMax     = FuncionesUtiles.listaDetalle[FuncionesUtiles.posicionDetalle]["TIEMPO_MAX"].toString()
        startActivity(Intent(this,Marcacion::class.java))
    }

    private fun abrirUbicacion(){
        val configurarUbicacion = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(configurarUbicacion)
    }


}
