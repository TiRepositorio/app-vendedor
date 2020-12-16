package apolo.vendedores.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_canasta_de_marcas.*
import kotlinx.android.synthetic.main.activity_canasta_de_marcas2.*
import kotlinx.android.synthetic.main.barra_vendedores.*
import java.text.DecimalFormat

class CanastaDeMarcas : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var listaCanastaDeMarcas: ArrayList<HashMap<String, String>> = ArrayList()
        var sublistaCanastaDeMarcas: ArrayList<HashMap<String, String>> = ArrayList()
        var subListasCanastaDeMarcas: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatNumeroEntero : DecimalFormat = DecimalFormat("###,###,##0.##")
    private val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    private lateinit var funcion : FuncionesUtiles
    private var codVendedor = ""
    private var desVendedor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas_por_marcas2)
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

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.cargarTitulo(R.drawable.ic_dolar, "Canasta de marcas")
        AvanceDeComisiones.funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_liq_canasta_marca_sup"),this)
        funcion.listaVendedores("COD_VENDEDOR", "DESC_VENDEDOR", "ven_svm_liq_canasta_marca_sup")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        validacion()
        cargarCanastaDeMarcas()
        mostrarCanastaDeMarcas()
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
            codVendedor = tvVendedor.text!!.toString().split("-")[0]
            desVendedor = tvVendedor.text!!.toString().split("-")[1]
        } catch (e: java.lang.Exception){tvVendedor.text = "Nombre del vendedor"}
        if (tvVendedor.text.toString().split("-").size>2){
            desVendedor = tvVendedor.text!!.toString()
            while (desVendedor.indexOf("-") != 0){
                desVendedor = desVendedor.substring(1, desVendedor.length)
            }
            desVendedor = desVendedor.substring(1, desVendedor.length)
        }
