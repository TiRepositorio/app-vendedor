package apolo.vendedores.com.informes

import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_rebotes_por_vendedor.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class RebotesPorCliente : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        tvVendedor.text = menuItem.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        cargar()
        mostrar()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    companion object{
        var funcion : FuncionesUtiles = FuncionesUtiles()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rebotes_por_vendedor)

        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        inicializarElementos()
    }

    fun inicializarElementos(){
        barraMenu.setNavigationItemSelectedListener(this)
        funcion.listaVendedores("COD_VENDEDOR","NOM_VENDEDOR","svm_rebotes_por_cliente"/*,tvVendedor,nav_view_menu*/)
        funcion.cargarTitulo(R.drawable.ic_rebote,"Rebotes por vendedor")
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        cargar()
        mostrar()
    }

    private fun cargar(){
        val sql = ("SELECT /*COD_VENDEDOR,*/CODIGO, DESC_PENALIDAD, MONTO_TOTAL, FECHA/*, NOM_VENDEDOR, DESC_SUPERVISOR*/ "
                        + "  FROM svm_rebotes_por_cliente  "
//                        + " WHERE  "
//                        + "       COD_VENDEDOR = '" + tvVendedor.text.toString().split("-")[0] + "'   AND "
//                        + "       NOM_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "'       "
                        + " ORDER BY  CODIGO, date(substr(FECHA,7) || '-' ||"
                        + "substr(FECHA,4,2) || '-' ||"
                        + "substr(FECHA,1,2)) DESC")

        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
//            datos.put("COD_VENDEDOR",cursor.getString(cursor.getColumnIndex("COD_VENDEDOR")))
            datos["CODIGO"] = cursor.getString(cursor.getColumnIndex("CODIGO"))
            datos["DESC_PENALIDAD"] = cursor.getString(cursor.getColumnIndex("DESC_PENALIDAD"))
            datos["FECHA"] = cursor.getString(cursor.getColumnIndex("FECHA"))
//            datos.put("NOM_VENDEDOR",cursor.getString(cursor.getColumnIndex("NOM_VENDEDOR")))
//            datos.put("DESC_SUPERVISOR",cursor.getString(cursor.getColumnIndex("DESC_SUPERVISOR")))
            datos["MONTO_TOTAL"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_TOTAL")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.valores = arrayOf("CODIGO","FECHA", "DESC_PENALIDAD","MONTO_TOTAL")
        funcion.vistas = intArrayOf(R.id.tv1,R.id.tv1,R.id.tv3,R.id.tv4)
        val adapter: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
                                                                                        FuncionesUtiles.listaCabecera,
                                                                                        R.layout.inf_reb_vend_lista_rebotes,
                                                                                        funcion.vistas,funcion.valores)
        lvRebotes.adapter = adapter
        tvValor.text = funcion.entero(adapter.getTotalEntero("MONTO_TOTAL"))
        lvRebotes.setOnItemClickListener { _, view, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvRebotes.invalidateViews()
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
            cargar()
            mostrar()
        }
    }

}
