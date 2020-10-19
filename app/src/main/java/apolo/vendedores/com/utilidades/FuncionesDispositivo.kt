package apolo.vendedores.com.utilidades

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FuncionesDispositivo(var context: Context) {

    val funcion : FuncionesUtiles = FuncionesUtiles(context)

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun modoAvion():Boolean{
        var valor : Int = 0
        valor = Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)
        return if (valor != 0){
            Toast.makeText(context,"Debe desactivar el modo avion", Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    fun zonaHoraria(): Boolean {
        val nowUtc = Date()
        val americaAsuncion: TimeZone = TimeZone.getTimeZone("America/Asuncion")
        val nowAmericaAsuncion: Calendar = Calendar.getInstance(americaAsuncion)
        val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val phoneDateTime: String = df.format(Calendar.getInstance().getTime())
        val zone_time = (java.lang.String.format(
            "%02d",
            nowAmericaAsuncion
                .get(Calendar.HOUR_OF_DAY)
        ) + ":"
                + java.lang.String.format(
            "%02d",
            nowAmericaAsuncion
                .get(Calendar.MINUTE)
        ))
        val zone_date = (java.lang.String.format(
            "%02d",
            nowAmericaAsuncion
                .get(Calendar.DAY_OF_MONTH)
        ) + "/"
                + java.lang.String.format(
            "%02d",
            nowAmericaAsuncion
                .get(Calendar.MONTH) + 1
        )
                + "/"
                + java.lang.String.format("%02d", nowAmericaAsuncion.get(Calendar.YEAR)))
        val zone_date_time = "$zone_date $zone_time"
        return if (phoneDateTime != zone_date_time) {
            Toast.makeText(
                context,
                "La zona horaria no coincide con America/Asuncion",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else {
            true
        }
    }

    fun fechaCorrecta(): Boolean {
        var sql : String = "SELECT distinct IFNULL(FECHA,'01/01/2020') AS ULTIMA_SINCRO FROM svm_vendedor_pedido order by id desc"
        var cursor:Cursor = funcion.consultar(sql)
        var fecUltimaSincro:String = ""
        if (cursor.count > 0) {
            fecUltimaSincro = funcion.dato(cursor,"ULTIMA_SINCRO")
        }

        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        try {
            d = dfDate.parse(fecUltimaSincro)
            d1 = dfDate.parse(dfDate.format(cal.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val diffInDays = ((d1!!.time - d!!.time) / (1000 * 60 * 60 * 24)).toInt()
        return if (diffInDays != 0) {
            Toast.makeText(
                context,
                "La fecha actual del sistema no coincide con la fecha de sincronizacion",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else {
            true
        }
    }

    fun getFechaActual():String{
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        val cal = Calendar.getInstance()
        return dfDate.format(cal.getTime()) + ""
    }

    fun tarjetaSim(telMgr:TelephonyManager): Boolean {
        var state = true
        val simState = telMgr!!.simState
        when (simState) {
            TelephonyManager.SIM_STATE_ABSENT -> {
                Toast.makeText(
                    context,
                    "Insertar SIM para realizar la operacion",
                    Toast.LENGTH_SHORT
                ).show()
                state = false
            }
            TelephonyManager.SIM_STATE_UNKNOWN -> {
                Toast.makeText(
                    context,
                    "Insertar SIM para realizar la operacion",
                    Toast.LENGTH_SHORT
                ).show()
                state = false
            }
        }
        return state
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun imei(telMgr:TelephonyManager) : String {
        var state = true
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "No tiene permisos"
        }
        return telMgr!!.deviceId
    }

    fun horaAutomatica(): Boolean {
        if (Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.AUTO_TIME,
                0
            ) != 1
            || Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.AUTO_TIME_ZONE,
                0
            ) != 1
        ) {
            Toast.makeText(
                context,
                "Debe configurar su hora de manera automatica",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun validaEstadoSim(telMgr:TelephonyManager):Boolean{
        try {

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                funcion.mensaje(context,"Error!","Debe otorgar a la aplicacion los permisos para acceder al telefono.")
                return false
            }
            if (telMgr.dataNetworkType==0){
                funcion.mensaje(context,"Error!","Tarjeta sim fuera de servicio.")
                return false
            }
            val suscripcion = SubscriptionManager.from(context).activeSubscriptionInfoList
            for (subscriptionInfo in suscripcion){
                if (subscriptionInfo.iccId.toString().substring(0,4) != "8959"){
                    funcion.mensaje(context,"Tarjeta SIM extranjera", "Para continuar utilice una tarjeta sim de una operadora nacional.")
                    return false
                }
//                if (subscriptionInfo.countryIso.toString().toUpperCase() != "PY"){
//                    funcion.mensaje(context,"Tarjeta SIM extranjera", "Para continuar utilice una tarjeta sim de una operadora nacional.")
//                    return false
//                }
            }
        } catch (e : Exception){
            return false
        }
        return true
    }

    fun verificaRoot():Boolean{
        try {
            Runtime.getRuntime().exec("su")
            funcion.mensaje(context,"Atención","El teléfono está rooteado.")
            return false
        } catch (e : java.lang.Exception) {
            return true
        }
    }

}