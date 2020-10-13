package apolo.vendedores.com.informes

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_pedidos_en_reparto.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class PedidosEnReparto : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        this.tvVendedor.text = menuItem.title.toString()

        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    companion object{
        var funcion : FuncionesUtiles = FuncionesUtiles()
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var cursor: Cursor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos_en_reparto)

        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        inicializarElementos()
    }

    fun inicializarElementos(){
        barraMenu.setNavigationItemSelectedListener(this)
        funcion.cargarTitulo(R.drawable.ic_ruteo_lista,"Pedidos en reparto")
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR", "svm_pedidos_en_reparto")
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        cargar()
        mostrar()
    }

    fun cargar(){
        var sql = "SELECT DISTINCT DESC_REPARTIDOR ,TEL_REPARTIDOR " +
                         "  FROM svm_pedidos_en_reparto " +
//                         " WHERE " +
//                         "       COD_VENDEDOR   = '" + this.tvVendedor.text.toString().split("-")[0] + "'        AND" +
                         " ORDER BY DESC_REPARTIDOR ASC"
        cursor = funcion!!.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList<HashMap<String, String>>()
        for (i in 0 until cursor.count){
            datos = HashMap<String, String>()
            datos.put("DESC_REPARTIDOR",funcion!!.dato(cursor,"DESC_REPARTIDOR"))
            datos.put("TEL_REPARTIDOR",funcion!!.dato(cursor,"TEL_REPARTIDOR"))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
        FuncionesUtiles.subListaDetalle = ArrayList<ArrayList<HashMap<String,String>>>()
        for(i in 0 until FuncionesUtiles.listaCabecera.size){
            sql = ("select COD_EMPRESA     , NRO_PLANILLA    , DESC_REPARTIDOR ,"
                    + "TEL_REPARTIDOR  , FEC_PLANILLA    ,"
                    + "FEC_COMPROBANTE , TIP_COMPROBANTE , NRO_COMPROBANTE ,"
                    + "COD_CLIENTE     , COD_SUBCLIENTE  , NOM_CLIENTE     ,"
                    + "NOM_SUBCLIENTE  , SIGLAS          , DECIMALES       ,"
                    + "TOT_COMPROBANTE /*, COD_VENDEDOR    , DESC_VENDEDOR   ,"
                    + "COD_SUPERVISOR  , DESC_SUPERVISOR*/, ESTADO"
                    + "  FROM svm_pedidos_en_reparto  "
                    + " WHERE "
//                    + "       COD_VENDEDOR   = '" + this.tvVendedor.text.toString().split("-")[0] + "'        AND"
                    + "       DESC_REPARTIDOR= '" + FuncionesUtiles.listaCabecera.get(i).get("DESC_REPARTIDOR") + "'    "
                    + " Order By DESC_REPARTIDOR, Cast (NRO_PLANILLA as double) ")

            cursor = funcion.consultar(sql)
            FuncionesUtiles.listaDetalle = ArrayList<HashMap<String,String>>()

            for (i in 0 until cursor.count){
                datos = HashMap<String, String>()
                datos.put("NRO_PLANILLA"    , funcion.dato(cursor,"NRO_PLANILLA"))
                datos.put("FEC_PLANILLA"    , funcion.dato(cursor,"FEC_PLANILLA"))
                datos.put("NRO_COMPROBANTE" , funcion.dato(cursor,"NRO_COMPROBANTE"))
                datos.put("FEC_COMPROBANTE" , funcion.dato(cursor,"FEC_COMPROBANTE"))
                datos.put("COD_CLIENTE"     , funcion.dato(cursor,"COD_CLIENTE")+"-"+funcion.dato(cursor,"COD_SUBCLIENTE"))
                datos.put("NOM_SUBCLIENTE"  , funcion.dato(cursor,"NOM_SUBCLIENTE"))
                datos.put("ESTADO"          , funcion.dato(cursor,"ESTADO"))
                datos.put("TOT_COMPROBANTE" , funcion.entero(funcion.dato(cursor,"TOT_COMPROBANTE"))+funcion.dato(cursor,"SIGLAS"))
                FuncionesUtiles.listaDetalle.add(datos)
                cursor.moveToNext()
            }
            FuncionesUtiles.subListaDetalle.add(FuncionesUtiles.listaDetalle)
        }
    }

    fun mostrar(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2)
        funcion.valores = arrayOf("DESC_REPARTIDOR" ,"TEL_REPARTIDOR")
        funcion.subVistas  = intArrayOf(R.id.tvs1,R.id.tvs2,R.id.tvs3,R.id.tvs4,R.id.tvs5,R.id.tvs6,R.id.tvs7,R.id.tvs8)
        funcion.subValores = arrayOf("NRO_PLANILLA"     , "FEC_PLANILLA"    , "NRO_COMPROBANTE" ,
                                     "FEC_COMPROBANTE"  , "COD_CLIENTE"     , "NOM_SUBCLIENTE"  ,
                                     "TOT_COMPROBANTE"  , "ESTADO")

        val adapter:Adapter.ListaDesplegable =
            Adapter.ListaDesplegable(this,
                                     FuncionesUtiles.listaCabecera,
                                     FuncionesUtiles.subListaDetalle,
                                     R.layout.inf_ped_rep_lista_pedidos,
                                     R.layout.inf_ped_rep_sub_lista_pedidos,
                                     funcion.vistas,
                                     funcion.valores,
                                     funcion.subVistas,
                                     funcion.subValores,
                                     R.id.lvSubtabla,
                                     R.id.llSubTabla)

        lvPedidosEnReparto.adapter = adapter

    }

    fun actualizarDatos(imageView: ImageView){
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
