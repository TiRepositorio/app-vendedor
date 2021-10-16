package apolo.vendedores.com.utilidades

import android.content.Context
import android.database.Cursor
import android.view.View
import android.widget.*

class FuncionesConsultor {

    constructor(context:Context){
        this.context = context
        this.funcion = FuncionesUtiles(context)
        this.etDesde = EditText(context)
        this.etHasta = EditText(context)
        this.rbPendiente = RadioButton(context)
        this.rbEnviado = RadioButton(context)
        this.rbTodo = RadioButton(context)
        this.ibtBuscar = ImageButton(context)
        this.btModificar = Button(context)
        this.btConsultar = Button(context)
        this.btEliminar = Button(context)
    }

    constructor(context: Context,etDesde:EditText,etHasta:EditText,rbPendiente:RadioButton,rbEnviado:RadioButton,rbTodo:RadioButton,ibtBuscar:ImageButton){
        this.context = context
        this.funcion = FuncionesUtiles(context)
        this.etDesde = etDesde
        this.etHasta = etHasta
        this.rbPendiente = rbPendiente
        this.rbEnviado = rbEnviado
        this.rbTodo = rbTodo
        this.ibtBuscar = ibtBuscar
        this.btModificar = Button(context)
        this.btConsultar = Button(context)
        this.btEliminar = Button(context)
    }

    constructor(context: Context,etDesde:EditText,etHasta:EditText,rbPendiente:RadioButton,rbEnviado:RadioButton,rbTodo:RadioButton,ibtBuscar:ImageButton,btModificar:Button,btConsultar:Button,btEliminar:Button,rgFiltro:RadioGroup){
        this.context = context
        this.funcion = FuncionesUtiles(context)
        this.etDesde = etDesde
        this.etHasta = etHasta
        this.rbPendiente = rbPendiente
        this.rbEnviado = rbEnviado
        this.rbTodo = rbTodo
        this.ibtBuscar = ibtBuscar
        this.btModificar = btModificar
        this.btConsultar = btConsultar
        this.btEliminar = btEliminar
        this.rgFiltro = rgFiltro

        rgFiltro.check(rbTodo.id)
    }

    constructor(context: Context,etDesde:EditText,etHasta:EditText,rbPendiente:RadioButton,rbEnviado:RadioButton,rbTodo:RadioButton,ibtBuscar:ImageButton,btConsultar:Button,btEliminar:Button,rgFiltro:RadioGroup){
        this.context = context
        this.funcion = FuncionesUtiles(context)
        this.etDesde = etDesde
        this.etHasta = etHasta
        this.rbPendiente = rbPendiente
        this.rbEnviado = rbEnviado
        this.rbTodo = rbTodo
        this.ibtBuscar = ibtBuscar
        this.btModificar = btConsultar
        this.btConsultar = btConsultar
        this.btEliminar = btEliminar
        this.rgFiltro = rgFiltro

        rgFiltro.check(rbTodo.id)
    }

    constructor(context: Context,etDesde:EditText,etHasta:EditText,rbPendiente:RadioButton,rbEnviado:RadioButton,rbTodo:RadioButton,ibtBuscar:ImageButton,btModificar:Button,btEliminar:Button){
        this.context = context
        this.funcion = FuncionesUtiles(context)
        this.etDesde = etDesde
        this.etHasta = etHasta
        this.rbPendiente = rbPendiente
        this.rbEnviado = rbEnviado
        this.rbTodo = rbTodo
        this.ibtBuscar = ibtBuscar
        this.btModificar = btModificar
        this.btConsultar = Button(context)
        this.btEliminar = btEliminar

    }

    var context:Context
    var funcion: FuncionesUtiles
    private var etDesde:EditText
    private var etHasta:EditText
    private var rbPendiente:RadioButton
    private var rbEnviado:RadioButton
    private var rbTodo:RadioButton
    private var ibtBuscar:ImageButton
    private var btModificar:Button
    private var btConsultar:Button
    private var btEliminar:Button
    private lateinit var rgFiltro:RadioGroup
    var where:HashMap<String,String> = HashMap()



    fun setRadioButtonText(text1:String,text2:String,text3:String){
        where = HashMap()
        where[rbPendiente.id.toString()] = text1.split("-")[1]
        where[rbEnviado.id.toString()] = text2.split("-")[1]
        where[rbTodo.id.toString()] = text3.split("-")[1]
        if (text1 == "-"){ rbPendiente.visibility = View.GONE }
        if (text1 == "-"){ rbEnviado.visibility = View.GONE }
        if (text1 == "-"){ rbTodo.visibility = View.GONE }
        rbPendiente.text = text1.split("-")[0]
        rbEnviado.text = text2.split("-")[0]
        rbTodo.text = text3.split("-")[0]
    }

    fun buscarGen(tabla:String,where:String):Cursor{
        val sql : String = "SELECT * FROM " + tabla + " WHERE " + where + " AND " + this.where[rgFiltro.checkedRadioButtonId.toString()]
        return funcion.consultar(sql)
    }

    fun buscar(tabla:String,campoFecha:String):Cursor{
        val sql : String = "SELECT * FROM " + tabla + " " +
                " WHERE CAST(substr(" + campoFecha + ",7,10) || substr(" + campoFecha + ",4,2) || substr(" + campoFecha + ",0,3) AS DATE) BETWEEN CAST(substr('" + etDesde.text + "',7,10) || substr('" + etDesde.text + "',4,2) || substr('" + etDesde.text + "',0,3) AS DATE) AND CAST(substr('" + etHasta.text + "',7,10) || substr('" + etHasta.text + "',4,2) || substr('" + etHasta.text + "',0,3) AS DATE)" +
                "   AND " + this.where[rgFiltro.checkedRadioButtonId.toString()]
        return funcion.consultar(sql)
    }

    fun buscar(tabla:String,campoFecha:String,campo:String,valor:String):Cursor{
        val sql : String = "SELECT * FROM " + tabla + " " +
                " WHERE CAST(substr(" + campoFecha + ",7,10) || substr(" + campoFecha + ",4,2) || substr(" + campoFecha + ",0,3) AS DATE) BETWEEN CAST(substr('" + etDesde.text + "',7,10) || substr('" + etDesde.text + "',4,2) || substr('" + etDesde.text + "',0,3) AS DATE) AND CAST(substr('" + etHasta.text + "',7,10) || substr('" + etHasta.text + "',4,2) || substr('" + etHasta.text + "',0,3) AS DATE)" +
                "   AND " + this.where[rgFiltro.checkedRadioButtonId.toString()] + " AND $campo = '$valor'"
        return funcion.consultar(sql)
    }

    fun buscar(tabla:String,campoFecha:String,campos:String):Cursor{
        val sql : String = "SELECT " + campos + " FROM " + tabla + " " +
                " WHERE CAST(" + campoFecha + " AS DATE) BETWEEN CAST('" + etDesde.text + "' AS DATE) AND CAST('" + etHasta.text + "' AS DATE)" +
                "   AND " + this.where[rgFiltro.checkedRadioButtonId.toString()] +
                " GROUP BY strftime('%W',date( substr(FECHA,7) || '-' || substr(FECHA,4,2) || '-' || substr(FECHA,1,2)  )), ESTADO "
        return funcion.consultar(sql)
    }

}