package apolo.vendedores.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import apolo.vendedores.com.MainActivity2

@Suppress("UNCHECKED_CAST")
class SubVentasPorMarcas{

    lateinit var cursor : Cursor
    lateinit var lista : ArrayList<HashMap<String, String>>
    lateinit var datosX : Array<String>
    lateinit var datosY : IntArray

    @SuppressLint("Recycle")
    fun cargarVentasPorMarcas():ArrayList<HashMap<String, String>>{

        val sql : String = ("SELECT DESC_GTE_MARKETIN, DESC_MODULO, SUM(MAYOR_VENTA) MAYOR_VENTA, SUM(VENTA_MES1) VENTA_MES1, "
                +   "		   CAST(SUM(VENTA_MES2) AS INTEGER) VENTA_MES2  , SUM(META) META, ((CAST(VENTA_MES2 AS NUMBER)*100)/CAST(META AS NUMBER)) AS PORC "
                +   "  FROM svm_metas_punto_por_linea "
                +   " GROUP BY DESC_GTE_MARKETIN ")

        try {
            cursor = MainActivity2.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
        }

        lista = ArrayList()
        if (cursor != null){
            MainActivity2.funcion.cargarLista(lista,cursor)
        }
        if (lista.size==0){
            var dato = HashMap<String,String>()
            dato["DESC_GTE_MARKETIN"] = "APOLO"
            dato["DESC_MODULO"] = "APOLO"
            dato["MAYOR_VENTA"] = "0"
            dato["VENTA_MES1"] = "0"
            dato["VENTA_MES2"] = "0"
            dato["META"] = "0"
            dato["PORC"] = "0"
            lista.add(dato)
            dato = HashMap()
            dato["DESC_GTE_MARKETIN"] = "INPASA"
            dato["DESC_MODULO"] = "INPASA"
            dato["MAYOR_VENTA"] = "0"
            dato["VENTA_MES1"] = "0"
            dato["VENTA_MES2"] = "0"
            dato["META"] = "0"
            dato["PORC"] = "0"
            lista.add(dato)
            dato = HashMap()
            dato["DESC_GTE_MARKETIN"] = "BEBIDAS"
            dato["DESC_MODULO"] = "BEBIDAS"
            dato["MAYOR_VENTA"] = "0"
            dato["VENTA_MES1"] = "0"
            dato["VENTA_MES2"] = "0"
            dato["META"] = "0"
            dato["PORC"] = "0"
            lista.add(dato)
            dato = HashMap()
            dato["DESC_GTE_MARKETIN"] = "SINERGIA"
            dato["DESC_MODULO"] = "SINERGIA"
            dato["MAYOR_VENTA"] = "0"
            dato["VENTA_MES1"] = "0"
            dato["VENTA_MES2"] = "0"
            dato["META"] = "0"
            dato["PORC"] = "0"
            lista.add(dato)
            dato = HashMap()
            dato["DESC_GTE_MARKETIN"] = "JNJ"
            dato["DESC_MODULO"] = "JNJ"
            dato["MAYOR_VENTA"] = "0"
            dato["VENTA_MES1"] = "0"
            dato["VENTA_MES2"] = "0"
            dato["META"] = "0"
            dato["PORC"] = "0"
            lista.add(dato)
        }
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
        val listaInt : ArrayList<Int> = ArrayList()
        datosY = intArrayOf(0)
        for (i in 0 until lista.size){
            try {
                listaInt.add(MainActivity2.funcion.entero(lista[i][indice].toString().replace(".","")).replace(".","").toInt())
            } catch (e : Exception){
                listaInt.add(0)
            }
        }
        datosY = listaInt.toIntArray()
    }

    private fun dimensionaArrayString(cant : Int):Array<String>{
        val lista : Array<String?> = arrayOfNulls(cant)
        for (i in 0 until cant){
            lista[i] = ""
        }
        return lista as Array<String>
    }

}