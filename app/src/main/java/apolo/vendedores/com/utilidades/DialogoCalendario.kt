package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.widget.EditText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DialogoCalendario(var context: Context) {

    private val dateDialogId : Int = 1
    private var mYear : Int = 0
    private var mMonth : Int = 0
    private var mDay : Int = 0
    var funcion : FuncionesUtiles = FuncionesUtiles(context)
    private lateinit var etDesde: EditText
    private lateinit var etACargar:EditText

    private val mDateSetListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            if (etACargar == etDesde) {
                fechaDesde(etACargar)
            } else {
                fechaHasta(etACargar)
            }
        }

    fun onCreateDialog(id: Int,etACargar:EditText, etDesde: EditText): Dialog? {
        this.etACargar = etACargar
        this.etDesde = etDesde
        mYear = etACargar.text.toString().split("/")[2].toInt()
        mMonth = etACargar.text.toString().split("/")[1].toInt()-1
        mDay = etACargar.text.toString().split("/")[0].toInt()
        when (id) {
            dateDialogId -> return DatePickerDialog(context, mDateSetListener, mYear, mMonth,mDay)
        }
        return null
    }

    @SuppressLint("SimpleDateFormat")
    private fun fechaDesde(etDesde:EditText) {
        mMonth += 1
        val mes: String = if (mMonth <= 9) {
            "0" + StringBuilder().append(mMonth)
        } else {
            "" + StringBuilder().append(mMonth)
        }
        val dia: String = if (mDay <= 9) {
            "0" + StringBuilder().append(mDay)
        } else {
            "" + StringBuilder().append(mDay)
        }
        etDesde.setText(StringBuilder()
                .append(dia).append("/").append(mes).append("/").append(mYear)
                .append(""))
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        try {
            d = dfDate.parse(etDesde.text.toString())
            d1 = dfDate.parse(dfDate.format(cal.time)) // Returns
            // 15/10/2012
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val diffInDays = ((d!!.time - d1!!.time) / (1000 * 60 * 60 * 24)).toInt()
        println(diffInDays.toString())
    }

    @SuppressLint("SimpleDateFormat")
    private fun fechaHasta(etHasta:EditText) {
        mMonth += 1
        val mes: String = if (mMonth <= 9) {
            "0" + java.lang.StringBuilder().append(mMonth)
        } else {
            "" + java.lang.StringBuilder().append(mMonth)
        }
        val dia: String = if (mDay <= 9) {
            "0" + java.lang.StringBuilder().append(mDay)
        } else {
            "" + java.lang.StringBuilder().append(mDay)
        }
        etHasta.setText(
            java.lang.StringBuilder()
                .append(dia).append("/").append(mes).append("/").append(mYear)
                .append("")
        )
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        try {
            d = dfDate.parse(etDesde.text.toString())
            d1 = dfDate.parse(etHasta.text.toString()) // Returns
            // 15/10/2012
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        var diffInDays = ((d1!!.time - d!!.time) / (1000 * 60 * 60 * 24)).toInt()
        println(diffInDays.toString())
        if (diffInDays < 0) {
            funcion.mensaje(context,"Atencion","Fecha incorrecta")
            val sdf = SimpleDateFormat(
                "dd/MM/yyyy"
            )
            etHasta.setText(sdf.format(Date()))
        } else {
            try {
                d = dfDate.parse(etHasta.text.toString())
                d1 = dfDate.parse(dfDate.format(cal.time)) // Returns
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            diffInDays = ((d!!.time - d1!!.time) / (1000 * 60 * 60 * 24)).toInt()
            println(diffInDays.toString())
        }

    }

    /*fun fechasProxSemana():ArrayList<String>{
        val fechas : ArrayList<String> = ArrayList()
        val year : Int = funcion.getFechaActual().split("/")[2].toInt()
        val mes : Int = funcion.getFechaActual().split("/")[1].toInt()
        val dia : Int = funcion.getFechaActual().split("/")[0].toInt()
        val fecha : Calendar = Calendar.getInstance()
        fecha.set(year,mes-1,dia)
        while (fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            fecha.add(Calendar.DATE,1)
        }
        for (i in 0 until 7){
            var fec = ""
            fecha.add(Calendar.DATE,1)
            if (fecha.get(Calendar.DAY_OF_MONTH) < 10) fec +=  "0"
            fec += fecha.get(Calendar.DAY_OF_MONTH).toString() + "/"
            if (fecha.get(Calendar.MONTH) + 1 <10) fec += "0"
            fec += (fecha.get(Calendar.MONTH)+1).toString() + "/" + fecha.get(Calendar.YEAR)
            fechas.add(fec)
        }
        return fechas
    }
*/
    /*fun fechasSemana(fecTemp:String):ArrayList<String>{
        val fechas : ArrayList<String> = ArrayList()
        val year : Int = fecTemp.split("/")[2].toInt()
        val mes  : Int = fecTemp.split("/")[1].toInt()
        val dia  : Int = fecTemp.split("/")[0].toInt()
        val fecha : Calendar = Calendar.getInstance()
        fecha.set(year,mes-1,dia)
        while (fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            fecha.add(Calendar.DATE,-1)
        }
        for (i in 0 until 7){
            var fec = ""
            fecha.add(Calendar.DATE,1)
            if (fecha.get(Calendar.DAY_OF_MONTH) < 10) fec +=  "0"
            fec += fecha.get(Calendar.DAY_OF_MONTH).toString() + "/"
            if (fecha.get(Calendar.MONTH) + 1 <10) fec += "0"
            fec += (fecha.get(Calendar.MONTH)+1).toString() + "/" + fecha.get(Calendar.YEAR)
            fechas.add(fec)
        }
        return fechas
    }
*/
}