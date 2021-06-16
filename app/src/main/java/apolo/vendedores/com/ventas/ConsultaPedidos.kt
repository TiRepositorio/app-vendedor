package apolo.vendedores.com.ventas

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.DialogoCalendario
import apolo.vendedores.com.utilidades.FuncionesConsultor
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_consulta_pedidos.*

class ConsultaPedidos : AppCompatActivity() {

    private var calendario : DialogoCalendario = DialogoCalendario(this)
    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    private var posicion : Int = 0
    private lateinit var consultor: FuncionesConsultor
    lateinit var lista : ArrayList<HashMap<String,String>>
    private lateinit var listaCliente : ArrayList<HashMap<String,String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_pedidos)

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.inicializaContadores()
        consultor = FuncionesConsultor(this,etDesde,etHasta,rbPendiente,rbEnviado,rbTodo,imgBuscar,btModificar,btConsultar,btEliminar,rgFiltro)
        consultor.setRadioButtonText("Pendiente-ESTADO = 'P'","Enviado-ESTADO = 'E'","Todo-ESTADO LIKE '%%'")
        etDesde.setText(funcion.getFechaActual())
        etHasta.setText(funcion.getFechaActual())
        etDesde.setOnClickListener{calendario.onCreateDialog(1,etDesde,etDesde)!!.show()}
        etHasta.setOnClickListener{calendario.onCreateDialog(1,etHasta,etDesde)!!.show()}
        imgBuscar.setOnClickListener{buscarPedidos()}
        btModificar.setOnClickListener{modificar()}
        btConsultar.setOnClickListener{consultar()}
        btEliminar.setOnClickListener{eliminar()}
        funcion.ejecutar("update vt_pedidos_cab set estado = 'A' where  julianday(datetime('now','localtime')) - julianday(hora_registro)  > 2",this)
    }

    private fun buscarPedidos(){
        lista = ArrayList()
        funcion.cargarLista(lista,consultor.buscar("vt_pedidos_cab","FECHA",
                                                  "COD_VENDEDOR", ListaClientes.codVendedor))
        mostrar()
    }

    private fun mostrar(){
        FuncionesUtiles.posicionDetalle = 0
        posicion = 0
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7,R.id.tv8)
        funcion.valores = arrayOf("NUMERO","COD_CLIENTE","DESC_CLIENTE","FECHA","COD_MONEDA","COD_LISTA_PRECIO","TOT_COMPROBANTE","ESTADO")
        val adapter = Adapter.AdapterGenericoCabecera(this,lista,R.layout.ven_con_lista_consulta_pedidos,funcion.vistas,funcion.valores)
        lvConsultaPedidos.adapter = adapter
        lvConsultaPedidos.setOnItemClickListener { _, view, position, _ ->
            posicion = position
            FuncionesUtiles.posicionCabecera = posicion
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvConsultaPedidos.invalidateViews()
            cargarDatosCliente(posicion)
        }
        if (lista.size>0){
            cargarDatosCliente(0)
        }
    }

    private fun cargarDatosCliente(position:Int){
        listaCliente = ArrayList()
        val sql = "SELECT * FROM svm_cliente_vendedor " +
                        "  WHERE COD_EMPRESA = '1' " +
                        "    AND COD_CLIENTE    = '${lista[position]["COD_CLIENTE"]}'       " +
                        "    AND COD_SUBCLIENTE = '${lista[position]["COD_SUBCLIENTE"]}'    " +
                        "    AND COD_VENDEDOR   = '${lista[position]["COD_VENDEDOR"]}'      " +
                ""
        funcion.cargarLista(listaCliente,funcion.consultar(sql))
        ListaClientes.codCliente        = lista[position]["COD_CLIENTE"].toString()
        ListaClientes.codSubcliente     = lista[position]["COD_SUBCLIENTE"].toString()
        ListaClientes.descCliente       = lista[position]["DESC_CLIENTE"].toString()
        ListaClientes.tipCliente        = listaCliente[0]["TIP_CLIENTE"].toString()
        ListaClientes.indEspecial       = listaCliente[0]["IND_ESP"].toString()
        ListaClientes.tipCondicion      = listaCliente[0]["TIPO_CONDICION"].toString()
        ListaClientes.codCondicion      = lista[position]["COD_CONDICION_VENTA"].toString()
        ListaClientes.diasInicial       = lista[position]["DIAS_INICIAL"].toString()
//        Pedidos.indPresencial           = lista[position]["IND_PRESENCIAL"].toString().trim()
        Pedidos.indPresencial           = "N"
        Pedidos.vent                    = "N"
        Pedidos.totalPedido             = lista[position]["TOT_COMPROBANTE"].toString()
    }

    private fun modificar(){
        if (lista.size>0){
            if (lista[posicion]["ESTADO"].toString().trim() == "E"){
                funcion.toast(this,"El pedido ya fue enviado.")
                return
            }
            if (lista[posicion]["ESTADO"].toString().trim() == "A"){
                funcion.toast(this,"El pedido ya fue anulado.")
                return
            }
            Pedidos.nuevo = false
            Pedidos.maximo = lista[posicion]["NUMERO"].toString().toInt()
            startActivity(Intent(this,Pedidos::class.java))
        }
    }

    private fun consultar(){
        val dialogo = DialogoPedidos(this, lista[posicion]["NUMERO"].toString().toInt(),lista[posicion]["COD_LISTA_PRECIO"].toString())
        dialogo.mostrarDialogo()
//        etDesde.setText(0)
    }

    private fun eliminar(){
        if (lista.size==0){
            return
        }
        if (lista[posicion]["ESTADO"].toString().trim() == "E"){
            funcion.toast(this,"El pedido ya fue enviado.")
            return
        }
        if (lista[posicion]["ESTADO"].toString().trim() == "A"){
            funcion.toast(this,"El pedido ya fue anulado.")
            return
        }
        funcion.ejecutar("DELETE FROM vt_pedidos_det WHERE NUMERO = '${lista[posicion]["NUMERO"]}' AND COD_VENDEDOR = '${lista[posicion]["COD_VENDEDOR"]}'",this)
        funcion.ejecutar("DELETE FROM vt_pedidos_cab WHERE id = '${lista[posicion]["id"]}'",this)
        buscarPedidos()
    }

}