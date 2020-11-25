package apolo.vendedores.com.informes

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import kotlinx.android.synthetic.main.activity_evolucion_diaria_de_ventas.*

class EvolucionDiariaDeVentas : AppCompatActivity() {

    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
        lateinit var vistas: IntArray
        lateinit var valores: Array<String>
    }

    lateinit var funcion : FuncionesUtiles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evolucion_diaria_de_ventas)

        inicializarElementos()
    }

    private fun inicializarElementos(){
        funcion = FuncionesUtiles(this)
        funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_evol_diaria_venta"),this)
        cargar()
        mostrar()
    }

    private fun cargar(){
        val sql = ("SELECT a.COD_EMPRESA  , a.COD_VENDEDOR	    , b.DESC_VENDEDOR, a.PEDIDO_2_ATRAS, a.PEDIDO_1_ATRAS,"
                    + "           a.TOTAL_PEDIDOS, a.TOTAL_FACT  	    , a.META_VENTA   , a.META_LOGRADA  , a.PROY_VENTA    ,"
                    + "		      a.TOTAL_REBOTE , a.TOTAL_DEVOLUCION   , a.CANT_CLIENTES, a.CANT_POSIT    , a.EF_VISITA 	   ,"
                    + "		      a.DIA_TRAB	   , a.PUNTAJE			, a.SURTIDO_EF   , "
                    + "           IFNULL(((CAST(a.CANT_POSIT AS DOUBLE)/CAST(a.CANT_CLIENTES AS DOUBLE))*100),0.0) COBERTURA "
                    + "      FROM svm_evol_diaria_venta a, svm_vendedor_pedido b  "
                    + "     WHERE a.COD_VENDEDOR = b.COD_VENDEDOR  "
                    + "       AND a.COD_EMPRESA  = b.COD_EMPRESA  "
                    + "     ORDER BY CAST(a.COD_VENDEDOR AS INTEGER)  "
                )
        try {
            cursor = funcion.consultar(sql)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,funcion.consultar(sql))
        for (i in 0 until FuncionesUtiles.listaCabecera.size){
            FuncionesUtiles.listaCabecera[i]["PEDIDO_1_ATRAS"]   = funcion.entero(FuncionesUtiles.listaCabecera[i]["PEDIDO_1_ATRAS"].toString())
            FuncionesUtiles.listaCabecera[i]["PEDIDO_2_ATRAS"]   = funcion.entero(FuncionesUtiles.listaCabecera[i]["PEDIDO_2_ATRAS"].toString())
            FuncionesUtiles.listaCabecera[i]["TOTAL_PEDIDOS"]    = funcion.entero(FuncionesUtiles.listaCabecera[i]["TOTAL_PEDIDOS"].toString())
            FuncionesUtiles.listaCabecera[i]["TOTAL_FACT"]       = funcion.entero(FuncionesUtiles.listaCabecera[i]["TOTAL_FACT"].toString())
            FuncionesUtiles.listaCabecera[i]["META_VENTA"]       = funcion.entero(FuncionesUtiles.listaCabecera[i]["META_VENTA"].toString())
            FuncionesUtiles.listaCabecera[i]["META_LOGRADA"]     = funcion.enteroCliente(FuncionesUtiles.listaCabecera[i]["META_LOGRADA"].toString())
            FuncionesUtiles.listaCabecera[i]["PROY_VENTA"]       = funcion.entero(FuncionesUtiles.listaCabecera[i]["PROY_VENTA"].toString())
            FuncionesUtiles.listaCabecera[i]["TOTAL_REBOTE"]     = funcion.entero(FuncionesUtiles.listaCabecera[i]["TOTAL_REBOTE"].toString())
            FuncionesUtiles.listaCabecera[i]["TOTAL_DEVOLUCION"] = funcion.entero(FuncionesUtiles.listaCabecera[i]["TOTAL_DEVOLUCION"].toString())
        }
    }

    fun mostrar(){
        valores = arrayOf("COD_VENDEDOR"    ,"DESC_VENDEDOR"    ,"PEDIDO_2_ATRAS"   ,
                          "PEDIDO_1_ATRAS"  ,"TOTAL_PEDIDOS"    ,"TOTAL_FACT"       ,
                          "META_VENTA"      ,"META_LOGRADA"     ,"PROY_VENTA"       ,
                          "TOTAL_REBOTE"    ,"TOTAL_DEVOLUCION" ,"CANT_CLIENTES"    ,
                          "CANT_POSIT"      ,"COBERTURA"        ,"EF_VISITA"        ,
                          "DIA_TRAB"        ,"PUNTAJE"          ,"SURTIDO_EF"
                         )
        vistas = intArrayOf( R.id.tv1 ,R.id.tv2 ,R.id.tv3 ,R.id.tv4 ,R.id.tv5 ,R.id.tv6 ,
                             R.id.tv7 ,R.id.tv8 ,R.id.tv9 ,R.id.tv10,R.id.tv11,R.id.tv12,
                             R.id.tv13,R.id.tv14,R.id.tv15,R.id.tv16,R.id.tv17,R.id.tv18)
       val adapter: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
           FuncionesUtiles.listaCabecera, R.layout.inf_evo_dia_lista_evolucion,vistas,valores)

        lvEvolucionDiariaDeVentas.adapter = adapter
        lvEvolucionDiariaDeVentas.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvEvolucionDiariaDeVentas.invalidateViews()
        }
    }
}
