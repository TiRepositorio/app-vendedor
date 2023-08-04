package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import apolo.vendedores.com.R
import kotlinx.android.synthetic.main.busqueda.*

class DialogoBusquedaInventario {

    companion object{
        var posicion: Int = 0
    }

    constructor(context: Context, tabla:String, codigo:String, descripcion:String, order:String, condicion:String, tvResultado: TextView, tvResultado2: TextView?){
        this.context = context
        this.codigo = codigo
        this.subCodigo = ""
        this.descripcion = descripcion
        this.campos = ""
        this.tabla = tabla
        this.order = order
        this.condicion = condicion
        this.tvResultado = tvResultado
        this.tvResultado2 = tvResultado2
        this.codBarra = ""
    }

    constructor(context: Context, tabla:String, codigo:String, subCodigo:String, descripcion:String, campos:String, order:String, condicion:String, tvResultado: TextView, tvResultado2: TextView?){
        this.context = context
        this.codigo = codigo
        this.subCodigo = subCodigo
        this.descripcion = descripcion
        this.campos = campos
        this.tabla = tabla
        this.order = order
        this.condicion = condicion
        this.tvResultado = tvResultado
        this.tvResultado2 = tvResultado2
        this.codBarra = ""
    }

    constructor(context: Context, tabla:String, codigo:String, subCodigo:String, descripcion:String, codBarra : String, campos:String, order:String, condicion:String, tvResultado: TextView, tvResultado2: TextView?){
        this.context = context
        this.codigo = codigo
        this.subCodigo = subCodigo
        this.descripcion = descripcion
        this.campos = campos
        this.tabla = tabla
        this.order = order
        this.condicion = condicion
        this.tvResultado = tvResultado
        this.tvResultado2 = tvResultado2
        this.codBarra = codBarra
    }

    var context: Context
    var codigo: String
    private var codBarra : String
    private var subCodigo : String
    private var descripcion: String
    private var tabla: String
    private var condicion: String
    var order: String
    private var tvResultado: TextView
    private var tvResultado2: TextView?
    private var lista: ArrayList<HashMap<String,String>> = ArrayList()
    var funcion = FuncionesUtiles()
    private var campos : String
    lateinit var dialogo: Dialog


    fun cargarDialogo(cancelable:Boolean){
        dialogo = Dialog(context)
        if (campos == "" && codBarra == ""){
            dialogo.setContentView(R.layout.busqueda)
            buscar(dialogo.etBuscar)
            mostrar(dialogo.lvBuscar)
        } else {
            if (codBarra != ""){
                dialogo.setContentView(R.layout.busqueda3)
                buscar(dialogo.etBuscar)
                mostrarCampos3(dialogo.lvBuscar)
            } else {
                dialogo.setContentView(R.layout.busqueda4)
                buscar(dialogo.etBuscar)
                mostrarCampos(dialogo.lvBuscar)
            }
        }
        dialogo.imgBuscar.setOnClickListener{
            buscar(dialogo.etBuscar)
            if (campos == "" && codBarra == ""){
                mostrar(dialogo.lvBuscar)
            } else {
                if (codBarra != ""){
                    mostrarCampos3(dialogo.lvBuscar)
                } else {
                    mostrarCampos(dialogo.lvBuscar)
                }
            }
        }
        if (tvResultado2 == null){
            seleccionar(dialogo.btSeleccionar,tvResultado)
        } else {
            seleccionar(dialogo.btSeleccionar,tvResultado,tvResultado2)
        }
        dialogo.setCancelable(cancelable)
        dialogo.show()
    }

    private fun buscar(etBuscar: EditText){
        var sql: String
        if (campos != ""){
            sql = "SELECT DISTINCT $campos"
        } else {
            sql = if (codBarra != ""){
                if (subCodigo.trim() != "" && codigo.split(" ").size == 1){
                    "SELECT DISTINCT $codigo || '-' || $subCodigo as $codigo, $descripcion"
                } else {
                    "SELECT DISTINCT $codigo, $descripcion, $codBarra "
                }
            } else {
                if (subCodigo.trim() != "" && codigo.split(" ").size == 1){
                    "SELECT DISTINCT $codigo || '-' || $subCodigo as $codigo, $descripcion"
                } else {
                    "SELECT DISTINCT $codigo, $descripcion"
                }
            }
        }

        if (subCodigo.trim() != ""){
            sql += "  FROM " + tabla +
                    " WHERE (" + codigo.split(" ")[0] + " LIKE '%" + etBuscar.text + "%' " +
                    "    OR " + descripcion + " LIKE '%" + etBuscar.text + "%') "
        } else {
            sql += if (codBarra.trim() == ""){
                "  FROM " + tabla +
                        " WHERE (" + codigo.split(" ")[0] + " LIKE '%" + etBuscar.text + "%' " +
                        "    OR " + descripcion + " LIKE '%" + etBuscar.text + "%' ) "
            } else {
                "  FROM " + tabla +
                        " WHERE (" + codigo.split(" ")[0] + " LIKE '%" + etBuscar.text + "%' " +
                        "    OR " + descripcion + " LIKE '%" + etBuscar.text + "%'" +
                        "    OR " + codBarra    + " LIKE '%" + etBuscar.text + "') "
            }
        }

        if (condicion != ""){
            sql += condicion
        }
        sql += " ORDER BY $order"

        cargarDatos(funcion.consultar(sql))
    }

