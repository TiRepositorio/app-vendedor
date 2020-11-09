package apolo.vendedores.com.reportes

import android.database.Cursor
import apolo.vendedores.com.MainActivity2

class SubVentasPorMarcas{

    lateinit var cursor : Cursor
    lateinit var lista : ArrayList<HashMap<String, String>>
    lateinit var datosX : Array<String>
    lateinit var datosY : IntArray

    fun cargarVentasPorMarcas():ArrayList<HashMap<String, String>>{

        val sql : String = ("select DESC_GTE_MARKETIN, DESC_MODULO, sum(MAYOR_VENTA) MAYOR_VENTA, sum(VENTA_MES1) VENTA_MES1, "
                +   "		   CAST(sum(VENTA_MES2) AS INTEGER) VENTA_MES2  , sum(META) META, ((CAST(VENTA_MES2 AS NUMBER)*100)/CAST(META AS NUMBER)) AS PORC "
                +   " from svm_metas_punto_por_linea "
                +   " group by DESC_GTE_MARKETIN ")

        try {
            cursor = MainActivity2.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            var error = e.message
            return lista
        }

        lista = ArrayList()
        MainActivity2.funcion.cargarLista(lista,cursor)
        return lista
    }

    fun cargarDatosX(indice:String){
        datosX = dimensionaArrayString(lista.size)
        for (i in 0 until lista.size){
            if (lista[i][indice].toString().length > 10) {
                datosX[i] = lista[i][indice].toString().substring(0,10)
            } else {
                datosX[i] = lista[i][indice].toString()
            }
        }
    }

    fun cargarDatosY(indice:String){
        var listaInt : ArrayList<Int> = ArrayList()
        datosY = intArrayOf(0)
        for (i in 0 until lista.size){
            try {
                listaInt.add(MainActivity2.funcion.entero(lista[i][indice].toString().replace(".","")).replace(".","").toInt())
            } catch (e : Exception){
                listaInt.add(0)
            }
//            datosY[i] = lista.get(i).get(indice).toString().replace(".","").toInt()
        }
        datosY = listaInt.toIntArray()
    }

    fun dimensionaArrayString(cant : Int):Array<String>{
        val lista : Array<String?> = arrayOfNulls(cant)
        for (i in 0 until cant){
            lista[i] = ""
        }
        return lista as Array<String>
    }

}