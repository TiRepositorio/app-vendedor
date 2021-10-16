package apolo.vendedores.com.ventas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.DialogoCalendario
import apolo.vendedores.com.utilidades.FuncionesConsultor
import apolo.vendedores.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.activity_consulta_datos_de_cliente.*
import kotlinx.android.synthetic.main.activity_consulta_pedidos.btConsultar
import kotlinx.android.synthetic.main.activity_consulta_pedidos.btEliminar
import kotlinx.android.synthetic.main.activity_consulta_pedidos.btModificar
import kotlinx.android.synthetic.main.activity_consulta_pedidos.etDesde
import kotlinx.android.synthetic.main.activity_consulta_pedidos.etHasta
import kotlinx.android.synthetic.main.activity_consulta_pedidos.imgBuscar
import kotlinx.android.synthetic.main.activity_consulta_pedidos.rbEnviado
import kotlinx.android.synthetic.main.activity_consulta_pedidos.rbPendiente
import kotlinx.android.synthetic.main.activity_consulta_pedidos.rbTodo
import kotlinx.android.synthetic.main.activity_consulta_pedidos.rgFiltro

class ConsultaDatosDeCliente : AppCompatActivity() {

    private var calendario : DialogoCalendario = DialogoCalendario(this)
    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    private var posicion : Int = 0
    private lateinit var consultor: FuncionesConsultor
    lateinit var lista : ArrayList<HashMap<String,String>>
    private lateinit var listaCliente : ArrayList<HashMap<String,String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_datos_de_cliente)

        inicializarElementos()
    }

    fun inicializarElementos(){
        crearVista()
        consultor = FuncionesConsultor(this,etDesde,etHasta,rbPendiente,rbEnviado,rbTodo,imgBuscar,btModificar,btConsultar,btEliminar,rgFiltro)
        consultor.setRadioButtonText("Pendiente-ESTADO = 'P'","Enviado-ESTADO = 'E'","Todo-ESTADO LIKE '%%'")
        etDesde.setText(funcion.getFechaActual())
        etHasta.setText(funcion.getFechaActual())
        etDesde.setOnClickListener{calendario.onCreateDialog(1,etDesde,etDesde)!!.show()}
        etHasta.setOnClickListener{calendario.onCreateDialog(1,etHasta,etDesde)!!.show()}
        imgBuscar.setOnClickListener{buscarDatos()}
        btModificar.setOnClickListener{modificar()}
        btConsultar.setOnClickListener{consultar()}
        btEliminar.setOnClickListener{eliminar()}
    }

    private fun buscarDatos(){
        lista = ArrayList()
        funcion.cargarLista(lista,consultor.buscar("svm_modifica_catastro_cliente","FECHA"))
        mostrar()
    }

    private fun crearVista(){
        var sql = ("drop view if exists svm_modifica_catastro_cliente")
        funcion.ejecutar(sql,this)
        sql = (" create view svm_modifica_catastro_cliente as "
                + "select a.id, a.COD_CLIENTE , a.COD_SUBCLIENTE  , b.DESC_SUBCLIENTE, a.TELEFONO1, a.TELEFONO2, a.DIRECCION "
                + "     , a.CERCA_DE  	, a.LATITUD			, a.LONGITUD 	   , a.FECHA	, a.ESTADO   , a.FOTO_FACHADA "
                + "		, a.TIPO        , b.COD_VENDEDOR "
                + "  from svm_modifica_catastro a,"
                + "		  svm_cliente_vendedor b "
                + "  where a.COD_CLIENTE    = b.COD_CLIENTE "
                + "   and a.COD_SUBCLIENTE = b.COD_SUBCLIENTE "
                + " GROUP BY a.id, a.COD_CLIENTE , a.COD_SUBCLIENTE  , b.DESC_SUBCLIENTE, a.TELEFONO1, a.TELEFONO2, a.DIRECCION,"
                + "			 a.CERCA_DE  	, a.LATITUD			, a.LONGITUD 	   , a.FECHA	, a.ESTADO"
                + " ORDER BY a.ESTADO DESC, a.id")
        funcion.ejecutar(sql,this)
    }

    private fun mostrar(){
        posicion = 0
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5)
        funcion.valores = arrayOf("COD_CLIENTE","COD_SUBCLIENTE","DESC_SUBCLIENTE","FECHA","ESTADO")
        val adapter = Adapter.AdapterGenericoCabecera(this,lista,R.layout.ven_con_lista_consulta_datos_de_clientes,funcion.vistas,funcion.valores)
        lvConsultaDatosDeClientes.adapter = adapter
        lvConsultaDatosDeClientes.setOnItemClickListener { _, view, position, _ ->
            posicion = position
            FuncionesUtiles.posicionCabecera = posicion
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvConsultaDatosDeClientes.invalidateViews()
            cargarDatosCliente(posicion)
        }
        if (lista.size>0){
            cargarDatosCliente(0)
        }
    }

    private fun cargarDatosCliente(position:Int){
        listaCliente = ArrayList()
        val sql = "SELECT * FROM svm_cliente_vendedor " +
                        "  WHERE COD_EMPRESA    = '${FuncionesUtiles.usuario["COD_EMPRESA"]}'   " +
                        "    AND COD_CLIENTE    = '${lista[position]["COD_CLIENTE"]}'           " +
                        "    AND COD_SUBCLIENTE = '${lista[position]["COD_SUBCLIENTE"]}'        " +
                        "    AND COD_VENDEDOR   = '${lista[position]["COD_VENDEDOR"]}'          " +
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
        ListaClientes.codVendedor       = lista[position]["COD_VENDEDOR"].toString()
        Pedidos.vent                    = "N"
        Pedidos.totalPedido             = lista[position]["TOT_COMPROBANTE"].toString()
    }

    private fun modificar(){
        if (lista.size>0){
            if (lista[posicion]["ESTADO"].toString().trim() == "E"){
                funcion.toast(this,"Los datos ya fueron enviados.")
                return
            }
            ModificarCliente.editable = true
            startActivity(Intent(this,ModificarCliente::class.java))
        }
    }

    private fun consultar(){
        if (lista.size>0){
            ModificarCliente.editable = false
            startActivity(Intent(this,ModificarCliente::class.java))
        }
    }

    private fun eliminar(){
        funcion.ejecutar("DELETE FROM svm_modifica_catastro WHERE id = '${lista[posicion]["id"]}' AND COD_VENDEDOR = '${lista[posicion]["COD_VENDEDOR"]}'",this)
        buscarDatos()
    }
}
