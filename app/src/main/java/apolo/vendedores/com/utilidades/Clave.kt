package apolo.vendedores.com.utilidades

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.tan
import kotlin.random.Random

class Clave {

    fun generaClave():String {
        val clave = Random.nextInt(10000000, 99999999)
        return  clave.toString()
    }

    fun contraClave(clave: String) : String {

        val iP1 = Integer.parseInt(clave.substring(1, 8))
        val iP2 = Integer.parseInt(clave.substring(4, 8))
        val iP3 = Integer.parseInt(clave.substring(0, 8))
        val iP4 = Integer.parseInt(clave.substring(2, 8))
        val iP5 = Integer.parseInt(clave.substring(0, 5))
        val iP6 = Integer.parseInt(clave.substring(3, 8))

        var sContraClave : String = smvRoundabs(cos(iP3.toDouble()), 100).toString()

        sContraClave += smvRoundabs(cos(iP1.toDouble()), 100)
        sContraClave += smvRoundabs(cos(iP4.toDouble()), 10)
        sContraClave += smvRoundabs(tan(iP3.toDouble()), 10)
        sContraClave += smvRoundabs(cos(iP6.toDouble()), 100)
        sContraClave += smvRoundabs(cos(iP2.toDouble()), 10)
        sContraClave += smvRoundabs(cos(iP5.toDouble()), 10)
        sContraClave = sContraClave.substring(0, 8)

        return sContraClave

    }

    private fun smvRoundabs(subClave: Double, multiplo: Int) : Long{
        return abs((subClave * multiplo).roundToInt().toLong())
    }

}