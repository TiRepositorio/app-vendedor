package apolo.vendedores.com.configurar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import apolo.vendedores.com.MainActivity2
import apolo.vendedores.com.R
import apolo.vendedores.com.clases.Usuario
import kotlinx.android.synthetic.main.activity_configurar_usuario_individual.*

class ConfigurarUsuarioIndividual : AppCompatActivity() {


    var usuarioActual : Usuario = Usuario()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar_usuario_individual)

        usuarioActual = MainActivity2.listaUsuarios[MainActivity2.posicionEditaUsuario]

        inicializarBotones()
        cargarUsuario()


    }


    private fun inicializarBotones() {

        ibtnGuardar.setOnClickListener {

            val usuario = Usuario()
            usuario.id = 0
            usuario.cod_empresa = etCodEmpresaIndividual.text.toString().trim()
            usuario.login = etUsuCodigoIndividual.text.toString().trim()
            usuario.nombre = etUsuNombreIndividual.text.toString().trim()
            usuario.version = etUsuVersionIndividual.text.toString().trim()

            MainActivity2.listaUsuarios[MainActivity2.posicionEditaUsuario] = usuario

            finish()

        }

        ibtnCancelar.setOnClickListener {

            finish()

        }

    }


    private fun cargarUsuario() {

        etCodEmpresaIndividual.setText(usuarioActual.cod_empresa)
        etUsuCodigoIndividual.setText(usuarioActual.login)
        etUsuNombreIndividual.setText(usuarioActual.nombre)
        etUsuVersionIndividual.setText(usuarioActual.version)

    }




    override fun onBackPressed() {
        super.onBackPressed()
    }




}