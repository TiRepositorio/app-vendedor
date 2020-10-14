package apolo.vendedores.com.reportes

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
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_avance_de_comisiones.*
import kotlinx.android.synthetic.main.activity_avance_de_comisiones2.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class AvanceDeComisiones : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var funcion : FuncionesUtiles = FuncionesUtiles()
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var cursor: Cursor
    }

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

        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.cargarTitulo(R.drawable.ic_dolar,"Avance de comisiones")
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR","svm_liq_premios_vend")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        try {
            cargarCabecera()
            mostrarCabecera()
            cargaDetalle()
            mostrarDetalle()
        } catch (e : java.lang.Exception){
            FuncionesUtiles.listaCabecera = ArrayList<HashMap<String,String>>()
            FuncionesUtiles.listaDetalle = ArrayList<HashMap<String,String>>()
        }
    }

    fun cargarCabecera(){

        var sql : String = (" SELECT  TIP_COM "
                + "       ,  SUM(MONTO_VENTA)    AS MONTO_VENTA "
                + "       ,  SUM(MONTO_A_COBRAR) AS MONTO_A_COBRAR "
                + "  FROM svm_liq_premios_vend "
                + " WHERE COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' "
                + "   AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' "
                + " GROUP BY TIP_COM ")

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
            datos.put("CATEGORIA",cursor.getString(cursor.getColumnIndex("TIP_COM")))
            datos.put("TOTAL",funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VENTA"))))
            datos.put("COMISION",funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR"))))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun cargaDetalle(){

        var sql : String = (" SELECT "
                + "       COD_MARCA"
                + " 	, COD_MARCA || ' - ' || DESC_MARCA AS DESC_MARCA"
                + "     , SUM(MONTO_VENTA) AS MONTO_VENTA"
                + " FROM svm_liq_premios_vend "
                + " WHERE TIP_COM  = '" + FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("CATEGORIA") + "' "
                + "   AND COD_VENDEDOR  = '" + tvVendedor.text.toString().split("-")[0] + "' "
                + "   AND DESC_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' "
                + " GROUP BY COD_MARCA ORDER BY COD_MARCA")

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
            datos.put("MARCA",cursor.getString(cursor.getColumnIndex("DESC_MARCA")))
            datos.put("TOTAL",funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VENTA"))))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarCabecera(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv1,R.id.tv3)
        funcion.valores = arrayOf("CATEGORIA","TOTAL","COMISION")

        val adapterCabecera: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(this,FuncionesUtiles.listaCabecera,R.layout.inf_comision_cabecera,funcion.vistas,funcion.valores
            )
        lvComisionCabecera.adapter = adapterCabecera
        tvCabeceraTotalVenta.setText(funcion.entero(adapterCabecera.getTotalEntero("TOTAL")) + " Gs.")
        tvCabeceraComisionTotal.setText(funcion.entero(adapterCabecera.getTotalEntero("COMISION")) + " Gs.")
        lvComisionCabecera.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            cargaDetalle()
            mostrarDetalle()
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvComisionCabecera.invalidateViews()
        }
    }

    fun mostrarDetalle(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv1)
        funcion.valores = arrayOf("MARCA","TOTAL")
        val adapterDetalle: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,R.layout.inf_comision_detalle,funcion.vistas,funcion.valores
        )
        lvComisionDetalle.adapter = adapterDetalle
        tvDetalleTotalVenta.setText(funcion.entero(adapterDetalle.getTotalEntero("TOTAL")) + " Gs.")
        lvComisionDetalle.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvComisionDetalle.invalidateViews()
        }
    }

    fun actualizarDatos(imageView: ImageView){
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
