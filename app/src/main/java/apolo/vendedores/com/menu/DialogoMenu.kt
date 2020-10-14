package apolo.vendedores.com.menu

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.configurar.AcercaDe
import apolo.vendedores.com.configurar.ConfigurarUsuario
import apolo.vendedores.com.informes.*
import apolo.vendedores.com.prueba.VentanaAuxiliar
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.ItemAbrir
import apolo.vendedores.com.utilidades.Sincronizacion
import kotlinx.android.synthetic.main.menu_cab_configurar.*
import java.lang.Exception

class DialogoMenu {

    constructor(context: Context){
        this.context = context
    }

    companion object{
        var posicion = 0
        lateinit var intent : Intent
    }
    var context : Context
    lateinit var dialogo: Dialog
    var lista : ArrayList<ItemAbrir> = ArrayList<ItemAbrir>()

    fun mostrarMenu(menuItem: MenuItem, layout: Int){
        lista = ArrayList<ItemAbrir>()
        when (menuItem.itemId){
            R.id.vendVenta                  -> lista = venta()
            R.id.vendReportes               -> lista = reportes()
            R.id.vendInformes               -> lista = informes()
            R.id.vendConfigurar             -> lista = configurar()
        }
        cargarDialogo(true,layout)
    }

    fun venta():ArrayList<ItemAbrir>{
        var lista:ArrayList<ItemAbrir> = ArrayList<ItemAbrir>()
        lista.add(item((R.drawable.ic_venta).toString(),"Realizar venta",Intent(context,apolo.vendedores.com.ventas.Promotores::class.java)))
        lista.add(item((R.drawable.ic_buscar).toString(),"Consultar",Intent(context,VentanaAuxiliar::class.java)))
        lista.add(item((R.drawable.ic_catastrar).toString(),"Catastar",Intent(context,VentanaAuxiliar::class.java)))
        lista.add(item((R.drawable.ic_reunion).toString(),"Marcar reunión",Intent(context,VentanaAuxiliar::class.java)))
        lista.add(item((R.drawable.ic_calendario).toString(),"Modificar día de visita",Intent(context,VentanaAuxiliar::class.java)))
        return lista
    }

    fun reportes():ArrayList<ItemAbrir>{
        var lista:ArrayList<ItemAbrir> = ArrayList<ItemAbrir>()
        lista.add(item((R.drawable.ic_dolar).toString(),"Avance de comisiones",Intent(context,apolo.vendedores.com.reportes.AvanceDeComisiones::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Extracto de cuenta",Intent(context,apolo.vendedores.com.reportes.ExtractoDeSalario::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Ventas por marca",Intent(context,apolo.vendedores.com.reportes.VentasPorMarca::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Ventas por cliente",Intent(context,apolo.vendedores.com.reportes.VentasPorCliente::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Producción semanal",Intent(context,apolo.vendedores.com.reportes.ProduccionSemanal::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Seguimiento de visitas",Intent(context,apolo.vendedores.com.reportes.SeguimientoDeVisitas::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Comprobantes pendientes a emitir",Intent(context,apolo.vendedores.com.reportes.ComprobantesPendientes::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Cobertura semanal",Intent(context,apolo.vendedores.com.reportes.CoberturaSemanal::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Variables mensuales",Intent(context,apolo.vendedores.com.reportes.VariablesMensuales::class.java)))
        return lista
    }

    fun informes():ArrayList<ItemAbrir>{
        var lista:ArrayList<ItemAbrir> = ArrayList<ItemAbrir>()
        lista.add(item((R.drawable.ic_check).toString(),"Corte de logistica",Intent(context,CorteDeStock::class.java)))
        lista.add(item((R.drawable.ic_check).toString(),"Pedidos en reparto",Intent(context,apolo.vendedores.com.informes.PedidosEnReparto::class.java)))
        lista.add(item((R.drawable.ic_check).toString(),"Rebotes por cliente",Intent(context,apolo.vendedores.com.informes.RebotesPorCliente::class.java)))
        lista.add(item((R.drawable.ic_lista).toString(),"Precios modificados",Intent(context,apolo.vendedores.com.informes.PreciosModificados::class.java)))
        lista.add(item((R.drawable.ic_check).toString(),"Estado de pedidos",Intent(context,apolo.vendedores.com.informes.EstadoDePedidos::class.java)))
        lista.add(item((R.drawable.ic_evol_diaria_venta).toString(),"Evolucion diaria de ventas",Intent(context,EvolucionDiariaDeVentas::class.java)))
        lista.add(item((R.drawable.ic_check).toString(),"Ver anuncio",Intent(context,VentanaAuxiliar::class.java)))
        lista.add(item((R.drawable.ic_lista).toString(),"Lista de precios",Intent(context,ListaDePrecios::class.java)))
        lista.add(item((R.drawable.ic_mapa).toString(),"Ruteo semanal",Intent(context,RuteoSemanal::class.java)))
        return lista
    }

    fun configurar():ArrayList<ItemAbrir>{
        var lista:ArrayList<ItemAbrir> = ArrayList<ItemAbrir>()
        lista.add(item((R.drawable.ic_usuario).toString(),"Configurar usuario",Intent(context,ConfigurarUsuario::class.java)))
        lista.add(item((R.drawable.ic_acerca).toString(),"Acerca de",Intent(context,AcercaDe::class.java)))
        lista.add(item((R.drawable.ic_sincronizar).toString(),"Sincronizar",Intent(context,Sincronizacion::class.java)))
        return lista
    }

    fun cargarDialogo(cancelable:Boolean,layout:Int){
        dialogo = Dialog(context)
        dialogo.setContentView(layout)
        dialogo.lvMenu.adapter = AdapterMenu(context,lista,R.layout.menu_lista)
        dialogo.tvCodigoVend.text = FuncionesUtiles.usuario.get("LOGIN")
        dialogo.tvNombreVend.text = FuncionesUtiles.usuario.get("NOMBRE")
        dialogo.lvMenu.setOnItemClickListener { parent, view, position, id ->
            posicion = position
            dialogo.lvMenu.invalidateViews()
            abrir(position)
        }
        dialogo.setCancelable(cancelable)
        dialogo.show()
    }

    fun item(icono:String,valor:String,intent: Intent):ItemAbrir{
        var hash = HashMap<String,String>()
        hash.put("VALOR",valor)
        hash.put("ICONO",icono)
        var item = ItemAbrir(hash,intent)
        return item
    }

    class AdapterMenu(private val context: Context,
                                  private val dataSource: ArrayList<ItemAbrir>,
                                  private val molde: Int) : BaseAdapter() {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            try {
                rowView.findViewById<TextView>(R.id.tv1).setText(dataSource.get(position).getValor().get("VALOR"))
//                rowView.findViewById<TextView>(R.id.tv1).setBackgroundResource(R.drawable.border_textview)
                rowView.findViewById<ImageView>(R.id.imgIcono).setImageResource(dataSource.get(position).getValor().get("ICONO")!!.toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return rowView
        }

    }

    fun abrir(position: Int){
        intent = Intent(context,VentanaAuxiliar::class.java)
        intent = lista.get(position).getIntent()
        MainActivity2.etAccion.setText("abrir")
    }

}