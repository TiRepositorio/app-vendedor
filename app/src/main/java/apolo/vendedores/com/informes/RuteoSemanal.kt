package apolo.vendedores.com.informes

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import apolo.vendedores.com.utilidades.SentenciasSQL
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_canasta_de_marcas2.*
import kotlinx.android.synthetic.main.activity_ruteo_semanal.*
import kotlinx.android.synthetic.main.activity_ruteo_semanal.barraMenu
import kotlinx.android.synthetic.main.activity_ruteo_semanal.contMenu
import kotlinx.android.synthetic.main.barra_ger_sup.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class RuteoSemanal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var funcion : FuncionesUtiles
    private var codVendedor = ""
    private var desVendedor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruteo_semanal)
        inicializarElementos()
    }

    fun inicializarElementos(){
        llBotonesCab.visibility = View.GONE
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        funcion.cargarTitulo(R.drawable.ic_mapa,"Ruteo semanal")
        funcion.ejecutar(SentenciasSQL.venVistaCabecera("svm_ruteo_semanal_cliente_vend"),this)
        funcion.listaVendedores("COD_VENDEDOR", "DESC_VENDEDOR", "ven_svm_ruteo_semanal_cliente_vend")
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        validacion()
        cargarDatos()
    }

    private fun validacion(){
        if (tvVendedor!!.text.toString() == "Nombre del vendedor"){
            funcion.toast(this, "No hay datos para mostrar.")
            finish()
        }
    }

    private fun cargarCodigos(){
        try {
            codVendedor = tvVendedor.text!!.toString().split("-")[0]
            desVendedor = tvVendedor.text!!.toString().split("-")[1]
        } catch (e: java.lang.Exception){tvVendedor.text = "Nombre del vendedor"}
        if (tvVendedor.text.toString().split("-").size>2){
            desVendedor = tvVendedor.text!!.toString()
            while (desVendedor.indexOf("-") != 0){
                desVendedor = desVendedor.substring(1, desVendedor.length)
            }
            desVendedor = desVendedor.substring(1, desVendedor.length)
        }
//        funcion.mensaje(this,codVendedor,desVendedor)
    }

    private fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarDatos()
            mostrar()
        }
    }

    private fun cargarDatos(){
        cargarCodigos()
        val sql = ("SELECT COD_CLIENTE || ' - ' || DESC_CLIENTE AS DESC_CLIENTE "
                       + "      , DIA            "
                       + "      , FEC_VISITA AS FECHA     "
                       + "   FROM svm_ruteo_semanal_cliente_vend "
                       + "  where COD_VENDEDOR = '$codVendedor' "
                       + "  ORDER BY DATE(substr(FECHA,7,4)||'-'||substr(FECHA,4,2) || '-' || substr(FECHA,1,2))")
        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,funcion.consultar(sql))
        mostrar()
    }

    fun mostrar(){
        FuncionesUtiles.posicionCabecera = 0
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3)
        funcion.valores = arrayOf("DESC_CLIENTE","DIA","FECHA")
        lvRuteoSemanal.adapter = Adapter.AdapterGenericoCabecera(this
                                                                ,FuncionesUtiles.listaCabecera
                                                                ,R.layout.inf_rut_sem_lista_ruteo_semanal
                                                                ,funcion.vistas
                                                                ,funcion.valores)
        lvRuteoSemanal.setOnItemClickListener { _, _, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            lvRuteoSemanal.invalidateViews()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        FuncionesUtiles.posicionCabecera = 0
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Gerentes"){
            tvGerente.text = menuItem.title.toString()
            funcion.listaSupervisores("COD_SUPERVISOR",
                "DESC_SUPERVISOR",
                "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_deuda_cliente " +
                        "WHERE COD_GERENTE = '${tvGerente.text.split("-")[0]}'","COD_SUPERVISOR")
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Supervisores"){
            tvSupervisor.text = menuItem.title.toString()
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Vendedores"
            && menuItem.title.toString().split("-")[0].trim().length>3){
            tvVendedor.text = menuItem.title.toString()
        }
        cargarDatos()
        mostrar()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }


}