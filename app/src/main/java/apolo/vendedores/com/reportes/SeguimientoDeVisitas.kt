package apolo.vendedores.com.reportes

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_seguimiento_de_visitas.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class SeguimientoDeVisitas : AppCompatActivity(){

    companion object {
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var funcion: FuncionesUtiles
        lateinit var cursor: Cursor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento_de_visitas)

        funcion = FuncionesUtiles(imgTitulo, tvTitulo)
        inicializarElementos()
    }

    fun inicializarElementos() {
        llBotonVendedores.visibility = View.GONE
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_visitado, "Seguimiento de visitas")
        tvVendedor.setOnClickListener { funcion.mostrarMenu() }
        cargarPeriodo()
        mostrarPeriodo()
        cargarDetalle()
        mostrarDetalle()
    }

    fun actualizarDatos(imageView: ImageView) {
        imageView.setOnClickListener {
            if (imageView.id == ibtnAnterior.id) {
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
//            cargarCabecera()
//            mostrarCabecera()
//            cargaDetalle()
//            mostrarDetalle()
        }
    }

    fun cargarPeriodo() {
        var sql: String =
            "SELECT DISTINCT SEMANA, FEC_INICIO || ' AL ' || FEC_FIN AS PERIODO FROM svm_seg_visitas_semanal ORDER BY FEC_INICIO"
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList<HashMap<String, String>>()
        for (i in 0 until cursor.count) {
            datos = HashMap<String, String>()
            datos.put("SEMANA", funcion.dato(cursor, "SEMANA"))
            datos.put("PERIODO", funcion.dato(cursor, "PERIODO"))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarPeriodo() {
        funcion.vistas = intArrayOf(R.id.tv1, R.id.tv1)
        funcion.valores = arrayOf("SEMANA", "PERIODO")
        var adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(
                this
                , FuncionesUtiles.listaCabecera
                , R.layout.rep_seg_vis_lista_periodo
                , funcion.vistas
                , funcion.valores
            )
        lvPeriodo.adapter = adapter
        lvPeriodo.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPeriodo.invalidateViews()
            cargarDetalle()
            mostrarDetalle()
        }
    }

    fun cargarDetalle(){
        if (FuncionesUtiles.listaCabecera.size==0){
            FuncionesUtiles.listaDetalle = ArrayList<HashMap<String,String>>()
            return
        }
        var sql: String = "SELECT IFNULL(COD_VENDEDOR,'0')    AS COD_VENDEDOR  , IFNULL(DESC_VENDEDOR,'')       AS NOMBRE_VENDEDOR        , " +
                          "       IFNULL(CANTIDAD,'0')        AS CANTIDAD      , IFNULL(CANT_VENDIDO,'0')       AS CANT_VENDIDO           , " +
                          "       IFNULL(CANT_NO_VENDIDO,'0') AS CANT_NO_VENTA , IFNULL(CANT_NO_VISITADO,'0')   AS CANT_NO_VISITADO       , " +
                          "       IFNULL(PORC,'0.0') PORC " +
                          "  FROM svm_seg_visitas_semanal " +
                          " WHERE SEMANA = '" + FuncionesUtiles.listaCabecera.get(FuncionesUtiles.posicionCabecera).get("SEMANA") + "' " +
                          " ORDER BY COD_VENDEDOR "
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaDetalle = ArrayList<HashMap<String, String>>()
        for (i in 0 until cursor.count) {
            datos = HashMap<String, String>()
//            datos.put("COD_VENDEDOR", funcion.dato(cursor, "COD_VENDEDOR"))
//            datos.put("NOMBRE_VENDEDOR", funcion.dato(cursor, "NOMBRE_VENDEDOR"))
            datos.put("CANTIDAD", funcion.entero(funcion.dato(cursor, "CANTIDAD")))
            datos.put("CANT_VENDIDO", funcion.entero(funcion.dato(cursor, "CANT_VENDIDO")))
            datos.put("CANT_NO_VENTA", funcion.entero(funcion.dato(cursor, "CANT_NO_VENTA")))
            datos.put("CANT_NO_VISITADO", funcion.entero(funcion.dato(cursor, "CANT_NO_VISITADO")))
            datos.put("PORC", funcion.decimal(funcion.dato(cursor, "PORC")))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarDetalle(){
        funcion.subVistas = intArrayOf(R.id.tvd1, R.id.tvd2, R.id.tvd3, R.id.tvd4, R.id.tvd5, R.id.tvd6, R.id.tvd7)
        funcion.subValores = arrayOf("", ""  ,"CANTIDAD"         ,
                                     "CANT_VENDIDO", "CANT_NO_VENTA"    ,"CANT_NO_VISITADO" ,
                                     "PORC")
//        funcion.subValores = arrayOf("COD_VENDEDOR", "NOMBRE_VENDEDOR"  ,"CANTIDAD"         ,
//            "CANT_VENDIDO", "CANT_NO_VENTA"    ,"CANT_NO_VISITADO" ,
//            "PORC")
        var adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(
                this
                , FuncionesUtiles.listaDetalle
                , R.layout.rep_seg_vis_lista_vendedores
                , funcion.subVistas
                , funcion.subValores
            )
        lvVendedroes.adapter = adapter
        lvVendedroes.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvVendedroes.invalidateViews()
        }
    }
}
