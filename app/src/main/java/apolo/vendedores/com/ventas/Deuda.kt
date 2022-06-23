package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_deuda.*

class Deuda : AppCompatActivity() {

    companion object{
        var codigo : String = ""
        var nombre : String = ""
        var codVen : String = ""
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deuda)

        inicializar()
    }

    private fun inicializar(){
        tvCodigo.text = codigo
        tvDescripcion.text = nombre
        cargar()
        mostrar()
        vencido()
        aVencer()
        cantTotal()
    }

    private fun cargar(){
        val sql : String = (("select COD_EMPRESA, COD_VENDEDOR   , COD_CLIENTE  , COD_SUBCLIENTE, "
                + " FEC_EMISION, FEC_VENCIMIENTO, TIP_DOCUMENTO, NRO_DOCUMENTO ,"
                + " SALDO_CUOTA, DIA_ATRAZO     , ABREVIATURA"
                + " from svm_deuda_cliente "
                + " WHERE COD_CLIENTE       = '" + codigo.split("-")[0] + "'  "
                + "   AND COD_SUBCLIENTE    = '" + codigo.split("-")[1] + "'  "
//                    + " COD_VENDEDOR      = '" + ListaClientes.codVendedor  + "' "
                + "    AND COD_EMPRESA IN (SELECT DISTINCT COD_EMPRESA FROM svm_vendedor_pedido WHERE COD_VENDEDOR = '" + codVen + "')"
                + " Order By date(substr(FEC_VENCIMIENTO,7) || '-' || "
                + " substr(FEC_VENCIMIENTO,4,2) || '-' || "
                + " substr(FEC_VENCIMIENTO,1,2)) "))
        cargarLista(funcion.consultar(sql))
    }

    fun cargarLista(cursor:Cursor){
        FuncionesUtiles.listaDetalle2 = ArrayList()
        for (i in 0 until cursor.count){
            val datos : HashMap<String,String> = HashMap()
            datos["FEC_EMISION"] = funcion.dato(cursor,"FEC_EMISION")
            datos["FEC_VENCIMIENTO"] = funcion.dato(cursor,"FEC_VENCIMIENTO")
            datos["DIA_ATRAZO"] = funcion.dato(cursor,"DIA_ATRAZO")
            datos["TIP_DOCUMENTO"] = funcion.dato(cursor,"TIP_DOCUMENTO")
            datos["NRO_DOCUMENTO"] = funcion.dato(cursor,"NRO_DOCUMENTO")
            datos["SALDO_CUOTA"] = funcion.entero(funcion.dato(cursor,"SALDO_CUOTA"))
            datos["ABREVIATURA"] = funcion.dato(cursor,"ABREVIATURA")
            FuncionesUtiles.listaDetalle2.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.subVistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.subValores = arrayOf("FEC_EMISION"      , "FEC_VENCIMIENTO"  , "DIA_ATRAZO" ,
                                     "TIP_DOCUMENTO"    , "NRO_DOCUMENTO"    , "SALDO_CUOTA",
                                     "ABREVIATURA")
        val adapter:Adapter.AdapterGenericoDetalle2 =
                    Adapter.AdapterGenericoDetalle2(this
                                                    ,FuncionesUtiles.listaDetalle2
                                                    ,R.layout.ven_deu_cli_lista_sub_lista
                                                    ,funcion.subVistas
                                                    ,funcion.subValores)
        lvSubDeuda.adapter = adapter
        lvSubDeuda.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle2 = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvSubDeuda.invalidateViews()
        }

    }

    private fun vencido(){
        val sql : String = ("SELECT IFNULL(CAST(SUM(CAST(SALDO_CUOTA AS NUMBER)) AS TEXT),'0') AS DEUDA, COUNT(*) AS CANT "
                + "   FROM svm_deuda_cliente "
                + "  WHERE COD_SUBCLIENTE     = '" + codigo.split("-")[1] + "'"
//                + "    AND COD_VENDEDOR   = '" + codVen + "'"
                + "    AND COD_EMPRESA IN (SELECT DISTINCT COD_EMPRESA FROM svm_vendedor_pedido WHERE COD_VENDEDOR = '" + codVen + "')"
                + "    AND COD_CLIENTE      = '" + codigo.split("-")[0] + "'"
                + "    AND (CAST(DIA_ATRAZO AS NUMBER)) < 0")
        cargarFooter(tvTVenc,tvNVenc,funcion.consultar(sql))
    }

    private fun aVencer(){
        val sql : String = "SELECT IFNULL(CAST(SUM(CAST(SALDO_CUOTA AS NUMBER)) AS TEXT),'0') AS DEUDA, COUNT(*) AS CANT " +
                           "  FROM svm_deuda_cliente " +
                           "  WHERE COD_SUBCLIENTE     = '" + codigo.split("-")[1] + "'" +
//                           "    AND COD_VENDEDOR   = '" + codVen + "'" +
                           "    AND COD_EMPRESA IN (SELECT DISTINCT COD_EMPRESA FROM svm_vendedor_pedido WHERE COD_VENDEDOR = '" + codVen + "')" +
                           "   AND COD_CLIENTE     = '" + codigo.split("-")[0] + "'" +
                           "   AND (CAST(DIA_ATRAZO AS NUMBER)) >= 0"
        cargarFooter(tvTAVenc,tvNAVenc,funcion.consultar(sql))
    }

    private fun cantTotal(){
        val sql : String = "SELECT IFNULL(CAST(SUM(CAST(SALDO_CUOTA AS NUMBER)) AS TEXT),'0') AS DEUDA, COUNT(*) AS CANT " +
                           "  FROM svm_deuda_cliente " +
                           "  WHERE COD_SUBCLIENTE     = '" + codigo.split("-")[1] + "'" +
//                           "    AND COD_VENDEDOR   = '" + codVen + "'" +
                           "    AND COD_EMPRESA IN (SELECT DISTINCT COD_EMPRESA FROM svm_vendedor_pedido WHERE COD_VENDEDOR = '" + codVen + "')" +
                           "   AND COD_CLIENTE       = '" + codigo.split("-")[0] + "'"
        cargarFooter(tvTCant,tvNCant,funcion.consultar(sql))
    }

    private fun cargarFooter(cantidad: TextView, total:TextView, cursor:Cursor){
        if(cursor.count > 0){
            cantidad.text = funcion.dato(cursor,"CANT")
            total.text = funcion.entero(funcion.dato(cursor,"DEUDA"))
        } else {
            cantidad.setText(0)
            total.setText(0)
        }
    }

}
