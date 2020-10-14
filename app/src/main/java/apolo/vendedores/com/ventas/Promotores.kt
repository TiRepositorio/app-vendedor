package apolo.vendedores.com.ventas

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_promotores.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class Promotores : AppCompatActivity() {

    companion object{
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var funcion : FuncionesUtiles
        lateinit var cursor: Cursor
        var lista : ArrayList<HashMap<String,String>> = ArrayList<HashMap<String,String>>()
    }

    var posicion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotores)

        //solo con titulo
        funcion = FuncionesUtiles(imgTitulo,tvTitulo)
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,llBuscar,spBuscar,etBuscar,btBuscar)

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.addItemSpinner(this,"Codigo-Nombre","a.COD_VENDEDOR-a.DESC_VENDEDOR")
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_persona,"Lista de vendedores")
        btBuscar.setOnClickListener{buscar()}
        btRealizar.setOnClickListener{realizarVenta()}
        btConsultar.setOnClickListener{consultarPedidos()}
        btSalir.setOnClickListener{finish()}
        buscar()
    }

    fun buscar(){
        var campos = "DISTINCT a.COD_VENDEDOR, a.DESC_VENDEDOR "
        var groupBy = ""
        var orderBy = "a.COD_VENDEDOR"
        var tabla = " svm_cliente_vendedor a "
        var where = ("")
        cargarLista(funcion.buscar(tabla,campos,groupBy,orderBy,where))
        mostrar()
    }

    fun cargarLista(cursor: Cursor){
        lista = ArrayList<HashMap<String,String>>()
        for (i in 0 until cursor.count){
            datos = HashMap<String,String>()
            datos.put("COD_VENDEDOR",funcion.dato(cursor,"COD_VENDEDOR"))
            datos.put("DESC_VENDEDOR",funcion.dato(cursor,"DESC_VENDEDOR"))
            lista.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2)
        funcion.valores = arrayOf("COD_VENDEDOR", "DESC_VENDEDOR")
        var adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(this
                ,lista
                ,R.layout.ven_pro_lista_promotores
                ,funcion.vistas
                ,funcion.valores)
        lvPromotores.adapter = adapter
        lvPromotores.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            posicion = position
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPromotores.invalidateViews()
        }
    }

    fun realizarVenta(){
        if (lista.size==0){
            return
        }
        var sql : String = "SELECT NUMERO MAXIMO, IND_PALM, PER_VENDER " +
                "     FROM svm_vendedor_pedido " +
                "    WHERE COD_VENDEDOR = '"+lista.get(posicion).get("COD_VENDEDOR")+"'"

        var cursor:Cursor = funcion.consultar(sql)
        cursor.moveToFirst()
        var indPalm : String = "N"
        var perVender : String = "M"
        if (cursor.count > 0) {
            indPalm = funcion.dato(cursor,"IND_PALM")
            perVender = funcion.dato(cursor,"PER_VENDER")
            FuncionesUtiles.ultimaVenta = funcion.datoEntero(cursor,"MAXIMO")
        }


        if(indPalm.equals("S") && perVender.equals("S")){
//            Pedidos.nuevo = true
//            Pedidos.maximo = funcion.datoEntero(cursor,"MAXIMO")
            ListaClientes.codVendedor = lista.get(posicion).get("COD_VENDEDOR").toString()
            startActivity(Intent(this, ListaClientes::class.java))
        }else{
            funcion.mensaje(this,"Atención!","No posee permiso para vender en esta cartera!")
        }

    }

    fun consultarPedidos() {
//        ListaClientes.codVendedor = lista.get(posicion).get("COD_VENDEDOR").toString()
//        startActivity(Intent(this,ConsultaPedidos::class.java))
    }
}
