package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import apolo.vendedores.com.R
import apolo.vendedores.com.reportes.ExtractoDeSalario
import kotlinx.android.synthetic.main.inf_ped_rep_lista_pedidos.view.imgAbrir
import kotlinx.android.synthetic.main.inf_ped_rep_lista_pedidos.view.imgCerrar
import kotlinx.android.synthetic.main.rep_canasta_de_marcas.view.*
import kotlinx.android.synthetic.main.rep_ext_sal_debitos.view.*
import kotlinx.android.synthetic.main.rep_ext_sal_haberes.view.*
import kotlinx.android.synthetic.main.ven_lista_sd_detalles.view.*
import java.lang.Exception
import java.text.DecimalFormat

class Adapter{

    companion object{
        val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    }

    class AdapterGenericoCabecera(private val context: Context,
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

        fun getPorcDecimal(index: String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    totalPorcCump += formatNumeroDecimal.format(
                        dataSource[i][index].toString().replace(".", "").replace(",", ".")
                            .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index],Toast.LENGTH_SHORT).show()
                }
            }

            return totalPorcCump/dataSource.size
        }

        fun getPorcDecimal(index: String,total:Double):Double{

            var valor = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    valor += formatNumeroDecimal.format(
                        dataSource[i][index].toString().replace(".", "").replace(",", ".")
                            .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index],Toast.LENGTH_SHORT).show()
                }
            }

            return (total*100)/valor
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
//Porcentaje
        fun getPorcentaje(totalS:String, valorS:String, position: Int):Double{

            val total: Double = dataSource[position][totalS].toString().replace(".","").replace(",",".").replace("%","").toDouble()
            val valor: Double = dataSource[position][valorS].toString().replace(".","").replace(",",".").replace("%","").toDouble()

            return (valor*100)/total
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

        fun getTotalDecimal():Double{

            var totalPorcCump = 0.0

            for (i in 0 until dataSource.size) {
                totalPorcCump += dataSource[i]["PORC_CUMP"].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/dataSource.size
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

        fun getTotalEntero(parametro : String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(
                    dataSource[i][parametro].toString().replace(".", "", false)
                )
            }

            return totalValor
        }

        fun getTotalDecimal():Double{

            var totalPorcCump = 0.0

            for (i in 0 until dataSource.size) {
                totalPorcCump += dataSource[i]["PORC_CUMP"].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/dataSource.size
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

    class AdapterCabecera(private val context: Context,
                          private val dataSource: ArrayList<HashMap<String, String>>,
                          private val molde: Int,
                          private val vistas: IntArray,
                          private val valores: Array<String>,
                          private val vistasCabecera: IntArray) : BaseAdapter()
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
                    rowView.findViewById<TextView>(vistas[i]).visibility = View.VISIBLE
                    rowView.findViewById<TextView>(vistas[i]).width = rowView.findViewById<TextView>(vistasCabecera[i]).width
                    rowView.findViewById<TextView>(vistas[i]).width = rowView.findViewById<TextView>(vistasCabecera[i]).width
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

        fun getPorcDecimal(index: String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    totalPorcCump += formatNumeroDecimal.format(
                        dataSource[i][index].toString().replace(".", "").replace(",", ".")
                            .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index],Toast.LENGTH_SHORT).show()
                }
            }

            return totalPorcCump/dataSource.size
        }

        fun getPorcDecimal(index: String,total:Double):Double{

            var valor = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    valor += formatNumeroDecimal.format(
                        dataSource[i][index].toString().replace(".", "").replace(",", ".")
                            .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index],Toast.LENGTH_SHORT).show()
                }
            }

            return (total*100)/valor
        }

        fun getTotalDecimal(index: String):Double{

            var totalDecimal = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    totalDecimal += dataSource[i][index].toString().replace(".", "")
                        .replace(",", ".").replace("%", "").toDouble()
                }
