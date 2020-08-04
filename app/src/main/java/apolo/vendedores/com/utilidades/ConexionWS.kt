package apolo.vendedores.com.utilidades

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Base64
import apolo.vendedores.com.MainActivity
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.*
import java.util.*
import java.util.zip.GZIPInputStream


class ConexionWS {

    private val NAMESPACE = "http://edsystem/servidor"
    private var METHOD_NAME = ""
    private val URL = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
    private var SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME

    companion object{
        lateinit var context : Context
        var resultado : String = ""
    }

    fun setMethodName(name : String) {
        METHOD_NAME = name
        SOAP_ACTION = "$NAMESPACE/$METHOD_NAME"
    }

    fun procesaVersion(codVendedor: String):String{
        lateinit var resultado: String
        var NAMESPACE:String   = "http://edsystem/servidor";
        var URL:String         = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem";
        var METHOD_NAME:String = "ProcesaVersionVendedorAct";
        var SOAP_ACTION:String = "http://edsystem/servidor/ProcesaVersionVendedorAct";

        var request: SoapObject = SoapObject(NAMESPACE, METHOD_NAME)
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        request.addProperty("codVendedor", FuncionesUtiles.usuario.get("LOGIN"))
        request.addProperty("version", MainActivity.version)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        var transporte: HttpTransportSE = HttpTransportSE(URL)
        try {
            transporte.call(SOAP_ACTION, envelope)
            var sp:SoapPrimitive? = envelope.response as SoapPrimitive?
            resultado = sp.toString()
        } catch (e: Exception){
            var error : String = e.message.toString()
            error = error + ""
            resultado = error
        }
        return resultado
    }

