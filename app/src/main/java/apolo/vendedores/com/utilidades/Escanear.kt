package apolo.vendedores.com.utilidades

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class Escanear : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private var escanerZXing: ZXingScannerView? = null
    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        escanerZXing = ZXingScannerView(this)
        // Hacer que el contenido de la actividad sea el escaner
        setContentView(escanerZXing)
    }

    public override fun onResume() {
        super.onResume()
        // El "manejador" del resultado es esta misma clase, por eso implementamos ZXingScannerView.ResultHandler
        escanerZXing!!.setResultHandler(this)
        escanerZXing!!.startCamera() // Comenzar la cámara en onResume
    }

    public override fun onPause() {
        super.onPause()
        escanerZXing!!.stopCamera() // Pausar en onPause
    }

    // Estamos sobrescribiendo un método de la interfaz ZXingScannerView.ResultHandler
    override fun handleResult(resultado: Result) {

        // Si quieres que se siga escaneando después de haber leído el código, descomenta lo siguiente:
        // Si la descomentas no recomiendo que llames a finish
//        escanerZXing.resumeCameraPreview(this);
        // Obener código/texto leído
        val codigo = resultado.text
        // Preparar un Intent para regresar datos a la actividad que nos llamó
        val intentRegreso = Intent()
        intentRegreso.putExtra("codigo", codigo)
        setResult(Activity.RESULT_OK, intentRegreso)
        // Cerrar la actividad. Ahora mira onActivityResult de MainActivity
        finish()
    }
}
