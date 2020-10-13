package apolo.vendedores.com.utilidades

import android.content.Intent

class ItemAbrir {

    constructor(valor: HashMap<String,String>, intent: Intent){
        this.valor = valor
        this.intent= intent
    }

    private var valor : HashMap<String,String>
    private var intent: Intent

    fun getValor():HashMap<String,String> {
        return this.valor
    }

    fun getIntent():Intent {
        return this.intent
    }

    fun setValor(valor: HashMap<String, String>){
        this.valor = valor
    }

    fun setIntent(intent: Intent){
        this.intent = intent
    }

}