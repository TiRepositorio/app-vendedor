package apolo.vendedores.com.reportes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_ventas_por_cliente.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class VentasPorCliente : AppCompatActivity() {

    lateinit var funcion: FuncionesUtiles
    lateinit var adapter: Adapter.AdapterGenericoCabecera
    var totSurtido: Double = 0.0
    var mix4: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas_por_cliente)
        inicializarElementos()
    }

    fun inicializarElementos() {
        funcion = FuncionesUtiles(this, imgTitulo, tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        funcion.cargarTitulo(R.drawable.ic_mapa, "Ventas por cliente")
        tvMes1.setText(funcion.getMes(funcion.getMes() - 1))
        tvMes2.setText(funcion.getMes(funcion.getMes()))
        tvMes1.setBackgroundResource(R.drawable.border_textviews)
        tvMes2.setBackgroundResource(R.drawable.border_textviews)
        cargarDatos()
    }

    fun cargarDatos() {
        var sql =
            ("select DESC_VENDEDOR    , CODIGO         , NOM_SUBCLIENTE  , CIUDAD        , COD_SUPERVISOR , DESC_SUPERVISOR ,"
                    + "       LISTA_PRECIO     , MAYOR_VENTA    , VENTA_3         , MIX_3         , VENTA_4        , MIX_4           ,"
                    + "       METAS            , MES_1          , MES_2           , TOT_SURTIDO "
                    + "  FROM svm_metas_punto_por_cliente  "
                    + " ORDER BY CAST (CODIGO AS DOUBLE) ")
        FuncionesUtiles.listaCabecera = ArrayList<HashMap<String, String>>()
        funcion.cargarLista(FuncionesUtiles.listaCabecera, funcion.consultar(sql))
        totSurtido = 0.0
        for (i in 0 until FuncionesUtiles.listaCabecera.size) {
            FuncionesUtiles.listaCabecera.get(i).put(
                "MAYOR_VENTA",
                funcion.entero(FuncionesUtiles.listaCabecera.get(i).get("MAYOR_VENTA").toString())
            )
            FuncionesUtiles.listaCabecera.get(i).put(
                "VENTA_3",
                funcion.entero(FuncionesUtiles.listaCabecera.get(i).get("VENTA_3").toString())
            )
            FuncionesUtiles.listaCabecera.get(i).put(
                "MIX_3",
                funcion.entero(FuncionesUtiles.listaCabecera.get(i).get("MIX_3").toString())
            )
            FuncionesUtiles.listaCabecera.get(i).put(
                "VENTA_4",
                funcion.entero(FuncionesUtiles.listaCabecera.get(i).get("VENTA_4").toString())
            )
            var mix = FuncionesUtiles.listaCabecera.get(i).get("MIX_4").toString().replace(".", "")
                .toInt()
            mix4 += FuncionesUtiles.listaCabecera.get(i).get("VENTA_4").toString().replace(".", "")
                .toDouble()
            FuncionesUtiles.listaCabecera.get(i).put("MIX_4", funcion.entero(mix))
            totSurtido += FuncionesUtiles.listaCabecera.get(i).get("TOT_SURTIDO").toString()
                .replace(",", ".").toDouble()
            FuncionesUtiles.listaCabecera.get(i).put(
                "TOT_SURTIDO",
                funcion.decimal(
                    mix * 100 / FuncionesUtiles.listaCabecera.get(i).get("TOT_SURTIDO").toString()
                        .replace(",", ".").toDouble()
                )
            )
        }
        mostrar()
    }

    fun mostrar() {
        FuncionesUtiles.posicionCabecera = 0
        funcion.vistas = intArrayOf(
            R.id.tv1,
            R.id.tv1,
            R.id.tv3,
            R.id.tv4,
            R.id.tv5,
            R.id.tv6,
            R.id.tv7,
            R.id.tv8,
            R.id.tv9,
            R.id.tv10
        )
        funcion.valores = arrayOf(
            "CODIGO", "NOM_SUBCLIENTE", "CIUDAD", "LISTA_PRECIO", "MAYOR_VENTA", "VENTA_3",
            "MIX_3" , "VENTA_4"       , "MIX_4" , "TOT_SURTIDO"
        )
        adapter = Adapter.AdapterGenericoCabecera(
            this
            , FuncionesUtiles.listaCabecera
            , R.layout.rep_ven_cli_lista_ventas_por_cliente
            , funcion.vistas
            , funcion.valores
        )
        lvVentasPorCliente.adapter = adapter
        lvVentasPorCliente.setOnItemClickListener { parent, view, position, id ->
            FuncionesUtiles.posicionCabecera = position
            lvVentasPorCliente.invalidateViews()
        }
        if (FuncionesUtiles.listaCabecera.size > 0) {
            total()
        }
    }

    fun total() {
        val sql =
            (" SELECT  COD_EMPRESA   ,COD_VENDEDOR	    ,DESC_VENDEDOR , PEDIDO_2_ATRAS ,PEDIDO_1_ATRAS   ,TOTAL_PEDIDOS ,TOTAL_FACT  	,"
                    + "         META_VENTA    ,META_LOGRADA     ,PROY_VENTA    , TOTAL_REBOTE   ,TOTAL_DEVOLUCION ,CANT_CLIENTES ,CANT_POSIT    ,"
                    + "         EF_VISITA 	  ,DIA_TRAB	        ,PUNTAJE	   , SURTIDO_EF "
                    + "    FROM svm_evol_diaria_venta  ")
        FuncionesUtiles.listaDetalle = ArrayList<HashMap<String, String>>()
        funcion.cargarLista(FuncionesUtiles.listaDetalle, funcion.consultar(sql))
        var cobertura = 0.0
        if (FuncionesUtiles.listaDetalle.size > 0) {
            val total_clientes: Double =
                FuncionesUtiles.listaDetalle.get(0).get("CANT_CLIENTES").toString().toDouble()
            val clientes_positivados: Double =
                FuncionesUtiles.listaDetalle.get(0).get("CANT_POSIT").toString().toDouble()
            if (total_clientes > 0 && clientes_positivados.toInt() > 0) {
                cobertura = clientes_positivados * 100 / total_clientes
            }
        }
        tvCobertura.setText("Cobertura (" + funcion.decimal(cobertura).toString() + ")")
        tvMayorVenta.setText(funcion.entero(adapter.getTotalEntero("MAYOR_VENTA").toString()))
        tvVenta3.setText(funcion.entero(adapter.getTotalEntero("VENTA_3").toString()))
        tvMix3.setText(funcion.entero(adapter.getTotalEntero("MIX_3").toString()))
        tvVenta4.setText(funcion.entero(adapter.getTotalEntero("VENTA_4").toString()))
        tvMix4.setText(funcion.entero(adapter.getTotalEntero("MIX_4").toString()))
        tvSurtidoEficiente.setText(funcion.decimal(adapter.getTotalEntero("MIX_4") * 100 / totSurtido))
    }
}