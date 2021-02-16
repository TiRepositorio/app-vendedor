package apolo.vendedores.com.informes

//import kotlinx.android.synthetic.main.activity_ventas_por_cliente.barraMenu
//import kotlinx.android.synthetic.main.activity_ventas_por_cliente.contMenu
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_precios_modificados.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class PreciosModificados : AppCompatActivity(){

    companion object{
        var datos: HashMap<String, String> = HashMap()
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

    private fun inicializarElementos(){
        funcion.addItemSpinner(this, "Codigo-Descripcion", "COD_ARTICULO-DESC_ARTICULO")
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_lista, "Listado de precios modificados")
        inicializarETAccion()
        cargarArticulo()
        if (FuncionesUtiles.listaCabecera.size > 0){
            mostrarArticulo()
            cargarPrecios()
            mostrarPrecio()
            btBuscar.setOnClickListener{buscar()}
        } else {
            val dialogo = DialogoAutorizacion(this)
            dialogo.dialogoAccion("finish",accion,"No hay datos para mostrar.","¡Atención!","OK",false)
        }
    }

    private fun cargarArticulo(){
        buscar()
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarArticulo(){
        funcion.vistas  = intArrayOf(R.id.tv1, R.id.tv2, R.id.tv3,R.id.tv4)
        funcion.valores = arrayOf("COD_ARTICULO", "DESC_ARTICULO", "COD_UNIDAD_MEDIDA", "REFERENCIA")
        val adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(
                this,
                FuncionesUtiles.listaCabecera,
                R.layout.inf_prec_mod_lista_articulo,
                funcion.vistas,
                funcion.valores
            )
        lvArticulos.adapter = adapter
        lvArticulos.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvArticulos.invalidateViews()
            cargarPrecios()
            mostrarPrecio()
            tvdCod.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"]
            tvdRef.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_UNIDAD_MEDIDA"] + "-" +
                    FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["REFERENCIA"]
            tvdDesc.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_ARTICULO"]
        }
        try {
            tvdCod.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"]
            tvdRef.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_UNIDAD_MEDIDA"] + "-" +
                    FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["REFERENCIA"]
            tvdDesc.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_ARTICULO"]
        } catch (e : java.lang.Exception) {
            funcion.mensaje(this,"Error",e.message.toString())
        }
    }

    private fun cargarPrecios(){
        val sql = ("SELECT DISTINCT FEC_VIGENCIA, COD_LISTA_PRECIO, PRECIO_ANT, PRECIO_ACT, TIPO, DECIMALES "
                + "  from vtv_precios_fijos "
                + "  WHERE COD_ARTICULO = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"] + "'"
                + "    and COD_UNIDAD_MEDIDA = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_UNIDAD_MEDIDA"] + "'"
                + "  ORDER BY cast(COD_LISTA_PRECIO as double)")
        FuncionesUtiles.listaDetalle = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaDetalle, funcion.consultar(sql))
        for (i in 0 until FuncionesUtiles.listaDetalle.size){
            try{
                FuncionesUtiles.listaDetalle[i]["PRECIO_ANT"] =
                    funcion.entero(FuncionesUtiles.listaDetalle[i]["PRECIO_ANT"].toString())
            } catch (e: Exception){}
            try{
                FuncionesUtiles.listaDetalle[i]["PRECIO_ACT"] =
                    funcion.entero(FuncionesUtiles.listaDetalle[i]["PRECIO_ACT"].toString())
            } catch (e: Exception){}
        }
    }

    private fun mostrarPrecio(){
        funcion.subVistas  = intArrayOf(R.id.tvd1, R.id.tvd2, R.id.tvd3, R.id.tvd4, R.id.tvd5)
        funcion.subValores = arrayOf("FEC_VIGENCIA", "COD_LISTA_PRECIO", "PRECIO_ANT", "PRECIO_ACT", "TIPO")
        val adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(
                this,
                FuncionesUtiles.listaDetalle,
                R.layout.inf_prec_mod_lista_precio,
                funcion.subVistas,
                funcion.subValores
            )
        lvPrecios.adapter = adapter
        lvPrecios.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPrecios.invalidateViews()
        }
    }

    fun buscar(){
        val campos = "DISTINCT COD_ARTICULO, DESC_ARTICULO, COD_UNIDAD_MEDIDA, REFERENCIA "
        val groupBy = ""
        val orderBy = "DESC_ARTICULO"
        FuncionesUtiles.listaCabecera = ArrayList()
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
        if (FuncionesUtiles.listaCabecera.size > 0){
            mostrarArticulo()
            cargarPrecios()
            mostrarPrecio()
        }
    }

    private fun inicializarETAccion(){
        accion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "finish"){
                    finish()
                }
            }

        })
    }

}
