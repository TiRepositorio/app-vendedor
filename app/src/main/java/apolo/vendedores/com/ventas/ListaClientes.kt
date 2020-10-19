package apolo.vendedores.com.ventas

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
//import apolo.vendedores.com.informes.DeudaDeClientes
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.DialogoAutorizacion
import apolo.vendedores.com.utilidades.FuncionesUtiles
//import apolo.vendedores.com.utilidades.Mapa
//import apolo.vendedores.com.ventas.sd.SolicitudDevolucion
import kotlinx.android.synthetic.main.activity_lista_clientes.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class ListaClientes : AppCompatActivity() {

    companion object{
        var datos: HashMap<String, String> = HashMap<String, String>()
        lateinit var funcion : FuncionesUtiles
        var codVendedor : String = ""
        var codCliente : String = ""
        var codSubcliente : String = ""
        var descCliente : String = ""
        var tipCliente : String = ""
        var indEspecial : String = ""
        var tipCondicion : String = ""
        var codCondicion : String = ""
        var diasInicial : String = ""
        var codSucursalCliente : String = ""
        var indDirecta : String = ""
        var lista : ArrayList<HashMap<String,String>> = ArrayList<HashMap<String,String>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_clientes)

        //solo con titulo
        funcion = FuncionesUtiles(imgTitulo,tvTitulo)
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,llBuscar,spBuscar,etBuscar,btBuscar)

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.addItemSpinner(this,"Codigo-Nombre-Ciudad","COD_CLIENTE-DESC_SUBCLIENTE-desc_ciudad")
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_persona,"Lista de clientes")
        btBuscar.setOnClickListener{buscar(" AND TRIM(FEC_VISITA) = '' ")}
        btDeuda.setOnClickListener{deuda()}
        ibtnMapa.setOnClickListener{mapa()}
        inicializaETAccion(accion)
        btModificar.setOnClickListener{modificar()}
        btSD.setOnClickListener{sd()}
        btVender.setOnClickListener{vender()}
        buscarRuteo()
        cbNoAtendidos.setOnClickListener{buscarBloqueado()}
        cbRuteoDelDia.setOnClickListener{buscarRuteo()}
        cbTodos.setOnClickListener{buscarTodo()}
    }

    fun buscar(condicion:String){
        var campos = " * "
        var groupBy = ""
        var orderBy = "COD_CLIENTE"
        var tabla = " svm_cliente_vendedor "
        var fecha = funcion.getFechaActual()
//        var where = " AND COD_VENDEDOR = '" + codVendedor +
        var where = " AND COD_VENDEDOR = '" + codVendedor + "' $condicion "
        cargarLista(funcion.buscar(tabla,campos,groupBy,orderBy,where))
        mostrar()
    }

    fun buscarRuteo(){
        cbTodos.isChecked = false
        cbNoAtendidos.isChecked = false
        if (cbRuteoDelDia.isChecked){
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        } else {
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        }
    }

    fun buscarBloqueado(){
        cbRuteoDelDia.isChecked = false
        cbTodos.isChecked = false
        if (cbNoAtendidos.isChecked){
            buscar(" AND IND_VISITA = 'B' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        } else {
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        }
    }

    fun buscarTodo(){
        cbRuteoDelDia.isChecked = false
        cbNoAtendidos.isChecked = false
        if (cbTodos.isChecked){
            buscar(" AND TRIM(FEC_VISITA) = '' ")
        } else {
            buscar(" AND IND_VISITA = 'S' AND FEC_VISITA = '${funcion.getFechaActual()}' ")
        }
    }

    fun cargarLista(cursor: Cursor){
        FuncionesUtiles.listaDetalle = ArrayList<HashMap<String,String>>()
        for (i in 0 until cursor.count){
            datos = HashMap<String,String>()
            for (j in 0 until cursor.columnCount){
                try {
                    datos.put(cursor.getColumnName(j),funcion.dato(cursor,cursor.getColumnName(j)))
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }
            datos.put("LIMITE_CREDITO",funcion.enteroCliente(datos.get("LIMITE_CREDITO").toString()))
            datos.put("TOT_DEUDA",funcion.enteroCliente(datos.get("TOT_DEUDA").toString()))
            datos.put("SALDO",funcion.enteroCliente(datos.get("SALDO").toString()))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.vistas  = intArrayOf(R.id.tv1 ,R.id.tv2 ,R.id.tv3 ,R.id.tv4 ,R.id.tv5 ,R.id.tv6 ,
                                     R.id.tv7 ,R.id.tv8 ,R.id.tv9 ,R.id.tv10,R.id.tv11,R.id.tv12,
                                     R.id.tv13,R.id.tv14,R.id.tv15,R.id.tv16,R.id.tv17 )
        funcion.valores = arrayOf("TIP_CAUSAL"      , "CATEGORIA"       ,"COD_CLIENTE"      , "COD_SUBCLIENTE"  , "TIP_CLIENTE"    ,
                                  "DESC_CLIENTE"    , "DESC_SUBCLIENTE" ,"DESC_CIUDAD"      , "DIRECCION"       , "RUC"            ,
                                  "DESC_ZONA"       , "TELEFONO"        ,"DESC_CONDICION"   , "LIMITE_CREDITO"  , "TOT_DEUDA"      ,
                                  "SALDO"           , "FEC_VISITA"      )
        var adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(this
                ,FuncionesUtiles.listaDetalle
                ,R.layout.ven_cli_lista_clientes
                ,funcion.vistas
                ,funcion.valores)
        lvClientes.adapter = adapter
        lvClientes.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            FuncionesUtiles.posicionDetalle  = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvClientes.invalidateViews()
            cargarDatos(position)
        }
        if (FuncionesUtiles.listaDetalle.size>0){
            cargarDatos(0)
        }
    }

    private fun cargarDatos(posicion : Int){
        codCliente = FuncionesUtiles.listaDetalle.get(posicion).get("COD_CLIENTE").toString()
        codSubcliente = FuncionesUtiles.listaDetalle.get(posicion).get("COD_SUBCLIENTE").toString()
        descCliente = FuncionesUtiles.listaDetalle.get(posicion).get("DESC_SUBCLIENTE").toString()
        tipCliente = FuncionesUtiles.listaDetalle.get(posicion).get("TIP_CLIENTE").toString()
        indEspecial = FuncionesUtiles.listaDetalle.get(posicion).get("IND_ESP").toString()
        tipCondicion = FuncionesUtiles.listaDetalle.get(posicion).get("TIPO_CONDICION").toString()
        codCondicion = FuncionesUtiles.listaDetalle.get(posicion).get("COD_CONDICION").toString()
        diasInicial = FuncionesUtiles.listaDetalle.get(posicion).get("DIAS_INICIAL").toString()
        indDirecta = FuncionesUtiles.listaDetalle.get(posicion).get("IND_DIRECTA").toString()
        codSucursalCliente = FuncionesUtiles.listaDetalle.get(posicion).get("COD_SUCURSAL").toString()
    }

    fun deuda(){
        if (FuncionesUtiles.listaDetalle.size > 0){
            Deuda.codigo = FuncionesUtiles.listaDetalle.get(FuncionesUtiles.posicionDetalle).get("COD_CLIENTE").toString() + "-" +
                           FuncionesUtiles.listaDetalle.get(FuncionesUtiles.posicionDetalle).get("COD_SUBCLIENTE").toString()
            Deuda.nombre = FuncionesUtiles.listaDetalle.get(FuncionesUtiles.posicionDetalle).get("DESC_CLIENTE").toString()
            if (FuncionesUtiles.listaDetalle.get(FuncionesUtiles.posicionDetalle).get("TOT_DEUDA").equals("0")){
                funcion.mensaje(this,"Atención!","El cliente no tiene deudas.")
                return
            }
            var deuda : Intent = Intent(this,Deuda::class.java)
            startActivity(deuda)
        }
    }

    fun verCliente(){
//        Mapa.lista = ArrayList<HashMap<String,String>>()
//        Mapa.lista.add(FuncionesUtiles.listaDetalle.get(FuncionesUtiles.posicionDetalle))
//        Mapa.modificarCliente = false
//        var intent : Intent = Intent(this,Mapa::class.java)
//        startActivity(intent)
    }

    fun verRuteo(){
//        Mapa.lista = ArrayList<HashMap<String,String>>()
//        Mapa.lista = FuncionesUtiles.listaDetalle
//        Mapa.modificarCliente = false
//        var intent : Intent = Intent(this,Mapa::class.java)
//        startActivity(intent)
    }

    fun inicializaETAccion(etAccion: EditText){
        etAccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etAccion.text.toString().equals("cliente")){
                    verCliente()
                    accion.setText("")
                    return
                }
                if (etAccion.text.toString().trim().equals("ruteo")){
                    verRuteo()
                    accion.setText("")
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

        })
    }

    fun mapa(){
//        var dialogo : DialogoAutorizacion = DialogoAutorizacion(this)
//        dialogo.dialogoMapa("",accion)
    }

    fun modificar(){
        if (FuncionesUtiles.listaDetalle.size==0){return}
        var modifcar : Intent = Intent(this,ModificarCliente::class.java)
        startActivity(modifcar)
    }

    fun vender(){
//        Pedidos.nuevo = true
//        var vender : Intent = Intent(this,Pedidos::class.java)
//        startActivity(vender)
    }

    fun sd(){
        var sd : Intent = Intent(this,apolo.vendedores.com.ventas.sd.SolicitudDevolucion::class.java)
        startActivity(sd)
    }

}
