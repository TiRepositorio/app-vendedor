package apolo.vendedores.com.utilidades

import kotlin.random.Random

class Clave {

    public fun generaClave():String {
        var clave = Random.nextInt(10000000, 99999999)
        return  clave.toString()
    }

    public fun contraClave(clave: String) : String {

        val iP1 = Integer.parseInt(clave.substring(1, 8))
        val iP2 = Integer.parseInt(clave.substring(4, 8))
        val iP3 = Integer.parseInt(clave.substring(0, 8))
        val iP4 = Integer.parseInt(clave.substring(2, 8))
        val iP5 = Integer.parseInt(clave.substring(0, 5))
        val iP6 = Integer.parseInt(clave.substring(3, 8))

        var sContraClave : String = smv_roundabs(Math.cos(iP3.toDouble()), 100).toString()

            sContraClave = sContraClave + smv_roundabs(Math.cos(iP1.toDouble()), 100)
            sContraClave = sContraClave + smv_roundabs(Math.cos(iP4.toDouble()), 10)
            sContraClave = sContraClave + smv_roundabs(Math.tan(iP3.toDouble()), 10)
            sContraClave = sContraClave + smv_roundabs(Math.cos(iP6.toDouble()), 100)
            sContraClave = sContraClave + smv_roundabs(Math.cos(iP2.toDouble()), 10)
            sContraClave = sContraClave + smv_roundabs(Math.cos(iP5.toDouble()), 10)
            sContraClave = sContraClave.substring(0, 8)

        return sContraClave

    }

    public fun smv_roundabs(subClave: Double, multiplo: Int) : Long{
        return Math.abs(Math.round(subClave * multiplo))
    }

    fun smv_contraclave_checkList(
        sChave: String, cod_cliente: String,
        cod_subcliente: String, nro_planilla: String
    ): String {

        //		sChave = "54357440";

        val iP1 = Integer.parseInt(sChave.substring(1, 8))
        val iP2 = Integer.parseInt(sChave.substring(4, 8))
        val iP3 = Integer.parseInt(sChave.substring(0, 8))
        val iP4 = Integer.parseInt(sChave.substring(2, 8))
        val iP5 = Integer.parseInt(sChave.substring(0, 5))
        val iP6 = Integer.parseInt(sChave.substring(3, 8))

        var val1: Int
        var val2: Int
        var val3: Int

        try {
            val1 = Integer.parseInt(cod_cliente)
        } catch (e: Exception) {
            val1 = 0
        }

        try {
            val2 = Integer.parseInt(cod_subcliente)
        } catch (e: Exception) {
            val2 = 0
        }

        try {
            val3 = Integer.parseInt(nro_planilla)
        } catch (e: Exception) {
            val3 = 0
        }

        var sum = 0
        val vCliente = val1 + val2 + val3
        val largo = Integer.toString(vCliente).length

        for (i in 0 until largo) {
            var `val` = Integer.toString(vCliente)
            `val` = Character.toString(`val`[i])
            try {
                sum = sum + Integer.parseInt(`val`)
            } catch (e: Exception) {

            }

        }

        sum = Integer
            .parseInt(smv_roundabs(Math.cos(sum.toDouble()), 100).toString())
        val resCliente = retorna_valor(sum.toString(), 2)

        // vcliente := substr(abs ( round ( cos(substr(vvalor,1,2 ) ) * 100 , 0
        // ) ), 1, 2 ) ;

        var sContraChave: String
        val dosNumeros = sChave.substring(0, 2)
        if (dosNumeros != resCliente) {
            sContraChave = smv_roundabs(
                Math.cos(iP3.toDouble()),
                Integer.parseInt(resCliente)
            ).toString()
        } else {
            sContraChave = smv_roundabs(Math.cos(iP3.toDouble()), 100).toString()
        }

        sContraChave = sContraChave + smv_roundabs(Math.cos(iP6.toDouble()), 100).toString()//p1
        sContraChave = sContraChave + smv_roundabs(Math.cos(iP2.toDouble()), 10).toString() //p4
        sContraChave = sContraChave + smv_roundabs(Math.tan(iP5.toDouble()), 10).toString() //p3
        sContraChave = sContraChave + smv_roundabs(Math.cos(iP1.toDouble()), 100).toString()//p6
        sContraChave = sContraChave + smv_roundabs(Math.cos(iP4.toDouble()), 10).toString() //p2
        sContraChave = sContraChave + smv_roundabs(Math.cos(iP3.toDouble()), 10).toString() //p5
        sContraChave = sContraChave.substring(0, 8)

        return sContraChave
    }

    fun retorna_valor(valor: String, cantidad: Int): String {
        var result = ""

        val vvalor = cantidad - valor.length

        if (vvalor == 0) {
            return valor
        }

        for (i in 0 until vvalor) {
            result += "0"
        }

        result += valor

        if (result.length > cantidad) {
            result = result.substring(0, cantidad)
        }

        return result
    }

}