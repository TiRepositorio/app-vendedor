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
import kotlinx.android.synthetic.main.activity_evolucion_diaria_de_ventas.*

class EvolucionDiariaDeVentas : AppCompatActivity() {

    companion object{
        var datos: HashMap<String, String> = HashMap<String, String>()
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

    fun inicializarElementos(){
        funcion = FuncionesUtiles(this)
        cargar()
        mostrar()
    }

    fun cargar(){
        var sql = ("SELECT COD_EMPRESA  , COD_VENDEDOR	    , DESC_VENDEDOR, PEDIDO_2_ATRAS, PEDIDO_1_ATRAS,"
                    + "           TOTAL_PEDIDOS, TOTAL_FACT  	    , META_VENTA   , META_LOGRADA  , PROY_VENTA    ,"
                    + "		      TOTAL_REBOTE , TOTAL_DEVOLUCION   , CANT_CLIENTES, CANT_POSIT    , EF_VISITA 	   ,"
                    + "		      DIA_TRAB	   , PUNTAJE			, SURTIDO_EF   , "
                    + "           IFNULL(((CAST(CANT_POSIT AS DOUBLE)/CAST(CANT_CLIENTES AS DOUBLE))*100),0.0) COBERTURA "
                    + "      FROM svm_evol_diaria_venta  ")
        try {
            cursor = funcion.consultar(sql)
            cursor.moveToFirst()
        } catch (e : Exception){
            var error = e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList<HashMap<String,String>>()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,funcion.consultar(sql))

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
        lvEvolucionDiariaDeVentas.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvEvolucionDiariaDeVentas.invalidateViews()
        }
    }
}
