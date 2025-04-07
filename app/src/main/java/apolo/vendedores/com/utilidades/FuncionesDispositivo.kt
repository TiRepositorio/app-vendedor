package apolo.vendedores.com.utilidades

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.StrictMode
import android.provider.Settings.Global
import android.provider.Settings.System
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import org.json.JSONObject
import java.net.URL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FuncionesDispositivo(var context: Context) {

    val funcion : FuncionesUtiles = FuncionesUtiles(context)

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun modoAvion():Boolean{
        val valor : Int = Global.getInt(context.contentResolver, Global.AIRPLANE_MODE_ON, 0)
        return if (valor != 0) {
            Toast.makeText(context,"Debe desactivar el modo avion", Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    @SuppressLint("DefaultLocale", "SimpleDateFormat")
    fun zonaHoraria(): Boolean {
        val americaAsuncion: TimeZone = TimeZone.getTimeZone("America/Asuncion")
        val nowAmericaAsuncion: Calendar = Calendar.getInstance(americaAsuncion)
        val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val phoneDateTime: String = df.format(Calendar.getInstance().time)
        val zoneTime = (java.lang.String.format("%02d", nowAmericaAsuncion.get(Calendar.HOUR_OF_DAY)) + ":" + java.lang.String.format("%02d", nowAmericaAsuncion.get(Calendar.MINUTE)))
        val zoneDate = (java.lang.String.format("%02d", nowAmericaAsuncion
                .get(Calendar.DAY_OF_MONTH)) + "/"
                + java.lang.String.format("%02d", nowAmericaAsuncion
                .get(Calendar.MONTH) + 1)
                + "/"
                + java.lang.String.format("%02d", nowAmericaAsuncion.get(Calendar.YEAR)))
        val zoneDateTime = "$zoneDate $zoneTime"
        return if (phoneDateTime != zoneDateTime) {
            Toast.makeText(context, "La zona horaria no coincide con America/Asuncion", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun fechaCorrecta(): Boolean {
        val sql = "SELECT distinct IFNULL(FECHA,'01/01/2020') AS ULTIMA_SINCRO FROM svm_vendedor_pedido order by id desc"
        val cursor:Cursor = funcion.consultar(sql)
        var fecUltimaSincro = ""
        println("esta es la ultima sincronizacion: ${fecUltimaSincro}")
        if (cursor.count > 0) {
            fecUltimaSincro = funcion.dato(cursor,"ULTIMA_SINCRO")
        }

        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        try {
            d = dfDate.parse(fecUltimaSincro)
            println("Este es el d: ${d}")
            d1 = dfDate.parse(dfDate.format(cal.time))
            println("este es d1: +${d1}")
        } catch (e: ParseException) {
            e.printStackTrace()
            e.message
        }
        if (d != null && d1 != null){
            val diffInDays = ((d1.time - d.time) / (1000 * 60 * 60 * 24)).toInt()
            println("Este es el DIA: ${diffInDays}")
//        return true
            return if (diffInDays != 0) {
                println("Este es el DIA: ${diffInDays}")
                Toast.makeText(context, "La fecha actual del sistema: ${d} == ${d1} no coincide con la fecha de sincronizacion", Toast.LENGTH_SHORT).show()
                false
            } else {
                true
            }
        }else{
            Toast.makeText(context, "Las fechas no son válidas ${d} !== ${d1}", Toast.LENGTH_SHORT).show()
            return false
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun getFechaActual():String{

        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        val cal = Calendar.getInstance()

        var fecha = dfDate.format(cal.time) + ""

        return fecha
    }

    fun tarjetaSim(telMgr:TelephonyManager): Boolean {
        var state = true
        when (telMgr.simState) {
            TelephonyManager.SIM_STATE_ABSENT -> {
                Toast.makeText(context, "Insertar SIM para realizar la operacion", Toast.LENGTH_SHORT).show()
                state = false
            }
            TelephonyManager.SIM_STATE_UNKNOWN -> {
                Toast.makeText(context, "Insertar SIM para realizar la operacion", Toast.LENGTH_SHORT).show()
                state = false
            }
            TelephonyManager.SIM_STATE_CARD_IO_ERROR -> { }
            TelephonyManager.SIM_STATE_CARD_RESTRICTED -> { }
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> { }
            TelephonyManager.SIM_STATE_NOT_READY -> { }
            TelephonyManager.SIM_STATE_PERM_DISABLED -> { }
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> { }
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> { }
            TelephonyManager.SIM_STATE_READY -> { }
        }
        return state
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    fun imei(telMgr:TelephonyManager) : String {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "No tiene permisos"
        }
        return telMgr.deviceId
    }*/

    fun horaAutomatica(): Boolean {
        if (System.getInt(context.contentResolver, System.AUTO_TIME, 0) != 1
            || System.getInt(context.contentResolver, System.AUTO_TIME_ZONE, 0) != 1
        ) {
            Toast.makeText(context, "Debe configurar su hora de manera automatica", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validaEstadoSim(telMgr:TelephonyManager):Boolean{
        try {

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //funcion.mensaje(context,"Error!","Debe otorgar a la aplicacion los permisos para acceder al telefono.")
                return false
            }
            if (telMgr.dataNetworkType==0){
                //funcion.mensaje(context,"Error!","Tarjeta sim fuera de servicio.")
                return false
            }
            val suscripcion = SubscriptionManager.from(context).activeSubscriptionInfoList
            for (subscriptionInfo in suscripcion){
                if (subscriptionInfo.iccId.toString().substring(0,4) != "8959"){
                    //funcion.mensaje(context,"Tarjeta SIM extranjera", "Para continuar utilice una tarjeta sim de una operadora nacional.")
                    return false
                }
                if (subscriptionInfo.countryIso.toString().toUpperCase() != "PY"){
                    //funcion.mensaje(context,"Tarjeta SIM extranjera", "Para continuar utilice una tarjeta sim de una operadora nacional.")
                    return false
                }
            }
        } catch (e : Exception){
            return false
        }
        return true
    }

    //obtener aplicaciones con ubicacion simulada
    fun getAppsForMockLocation(context: Context): String {
        val appList: MutableList<String> = mutableListOf()

        val packageManager: PackageManager = context.packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (app in installedApps) {
            try {
                val appInfo = packageManager.getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
                val appPermissions = packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
                val permissions = appPermissions.requestedPermissions



                if (permissions != null) {
                    for (permission in permissions) {
                        if (permission == "android.permission.ACCESS_MOCK_LOCATION") {
                            //appList.add(appInfo.loadLabel(packageManager).toString())
                            if (appInfo.packageName.toString().indexOf("apolo") == -1) {
                                appList.add(appInfo.packageName.toString())
                            }
                            break
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        var paquetes = ""
        appList.forEach {

            if (paquetes == "") {
                paquetes = "(Apps Instaladas: "
            }
            paquetes += "$it "

        }

        if (paquetes !== "") {
            paquetes += ")"
        }

        return paquetes
    }


    fun aplicacionBloqueada(): String {
        var aplicacionBloqueadora = ""
        val sql = "SELECT APLIC_BLOQ FROM svm_vendedor_pedido order by id desc"
        val cursor:Cursor = funcion.consultar(sql)
        var aplicBloq = ""
        if (cursor.count > 0) {
            aplicBloq = funcion.dato(cursor,"APLIC_BLOQ")
        }


        val appBloqList = aplicBloq.split(";")


        val appList: MutableList<String> = mutableListOf()

        val packageManager: PackageManager = context.packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (app in installedApps) {
            try {
                val appInfo = packageManager.getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
                val appPermissions = packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
                val permissions = appPermissions.requestedPermissions



                if (permissions != null) {
                    for (permission in permissions) {
                        if (permission == "android.permission.ACCESS_MOCK_LOCATION") {
                            //appList.add(appInfo.loadLabel(packageManager).toString())
                            if (appInfo.packageName.toString().indexOf("apolo") == -1) {
                                appList.add(appInfo.packageName.toString())
                            }
                            break
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        appList.forEach { it2 ->


            appBloqList.forEach {


                if (it == it2) {
                    aplicacionBloqueadora = it
                    return aplicacionBloqueadora;
                }

            }

        }


        return "";
    }


    fun verificaRoot():Boolean{
        return try {
            Runtime.getRuntime().exec("su")
//            funcion.mensaje(context,"Atención","El teléfono está rooteado.")
            true
        } catch (e : java.lang.Exception) {
            false
        }
    }







}