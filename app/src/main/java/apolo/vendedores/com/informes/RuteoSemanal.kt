package apolo.vendedores.com.informes

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Adapter
import apolo.vendedores.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_ruteo_semanal.*
import kotlinx.android.synthetic.main.barra_ger_sup.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class RuteoSemanal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var funcion : FuncionesUtiles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruteo_semanal)
        inicializarElementos()
    }

    fun inicializarElementos(){
        llBotonesCab.visibility = View.GONE
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBotonVendedores)
        funcion.cargarTitulo(R.drawable.ic_mapa,"Ruteo semanal")
        cargarDatos()
    }

    private fun cargarDatos(){
        val sql = ("SELECT COD_CLIENTE || ' - ' || DESC_CLIENTE AS DESC_CLIENTE "
                       + "      , DIA            "
                       + "      , FEC_VISITA AS FECHA     "
                       + "   FROM svm_ruteo_semanal_cliente_vend "
                       + "  ORDER BY DATE(substr(FECHA,7,4)||'-'||substr(FECHA,4,2) || '-' || substr(FECHA,1,2))")
        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,funcion.consultar(sql))
        mostrar()
    }

    fun mostrar(){
        FuncionesUtiles.posicionCabecera = 0
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv1,R.id.tv3)
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