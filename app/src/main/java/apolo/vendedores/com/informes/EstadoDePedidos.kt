package apolo.vendedores.com.informes

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

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_estado_de_pedidos)
        inicializarElementos()
    }
    
    fun inicializarElementos(){
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        funcion.cargarTitulo(R.drawable.ic_check,"Corte de logistica")
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR", "svm_listado_pedidos")
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        cargar()
        mostrar()
    }

    fun cargar(){
        lista = ArrayList<HashMap<String,String>>()
        try {
            val sql = ("select COD_EMPRESA	, NRO_PEDIDO    	, FEC_COMPROBANTE ,"
                    + "COD_CLIENTE	|| '-' || COD_SUBCLIENTE AS CLIENTE , NOM_SUBCLIENTE  ,"
                    + "SIGLAS 		, DECIMALES 	, TOT_COMPROBANTE   , OBSERVACIONES    "
                    + " from svm_listado_pedidos  "
                    + " Order By  Cast (NRO_PEDIDO as double) ")
            cursor = funcion.consultar(sql)
        } catch (e: Exception) {
            var err = e.message
            err = "" + err
        }
        funcion.cargarLista(lista,cursor)
        for (i in 0 until lista.size){
            lista.get(i).put("TOT_COMPROBANTE",funcion.entero(lista.get(i).get("TOT_COMPROBANTE")!!))
        }
    }

    fun mostrar(){
        funcion.valores = arrayOf("NRO_PEDIDO"      , "CLIENTE"         , "NOM_SUBCLIENTE"  ,
                                  "FEC_COMPROBANTE" , "SIGLAS"          , "TOT_COMPROBANTE" ,
                                  "OBSERVACIONES")
        funcion.vistas  = intArrayOf(R.id.td1, R.id.td2, R.id.td3, R.id.td4, R.id.td5, R.id.td6, R.id.td7)
        adapter = Adapter.AdapterGenericoCabecera(this,lista,R.layout.inf_est_ped_lista_estado_de_pedidos,funcion.vistas,funcion.valores)
        lvEstadoDePedidos.adapter = adapter
        lvEstadoDePedidos.setOnItemClickListener { parent, view, position, id ->
            FuncionesUtiles.posicionCabecera = position
            lvEstadoDePedidos.invalidateViews()
        }
    }

    fun actualizarDatos(imageView: ImageView){
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