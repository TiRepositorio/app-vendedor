package apolo.vendedores.com.utilidades

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Base64
import apolo.vendedores.com.MainActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.ventas.catastro.CatastrarCliente
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
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        var resultados : String = ""
        private const val NAMESPACE: String = "http://edsystem/servidor"
        private var METHOD_NAME = ""
        private const val URL = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        private var SOAP_ACTION = "${NAMESPACE}/${METHOD_NAME}"
    }

    private fun setMethodName(name: String) {
        METHOD_NAME = name
        SOAP_ACTION = "${NAMESPACE}/${METHOD_NAME}"
    }

    fun procesaVersion():String{
        lateinit var resultado: String
        setMethodName("ProcesaVersionGestor")

        val request = SoapObject(NAMESPACE, METHOD_NAME)
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        request.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
        request.addProperty("codPersona", FuncionesUtiles.usuario["LOGIN"])
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

    /*fun generaArchivos()  : Boolean{
        setMethodName("GeneraArchivosGestor")
        lateinit var solicitud : SoapObject
        lateinit var resultado : String
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codPersona", FuncionesUtiles.usuario["COD_EMPRESA"])
            solicitud.addProperty("codEmpresa", "1")
//            solicitud.addProperty("codPersona", FuncionesUtiles.usuario["COD_PERSONA"])
        } catch (e: Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.xsd = SoapSerializationEnvelope.XSD
        envelope.enc = SoapSerializationEnvelope.ENC
        MarshalHashtable().register(envelope)
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 600000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
            if (resultado.indexOf("01*") <= -1){
                return false
            }
        } catch (e: Exception){
            resultados = e.message.toString()
            return false
        }
        return true
    }
*/
    fun generaArchivos()  : Boolean{
        setMethodName("GeneraArchivosGestor")
        lateinit var solicitud : SoapObject
        lateinit var resultado : String
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codPersona", FuncionesUtiles.usuario["LOGIN"])
            solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
//            solicitud.addProperty("codPersona", FuncionesUtiles.usuario["COD_PERSONA"])
        } catch (e: Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 600000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
            if (resultado.indexOf("01*") <= -1){
                return false
            }
        } catch (e: Exception){
            resultados = e.message.toString()
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun obtenerArchivos(): Boolean{
        setMethodName("ObtieneArchivosGestor")
        lateinit var solicitud : SoapObject
        lateinit var resultado : Vector<*>
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
            solicitud.addProperty("codVendedor", FuncionesUtiles.usuario["LOGIN"])
        } catch (e: Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response as Vector<*>
        } catch (e: Exception) {
            resultados = e.message.toString()
            return false
        }
        try {
            val listaTablas : ArrayList<String> = MainActivity.tablasSincronizacion.listaSQLCreateTables()
            if (resultado.size>4){
                extraerDatos(resultado, listaTablas)
            }
        } catch (e: Exception){
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
                salida.write(buf, 0, len)
                len = entrada.read(buf)
            }
            salida.close()
            entrada.close()
            val archivo = File(direccionEntrada)
            archivo.delete()
            return true
        }catch (e: FileNotFoundException){

        }catch (e: IOException){

        }
        return false
    }

    @SuppressLint("SdCardPath")
    fun extraerDatos(resultado: Vector<*>, listaTablas: ArrayList<String>) : Boolean{
        lateinit var fos : FileOutputStream
        try {
            for (i in 0 until resultado.size){
                fos = FileOutputStream("/data/data/apolo.vendedores.com/" + listaTablas[i].split(" ")[5] + ".gzip")
                fos.write(Base64.decode(resultado[i].toString(), 0))
                fos.close()
                descomprimir(
                    "/data/data/apolo.vendedores.com/" + listaTablas[i].split(" ")[5] + ".gzip",
                    "/data/data/apolo.vendedores.com/" + listaTablas[i].split(" ")[5] + ".txt"
                )
            }
        } catch (e: Exception) {
            e.message
            return false
        }
        return true
    }

    //enviar marcaciones
    fun procesaMarcacionAsistencia(codVendedor: String, vMarcaciones: String, codEmpresa: String) : String{
        setMethodName("ProcesaMarcacionProm")

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            //solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario[COD_EMPRESA])
            solicitud.addProperty("codEmpresa", codEmpresa)
            solicitud.addProperty("codVendedor", codVendedor)
            solicitud.addProperty("detalle", vMarcaciones)
        } catch (e: Exception){
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e: Exception){
            return e.message.toString()
        }
        return resultado
    }
    fun procesaMarcacionAsistenciaAct(codVendedor: String, vMarcaciones: String, codEmpresa: String) : String{
        setMethodName("ProcesaMarcacionAsistenciaAct")

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            //solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario[COD_EMPRESA])
            solicitud.addProperty("codEmpresa", codEmpresa)
            solicitud.addProperty("codVendedor", codVendedor)
            solicitud.addProperty("detalle", vMarcaciones)
        } catch (e: Exception){
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e: Exception){
            return e.message.toString()
        }
        return resultado
    }

    fun procesaEnviaSolicitudSD(codRepartidor: String, cabecera: String, detalle: String, codEmpresa: String) : String{
        setMethodName("ProcesaAutorizaSDVend")

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            //solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario[COD_EMPRESA])
            solicitud.addProperty("codEmpresa", codEmpresa)
            solicitud.addProperty("codRepartidor", codRepartidor)
            solicitud.addProperty("cabecera", cabecera)
            solicitud.addProperty("detalle", detalle)
        } catch (e: Exception){
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
        } catch (e: Exception){
            return e.message.toString()
        }
        return resultado
    }

    //Enviar datos modificados del cliente
    fun procesaActualizaDatosClienteFinal(
        codVendedor: String?,
        clientes: String,
        FotoFachada: String?
    ): String {
        setMethodName("ProcesaActualizaClienteFinalAct")
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
    //Catastrar cliente
    fun procesaCatastroClienteFinal(
        cod_cliente: String?,
        vcliente: String,
        vFotoFachada: String?
    ): String {
        METHOD_NAME = "ProcesaClienteFinalAct"
        SOAP_ACTION = "http://edsystem/servidor/ProcesaClienteFinalAct"
        val request: SoapObject?
        val resultado: String?
        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
            request.addProperty("vcodVendedor", CatastrarCliente.codVendedor)
            request.addProperty("vcodCliente", cod_cliente)
            request.addProperty("vclientes", vcliente.replace("''", " ").replace("'", ""))
            request.addProperty("vfoto_fachada", vFotoFachada)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
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
        setMethodName("ProcesaInstaladorGestores")
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
            resultados = sp.toString()
            val fos: FileOutputStream?
//            fos = FileOutputStream("/sdcard/apolo_02.apk")
            fos = FileOutputStream(MainActivity2.nombre)
            fos.write(Base64.decode(resultados.toByteArray(), 0))
            fos.close()
        } catch (e: java.lang.Exception) {
            return false
        }
        return true
    }

    fun procesaNoVenta(noVenta: String, codVendedor: String, codEmpresa: String): String {
            setMethodName("ProcesaNoVenta")
        val request: SoapObject
        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
            //request.addProperty("vcodEmpresa", FuncionesUtiles.usuario[COD_EMPRESA])
            request.addProperty("vcodEmpresa", codEmpresa)
            request.addProperty("vcodVendedor", codVendedor)
            request.addProperty("nopositivado", noVenta)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL)
        var res: String
        res = try {
            transporte.call(SOAP_ACTION, envelope)
            val resultadoXml = envelope.response as SoapPrimitive
            resultadoXml.toString()
        } catch (e: java.lang.Exception) {
            e.message.toString()
        }
        if (res.contains("unique constraint (INV")) {
            res = "01*Ya se guardo la justificacion"
        }
        return res
    }

    fun enviarPedido(cabecera: String, detalle: String, nroComprobante: String, codVendedor: String, codEmpresa: String): String {
        setMethodName("ProcesaPedido")
        val request = SoapObject(NAMESPACE, METHOD_NAME)
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        //request.addProperty("codEmpresa", FuncionesUtiles.usuario[COD_EMPRESA])
        request.addProperty("codEmpresa", codEmpresa)
        request.addProperty("tipComprobante", "PRO")
        request.addProperty("serComprobante", codVendedor)
        request.addProperty("nroComprobante", nroComprobante)
        request.addProperty("cabecera", cabecera)
        request.addProperty("detalle", detalle)
        val envelope = SoapSerializationEnvelope(
            SoapEnvelope.VER11
        )
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL, 240000)
        val res: String
        try {
            transporte.call(SOAP_ACTION, envelope)
            val resultadoXml = envelope.response as SoapPrimitive
            res = resultadoXml.toString()
            resultados = res
        } catch (e: java.lang.Exception) {
            resultados = e.message.toString()
        }
        return resultados
    }

    fun enviarBaja(codVendedor: String, codCliente: String, codSubcliente: String, cliente: String, fotoFachada: String): String {
        setMethodName("ProcesaBajaClienteFinal ")
        val request: SoapObject?
        val resultado: String?
        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
            request.addProperty("vcodVendedor", codVendedor)
            request.addProperty("vcodCliente", codCliente)
            request.addProperty("vcodSubcliente", codSubcliente)
            request.addProperty("vcliente", cliente)
            request.addProperty("vfoto_fachada", fotoFachada)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL, 120000)
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



    fun procesaEnviaInventarioVencimiento(codVendedor:String,detalle:String) : String{
        val nameSpace   = "http://edsystem/servidor"
        val url         = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        val methodName  = "ProcesaInventarioProm"
        val soapAction  = "http://edsystem/servidor/ProcesaInventarioProm"

        lateinit var solicitud : SoapObject
        lateinit var resultado : String

        try {
            solicitud = SoapObject(nameSpace,methodName)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
            solicitud.addProperty("codVendedor", codVendedor)
            solicitud.addProperty("detalle", detalle)
        } catch (e : Exception){
            return e.message.toString()
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(url,240000)
        try {
            transporte.call(soapAction, envelope)
            resultado = envelope.response.toString()
        } catch (e : Exception){
            return e.message.toString()
        }
        return resultado
    }

}