//                totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalDecimal
        }

        fun getPorcentaje(totalS:String, valorS:String, position: Int):Double{
            val valor: Double = dataSource[position][valorS].toString().replace(".","").replace(",",".").replace("%","").toDouble()
            val total: Double = dataSource[position][totalS].toString().replace(".","").replace(",",".").replace("%","").toDouble()
            return (valor*100)/total
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

        fun getPromedioDecimalSubLista(index:String):Double{

            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                for (j in 0 until subDataSource[i].size) {
                    promDecimal += subDataSource[i][j][index].toString()
                        .replace(".", "")
                        .replace(",", ".")
                        .replace("%", "").toDouble()
                }
                promDecimal /= subDataSource[i].size
            }
            return promDecimal/dataSource.size
        }

        fun getPromedioDecimal(index: String):Double{
            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal = dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal/dataSource.size
        }

        fun getSubTablaHeight(parent: ViewGroup?):Int{
            var subRowView = inflater.inflate(subMolde, parent, false)
            var subHeight = 0
            for (i in subVistas.indices){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
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
        private var height : Int = 0

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
            var subHeight : Int = 0


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

        fun getTotalPorcCump(index:String):Double{

            var totalPorcCump = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump += subDataSource[i][index].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/subDataSource.size
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

        fun getTablaHeight(parent: ViewGroup?):Int{
            val subRowView = inflater.inflate(subMolde, parent, false)
            var subHeight = 0
            for (i in subVistas.indices){
                if (subRowView.findViewById<TextView>(vistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(vistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

        fun getTotalEntero(index:String):Int{

            var totalValor: Int = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource.get(i).get(index).toString().replace(".", "", false))
            }

            return totalValor
        }

        fun getTotalDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal
        }

        fun getPromedioDecimalSubLista(index:String):Double{

            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                for (j in 0 until subDataSource[i].size) {
                    promDecimal += subDataSource[i][j][index].toString()
                        .replace(".", "")
                        .replace(",", ".")
                        .replace("%", "").toDouble()
                }
                promDecimal /= subDataSource[i].size
            }
            return promDecimal/dataSource.size
        }

        fun getPromedioDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal = dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal/dataSource.size
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val adapterSubLista = SubLista(context, subDataSource.get(position),subMolde, subVistas, subValores,position)
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
            rowView.setOnClickListener(View.OnClickListener {
                FuncionesUtiles.posicionDetalle  = position
                FuncionesUtiles.posicionDetalle2 = 0
                if (rowView.imgAbrir.visibility == View.VISIBLE){
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla2).visibility = View.VISIBLE
                    }
//                    rowView.layoutParams.height = (rowView.layoutParams.height) + ((subLista.layoutParams.height / dataSource.size) * subDataSource.get(position).size )
                    rowView.imgAbrir.visibility  = View.GONE
                    rowView.imgCerrar.visibility = View.VISIBLE
                    subLista.visibility = View.VISIBLE
                } else {
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla2).visibility = View.GONE
                    }
                    rowView.imgAbrir.visibility  = View.VISIBLE
                    rowView.imgCerrar.visibility = View.GONE
                    subLista.visibility = View.GONE
                }
            })

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

        fun getPromedioDecimalSubLista(index:String):Double{

            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                for (j in 0 until subDataSource[i].size) {
                    promDecimal += subDataSource[i][j][index].toString()
                        .replace(".", "")
                        .replace(",", ".")
                        .replace("%", "").toDouble()
                }
                promDecimal /= subDataSource[i].size
            }
            return promDecimal/dataSource.size
        }

        fun getPromedioDecimal(index: String):Double{
            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal = dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal/dataSource.size
        }

        fun getSubTablaHeight(parent: ViewGroup?):Int{
            val subRowView = inflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0
            for (i in subVistas.indices){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

    }

    class SubLista1(
        context: Context,
        private val subDataSource: ArrayList<HashMap<String, String>>,
        private val subDataSource2: ArrayList<ArrayList<HashMap<String, String>>>,
        private val subMolde: Int,
        private val subMolde2: Int,
        private val subVistas: IntArray,
        private val subValores: Array<String>,
        private val subVistas2: IntArray,
        private val subValores2: Array<String>,
        private val posicionCabecera : Int) : BaseAdapter()
    {

        private val subInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        private var height = 0

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

        fun getTotalPorcCump(index:String):Double{

            var totalPorcCump = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump += subDataSource[i][index].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/subDataSource.size
        }
    }

    class SubLista2(context: Context,
                    private val subDataSource: ArrayList<HashMap<String, String>>,
                    private val subMolde: Int,
                    private val subVistas: IntArray,
                    private val subValores: Array<String>,
                    private val posicionCabecera : Int,
                    private val posicionSubLista1: Int) : BaseAdapter()
    {

        private val subInflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        private var height = 0

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

        fun getTotalPorcCump(index:String):Double{

            var totalPorcCump = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump += subDataSource[i][index].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/subDataSource.size
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
    class ExtractoDeSalarioDebitos(context: Context, private val dataSource: ArrayList<HashMap<String, String>>, private val totalHaberes:Int) : BaseAdapter() {

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
            val imgAbrir = rowView.findViewById<ImageButton>(R.id.imgAbrir)
            val imgCerrar = rowView.findViewById<ImageButton>(R.id.imgCerrar)

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
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.VISIBLE
                    }
                    subLista.visibility = View.VISIBLE
                } else {
                    imgAbrir.visibility  = View.VISIBLE
                    imgCerrar.visibility = View.GONE
                    if (layoutSubTabla != null){
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.GONE
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

        fun getTotalPorcCump(index:String):Double{

            var totalPorcCump = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump += subDataSource[i][index].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/subDataSource.size
        }
    }

}