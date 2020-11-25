package apolo.vendedores.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import apolo.vendedores.com.MainActivity2

class SubVentasPorClientes{

    lateinit var cursor : Cursor
    lateinit var lista : ArrayList<HashMap<String, String>>
    lateinit var datosX : Array<String>
    lateinit var datosY : IntArray

    @SuppressLint("Recycle")
    fun cargarVentasPorCliente():ArrayList<HashMap<String, String>>{

        val sql : String = ("select COD_VENDEDOR,  "
                +   "		   CAST(sum(VENTA_4) AS INTEGER) VENTA_4  "
                +   " from svm_metas_punto_por_cliente "
                +   " group by COD_VENDEDOR ")

        try {
            cursor = MainActivity2.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
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