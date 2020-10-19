package apolo.vendedores.com.ventas.sd

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.*
import apolo.vendedores.com.ventas.ListaClientes
import kotlinx.android.synthetic.main.activity_solicitud_devolucion.*
import kotlinx.android.synthetic.main.activity_solicitud_devolucion.accion
import kotlinx.android.synthetic.main.sd_detalle.*
import kotlinx.android.synthetic.main.sd_enviados.*
import kotlinx.android.synthetic.main.sd_productos.*


class SolicitudDevolucion : AppCompatActivity() {

    lateinit var funcion : FuncionesUtiles
    private lateinit var dispositivo: FuncionesDispositivo
    private lateinit var listaProductos :  ArrayList<HashMap<String,String>>
    lateinit var listaDetalles  :  ArrayList<HashMap<String,String>>
    private lateinit var listaEnviados  :  ArrayList<HashMap<String,String>>
    private lateinit var fspMotivo : FuncionesSpinner
    private lateinit var fspUM : FuncionesSpinner
    private var posProducto : Int = 0

    companion object{
        var posDetalles : Int = 0
        var posEnviados : Int = 0
        lateinit var etAccion : EditText
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud_devolucion)

        inicializarProductos()
    }

    private fun inicializarProductos(){
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,spBuscar,etBuscar,btBuscar)
        funcion.addItemSpinner(this,"Codigo-Descripcion-Cod. Barra","a.COD_ARTICULO-a.DESC_ARTICULO-b.COD_BARRA")
        funcion.cargarTitulo(R.drawable.ic_sd,"Solicitud de devolución")
        dispositivo = FuncionesDispositivo(this)
        btBuscar.setOnClickListener{buscar()}
        cargarProductos()
        mostrarProductos()
        motivos()
        inicializarEtNumerico(etDetCantidad)
        cargarCliente()
        btAgregar.setOnClickListener{registrarSDDetalle()}
        btEnviar.setOnClickListener{enviarSD()}
        inicializaETAccion(accion)
        inicializaETCodCliente(etCodCliente)
    }

    @SuppressLint("SetTextI18n")
    fun cargarCliente(){
        etCodCliente.setText(ListaClientes.codCliente + "-" + ListaClientes.codSubcliente)
        etDescCliente.setText(ListaClientes.descCliente)
    }

    fun mostrarContenido(view: View) {
        tvProductos.setBackgroundColor(Color.parseColor("#757575"))
        tvDetalle.setBackgroundColor(Color.parseColor("#757575"))
        tvEnviados.setBackgroundColor(Color.parseColor("#757575"))
        view.setBackgroundColor(Color.parseColor("#00574B"))
        llProductos.visibility = View.GONE
        llDetalle.visibility   = View.GONE
        llEnviados.visibility  = View.GONE
        when(view.id){
            tvProductos.id  -> llProductos.visibility = View.VISIBLE
            tvDetalle.id    -> llDetalle.visibility   = View.VISIBLE
            tvEnviados.id   -> llEnviados.visibility  = View.VISIBLE
        }
    }

    private fun inicializaETAccion(etAccion: EditText){
        etAccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etAccion.text.toString() == "CANTIDAD"){
                    etAccion.setText("")
                }
                if (etAccion.text.toString().trim() == "eliminarDetalle"){
                    eliminarDetalle(listaDetalles[posDetalles]["id"].toString().trim())
                    etAccion.setText("")
                }
                if (etAccion.text.toString().trim() == "SALIR"){
                    eliminarTodoDetalle()
                    finish()
                }
                if (etAccion.text.toString().trim() == "ACTUALIZAR"){
                    etAccion.setText("")
                    cargarEnviado()
                    cargarDetalle()
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

    //Producto
    private fun cargarProductos(){
        val sql : String = (" SELECT 	a.id,a.COD_EMPRESA	,a.COD_ARTICULO	,a.DESC_ARTICULO	, "
                    + " 		a.COD_UNIDAD_REL	,a.REFERENCIA	,a.IND_BASICO		, "
                    + " 		b.COD_BARRA "
                    + " FROM svm_st_articulos a, svm_articulos_precios b "
                    + " WHERE IND_BASICO = 'S' "
                    + "   and a.COD_EMPRESA = b.COD_EMPRESA "
                    + "   and a.COD_ARTICULO = b.COD_ARTICULO "
                    + " GROUP BY a.id,a.COD_EMPRESA	,a.COD_ARTICULO	,a.DESC_ARTICULO	, "
                    + " 		 a.COD_UNIDAD_REL	,a.REFERENCIA	,a.IND_BASICO		, "
                    + "			 b.COD_BARRA "
                    + " ORDER BY a.DESC_ARTICULO asc ")
        listaProductos = ArrayList()
        funcion.cargarLista(listaProductos,funcion.consultar(sql))
    }

    private fun buscar(){
        val campos: String = " a.id,a.COD_EMPRESA,a.COD_ARTICULO,a.DESC_ARTICULO, " +
                     " a.COD_UNIDAD_REL,a.REFERENCIA,a.IND_BASICO, " +
                     " b.COD_BARRA "
        val groupBy : String = campos
        val orderBy = " a.DESC_ARTICULO asc "
        val tabla   = " svm_st_articulos a, svm_articulos_precios b "
        val where   = " and IND_BASICO = 'S' and a.COD_EMPRESA = b.COD_EMPRESA and a.COD_ARTICULO = b.COD_ARTICULO "
        listaProductos = ArrayList()
        funcion.cargarLista(listaProductos,funcion.buscar(tabla,campos,groupBy,orderBy,where))
        mostrarProductos()
    }

    private fun mostrarProductos(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5)
        funcion.valores = arrayOf("COD_ARTICULO", "DESC_ARTICULO", "COD_UNIDAD_REL", "REFERENCIA", "COD_BARRA")
        val adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(this
                ,listaProductos
                ,R.layout.ven_lista_sd_productos
                ,funcion.vistas
                ,funcion.valores)
        lvProductos.adapter = adapter
        lvProductos.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            posProducto = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            etDetCodArticulo.setText(listaProductos[posProducto]["COD_ARTICULO"])
            unidadDeMedida()
            etDetCantidad.setText("0")
            lvProductos.invalidateViews()
        }
        if (listaProductos.size>0){
            etDetCodArticulo.setText(listaProductos[posProducto]["COD_ARTICULO"])
            unidadDeMedida()
        }
    }

    private fun motivos(){
        val campos = " COD_EMPRESA,COD_MOTIVO,DESC_MOTIVO "
        val tabla = " svm_motivos_sd_dev "
        val where = " COD_EMPRESA = '1' "
        val whereOpcional = ""
        val group = ""
        val order = ""
        fspMotivo = FuncionesSpinner(this,spMtivo)
        fspMotivo.generaSpinner(campos,tabla,where,whereOpcional,group,order,"DESC_MOTIVO","")
    }

    private fun unidadDeMedida(){
        val campos = " DESC_ARTICULO,COD_UNIDAD_REL,REFERENCIA "
        val tabla = " svm_st_articulos "
        val where : String = " COD_ARTICULO = '" + etDetCodArticulo.text.toString().trim() + "' "
        val whereOpcional = ""
        val group = ""
        val order = ""
        fspUM = FuncionesSpinner(this,spUM)
        fspUM.generaSpinner(campos,tabla,where,whereOpcional,group,order,"REFERENCIA","")
    }

    private fun inicializarEtNumerico(etNumerico:EditText){
        etNumerico.setOnClickListener{
            funcion.dialogoEntradaNumero(etNumerico,this)
        }
    }

    private fun inicializaETCodCliente(et: EditText){
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et.text.isNotEmpty()){
                    cargarDetalle()
                    cargarEnviado()
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

    private fun registrarSDDetalle(): Boolean {
        val resultado: Boolean
        val orden: String = (funcion.ultimoNroOrden("svm_solicitud_dev_det") + 1).toString() + ""
        MainActivity.bd!!.beginTransaction()

        val misValues = ContentValues()
        misValues.put("NRO_ORDEN", orden)
        misValues.put("COD_EMPRESA", listaProductos[posProducto]["COD_EMPRESA"])
        misValues.put("NRO_PLANILLA", ListaClientes.codVendedor)
        misValues.put("COD_VENDEDOR", ListaClientes.codVendedor)
        misValues.put("COD_CLIENTE", etCodCliente.text.toString().split("-")[0].trim())
        misValues.put("COD_SUBCLIENTE", etCodCliente.text.toString().split("-")[1].trim())
        misValues.put("NRO_REGISTRO_REF", "0")
        misValues.put("COD_ARTICULO", listaProductos[posProducto]["COD_ARTICULO"])
        misValues.put("DESC_ARTICULO", listaProductos[posProducto]["DESC_ARTICULO"])
        misValues.put("CANTIDAD", etDetCantidad.text.toString())
        misValues.put("COD_UNIDAD_REL", fspUM.getDato("COD_UNIDAD_REL"))
        misValues.put("REFERENCIA", fspUM.getDato("REFERENCIA"))
        misValues.put("COD_PENALIDAD", fspMotivo.getDato("COD_MOTIVO"))
        misValues.put("GRABADO_CAB", "N")
        misValues.put("EST_ENVIO", "N")
        misValues.put("PAGO", "0")
        misValues.put("MONTO", "0")
        misValues.put("TOTAL", "0")
        misValues.put("FECHA", funcion.getFechaActual())
        if (!validarRegistro()){
            return false
        }
        resultado = try {
            val cColumnas: String = (" NRO_ORDEN,COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,COD_CLIENTE,COD_SUBCLIENTE,NRO_REGISTRO_REF,COD_ARTICULO,"
                    + " DESC_ARTICULO, CANTIDAD,PAGO,MONTO,TOTAL,COD_UNIDAD_REL,REFERENCIA,COD_PENALIDAD,GRABADO_CAB,"
                    + " EST_ENVIO, FECHA")
            funcion.insertar("svm_solicitud_dev_det", cColumnas, misValues)
            MainActivity.bd!!.setTransactionSuccessful()
            MainActivity.bd!!.endTransaction()
            true
        } catch (e: Exception) {
            false
        }
        cargarDetalle()
        return resultado
    }

    private fun validarRegistro():Boolean{
        if (listaProductos.size==0){
            return false
        }
        if (etDetCantidad.text.toString().trim() == "0"){
            funcion.mensaje(this,"Atención!","La cantidad debe ser mayor a 0.")
            return false
        }

        if (!verificarExistencia()){
            funcion.mensaje(this,"Atención!","El producto que intenta agregar ya existe en la nota.")
            return false
        }
        if (cantidadDeRegistros()>10){
            funcion.mensaje(this,"Atención!","La nota ya posee la cantidad máxima de item permitida por nota.")
            return false
        }
        if (!dispositivo.horaAutomatica()){
            return false
        }
        return true
    }

    private fun verificarExistencia():Boolean{
        val sql : String = ("SELECT COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,COD_CLIENTE,COD_SUBCLIENTE "
                         + "   FROM svm_solicitud_dev_det "
                         + "  WHERE NRO_PLANILLA 	= '${ListaClientes.codVendedor}' "
                         + "    AND COD_SUBCLIENTE	= '" + etCodCliente.text.toString().split("-")[1].trim() + "' "
                         + "    AND COD_CLIENTE 	= '" + etCodCliente.text.toString().split("-")[0].trim() + "' "
                         + "    AND COD_ARTICULO	= '" + listaProductos[posProducto]["COD_ARTICULO"] + "' "
                         + "    AND EST_ENVIO		= 'N'"
                         + "    AND COD_UNIDAD_REL 	= '" + fspUM.getDato("COD_UNIDAD_REL") + "' ")
        return funcion.consultar(sql).count <= 0
    }

    private fun cantidadDeRegistros():Int{
        val sql : String = ("SELECT COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,COD_CLIENTE,COD_SUBCLIENTE "
                + "  FROM svm_solicitud_dev_det "
                + " WHERE NRO_PLANILLA 	= '${ListaClientes.codVendedor}' "
                + "   AND COD_SUBCLIENTE	= '" + etCodCliente.text.toString().split("-")[1].trim() + "' "
                + "   AND COD_CLIENTE 	= '" + etCodCliente.text.toString().split("-")[0].trim() + "' "
                + "   AND EST_ENVIO		= 'N'")
        return try {
            funcion.consultar(sql).count
        } catch (e : java.lang.Exception) {
            0
        }
    }

    private fun enviarSD(){
        EnviarSD.context = this
        EnviarSD.codCliente = etCodCliente.text.split("-")[0].trim()
        EnviarSD.codSubcliente = etCodCliente.text.split("-")[1].trim()
        etAccion = accion
        val enviarSD = EnviarSD()
        enviarSD.enviar()
    }

    //Detalle
    private fun cargarDetalle() {
        listaDetalles = ArrayList()

        val sql = ("select id,(NRO_ORDEN*1) NRO_ORDEN,COD_EMPRESA,COD_ARTICULO,DESC_ARTICULO,"
                        + " COD_UNIDAD_REL,CANTIDAD,PAGO,MONTO,REFERENCIA "
                        + " FROM svm_solicitud_dev_det where"
                        + " COD_CLIENTE		= '" + etCodCliente.text.toString().split("-")[0].trim() + "' and"
                        + " COD_SUBCLIENTE	= '" + etCodCliente.text.toString().split("-")[1].trim() + "' and"
                        + " NRO_PLANILLA	= '${ListaClientes.codVendedor}' and"
                        + " EST_ENVIO 		= 'N' "
                        + " ORDER BY NRO_ORDEN")
        funcion.cargarLista(listaDetalles,funcion.consultar(sql))
        mostrarDetalles()
    }

    private fun mostrarDetalles(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores = arrayOf("NRO_ORDEN"       ,"COD_ARTICULO" ,"DESC_ARTICULO",
                                  "COD_UNIDAD_REL"  ,"CANTIDAD"     ,"PAGO"         ,"MONTO")
        val adapter: Adapter.AdapterSDDetalle =
            Adapter.AdapterSDDetalle(this
                ,listaDetalles
                ,R.layout.ven_lista_sd_detalles
                ,funcion.vistas
                ,funcion.valores
                ,"eliminarDetalle"
                ,accion)
        lvDetalle.adapter = adapter
        lvDetalle.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            posDetalles = position
            lvDetalle.invalidateViews()
        }
    }

    private fun eliminarDetalle(id:String){
        val sql = "DELETE FROM svm_solicitud_dev_det WHERE id = $id "
        funcion.ejecutar(sql,this)
        funcion.recalcularNroOrden("svm_solicitud_dev_det",this)
        cargarDetalle()
    }

    private fun eliminarTodoDetalle(){
        funcion.ejecutar("DELETE FROM svm_solicitud_dev_det WHERE EST_ENVIO = 'N' ",this)
        funcion.ejecutar("DELETE FROM svm_solicitud_dev_cab WHERE EST_ENVIO = 'N' ",this)
    }

    private fun registrosPendientes():Boolean{
        val sql = "SELECT COD_EMPRESA,NRO_PLANILLA,COD_VENDEDOR,COD_CLIENTE,COD_SUBCLIENTE FROM svm_solicitud_dev_det WHERE EST_ENVIO	= 'N'"
        if (funcion.consultar(sql).count>0){
            return true
        }
        return false
    }

    override fun onBackPressed() {
        if (registrosPendientes()){
            val mensaje = "Al salir se perderán las solicitudes pendientes de envio.\n¿Deseas continuar?"
            val dialogo = DialogoAutorizacion(this)
            dialogo.dialogoAccionOpcion("SALIR","",accion,mensaje,"Atención!","SI","NO")
        } else {
            finish()
        }
    }

    //Enviados
    private fun cargarEnviado() {
        listaEnviados = ArrayList()

        val sql = ("select id,COD_EMPRESA,COD_CLIENTE,COD_ARTICULO,DESC_ARTICULO,"
                + " COD_UNIDAD_REL,CANTIDAD,REFERENCIA,NRO_REGISTRO_REF "
                + " FROM svm_solicitud_dev_det where"
                + " COD_CLIENTE		= '" + etCodCliente.text.toString().split("-")[0].trim() + "' and"
                + " COD_SUBCLIENTE	= '" + etCodCliente.text.toString().split("-")[1].trim() + "' and"
                + " NRO_PLANILLA	= '${ListaClientes.codVendedor}' and"
                + " EST_ENVIO 		= 'S' "
                + " ORDER BY NRO_ORDEN")
        funcion.cargarLista(listaEnviados,funcion.consultar(sql))
        mostrarEnviados()
    }

    private fun mostrarEnviados(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6)
        funcion.valores = arrayOf("NRO_REGISTRO_REF","COD_CLIENTE","COD_ARTICULO", "DESC_ARTICULO", "COD_UNIDAD_REL","CANTIDAD")
        val adapter: Adapter.AdapterSDEnviado =
            Adapter.AdapterSDEnviado(this
                ,listaEnviados
                ,R.layout.ven_lista_sd_enviados
                ,funcion.vistas
                ,funcion.valores)
        lvEnviado.adapter = adapter
        lvEnviado.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            posEnviados = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvEnviado.invalidateViews()
        }
    }

}