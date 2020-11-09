package apolo.vendedores.com.reportes

import android.database.Cursor
import apolo.vendedores.com.MainActivity2

class SubAvanceDeComisiones{

    lateinit var cursor : Cursor
    lateinit var lista : ArrayList<HashMap<String, String>>
    lateinit var datosX : Array<String>
    lateinit var datosY : IntArray

    fun cargarDatos():ArrayList<HashMap<String, String>>{

        val sql : String = (" SELECT  TIP_COM "
                + "       ,  CAST(SUM(MONTO_VENTA) AS INTEGER)    AS MONTO_VENTA "
                + "       ,  CAST(SUM(MONTO_A_COBRAR) AS INTEGER) AS MONTO_A_COBRAR "
                + "  FROM svm_liq_premios_vend "
//                + " WHERE COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' "
//                + "   AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' "
                + " GROUP BY TIP_COM ")

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
        val listaInt : ArrayList<Int> = ArrayList()
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

    private fun dimensionaArrayString(cant : Int):Array<String>{
        val lista : Array<String?> = arrayOfNulls(cant)
        for (i in 0 until cant){
            lista[i] = ""
        }
        return lista as Array<String>
    }

}