    fun generaArchivos()  : Boolean{
        val NAMESPACE   : String = "http://edsystem/servidor"
        val URL         : String = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val METHOD_NAME : String = "GeneraArchivosVendedor_Act"
        val SOAP_ACTION : String = "http://edsystem/servidor/GeneraArchivosVendedor_Act"
        lateinit var solicitud : SoapObject
        lateinit var resultado : String
        try {
            solicitud = SoapObject(NAMESPACE,METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", "1")
            solicitud.addProperty("codVendedor", FuncionesUtiles.usuario.get("LOGIN"))
            solicitud.addProperty("codPersona", FuncionesUtiles.usuario.get("COD_PERSONA"))
        } catch (e : Exception){
            return false
        }
        var envelope : SoapSerializationEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        var transporte: HttpTransportSE = HttpTransportSE(URL,480000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
            if (!(resultado.indexOf("01*")==-1)){
                return false
            }
        } catch (e : Exception){
            var error : String = e.message.toString()
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun obtenerArchivos(): Boolean{
        val NAMESPACE   : String = "http://edsystem/servidor"
        val URL         : String = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val METHOD_NAME : String = "ObtieneArchivosPromotor"
        val SOAP_ACTION : String = "http://edsystem/servidor/ObtieneArchivosPromotor"
        lateinit var solicitud : SoapObject
        lateinit var resultado : Vector<SoapPrimitive>
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", "1")
            solicitud.addProperty("codVendedor", FuncionesUtiles.usuario.get("LOGIN"))
        } catch (e : Exception){
            return false
        }
        var envelope : SoapSerializationEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        var transporte : HttpTransportSE = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response as Vector<SoapPrimitive>
        } catch (e : Exception){
            e.message
            return false
        }
        try {
            val listaTablas : Vector<String> = MainActivity.tablasSincronizacion.listaSQLCreateTables()
            if (resultado.size>4){
                extraerDatos(resultado, listaTablas)
            }
        } catch (e : Exception){
            return false
        }
        return true
    }

    fun descomprimir(direccionEntrada: String, direccionSalida: String):Boolean{
        try {
            var entrada : GZIPInputStream  = GZIPInputStream(FileInputStream(direccionEntrada))
            var salida  : FileOutputStream = FileOutputStream(direccionSalida)
            val buf = ByteArray(1024)
            var len: Int = entrada.read(buf)
            while (len>0){
                salida.write(buf,0,len)
                len = entrada.read(buf)
            }
            salida.close()
            entrada.close()
            var archivo : File = File(direccionEntrada)
            archivo.delete()
            return true
        }catch (e:FileNotFoundException){

        }catch (e:IOException){

        }
        return false
    }

    fun extraerDatos(resultado : Vector<SoapPrimitive>, listaTablas:Vector<String>) : Boolean{
        lateinit var fos : FileOutputStream
        try {
            for (i in 0 until resultado.size){
                fos = FileOutputStream("/data/data/apolo.vendedores.com/" +listaTablas[i].split(" ")[5] + ".gzip")
                fos.write(Base64.decode(resultado.get(i).toString(),0))
                fos.close()
                descomprimir("/data/data/apolo.vendedores.com/"+listaTablas[i].split(" ")[5] +".gzip",
                    "/data/data/apolo.vendedores.com/"+listaTablas[i].split(" ")[5] +".txt" )
            }
        } catch (e : Exception) {
            var error = e.message
            error = "" + error
            return false
        }
        return true
    }

    //enviar marcaciones
    fun procesaMarcacionAsistencia(codVendedor: String,vMarcaciones:String) : String{
        val NAMESPACE   : String = "http://edsystem/servidor"
        val URL         : String = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val METHOD_NAME : String = "ProcesaMarcacionProm"
        val SOAP_ACTION : String = "http://edsystem/servidor/ProcesaMarcacionProm"

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(NAMESPACE,METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", "1")
            solicitud.addProperty("codVendedor", codVendedor)
            solicitud.addProperty("detalle", vMarcaciones)
        } catch (e : Exception){
            var error : String = e.message.toString()
            return error
        }

        var envelope : SoapSerializationEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        var transporte: HttpTransportSE = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            var error : String= e.message.toString()
//            error = "" + error
            return error
        }
        return resultado
    }
    fun procesaMarcacionAsistenciaAct(codVendedor: String,vMarcaciones:String) : String{
        val NAMESPACE   : String = "http://edsystem/servidor"
        val URL         : String = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val METHOD_NAME : String = "ProcesaMarcacionAsistenciaAct"
        val SOAP_ACTION : String = "http://edsystem/servidor/ProcesaMarcacionAsistenciaAct"

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(NAMESPACE,METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", "1")
            solicitud.addProperty("codVendedor", codVendedor)
            solicitud.addProperty("detalle", vMarcaciones)
        } catch (e : Exception){
            var error : String = e.message.toString()
            return error
        }

        var envelope : SoapSerializationEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        var transporte: HttpTransportSE = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            var error : String= e.message.toString()
//            error = "" + error
            return error
        }
        return resultado
    }

    fun procesaEnviaSolicitudSD(codRepartidor:String,cabecera:String,detalle:String) : String{
        val NAMESPACE   : String = "http://edsystem/servidor"
        val URL         : String = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val METHOD_NAME : String = "ProcesaAutorizaSDProm"
        val SOAP_ACTION : String = "http://edsystem/servidor/ProcesaAutorizaSDProm"

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(NAMESPACE,METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", "1")
            solicitud.addProperty("codRepartidor", codRepartidor)
            solicitud.addProperty("cabecera", cabecera)
            solicitud.addProperty("detalle", detalle)
        } catch (e : Exception){
            var error : String = e.message.toString()
            return error
        }

        var envelope : SoapSerializationEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        var transporte: HttpTransportSE = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            var error : String= e.message.toString()
            return error
        }
        return resultado
    }

    fun procesaEnviaNuevaUbicacionCliente( codVendedor: String, codCliente:String, imagenFachada:String) : String{
        val NAMESPACE   : String = "http://edsystem/servidor"
        val URL         : String = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val METHOD_NAME : String = "ProcesaActualizaClienteFinal"
        val SOAP_ACTION : String = "http://edsystem/servidor/ProcesaActualizaClienteFinal"

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(NAMESPACE,METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("vcodVendedor", codVendedor)
            solicitud.addProperty("vclientes", codCliente.replace("''","").replace("'",""))
            solicitud.addProperty("vfoto_fachada", imagenFachada)
        } catch (e : Exception){
            var error : String = e.message.toString()
            return error
        }

        var envelope : SoapSerializationEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        var transporte: HttpTransportSE = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            var error : String= e.message.toString()
            return error
        }
        return resultado
    }

    fun obtieneInstalador(): Boolean {
        val NAMESPACE = "http://edsystem/servidor"
        val URL = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val METHOD_NAME = "ProcesaInstaladorGestores"
        val SOAP_ACTION = "http://edsystem/servidor/ProcesaInstaladorGestores"
        var request: SoapObject? = null
        var vers = "05"

        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
            request.addProperty("version", vers)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            val sp = envelope.response as SoapPrimitive
            resultado = sp.toString()
            var fos: FileOutputStream? = null
//            fos = FileOutputStream("/sdcard/apolo_02.apk")
            fos = FileOutputStream(MainActivity.nombre)
            fos.write(Base64.decode(resultado.toString().toByteArray(),0))
            fos.close()
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return false
        }
        return true
    }

}