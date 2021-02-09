package apolo.vendedores.com.configurar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.R
import apolo.vendedores.com.utilidades.Clave
import kotlinx.android.synthetic.main.activity_calcular_clave_prueba.*

class CalcularClavePrueba : AppCompatActivity() {

    companion object{
        var informe:Class<*>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val contraClave = Clave()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_clave_prueba)
        val clave = contraClave.generaClave()
//        tvClaveTemporal.text = "54127854"
        tvClaveTemporal.text = clave
        tvClave.visibility = View.GONE
    }

    fun calcular(view: View) {
        val contraClave = Clave()
        if (contraClave.contraClave(tvClaveTemporal.text.toString()) == etClave.text.toString()
            && etClave.text.toString().length==8){
            if (informe == null){
                return
            } else {
                val menu2 = Intent(this, informe)
                startActivity(menu2)
                finish()
            }
        }else{
            Toast.makeText(this, "Clave Incorrecta", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
