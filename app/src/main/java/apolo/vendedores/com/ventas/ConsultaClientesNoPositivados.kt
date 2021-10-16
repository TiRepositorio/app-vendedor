package apolo.vendedores.com.ventas

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.DialogoCalendario
import apolo.vendedores.com.utilidades.FuncionesConsultor
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.ventas.justificacion.NoVenta
import kotlinx.android.synthetic.main.activity_consulta_clientes_no_positivados.*
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

class ConsultaClientesNoPositivados : AppCompatActivity() {

    private var calendario : DialogoCalendario = DialogoCalendario(this)
    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    private var posicion : Int = 0
    private lateinit var consultor: FuncionesConsultor
    lateinit var lista : ArrayList<HashMap<String,String>>
    private lateinit var listaCliente : ArrayList<HashMap<String,String>>

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_clientes_no_positivados)

        inicializarElementos()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
        inicializarEtAccion()
    }

    private fun inicializarEtAccion(){
        accion.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim() == "recargarLista"){
                    buscarDatos()
                }
            }

        })
    }

    private fun buscarDatos(){
        lista = ArrayList()
        funcion.cargarLista(lista,consultor.buscar("svm_no_positivados","FECHA"))
        mostrar()
    }

    private fun crearVista(){
        var sql = ("drop view if exists svm_no_positivados")
        funcion.ejecutar(sql,this)
        sql =
            ("create view if not exists svm_no_positivados as Select a.id		 , a.COD_CLIENTE, a.COD_SUBCLIENTE, b.DESC_CLIENTE, b.DESC_SUBCLIENTE, a.COD_MOTIVO , "
                    + " c.DESC_MOTIVO, a.FECHA		, a.ESTADO    , b.COD_VENDEDOR "
                    + " from vt_marcacion_visita  a,"
                    + "		 svm_cliente_vendedor b,"
                    + "		 spm_motivo_no_venta  c "
                    + " where a.COD_CLIENTE    = b.COD_CLIENTE "
                    + "   and a.COD_SUBCLIENTE = b.COD_SUBCLIENTE "
                    + "   and a.COD_MOTIVO     = c.COD_MOTIVO "
                    + " GROUP BY a.id, a.COD_CLIENTE, a.COD_SUBCLIENTE, b.DESC_CLIENTE, b.DESC_SUBCLIENTE, a.COD_MOTIVO, c.DESC_MOTIVO, a.FECHA, a.ESTADO, b.COD_VENDEDOR "
                    + " ORDER BY a.id DESC")
        funcion.ejecutar(sql,this)
    }

    private fun mostrar(){
        posicion = 0
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores = arrayOf("COD_CLIENTE","COD_SUBCLIENTE","DESC_SUBCLIENTE","COD_MOTIVO","DESC_MOTIVO","FECHA","ESTADO")
        val adapter = Adapter.AdapterGenericoCabecera(this,lista,R.layout.ven_con_lista_consulta_clientes_no_positivados,funcion.vistas,funcion.valores)
        lvConsultaClientesNoPositivados.adapter = adapter
        lvConsultaClientesNoPositivados.setOnItemClickListener { _, view, position, _ ->
            posicion = position
            FuncionesUtiles.posicionCabecera = posicion
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvConsultaClientesNoPositivados.invalidateViews()
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
        ListaClientes.codSucursalCliente= listaCliente[0]["COD_SUCURSAL"].toString()
        Pedidos.vent                    = "N"
        Pedidos.totalPedido             = lista[position]["TOT_COMPROBANTE"].toString()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun modificar(){
        if (lista.size>0){
            if (lista[posicion]["ESTADO"].toString().trim() == "E"){
                funcion.toast(this,"Los datos ya fueron enviados.")
                return
            }
            NoVenta.id = lista[posicion]["id"].toString().trim()
            NoVenta.editable = true
            NoVenta.modificacion = true
            NoVenta.nuevo = false
            NoVenta.etAccion = accion
            NoVenta.context = this
            NoVenta.fechaMod = lista[posicion]["FECHA"].toString().trim()
            NoVenta(ListaClientes.codCliente, ListaClientes.codSubcliente, null, null, ListaClientes.latitud, ListaClientes.longitud).cargarDialogo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun consultar(){
        if (lista.size>0){
            NoVenta.id = lista[posicion]["id"].toString().trim()
            NoVenta.editable = false
            NoVenta.modificacion = true
            NoVenta.nuevo = false
            NoVenta.etAccion = accion
            NoVenta.context = this
            NoVenta(ListaClientes.codCliente, ListaClientes.codSubcliente, null, null, ListaClientes.latitud, ListaClientes.longitud).cargarDialogo()
        }
    }

    private fun eliminar(){
        if (lista[posicion]["ESTADO"] == "E"){
            funcion.toast(this,"El registro ya fue enviado.")
            return
        }
        funcion.ejecutar("DELETE FROM vt_marcacion_visita WHERE id = '${lista[posicion]["id"]}' ",this)
        buscarDatos()
    }
}
