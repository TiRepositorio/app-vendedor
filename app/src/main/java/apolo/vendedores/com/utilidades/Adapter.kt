package apolo.vendedores.com.utilidades

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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in 0 until vistas.size){
                try {
                    rowView.findViewById<TextView>(vistas[i]).setText(dataSource.get(position).get(valores[i]))
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

            var totalValor: Int = 0

            for (i in 0 until dataSource.size) {
                totalValor = totalValor + Integer.parseInt(dataSource.get(i).get(index).toString().replace(".",""))
            }

            return totalValor
        }

        fun getPorcDecimal(index: String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource.get(i).get(index).toString().contains(Regex("^[\\-\\d+\\%$]"))){
                    totalPorcCump = totalPorcCump + formatNumeroDecimal.format(dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()).toString().replace(",",".").toDouble()
                } else {
                    Toast.makeText(context,dataSource.get(i).get(index),Toast.LENGTH_SHORT).show()
                }
            }

            return totalPorcCump/dataSource.size
        }

        fun getPorcDecimal(index: String,total:Double):Double{

            var valor: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource.get(i).get(index).toString().contains(Regex("^[\\-\\d+\\%$]"))){
                    valor = valor + formatNumeroDecimal.format(dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()).toString().replace(",",".").toDouble()
                } else {
                    Toast.makeText(context,dataSource.get(i).get(index),Toast.LENGTH_SHORT).show()
                }
            }

            return (total*100)/valor
        }

        fun getTotalDecimal(index: String):Double{

            var totalDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource.get(i).get(index).toString().contains(Regex("^[\\-\\d+\\%$]"))){
                    var subtotal = dataSource.get(i).get(index).toString().replace(".","")
                    totalDecimal = totalDecimal + subtotal.replace(",",".").replace("%","").toDouble()
                }
//                totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalDecimal
        }

        fun getPorcentaje(totalS:String, valorS:String, position: Int):Double{
            var total: Double = 0.0
            var valor: Double = 0.0

            total = dataSource.get(position).get(totalS).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            valor = dataSource.get(position).get(valorS).toString().replace(".","").replace(",",".").replace("%","").toDouble()

            return (valor*100)/total
        }

    }

    class AdapterGenericoDetalle(private val context: Context,
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).setText(dataSource.get(position).get(valores[i]))
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

            var totalValor: Int = 0

            for (i in 0 until dataSource.size) {
                totalValor = totalValor + Integer.parseInt(dataSource.get(i).get(parametro).toString().replace(".","",false))
            }

            return totalValor
        }

        fun getTotalDecimal():Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until dataSource.size) {
                totalPorcCump = totalPorcCump + dataSource.get(i).get("PORC_CUMP").toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalPorcCump/dataSource.size
        }

    }

    class AdapterBusqueda(private val context: Context,
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).setText(dataSource.get(position).get(valores[i]))
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

    class AdapterSDDetalle(private val context: Context,
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).setText(dataSource.get(position).get(valores[i]))
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

//            rowView.imgEliminar.setOnClickListener{
//                etAccion.setText(accion)
//            }
//
//            if (position%2==0){
//                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
//            } else {
//                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
//            }
//
//            if (SolicitudDevolucion.posDetalles == position){
//                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
//            }

            return rowView
        }

    }

    class AdapterSDEnviado(private val context: Context,
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).setText(dataSource.get(position).get(valores[i]))
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in 0 until vistas.size){
                try {
                    rowView.findViewById<TextView>(vistas[i]).setText(dataSource.get(position).get(valores[i]))
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

            var totalValor: Int = 0

            for (i in 0 until dataSource.size) {
                totalValor = totalValor + Integer.parseInt(dataSource.get(i).get(index).toString().replace(".",""))
            }

            return totalValor
        }

        fun getPorcDecimal(index: String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource.get(i).get(index).toString().contains(Regex("^[\\-\\d+\\%$]"))){
                    totalPorcCump = totalPorcCump + formatNumeroDecimal.format(dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()).toString().replace(",",".").toDouble()
                } else {
                    Toast.makeText(context,dataSource.get(i).get(index),Toast.LENGTH_SHORT).show()
                }
            }

            return totalPorcCump/dataSource.size
        }

        fun getPorcDecimal(index: String,total:Double):Double{

            var valor: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource.get(i).get(index).toString().contains(Regex("^[\\-\\d+\\%$]"))){
                    valor = valor + formatNumeroDecimal.format(dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()).toString().replace(",",".").toDouble()
                } else {
                    Toast.makeText(context,dataSource.get(i).get(index),Toast.LENGTH_SHORT).show()
                }
            }

            return (total*100)/valor
        }

        fun getTotalDecimal(index: String):Double{

            var totalDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource.get(i).get(index).toString().contains(Regex("^[\\-\\d+\\%$]"))){
                    totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
                }
