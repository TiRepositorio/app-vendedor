package apolo.vendedores.com.reportes

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import apolo.vendedores.com.R
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_extracto_de_salarios.*
import java.lang.Exception
import java.text.DecimalFormat

class ExtractoDeSalario : AppCompatActivity() {

    companion object{
        var posicionHaberes: Int = 0
        var posicionDebitos     :  Int = 0
        var listaHaberes: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
        var listaDebitos:  ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var cursor: Cursor
        lateinit var funcion: FuncionesUtiles
    }

    val formatoNumeroEntero : DecimalFormat = DecimalFormat("###,###,###.##")
    val formatoNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    var totalHaberes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extracto_de_salarios)

        inicializaElementos()
    }

    fun inicializaElementos(){
        funcion = FuncionesUtiles(this)
        cargarHaberes()
        mostrarHaberes()
        cargarDebitos()
        mostrarDebitos()
    }

    fun cargarHaberes(){
        var sql : String = ("select NRO_ORDEN        , FEC_HASTA     , COD_CONCEPTO  , "
                         +  "       DESC_CONCEPTO    , MONTO         , DECIMALES     , "
                         +  "       '0' AS TOT_VENTAS, '0' AS MONTO_COMISION            "
                         +  "  from svm_liq_comision_vendedor "
                         +  " where TIPO = 'H'"
                         +  " ORDER BY Cast(NRO_ORDEN as double)")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            var error = e.message
            e.printStackTrace()
            return
        }

        listaHaberes = ArrayList<HashMap<String, String>>()

        for (i in 0 until cursor.count){
            datos = HashMap<String, String>()
            datos.put("NRO_ORDEN",cursor.getString(cursor.getColumnIndex("NRO_ORDEN")))
            datos.put("DESC_CONCEPTO",cursor.getString(cursor.getColumnIndex("DESC_CONCEPTO")))
            datos.put("TOT_VENTAS",cursor.getString(cursor.getColumnIndex("TOT_VENTAS")))
            datos.put("MONTO_COMISION",cursor.getString(cursor.getColumnIndex("MONTO_COMISION")))
            datos.put("MONTO",formatoNumeroEntero.format(Integer.parseInt(
                              cursor.getString(cursor.getColumnIndex("MONTO")).replace(",", "."))))
            listaHaberes.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarHaberes(){
        val adapterHaberes = Adapter.ExtractoDeSalarioHaberes(this,
            listaHaberes
        )
        lvHaberes.adapter = adapterHaberes
        lvHaberes.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            posicionHaberes = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvHaberes.invalidateViews()
        }
        tvHabTotalVenta.text = formatoNumeroEntero.format(adapterHaberes.getTotalVenta())
        tvHabTotalComision.text = formatoNumeroEntero.format(adapterHaberes.getTotalComision())
        totalHaberes = adapterHaberes.getTotalMonto()
        tvHabTotalMonto.text = formatoNumeroEntero.format(totalHaberes)

    }

    fun cargarDebitos(){
        var sql : String = ("select MIN(Cast(NRO_ORDEN as number)) NRO_ORDEN,"
                + " COD_CONCEPTO, DESC_CONCEPTO, NRO_CUOTA, "
                + " SUM(Cast(MONTO as number)) MONTO, DECIMALES,"
                + " COUNT(id) CANT "
                + " from svm_liq_comision_vendedor "
                + " where TIPO = 'D'"
                + " GROUP BY COD_CONCEPTO, DESC_CONCEPTO, NRO_CUOTA, DECIMALES"
                + " ORDER BY NRO_ORDEN")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            var error = e.message
            e.printStackTrace()
            return
        }

        listaDebitos = ArrayList<HashMap<String, String>>()

        for (i in 0 until cursor.count){
            datos = HashMap<String, String>()
            datos.put("NRO_ORDEN",(i+1).toString())
            datos.put("DESC_CONCEPTO",cursor.getString(cursor.getColumnIndex("DESC_CONCEPTO")))
            datos.put("NRO_CUOTA",formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("NRO_CUOTA")).replace(",", "."))))
            var cadena = cursor.getString(cursor.getColumnIndex("MONTO"))
            datos.put("MONTO",formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO")).replace(",", "."))))
            listaDebitos.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarDebitos(){
        val adapterDebitos: Adapter.ExtractoDeSalarioDebitos = Adapter.ExtractoDeSalarioDebitos(this,
            listaDebitos, totalHaberes
        )
        lvDebitos.adapter = adapterDebitos
        lvDebitos.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            posicionDebitos = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvDebitos.invalidateViews()
        }
        tvDebTotalMontoDebito.text = formatoNumeroEntero.format(adapterDebitos.getTotalMonto())
        tvDebTotalMontoSaldo.text  = formatoNumeroEntero.format(adapterDebitos.getTotalSaldo(totalHaberes))
    }

}
