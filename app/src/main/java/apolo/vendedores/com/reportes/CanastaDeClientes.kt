package apolo.vendedores.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
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
import kotlinx.android.synthetic.main.activity_canasta_de_clientes.*
import kotlinx.android.synthetic.main.activity_canasta_de_marcas2.*
import kotlinx.android.synthetic.main.barra_vendedores.*
import java.text.DecimalFormat

class CanastaDeClientes : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var listaCanastaDeClientes: ArrayList<HashMap<String, String>> = ArrayList()
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
        setContentView(R.layout.activity_canasta_de_clientes2)
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
        funcion.cargarTitulo(R.drawable.ic_dolar, "Canasta de clientes")
        AvanceDeComisiones.funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_liq_canasta_cte_sup"),this)
        funcion.listaVendedores("COD_VENDEDOR", "DESC_VENDEDOR", "ven_svm_liq_canasta_cte_sup")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        validacion()
        cargarCanastaDeClientes()
        mostrarCanastaDeClientes()
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
    private fun cargarCanastaDeClientes(){

        val sql : String = ("SELECT COD_CLIENTE	, DESC_CLIENTE		, "
                + " 		 VALOR_CANASTA	    , VENTAS			, " +
                "            CUOTA				, PUNTAJE_CLIENTE	, "
                + "			 PORC_CUMP		    , MONTO_A_COBRAR "
                + " FROM svm_liq_canasta_cte_sup "
                + " ORDER BY DESC_CLIENTE ASC ")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        listaCanastaDeClientes = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_CLIENTE"] = cursor.getString(cursor.getColumnIndex("COD_CLIENTE"))
            datos["DESC_CLIENTE"] = cursor.getString(cursor.getColumnIndex("DESC_CLIENTE"))
            datos["VALOR_CANASTA"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".").toDouble())
            datos["VENTAS"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VENTAS")).replace(",", ".").toDouble())
            datos["CUOTA"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("CUOTA")).replace(",", ".")))
            datos["PUNTAJE_CLIENTE"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("PUNTAJE_CLIENTE")).replace(",", ".")))
            datos["PORC_CUMP"] = formatNumeroDecimal.format((
                    cursor.getString(cursor.getColumnIndex("PORC_CUMP")).replace(",", ".").toDouble()))+"%"
            datos["MONTO_A_COBRAR"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".")))
            listaCanastaDeClientes.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarCanastaDeClientes(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores = arrayOf("COD_CLIENTE","DESC_CLIENTE","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        val adapterCanastaDeClientes: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            listaCanastaDeClientes,R.layout.rep_can_cli_lista_canasta_de_clientes,funcion.vistas,funcion.valores
        )
        lvCanastaDeClientes.adapter = adapterCanastaDeClientes
        lvCanastaDeClientes.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvCanastaDeClientes.invalidateViews()
        }
        tvCanCliTotalValorDeLaCanasta.text = formatNumeroEntero.format(adapterCanastaDeClientes.getTotalEntero("VALOR_CANASTA"))
        tvCanCliTotalVentas.text = formatNumeroEntero.format(adapterCanastaDeClientes.getTotalEntero("VENTAS"))
        tvCanCliTotalMetas.text = formatNumeroEntero.format(adapterCanastaDeClientes.getTotalEntero("CUOTA"))
        tvCanCliTotalPorcCump.text = formatNumeroEntero.format(adapterCanastaDeClientes.getTotalDecimal("PORC_CUMP")) + "%"
        tvCanCliTotalTotalPercibir.text = formatNumeroEntero.format(adapterCanastaDeClientes.getTotalEntero("MONTO_A_COBRAR"))
    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarCanastaDeClientes()
            mostrarCanastaDeClientes()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCanastaDeClientes()
        mostrarCanastaDeClientes()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

}
