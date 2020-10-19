package apolo.vendedores.com.ventas

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_deuda_de_clientes.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class DeudaDeClientes : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        FuncionesUtiles.posicionCabecera = 0
        tvVendedor.text = menuItem.title.toString()
        cargarTodo()
        mostrar()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var funcion : FuncionesUtiles
        lateinit var cursor: Cursor
        var venta : Boolean = false
        var codCliente : String = ""
        var codSubcliente : String = ""
        var descSubcliente : String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deuda_de_clientes)

        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,llBuscar,spBuscar,etBuscar,btBuscar)
        btBuscar.isEnabled = true
        if (!venta){
            funcion = FuncionesUtiles(this,imgTitulo,tvTitulo)
            btBuscar.isEnabled = false
            barraMenu.layoutParams.width = 0
        }
        inicializarElementos()
    }

    private fun inicializarElementos(){
        barraMenu.setNavigationItemSelectedListener(this)
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        funcion.inicializaContadores()
        if (!venta){
            funcion.cargarTitulo(R.drawable.ic_dolar_tach,"Deuda de clientes")
            funcion.addItemSpinner(this,"Codigo-Nombre","COD_CLIENTE-DESC_CLIENTE,DESC_SUBCLIENTE")
            tvVendedor.setOnClickListener { funcion.mostrarMenu() }
        } else {
            funcion.cargarTitulo(R.drawable.ic_dolar_tach, "$codCliente-$codSubcliente-$descSubcliente")
        }
        cargarTodo()
        mostrar()
        btBuscar.setOnClickListener{buscar()}
        deuda(btDeuda)
    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarTodo()
            mostrar()
        }
    }

    private fun cargarTodo(){
        var sql : String = ("SELECT COD_CLIENTE     , DESC_CLIENTE             , COD_SUBCLIENTE,"
                         +  "       DESC_SUBCLIENTE, Sum(SALDO_CUOTA) AS SALDO , DESC_VENDEDOR ,"
                         +  "       DESC_SUPERVISOR, COD_VENDEDOR "
                         +  "  FROM svm_deuda_cliente  "
                         +  " WHERE COD_VENDEDOR = '" + tvVendedor.text.toString().split("-")[0] + "' "
                         +  " GROUP BY COD_CLIENTE, COD_SUBCLIENTE "
                         +  " Order By Cast(COD_CLIENTE as NUMBER), Cast(COD_SUBCLIENTE as NUMBER) ")
        if (venta) {
            sql = ("select COD_EMPRESA, COD_VENDEDOR   , COD_CLIENTE  , COD_SUBCLIENTE, "
                    + " FEC_EMISION, FEC_VENCIMIENTO, TIP_DOCUMENTO, NRO_DOCUMENTO ,"
                    + " SALDO_CUOTA, DIA_ATRAZO     , ABREVIATURA"
                    + " from svm_deuda_cliente "
                    + " WHERE COD_CLIENTE       = '" + codCliente                 + "'  "
                    + "   AND COD_SUBCLIENTE    = '" + codSubcliente              + "'  "
//                    + " COD_VENDEDOR      = '" + ListaClientes.codVendedor  + "' "
                    + " Order By date(substr(FEC_VENCIMIENTO,7) || '-' || "
                    + " substr(FEC_VENCIMIENTO,4,2) || '-' || "
                    + " substr(FEC_VENCIMIENTO,1,2)) ")
        }
        cargarLista(funcion.consultar(sql))
    }

    private fun buscar(){
        val campos = "COD_CLIENTE,DESC_CLIENTE,COD_SUBCLIENTE,DESC_SUBCLIENTE,Sum(SALDO_CUOTA) AS SALDO,DESC_VENDEDOR," +
                            "DESC_SUPERVISOR,COD_VENDEDOR "
        val groupBy = "COD_CLIENTE, COD_SUBCLIENTE"
        val orderBy = "Cast(COD_CLIENTE as NUMBER), Cast(COD_SUBCLIENTE as NUMBER)"
        cargarLista(funcion.buscar("svm_deuda_cliente",campos,groupBy,orderBy))
        mostrar()
    }

    private fun cargarLista(cursor: Cursor){
        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,cursor)
        for (i in 0 until cursor.count){
            FuncionesUtiles.listaCabecera[i]["SALDO_CUOTA"] = funcion.entero(FuncionesUtiles.listaCabecera[i]["SALDO_CUOTA"].toString())
        }
    }

    private fun mostrar() {
        funcion.vistas = intArrayOf(R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.tv7)
        funcion.valores = arrayOf("FEC_EMISION"     , "FEC_VENCIMIENTO" , "DIA_ATRAZO"  ,
                                  "TIP_DOCUMENTO"   , "NRO_DOCUMENTO"   , "SALDO_CUOTA" , "ABREVIATURA")
        val adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(
                this
                , FuncionesUtiles.listaCabecera
                , R.layout.ven_deu_cli_lista_deuda
                , funcion.vistas
                , funcion.valores
            )
        lvDeuda.adapter = adapter
        lvDeuda.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvDeuda.invalidateViews()
        }
        if (FuncionesUtiles.listaCabecera.size>0){
            tvTotalDeuda.text = funcion.entero(adapter.getTotalEntero("SALDO_CUOTA"))
        }
    }

    private fun deuda(btDeuda:Button){
        btDeuda.setOnClickListener{
            Deuda.codVen = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_VENDEDOR"].toString()
            Deuda.codigo = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_CLIENTE"].toString() + "-" +
                           FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_SUBCLIENTE"].toString()
            Deuda.nombre = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_SUBCLIENTE"].toString()
            val deuda = Intent(this, Deuda::class.java)
            startActivity(deuda)
        }
    }

}
