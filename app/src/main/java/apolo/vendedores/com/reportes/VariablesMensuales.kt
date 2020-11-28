package apolo.vendedores.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_variables_mensuales.*
import kotlinx.android.synthetic.main.activity_variables_mensuales2.barraMenu
import kotlinx.android.synthetic.main.activity_variables_mensuales2.contMenu
import kotlinx.android.synthetic.main.barra_vendedores.*
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
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatoNumeroEntero : DecimalFormat = DecimalFormat("###,###,###.##")
    private val formatoNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_variables_mensuales2)

//        funcion = FuncionesUtiles(img)
        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion = FuncionesUtiles(
            imgTitulo,
            tvTitulo,
            ibtnAnterior,
            ibtnSiguiente,
            tvVendedor,
            contMenu,
            barraMenu,
            llBotonVendedores
        )
        funcion.cargarTitulo(R.drawable.ic_dolar,"Cobertura mensual")
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        llBotonVendedores.visibility = View.VISIBLE
        llBotonVendedores.setOnClickListener { mostrarMenu() }
        barraMenu.setNavigationItemSelectedListener(this)
        funcion.ejecutar(SentenciasSQL.venVistaCabecera("fvv_liq_cuota_x_und_neg_vend"),this)
        funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_cobertura_mensual_vend"),this)
        funcion.ejecutar("create view if not exists variables_mensuales_cab as ${sqlVendedores()}",this)
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR","variables_mensuales_cab")
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
//        listaVendedores()
        cargarCoberturaMensual()
        mostrarCoberturaMensual()
        cargarCuotaPorUnidadDeNegocio()
        mostrarCuotaPorUnidadDeNegocio()
    }

    private fun mostrarMenu(){
        if (contMenu.isDrawerOpen(GravityCompat.START)) {
            contMenu.closeDrawer(GravityCompat.START)
        } else {
            contMenu.openDrawer(GravityCompat.START)
        }
    }

    private fun sqlVendedores():String{
        return ("select distinct COD_VENDEDOR, DESC_VENDEDOR from ("
                + "select distinct c.COD_VENDEDOR, d.DESC_VENDEDOR "
                + "  from fvv_liq_cuota_x_und_neg_vend c, ven_fvv_liq_cuota_x_und_neg_vend d "
                + " where c.COD_VENDEDOR = d.COD_VENDEDOR "
                + " union all "
                + "select distinct c.COD_VENDEDOR, d.DESC_VENDEDOR "
                + "  from svm_cobertura_mensual_vend c, ven_svm_cobertura_mensual_vend d "
                + " where c.COD_VENDEDOR = d.COD_VENDEDOR "
                + ")"
                + " order by cast(COD_VENDEDOR as integer)")
    }

    @SuppressLint("SetTextI18n", "Recycle")
    fun listaVendedores(){
        val sql : String = ("select distinct COD_VENDEDOR, DESC_VENDEDOR from ("
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
            val codigo : String = cursor.getString(cursor.getColumnIndex("COD_VENDEDOR"))
            val descripcion : String = cursor.getString(cursor.getColumnIndex("DESC_VENDEDOR"))
            tvVendedor.text = "$codigo-$descripcion"
        } catch (e : Exception){
            e.message
            return
        }

        for (i in 0 until cursor.count){
            val codigo = cursor.getString(cursor.getColumnIndex("COD_VENDEDOR"))
            val descripcion = cursor.getString(cursor.getColumnIndex("DESC_VENDEDOR"))
            barraMenu.menu.add("$codigo-$descripcion").setIcon(R.drawable.ic_usuario)
            cursor.moveToNext()
        }
    }

    @SuppressLint("Recycle")
    private fun cargarCoberturaMensual(){
        val sql : String = (" SELECT TOT_CLI_CART,CANT_POSIT,PORC_LOGRO,PORC_COB,PREMIOS,MONTO_A_COBRAR"
                          + "   from svm_cobertura_mensual_vend" 
//                          + "  WHERE COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' " 
//                          + "    AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' " 
                )

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){

            datos = HashMap()
            datos["TOT_CLIEN_ASIG"] = cursor.getString(cursor.getColumnIndex("TOT_CLI_CART"))
            datos["CANT_POSIT"] = cursor.getString(cursor.getColumnIndex("CANT_POSIT"))
            datos["PORC_LOGRO"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_LOGRO")).replace(",", ".").toDouble())
            datos["PORC_COB"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_COB")).replace(",", ".").toDouble())
            datos["PREMIOS"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("PREMIOS")).replace(",", ".")))
            datos["MONTO_A_COBRAR"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarCoberturaMensual(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6)
        funcion.valores = arrayOf("TOT_CLIEN_ASIG","CANT_POSIT","PORC_COB","PORC_LOGRO","PREMIOS","MONTO_A_COBRAR")
        val adapterCoberturaMensual: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,
            R.layout.rep_var_lista_cobertura_mensual,
            funcion.vistas,
            funcion.valores
        )
        lvCoberturaMensual.adapter = adapterCoberturaMensual
        lvCoberturaMensual.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            lvCoberturaMensual.invalidateViews()
        }
    }

    @SuppressLint("Recycle")
    private fun cargarCuotaPorUnidadDeNegocio(){
        val sql : String = ("SELECT FEC_DESDE || '-' || FEC_HASTA AS PERIODO "
                + " ,COD_UNID_NEGOCIO || '-' || DESC_UNID_NEGOCIO AS UNIDAD_DE_NEGOCIO"
                + " ,PORC_PREMIO		, PORC_ALC_PREMIO		, MONTO_VENTA	"
                + " ,MONTO_CUOTA		, MONTO_A_COBRAR "
                + "  from fvv_liq_cuota_x_und_neg_vend "
                + " WHERE COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' "
                + " ORDER BY CAST(COD_UNID_NEGOCIO AS INTEGER)")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaDetalle = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["PERIODO"] = cursor.getString(cursor.getColumnIndex("PERIODO"))
            datos["UNIDAD_DE_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("UNIDAD_DE_NEGOCIO"))
            datos["PORC_PREMIO"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_PREMIO")).replace(",", ".").toDouble()) + "%"
            datos["PORC_ALC_PREMIO"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_ALC_PREMIO")).replace(",", ".").toDouble()) + "%"
            datos["MONTO_VENTA"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_VENTA")).replace(",", ".")))
            datos["MONTO_CUOTA"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_CUOTA")).replace(",", ".")))
            datos["MONTO_A_COBRAR"] = formatoNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".")))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarCuotaPorUnidadDeNegocio(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores = arrayOf("UNIDAD_DE_NEGOCIO","PERIODO","PORC_ALC_PREMIO","PORC_PREMIO","MONTO_CUOTA","MONTO_VENTA","MONTO_A_COBRAR")
        val adapterCuotaPorUnidadDeNegocios: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,
            R.layout.rep_var_lista_cuota_por_unidad_de_negocio,
            funcion.vistas,
            funcion.valores
        )
        lvCuotaPorUnidadDeNegocio.adapter = adapterCuotaPorUnidadDeNegocios
        lvCuotaPorUnidadDeNegocio.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            lvCuotaPorUnidadDeNegocio.invalidateViews()
        }
    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarCoberturaMensual()
            mostrarCoberturaMensual()
            cargarCuotaPorUnidadDeNegocio()
            mostrarCuotaPorUnidadDeNegocio()
        }
    }

}
