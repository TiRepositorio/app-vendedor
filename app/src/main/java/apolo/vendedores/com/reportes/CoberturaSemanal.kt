package apolo.vendedores.com.reportes

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_cobertura_semanal.*

class CoberturaSemanal : AppCompatActivity() {

    companion object{
        var funcion : FuncionesUtiles = FuncionesUtiles()
        lateinit var vistas: IntArray
        lateinit var valores: Array<String>
        lateinit var cursor: Cursor
        var datos: HashMap<String, String> = HashMap()
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

    private fun cargarCabecera(){
        val sql = " SELECT distinct " +
                "        COD_VENDEDOR, DESC_VENDEDOR       " +
                "      , SUM(CAST(MONTO_A_COBRAR AS NUMBER)) as MONTO_A_COBRAR " +
                "   FROM fvv_cob_semanal_vend  " +
                "  GROUP BY COD_VENDEDOR, DESC_VENDEDOR"

        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_VENDEDOR"] = cursor.getString(cursor.getColumnIndex("COD_VENDEDOR"))
            datos["DESC_VENDEDOR"] = cursor.getString(cursor.getColumnIndex("COD_VENDEDOR")) + "-" + cursor.getString(cursor.getColumnIndex("DESC_VENDEDOR"))
            datos["MONTO_A_COBRAR"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarCabecera(){
        valores = arrayOf("DESC_VENDEDOR","MONTO_A_COBRAR")
        vistas = intArrayOf(R.id.tv1,R.id.tv1)
        val adapterCabecera: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
                                                                                FuncionesUtiles.listaCabecera,
                                                                                R.layout.rep_cob_sem_lista_cobertura_semanal_cabecera,
                                                                                vistas,
                                                                                valores
                                                                                )
        lvVendCoberturaSemanal.adapter = adapterCabecera
        lvVendCoberturaSemanal.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            cargarDetalle()
            mostrarDetalle()
            lvVendCoberturaSemanal.invalidateViews()
        }
    }

    private fun cargarDetalle(){
        val sql = ("SELECT SEMANA, CLIENT_VENTAS, TOT_CLIENTES, CAST(PORC_COBERTURA AS NUMBER) PORC_COBERTURA, PERIODO "
                + "     FROM fvv_cob_semanal_vend   "
                + "    where COD_VENDEDOR  = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_VENDEDOR"] + "' "
                + "      AND DESC_VENDEDOR = '" +  FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_VENDEDOR"]
                                                               .toString().split("-")[1] + "' "
                + " ORDER BY CAST(SEMANA AS NUMBER) ")

        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaDetalle = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["SEMANA"] = funcion.entero(cursor.getString(cursor.getColumnIndex("SEMANA")))
            datos["CLIENT_VENTAS"] = funcion.entero(cursor.getString(cursor.getColumnIndex("CLIENT_VENTAS")))
            datos["TOT_CLIENTES"] = funcion.entero(cursor.getString(cursor.getColumnIndex("TOT_CLIENTES")))
            datos["PORC_COBERTURA"] = funcion.porcentaje(cursor.getString(cursor.getColumnIndex("PORC_COBERTURA")))
            datos["PERIODO"] = cursor.getString(cursor.getColumnIndex("PERIODO"))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }

    }

    private fun mostrarDetalle(){
        valores = arrayOf("SEMANA","CLIENT_VENTAS","TOT_CLIENTES","PORC_COBERTURA","PERIODO")
        vistas = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5)
        val adapterDetalle: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,
            R.layout.rep_cob_sem_lista_cobertura_semanal_detalle,
            vistas,
            valores
        )
        lvVendCoberturaSemanalDetalle.adapter = adapterDetalle
        lvVendCoberturaSemanalDetalle.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            lvVendCoberturaSemanalDetalle.invalidateViews()
        }
    }


}
