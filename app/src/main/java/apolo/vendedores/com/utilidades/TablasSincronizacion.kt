package apolo.vendedores.com.utilidades

import java.util.*

class TablasSincronizacion {

//    var tablasReportes : TablasReportes = TablasReportes()
//    var tablasVisitas  : TablasVisitas  = TablasVisitas()
//    var tablasInformes : TablasInformes = TablasInformes()

    fun listaSQLCreateTables(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0 , SentenciasSQL.createTableSvmArticulosPrecios())
        lista.add(1 , SentenciasSQL.createTableClienteListPrec())
        lista.add(2 , SentenciasSQL.createTableSvmClienteVendedor())
        lista.add(3 , SentenciasSQL.createTableSvmCondicionVenta())
        lista.add(4 , SentenciasSQL.createTableSvmStArticulos())
        lista.add(5 ,SentenciasSQL.createTableSvmUltimaVentaCliente())
        lista.add(6 ,SentenciasSQL.createTableSvmVendedorPedido())
        lista.add(7 ,SentenciasSQL.createTableSvmDeudaCliente())
        lista.add(8 ,SentenciasSQL.createTableSvmPromArticulosTarCred())
        lista.add(9 ,SentenciasSQL.createTableSvmMetasPuntoPorLinea())
        lista.add(10,SentenciasSQL.createTableSvmMetasPuntoPorCliente())
        lista.add(11,SentenciasSQL.createTableSvmPedidosSinStockRep())
        lista.add(12,SentenciasSQL.createTableSvmNegociacionVenta())
        lista.add(13,SentenciasSQL.createTableSvmPedidosEnReparto())
        lista.add(14,SentenciasSQL.createTableSvmRebotesPorCliente())
        lista.add(15,SentenciasSQL.createTableSvmCiudades())
        lista.add(16,SentenciasSQL.createTableSvmTablaVisitas())
        lista.add(17,SentenciasSQL.createTableSvmTipoCliente())
        lista.add(18,SentenciasSQL.createTableSvmCondicionVentaCliente())
        lista.add(19,SentenciasSQL.createTableSvmPreciosFijos())
        lista.add(20,SentenciasSQL.createTableSvmLiqComisionVendedor())
        lista.add(21,SentenciasSQL.createTableSvmModuloComisionVend())
        lista.add(22,SentenciasSQL.createTableSvmDiasVisitaCliente())
        lista.add(23,SentenciasSQL.createTableSvmCondVentaVendedor())
        lista.add(24,SentenciasSQL.createTableSvmEvolDiariaVenta())
        lista.add(25,SentenciasSQL.createTableVtvPreciosFijos())
        lista.add(26,SentenciasSQL.createTableStvCategoriaPalm())
        lista.add(27,SentenciasSQL.createTableSpmRetornoComentario())
        lista.add(28,SentenciasSQL.createTableSpmMotivoNoVenta())
        lista.add(29,SentenciasSQL.createTableSvmPromocionesPorPerfiles())
        lista.add(30,SentenciasSQL.createTableSvmListadoPedido())
        lista.add(31,SentenciasSQL.createTableSvmDiasTomaFotoCliente())
        lista.add(32,SentenciasSQL.createTableSvmSegVisitasSemanal())
        lista.add(33,SentenciasSQL.createTableCcvMotivoSolTarjCred())
        lista.add(34,SentenciasSQL.createTableSvmPromocionesArtCab())
        lista.add(35,SentenciasSQL.createTableSvmPromocionesArtDet())
        lista.add(36,SentenciasSQL.createTableSvmMensajeConclusion())
        lista.add(37,SentenciasSQL.createTableSvmSurtidoEficiente())
        lista.add(38,SentenciasSQL.createTableSvmLiqPremiosVend())
        lista.add(39,SentenciasSQL.createTableSvmProduccionSemanalVend())
        lista.add(40,SentenciasSQL.createTableSvmRuteoSemanalClienteVend())
        lista.add(41,SentenciasSQL.createTableSvmCoberturaVisVendedores())
        lista.add(42,SentenciasSQL.createTableRhvLiquidacionFuerzaVenta())
        lista.add(43,SentenciasSQL.createTableSvmVtMotivosSdDev())
        lista.add(44,SentenciasSQL.createTableFvvLiqCuotaXUndNegVend())
        lista.add(45,SentenciasSQL.createTableSvmCoberturaMensualVend())
        lista.add(46,SentenciasSQL.createTableFvvCobSemanalVend())
//        lista.add(47,SentenciasSQL.)
//        lista.add(48,SentenciasSQL.)
//        lista.add(49,SentenciasSQL.)

