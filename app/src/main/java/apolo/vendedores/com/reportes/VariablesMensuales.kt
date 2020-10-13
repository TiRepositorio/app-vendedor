package apolo.vendedores.com.reportes

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_variables_mensuales.*
import kotlinx.android.synthetic.main.activity_variables_mensuales2.*
import java.lang.Exception
import java.text.DecimalFormat

class VariablesMensuales : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        this.tvVendedor.text = item.title.toString()
        cargarCoberturaMensual()
        mostrarCoberturaMensual()
        cargarCuotaPorUnidadDeNegocio()
        mostrarCuotaPorUnidadDeNegocio()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    companion object{
        var funcion:FuncionesUtiles = FuncionesUtiles()
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var cursor: Cursor
    }

    val formatoNumeroEntero : DecimalFormat = DecimalFormat("###,###,###.##")
    val formatoNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_variables_mensuales2)

//        funcion = FuncionesUtiles(img)
        inicializarElementos()
    }

    fun inicializarElementos(){
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        llBotonVendedores.visibility = View.VISIBLE
        llBotonVendedores.setOnClickListener(View.OnClickListener {
            mostrarMenu()
        })
        barraMenu.setNavigationItemSelectedListener(this)

        listaVendedores()
        cargarCoberturaMensual()
        mostrarCoberturaMensual()
        cargarCuotaPorUnidadDeNegocio()
        mostrarCuotaPorUnidadDeNegocio()
    }

    fun mostrarMenu(){
        if (contMenu.isDrawerOpen(GravityCompat.START)) {
            contMenu.closeDrawer(GravityCompat.START)
        } else {
            contMenu.openDrawer(GravityCompat.START)
        }
    }

    fun listaVendedores(){
        var sql = ("select distinct COD_VENDEDOR, DESC_VENDEDOR from ("
                + "select distinct c.COD_VENDEDOR, c.DESC_VENDEDOR "
                + "  from fvv_liq_cuota_x_und_neg_vend c "
                + " union all "
                + "select distinct c.COD_VENDEDOR, c.DESC_VENDEDOR "
                + "  from svm_cobertura_mensual_vend c "
                + ")"
                + " order by cast(COD_VENDEDOR as integer)")
        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
            var codigo = cursor.getString(cursor.getColumnIndex("COD_VENDEDOR"))
            var descripcion = cursor.getString(cursor.getColumnIndex("DESC_VENDEDOR"))
            tvVendedor.text = codigo + "-" + descripcion
        } catch (e : Exception){
            var error = e.message
            return
        }

        for (i in 0 until cursor.count){
            var codigo = cursor.getString(cursor.getColumnIndex("COD_VENDEDOR"))
            var descripcion = cursor.getString(cursor.getColumnIndex("DESC_VENDEDOR"))
            barraMenu.menu.add(codigo + "-" + descripcion).setIcon(R.drawable.ic_usuario)
            cursor.moveToNext()
        }
    }

    fun cargarCoberturaMensual(){
        var sql : String = (" SELECT TOT_CLI_CART,CANT_POSIT,PORC_LOGRO,PORC_COB,PREMIOS,MONTO_A_COBRAR" 
                          + "   from svm_cobertura_mensual_vend" 
//                          + "  WHERE COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' " 
//                          + "    AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' " 
                )

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            var error = e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList<HashMap<String, String>>()

        for (i in 0 until cursor.count){

            datos = HashMap<String, String>()
            datos.put("TOT_CLIEN_ASIG",cursor.getString(cursor.getColumnIndex("TOT_CLI_CART")))
            datos.put("CANT_POSIT",cursor.getString(cursor.getColumnIndex("CANT_POSIT")))
            datos.put("PORC_LOGRO",formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_LOGRO")).replace(",", ".").toDouble()))
            datos.put("PORC_COB",formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_COB")).replace(",", ".").toDouble()))
            datos.put("PREMIOS",formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("PREMIOS")).replace(",", "."))))
            datos.put("MONTO_A_COBRAR",formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", "."))))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarCoberturaMensual(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6)
        funcion.valores = arrayOf("TOT_CLIEN_ASIG","CANT_POSIT","PORC_COB","PORC_LOGRO","PREMIOS","MONTO_A_COBRAR")
        val adapterCoberturaMensual: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,
            R.layout.rep_var_lista_cobertura_mensual,
            funcion.vistas,
            funcion.valores
        )
        lvCoberturaMensual.adapter = adapterCoberturaMensual
        lvCoberturaMensual.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvCoberturaMensual.invalidateViews()
        }
    }

    fun cargarCuotaPorUnidadDeNegocio(){
        var sql : String = ("SELECT FEC_DESDE || '-' || FEC_HASTA AS PERIODO "
                + " ,COD_UNID_NEGOCIO || '-' || DESC_UNID_NEGOCIO AS UNIDAD_DE_NEGOCIO"
                + " ,PORC_PREMIO		, PORC_ALC_PREMIO		, MONTO_VENTA	"
                + " ,MONTO_CUOTA		, MONTO_A_COBRAR "
                + "  from fvv_liq_cuota_x_und_neg_vend "
//                + " WHERE COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' "
//                + "   AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' "
                + " ORDER BY CAST(COD_UNID_NEGOCIO AS INTEGER)")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            var error = e.message
            return
        }

        FuncionesUtiles.listaDetalle = ArrayList<HashMap<String, String>>()

        for (i in 0 until cursor.count){
            datos = HashMap<String, String>()
            datos.put("PERIODO",cursor.getString(cursor.getColumnIndex("PERIODO")))
            datos.put("UNIDAD_DE_NEGOCIO",cursor.getString(cursor.getColumnIndex("UNIDAD_DE_NEGOCIO")))
            datos.put("PORC_PREMIO",formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_PREMIO")).replace(",", ".").toDouble()) + "%")
            datos.put("PORC_ALC_PREMIO",formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_ALC_PREMIO")).replace(",", ".").toDouble()) + "%")
            datos.put("MONTO_VENTA",formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_VENTA")).replace(",", "."))))
            datos.put("MONTO_CUOTA",formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_CUOTA")).replace(",", "."))))
            datos.put("MONTO_A_COBRAR",formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", "."))))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarCuotaPorUnidadDeNegocio(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores = arrayOf("UNIDAD_DE_NEGOCIO","PERIODO","PORC_ALC_PREMIO","PORC_PREMIO","MONTO_CUOTA","MONTO_VENTA","MONTO_A_COBRAR")
        val adapterCuotaPorUnidadDeNegocios: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,
            R.layout.rep_var_lista_cuota_por_unidad_de_negocio,
            funcion.vistas,
            funcion.valores
        )
        lvCuotaPorUnidadDeNegocio.adapter = adapterCuotaPorUnidadDeNegocios
        lvCuotaPorUnidadDeNegocio.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvCuotaPorUnidadDeNegocio.invalidateViews()
        }
    }
}