//                totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalDecimal
        }

        fun getPorcentaje(totalS:String, valorS:String, position: Int):Double{
            var total: Double = 0.0
            var valor: Double = 0.0

            total = dataSource.get(position).get(totalS).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            valor = dataSource.get(position).get(valorS).toString().replace(".","").replace(",",".").replace("%","").toDouble()

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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val adapterSubLista = SubLista(context, subDataSource.get(position),subMolde, subVistas, subValores,position)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            subLista.visibility = View.GONE

            rowView.imgAbrir.visibility  = View.VISIBLE
            rowView.imgCerrar.visibility = View.GONE
            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource.get(position).get(valores[i])
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource.get(position).size
            subLista.setOnItemClickListener { parent: ViewGroup, view: View, subPosition: Int, id: Long ->
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
            rowView.setOnClickListener(View.OnClickListener {
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
            })

            rowView.setOnFocusChangeListener { v, hasFocus ->  v.invalidate() }


            return rowView
        }

        fun getTotalEntero(index:String):Int{

            var totalValor: Int = 0

            for (i in 0 until dataSource.size) {
                totalValor = totalValor + Integer.parseInt(dataSource.get(i).get(index).toString().replace(".","",false))
            }

            return totalValor
        }

        fun getTotalDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource.get(i).get(index).toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal
        }

        fun getPromedioDecimalSubLista(index:String):Double{

            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                for (j in 0 until subDataSource.get(i).size) {
                    promDecimal =
                        promDecimal + subDataSource.get(i).get(j).get(index).toString()
                            .replace(".","")
                            .replace(",", ".")
                            .replace("%", "").toDouble()
                }
                promDecimal = promDecimal / subDataSource.get(i).size
            }
            return promDecimal/dataSource.size
        }

        fun getPromedioDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal = dataSource.get(i).get(index).toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal/dataSource.size
        }

        fun getSubTablaHeight(parent: ViewGroup?):Int{
            var subRowView = inflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0
            for (i in 0 until subVistas.size){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

    }

    class SubLista(private val context: Context,
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
            return subDataSource.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            var subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0


            for (i in 0 until subVistas.size){
                subRowView.findViewById<TextView>(subVistas[i]).text = subDataSource.get(position).get(subValores[i])
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
            var subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0
            for (i in 0 until subVistas.size){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

        fun getTotalPorcCump(index:String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump = totalPorcCump + subDataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
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
            val adapterSubLista = ListaDesplegable3(context, subDataSource.get(position),subDataSource2.get(position),subMolde,subMolde2, subVistas, subValores, subVistas2,subValores2,idSubLista2,layoutSubTabla2)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            subLista.visibility = View.GONE

            rowView.imgAbrir2.visibility  = View.VISIBLE
            rowView.imgCerrar2.visibility = View.GONE
            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource.get(position).get(valores[i])
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource.get(position).size
            subLista.setOnItemClickListener { parent: ViewGroup, view: View, subPosition: Int, id: Long ->
                subLista.setBackgroundColor(Color.parseColor("#aabbaa"))
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = subPosition
                if (subLista.get(position).imgAbrir.visibility == View.VISIBLE){
                    subLista.layoutParams.height =  (adapterSubLista.getSubTablaHeight(subLista) *
                                    subDataSource2.get(position).get(FuncionesUtiles.posicionDetalle).size)

                } else {
                    subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
                    subLista.layoutParams.height = subLista.layoutParams.height * subDataSource.get(position).size
                }
                subLista.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            rowView.setOnClickListener(View.OnClickListener {
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
            })

            rowView.setOnFocusChangeListener { v, hasFocus ->  v.invalidate() }


            return rowView
        }

        fun getTablaHeight(parent: ViewGroup?):Int{
            var subRowView = inflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0
            for (i in 0 until subVistas.size){
                if (subRowView.findViewById<TextView>(vistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(vistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

        fun getTotalEntero(index:String):Int{

            var totalValor: Int = 0

            for (i in 0 until dataSource.size) {
                totalValor = totalValor + Integer.parseInt(dataSource.get(i).get(index).toString().replace(".","",false))
            }

            return totalValor
        }

        fun getTotalDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource.get(i).get(index).toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal
        }

        fun getPromedioDecimalSubLista(index:String):Double{

            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                for (j in 0 until subDataSource.get(i).size) {
                    promDecimal =
                        promDecimal + subDataSource.get(i).get(j).get(index).toString()
                            .replace(".","")
                            .replace(",", ".")
                            .replace("%", "").toDouble()
                }
                promDecimal = promDecimal / subDataSource.get(i).size
            }
            return promDecimal/dataSource.size
        }

        fun getPromedioDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal = dataSource.get(i).get(index).toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal/dataSource.size
        }

        fun refrescar(){

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
            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource.get(position).get(valores[i])
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource.get(position).size
            subLista.setOnItemClickListener { parent: ViewGroup, view: View, subPosition: Int, id: Long ->
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

            rowView.setOnFocusChangeListener { v, hasFocus ->  v.invalidate() }


            return rowView
        }

        fun getTotalEntero(index:String):Int{

            var totalValor: Int = 0

            for (i in 0 until dataSource.size) {
                totalValor = totalValor + Integer.parseInt(dataSource.get(i).get(index).toString().replace(".","",false))
            }

            return totalValor
        }

        fun getTotalDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource.get(i).get(index).toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal
        }

        fun getPromedioDecimalSubLista(index:String):Double{

            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                for (j in 0 until subDataSource.get(i).size) {
                    promDecimal =
                        promDecimal + subDataSource.get(i).get(j).get(index).toString()
                            .replace(".","")
                            .replace(",", ".")
                            .replace("%", "").toDouble()
                }
                promDecimal = promDecimal / subDataSource.get(i).size
            }
            return promDecimal/dataSource.size
        }

        fun getPromedioDecimal(index: String):Double{
            var promDecimal: Double = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal = dataSource.get(i).get(index).toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal/dataSource.size
        }

        fun getSubTablaHeight(parent: ViewGroup?):Int{
            var subRowView = inflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0
            for (i in 0 until subVistas.size){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

    }

    class SubLista1(private val context: Context,
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

        private val subInflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        private var height : Int = 0

        override fun getItem(position: Int): HashMap<String,String> {
            return subDataSource.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            var subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0


            for (i in 0 until subVistas.size){
                subRowView.findViewById<TextView>(subVistas[i]).text = subDataSource.get(position).get(subValores[i])
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
            var subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0
            for (i in 0 until subVistas.size){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

        fun getTotalPorcCump(index:String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump = totalPorcCump + subDataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalPorcCump/subDataSource.size
        }
    }

    class SubLista2(private val context: Context,
                    private val subDataSource: ArrayList<HashMap<String, String>>,
                    private val subMolde: Int,
                    private val subVistas: IntArray,
                    private val subValores: Array<String>,
                    private val posicionCabecera : Int,
                    private val posicionSubLista1: Int) : BaseAdapter()
    {

        private val subInflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        private var height : Int = 0

        override fun getItem(position: Int): HashMap<String,String> {
            return subDataSource.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            var subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0


            for (i in 0 until subVistas.size){
                subRowView.findViewById<TextView>(subVistas[i]).text = subDataSource.get(position).get(subValores[i])
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
            var subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0
            for (i in 0 until subVistas.size){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

        fun getTotalPorcCump(index:String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump = totalPorcCump + subDataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalPorcCump/subDataSource.size
        }
    }

    //EXTRACTO DE SALARIO
    class ExtractoDeSalarioHaberes(private val context: Context, private val dataSource: ArrayList<HashMap<String, String>>) : BaseAdapter() {

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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.rep_ext_sal_haberes, parent, false)

            rowView.tvNro.setText(dataSource.get(position).get("NRO_ORDEN").toString().replace("null",""))
            rowView.tvConcepto.setText(dataSource.get(position).get("DESC_CONCEPTO").toString().replace("null",""))
            rowView.tvTotalVenta.setText(dataSource.get(position).get("TOT_VENTAS").toString().replace("null",""))
            rowView.tvTotalComision.setText(dataSource.get(position).get("MONTO_COMISION").toString().replace("null",""))
            rowView.tvMonto.setText(dataSource.get(position).get("MONTO").toString().replace("null",""))

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

            var totalVenta: Int = 0

            for (i in 0 until dataSource.size) {
                totalVenta = totalVenta + Integer.parseInt(dataSource.get(i).get("TOT_VENTAS").toString().replace("null",""))
            }

            return totalVenta
        }

        fun getTotalComision():Int{

            var totalComision: Int = 0

            for (i in 0 until dataSource.size) {
                totalComision = totalComision + Integer.parseInt(dataSource.get(i).get("MONTO_COMISION").toString().replace("null",""))
            }

            return totalComision
        }

        fun getTotalMonto():Int{

            var totalMonto: Int = 0

            for (i in 0 until dataSource.size) {
                totalMonto = totalMonto + Integer.parseInt(dataSource.get(i).get("MONTO").toString().replace(".","",false))
            }

            return totalMonto
        }
    }
    class ExtractoDeSalarioDebitos(private val context: Context, private val dataSource: ArrayList<HashMap<String, String>>, private val totalHaberes:Int) : BaseAdapter() {

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
            val rowView = inflater.inflate(R.layout.rep_ext_sal_debitos, parent, false)

            rowView.tvDebNro.text       = dataSource.get(position).get("NRO_ORDEN")
            rowView.tvDebConcepto.text  = dataSource.get(position).get("DESC_CONCEPTO")
            rowView.tvDebCuota.text     = dataSource.get(position).get("NRO_CUOTA")
            rowView.tvDebMonto.text     = dataSource.get(position).get("MONTO")

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

            var totalMonto: Int = 0

            for (i in 0 until dataSource.size) {
                totalMonto = totalMonto + Integer.parseInt(dataSource.get(i).get("MONTO").toString().replace(".","",false))
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
            val adapterSubtabla : Subtabla = Subtabla(context, subDataSource.get(position),subMolde,subVistas,subValores,position)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            val imgAbrir = rowView.findViewById<ImageButton>(R.id.imgAbrir)
            val imgCerrar = rowView.findViewById<ImageButton>(R.id.imgCerrar)

            rowView.imgAbrir.visibility  = View.VISIBLE
            rowView.imgCerrar.visibility = View.GONE
            for (i in 0 until vistas.size){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource.get(position).get(valores[i])
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }
//
//            rowView.imgComAbrir.visibility  = View.VISIBLE
//            rowView.imgComCerrar.visibility = View.GONE
//            rowView.tvComPeriodo.setText(dataSource.get(position).get("PERIODO"))
//            rowView.tvComConcepto.setText(dataSource.get(position).get("DESCRIPCION"))
//            rowView.tvComExenta.setText(dataSource.get(position).get("TOT_EXENTA"))
//            rowView.tvComGravada.setText(dataSource.get(position).get("TOT_GRAVADA"))
//            rowView.tvComIva.setText(dataSource.get(position).get("TOT_IVA"))
//            rowView.tvComMonto.setText(dataSource.get(position).get("TOT_COMPROBANTE"))
            subLista.adapter = adapterSubtabla
            subLista.layoutParams.height = 70 * subDataSource.get(position).size
            subLista.setOnItemClickListener { parent: ViewGroup, view: View, subPosition: Int, id: Long ->
                subLista.setBackgroundColor(Color.parseColor("#aabbaa"))
                FuncionesUtiles.posicionDetalle = subPosition
                subLista.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            rowView.setOnClickListener(View.OnClickListener {
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
            })

            return rowView
        }
    }
    class Subtabla(private val context: Context,
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
            return subDataSource.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            var subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight : Int = 0

            for (i in 0 until subVistas.size){
                subRowView.findViewById<TextView>(subVistas[i]).text = subDataSource.get(position).get(subValores[i])
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

            var totalPorcCump: Double = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump = totalPorcCump + subDataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalPorcCump/subDataSource.size
        }
    }

}