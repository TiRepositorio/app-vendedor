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
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_avance_de_comisiones.*
import kotlinx.android.synthetic.main.activity_avance_de_comisiones2.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class AvanceDeComisiones : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private var codVendedor = ""
    private var desVendedor = ""

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCabecera()
        mostrarCabecera()
        cargaDetalle()
        mostrarDetalle()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avance_de_comisiones2)

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
        funcion.cargarTitulo(R.drawable.ic_dolar,"Avance de comisiones")
        funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_liq_premios_vend"),this)
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR","ven_svm_liq_premios_vend")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        validacion()
        try {
            cargarCabecera()
            mostrarCabecera()
            cargaDetalle()
            mostrarDetalle()
        } catch (e : java.lang.Exception){
            FuncionesUtiles.listaCabecera = ArrayList()
            FuncionesUtiles.listaDetalle = ArrayList()
        }
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

    private fun cargarCabecera(){
        cargarCodigos()
        val sql : String = (" SELECT  TIP_COM "
                + "       ,  SUM(MONTO_VENTA)    AS MONTO_VENTA "
                + "       ,  SUM(MONTO_A_COBRAR) AS MONTO_A_COBRAR "
                + "  FROM svm_liq_premios_vend "
                + " WHERE COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' "
//                + "   AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' "
                + " GROUP BY TIP_COM ")

        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["CATEGORIA"] = cursor.getString(cursor.getColumnIndex("TIP_COM"))
            datos["TOTAL"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VENTA")))
            datos["COMISION"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun cargaDetalle(){

        val sql : String = (" SELECT "
                + "       COD_MARCA"
                + " 	, COD_MARCA || ' - ' || DESC_MARCA AS DESC_MARCA"
                + "     , SUM(MONTO_VENTA) AS MONTO_VENTA"
                + " FROM svm_liq_premios_vend "
                + " WHERE TIP_COM  = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["CATEGORIA"] + "' "
//                + "   AND COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' "
//                + "   AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' "
                + " GROUP BY COD_MARCA ORDER BY COD_MARCA")

        cursor = funcion.consultar(sql)

        FuncionesUtiles.listaDetalle = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["MARCA"] = cursor.getString(cursor.getColumnIndex("DESC_MARCA"))
            datos["TOTAL"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VENTA")))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarCabecera(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3)
        funcion.valores = arrayOf("CATEGORIA","TOTAL","COMISION")

        val adapterCabecera: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(this,FuncionesUtiles.listaCabecera,R.layout.inf_comision_cabecera,funcion.vistas,funcion.valores
            )
        lvComisionCabecera.adapter = adapterCabecera
        tvCabeceraTotalVenta.text = funcion.entero(adapterCabecera.getTotalEntero("TOTAL")) + " Gs."
        tvCabeceraComisionTotal.text = funcion.entero(adapterCabecera.getTotalEntero("COMISION")) + " Gs."
        lvComisionCabecera.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            cargaDetalle()
            mostrarDetalle()
            lvComisionCabecera.invalidateViews()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarDetalle(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2)
        funcion.valores = arrayOf("MARCA","TOTAL")
        val adapterDetalle: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,R.layout.inf_comision_detalle,funcion.vistas,funcion.valores
        )
        lvComisionDetalle.adapter = adapterDetalle
        tvDetalleTotalVenta.text = funcion.entero(adapterDetalle.getTotalEntero("TOTAL")) + " Gs."
        lvComisionDetalle.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            lvComisionDetalle.invalidateViews()
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
            cargarCabecera()
            mostrarCabecera()
            cargaDetalle()
            mostrarDetalle()
        }
    }

}
