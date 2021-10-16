package apolo.vendedores.com.ventas

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.dialogo_bonificacion_combo.*
import kotlinx.android.synthetic.main.ven_con_consulta_detalle.*

class DialogoPedidos(var context: Context, var numero: Int, listaDePrecio: String) {

    var lista: ArrayList<HashMap<String,String>> = ArrayList()
    var listaDetalle: ArrayList<HashMap<String,String>> = ArrayList()
    var funcion : FuncionesUtiles = FuncionesUtiles(context)
    lateinit var adapter: Adapter.AdapterGenericoDetalle
    lateinit var dialogo: Dialog
    private var listaDePrecios = listaDePrecio

    fun mostrarDialogo(){
        dialogo = Dialog(context)
        dialogo.setContentView(R.layout.ven_con_consulta_detalle)
        buscar()
        mostrar(dialogo.lvDetPedido)
        if (lista[0]["IND_COMBO"].toString() == "S"){
            dialogo.trCombo.visibility = View.VISIBLE
            dialogo.tvdCantidadPromo.isEnabled = false
        }
        dialogo.show()
    }

    private fun buscar(){
        val sql : String = ("SELECT DISTINCT a.*, b.DESC_ARTICULO FROM vt_pedidos_det a, svm_articulos_precios b "
                +  " WHERE a.NUMERO = '$numero' "
                +  "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                +  "   AND a.COD_EMPRESA  = b.COD_EMPRESA "
                +  "   AND a.COD_ARTICULO = b.COD_ARTICULO "
                +  "   AND a.COD_VENDEDOR = b.COD_VENDEDOR "
                +  "   AND b.COD_LISTA_PRECIO = '$listaDePrecios' "
                +  " "
                +  " ")

        lista = ArrayList()
        cargarDatos(funcion.consultar(sql),lista)
    }

    private fun cargarDatos(cursor: Cursor, lista:ArrayList<HashMap<String,String>>){
        for (i in 0 until cursor.count){
            val datos = HashMap<String,String>()
            for (j in 0 until cursor.columnCount){
                datos[cursor.getColumnName(j)] = funcion.dato(cursor,cursor.getColumnName(j))
            }
            datos["PREC_CAJA"] = funcion.entero(funcion.dato(cursor,"PREC_CAJA"))
            datos["PRECIO_UNITARIO"] = funcion.entero(funcion.dato(cursor,"PRECIO_UNITARIO"))
            datos["MONTO_TOTAL"] = funcion.entero(funcion.dato(cursor,"MONTO_TOTAL"))
            lista.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrar(lvBusqueda: ListView){
        FuncionesUtiles.posicionDetalle = 0
        funcion.vistas = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5)
        funcion.valores = arrayOf("COD_ARTICULO"   , "DESC_ARTICULO"  ,
                                  "CANTIDAD"       , "PRECIO_UNITARIO", "MONTO_TOTAL")
        adapter = Adapter.AdapterGenericoDetalle(context
            ,lista
            ,R.layout.ven_con_lista_consulta_detalle
            ,funcion.vistas
            ,funcion.valores)
        lvBusqueda.adapter = adapter
        lvBusqueda.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            lvBusqueda.invalidateViews()
        }
    }


}