package apolo.vendedores.com.utilidades

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.ventas.catastro.CatastrarCliente
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mapa.*

class MapaCatastro : AppCompatActivity(), OnMapReadyCallback {

    companion object{
        var modificarCliente : Boolean = false
        var codCliente : String = ""
        var codSubcliente :String = ""
        var codVendedor : String = ""
        var lista : ArrayList<HashMap<String,String>> = ArrayList()
    }

    private lateinit var mMap: GoogleMap
    private lateinit var positionCliente : LatLng
    private lateinit var ubicacion : FuncionesUbicacion
    private lateinit var lm: LocationManager
    var funcion  : FuncionesUtiles = FuncionesUtiles(this)
    private var latitud  : String = ""
    private var longitud : String = ""
    var id : String = ""
    var tipo : String  = ""
    var cliente : String = ""
    var resultado : String = ""
//    var respuesta : String = ""
//    var foto      : String = ""
//    var conexion : ConexionWS = ConexionWS()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val googleMap : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        googleMap.getMapAsync(this)

        inicializaElementos()
    }

    private fun inicializaElementos(){
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ubicacion = FuncionesUbicacion(this)
        ubicacion.obtenerUbicacionLatLng(lm)
        inicializaBoton(btnObtenerUbicacion)
        if (!modificarCliente){
            btnObtenerUbicacion.visibility = View.GONE
        }
    }

    private fun validaUbicacion(yo:LatLng, cliente:LatLng):Boolean{
        val rangoDistanciaCliente = 100.0
        val distanciaCliente : Double = ubicacion.calculaDistanciaCoordenadas(yo.latitude,cliente.latitude,yo.longitude,cliente.longitude)
        if (distanciaCliente > rangoDistanciaCliente){
            Toast.makeText(this,
                "No puede marcar a mas de $rangoDistanciaCliente metros de su ubicacion.", Toast.LENGTH_SHORT).show()
            return false
        } else {
            Toast.makeText(this,"Se encuentra a " + funcion.decimal(distanciaCliente.toString()) + " metros de su ubicacion.", Toast.LENGTH_SHORT).show()
        }
        return true
    }

//    private fun cargarUbicacion(){
//        if (modificarCliente){
//
//        }
//    }

//    private fun sqlCliente() : String {
//        return (" SELECT TELEFONO, TELEFONO2, DIRECCION, CERCA_DE "
//                + " FROM  svm_cliente_vendedor "
//                + " WHERE COD_CLIENTE    = '" + codCliente      + "'"
//                + " AND   COD_SUBCLIENTE = '" + codSubcliente   + "'")
//    }

    fun valores(cursor: Cursor):ContentValues{
        if (cursor.count>0){
            id = funcion.dato(cursor,"id")
            tipo = (if (funcion.dato(cursor,"TIPO").trim().isNotEmpty()){
                        funcion.dato(cursor,"TIPO")
                    } else {
                        tipo
                    }).toString()
            if (tipo == "D"){
                tipo = "A"
            }
        }

        val valores = ContentValues()
        for (i in 0 until cursor.columnCount){
            valores.put(cursor.getColumnName(i),funcion.dato(cursor,cursor.getColumnName(i)))
        }
        valores.put("COD_EMPRESA", FuncionesUtiles.usuario["COD_EMPRESA"].toString())
        valores.put("COD_CLIENTE", codCliente)
        valores.put("COD_SUBCLIENTE", codSubcliente)
        valores.put("LATITUD",latitud)
        valores.put("LONGITUD",longitud)
        valores.put("FECHA",funcion.getFechaActual())
        valores.put("ESTADO","P")
        valores.put("TIPO",tipo)
        if (funcion.dato(cursor,"TELEFONO").trim().isNotEmpty()){
            valores.put("TELEFONO1",funcion.dato(cursor,"TELEFONO"))
            valores.remove("TELEFONO")
        }
        return valores
    }

    private fun inicializaBoton(boton:Button){
        boton.setOnClickListener{
            if (validaUbicacion(ubicacion.obtenerUbicacionLatLng(lm), positionCliente)){
                CatastrarCliente.tvmLatitud.text  = positionCliente.latitude.toString()
                CatastrarCliente.tvmLongitud.text = positionCliente.longitude.toString()
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.isIndoorEnabled = true
        mMap.addMarker(MarkerOptions().position(ubicacion.obtenerUbicacionLatLng(lm)).title("Posicion actual"))
        positionCliente = ubicacion.obtenerUbicacionLatLng(lm)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positionCliente,mMap.maxZoomLevel - 5))

        if (modificarCliente){
            googleMap.setOnMapClickListener { point ->
                //lstLatLngs.add(point);
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(point).title("Este es el cliente"))
                positionCliente = point
            }
            googleMap.clear()
            ubicacion.obtenerUbicacionLatLng(lm)
            ubicacion.obtenerUbicacionLatLng(lm)
            val posicion = ubicacion.obtenerUbicacionLatLng(lm)
            mMap.addMarker(MarkerOptions().position(posicion).title("CLIENTE: NUEVO CLIENTE")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion,mMap.maxZoomLevel - 5))

        }

        mMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(this@MapaCatastro)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(this@MapaCatastro)
                title.setTextColor(Color.BLACK)
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(this@MapaCatastro)
                snippet.setTextColor(Color.BLACK)
                snippet.text = marker.snippet
                snippet.setTypeface(null, Typeface.BOLD)
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
    }
}
