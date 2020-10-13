package apolo.vendedores.com.informes

//import kotlinx.android.synthetic.main.activity_ventas_por_cliente.barraMenu
//import kotlinx.android.synthetic.main.activity_ventas_por_cliente.contMenu
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_precios_modificados.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class PreciosModificados : AppCompatActivity(){

    companion object{
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var funcion : FuncionesUtiles
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_precios_modificados)

        funcion = FuncionesUtiles(
            this,
            imgTitulo,
            tvTitulo,
            llBuscar,
            spBuscar,
            etBuscar,
            btBuscar
        )
        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.addItemSpinner(this, "Codigo-Descripcion", "COD_ARTICULO-DESC_ARTICULO")
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_lista, "Listado de precios modificados")
        cargarArticulo()
        mostrarArticulo()
        cargarPrecios()
        mostrarPrecio()
        btBuscar.setOnClickListener{buscar()}
    }

    fun cargarArticulo(){
        buscar()
    }

    fun mostrarArticulo(){
        funcion.vistas  = intArrayOf(R.id.tv1, R.id.tv2, R.id.tv3,R.id.tv4)
        funcion.valores = arrayOf("COD_ARTICULO", "DESC_ARTICULO", "COD_UNIDAD_MEDIDA", "REFERENCIA")
        var adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(
                this,
                FuncionesUtiles.listaCabecera,
                R.layout.inf_prec_mod_lista_articulo,
                funcion.vistas,
                funcion.valores
            )
        lvArticulos.adapter = adapter
        lvArticulos.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvArticulos.invalidateViews()
            cargarPrecios()
            mostrarPrecio()
            tvdCod.setText(
                FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera)
                    .get("COD_ARTICULO")
            )
            tvdRef.setText(
                FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("COD_UNIDAD_MEDIDA") + "-" +
                FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("REFERENCIA")
            )
            tvdDesc.setText(
                FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get(
                    "DESC_ARTICULO"
                )
            )
        }
        tvdCod.setText(
            FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("COD_ARTICULO")
        )
        tvdRef.setText(
            FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("COD_UNIDAD_MEDIDA") + "-" +
            FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("REFERENCIA")
        )
        tvdDesc.setText(
            FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("DESC_ARTICULO")
        )
    }

    fun cargarPrecios(){
        val sql = ("select FEC_VIGENCIA, COD_LISTA_PRECIO, PRECIO_ANT, PRECIO_ACT, TIPO, DECIMALES "
                + "  from vtv_precios_fijos "
                + "  WHERE COD_ARTICULO = '" + FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("COD_ARTICULO") + "'"
                + "    and COD_UNIDAD_MEDIDA = '" + FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("COD_UNIDAD_MEDIDA") + "'"
                + "  ORDER BY cast(COD_LISTA_PRECIO as double)")
        FuncionesUtiles.listaDetalle = ArrayList<HashMap<String, String>>()
        funcion.cargarLista(FuncionesUtiles.listaDetalle, funcion.consultar(sql))
        for (i in 0 until FuncionesUtiles.listaDetalle.size){
            try{
                FuncionesUtiles.listaDetalle.get(i).put("PRECIO_ANT", funcion.entero(FuncionesUtiles.listaDetalle.get(i).get("PRECIO_ANT").toString())
                )
            } catch (e: Exception){}
            try{
                FuncionesUtiles.listaDetalle.get(i).put("PRECIO_ACT", funcion.entero(FuncionesUtiles.listaDetalle.get(i).get("PRECIO_ACT").toString())
                )
            } catch (e: Exception){}
        }
    }

    fun mostrarPrecio(){
        funcion.subVistas  = intArrayOf(R.id.tvd1, R.id.tvd2, R.id.tvd3, R.id.tvd4, R.id.tvd5)
        funcion.subValores = arrayOf("FEC_VIGENCIA", "COD_LISTA_PRECIO", "PRECIO_ANT", "PRECIO_ACT", "TIPO")
        var adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(
                this,
                FuncionesUtiles.listaDetalle,
                R.layout.inf_prec_mod_lista_precio,
                funcion.subVistas,
                funcion.subValores
            )
        lvPrecios.adapter = adapter
        lvPrecios.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPrecios.invalidateViews()
        }
    }

    fun buscar(){
        var campos = "DISTINCT COD_ARTICULO, DESC_ARTICULO, COD_UNIDAD_MEDIDA, REFERENCIA "
        var groupBy = ""
        var orderBy = "DESC_ARTICULO"
        FuncionesUtiles.listaCabecera = ArrayList<HashMap<String, String>>()
        funcion.cargarLista(
            FuncionesUtiles.listaCabecera, funcion.buscar(
                "vtv_precios_fijos",
                campos,
                groupBy,
                orderBy,
                ""
            )
        )
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        mostrarArticulo()
        cargarPrecios()
        mostrarPrecio()
    }

}
