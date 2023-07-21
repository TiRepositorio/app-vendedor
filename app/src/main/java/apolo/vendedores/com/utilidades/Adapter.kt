package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.clases.Usuario
import apolo.vendedores.com.configurar.ConfigurarUsuarioIndividual
import apolo.vendedores.com.reportes.ExtractoDeSalario
import apolo.vendedores.com.ventas.DialogoPromocion
import apolo.vendedores.com.ventas.ListaClientes
import apolo.vendedores.com.ventas.Pedidos
import apolo.vendedores.com.ventas.Promociones
import kotlinx.android.synthetic.main.inf_ped_rep_lista_pedidos.view.*
import kotlinx.android.synthetic.main.lista_usuario_detalle.view.*
import kotlinx.android.synthetic.main.rep_canasta_de_marcas.view.*
import kotlinx.android.synthetic.main.rep_ext_sal_debitos.view.*
import kotlinx.android.synthetic.main.rep_ext_sal_haberes.view.*
import kotlinx.android.synthetic.main.ven_lista_sd_detalles.view.*

class Adapter{


    class AdapterGenericoCabecera(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas: IntArray,
        private val valores: Array<String>) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionCabecera == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalEntero(index: String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][index].toString().replace(".", ""))
            }

            return totalValor
        }

        fun getTotalDecimal(index: String):Double{

            var totalDecimal = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    val subtotal = dataSource[i][index].toString().replace(".","")
                    totalDecimal += subtotal.replace(",", ".").replace("%", "").toDouble()
                }
//                totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalDecimal
        }

    }

    class AdapterGenericoDetalle(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas:IntArray,
        private val valores:Array<String>) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionDetalle == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalEntero(parametro : String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][parametro].toString().replace(".", "", false))
            }

            return totalValor
        }

    }

    class AdapterGenericoDetalle2(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas:IntArray,
        private val valores:Array<String>) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionDetalle2 == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

    }

    class AdapterBusqueda(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas:IntArray,
        private val valores:Array<String>) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (DialogoBusqueda.posicion == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

    }

    class AdapterSDDetalle(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas:IntArray,
        private val valores:Array<String>,
        private val accion : String,
        private val etAccion : EditText) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.imgEliminar.setOnClickListener{
                etAccion.setText(accion)
            }
//
            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (apolo.vendedores.com.ventas.sd.SolicitudDevolucion.posDetalles == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

    }

    class AdapterSDEnviado(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas:IntArray,
        private val valores:Array<String>) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

