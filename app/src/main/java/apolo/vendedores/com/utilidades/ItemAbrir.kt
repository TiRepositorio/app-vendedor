package apolo.vendedores.com.utilidades

import android.content.Intent

class ItemAbrir(private var valor: HashMap<String, String>, private var intent: Intent) {

    fun getValor():HashMap<String,String> {
        return this.valor
    }

    fun getIntent():Intent {
        return this.intent
    }

}