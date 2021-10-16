package apolo.vendedores.com.reportes

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_extracto_de_salarios.*
import java.text.DecimalFormat

class ExtractoDeSalario : AppCompatActivity() {

    companion object{
        var posicionHaberes: Int = 0
        var posicionDebitos     :  Int = 0
        var listaHaberes: ArrayList<HashMap<String, String>> = ArrayList()
        var listaDebitos:  ArrayList<HashMap<String, String>> = ArrayList()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
        lateinit var funcion: FuncionesUtiles
    }

    private val formatoNumeroEntero : DecimalFormat = DecimalFormat("###,###,###.##")
//    val formatoNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
private var totalHaberes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extracto_de_salarios)

        inicializaElementos()
    }

    private fun inicializaElementos(){
        funcion = FuncionesUtiles(this)
        cargarHaberes()
        mostrarHaberes()
        cargarDebitos()
        mostrarDebitos()
    }

    private fun cargarHaberes(){
        val sql : String = ("select NRO_ORDEN        , FEC_HASTA     , COD_CONCEPTO  , "
                         +  "       DESC_CONCEPTO    , MONTO         , DECIMALES     , "
                         +  "       '0' AS TOT_VENTAS, '0' AS MONTO_COMISION            "
                         +  "  from svm_liq_comision_vendedor "
                         +  " where TIPO = 'H'"
                         +  " ORDER BY Cast(NRO_ORDEN as double)")

        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            e.printStackTrace()
            return
        }

        listaHaberes = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["NRO_ORDEN"] = cursor.getString(cursor.getColumnIndex("NRO_ORDEN"))
            datos["DESC_CONCEPTO"] = cursor.getString(cursor.getColumnIndex("DESC_CONCEPTO"))
            datos["TOT_VENTAS"] = cursor.getString(cursor.getColumnIndex("TOT_VENTAS"))
            datos["MONTO_COMISION"] = cursor.getString(cursor.getColumnIndex("MONTO_COMISION"))
            datos["MONTO"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO")).replace(",", ".")))
            listaHaberes.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarHaberes(){
        val adapterHaberes = Adapter.ExtractoDeSalarioHaberes(this,
            listaHaberes
        )
        lvHaberes.adapter = adapterHaberes
        lvHaberes.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            posicionHaberes = position
            lvHaberes.invalidateViews()
        }
        tvHabTotalVenta.text = formatoNumeroEntero.format(adapterHaberes.getTotalVenta())
        tvHabTotalComision.text = formatoNumeroEntero.format(adapterHaberes.getTotalComision())
        totalHaberes = adapterHaberes.getTotalMonto()
        tvHabTotalMonto.text = formatoNumeroEntero.format(totalHaberes)

    }

    private fun cargarDebitos(){
        val sql : String = ("select MIN(Cast(NRO_ORDEN as number)) NRO_ORDEN,"
                + " COD_CONCEPTO, DESC_CONCEPTO, NRO_CUOTA, "
                + " SUM(Cast(MONTO as number)) MONTO, DECIMALES,"
                + " COUNT(id) CANT "
                + " from svm_liq_comision_vendedor "
                + " where TIPO = 'D'"
                + " GROUP BY COD_CONCEPTO, DESC_CONCEPTO, NRO_CUOTA, DECIMALES"
                + " ORDER BY NRO_ORDEN")

        try {
            cursor = funcion.consultar(sql)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.printStackTrace()
            return
        }

        listaDebitos = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["NRO_ORDEN"] = (i+1).toString()
            datos["DESC_CONCEPTO"] = cursor.getString(cursor.getColumnIndex("DESC_CONCEPTO"))
            datos["NRO_CUOTA"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("NRO_CUOTA")).replace(",", ".")))
            cursor.getString(cursor.getColumnIndex("MONTO"))
            datos["MONTO"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO")).replace(",", ".")))
            listaDebitos.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarDebitos(){
        val adapterDebitos: Adapter.ExtractoDeSalarioDebitos = Adapter.ExtractoDeSalarioDebitos(
            this,
            listaDebitos
        )
        lvDebitos.adapter = adapterDebitos
        lvDebitos.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            posicionDebitos = position
            lvDebitos.invalidateViews()
        }
        tvDebTotalMontoDebito.text = formatoNumeroEntero.format(adapterDebitos.getTotalMonto())
        tvDebTotalMontoSaldo.text  = formatoNumeroEntero.format(adapterDebitos.getTotalSaldo(totalHaberes))
    }

}
