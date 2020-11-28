package apolo.vendedores.com.informes

import apolo.vendedores.com.R
import android.app.Activity
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.os.Bundle
import android.widget.*
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import kotlinx.android.synthetic.main.activity_corte_de_stock.*
import kotlinx.android.synthetic.main.barra_vendedores.*
import java.util.*
import kotlin.collections.ArrayList

class CorteDeStock : Activity() {
    lateinit var lista: ArrayList<HashMap<String, String>>
    lateinit var cursor: Cursor
    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    private var codVendedor = ""
    private var desVendedor = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_corte_de_stock)

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion = FuncionesUtiles(
            imgTitulo,
            tvTitulo,
            ibtnAnterior,
            ibtnSiguiente,
            tvVendedor,
            contMenu,
            barraMenu,
            llBotonVendedores
        )
        funcion.cargarTitulo(R.drawable.ic_check,"Corte de logistica")
        funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_pedidos_sin_stock_rep"),this)
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR", "ven_svm_pedidos_sin_stock_rep")
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
            val sql = ("select COD_EMPRESA     , ORIGEN      , DESC_DEPOSITO   ,"
                    + "FEC_COMPROBANTE , COD_CLIENTE , CLIENTE         ,"
                    + "VENDEDOR        , SUPERVISOR  , COD_ARTICULO    ,"
                    + "ARTICULO        , CANTIDAD    , PRECIO_UNITARIO ,"
                    + "MONTO_TOTAL     , PEDIDO      , DESC_MOTIVO     ,"
                    + "DECIMALES "
                    + " from svm_pedidos_sin_stock_rep "
                    + "where COD_VENDEDOR = '$codVendedor'")
            cursor = funcion.consultar(sql)
        } catch (e: Exception) {
            e.message
        }
        funcion.cargarLista(lista,cursor)
        for (i in 0 until lista.size){
            lista[i]["MONTO_TOTAL"] =
                funcion.decimal(lista[i]["MONTO_TOTAL"]!!, lista[i]["DECIMALES"]!!.toInt())
            lista[i]["PRECIO_UNITARIO"] =
                funcion.decimal(lista[i]["PRECIO_UNITARIO"]!!, lista[i]["DECIMALES"]!!.toInt())
        }
    }

    fun mostrar(){
        funcion.valores = arrayOf(  "FEC_COMPROBANTE"   , "DESC_DEPOSITO"   , "COD_CLIENTE"     ,
                                    "CLIENTE"           , "COD_ARTICULO"    , "ARTICULO"        ,
                                    "PEDIDO"            , "CANTIDAD"        , "PRECIO_UNITARIO" )
        funcion.vistas  = intArrayOf(R.id.td1, R.id.td2, R.id.td3, R.id.td4, R.id.td5, R.id.td6, R.id.td7, R.id.td8, R.id.td9)
        adapter = Adapter.AdapterGenericoCabecera(this,lista,R.layout.inf_cor_sto_lista_corte_de_stock,funcion.vistas,funcion.valores)
        lvCorteDeStock.adapter = adapter
        lvCorteDeStock.setOnItemClickListener { _, _, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            lvCorteDeStock.invalidateViews()
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