//            if (SolicitudDevolucion.posDetalles == position){
//                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
//            }

            return rowView
        }

    }

    //GENERICO CON SUBLISTA
    class ListaDesplegable(private val context: Context,
                           private val dataSource: ArrayList<HashMap<String, String>>,
                           private val subDataSource: ArrayList<ArrayList<HashMap<String, String>>>,
                           private val molde: Int,
                           private val subMolde: Int,
                           private val vistas: IntArray,
                           private val valores: Array<String>,
                           private val subVistas: IntArray,
                           private val subValores: Array<String>,
                           private val idSubLista: Int,
                           private val layoutSubTabla: Int?) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val adapterSubLista = SubLista(context, subDataSource[position],subMolde, subVistas, subValores,position)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            subLista.visibility = View.GONE

            rowView.imgAbrir.visibility  = View.VISIBLE
            rowView.imgCerrar.visibility = View.GONE
            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource[position].size
            subLista.setOnItemClickListener { _: ViewGroup, _: View, subPosition: Int, _: Long ->
//                subLista.setBackgroundColor(Color.parseColor("#aabbaa"))
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = subPosition
                subLista.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            rowView.setOnClickListener {
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = 0
                if (rowView.imgAbrir.visibility == View.VISIBLE){
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.VISIBLE
                    }
                    rowView.imgAbrir.visibility  = View.GONE
                    rowView.imgCerrar.visibility = View.VISIBLE
                    subLista.visibility = View.VISIBLE
                } else {
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.GONE
                    }
                    rowView.imgAbrir.visibility  = View.VISIBLE
                    rowView.imgCerrar.visibility = View.GONE
                    subLista.visibility = View.GONE
                }
            }

            rowView.setOnFocusChangeListener { v, _ ->  v.invalidate() }


            return rowView
        }

        fun getTotalDecimal(index: String):Double{
            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal
        }

    }

    class SubLista(
        context: Context,
        private val subDataSource: ArrayList<HashMap<String, String>>,
        private val subMolde: Int,
        private val subVistas: IntArray,
        private val subValores: Array<String>,
        private val posicionCabecera : Int) : BaseAdapter()
    {

        private val subInflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getItem(position: Int): HashMap<String,String> {
            return subDataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight = 0


            for (i in subVistas.indices){
                subRowView.findViewById<TextView>(subVistas[i]).text = subDataSource[position][subValores[i]]
                subRowView.findViewById<TextView>(subVistas[i]).setBackgroundResource(R.drawable.border_textview)
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }

//            subRowView.setBackgroundResource(R.drawable.border_textview)
            if (position%2==0){
                subRowView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                subRowView.setBackgroundColor(Color.parseColor("#DDDDDD"))
            }

            if (FuncionesUtiles.posicionDetalle == position && FuncionesUtiles.posicionCabecera == posicionCabecera){
                subRowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return subRowView
        }

        fun getSubTablaHeight(parent: ViewGroup?):Int{
            val subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight = 0
            for (i in subVistas.indices){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

    }

    //GENERICO CON 2 SUBLISTA
    class ListaDesplegable2(private val context: Context,
                           private val dataSource: ArrayList<HashMap<String, String>>,
                           private val subDataSource: ArrayList<ArrayList<HashMap<String, String>>>,
                           private val subDataSource2: ArrayList<ArrayList<ArrayList<HashMap<String, String>>>>,
                           private val molde: Int,
                           private val subMolde: Int,
                           private val subMolde2: Int,
                           private val vistas: IntArray,
                           private val valores: Array<String>,
                           private val subVistas: IntArray,
                           private val subValores: Array<String>,
                           private val subVistas2: IntArray,
                           private val subValores2: Array<String>,
                           private val idSubLista: Int,
                           private val idSubLista2: Int,
                           private val layoutSubTabla: Int?,
                           private val layoutSubTabla2: Int?) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val adapterSubLista = ListaDesplegable3(context,
                subDataSource[position],
                subDataSource2[position],subMolde,subMolde2, subVistas, subValores, subVistas2,subValores2,idSubLista2,layoutSubTabla2)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            subLista.visibility = View.GONE

            rowView.imgAbrir2.visibility  = View.VISIBLE
            rowView.imgCerrar2.visibility = View.GONE
            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource[position].size
            subLista.setOnItemClickListener { _: ViewGroup, _: View, subPosition: Int, _: Long ->
                subLista.setBackgroundColor(Color.parseColor("#aabbaa"))
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = subPosition
                if (subLista[position].imgAbrir.visibility == View.VISIBLE){
                    subLista.layoutParams.height =  (adapterSubLista.getSubTablaHeight(subLista) *
                                    subDataSource2[position][FuncionesUtiles.posicionDetalle].size)

                } else {
                    subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
                    subLista.layoutParams.height = subLista.layoutParams.height * subDataSource[position].size
                }
                subLista.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            rowView.setOnClickListener {
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = 0
                if (rowView.imgAbrir2.visibility == View.VISIBLE){
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.VISIBLE
                    }
                    rowView.imgAbrir2.visibility  = View.GONE
                    rowView.imgCerrar2.visibility = View.VISIBLE
                    subLista.visibility = View.VISIBLE
                } else {
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.GONE
                    }
                    rowView.imgAbrir2.visibility  = View.VISIBLE
                    rowView.imgCerrar2.visibility = View.GONE
                    subLista.visibility = View.GONE
                }
            }

            rowView.setOnFocusChangeListener { v, _ ->  v.invalidate() }


            return rowView
        }

        fun getTotalEntero(index:String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][index].toString().replace(".", "", false))
            }

            return totalValor
        }

        fun getTotalDecimal(index: String):Double{
            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal
        }

    }

    class ListaDesplegable3(private val context: Context,
                           private val dataSource: ArrayList<HashMap<String, String>>,
                           private val subDataSource: ArrayList<ArrayList<HashMap<String, String>>>,
                           private val molde: Int,
                           private val subMolde: Int,
                           private val vistas: IntArray,
                           private val valores: Array<String>,
                           private val subVistas: IntArray,
                           private val subValores: Array<String>,
                           private val idSubLista: Int,
                           private val layoutSubTabla: Int?) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val adapterSubLista = SubLista(context, subDataSource[position],subMolde, subVistas, subValores,position)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            subLista.visibility = View.GONE

            rowView.imgAbrir.visibility  = View.VISIBLE
            rowView.imgCerrar.visibility = View.GONE
            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource[position].size
            subLista.setOnItemClickListener { _: ViewGroup, _: View, subPosition: Int, _: Long ->
//                subLista.setBackgroundColor(Color.parseColor("#aabbaa"))
                FuncionesUtiles.posicionDetalle  = position
                FuncionesUtiles.posicionDetalle2 = subPosition
                subLista.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            rowView.setOnClickListener {
                FuncionesUtiles.posicionDetalle = position
                FuncionesUtiles.posicionDetalle2 = 0
                if (rowView.imgAbrir.visibility == View.VISIBLE) {
                    if (layoutSubTabla != null) {
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla2).visibility =
                            View.VISIBLE
                    }
//                    rowView.layoutParams.height = (rowView.layoutParams.height) + ((subLista.layoutParams.height / dataSource.size) * subDataSource.get(position).size )
                    rowView.imgAbrir.visibility = View.GONE
                    rowView.imgCerrar.visibility = View.VISIBLE
                    subLista.visibility = View.VISIBLE
                } else {
                    if (layoutSubTabla != null) {
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla2).visibility = View.GONE
                    }
                    rowView.imgAbrir.visibility = View.VISIBLE
                    rowView.imgCerrar.visibility = View.GONE
                    subLista.visibility = View.GONE
                }
            }

            rowView.setOnFocusChangeListener { v, _ ->  v.invalidate() }


            return rowView
        }

        fun getSubTablaHeight(parent: ViewGroup?):Int{
            val subRowView = inflater.inflate(subMolde, parent, false)
            var subHeight = 0
            for (i in subVistas.indices){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

    }

    //EXTRACTO DE SALARIO
    class ExtractoDeSalarioHaberes(context: Context, private val dataSource: ArrayList<HashMap<String, String>>) : BaseAdapter() {

        private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.rep_ext_sal_haberes, parent, false)

            rowView.tvNro.text = dataSource[position]["NRO_ORDEN"].toString().replace("null","")
            rowView.tvConcepto.text = dataSource[position]["DESC_CONCEPTO"].toString().replace("null","")
            rowView.tvTotalVenta.text = dataSource[position]["TOT_VENTAS"].toString().replace("null","")
            rowView.tvTotalComision.text = dataSource[position]["MONTO_COMISION"].toString().replace("null","")
            rowView.tvMonto.text = dataSource[position]["MONTO"].toString().replace("null","")

            if (position % 2 == 0) {
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (ExtractoDeSalario.posicionHaberes == position) {
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalVenta():Int{

            var totalVenta = 0

            for (i in 0 until dataSource.size) {
                totalVenta += Integer.parseInt(dataSource[i]["TOT_VENTAS"].toString().replace("null", ""))
            }

            return totalVenta
        }

        fun getTotalComision():Int{

            var totalComision = 0

            for (i in 0 until dataSource.size) {
                totalComision += Integer.parseInt(dataSource[i]["MONTO_COMISION"].toString().replace("null", ""))
            }

            return totalComision
        }

        fun getTotalMonto():Int{

            var totalMonto = 0

            for (i in 0 until dataSource.size) {
                totalMonto += Integer.parseInt(dataSource[i]["MONTO"].toString().replace(".", "", false))
            }

            return totalMonto
        }
    }
    class ExtractoDeSalarioDebitos(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>
    ) : BaseAdapter() {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.rep_ext_sal_debitos, parent, false)

            rowView.tvDebNro.text       = dataSource[position]["NRO_ORDEN"]
            rowView.tvDebConcepto.text  = dataSource[position]["DESC_CONCEPTO"]
            rowView.tvDebCuota.text     = dataSource[position]["NRO_CUOTA"]
            rowView.tvDebMonto.text     = dataSource[position]["MONTO"]

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (ExtractoDeSalario.posicionDebitos == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalMonto():Int{

            var totalMonto = 0

            for (i in 0 until dataSource.size) {
                totalMonto += Integer.parseInt(dataSource[i]["MONTO"].toString().replace(".", "", false))
            }

            return totalMonto
        }

        fun getTotalSaldo(totalHaberes: Int):Int{
            return totalHaberes - getTotalMonto()
        }
    }

    //COMPROBANTES PENDIENTES
    class ListaConSubtabla(private val context: Context,
                                 private val dataSource: ArrayList<HashMap<String, String>>,
                                 private val subDataSource: ArrayList<ArrayList<HashMap<String, String>>>,
                                 private val molde: Int,
                                 private val subMolde: Int,
                                 private val vistas: IntArray,
                                 private val valores: Array<String>,
                                 private val subVistas: IntArray,
                                 private val subValores: Array<String>,
                                 private val idSubLista: Int,
                                 private val layoutSubTabla: Int?) : BaseAdapter()
    {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val adapterSubtabla = Subtabla(context,
                subDataSource[position],subMolde,subVistas,subValores,position)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            val imgAbrir = rowView.findViewById<ImageView>(R.id.imgAbrir)
            val imgCerrar = rowView.findViewById<ImageView>(R.id.imgCerrar)

            rowView.imgAbrir.visibility  = View.VISIBLE
            rowView.imgCerrar.visibility = View.GONE
            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }
            subLista.adapter = adapterSubtabla
            subLista.layoutParams.height = 70 * subDataSource[position].size
            subLista.setOnItemClickListener { _: ViewGroup, _: View, subPosition: Int, _: Long ->
                subLista.setBackgroundColor(Color.parseColor("#aabbaa"))
                FuncionesUtiles.posicionDetalle = subPosition
                subLista.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            rowView.setOnClickListener {
                subLista.adapter = adapterSubtabla//SubCanastaDeMarcas(context, subDataSource)
                FuncionesUtiles.posicionCabecera = position
                if (imgAbrir.visibility == View.VISIBLE){
                    imgAbrir.visibility  = View.GONE
                    imgCerrar.visibility = View.VISIBLE
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(layoutSubTabla).visibility = View.VISIBLE
                    }
                    subLista.visibility = View.VISIBLE
                } else {
                    imgAbrir.visibility  = View.VISIBLE
                    imgCerrar.visibility = View.GONE
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(layoutSubTabla).visibility = View.GONE
                    }
                    subLista.visibility = View.GONE
                }
            }

            return rowView
        }
    }
    class Subtabla(
        context: Context,
        private val subDataSource: ArrayList<HashMap<String, String>>,
        private val subMolde: Int,
        private val subVistas: IntArray,
        private val subValores: Array<String>,
        private val posicionCabecera : Int) : BaseAdapter()
    {

        private val subInflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

//        val subDataSources = subDataSource

        override fun getItem(position: Int): HashMap<String,String> {
            return subDataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight = 0

            for (i in subVistas.indices){
                subRowView.findViewById<TextView>(subVistas[i]).text = subDataSource[position][subValores[i]]
                subRowView.findViewById<TextView>(subVistas[i]).setBackgroundResource(R.drawable.border_textview)
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }

//            subRowView.tvComSubFecComprobante.setText(getItem(position).get("FEC_COMPROBANTE"))
//            subRowView.tvComSubObservacion.setText(getItem(position).get("OBSERVACION"))
//            subRowView.tvComSubDescripcion.setText(getItem(position).get("DESCRIPCION"))
//            subRowView.tvComSubExenta.setText(getItem(position).get("TOT_EXENTA"))
//            subRowView.tvComSubGravada.setText(getItem(position).get("TOT_GRAVADA"))
//            subRowView.tvComSubIva.setText(getItem(position).get("TOT_IVA"))
//            subRowView.tvComSubTotal.setText(getItem(position).get("TOT_COMPROBANTE"))

            if (position%2==0){
                subRowView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                subRowView.setBackgroundColor(Color.parseColor("#DDDDDD"))
            }

            if (FuncionesUtiles.posicionDetalle == position && FuncionesUtiles.posicionCabecera == posicionCabecera){
                subRowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return subRowView
        }

    }

    //PROMOCIONES
    class AdapterPromociones(private val context: Context,
                             private val dataSource: ArrayList<HashMap<String, String>>,
                             private val molde: Int,
                             private val vistas:IntArray,
                             private val valores:Array<String>) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                    if (verificaPromoCargada(position)){
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#000000"))
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#000000"))
                    } else {
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#FF0000"))
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#FF0000"))
                    }
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (Promociones.posPromocion== position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        private fun verificaPromoCargada(position:Int):Boolean{
            if (Pedidos.nuevo){
                return true
            }
            val sql : String = ("SELECT DISTINCT COD_ARTICULO FROM vt_pedidos_det "
                    +  " WHERE NUMERO = '${Pedidos.maximo}' "
                    +  "   AND COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                    +  "   AND NRO_PROMOCION = '${dataSource[position]["NRO_PROMOCION"]}' "
                    +  " ")
            val funcion = FuncionesUtiles(context)
            if (funcion.consultar(sql).count > 0){
                return false
            }
            return true
        }

    }
    class AdapterDialogoPromociones(private val context: Context,
                                    private val dataSource: ArrayList<HashMap<String, String>>,
                                    private val molde: Int,
                                    private val vistas:IntArray,
                                    private val valores:Array<String>) : BaseAdapter()
    {

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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                    if (verificaCargado(position)){
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#000000"))
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#000000"))
                    } else {
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#FF0000"))
                        rowView.findViewById<TextView>(vistas[i]).setTextColor(Color.parseColor("#FF0000"))
                    }
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (DialogoPromocion.posicion == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            if (dataSource[position]["PREC_CAJA"].toString().trim() == "0"){
                rowView.findViewById<TextView>(R.id.tvb3).visibility = View.GONE
            }

            return rowView
        }

        fun getTotalEntero(parametro:String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][parametro].toString().replace(".", "", false))
            }

            return totalValor
        }

        fun getTotalDecimal(parametro: String):Double{
            var totalPorcCump = 0.0

            for (i in 0 until dataSource.size) {
                totalPorcCump += dataSource[i][parametro].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }
            return totalPorcCump
        }

        private fun verificaCargado(position:Int):Boolean{
            if (Pedidos.nuevo){
                return true
            }
            val sql : String = ("SELECT DISTINCT COD_ARTICULO FROM vt_pedidos_det "
                    +  " WHERE NUMERO = '${Pedidos.maximo}' "
                    +  "   AND COD_VENDEDOR = '${ListaClientes.codVendedor}' "
                    +  "   AND COD_ARTICULO = '${dataSource[position]["COD_ARTICULO"]}' "
                    +  " ")
            val funcion = FuncionesUtiles(context)
            if (funcion.consultar(sql).count > 0){
                return false
            }
            return true
        }

    }

    //PEDIDOS
    class AdapterDetallePedido(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas: IntArray,
        private val valores: Array<String>,
        private val etAccion : EditText,
        private val accion: String) : BaseAdapter()
    {

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

        @SuppressLint("SetTextI18n", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }

            rowView.imgEliminar.setOnClickListener {
                etAccion.setText("$accion*$position")
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (Pedidos.posDetalle == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

    }





    //NUEVA CONFIGURACION DE USUARIOS
    class ConfigurarUsuarioNuevo(private val context: Context,
                                 private val dataSource: ArrayList<Usuario>,
                                 private val lvUsuarios: ListView) : BaseAdapter() {

        private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.lista_usuario_detalle, parent, false)

            rowView.tvEmpresa.text = dataSource[position].cod_empresa
            rowView.tvCodigo.text = dataSource[position].login
            rowView.tvNombre.text = dataSource[position].nombre
            rowView.tvVersion.text = dataSource[position].version


            rowView.btnModificarUsuario.setOnClickListener {

                MainActivity2.posicionEditaUsuario = position
                this.context.startActivity(Intent(this.context, ConfigurarUsuarioIndividual::class.java))


            }


            rowView.btnEliminarUsuario.setOnClickListener {
                dataSource.removeAt(position)
                lvUsuarios.invalidateViews()

            }



            return rowView
        }

    }



    class ListaConSubitem(
        private val context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val subDataSource: ArrayList<ArrayList<HashMap<String, String>>>,
        private val molde: Int,
        private val subMolde: Int,
        private val vistas: IntArray,
        private val valores: Array<String>,
        private val subVistas: IntArray,
        private val subValores: Array<String>,
        private val idSubLista: Int,
        private val lista: ListView,
        private val etAccion: EditText?,
        private val accion: String
    ) : BaseAdapter() {

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

        @SuppressLint("ViewHolder", "SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val adapterSubLista = SubLista(context,
                subDataSource[position],subMolde, subVistas, subValores,position)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            val lista = lista
            subLista.visibility = View.GONE

            // rowView.imgAbrir.visibility  = View.VISIBLE
            // rowView.imgCerrar.visibility = View.GONE
            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource[position].size
            subLista.setOnItemClickListener { _: ViewGroup, _: View, subPosition: Int, _: Long ->
                FuncionesUtiles.posicionCabecera = position
                lista.invalidateViews()
                FuncionesUtiles.posicionDetalle = subPosition
                etAccion!!.setText("detalleSublista")
                // rowView.lvSubtabla.invalidateViews()
            }

            if (subDataSource[position].size>0){
                rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.VISIBLE
                subLista.visibility = View.VISIBLE
            } else {
                rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.GONE
                subLista.visibility = View.GONE
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionCabecera == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }
            rowView.setOnClickListener {
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
                rowView.invalidate()
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = 0
                etAccion?.setText(accion)
//                if (rowView.imgAbrir.visibility == View.VISIBLE){
//                    if (layoutSubTabla != null){
//                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.VISIBLE
//                    }
//                    rowView.imgAbrir.visibility  = View.GONE
//                    rowView.imgCerrar.visibility = View.VISIBLE
//                    subLista.visibility = View.VISIBLE
//                } else {
//                    if (layoutSubTabla != null){
//                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.GONE
//                    }
//                    rowView.imgAbrir.visibility  = View.VISIBLE
//                    rowView.imgCerrar.visibility = View.GONE
//                    subLista.visibility = View.GONE
//                }
            }

            return rowView
        }

    }

}