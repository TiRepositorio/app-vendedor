package apolo.vendedores.com.ventas

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_promociones.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class Promociones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promociones)

        inicializarElementos()
    }

    companion object{
        var codListaPrecio : String = ""
        var condicionVenta : String = ""
        var codArticulo    : String = ""
        var posPromocion   : Int = 0
//        var promocion = true
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccionPromo : EditText
    }

    lateinit var funcion : FuncionesUtiles
    private lateinit var listaPromociones : ArrayList<HashMap<String, String>>
    private var inNro = ""

    private fun inicializarElementos(){
        funcion = FuncionesUtiles(this, imgTitulo, tvTitulo, llBuscar, spBuscar, etBuscar, btBuscar)
        funcion.cargarTitulo(R.drawable.ic_promocion, "Promociones")
        funcion.addItemSpinner(this, "Número-Descripción", "NRO_PROMOCION-DESCRIPCION")

        buscarPromociones()
        btBuscar.setOnClickListener{buscarPromociones()}
        etAccionPromo = accionPromocion
        inicializarEtAccion(accionPromocion)
        inicializarEtAccion(etAccionPromo)
    }

    @SuppressLint("Recycle")
    private fun buscarPromociones(){
        FuncionesUtiles.posicionCabecera = 0
        posPromocion = 0
        seleccionaPromociones()

        val campos : String = " DISTINCT z.NRO_PROMOCION, z.TIP_CLIENTE, z.DESCRIPCION, z.COMENTARIO, z.COD_CONDICION_VENTA," +
                            " z.FEC_VIGENCIA , z.IND_TIPO   ,  z.IND_COMBO "
        val groupBy : String  = " z.NRO_PROMOCION, z.TIP_CLIENTE, z.DESCRIPCION, z.COMENTARIO, z.COD_CONDICION_VENTA," +
                             " z.FEC_VIGENCIA , z.IND_TIPO   , z.CANT_VENTA , z.IND_COMBO "
        val orderBy = " CAST(NRO_PROMOCION AS INTEGER) asc "
        val tabla = " svm_promociones_art_cab z "
        var where : String  = (" AND (z.COD_CONDICION_VENTA   = '$condicionVenta'             or trim(z.COD_CONDICION_VENTA)  = '' or z.COD_CONDICION_VENTA IS NULL) "
                            +  "    AND (z.TIP_CLIENTE        = '${ListaClientes.tipCliente}' or trim(z.TIP_CLIENTE)          = '' or z.TIP_CLIENTE IS NULL)"
                            +  "    AND (z.COD_LISTA_PRECIO   = '$codListaPrecio'             or trim(z.COD_LISTA_PRECIO)     = '' or z.COD_LISTA_PRECIO IS NULL)"
                            +  "    AND  z.COD_VENDEDOR       = '${ListaClientes.codVendedor}' "
                            +  "    AND  z.NRO_PROMOCION IN ($inNro)"
                            +  "    AND EXISTS ("
                            +  " SELECT 1 FROM svm_promociones_art_cab a, svm_articulos_precios b "
                            +  " WHERE a.COD_EMPRESA  = b.COD_EMPRESA  "
                            +  "   AND a.COD_ARTICULO = b.COD_ARTICULO "
                            +  "   AND (a.COD_LISTA_PRECIO = '$codListaPrecio' or trim(a.COD_LISTA_PRECIO) = '' or a.COD_LISTA_PRECIO IS NULL ) "
                            +  "   AND (b.COD_LISTA_PRECIO = '$codListaPrecio' or trim(b.COD_LISTA_PRECIO) = '' or b.COD_LISTA_PRECIO IS NULL ) "
                            +  "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                            +  "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                            +  "   AND (a.COD_CONDICION_VENTA = '$condicionVenta' or trim(a.COD_CONDICION_VENTA) = '' or a.COD_CONDICION_VENTA IS NULL ) "
                            +  "   AND  a.NRO_PROMOCION = z.NRO_PROMOCION "
                            +  "   AND (a.TIP_CLIENTE   = '${ListaClientes.tipCliente}' or trim(a.TIP_CLIENTE) = '' or a.TIP_CLIENTE IS NULL ) "
                            +  " GROUP BY a.COD_ARTICULO "
                            +  " )"
                )
        where += if (codArticulo.isNotEmpty()){
            " and IND_ART = 'S' "
        } else {
            " and IND_PROM = 'S' "
        }
        cbCombo.isChecked = false
        cbMonto.isChecked = false
        cbCantidad.isChecked = false
        cbBonificacion.isChecked = false
        listaPromociones = ArrayList()
        funcion.cargarLista(
            listaPromociones,
            funcion.buscar(tabla, campos, groupBy, orderBy, where)
        )
        mostrar()
    }

    private fun mostrar(){
        funcion.vistas  = intArrayOf(R.id.tv1, R.id.tv2, R.id.tv3)
        funcion.valores = arrayOf("FEC_VIGENCIA", "DESCRIPCION", "NRO_PROMOCION")
        val adapter: Adapter.AdapterPromociones =
            Adapter.AdapterPromociones(
                this,
                listaPromociones,
                R.layout.ven_pro_lista_promociones,
                funcion.vistas,
                funcion.valores
            )
        lvPromocionCabecera.adapter = adapter
        lvPromocionCabecera.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            posPromocion = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPromocionCabecera.invalidateViews()
            tvComentario.text = listaPromociones[posPromocion]["COMENTARIO"]
        }
        if (listaPromociones.size>0){
            tvComentario.text = listaPromociones[posPromocion]["COMENTARIO"]
        } else {
            tvComentario.text = ""
        }
    }

    @SuppressLint("Recycle")
    fun buscarPromociones(view: View) {
        FuncionesUtiles.posicionCabecera = 0
        posPromocion = 0
        seleccionaPromociones()

        val campos : String = " DISTINCT z.NRO_PROMOCION, z.TIP_CLIENTE, z.DESCRIPCION, z.COMENTARIO, z.COD_CONDICION_VENTA," +
                " z.FEC_VIGENCIA , z.IND_TIPO   , z.IND_COMBO "
        val groupBy : String  = " z.NRO_PROMOCION, z.TIP_CLIENTE, z.DESCRIPCION, z.COMENTARIO, z.COD_CONDICION_VENTA," +
                " z.FEC_VIGENCIA , z.IND_TIPO   , z.CANT_VENTA , z.IND_COMBO "
        val orderBy = " CAST(z.NRO_PROMOCION AS INTEGER) asc "
        val tabla = " svm_promociones_art_cab z "
        var where : String  = (" AND (z.COD_CONDICION_VENTA   = '$condicionVenta'             or trim(z.COD_CONDICION_VENTA)  = '' or z.COD_CONDICION_VENTA IS NULL) "
                +  "    AND (z.TIP_CLIENTE        = '${ListaClientes.tipCliente}' or trim(z.TIP_CLIENTE)          = '' or z.TIP_CLIENTE IS NULL)"
                +  "    AND (z.COD_LISTA_PRECIO   = '$codListaPrecio'             or trim(z.COD_LISTA_PRECIO)     = '' or z.COD_LISTA_PRECIO IS NULL)"
                +  "    AND  z.COD_VENDEDOR       = '${ListaClientes.codVendedor}' "
                +  "    AND  z.NRO_PROMOCION IN ($inNro) "
                +  "    AND EXISTS ("
                +  " SELECT 1 FROM svm_promociones_art_cab a, svm_articulos_precios b "
                +  " WHERE a.COD_EMPRESA  = b.COD_EMPRESA  "
                +  "   AND a.COD_ARTICULO = b.COD_ARTICULO "
                +  "   AND (a.COD_LISTA_PRECIO = '$codListaPrecio' or trim(a.COD_LISTA_PRECIO) = '' or a.COD_LISTA_PRECIO IS NULL ) "
                +  "   AND (b.COD_LISTA_PRECIO = '$codListaPrecio' or trim(b.COD_LISTA_PRECIO) = '' or b.COD_LISTA_PRECIO IS NULL ) "
                +  "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                +  "   AND a.COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                +  "   AND (a.COD_CONDICION_VENTA = '$condicionVenta' or trim(a.COD_CONDICION_VENTA) = '' or a.COD_CONDICION_VENTA IS NULL ) "
                +  "   AND  a.NRO_PROMOCION = z.NRO_PROMOCION "
                +  "   AND (a.TIP_CLIENTE   = '${ListaClientes.tipCliente}' or trim(a.TIP_CLIENTE) = '' or a.TIP_CLIENTE IS NULL ) "
                +  " GROUP BY a.COD_ARTICULO "
                +  " )"
                )
        if (view.id != cbCombo.id){
            cbBonificacion.isChecked = false
            cbMonto.isChecked = false
            cbCantidad.isChecked = false
            findViewById<CheckBox>(view.id).isChecked = true
        }
        if (cbCombo.isChecked){
            where += " AND IND_COMBO = 'S' "
        }
        if (cbBonificacion.isChecked){
            where += " AND IND_TIPO = 'B' "
        }
        if (cbMonto.isChecked){
            where += " AND IND_TIPO = 'M' "
        }
        if (cbCantidad.isChecked){
            where += " AND IND_TIPO = 'F' "
        }
        where += if (codArticulo.isEmpty()){
            " AND IND_PROM = 'S' "
        } else {
            " AND IND_ART = 'S' "
        }
        listaPromociones = ArrayList()
        funcion.cargarLista(
            listaPromociones,
            funcion.buscar(tabla, campos, groupBy, orderBy, where)
        )
        mostrar()
    }

    @SuppressLint("Recycle")
    private fun seleccionaPromociones(){
        var sql = ("SELECT distinct NRO_PROMOCION FROM svm_promociones_art_cab c "
                + "   WHERE (c.COD_CONDICION_VENTA = '" + condicionVenta + "' or c.COD_CONDICION_VENTA = ' ' or c.COD_CONDICION_VENTA IS NULL)"
                + "     and (c.TIP_CLIENTE = '" + ListaClientes.tipCliente + "' or c.TIP_CLIENTE = ' ' or c.TIP_CLIENTE IS NULL)"
                + "     and (c.COD_LISTA_PRECIO = '" + codListaPrecio + "' or c.COD_LISTA_PRECIO = ' ' or c.COD_LISTA_PRECIO IS NULL)")

        if (codArticulo.isNotEmpty()) {
            sql += "     and  c.COD_ARTICULO = '$codArticulo'"
        }

        var cursorProm = MainActivity2.bd!!.rawQuery(sql, null)

        var nreg: Int = cursorProm.count

        inNro = ""
        if (nreg > 0) {
            cursorProm.moveToFirst()
            inNro = "'" + cursorProm.getString(cursorProm.getColumnIndex("NRO_PROMOCION")).toString() + "'"
        }

        for (i in 0 until nreg - 1) {
            cursorProm.moveToNext()
            inNro = inNro + ",'" + cursorProm.getString(cursorProm.getColumnIndex("NRO_PROMOCION")) + "'"
        }

        if (codArticulo.isNotEmpty()) {
            sql = ("SELECT distinct NRO_PROMOCION FROM svm_promociones_art_det b "
                    + "   WHERE COD_ARTICULO IN (" + codArticulo + ")")
            cursorProm = MainActivity2.bd!!.rawQuery(sql, null)
            var inNroDet = ""
            if (cursorProm.count == 0) {
                nreg = 0
            } else {
                nreg = cursorProm.count
                cursorProm.moveToFirst()
                inNroDet += "'" + cursorProm.getString(cursorProm.getColumnIndex("NRO_PROMOCION"))
                    .toString() + "'"
            }
            for (i in 0 until nreg - 1) {
                cursorProm.moveToNext()
                inNroDet += ",'" + cursorProm.getString(cursorProm.getColumnIndex("NRO_PROMOCION"))
                    .toString() + "'"
            }
            if (nreg > 0) {
                inNro += if (inNro.isEmpty()) {
                    inNroDet
                } else {
                    ",$inNroDet"
                }
            }
        }
    }

    fun cargarPromocion(view: View) {
        view.id
        if (!verificaPromoCargada(posPromocion)){
            lvPromocionCabecera.invalidateViews()
            return
        }
        DialogoPromocion.posicion = 0
        val dialogo = DialogoPromocion(
            this,
            "NRO_PROMOCION",
            "",
            "DESC_ARTICULO"
        )
        if (listaPromociones[posPromocion]["IND_TIPO"] == "B"){
            dialogo.dialogoComboBonificacion(
                listaPromociones[posPromocion]["NRO_PROMOCION"].toString().trim()
            )
        } else {
            dialogo.dialogoDescuentoM(
                listaPromociones[posPromocion]["NRO_PROMOCION"].toString().trim()
            )
        }
    }

    private fun inicializarEtAccion(editText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (editText.text.toString().trim() == "CERRAR") {
                    finish()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verificaPromoCargada(position: Int):Boolean{
        if (Pedidos.nuevo){
            return true
        }
        val sql : String = ("SELECT DISTINCT COD_ARTICULO FROM vt_pedidos_det "
                +  " WHERE NUMERO = '${Pedidos.maximo}' "
                +  "   AND COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                +  "   AND NRO_PROMOCION = '${listaPromociones[position]["NRO_PROMOCION"]}' "
                +  " ")
        val funcion = FuncionesUtiles(this)
        if (funcion.consultar(sql).count > 0){
            funcion.mensaje(this, "¡Atención!", "La promoción ya fue cargada.")
            return false
        }
        return true
    }

}