    private fun cargarDatos(cursor: Cursor){
        lista = ArrayList()
        for (i in 0 until cursor.count){
            val datos = HashMap<String,String>()
            if (campos == ""){
                datos[codigo.split(" ")[0]] = funcion.dato(cursor,codigo.split(" ")[0])
                datos[descripcion] = funcion.dato(cursor,descripcion)
                if (codBarra != ""){
                    datos[codBarra] = funcion.dato(cursor,codBarra)
                }
            } else {
                for (j in campos.split(",").indices){
                    datos[campos.split(",")[j]] = funcion.dato(cursor,campos.split(",")[j])
                }
            }
            lista.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrar(lvBusqueda: ListView){
        funcion.vistas = intArrayOf(R.id.tvb1,R.id.tvb2)
        funcion.valores = arrayOf(codigo.split(" ")[0],descripcion)
        val adapter:Adapter.AdapterBusqueda =
            Adapter.AdapterBusqueda(context
                ,lista
                ,R.layout.busqueda_lista
                ,funcion.vistas
                ,funcion.valores)
        lvBusqueda.adapter = adapter
        lvBusqueda.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            posicion = position
            DialogoBusqueda.posicion = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvBusqueda.invalidateViews()
        }
    }

    private fun mostrarCampos(lvBusqueda: ListView){
        funcion.vistas = intArrayOf(R.id.tvb1,R.id.tvb2,R.id.tvb3,R.id.tvb4,R.id.tvb5)
        funcion.valores = arrayOf(campos.split(",")[0],campos.split(",")[1],campos.split(",")[2],
            campos.split(",")[3],campos.split(",")[4])
        val adapter:Adapter.AdapterBusqueda =
            Adapter.AdapterBusqueda(context
                ,lista
                ,R.layout.busqueda4_lista
                ,funcion.vistas
                ,funcion.valores)
        lvBusqueda.adapter = adapter
        lvBusqueda.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            posicion = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvBusqueda.invalidateViews()
        }
    }

    private fun mostrarCampos3(lvBusqueda: ListView){
        funcion.vistas = intArrayOf(R.id.tvb1,R.id.tvb2,R.id.tvb3)
        funcion.valores = arrayOf(codigo,descripcion,codBarra)
        val adapter:Adapter.AdapterBusqueda =
            Adapter.AdapterBusqueda(context
                ,lista
                ,R.layout.busqueda3_lista
                ,funcion.vistas
                ,funcion.valores)
        lvBusqueda.adapter = adapter
        lvBusqueda.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            posicion = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvBusqueda.invalidateViews()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun seleccionar(btSeleccionar: Button, textView: TextView){
        btSeleccionar.setOnClickListener{
            if (lista.size>0){
                if (campos == ""){
                    textView.text = lista[posicion][codigo.split(" ")[0]] + " - " + lista[posicion][descripcion]
                } else {
                    textView.text = lista[posicion][campos.split(",")[0]] +
                            "-" + lista[posicion][campos.split(",")[1]] +
                            "-" + lista[posicion][campos.split(",")[2]] +
                            "-" + lista[posicion][campos.split(",")[3]] +
                            "-" + lista[posicion][campos.split(",")[4]]
                }
            }
            dialogo.dismiss()
            posicion = 0
        }
    }

    private fun seleccionar(btSeleccionar: Button, textView: TextView, textView2: TextView?){
        btSeleccionar.setOnClickListener{
            if (lista.size>0){
                textView.text = lista[posicion][codigo.split(" ")[0]]
                textView2!!.text = lista[posicion][descripcion]
            }
            dialogo.dismiss()
            posicion = 0
        }
    }



}

