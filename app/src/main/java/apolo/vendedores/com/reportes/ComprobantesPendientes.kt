package apolo.vendedores.com.reportes

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import apolo.vendedores.com.R
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_comprobantes_pendientes.*
import java.text.DecimalFormat



class ComprobantesPendientes : AppCompatActivity() {

    companion object{
        var posicionSeleccionadoComprobantes: Int = 0
        var subPosicionSeleccionadoComprobantes: Int = 0
        var listaComprobantes: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
        var sublistaComprobantes: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
        var subListasComprobantes: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList<ArrayList<HashMap<String, String>>>()
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var cursor: Cursor
    }

    val formatNumeroEntero : DecimalFormat = DecimalFormat("###,###,##0.##")
    val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    val funcion = FuncionesUtiles(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobantes_pendientes)

        inicializarElementos()
    }

    fun inicializarElementos(){
        cargarComprobantesPendientes()
        mostrarComprobantesPendientes()
    }

    fun cargarComprobantesPendientes(){

        var sql : String = (" SELECT TIP_COMPROBANTE_REF                          , "
                + "        SUBSTR(FEC_COMPROBANTE,4,7) AS PERIODO       , "
                + "        DESCRIPCION                                  , "
                + "        SUM(TOT_EXENTA)      AS TOT_EXENTA           , "
                + "        SUM(TOT_GRAVADA + TOT_IVA) AS TOT_GRAVADA    , "
                + "        (SUM(TOT_IVA)+sum(TOT_GRAVADA))/11      AS TOT_IVA              , "
                + "        SUM(TOT_COMPROBANTE) AS TOT_COMPROBANTE        "
                + "   FROM svm_liquidacion_fuerza_venta                   "
                + "  GROUP BY TIP_COMPROBANTE_REF,SUBSTR(FEC_COMPROBANTE,4,7), DESCRIPCION ")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            var error = e.message
            return
        }

        listaComprobantes= ArrayList<HashMap<String, String>>()

        for (i in 0 until cursor.count){
            datos = HashMap<String, String>()
            datos.put("TIP_COMPROBANTE_REF",cursor.getString(cursor.getColumnIndex("TIP_COMPROBANTE_REF")))
            datos.put("PERIODO",cursor.getString(cursor.getColumnIndex("PERIODO")))
            datos.put("DESCRIPCION",cursor.getString(cursor.getColumnIndex("DESCRIPCION")))
            datos.put("TOT_EXENTA",formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("TOT_EXENTA")).replace(",", ".").replace("null", "").toDouble()))
            datos.put("TOT_GRAVADA",formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("TOT_GRAVADA")).replace(",", ".").replace("null", "").toDouble()))
            datos.put("TOT_IVA",formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("TOT_IVA")).replace(",", ".").replace("null", "").toDouble()))
            datos.put("TOT_COMPROBANTE",formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("TOT_COMPROBANTE")).replace(",", "."))))
            listaComprobantes.add(datos)
            cursor.moveToNext()
        }

        subListasComprobantes = ArrayList<ArrayList<HashMap<String, String>>>()

        for (i in 0 until listaComprobantes.size){
            sql = (" SELECT TIP_COMPROBANTE_REF, "
                    + "        FEC_COMPROBANTE    , "
                    + "        OBSERVACION        , "
                    + "        DESCRIPCION        , "
                    + "        TOT_EXENTA         , "
                    + "        TOT_GRAVADA + TOT_IVA AS TOT_GRAVADA        , "
                    + "        TOT_IVA            , "
                    + "        TOT_COMPROBANTE      "
                    + " FROM   svm_liquidacion_fuerza_venta"
                    + " WHERE  TIP_COMPROBANTE_REF         = '" + listaComprobantes.get(i).get("TIP_COMPROBANTE_REF") + "'"
                    + "   AND  SUBSTR(FEC_COMPROBANTE,4,7) = '" + listaComprobantes.get(i).get("PERIODO") + "'"
                    + " ORDER BY FEC_COMPROBANTE DESC")
            try {
                cursor = MainActivity.bd!!.rawQuery(sql, null)
                cursor.moveToFirst()
            } catch (e : Exception){
                var error = e.message
                return
            }

            sublistaComprobantes = ArrayList<HashMap<String, String>>()

            for (j in 0 until cursor.count){
                datos = HashMap<String, String>()
                datos.put("TIP_COMPROBANTE_REF",cursor.getString(cursor.getColumnIndex("TIP_COMPROBANTE_REF")))
                datos.put("FEC_COMPROBANTE",cursor.getString(cursor.getColumnIndex("FEC_COMPROBANTE")))
                datos.put("OBSERVACION",cursor.getString(cursor.getColumnIndex("OBSERVACION")))
                datos.put("DESCRIPCION",cursor.getString(cursor.getColumnIndex("DESCRIPCION")))
                datos.put("TOT_EXENTA",formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("TOT_EXENTA")).replace(",", ".").replace("null", "0").toInt()))
                datos.put("TOT_GRAVADA",formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("TOT_GRAVADA")).replace(",", ".").replace("null", "0").toInt()))
                datos.put("TOT_IVA",formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("TOT_IVA")).replace(",", ".").replace("null", "0").toInt()))
                datos.put("TOT_COMPROBANTE",formatNumeroEntero.format(Integer.parseInt(
                    cursor.getString(cursor.getColumnIndex("TOT_COMPROBANTE")).replace(",", ".").replace("null", "0"))))
                sublistaComprobantes.add(datos)
                cursor.moveToNext()
            }
            subListasComprobantes.add(sublistaComprobantes)
        }

    }

    fun mostrarComprobantesPendientes(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv1,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6)
        funcion.valores = arrayOf("PERIODO","DESCRIPCION","TOT_EXENTA","TOT_GRAVADA","TOT_IVA","TOT_COMPROBANTE")
        funcion.subVistas = intArrayOf(R.id.tvs1,R.id.tvs2,R.id.tvs3,R.id.tvs4,R.id.tvs5,R.id.tvs6,R.id.tvs7)
        funcion.subValores = arrayOf("FEC_COMPROBANTE","OBSERVACION","DESCRIPCION","TOT_EXENTA","TOT_GRAVADA","TOT_IVA","TOT_COMPROBANTE")
        val adapterComprobantesPendientes = Adapter.ListaConSubtabla(this,
            listaComprobantes   , subListasComprobantes ,
            R.layout.rep_com_pen_lista_comprobantes     ,
            R.layout.rep_com_pen_lista_sub_comprobantes ,
            funcion.vistas      ,funcion.valores        ,
            funcion.subVistas   ,funcion.subValores     ,
            R.id.lvSubComprobantesPendientes,R.id.llCompSubCabecera
        )
        lvComprobantesPendientes.adapter = adapterComprobantesPendientes
        lvComprobantesPendientes.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            subPosicionSeleccionadoComprobantes = 0
        }

    }

}
