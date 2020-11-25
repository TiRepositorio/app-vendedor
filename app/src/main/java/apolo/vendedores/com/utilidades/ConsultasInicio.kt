package apolo.vendedores.com.utilidades

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import apolo.vendedores.com.R
import apolo.vendedores.com.informes.EvolucionDiariaDeVentas

class ConsultasInicio(var context: Context) {

    var funcion : FuncionesUtiles = FuncionesUtiles(context)

    fun evolucionDiariaDeVenta(lista:ListView){
        val sql = ("SELECT a.COD_EMPRESA  , a.COD_VENDEDOR	    , b.DESC_VENDEDOR, a.PEDIDO_2_ATRAS, a.PEDIDO_1_ATRAS,"
                + "           a.TOTAL_PEDIDOS, a.TOTAL_FACT  	    , a.META_VENTA   , a.META_LOGRADA  , a.PROY_VENTA    ,"
                + "		      a.TOTAL_REBOTE , a.TOTAL_DEVOLUCION   , a.CANT_CLIENTES, a.CANT_POSIT    , a.EF_VISITA 	   ,"
                + "		      a.DIA_TRAB	   , a.PUNTAJE			, a.SURTIDO_EF   , "
                + "           IFNULL(((CAST(a.CANT_POSIT AS DOUBLE)/CAST(a.CANT_CLIENTES AS DOUBLE))*100),0.0) COBERTURA "
                + "      FROM svm_evol_diaria_venta a, svm_vendedor_pedido b  "
                + "     WHERE a.COD_EMPRESA  = b.COD_EMPRESA   "
                + "       AND a.COD_VENDEDOR = b.COD_VENDEDOR  "
                + "")
        try {
            EvolucionDiariaDeVentas.cursor = funcion.consultar(sql)
            EvolucionDiariaDeVentas.cursor.moveToFirst()
        } catch (e : Exception){
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,funcion.consultar(sql))
        mostrar(lista)
    }

    fun mostrar(lvEvolucionDiariaDeVentas:ListView){
        EvolucionDiariaDeVentas.valores = arrayOf("COD_VENDEDOR"    ,"DESC_VENDEDOR"    ,"PEDIDO_2_ATRAS"   ,
            "PEDIDO_1_ATRAS"  ,"TOTAL_PEDIDOS"    ,"TOTAL_FACT"       ,
            "META_VENTA"      ,"META_LOGRADA"     ,"PROY_VENTA"       ,
            "TOTAL_REBOTE"    ,"TOTAL_DEVOLUCION" ,"CANT_CLIENTES"    ,
            "CANT_POSIT"      ,"COBERTURA"        ,"EF_VISITA"        ,
            "DIA_TRAB"        ,"PUNTAJE"          ,"SURTIDO_EF"
        )
        EvolucionDiariaDeVentas.vistas = intArrayOf( R.id.tv1 ,
            R.id.tv2 ,
            R.id.tv3 ,
            R.id.tv4 ,
            R.id.tv5 ,
            R.id.tv6 ,
            R.id.tv7 ,
            R.id.tv8 ,
            R.id.tv9 ,
            R.id.tv10,
            R.id.tv11,
            R.id.tv12,
            R.id.tv13,
            R.id.tv14,
            R.id.tv15,
            R.id.tv16,
            R.id.tv17,
            R.id.tv18)
        val adapter: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(context,
            FuncionesUtiles.listaCabecera, R.layout.inf_evo_dia_lista_evolucion,
            EvolucionDiariaDeVentas.vistas,
            EvolucionDiariaDeVentas.valores
        )

        lvEvolucionDiariaDeVentas.adapter = adapter
        lvEvolucionDiariaDeVentas.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvEvolucionDiariaDeVentas.invalidateViews()
        }
    }

}