        return lista
    }

    fun listaCamposSincronizacion(): Vector<Vector<String>> {
        val lista : Vector<Vector<String>> = Vector()
        lista.add(0 ,camposTablaSvmArticulosPrecios())
        lista.add(1 ,camposTablaSvmClienteListPrec())
        lista.add(2 ,camposTablaSvmClienteVendedor())
        lista.add(3 ,camposTablaSvmCondicionVenta())
        lista.add(4 ,camposTablaSvmStArticulos())
        lista.add(5 ,camposTablaSvmUltimaVentaCliente())
        lista.add(6 ,camposTablaSvmVendedorPedido())
        lista.add(7 ,camposTablaSvmDeudaCliente())
        lista.add(8 ,camposTablaSvmPromArticulosTarCred())
        lista.add(9 ,camposTablaSvmMetasPuntoPorLinea())
        lista.add(10,camposTablaSvmMetasPuntoPorCliente())
        lista.add(11,camposTablaSvmPedidosSinStockRep())
        lista.add(12,camposTablaSvmNegociacionVenta())
        lista.add(13,camposTablaSvmPedidosEnReparto())
        lista.add(14,camposTablaSvmRebotesPorCliente())
        lista.add(15,camposTablaSvmCiudades())
        lista.add(16,camposTablaSvmTablaVisitas())
        lista.add(17,camposTablaSvmTipoCliente())
        lista.add(18,camposTablaSvmCondicionVentaCliente())
        lista.add(19,camposTablaSvmPreciosFijos())
        lista.add(20,camposTablaSvmLiqComisionVendedor())
        lista.add(21,camposTablaSvmModuloComisionVend())
        lista.add(22,camposTablaSvmDiasVisitaCliente())
        lista.add(23,camposTablaSvmCondVentaVendedor())
        lista.add(24,camposTablaSvmEvolDiariaVenta())
        lista.add(25,camposTablaSvmVtvPreciosFijosMod())
        lista.add(26,camposTablaStvCategoriaPalm())
        lista.add(27,camposTablaSvmRetornoComentario())
        lista.add(28,camposTablaSpmMotivoNoVenta())
        lista.add(29,camposTablaSvmPromocionesPorPerfiles())
        lista.add(30,camposTablaSvmListadoPedidos())
        lista.add(31,camposTablaSvmDiasTomaFotoCliente())
        lista.add(32,camposTablaSvmSegVisitasSemanal())
        lista.add(33,camposTablaCcvMotivoSolTarjCred())
        lista.add(34,camposTablaSvmPromocionesArtCab())
        lista.add(35,camposTablaSvmPromocionesArtDet())
        lista.add(36,camposTablaSvmMensajeConclusion())
        lista.add(37,camposTablaSvmSurtidoEficiente())
        lista.add(38,camposTablaSvmLiqPremiosVend())
        lista.add(39,camposTablaSvmProduccionSemanalVend())
        lista.add(40,camposTablaSvmRuteoSemanalClienteVend())
        lista.add(41,camposTablaSvmCoberturaVisVendedores())
        lista.add(42,camposTablaRhvLiquidacionFuerzaVenta())
        lista.add(43,camposTablaSvmVtMotivosSodDev())
        lista.add(44,camposTablaFvvLiqCuotaXUndNegVend())
        lista.add(45,camposTablaSvmCoberturaMensualVend())
        lista.add(46,camposTablaFvvCobSemanalVend())

        return lista
    }

    private fun camposTablaSvmArticulosPrecios(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_VENDEDOR")
        lista.add(2,"MODULO")
        lista.add(3,"COD_LISTA_PRECIO")
        lista.add(4,"COD_ARTICULO")
        lista.add(5,"DESC_ARTICULO")
        lista.add(6,"PREC_CAJA")
        lista.add(7,"PREC_UNID")
        lista.add(8,"COD_UNIDAD_MEDIDA")
        lista.add(9,"PORC_IVA")
        lista.add(10,"REFERENCIA")
        lista.add(11,"MULT")
        lista.add(12,"DIV")
        lista.add(13,"IND_LIM_VENTA")
        lista.add(14,"COD_LINEA")
        lista.add(15,"COD_FAMILIA")
        lista.add(16,"COD_BARRA")
        lista.add(17,"CANT_MINIMA")
        lista.add(18,"PORC_DESCUENTO")
        return lista
    }
    private fun camposTablaSvmClienteListPrec(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CLIENTE")
        lista.add(2,"COD_SUBCLIENTE")
        lista.add(3,"COD_VENDEDOR")
        lista.add(4,"COD_LISTA_PRECIO")
        lista.add(5,"DESC_LISTA")
        lista.add(6,"IND_DEFECTO")
        lista.add(7,"COD_MONEDA")
        lista.add(8,"DECIMALES")
        return lista
    }
    private fun camposTablaSvmClienteVendedor(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CLIENTE")
        lista.add(2,"COD_SUBCLIENTE")
        lista.add(3,"DESC_SUBCLIENTE")
        lista.add(4,"DESC_CLIENTE")
        lista.add(5,"COD_VENDEDOR")
        lista.add(6,"TELEFONO")
        lista.add(7,"RUC")
        lista.add(8,"TIP_CAUSAL")
        lista.add(9,"DESC_CIUDAD")
        lista.add(10,"DESC_ZONA")
        lista.add(11,"DESC_REGION")
        lista.add(12,"DIRECCION")
        lista.add(13,"TELEFONO")
        lista.add(14,"COD_SUCURSAL")
        lista.add(15,"DESC_TIPO")
        lista.add(16,"COD_CONDICION")
        lista.add(17,"DESC_CONDICION")
        lista.add(18,"LIMITE_CREDITO")
        lista.add(19,"TOT_DEUDA")
        lista.add(20,"SALDO")
        lista.add(21,"FEC_VISITA")
        lista.add(22,"IND_VISITA")
        lista.add(23,"COD_ZONA")
        lista.add(24,"TIPO_CONDICION")
        lista.add(25,"IND_DIRECTA")
        lista.add(26,"IND_ATRAZO")
        lista.add(27,"FRECUENCIA")
        lista.add(28,"COMENTARIO")
        lista.add(29,"IND_ESP")
        lista.add(30,"CATEGORIA")
        lista.add(31,"TELEFONO2")
        lista.add(32,"LATITUD")
        lista.add(33,"LONGITUD")
        lista.add(34,"CERCA_DE")
        lista.add(35,"IND_CADUCADO")
        lista.add(36,"TIP_CLIENTE")
        lista.add(37,"IND_VITRINA")
        lista.add(38,"FOTO_FACHADA")
        lista.add(39,"TIEMPO_MIN")
        lista.add(40,"TIEMPO_MAX")
        lista.add(41,"SOL_TARG")
        lista.add(42,"DIAS_INICIAL")
        return lista
    }
    private fun camposTablaSvmCondicionVenta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CONDICION_VENTA")
        lista.add(2,"DESCRIPCION")
        lista.add(3,"TIPO_CONDICION")
        lista.add(4,"ABREVIATURA")
        lista.add(5,"DIAS_INICIAL")
        lista.add(6,"MONTO_MIN_DESC")
        lista.add(7,"PORC_DESC")
        return lista
    }
    private fun camposTablaSvmStArticulos(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_ARTICULO")
        lista.add(2,"DESC_ARTICULO")
        lista.add(3,"COD_UNIDAD_REL")
        lista.add(4,"REFERENCIA")
        lista.add(5,"MULT")
        lista.add(6,"DIV")
        lista.add(7,"COD_IVA")
        lista.add(8,"PORC_IVA")
        lista.add(9,"COD_LINEA")
        lista.add(10,"COD_FAMILIA")
        lista.add(11,"CANT_MINIMA")
        lista.add(12,"CANT_EXIST")
        lista.add(13,"COD_BARRA")
        lista.add(14,"IND_BASICO")
        return lista
    }
    private fun camposTablaSvmUltimaVentaCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CLIENTE")
        lista.add(2,"COD_VENDEDOR")
        lista.add(3,"COD_SUBCLIENTE")
        lista.add(4,"FEC_COMPROBANTE")
        lista.add(5,"COD_ARTICULO")
        lista.add(6,"PRECIO_UNITARIO")
        lista.add(7,"CANTIDAD")
        lista.add(8,"DESC_UNIDAD")
        lista.add(9,"SIGLAS")
        lista.add(10,"DESC_ARTICULO")
        lista.add(11,"DECIMALES")
        return lista
    }
    private fun camposTablaSvmVendedorPedido(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_VENDEDOR")
        lista.add(2,"IND_PALM")
        lista.add(3,"TIPO")
        lista.add(4,"SERIE")
        lista.add(5,"NUMERO")
        lista.add(6,"FECHA")
        lista.add(7,"COD_CLI_VEND")
        lista.add(8,"COD_PERSONA")
        lista.add(9,"RANGO")
        lista.add(10,"MIN_FOTOS")
        lista.add(11,"MAX_FOTOS")
        lista.add(12,"PER_VENDER")
        lista.add(13,"PER_PRESENCIAL")
        lista.add(14,"HORA")
        lista.add(15,"VERSION_SISTEMA")
        lista.add(16,"PER_ASISTENCIA")
        lista.add(17,"FRECUENCIA_RASTREO")
        lista.add(18,"HORA_INICIO")
        lista.add(19,"HORA_FIN")
        lista.add(20,"TIEMPO_ASISTENCIA")
        lista.add(21,"IND_FOTO")
        lista.add(22,"MIN_VENTA")
        lista.add(23,"PER_MARC_ASIS")
        lista.add(24,"VENT_CON_MARC")
        lista.add(25,"VERSION")


//        lista.add(25,"")
//        lista.add(26,"")
//        lista.add(27,"")
//        lista.add(28,"ULTIMA_VEZ")
//        lista.add(29,"ultima_sincro")
        return lista
    }
    private fun camposTablaSvmDeudaCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CLIENTE")
        lista.add(2,"COD_SUBCLIENTE")
        lista.add(3,"FEC_EMISION")
        lista.add(4,"FEC_VENCIMIENTO")
        lista.add(5,"TIP_DOCUMENTO")
        lista.add(6,"NRO_DOCUMENTO")
        lista.add(7,"DIA_ATRAZO")
        lista.add(8,"ABREVIATURA")
        lista.add(9,"SALDO_CUOTA")
        return lista
    }
    private fun camposTablaSvmPromArticulosTarCred(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"NRO_PROMOCION")
        lista.add(2,"COD_CONDICION_VENTA")
        lista.add(3,"COD_ARTICULO")
        lista.add(4,"DESC_ARTICULO")
        lista.add(5,"PORC_DESCUENTO")
        lista.add(6,"DESCRIPCION")
        lista.add(7,"COMENTARIO")
        return lista
    }
    private fun camposTablaSvmMetasPuntoPorLinea(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"DESC_VENDEDOR")
        lista.add(2,"COD_CATEGORIA")
        lista.add(3,"DESC_CATEGORIA")
        lista.add(4,"DESC_GTE_MARKETIN")
        lista.add(5,"DESC_MODULO")
        lista.add(6,"COD_SUPERVISOR")
        lista.add(7,"DESC_SUPERVISOR")
        lista.add(8,"MAYOR_VENTA")
        lista.add(9,"VENTA_MES1")
        lista.add(10,"VENTA_MES2")
        lista.add(11,"META")
        lista.add(12,"MES_1")
        lista.add(13,"MES_2")
        lista.add(14,"NRO_ORD_MAG")
        lista.add(15,"ORD_GTE_MARK")
        lista.add(16,"ORD_CATEGORIA")
        return lista
    }
    private fun camposTablaSvmMetasPuntoPorCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"DESC_VENDEDOR")
        lista.add(2,"CODIGO")
        lista.add(3,"NOM_SUBCLIENTE")
        lista.add(4,"CIUDAD")
        lista.add(5,"COD_SUPERVISOR")
        lista.add(6,"DESC_SUPERVISOR")
        lista.add(7,"LISTA_PRECIO")
        lista.add(8,"MAYOR_VENTA")
        lista.add(9,"VENTA_3")
        lista.add(10,"MIX_3")
        lista.add(11,"VENTA_4")
        lista.add(12,"MIX_4")
        lista.add(13,"METAS")
        lista.add(14,"TOT_SURTIDO")
        lista.add(15,"MES_1")
        lista.add(16,"MES_2")
        return lista
    }
    private fun camposTablaSvmPedidosSinStockRep(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"ORIGEN")
        lista.add(2,"DESC_DEPOSITO")
        lista.add(3,"FEC_COMPROBANTE")
        lista.add(4,"COD_CLIENTE")
        lista.add(5,"CLIENTE")
        lista.add(6,"VENDEDOR")
        lista.add(7,"SUPERVISOR")
        lista.add(8,"COD_ARTICULO")
        lista.add(9,"ARTICULO")
        lista.add(10,"CANTIDAD")
        lista.add(11,"PRECIO_UNITARIO")
        lista.add(12,"MONTO_TOTAL")
        lista.add(13,"PEDIDO")
        lista.add(14,"DECIMALES")
        lista.add(15,"DESC_MOTIVO")
        return lista
    }
    private fun camposTablaSvmNegociacionVenta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_COMPROBANTE")
        lista.add(2,"COD_CLIENTE")
        lista.add(3,"DESC_SUBCLIENTE")
        lista.add(4,"COD_LISTA_PRECIO")
        lista.add(5,"COD_SUPERVISOR")
        lista.add(6,"NOM_SUPERVISOR")
        lista.add(7,"COD_ARTICULO")
        lista.add(8,"DESC_ARTICULO")
        lista.add(9,"REFERENCIA")
        lista.add(10,"CANTIDAD")
        lista.add(11,"PRECIO_LISTA")
        lista.add(12,"PRECIO_SOLIC")
        lista.add(13,"PORC_DESCUENTO")
        lista.add(14,"TOTAL_DESC_OTORG")
        lista.add(15,"DECIMALES")
        lista.add(16,"IND_AUT")
        lista.add(17,"OBSERVACION")
        return lista
    }
    private fun camposTablaSvmPedidosEnReparto(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"NRO_PLANILLA")
        lista.add(2,"DESC_REPARTIDOR")
        lista.add(3,"TEL_REPARTIDOR")
        lista.add(4,"FEC_PLANILLA")
        lista.add(5,"FEC_COMPROBANTE")
        lista.add(6,"TIP_COMPROBANTE")
        lista.add(7,"NRO_COMPROBANTE")
        lista.add(8,"COD_CLIENTE")
        lista.add(9,"COD_SUBCLIENTE")
        lista.add(10,"NOM_SUBCLIENTE")
        lista.add(11,"SIGLAS")
        lista.add(12,"DECIMALES")
        lista.add(13,"ESTADO")
        lista.add(14,"TOT_COMPROBANTE")
        return lista
    }
    private fun camposTablaSvmRebotesPorCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"CODIGO")
        lista.add(2,"DESC_PENALIDAD")
        lista.add(3,"MONTO_TOTAL")
        lista.add(4,"FECHA")
        return lista
    }
    private fun camposTablaSvmCiudades(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_PAIS")
        lista.add(1,"DESC_PAIS")
        lista.add(2,"COD_DEPARTAMENTO")
        lista.add(3,"DESC_DEPARTAMENTO")
        lista.add(4,"COD_CIUDAD")
        lista.add(5,"DESC_CIUDAD")
        lista.add(6,"FRECUENCIA")
        return lista
    }
    private fun camposTablaSvmTablaVisitas(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"CODIGO")
        lista.add(2,"DESCRIPCION")
        lista.add(3,"FRECUENCIA")
        return lista
    }
    private fun camposTablaSvmTipoCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"CODIGO")
        lista.add(1,"COD_SUBTIPO")
        lista.add(2,"DESC_CANAL_VENTA")
        lista.add(3,"DESCRIPCION")
        return lista
    }
    private fun camposTablaSvmCondicionVentaCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CONDICION_VENTA")
        lista.add(2,"DESCRIPCION")
        lista.add(3,"TIPO_CONDICION")
        lista.add(4,"ABREVIATURA")
        lista.add(5,"DIAS_INICIAL")
        return lista
    }
    private fun camposTablaSvmPreciosFijos(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_LISTA_PRECIO")
        lista.add(2,"DESC_LISTA_PRECIO")
        return lista
    }
    private fun camposTablaSvmLiqComisionVendedor(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_CONCEPTO")
        lista.add(1,"DESC_CONCEPTO")
        lista.add(2,"MONTO")
        lista.add(3,"TIPO")
        lista.add(4,"NRO_ORDEN")
        lista.add(5,"NRO_CUOTA")
        lista.add(6,"FEC_HASTA")
        lista.add(7,"COD_MONEDA")
        lista.add(8,"DECIMALES")
        lista.add(9,"SIGLAS")
        lista.add(10,"COMENT")
        return lista
    }
    private fun camposTablaSvmModuloComisionVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_CONCEPTO")
        lista.add(1,"DESC_CONCEPTO")
        lista.add(2,"DESC_MODULO")
        lista.add(3,"MONTO_VENTA")
        lista.add(4,"MONTO_COMISION")
        lista.add(5,"FEC_FINAL")
        lista.add(6,"TOT_VENTAS")
        lista.add(7,"TOT_COMISION")
        return lista
    }
    private fun camposTablaSvmDiasVisitaCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_CLIENTE")
        lista.add(1,"DESC_CLIENTE")
        lista.add(2,"COD_SUBCLIENTE")
        lista.add(3,"DESC_SUBCLIENTE")
        lista.add(4,"COD_VISITA_ACTUAL")
        lista.add(5,"DESC_VISITA_ACTUAL")
        return lista
    }
    private fun camposTablaSvmCondVentaVendedor(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_LISTA_PRECIO")
        lista.add(1,"COD_CLIENTE")
        lista.add(2,"COD_SUBCLIENTE")
        lista.add(3,"COD_CONDICION_VENTA")
        return lista
    }
    private fun camposTablaSvmEvolDiariaVenta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_VENDEDOR")
        lista.add(2,"DESC_VENDEDOR")
        lista.add(3,"PEDIDO_2_ATRAS")
        lista.add(4,"PEDIDO_1_ATRAS")
        lista.add(5,"TOTAL_PEDIDOS")
        lista.add(6,"TOTAL_FACT")
        lista.add(7,"META_VENTA")
        lista.add(8,"META_LOGRADA")
        lista.add(9,"PROY_VENTA")
        lista.add(10,"TOTAL_REBOTE")
        lista.add(11,"TOTAL_DEVOLUCION")
        lista.add(12,"CANT_CLIENTES")
        lista.add(13,"CANT_POSIT")
        lista.add(14,"EF_VISITA")
        lista.add(15,"DIA_TRAB")
        lista.add(16,"PUNTAJE")
        lista.add(17,"SURTIDO_EF")
        return lista
    }
    private fun camposTablaSvmVtvPreciosFijosMod(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_ARTICULO")
        lista.add(2,"DESC_ARTICULO")
        lista.add(3,"COD_LISTA_PRECIO")
        lista.add(4,"COD_UNIDAD_MEDIDA")
        lista.add(5,"REFERENCIA")
        lista.add(6,"FEC_VIGENCIA")
        lista.add(7,"PRECIO_ANT")
        lista.add(8,"PRECIO_ACT")
        lista.add(9,"DECIMALES")
        lista.add(10,"TIPO")
        return lista
    }
    private fun camposTablaStvCategoriaPalm(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CATEGORIA")
        lista.add(2,"DESC_CATEGORIA")
        lista.add(3,"COD_SEGMENTO")
        lista.add(4,"DESC_SEGMENTO")
        lista.add(5,"ORDEN")
        lista.add(6,"TIPO")
        lista.add(7,"COD_TIP_CLIENTE")
        return lista
    }
    private fun camposTablaSvmRetornoComentario(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_ENTRADA")
        lista.add(2,"FEC_SALIDA")
        lista.add(3,"COD_PROMOTOR")
        lista.add(4,"COD_CLIENTE")
        lista.add(5,"COD_SUBCLIENTE")
        lista.add(6,"COD_CATEGORIA")
        lista.add(7,"COD_SEGMENTO")
        lista.add(8,"TIPO")
        lista.add(9,"PUNTUACION")
        lista.add(10,"COMENTARIO_SUPERVISOR")
        return lista
    }
    private fun camposTablaSpmMotivoNoVenta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_MOTIVO")
        lista.add(2,"CIERRA")
        lista.add(3,"DESC_MOTIVO")
        return lista
    }
    private fun camposTablaSvmPromocionesPorPerfiles(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"TIP_CLIENTE")
        lista.add(2,"NRO_PROMOCION")
        lista.add(3,"DESCRIPCION")
        lista.add(4,"COMENTARIO")
        lista.add(5,"FEC_INI_PROMO")
        lista.add(6,"FEC_FIN_PROMO")
        lista.add(7,"COD_ARTICULO")
        return lista
    }
    private fun camposTablaSvmListadoPedidos(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"NRO_PEDIDO")
        lista.add(2,"FEC_COMPROBANTE")
        lista.add(3,"COD_CLIENTE")
        lista.add(4,"COD_SUBCLIENTE")
        lista.add(5,"NOM_SUBCLIENTE")
        lista.add(6,"SIGLAS")
        lista.add(7,"DECIMALES")
        lista.add(8,"TOT_COMPROBANTE")
        lista.add(9,"OBSERVACIONES")
        return lista
    }
    private fun camposTablaSvmDiasTomaFotoCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_TIP_CLIENTE")
        lista.add(2,"FEC_INICIO")
        lista.add(3,"FEC_FIN")
        return lista
    }
    private fun camposTablaSvmSegVisitasSemanal(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_MOVIMIENTO")
        lista.add(2,"FEC_INICIO")
        lista.add(3,"FEC_FIN")
        lista.add(4,"CANTIDAD")
        lista.add(5,"CANT_VENDIDO")
        lista.add(6,"CANT_NO_VENDIDO")
        lista.add(7,"CANT_NO_VISITADO")
        lista.add(8,"SEMANA")
        lista.add(9,"PORC")
        return lista
    }
    private fun camposTablaCcvMotivoSolTarjCred(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_MOTIVO")
        lista.add(1,"IND_FOTO")
        lista.add(2,"DESC_MOTIVO")
        return lista
    }
    private fun camposTablaSvmPromocionesArtCab(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"NRO_PROMOCION")
        lista.add(2,"COD_UNID_NEGOCIO")
        lista.add(3,"TIP_CLIENTE")
        lista.add(4,"DESCRIPCION")
        lista.add(5,"COMENTARIO")
        lista.add(6,"COD_CONDICION_VENTA")
        lista.add(7,"FEC_VIGENCIA")
        lista.add(8,"COD_ARTICULO")
        lista.add(9,"DESC_ARTICULO")
        lista.add(10,"COD_UNIDAD_MEDIDA")
        lista.add(11,"REFERENCIA")
        lista.add(12,"IND_TIPO")
        lista.add(13,"MULT")
        lista.add(14,"COD_LISTA_PRECIO")
        lista.add(15,"IND_COMBO")
        lista.add(16,"IND_PROM")
        lista.add(17,"IND_ART")
        lista.add(18,"CANT_VENTA")
        return lista
    }
    private fun camposTablaSvmPromocionesArtDet(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"NRO_PROMOCION")
        lista.add(2,"COD_UNID_NEGOCIO")
        lista.add(3,"CANT_DESDE")
        lista.add(4,"CANT_HASTA")
        lista.add(5,"COD_ARTICULO")
        lista.add(6,"COD_UNIDAD_MEDIDA")
        lista.add(7,"DESC_UND_MEDIDA")
        lista.add(8,"IND_TIPO")
        lista.add(9,"DESCUENTO")
        lista.add(10,"PRECIO_GS")
        lista.add(11,"IND_MULTIPLE")
        lista.add(12,"PRECIO_RS")
        return lista
    }
    private fun camposTablaSvmMensajeConclusion(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"IND_LOGRADO")
        lista.add(2,"RESULTADO")
        return lista
    }
    private fun camposTablaSvmSurtidoEficiente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CLIENTE")
        lista.add(2,"COD_SUBCLIENTE")
        lista.add(3,"TIP_CLIENTE")
        lista.add(4,"COD_ARTICULO")
        lista.add(5,"TIP_SURTIDO")
        return lista
    }
    private fun camposTablaSvmLiqPremiosVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"TIP_COM")
        lista.add(2,"COD_MARCA")
        lista.add(3,"DESC_MARCA")
        lista.add(4,"MONTO_VENTA")
        lista.add(5,"MONTO_META")
        lista.add(6,"PORC_COBERTURA")
        lista.add(7,"PORC_LOG")
        lista.add(8,"MONTO_A_COBRAR")
        lista.add(9,"MONTO_COBRAR")
        lista.add(10,"TOT_CLIENTES")
        lista.add(11,"CLIENTES_VISITADOS")
        return lista
    }
    private fun camposTablaSvmProduccionSemanalVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"CANTIDAD")
        lista.add(2,"SEMANA")
        lista.add(3,"FECHA")
        lista.add(4,"MONTO_VIATICO")
        lista.add(5,"MONTO_TOTAL")
        lista.add(6,"PERIODO")
        return lista
    }
    private fun camposTablaSvmRuteoSemanalClienteVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_CLIENTE")
        lista.add(2,"COD_SUBCLIENTE")
        lista.add(3,"DESC_CLIENTE")
        lista.add(4,"DIA")
        lista.add(5,"FEC_VISITA")
        return lista
    }
    private fun camposTablaSvmCoberturaVisVendedores(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_GRUPO")
        lista.add(1,"PORC_DESDE")
        lista.add(2,"PORC_HASTA")
        lista.add(3,"PORC_COM")
        return lista
    }
    private fun camposTablaRhvLiquidacionFuerzaVenta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_COMPROBANTE")
        lista.add(2,"OBSERVACION")
        lista.add(3,"DESCRIPCION")
        lista.add(4,"TIP_COMPROBANTE_REF")
        lista.add(5,"TOT_IVA")
        lista.add(6,"TOT_GRAVADA")
        lista.add(7,"TOT_EXENTA")
        lista.add(8,"TOT_COMPROBANTE")
        return lista
    }
    private fun camposTablaSvmVtMotivosSodDev(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_MOTIVO")
        lista.add(2,"DESC_MOTIVO")
        return lista
    }
    private fun camposTablaFvvLiqCuotaXUndNegVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_UNID_NEGOCIO")
        lista.add(2,"DESC_UNID_NEGOCIO")
        lista.add(3,"MONTO_VENTA")
        lista.add(4,"MONTO_CUOTA")
        lista.add(5,"PORC_PREMIO")
        lista.add(6,"FEC_DESDE")
        lista.add(7,"FEC_HASTA")
        lista.add(8,"PORC_ALC_PREMIO")
        lista.add(9,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaSvmCoberturaMensualVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"TOT_CLI_CART")
        lista.add(2,"CANT_POSIT")
        lista.add(3,"PORC_COB")
        lista.add(4,"PORC_LOGRO")
        lista.add(5,"FEC_DESDE")
        lista.add(6,"FEC_HASTA")
        lista.add(7,"PREMIOS")
        lista.add(8,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaFvvCobSemanalVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"SEMANA")
        lista.add(2,"TOT_CLIENTES")
        lista.add(3,"CLIENT_VENTAS")
        lista.add(4,"PORC_COBERTURA")
        lista.add(5,"PERIODO")
        lista.add(6,"MONTO_A_COBRAR")
        return lista
    }

//    fun camposTablaS(): Vector<String> {
//        var lista : Vector<String> = Vector()
//        lista.add(0,"")
//        lista.add(1,"")
//        lista.add(2,"")
//        lista.add(3,"")
//        lista.add(4,"")
//        lista.add(5,"")
//        lista.add(6,"")
//        lista.add(7,"")
//        lista.add(8,"")
//        lista.add(9,"")
//        lista.add(10,"")
//        lista.add(11,"")
//        lista.add(12,"")
//        lista.add(13,"")
//        lista.add(14,"")
//        lista.add(15,"")
//        lista.add(16,"")
//        lista.add(17,"")
//        lista.add(18,"")
//        lista.add(19,"")
//        lista.add(20,"")
//        lista.add(21,"")
//        lista.add(22,"")
//        lista.add(23,"")
//        lista.add(24,"")
//        lista.add(25,"")
//        lista.add(26,"")
//        lista.add(27,"")
//        lista.add(28,"")
//        lista.add(29,"")
//        return lista
//    }


}
