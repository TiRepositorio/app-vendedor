package apolo.vendedores.com.utilidades

import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class Graficos(grafico: BarChart, private var datosX: Array<String>, private var datosY: IntArray) {

    private var graficoDeBarra : BarChart = grafico
    private var colores = MutableList(10){0}

    fun getGraficoDeBarra(descripcion:String,colorTexto:Int,dimension:Float,colorFondo:Int, animacion:Int){
        cargarColores()
        graficoDeBarra.description.text = descripcion
        graficoDeBarra.description.textSize = dimension
        graficoDeBarra.animateY(animacion)
        graficoDeBarra.setBackgroundColor(colorFondo)
        graficoDeBarra.description.textColor = colorTexto
        graficoDeBarra.setDrawBarShadow(false)
        graficoDeBarra.setDrawGridBackground(false)
        graficoDeBarra.setGridBackgroundColor(Color.WHITE)
        graficoDeBarra.data = barData()
        subTitulo(graficoDeBarra)
    }

    private fun cargarColores(){
        colores.add(0,Color.parseColor("#838383"))
        colores.add(1,Color.parseColor("#517E1F"))
        colores.add(2,Color.parseColor("#3F51B5"))
        colores.add(3,Color.parseColor("#4CAF50"))
        colores.add(4,Color.parseColor("#70B5FF"))
        colores.add(5,Color.parseColor("#41FFA6"))
        colores.add(6,Color.parseColor("#C27DFF"))
        colores.add(7,Color.parseColor("#B65500"))
        colores.add(8,Color.parseColor("#1F8D9C"))
        colores.add(9,Color.parseColor("#1F2265"))
//        colores.add(0,R.color.g0)
//        colores.add(1,R.color.g1)
//        colores.add(2,R.color.g2)
//        colores.add(3,R.color.g3)
//        colores.add(4,R.color.g4)
//        colores.add(5,R.color.g5)
//        colores.add(6,R.color.g6)
//        colores.add(7,R.color.g7)
//        colores.add(8,R.color.g8)
//        colores.add(9,R.color.g9)
    }

    private fun subTitulo(graficoDeBarra:BarChart){
        val subtitulo : Legend = graficoDeBarra.legend
        subtitulo.form = Legend.LegendForm.CIRCLE
        subtitulo.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        val datos : ArrayList<LegendEntry> = ArrayList()
        for (i in datosX.indices){
            val entrada = LegendEntry()
            entrada.formColor = colores[i]
            entrada.label = datosX[i]
            datos.add(entrada)
        }
        subtitulo.setCustom(datos)
        ejeX(graficoDeBarra.xAxis)
        ejeY(graficoDeBarra.axisLeft)
        ejeDerecho(graficoDeBarra.axisRight)
        subtitulo.isEnabled = true
    }

    private fun datosGraficoDeBarra():ArrayList<BarEntry>{
        val entrada : ArrayList<BarEntry> = ArrayList()
        for (i in datosY.indices){
            entrada.add(BarEntry(i.toFloat(),datosY[i].toFloat()))
        }
        return entrada
    }

    private fun ejeX(ejeX:XAxis){
        ejeX.isGranularityEnabled = true
        ejeX.position = XAxis.XAxisPosition.TOP
        ejeX.valueFormatter = IndexAxisValueFormatter(datosX)
    }

    private fun ejeY(ejeY : YAxis){
        ejeY.spaceTop = 30f
        ejeY.axisMinimum = 0f
    }

    private fun ejeDerecho(ejeY : YAxis){
        ejeY.isEnabled = false
    }

    private fun barData():BarData{
        val barDataSet = BarDataSet(datosGraficoDeBarra(),"")
        barDataSet.colors = colores
        barDataSet.barShadowColor = Color.WHITE
        val barData = BarData(barDataSet)
        barData.barWidth = 0.45f
        return barData
    }

    fun dataSet(dataSet: DataSet<Entry>):DataSet<Entry>{
        dataSet.colors = colores
        dataSet.valueTextSize = 10f
        return dataSet
    }

}