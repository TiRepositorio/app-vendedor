package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Base64
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.*
import java.util.*
import java.util.zip.GZIPInputStream

class ConexionWS {

    companion object{
        lateinit var context : Context
        var resultado : String = ""
        private const val NAMESPACE: String = "http://edsystem/servidor"
        private var METHOD_NAME = ""
        private const val URL = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        private var SOAP_ACTION = "${NAMESPACE}/${METHOD_NAME}"
    }

    fun setMethodName(name : String) {
        METHOD_NAME = name
        SOAP_ACTION = "${NAMESPACE}/${METHOD_NAME}"
    }

    fun procesaVersion(codVendedor: String):String{
        lateinit var resultado: String
        METHOD_NAME = "ProcesaVersionVendedorAct"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaVersionVendedorAct"

        val request = SoapObject(NAMESPACE, METHOD_NAME)
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        request.addProperty("codVendedor", codVendedor)
        request.addProperty("version", MainActivity.version)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL)
        try {
            transporte.call(SOAP_ACTION, envelope)
            val sp:SoapPrimitive? = envelope.response as SoapPrimitive?
            resultado = sp.toString()
        } catch (e: Exception){
            var error : String = e.message.toString()
            error += ""
            resultado = error
        }
        return resultado
    }

    fun generaArchivos()  : Boolean{
        METHOD_NAME = "GeneraArchivosVendedor_Act"
        SOAP_ACTION = "http://edsystem/servidor/GeneraArchivosVendedor_Act"
        lateinit var solicitud : SoapObject
        lateinit var resultado : String
        try {
            solicitud = SoapObject(NAMESPACE,METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codVendedor", FuncionesUtiles.usuario["LOGIN"])
            solicitud.addProperty("codPersona", FuncionesUtiles.usuario["COD_PERSONA"])
        } catch (e : Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL,480000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
            if (resultado.indexOf("01*") <= -1){
                return false
            }
        } catch (e : Exception){
            e.message.toString()
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun obtenerArchivos(): Boolean{
        METHOD_NAME = "ObtieneArchivosVendedor"
        SOAP_ACTION = "http://edsystem/servidor/ObtieneArchivosVendedor"
        lateinit var solicitud : SoapObject
        lateinit var resultado : Vector<SoapPrimitive>
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codVendedor", FuncionesUtiles.usuario["LOGIN"])
        } catch (e : Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 240000)
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

    private fun descomprimir(direccionEntrada: String, direccionSalida: String):Boolean{
        try {
            val entrada = GZIPInputStream(FileInputStream(direccionEntrada))
            val salida = FileOutputStream(direccionSalida)
            val buf = ByteArray(1024)
            var len: Int = entrada.read(buf)
            while (len>0){
                salida.write(buf,0,len)
                len = entrada.read(buf)
            }
            salida.close()
            entrada.close()
            val archivo = File(direccionEntrada)
            archivo.delete()
            return true
        }catch (e:FileNotFoundException){

        }catch (e:IOException){

        }
        return false
    }

    @SuppressLint("SdCardPath")
    fun extraerDatos(resultado : Vector<SoapPrimitive>, listaTablas:Vector<String>) : Boolean{
        lateinit var fos : FileOutputStream
        try {
            for (i in 0 until resultado.size){
                fos = FileOutputStream("/data/data/apolo.vendedores.com/" +listaTablas[i].split(" ")[5] + ".gzip")
                fos.write(Base64.decode(resultado[i].toString(),0))
                fos.close()
                descomprimir("/data/data/apolo.vendedores.com/"+listaTablas[i].split(" ")[5] +".gzip",
                    "/data/data/apolo.vendedores.com/"+listaTablas[i].split(" ")[5] +".txt" )
            }
        } catch (e : Exception) {
            e.message
            return false
        }
        return true
    }

    //enviar marcaciones
    fun procesaMarcacionAsistencia(codVendedor: String,vMarcaciones:String) : String{
        METHOD_NAME = "ProcesaMarcacionProm"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaMarcacionProm"

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
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            return e.message.toString()
        }
        return resultado
    }
    fun procesaMarcacionAsistenciaAct(codVendedor: String,vMarcaciones:String) : String{
        METHOD_NAME = "ProcesaMarcacionAsistenciaAct"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaMarcacionAsistenciaAct"

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
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            return e.message.toString()
        }
        return resultado
    }

    fun procesaEnviaSolicitudSD(codRepartidor:String,cabecera:String,detalle:String) : String{
        METHOD_NAME = "ProcesaAutorizaSDVend"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaAutorizaSDVend"

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
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            return e.message.toString()
        }
        return resultado
    }

    fun procesaEnviaNuevaUbicacionCliente( codVendedor: String, codCliente:String, imagenFachada:String) : String{
        METHOD_NAME = "ProcesaActualizaClienteFinal"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaActualizaClienteFinal"

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
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL,240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            return e.message.toString()
        }
        return resultado
    }

    //Enviar datos modificados del cliente
    fun procesaActualizaDatosClienteFinal(codVendedor: String?, clientes: String, FotoFachada: String?): String? {
        METHOD_NAME = "ProcesaActualizaClienteFinal"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaActualizaClienteFinal"
        val solicitud: SoapObject?
        val resultado: String?
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("vcodVendedor", codVendedor)
            solicitud.addProperty("vclientes", clientes.replace("''", " ").replace("'", ""))
            solicitud.addProperty("vfoto_fachada", FotoFachada)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            val sp = envelope.response as SoapPrimitive
            resultado = sp.toString()
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        return resultado
    }


    fun obtieneInstalador(): Boolean {
        METHOD_NAME = "ProcesaInstaladorGestores"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaInstaladorGestores"
        val request: SoapObject?
        val vers = "05"

        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
            request.addProperty("version", vers)
        } catch (e: java.lang.Exception) {
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
            val fos: FileOutputStream?
//            fos = FileOutputStream("/sdcard/apolo_02.apk")
            fos = FileOutputStream(MainActivity2.nombre)
            fos.write(Base64.decode(resultado.toByteArray(),0))
            fos.close()
        } catch (e: java.lang.Exception) {
            return false
        }
        return true
    }

}