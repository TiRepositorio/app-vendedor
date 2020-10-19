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
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_canasta_de_marcas.*
import kotlinx.android.synthetic.main.activity_canasta_de_marcas2.*
import kotlinx.android.synthetic.main.barra_vendedores.*
import java.text.DecimalFormat

class VentasPorMarca : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var subPosicionSeleccionadoCanastaDeMarcas: Int = 0
        var subPosicionSeleccionadoCanastaDeMarcas2: Int = 0
        var listaVentasPorMarca: ArrayList<HashMap<String, String>> = ArrayList()
        var sublistaVentasPorMarca: ArrayList<HashMap<String, String>> = ArrayList()
        var sublistasVentasPorMarcas: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
        var sublistasVentasPorMarcas2: ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> = ArrayList()
        var sub1 : ArrayList<HashMap<String, String>> = ArrayList()
        var sub2 :  ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatNumeroEntero : DecimalFormat = DecimalFormat("###,###,##0.##")
    private val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    private var codSupervisor = ""
    private var desSupervisor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canasta_de_marcas2)

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion = FuncionesUtiles(
            this,
            imgTitulo,
            tvTitulo,
            ibtnAnterior,
            ibtnSiguiente,
            tvVendedor,
            contMenu,
            barraMenu,
            llBotonVendedores
        )
        funcion.cargarTitulo(R.drawable.ic_dolar, "Venta por marcas")
        funcion.listaVendedores("COD_VENDEDOR", "DESC_VENDEDOR", "svm_metas_punto_por_linea")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        validacion()
        cargarVentasPorClientes()
        mostrarVentaPorMarcas()
    }

    private fun validacion(){
        if (tvVendedor!!.text.toString() == "Nombre del vendedor"){
            funcion.toast(this, "No hay datos para mostrar.")
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cargarCodigos(){
        try {
            codSupervisor = tvVendedor.text!!.toString().split("-")[0]
            desSupervisor = tvVendedor.text!!.toString().split("-")[1]
        } catch (e: java.lang.Exception){tvVendedor.text = "Nombre del vendedor"}
        if (tvVendedor.text.toString().split("-").size>2){
            desSupervisor = tvVendedor.text!!.toString()
            while (desSupervisor.indexOf("-") != 0){
                desSupervisor = desSupervisor.substring(1, desSupervisor.length)
            }
            desSupervisor = desSupervisor.substring(1, desSupervisor.length)
        }
//        funcion.mensaje(this,codSupervisor,desSupervisor)
    }

    @SuppressLint("Recycle")
    private fun cargarVentasPorClientes(){

        cargarCodigos()

        var sql : String    = ( "select DESC_GTE_MARKETIN, DESC_MODULO, sum(MAYOR_VENTA) MAYOR_VENTA, sum(VENTA_MES1) VENTA_MES1, "
                            +   "		   sum(VENTA_MES2) VENTA_MES2  , sum(META) META, ((CAST(VENTA_MES2 AS NUMBER)*100)/CAST(META AS NUMBER)) AS PORC "
                            +   " from svm_metas_punto_por_linea "
                            +   " group by DESC_GTE_MARKETIN ")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e: Exception){
            e.printStackTrace()
            return
        }

        listaVentasPorMarca = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["DESC_GTE_MARKETIN"] = cursor.getString(cursor.getColumnIndex("DESC_GTE_MARKETIN"))
            datos["DESC_MODULO"] = cursor.getString(cursor.getColumnIndex("DESC_MODULO"))
            datos["MAYOR_VENTA"] = formatNumeroEntero.format(cursor.getString(cursor.getColumnIndex("MAYOR_VENTA")).replace(",", ".").toDouble())
            datos["VENTA_MES1"] = formatNumeroEntero.format(cursor.getString(cursor.getColumnIndex("VENTA_MES1")).replace(",", ".").toDouble())
            datos["VENTA_MES2"] = formatNumeroEntero.format(cursor.getString(cursor.getColumnIndex("VENTA_MES2")).replace(",", ".").toDouble())
            datos["META"] = formatNumeroEntero.format(Integer.parseInt(cursor.getString(cursor.getColumnIndex("META")).replace(",", ".")))
            datos["PORC"] = formatNumeroDecimal.format(funcion.datoDecimal(cursor,"PORC"))
            listaVentasPorMarca.add(datos)
            cursor.moveToNext()
        }

        sublistasVentasPorMarcas = ArrayList()

        for (i in 0 until listaVentasPorMarca.size){
            sql = ("select DESC_MODULO  	  , SUM(MAYOR_VENTA) AS MAYOR_VENTA     , SUM(VENTA_MES1) AS VENTA_MES1 , SUM(VENTA_MES2) AS VENTA_MES2     ,"
                    + " SUM(META) AS META  	  , SUM(MES_1) AS MES_1		            , SUM(MES_2) AS MES_2           , SUM((CAST(VENTA_MES2 AS NUMBER)*100)/CAST(META AS NUMBER)) AS PORC 			 "
                    + " from svm_metas_punto_por_linea "
                    + " where DESC_GTE_MARKETIN = '${listaVentasPorMarca[i]["DESC_GTE_MARKETIN"]}'"
                    + " GROUP BY DESC_MODULO   "
                    + " Order By cast(ifnull(ORD_GTE_MARK,0) as double), cast(ifnull(NRO_ORD_MAG,0) as double), cast(ifnull(ORD_CATEGORIA,0) as double)")

            try {
                cursor = MainActivity.bd!!.rawQuery(sql, null)
                cursor.moveToFirst()
            } catch (e: Exception){
                e.message
                e.printStackTrace()
                return
            }

            sublistaVentasPorMarca = ArrayList()

            for (j in 0 until cursor.count){
                datos = HashMap()
                datos["DESC_MODULO"] = cursor.getString(cursor.getColumnIndex("DESC_MODULO"))
                datos["MAYOR_VENTA"] = cursor.getString(cursor.getColumnIndex("MAYOR_VENTA"))
                datos["VENTA_MES1"] = formatNumeroEntero.format(funcion.datoEntero(cursor,"VENTA_MES1"))
                datos["VENTA_MES2"] = formatNumeroEntero.format(funcion.datoEntero(cursor,"VENTA_MES2"))
                datos["META"] = formatNumeroEntero.format(cursor.getString(cursor.getColumnIndex("META")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                datos["PORC"] = formatNumeroDecimal.format(funcion.datoDecimal(cursor,"PORC"))
                datos["MES_1"] = cursor.getString(cursor.getColumnIndex("MES_1"))
                datos["MES_2"] = cursor.getString(cursor.getColumnIndex("MES_2"))
                sublistaVentasPorMarca.add(datos)
                    
                cursor.moveToNext()
            }

            sublistasVentasPorMarcas.add(sublistaVentasPorMarca)
        }
        sublistasVentasPorMarcas2 = ArrayList()
        for (i in 0 until listaVentasPorMarca.size){
            sub2 = ArrayList()
            for (j in 0 until sublistasVentasPorMarcas[i].size){

                sql = ("select DESC_SUPERVISOR , DESC_VENDEDOR, DESC_GTE_MARKETIN ,"
                        + "DESC_MODULO  	  , COD_CATEGORIA, DESC_CATEGORIA       ,"
                        + "MAYOR_VENTA     , VENTA_MES1   , VENTA_MES2        ,"
                        + "META  	   	  , MES_1		 , MES_2 , META, ((CAST(VENTA_MES2 AS NUMBER)*100)/CAST(META AS NUMBER)) AS PORC 			 "
                        + " from svm_metas_punto_por_linea "
                        + " where DESC_GTE_MARKETIN = '${listaVentasPorMarca[i]["DESC_GTE_MARKETIN"]}' "
                        + "   and DESC_MODULO       = '${sublistasVentasPorMarcas[i][j]["DESC_MODULO"]}' "
                        + " Order By cast(ifnull(ORD_GTE_MARK,0) as double), cast(ifnull(NRO_ORD_MAG,0) as double), cast(ifnull(ORD_CATEGORIA,0) as double)")
                cursor =funcion.consultar(sql)
                sub1 = ArrayList()
                for (k in 0 until cursor.count){
                    datos = HashMap()
                    datos["COD_CATEGORIA"] = cursor.getString(cursor.getColumnIndex("COD_CATEGORIA"))
                    datos["DESC_CATEGORIA"] = cursor.getString(cursor.getColumnIndex("DESC_CATEGORIA"))
                    datos["MAYOR_VENTA"] = cursor.getString(cursor.getColumnIndex("MAYOR_VENTA"))
                    datos["VENTA_MES1"] = formatNumeroEntero.format(funcion.datoEntero(cursor,"VENTA_MES1"))
                    datos["VENTA_MES2"] = formatNumeroEntero.format(funcion.datoEntero(cursor,"VENTA_MES2"))
                    datos["META"] = formatNumeroEntero.format(cursor.getString(cursor.getColumnIndex("META")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                    datos["PORC"] = formatNumeroDecimal.format(funcion.datoDecimal(cursor,"PORC"))
                    datos["MES_1"] = cursor.getString(cursor.getColumnIndex("MES_1"))
                    datos["MES_2"] = cursor.getString(cursor.getColumnIndex("MES_2"))
                    sub1.add(datos)

                    cursor.moveToNext()
                }
                sub2.add(sub1)
            }
            sublistasVentasPorMarcas2.add(sub2)
        }
        return
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarVentaPorMarcas(){
        funcion.vistas     = intArrayOf(R.id.tv1, R.id.tv1, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6,R.id.tv7)
        funcion.valores    = arrayOf("", "DESC_GTE_MARKETIN", "MAYOR_VENTA", "VENTA_MES1", "VENTA_MES2", "META","PORC")
        funcion.subVistas  = intArrayOf(R.id.tvs1, R.id.tvs2, R.id.tvs3, R.id.tvs4, R.id.tvs5, R.id.tvs6,R.id.tvs7)
        funcion.subValores = arrayOf("", "DESC_MODULO", "MAYOR_VENTA", "VENTA_MES1", "VENTA_MES2", "META","PORC")
        funcion.subVistas2 = intArrayOf(R.id.tvs21, R.id.tvs22, R.id.tvs23, R.id.tvs24, R.id.tvs25, R.id.tvs26,R.id.tvs27)
        funcion.subValores2= arrayOf("COD_CATEGORIA", "DESC_CATEGORIA", "MAYOR_VENTA", "VENTA_MES1", "VENTA_MES2", "META","PORC")
        val adapterCanastaDeMarcas: Adapter.ListaDesplegable2 = Adapter.ListaDesplegable2(
            this,
            listaVentasPorMarca,
            sublistasVentasPorMarcas,
            sublistasVentasPorMarcas2,
            R.layout.rep_canasta_de_marcas,
            R.layout.rep_canasta_de_marcas_sublista,
            R.layout.rep_canasta_de_marcas_sublista2,
            funcion.vistas,
            funcion.valores,
            funcion.subVistas,
            funcion.subValores,
            funcion.subVistas2,
            funcion.subValores2,
            R.id.lvSublista,
            R.id.lvSublista2,
            R.layout.rep_canasta_de_marcas_sublista,
            R.layout.rep_canasta_de_marcas_sublista2
        )
        lvCanastaDeMarcas.adapter = adapterCanastaDeMarcas
        lvCanastaDeMarcas.setOnItemClickListener { _: ViewGroup, _: View, _: Int, _: Long ->
            subPosicionSeleccionadoCanastaDeMarcas = 0
            subPosicionSeleccionadoCanastaDeMarcas2 = 0
            FuncionesUtiles.posicionDetalle  = 0
            FuncionesUtiles.posicionDetalle2 = 0
            lvCanastaDeMarcas.invalidateViews()
        }
        tvCanCliTotalValorDeLaCanasta.text = funcion.entero(adapterCanastaDeMarcas.getTotalEntero("MAYOR_VENTA").toString())
        tvCanCliTotalVentas.text = funcion.entero(adapterCanastaDeMarcas.getTotalEntero("VENTA_MES1").toString())
        tvCanCliTotalMetas.text = funcion.entero(adapterCanastaDeMarcas.getTotalEntero("VENTA_MES2").toString())
        tvCanCliTotalPorcCump.text = funcion.decimal(adapterCanastaDeMarcas.getTotalDecimal("PORC")) + "%"
        tvCanCliTotalTotalPercibir.text = funcion.entero(adapterCanastaDeMarcas.getTotalEntero("META").toString())
    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarVentasPorClientes()
            mostrarVentaPorMarcas()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarVentasPorClientes()
        mostrarVentaPorMarcas()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }
}
