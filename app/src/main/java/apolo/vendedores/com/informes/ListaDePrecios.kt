package apolo.vendedores.com.informes

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_lista_de_precios.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class ListaDePrecios : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        FuncionesUtiles.posicionCabecera = 0

        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var funcion : FuncionesUtiles
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_de_precios)

        funcion = FuncionesUtiles(
            imgTitulo,
            tvTitulo,
            ibtnAnterior,
            ibtnSiguiente,
            tvVendedor,
            contMenu,
            barraMenu,
            llBuscar,
            spBuscar,
            etBuscar,
            btBuscar,
            llBotonVendedores
        )
        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.addItemSpinner(this,"Codigo-Descripcion","COD_ARTICULO-DESC_ARTICULO")
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_lista,"Lista de precios")
        cargarArticulo()
        mostrarArticulo()
        cargarPrecios()
        mostrarPrecio()
        btBuscar.setOnClickListener{buscar()}
    }

    private fun cargarArticulo(){
        val sql = ("select distinct a.COD_ARTICULO, a.DESC_ARTICULO, a.REFERENCIA "
                + " from svm_articulos_precios a "
                + " inner join svm_precios_fijos b on a.cod_lista_precio = b.cod_lista_precio "
                + " Order By a.DESC_ARTICULO")
        var vista = "drop view if exists lista_de_articulos "
        funcion.ejecutar(vista,this)
        vista = "create view if not exists lista_de_articulos as $sql"
        funcion.ejecutar(vista,this)
        buscar()
    }

    private fun mostrarArticulo(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3)
        funcion.valores = arrayOf("COD_ARTICULO", "DESC_ARTICULO", "REFERENCIA")
        val adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(this
                                            ,FuncionesUtiles.listaCabecera
                                            ,R.layout.inf_lis_prec_lista_articulo
                                            ,funcion.vistas
                                            ,funcion.valores)
        lvArticulos.adapter = adapter
        lvArticulos.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvArticulos.invalidateViews()
            cargarPrecios()
            mostrarPrecio()
            tvdCod.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"]
            tvdRef.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["REFERENCIA"]
            tvdDesc.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_ARTICULO"]
        }
        tvdCod.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"]
        tvdRef.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["REFERENCIA"]
        tvdDesc.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_ARTICULO"]
    }

    private fun cargarPrecios(){
        val sql =
            ("select DISTINCT a.COD_LISTA_PRECIO, b.DESC_LISTA_PRECIO, a.PREC_CAJA, a.PREC_UNID, 'GS.' SIGLAS  "
                    + " from svm_articulos_precios a "
                    + " left join svm_precios_fijos b on a.cod_lista_precio = b.cod_lista_precio "
                    + " where COD_ARTICULO = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"] + "' "
                    + " ORDER BY Cast (a.COD_LISTA_PRECIO as double)")
        FuncionesUtiles.listaDetalle = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaDetalle,funcion.consultar(sql))
        for (i in 0 until FuncionesUtiles.listaDetalle.size){
            try{
                FuncionesUtiles.listaDetalle[i]["PREC_CAJA"] =
                    funcion.entero(FuncionesUtiles.listaDetalle[i]["PREC_CAJA"].toString())
            } catch (e : Exception){}
        }
    }

    private fun mostrarPrecio(){
        funcion.subVistas  = intArrayOf(R.id.tvd1,R.id.tvd2,R.id.tvd3,R.id.tvd4)
        funcion.subValores = arrayOf("COD_LISTA_PRECIO", "DESC_LISTA_PRECIO", "SIGLAS","PREC_CAJA")
        val adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(this
                ,FuncionesUtiles.listaDetalle
                ,R.layout.inf_lis_prec_lista_precio
                ,funcion.subVistas
                ,funcion.subValores)
        lvPrecios.adapter = adapter
        lvPrecios.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPrecios.invalidateViews()
        }
    }

    fun buscar(){
        val campos = "DISTINCT COD_ARTICULO, DESC_ARTICULO, REFERENCIA "
        val groupBy = ""
        val orderBy = "DESC_ARTICULO"
        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,funcion.buscar("lista_de_articulos",campos,groupBy,orderBy))
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        mostrarArticulo()
        cargarPrecios()
        mostrarPrecio()
    }

}