//        funcion.mensaje(this,codVendedor,desVendedor)
    }

    @SuppressLint("Recycle")
    fun cargarCanastaDeMarcas(){

        cargarCodigos()

        var sql : String = ("SELECT COD_UNID_NEGOCIO	                                , "
                        + "         DESC_UNID_NEGOCIO		                            , "
                        + " 	    SUM(CAST(VALOR_CANASTA AS NUMBER)) AS VALOR_CANASTA	, "
                        + "         SUM(CAST(VENTAS AS NUMBER)) AS VENTAS               , "
                        + "         SUM(CAST(CUOTA AS NUMBER)) AS CUOTA			        , "
                        + "         SUM(CAST(PORC_CUMP AS NUMBER)) AS PORC_CUMP         , "
                        + "			SUM(CAST(MONTO_A_COBRAR AS NUMBER)) AS MONTO_A_COBRAR "
                        + "   FROM svm_liq_canasta_marca_sup "
                        + "  GROUP BY COD_UNID_NEGOCIO, DESC_UNID_NEGOCIO "
                        + "  ORDER BY COD_UNID_NEGOCIO ASC, DESC_UNID_NEGOCIO ASC ")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        listaCanastaDeMarcas = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_UNID_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("COD_UNID_NEGOCIO"))
            datos["DESC_UNID_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("DESC_UNID_NEGOCIO"))
            datos["VALOR_CANASTA"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".").toDouble())
            datos["VENTAS"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VENTAS")).replace(",", ".").toDouble())
            datos["CUOTA"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("CUOTA")).replace(",", ".")))
            datos["PORC_CUMP"] = formatNumeroDecimal.format((
                    cursor.getString(cursor.getColumnIndex("PORC_CUMP")).replace(",", ".").toDouble()))+"%"
            datos["MONTO_A_COBRAR"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".")))
            listaCanastaDeMarcas.add(datos)
            cursor.moveToNext()
        }

        subListasCanastaDeMarcas = ArrayList()

        for (i in 0 until listaCanastaDeMarcas.size){
            sql = ("SELECT COD_MARCA	                                    , "
                    + "    DESC_MARCA	                                    , "
                    + "    IFNULL(VALOR_CANASTA, '0') AS VALOR_CANASTA      , "
                    + "    IFNULL(VENTAS, '0') AS VENTAS                    , "
                    + "    IFNULL(CUOTA, '0') AS CUOTA			            , "
                    + "    IFNULL(PORC_CUMP, '0') AS PORC_CUMP              , "
                    + "	   IFNULL(MONTO_A_COBRAR, '0') AS MONTO_A_COBRAR      "
                    + "   FROM svm_liq_canasta_marca_sup "
                    + "  WHERE COD_UNID_NEGOCIO = '" + listaCanastaDeMarcas[i]["COD_UNID_NEGOCIO"] + "' "
                    + "  ORDER BY COD_MARCA ASC, DESC_MARCA ASC ")
            try {
                cursor = MainActivity.bd!!.rawQuery(sql, null)
                cursor.moveToFirst()
            } catch (e : Exception){
                e.message
                return
            }

            sublistaCanastaDeMarcas = ArrayList()

            for (j in 0 until cursor.count){
                datos = HashMap()
                datos["COD_MARCA"] = cursor.getString(cursor.getColumnIndex("COD_MARCA"))
                datos["DESC_MARCA"] = cursor.getString(cursor.getColumnIndex("DESC_MARCA"))
//                var prueba = cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".")
//                datos.put("VALOR_CANASTA",formatNumeroEntero.format(prueba.toInt()))
                datos["VALOR_CANASTA"] = formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                datos["VENTAS"] = formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("VENTAS")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                datos["CUOTA"] = formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("CUOTA")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                datos["PORC_CUMP"] = formatNumeroDecimal.format((
                        cursor.getString(cursor.getColumnIndex("PORC_CUMP")).replace(",", ".").replace("null", "0").replace(" ", "0").toDouble()))+"%"
                datos["MONTO_A_COBRAR"] = formatNumeroEntero.format(Integer.parseInt(
                    cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".").replace("null", "0").replace(" ", "0")))
                sublistaCanastaDeMarcas.add(datos)
                cursor.moveToNext()
            }

            subListasCanastaDeMarcas.add(sublistaCanastaDeMarcas)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun mostrarCanastaDeMarcas(){
        funcion.vistas     = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores    = arrayOf("COD_UNID_NEGOCIO","DESC_UNID_NEGOCIO","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        funcion.subVistas  = intArrayOf(R.id.tvs1,R.id.tvs2,R.id.tvs3,R.id.tvs4,R.id.tvs5,R.id.tvs6,R.id.tvs7)
        funcion.subValores = arrayOf("COD_MARCA","DESC_MARCA","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        val adapterCanastaDeMarcas: Adapter.ListaDesplegable = Adapter.ListaDesplegable(this,
            listaCanastaDeMarcas, subListasCanastaDeMarcas,
            R.layout.rep_can_mar_lista_canasta_de_marcas,R.layout.rep_can_mar_lista_sub_canasta_de_marcas,
            funcion.vistas,funcion.valores,funcion.subVistas,funcion.subValores,R.id.lvSubCanastaDeMarcas,R.id.lvSubCanastaDeMarcas
        )
        lvCanastaDeMarcas.adapter = adapterCanastaDeMarcas
        lvCanastaDeMarcas.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle = 0
        }
        tvCanCliTotalValorDeLaCanasta.text = formatNumeroEntero.format(adapterCanastaDeMarcas.getTotalDecimal("VALOR_CANASTA"))
        tvCanCliTotalVentas.text = formatNumeroEntero.format(adapterCanastaDeMarcas.getTotalDecimal("VENTAS"))
        tvCanCliTotalMetas.text = formatNumeroEntero.format(adapterCanastaDeMarcas.getTotalDecimal("CUOTA"))
        tvCanCliTotalPorcCump.text = formatNumeroEntero.format(adapterCanastaDeMarcas.getTotalDecimal("PORC_CUMP")) + "%"
        tvCanCliTotalTotalPercibir.text = formatNumeroEntero.format(adapterCanastaDeMarcas.getTotalDecimal("MONTO_A_COBRAR"))

    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarCanastaDeMarcas()
            mostrarCanastaDeMarcas()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCanastaDeMarcas()
        mostrarCanastaDeMarcas()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }
}
