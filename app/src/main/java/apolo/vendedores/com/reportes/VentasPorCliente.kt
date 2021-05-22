package apolo.vendedores.com.reportes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_ventas_por_cliente.*
import kotlinx.android.synthetic.main.activity_ventas_por_cliente.barraMenu
import kotlinx.android.synthetic.main.activity_ventas_por_cliente.contMenu
import kotlinx.android.synthetic.main.barra_vendedores.*

class VentasPorCliente : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var funcion: FuncionesUtiles
    lateinit var adapter: Adapter.AdapterGenericoCabecera
    private var totSurtido: Double = 0.0
    private var mix4: Double = 0.0
    private var codVendedor = ""
    private var desVendedor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas_por_cliente)
        inicializarElementos()
    }

    fun inicializarElementos() {
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
        funcion.cargarTitulo(R.drawable.ic_mapa, "Ventas por cliente")
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR","svm_metas_punto_por_cliente")
        barraMenu.setNavigationItemSelectedListener(this)
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        tvMes1.text = funcion.getMes(funcion.getMes() - 1)
        tvMes2.text = funcion.getMes(funcion.getMes())
        tvMes1.setBackgroundResource(R.drawable.border_textviews)
        tvMes2.setBackgroundResource(R.drawable.border_textviews)
        validacion()
        cargarDatos()
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

    private fun cargarDatos() {
        cargarCodigos()
        val sql =
            ("select DESC_VENDEDOR    , CODIGO         , NOM_SUBCLIENTE  , CIUDAD        , COD_SUPERVISOR , DESC_SUPERVISOR ,"
                    + "       LISTA_PRECIO     , MAYOR_VENTA    , VENTA_3         , MIX_3         , VENTA_4        , MIX_4           ,"
                    + "       METAS            , MES_1          , MES_2           "
                    + "  FROM svm_metas_punto_por_cliente  "
                    + " WHERE COD_VENDEDOR = '$codVendedor'  "
                    + " ORDER BY CAST (CODIGO AS DOUBLE) ")
        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaCabecera, funcion.consultar(sql))
        totSurtido = 0.0
        for (i in 0 until FuncionesUtiles.listaCabecera.size) {
            FuncionesUtiles.listaCabecera[i]["MAYOR_VENTA"] =
                funcion.entero(FuncionesUtiles.listaCabecera[i]["MAYOR_VENTA"].toString())
            FuncionesUtiles.listaCabecera[i]["VENTA_3"] =
                funcion.entero(FuncionesUtiles.listaCabecera[i]["VENTA_3"].toString())
            FuncionesUtiles.listaCabecera[i]["MIX_3"] =
                funcion.entero(FuncionesUtiles.listaCabecera[i]["MIX_3"].toString())
            FuncionesUtiles.listaCabecera[i]["VENTA_4"] =
                funcion.entero(FuncionesUtiles.listaCabecera[i]["VENTA_4"].toString())
            val mix = FuncionesUtiles.listaCabecera[i]["MIX_4"].toString().replace(".", "")
                .toInt()
            mix4 += FuncionesUtiles.listaCabecera[i]["VENTA_4"].toString().replace(".", "")
                .toDouble()
            FuncionesUtiles.listaCabecera[i]["MIX_4"] = funcion.entero(mix)
        }
        mostrar()
    }

    fun mostrar() {
        FuncionesUtiles.posicionCabecera = 0
        funcion.vistas = intArrayOf(
            R.id.tv1,
            R.id.tv2,
            R.id.tv3,
            R.id.tv4,
            R.id.tv5,
            R.id.tv6,
            R.id.tv7,
            R.id.tv8,
            R.id.tv9
        )
        funcion.valores = arrayOf(
            "CODIGO"        , "NOM_SUBCLIENTE"  , "CIUDAD"      ,
            "LISTA_PRECIO"  , "MAYOR_VENTA"     , "VENTA_3"     ,
            "MIX_3"         , "VENTA_4"         , "MIX_4"
        )
        adapter = Adapter.AdapterGenericoCabecera(
            this
            , FuncionesUtiles.listaCabecera
            , R.layout.rep_ven_cli_lista_ventas_por_cliente
            , funcion.vistas
            , funcion.valores
        )
        lvVentasPorCliente.adapter = adapter
        lvVentasPorCliente.setOnItemClickListener { _, _, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            lvVentasPorCliente.invalidateViews()
        }
        if (FuncionesUtiles.listaCabecera.size > 0) {
            total()
        }
    }

    @SuppressLint("SetTextI18n")
    fun total() {
        val sql =
            (" SELECT  COD_EMPRESA   ,COD_VENDEDOR	    ,DESC_VENDEDOR , PEDIDO_2_ATRAS ,PEDIDO_1_ATRAS   ,TOTAL_PEDIDOS ,TOTAL_FACT  	,"
                    + "         META_VENTA    ,META_LOGRADA     ,PROY_VENTA    , TOTAL_REBOTE   ,TOTAL_DEVOLUCION ,CANT_CLIENTES ,CANT_POSIT    ,"
                    + "         EF_VISITA 	  ,DIA_TRAB	        ,PUNTAJE	   "
                    + "    FROM svm_evol_diaria_venta  ")
        FuncionesUtiles.listaDetalle = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaDetalle, funcion.consultar(sql))
        var cobertura = 0.0
        if (FuncionesUtiles.listaDetalle.size > 0) {
            val totalClientes: Double =
                FuncionesUtiles.listaDetalle[0]["CANT_CLIENTES"].toString().toDouble()
            val clientesPositivados: Double =
                FuncionesUtiles.listaDetalle[0]["CANT_POSIT"].toString().toDouble()
            if (totalClientes > 0 && clientesPositivados.toInt() > 0) {
                cobertura = clientesPositivados * 100 / totalClientes
            }
        }
        tvCobertura.text = "Cobertura (" + funcion.decimal(cobertura) + ")"
        tvMayorVenta.text = funcion.entero(adapter.getTotalEntero("MAYOR_VENTA").toString())
        tvVenta3.text = funcion.entero(adapter.getTotalEntero("VENTA_3").toString())
        tvMix3.text = funcion.entero(adapter.getTotalEntero("MIX_3").toString())
        tvVenta4.text = funcion.entero(adapter.getTotalEntero("VENTA_4").toString())
        tvMix4.text = funcion.entero(adapter.getTotalEntero("MIX_4").toString())
    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarDatos()
            mostrar()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarDatos()
        mostrar()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }
}