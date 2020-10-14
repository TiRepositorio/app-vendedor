package apolo.vendedores.com.reportes

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import apolo.vendedores.com.R
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_cobertura_semanal.*

class CoberturaSemanal : AppCompatActivity() {

    companion object{
        var funcion : FuncionesUtiles = FuncionesUtiles()
        lateinit var vistas: IntArray
        lateinit var valores: Array<String>
        lateinit var cursor: Cursor
        var datos: HashMap<String, String> = HashMap<String, String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobertura_semanal)

        inicializarElementos()

    }

    fun inicializarElementos(){
        cargarCabecera()
        mostrarCabecera()
        if (FuncionesUtiles.listaCabecera.size>0){
            cargarDetalle()
            mostrarDetalle()
        }
    }

    fun cargarCabecera(){
        var sql = " SELECT distinct " +
                "        COD_VENDEDOR, DESC_VENDEDOR       " +
                "      , SUM(CAST(MONTO_A_COBRAR AS NUMBER)) as MONTO_A_COBRAR " +
                "   FROM fvv_cob_semanal_vend  " +
                "  GROUP BY COD_VENDEDOR, DESC_VENDEDOR"

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
            datos.put("COD_VENDEDOR",cursor.getString(cursor.getColumnIndex("COD_VENDEDOR")))
            datos.put("DESC_VENDEDOR",cursor.getString(cursor.getColumnIndex("COD_VENDEDOR")) + "-" + cursor.getString(cursor.getColumnIndex("DESC_VENDEDOR")))
            datos.put("MONTO_A_COBRAR",funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR"))))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrarCabecera(){
        valores = arrayOf("DESC_VENDEDOR","MONTO_A_COBRAR")
        vistas = intArrayOf(R.id.tv1,R.id.tv1)
        val adapterCabecera: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
                                                                                FuncionesUtiles.listaCabecera,
                                                                                R.layout.rep_cob_sem_lista_cobertura_semanal_cabecera,
                                                                                vistas,
                                                                                valores
                                                                                )
        lvVendCoberturaSemanal.adapter = adapterCabecera
        lvVendCoberturaSemanal.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            cargarDetalle()
            mostrarDetalle()
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvVendCoberturaSemanal.invalidateViews()
        }
    }

    fun cargarDetalle(){
        var sql = ("SELECT SEMANA, CLIENT_VENTAS, TOT_CLIENTES, CAST(PORC_COBERTURA AS NUMBER) PORC_COBERTURA, PERIODO "
                + "     FROM fvv_cob_semanal_vend   "
                + "    where COD_VENDEDOR  = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera].get("COD_VENDEDOR") + "' "
                + "      AND DESC_VENDEDOR = '" +  FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera].get("DESC_VENDEDOR")
                                                               .toString().split("-")[1] + "' "
                + " ORDER BY CAST(SEMANA AS NUMBER) ")

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
            datos.put("SEMANA",funcion.entero(cursor.getString(cursor.getColumnIndex("SEMANA"))))
            datos.put("CLIENT_VENTAS",funcion.entero(cursor.getString(cursor.getColumnIndex("CLIENT_VENTAS"))))
            datos.put("TOT_CLIENTES",funcion.entero(cursor.getString(cursor.getColumnIndex("TOT_CLIENTES"))))
            datos.put("PORC_COBERTURA",funcion.porcentaje(cursor.getString(cursor.getColumnIndex("PORC_COBERTURA"))))
            datos.put("PERIODO",cursor.getString(cursor.getColumnIndex("PERIODO")))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }

    }

    fun mostrarDetalle(){
        valores = arrayOf("SEMANA","CLIENT_VENTAS","TOT_CLIENTES","PORC_COBERTURA","PERIODO")
        vistas = intArrayOf(R.id.tv1,R.id.tv1,R.id.tv3,R.id.tv4,R.id.tv5)
        val adapterDetalle: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,
            R.layout.rep_cob_sem_lista_cobertura_semanal_detalle,
            vistas,
            valores
        )
        lvVendCoberturaSemanalDetalle.adapter = adapterDetalle
        lvVendCoberturaSemanalDetalle.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvVendCoberturaSemanalDetalle.invalidateViews()
        }
    }


}
