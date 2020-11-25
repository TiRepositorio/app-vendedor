package apolo.vendedores.com.informes

import android.annotation.SuppressLint
import apolo.vendedores.com.R
import android.app.Activity
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.view.GravityCompat
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_estado_de_pedidos.*
import kotlinx.android.synthetic.main.activity_estado_de_pedidos.barraMenu
import kotlinx.android.synthetic.main.activity_estado_de_pedidos.contMenu
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EstadoDePedidos : Activity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        this.tvVendedor.text = menuItem.title.toString()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    lateinit var lista: ArrayList<HashMap<String, String>> 
    lateinit var cursor: Cursor
    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    private var codVendedor = ""
    private var desVendedor = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_estado_de_pedidos)
        inicializarElementos()
    }
    
    fun inicializarElementos(){
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        funcion.cargarTitulo(R.drawable.ic_check,"Estado de pedidos")
        funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_listado_pedidos"),this)
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR", "ven_svm_listado_pedidos")
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        validacion()
        cargar()
        mostrar()
    }

    private fun validacion(){
        if (tvVendedor!!.text.toString() == "Nombre del vendedor"){
            funcion.toast(this, "No hay datos para mostrar.")
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cargarCodigos(){
        try {
            codVendedor = tvVendedor.text!!.toString().split("-")[0]
            desVendedor = tvVendedor.text!!.toString().split("-")[1]
        } catch (e: java.lang.Exception){tvVendedor.text = "Nombre del vendedor"}
        if (tvVendedor.text.toString().split("-").size>2){
            desVendedor = tvVendedor.text!!.toString()
            while (desVendedor.indexOf("-") != 0){
                desVendedor = desVendedor.substring(1, desVendedor.length)
            }
            desVendedor = desVendedor.substring(1, desVendedor.length)
        }
//        funcion.mensaje(this,codVendedor,desVendedor)
    }
    private fun cargar(){
        cargarCodigos()
        lista = ArrayList()
        try {
            val sql = ("select COD_EMPRESA	, NRO_PEDIDO    	, FEC_COMPROBANTE ,"
                    + "COD_CLIENTE	|| '-' || COD_SUBCLIENTE AS CLIENTE , NOM_SUBCLIENTE  ,"
                    + "SIGLAS 		, DECIMALES 	, TOT_COMPROBANTE   , OBSERVACIONES    "
                    + "  from svm_listado_pedidos  "
                    + " WHERE COD_VENDEDOR = '$codVendedor'  "
                    + " Order By  Cast (NRO_PEDIDO as double) ")
            cursor = funcion.consultar(sql)
        } catch (e: Exception) {
        }
        funcion.cargarLista(lista,cursor)
        for (i in 0 until lista.size){
            lista[i]["TOT_COMPROBANTE"] = funcion.entero(lista[i]["TOT_COMPROBANTE"]!!)
        }
    }

    fun mostrar(){
        funcion.valores = arrayOf("NRO_PEDIDO"      , "CLIENTE"         , "NOM_SUBCLIENTE"  ,
                                  "FEC_COMPROBANTE" , "SIGLAS"          , "TOT_COMPROBANTE" ,
                                  "OBSERVACIONES")
        funcion.vistas  = intArrayOf(R.id.td1, R.id.td2, R.id.td3, R.id.td4, R.id.td5, R.id.td6, R.id.td7)
        adapter = Adapter.AdapterGenericoCabecera(this,lista,R.layout.inf_est_ped_lista_estado_de_pedidos,funcion.vistas,funcion.valores)
        lvEstadoDePedidos.adapter = adapter
        lvEstadoDePedidos.setOnItemClickListener { _, _, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            lvEstadoDePedidos.invalidateViews()
        }
    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargar()
            mostrar()
        }
    }

    companion object {
        lateinit var adapter: Adapter.AdapterGenericoCabecera
    }
}