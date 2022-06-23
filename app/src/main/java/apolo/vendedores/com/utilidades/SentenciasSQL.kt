package apolo.vendedores.com.utilidades

import apolo.vendedores.com.clases.Usuario
import java.util.*

class SentenciasSQL {
    companion object{
        var sql: String = ""

        fun createTableUsuarios(): String{
            return "CREATE TABLE IF NOT EXISTS usuarios " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT  , NOMBRE TEXT       , " +
                    " LOGIN TEXT                            , TIPO TEXT         , " +
                    " ACTIVO TEXT                           , COD_EMPRESA TEXT  , " +
                    " VERSION TEXT                          , MIN_FOTOS   TEXT  , " +
                    " MAX_FOTOS TEXT                        , COD_PERSONA TEXT  , " +
                    " PROG_PEDIDO TEXT);"
        }
        fun insertUsuario(usuario: HashMap<String, String>):String{
            return ("INSERT INTO usuarios (NOMBRE, LOGIN, TIPO, ACTIVO, COD_EMPRESA, VERSION,PROG_PEDIDO) VALUES " +
                    "('" + usuario["NOMBRE"] + "'," +
                     "'" + usuario["LOGIN"] + "'," +
                     "'" + usuario["TIPO"] + "'," +
                     "'" + usuario["ACTIVO"] + "'," +
                     "'" + usuario["COD_EMPRESA"] + "'," +
                     "'" + usuario["VERSION"] + "'," +
                     "'" + usuario["PROG_PEDIDO"] + "'" +
                     ")")
        }

        fun insertUsuario2(usuario: Usuario):String{
            return ("INSERT INTO usuarios (NOMBRE, LOGIN, TIPO, ACTIVO, COD_EMPRESA, VERSION,PROG_PEDIDO) VALUES " +
                    "('" + usuario.nombre + "'," +
                    "'" + usuario.login + "'," +
                    "'" + usuario.tipo + "'," +
                    "'" + usuario.activo + "'," +
                    "'" + usuario.cod_empresa + "'," +
                    "'" + usuario.version + "'," +
                    "'" + usuario.prog_pedido + "'" +
                    ")")
        }

        //Sincronizacion
        fun createTableClienteListPrec(): String {
            return ("CREATE TABLE IF NOT EXISTS cliente_list_prec"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT     , COD_CLIENTE TEXT  , COD_SUBCLIENTE TEXT,"
                    + "COD_VENDEDOR TEXT                     , COD_LISTA_PRECIO TEXT, DESC_LISTA TEXT   , COD_MONEDA TEXT    ,"
                    + "DECIMALES TEXT                        , IND_DEFECTO TEXT     , DESC_VENDEDOR TEXT);")
        }

        fun createTableSvmClienteVendedor(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_cliente_vendedor "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT     , COD_CLIENTE TEXT , COD_SUBCLIENTE TEXT,"
                    + " DESC_SUBCLIENTE TEXT                 , DESC_CLIENTE TEXT    , COD_VENDEDOR TEXT, TELEFONO TEXT      ,"
                    + " LONGI REAL                           , LATI REAL            , TIP_CAUSAL TEXT  , DESC_CIUDAD TEXT   ,"
                    + " RUC TEXT                             , DESC_ZONA TEXT       , DESC_REGION TEXT , DIRECCION TEXT     ,"
                    + " COD_SUCURSAL TEXT                    , TIP_CLIENTE TEXT     , DESC_TIPO TEXT   , COD_CONDICION TEXT ,"
                    + " DESC_CONDICION TEXT                  , LIMITE_CREDITO NUMBER, TOT_DEUDA NUMBER , SALDO NUMBER       ,"
                    + " FEC_VISITA TEXT                      , IND_VISITA TEXT      , COD_ZONA         , TIPO_CONDICION TEXT,"
                    + " IND_DIRECTA TEXT                     , IND_ATRAZO TEXT      , FRECUENCIA TEXT  , DIAS_INICIAL TEXT  ,"
                    + " COMENTARIO TEXT						 , IND_ESP TEXT 		, CATEGORIA TEXT   , TELEFONO2 TEXT		,"
                    + " LATITUD TEXT						 , LONGITUD TEXT		, CERCA_DE TEXT	   , IND_CADUCADO TEXT  ,"
                    + " IND_VITRINA TEXT 					 , ESTADO TEXT 			, FOTO_FACHADA TEXT, TIEMPO_MIN TEXT    ,"
                    + " TIEMPO_MAX TEXT 					 , SOL_TARG TEXT        , DESC_VENDEDOR TEXT); ")
        }

        fun createTableSvmCondicionVenta(): String {
            return "CREATE TABLE IF NOT EXISTS svm_condicion_venta" +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_CONDICION_VENTA TEXT," +
                    " DESCRIPCION TEXT                     , TIPO_CONDICION TEXT, ABREVIATURA TEXT        ," +
                    " DIAS_INICIAL TEXT                    , PORC_DESC TEXT     , MONTO_MIN_DESC TEXT);"
        }

        fun createTableSvmDeudaCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_deuda_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , COD_VENDEDOR TEXT   ,"
                    + " COD_CLIENTE TEXT                    , COD_SUBCLIENTE TEXT , FEC_EMISION TEXT    ,"
                    + " FEC_VENCIMIENTO NUMBER              , TIP_DOCUMENTO TEXT  , NRO_DOCUMENTO TEXT  ,"
                    + " SALDO_CUOTA TEXT                    , DIA_ATRAZO TEXT     , ABREVIATURA TEXT    ,"
                    + " DESC_VENDEDOR TEXT                  )")
        }

        fun createTableSvmVendedorPedido(): String {
            return "CREATE TABLE IF NOT EXISTS svm_vendedor_pedido " +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT , COD_EMPRESA TEXT      , COD_VENDEDOR TEXT         ,       " +
                    " IND_PALM TEXT                         , TIPO TEXT             , SERIE TEXT                ,       " +
                    " NUMERO TEXT                           , FECHA TEXT            , ULTIMA_SINCRO TEXT        ,       " +
                    " VERSION TEXT                          , COD_CLI_VEND TEXT     , COD_PERSONA TEXT          ,       " +
                    " RANGO TEXT						    , PER_VENDER TEXT	    , PER_PRESENCIAL TEXT       ,       " +
                    " HORA TEXT							    , MIN_FOTOS TEXT	    , MAX_FOTOS TEXT            ,       " +
                    " VERSION_SISTEMA TEXT 				    , PER_ASISTENCIA TEXT   , FRECUENCIA_RASTREO TEXT   ,       " +
                    " HORA_INICIO TEXT					    , HORA_FIN TEXT         , TIEMPO_ASISTENCIA TEXT    ,       " +
                    " ULTIMA_VEZ TEXT 					    , IND_FOTO TEXT         , MIN_VENTA TEXT            ,       " +
                    " VENT_CON_MARC TEXT 					, PER_MARC_ASIS TEXT    , DESC_VENDEDOR TEXT)       ;"
        }

        fun createTableSvmArticulosPrecios(): String {
            return "CREATE TABLE IF NOT EXISTS svm_articulos_precios " +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA  TEXT     , COD_VENDEDOR TEXT default '9504'        ," +
                    " MODULO TEXT      ," +
                    " COD_LISTA_PRECIO TEXT                , COD_ARTICULO TEXT     , DESC_ARTICULO TEXT   , PREC_UNID NUMBER , " +
                    " PREC_CAJA NUMBER                     , COD_UNIDAD_MEDIDA TEXT, PORC_IVA NUMBER      , REFERENCIA TEXT  , " +
                    " MULT NUMBER                          , DIV NUMBER            , IND_LIM_VENTA NUMBER , COD_LINEA TEXT   , " +
                    " COD_FAMILIA TEXT                     , COD_BARRA TEXT        , PORC_DESCUENTO NUMBER,                    " +
                    " CANT_MINIMA NUMBER                   , IND_PROMO_ACT TEXT DEFAULT 'N');"
        }

        fun createTableSvmStArticulos(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_st_articulos"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA  TEXT , COD_ARTICULO TEXT, DESC_ARTICULO TEXT,"
                    + " COD_UNIDAD_REL TEXT                  , REFERENCIA TEXT   , MULT NUMBER      , DIV  NUMBER       ,"
                    + " COD_IVA TEXT                         , PORC_IVA TEXT     , COD_LINEA TEXT   , COD_FAMILIA TEXT  ,"
                    + " IND_BASICO TEXT                      , CANT_MINIMA NUMBER, CANT_EXIST TEXT  , COD_BARRA TEXT);")
        }

        fun createTableSvmMetasPuntoPorLinea(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_metas_punto_por_linea"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    	 , DESC_VENDEDOR TEXT  , COD_CATEGORIA TEXT ,"
                    + "  DESC_CATEGORIA TEXT				 , DESC_GTE_MARKETIN TEXT, DESC_MODULO TEXT    , COD_SUPERVISOR TEXT , "
                    + "  DESC_SUPERVISOR TEXT				 , MAYOR_VENTA TEXT		 , VENTA_MES1 TEXT	   , VENTA_MES2 TEXT	 , "
                    + "  META TEXT		   					 , MES_1 TEXT			 , MES_2 TEXT		   , NRO_ORD_MAG TEXT    ,"
                    + "  ORD_GTE_MARK TEXT					 , ORD_CATEGORIA TEXT    , COD_VENDEDOR TEXT)")
        }

        fun createTableSvmMetasPuntoPorCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_metas_punto_por_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , DESC_VENDEDOR TEXT ,"
                    + " CODIGO TEXT                         , NOM_SUBCLIENTE TEXT , CIUDAD TEXT        ,"
                    + " COD_SUPERVISOR TEXT                 , DESC_SUPERVISOR TEXT, LISTA_PRECIO TEXT  ,"
                    + " MAYOR_VENTA TEXT                    , VENTA_3 TEXT        , MIX_3 TEXT         ,"
                    + " VENTA_4 TEXT                        , MIX_4 TEXT          , METAS TEXT         ,"
                    + " TOT_SURTIDO TEXT                    , MES_1 TEXT          , MES_2 TEXT         ,"
                    + " COD_VENDEDOR TEXT)")
        }

        fun createTableSvmPedidosSinStockRep(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_pedidos_sin_stock_rep "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , ORIGEN TEXT         ,"
                    + " DESC_DEPOSITO TEXT                  , FEC_COMPROBANTE TEXT, COD_CLIENTE TEXT    ,"
                    + " CLIENTE TEXT                        , VENDEDOR TEXT       , SUPERVISOR TEXT     ,"
                    + " COD_ARTICULO TEXT                   , ARTICULO TEXT       , CANTIDAD TEXT       ,"
                    + " PRECIO_UNITARIO TEXT                , MONTO_TOTAL TEXT    , PEDIDO TEXT         ,"
                    + " DECIMALES TEXT                      , DESC_MOTIVO TEXT    , COD_VENDEDOR TEXT   ,"
                    + " DESC_VENDEDOR TEXT)")
        }

        fun createTableSvmNegociacionVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_negociacion_venta "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , FEC_COMPROBANTE TEXT ,"
                    + " COD_CLIENTE TEXT                    , DESC_SUBCLIENTE TEXT, COD_LISTA_PRECIO TEXT,"
                    + " COD_SUPERVISOR TEXT                 , NOM_SUPERVISOR TEXT , COD_ARTICULO TEXT    ,"
                    + " DESC_ARTICULO TEXT                  , REFERENCIA TEXT     , CANTIDAD TEXT        ,"
                    + " PRECIO_LISTA TEXT                   , PRECIO_SOLIC TEXT   , PORC_DESCUENTO TEXT  ,"
                    + " TOTAL_DESC_OTORG TEXT               , DECIMALES TEXT      , IND_AUT TEXT         ,"
                    + " OBSERVACION TEXT                    , COD_VENDEDOR TEXT   , DESC_VENDEDOR TEXT   )")
        }

        fun createTableSvmPedidosEnReparto(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_pedidos_en_reparto "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT      , NRO_PLANILLA TEXT    ,"
                    + " DESC_REPARTIDOR TEXT                , TEL_REPARTIDOR TEXT   ,"
                    + " FEC_PLANILLA TEXT                   , FEC_COMPROBANTE TEXT  , TIP_COMPROBANTE TEXT ,"
                    + " NRO_COMPROBANTE TEXT                , COD_CLIENTE TEXT      , COD_SUBCLIENTE TEXT  ,"
                    + " NOM_CLIENTE TEXT                    , NOM_SUBCLIENTE TEXT   , SIGLAS TEXT          ,"
                    + " DECIMALES TEXT                      , TOT_COMPROBANTE TEXT  , ESTADO TEXT          ,"
                    + " COD_VENDEDOR TEXT                   , DESC_VENDEDOR TEXT    )")
        }

        fun createTableSvmRebotesPorCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_rebotes_por_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT, CODIGO TEXT ,"
                    + " DESC_PENALIDAD TEXT                 , MONTO_TOTAL TEXT, FECHA TEXT  ,"
                    + " COD_VENDEDOR TEXT                   , DESC_VENDEDOR TEXT)")
        }

        fun createTableSvmCiudades(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_ciudades "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT      , COD_VENDEDOR TEXT     ,"
                    + "COD_CIUDAD TEXT        , DESC_CIUDAD TEXT                    , COD_DEPARTAMENTO TEXT ,"
                    + " DESC_DEPARTAMENTO TEXT, COD_PAIS TEXT                       , DESC_PAIS TEXT        ,"
                    + " FRECUENCIA TEXT       )")
        }

        fun createTableSvmTablaVisitas(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_tabla_visitas "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT, CODIGO TEXT, "
                    + " DESCRIPCION TEXT                    , FRECUENCIA TEXT )")
        }

        fun createTableSvmTipoCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_tipo_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT      ,   CODIGO TEXT, "
                    + " DESCRIPCION TEXT,   COD_SUBTIPO TEXT, DESC_CANAL_VENTA TEXT )")
        }

        fun createTableSvmCondicionVentaCliente() : String {
            return ("CREATE TABLE IF NOT EXISTS svm_condicion_venta_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_CONDICION_VENTA TEXT,"
                    + " DESCRIPCION TEXT                    , TIPO_CONDICION TEXT, ABREVIATURA TEXT        ,"
                    + " DIAS_INICIAL )")
        }

        fun createTableSvmPreciosFijos(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_precios_fijos "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_VENDEDOR TEXT    ,"
                    + "COD_LISTA_PRECIO TEXT, DESC_LISTA_PRECIO TEXT  )")
        }

        fun createTableSvmLiqComisionVendedor(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_liq_comision_vendedor "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_CONCEPTO TEXT  , DESC_CONCEPTO TEXT,"
                    + " MONTO TEXT							, TIPO TEXT          , NRO_ORDEN TEXT    ,"
                    + " NRO_CUOTA TEXT 						, FEC_HASTA TEXT  	 , COD_MONEDA TEXT   ,"
                    + " DECIMALES TEXT   					, SIGLAS TEXT  		 , COMENT TEXT       ,"
                    + " COD_VENDEDOR TEXT                   , DESC_VENDEDOR TEXT , COD_EMPRESA TEXT)")
        }

        fun createTableSvmModuloComisionVend(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_modulo_comision_vend "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_CONCEPTO TEXT    , DESC_CONCEPTO TEXT ,"
                    + " DESC_MODULO TEXT                     , MONTO_VENTA TEXT     , MONTO_COMISION TEXT,"
                    + " FEC_FINAL TEXT                       , TOT_VENTAS TEXT      , TOT_COMISION TEXT  ,"
                    + " COD_VENDEDOR TEXT                    , DESC_VENDEDOR TEXT   , COD_EMPRESA TEXT)")
        }

        fun createTableSvmCondVentaVendedor(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_cond_venta_vendedor "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT   , COD_EMPRESA TEXT          , COD_VENDEDOR TEXT ,"
                    + " COD_LISTA_PRECIO TEXT                   , COD_CONDICION_VENTA TEXT  , COD_CLIENTE TEXT  , "
                    + " COD_SUBCLIENTE TEXT )")
        }

        fun createTableSvmEvolDiariaVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_evol_diaria_venta"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_VENDEDOR TEXT  ,"
                    + "  DESC_VENDEDOR TEXT					 , PEDIDO_2_ATRAS TEXT, PEDIDO_1_ATRAS TEXT,"
                    + "  TOTAL_PEDIDOS TEXT					 , TOTAL_FACT TEXT    , META_VENTA TEXT	   ,"
                    + "  META_LOGRADA TEXT					 , PROY_VENTA TEXT	  , TOTAL_REBOTE TEXT  ,"
                    + "  TOTAL_DEVOLUCION TEXT				 , CANT_CLIENTES TEXT , CANT_POSIT TEXT	   ,"
                    + "  EF_VISITA TEXT						 , DIA_TRAB TEXT	  , PUNTAJE TEXT	   ,"
                    + "  SURTIDO_EF TEXT	)")
        }

        fun createTableVtvPreciosFijos(): String {
            return ("CREATE TABLE IF NOT EXISTS vtv_precios_fijos "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT  	, COD_ARTICULO TEXT  	,"
                    + " DESC_ARTICULO TEXT					 , COD_LISTA_PRECIO TEXT, COD_UNIDAD_MEDIDA TEXT,"
                    + " REFERENCIA TEXT						 , FEC_VIGENCIA TEXT 	, PRECIO_ANT TEXT 		,"
                    + " PRECIO_ACT TEXT					 	 , TIPO TEXT			, DECIMALES TEXT        ,"
                    + " COD_VENDEDOR TEXT)")
        }

        fun createTableStvCategoriaPalm(): String {
            return ("CREATE TABLE IF NOT EXISTS stv_categoria_palm "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT		, COD_CATEGORIA TEXT, DESC_CATEGORIA TEXT,"
                    + "	 COD_SEGMENTO TEXT					 , DESC_SEGMENTO TEXT	, ORDEN TEXT		, TIPO TEXT		 	 , "
                    + "  COD_TIP_CLIENTE TEXT                , COD_VENDEDOR TEXT)")
        }

        fun createTableSpmMotivoNoVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS spm_motivo_no_venta"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	, COD_MOTIVO TEXT,"
                    + "	 DESC_MOTIVO TEXT 					 , CIERRA TEXT)")
        }

        fun createTableSvmPromocionesPorPerfiles(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_promociones_por_perfiles "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	 , TIP_CLIENTE TEXT		,"
                    + " NRO_PROMOCION TEXT					 , DESCRIPCION TEXT	 , COMENTARIO TEXT		,"
                    + " FEC_INI_PROMO TEXT					 , FEC_FIN_PROMO TEXT, COD_ARTICULO TEXT )")
        }

        fun createTableSvmListadoPedido(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_listado_pedidos "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	 , NRO_PEDIDO TEXT		,"
                    + " FEC_COMPROBANTE TEXT				 , COD_CLIENTE TEXT	 , COD_SUBCLIENTE TEXT	,"
                    + " NOM_SUBCLIENTE TEXT					 , SIGLAS TEXT		 , DECIMALES TEXT 		,"
                    + " TOT_COMPROBANTE TEXT				 , OBSERVACIONES TEXT, COD_VENDEDOR TEXT    ,"
                    + " DESC_VENDEDOR TEXT)")
        }

        fun createTableSvmDiasTomaFotoCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_dias_toma_foto_cliente"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT , COD_TIP_CLIENTE TEXT, FEC_INICIO TEXT,"
                    + "  FEC_FIN TEXT                        , COD_VENDEDOR TEXT, DESC_VENDEDOR TEXT );")
        }

        fun createTableSvmSegVisitasSemanal(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_seg_visitas_semanal"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT , COD_EMPRESA TEXT       , FEC_MOVIMIENTO TEXT    , "
                    + "  FEC_INICIO TEXT                	  , FEC_FIN TEXT           , CANTIDAD TEXT          , "
                    + "  CANT_VENDIDO TEXT                    , CANT_NO_VENDIDO TEXT   , CANT_NO_VISITADO TEXT  , "
                    + "  SEMANA TEXT                          , PORC TEXT              , COD_VENDEDOR TEXT      , "
                    + "  DESC_VENDEDOR TEXT                   , COD_PERSONA TEXT       , CANT_CLIENTE TEXT      , "
                    + "  CLI_SEMANA TEXT                      , CANT_VISITA_VALIDA TEXT, CANT_EN_PAREJA TEXT    , "
                    + "  CANT_FUERA_RUTA TEXT                 , CANT_VISITA TEXT       , PORC_VISITA_VALIDA TEXT, "
                    + "  PORC_TOTAL_VISITA TEXT )")
        }

        fun createTableSvmPromocionesArtCab(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_promociones_art_cab"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT			, NRO_PROMOCION TEXT	,"
                    + "  COD_UNID_NEGOCIO TEXT				 , TIP_CLIENTE TEXT			, DESCRIPCION TEXT	  	,"
                    + "  COMENTARIO TEXT					 , COD_CONDICION_VENTA TEXT , FEC_VIGENCIA TEXT   	,"
                    + "  COD_ARTICULO TEXT					 , DESC_ARTICULO TEXT		, COD_UNIDAD_MEDIDA TEXT,"
                    + "  REFERENCIA TEXT					 , NRO_GRUPO TEXT			, IND_TIPO TEXT			,"
                    + "  CANT_VENTA TEXT 					 , MULT TEXT                , COD_LISTA_PRECIO TEXT ,"
                    + "  IND_COMBO TEXT                      , IND_PROM TEXT            , IND_ART TEXT          ,"
                    + "  COD_VENDEDOR TEXT                   , DESC_VENDEDOR TEXT)")
        }

        fun createTableSvmPromocionesArtDet(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_promociones_art_det"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT			, NRO_PROMOCION TEXT	,"
                    + "  COD_UNID_NEGOCIO TEXT				 , NRO_GRUPO TEXT			, CANT_DESDE TEXT	  	,"
                    + "  CANT_HASTA TEXT					 , COD_ARTICULO TEXT 		, COD_UNIDAD_MEDIDA TEXT,"
                    + "  DESC_UND_MEDIDA TEXT			 	 , IND_TIPO TEXT			, DESCUENTO TEXT		,"
                    + "  PRECIO_GS TEXT					 	 , PRECIO_RS TEXT		 	, MULT TEXT				,"
                    + "  IND_MULTIPLE TEXT                   , COD_VENDEDOR TEXT)")
        }

        fun createTableSvmMensajeConclusion(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_mensaje_conclusion "
                    + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT , RESULTADO TEXT, IND_LOGRADO TEXT)")
        }

        fun createTableSvmSurtidoEficiente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_surtido_eficiente "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT  	, COD_CLIENTE TEXT	, "
                    + " COD_SUBCLIENTE TEXT					 , TIP_CLIENTE TEXT 	, COD_ARTICULO TEXT	, "
                    + " TIP_SURTIDO TEXT                     , COD_VENDEDOR TEXT							  "
                    + ")")
        }

        fun createTableSvmLiqPremiosVend(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_liq_premios_vend "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA    TEXT  , TIP_COM  TEXT , "
                    + " COD_MARCA          TEXT   , DESC_MARCA     TEXT  , MONTO_VENTA  NUMBER , "
                    + " MONTO_META         NUMBER , PORC_COBERTURA TEXT  , PORC_LOG     TEXT   , "
                    + " MONTO_A_COBRAR     NUMBER , MONTO_COBRAR   NUMBER, TOT_CLIENTES TEXT   , "
                    + " CLIENTES_VISITADOS TEXT   , COD_VENDEDOR TEXT    , DESC_VENDEDOR TEXT )")
        }

        fun createTableSvmProduccionSemanalVend(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_produccion_semanal_vend "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA    TEXT  , CANTIDAD       NUMBER  , "
                    + " SEMANA         TEXT  , FECHA          TEXT    , "
                    + " MONTO_VIATICO  NUMBER, MONTO_TOTAL    NUMBER  , "
                    + " PERIODO TEXT         , COD_VENDEDOR TEXT      , DESC_VENDEDOR TEXT)")
        }

        fun createTableSvmRuteoSemanalClienteVend(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_ruteo_semanal_cliente_vend "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA    TEXT, COD_CLIENTE  TEXT, "
                    + " COD_SUBCLIENTE TEXT, DESC_CLIENTE TEXT, "
                    + " DIA            TEXT, FEC_VISITA   DATE, COD_VENDEDOR TEXT   , DESC_VENDEDOR TEXT )")
        }

        fun createTableSvmCoberturaVisVendedores(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_cobertura_vis_vendedores "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_GRUPO  TEXT      , PORC_DESDE  TEXT,"
                    + " PORC_HASTA TEXT, PORC_COM    TEXT    , COD_VENDEDOR TEXT    , COD_EMPRESA TEXT)")
        }

        fun createTableRhvLiquidacionFuerzaVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS rhv_liquidacion_fuerza_venta "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , FEC_COMPROBANTE  DATE  , "
                    + " OBSERVACION         TEXT  , DESCRIPCION      TEXT  , "
                    + " TIP_COMPROBANTE_REF TEXT  , TOT_IVA          NUMBER, "
                    + " TOT_GRAVADA         NUMBER, TOT_EXENTA       NUMBER, "
                    + " TOT_COMPROBANTE     NUMBER)")
        }

        fun createTableSvmVtMotivosSdDev(): String { //SD
            return ("CREATE TABLE IF NOT EXISTS svm_motivos_sd_dev"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT		, COD_MOTIVO TEXT	, "
                    + " DESC_MOTIVO TEXT) ")
        }

        fun createTableFvvLiqCuotaXUndNegVend(): String {
            return ("CREATE TABLE IF NOT EXISTS fvv_liq_cuota_x_und_neg_vend"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	       , COD_EMPRESA TEXT   , "
                    + " COD_UNID_NEGOCIO TEXT   , DESC_UNID_NEGOCIO TEXT , MONTO_VENTA NUMBER , MONTO_CUOTA     NUMBER , "
                    + " PORC_PREMIO      TEXT   , FEC_DESDE         TEXT , FEC_HASTA   TEXT   , PORC_ALC_PREMIO TEXT   , "
                    + " MONTO_A_COBRAR   NUMBER , COD_VENDEDOR TEXT      , DESC_VENDEDOR TEXT)")
        }

        fun createTableSvmCoberturaMensualVend(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_cobertura_mensual_vend"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT , COD_EMPRESA TEXT      , TOT_CLI_CART NUMBER , "
                    + " CANT_POSIT NUMBER , PORC_COB   NUMBER , PORC_LOGRO  TEXT      , FEC_DESDE  TEXT     , "
                    + " FEC_HASTA  TEXT   , PREMIOS    NUMBER , MONTO_A_COBRAR NUMBER , COD_VENDEDOR TEXT   , "
                    + " DESC_VENDEDOR TEXT)")
        }

        fun createTableFvvCobSemanalVend(): String {
            return ("CREATE TABLE IF NOT EXISTS fvv_cob_semanal_vend"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT      , COD_EMPRESA TEXT , SEMANA         TEXT   , TOT_CLIENTES NUMBER , "
                    + " CLIENT_VENTAS NUMBER , PORC_COBERTURA TEXT , PERIODO     TEXT , MONTO_A_COBRAR NUMBER , COD_VENDEDOR TEXT   , "
                    + " DESC_VENDEDOR TEXT) ")
        }

        //NO SINCRONIZADOS
        private fun createTableVtMarcacionUbicacion(): String {
            return "CREATE TABLE IF NOT EXISTS vt_marcacion_ubicacion" +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT , COD_EMPRESA TEXT      , FECHA TEXT        , COD_PROMOTOR TEXT, " +
                    "  COD_CLIENTE TEXT   				    , COD_SUBCLIENTE TEXT   , TIPO TEXT  	    , ESTADO TEXT      , " +
                    "  LATITUD TEXT    					    , LONGITUD TEXT	        , OBSERVACION TEXT  , IND_RRHH TEXT DEFAULT 'N'," +
                    "  IND_GC TEXT DEFAULT 'N'              , IND_COORD TEXT DEFAULT 'N'                , IND_SUBSOORD TEXT DEFAULT 'N' );"
        }
        private fun createTableSvmModificaCatastro(): String {
            return  ("CREATE TABLE IF NOT EXISTS svm_modifica_catastro "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	, COD_CLIENTE TEXT		,"
                    + " COD_SUBCLIENTE TEXT					 , TELEFONO1 TEXT	, TELEFONO2 TEXT		,"
                    + " DIRECCION TEXT						 , CERCA_DE TEXT	, LATITUD TEXT			,"
                    + " LONGITUD TEXT						 , ESTADO TEXT		, FECHA TEXT		    ,"
                    + " FOTO_FACHADA BLOB		 			 , TIPO TEXT        )")
        }
        private fun createTableSvmCatastroCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_catastro_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_CLIENTE TEXT      , COD_VENDEDOR TEXT       ,"
                    + " NOM_VENDEDOR TEXT 	 	 			, RAZON_SOCIAL TEXT      , NOM_FANTASIA TEXT       ,"
                    + " DIR_COMERCIAL TEXT	     			, COD_PAIS TEXT         , COD_DEPARTAMENTO TEXT   ,"
                    + " COD_CIUDAD TEXT  	     			, BARRIO TEXT   	    , RUC TEXT             	  ,"
                    + " CI TEXT    	   	  	 				, CELULAR TEXT    	    , LINEA_BAJA TEXT      	  ,"
                    + " COD_CONDICION_VENTA TEXT			, COD_TIPO_CLIENTE TEXT , COD_DIAS_VISITA TEXT    ,"
                    + " FEC_ALTA TEXT                       , ESTADO TEXT           , COD_CLI_VEND TEXT       ,"
                    + " CERCA_DE TEXT                       , EMAIL TEXT            , CANAL_SUGERIDO TEXT     ,"
                    + " NOM_REF_COMERCIAL TEXT              , TEL_REF_COMERCIAL TEXT, NOM_REF_BANCARIA TEXT   ,"
                    + " TEL_REF_BANCARIA TEXT               , COMENTARIO TEXT       , LIMITE_CREDITO TEXT	  ,"
                    + " LATITUD TEXT						, LONGITUD TEXT			, FOTO_FACHADA BLOB		 )")
        }
        private fun createTableSvmPedidosCab(): String {
            return ("CREATE TABLE IF NOT EXISTS vt_pedidos_cab"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT     , COD_CLIENTE TEXT  , COD_SUBCLIENTE TEXT        ,"
                    + " COD_VENDEDOR TEXT                    , COD_LISTA_PRECIO TEXT, FECHA TEXT        , FECHA_INT TEXT             ,"
                    + " NUMERO NUMBER                        , NRO_ORDEN_COMPRA TEXT, COD_CONDICION TEXT, TIP_CAMBIO                 ,"
                    + " TOT_COMPROBANTE TEXT                 , ESTADO TEXT          , COD_MONEDA TEXT   , DECIMALES TEXT             ,"
                    + " COMENTARIO TEXT                      , PORC_DESC_VAR TEXT   , PORC_DESC_FIN TEXT, DESCUENTO_VAR TEXT         ,"
                    + " DESCUENTO_FIN TEXT                   , TOT_DESCUENTO TEXT   , DESC_CLIENTE TEXT , DIAS_INICIAL               ,"
                    + " COD_CONDICION_VENTA TEXT             , NRO_AUTORIZACION TEXT, FEC_ALTA TEXT     , LATITUD TEXT		         ,"
                    + " LONGITUD TEXT						 , IND_PRESENCIAL TEXT  , HORA_ALTA TEXT    , NRO_AUTORIZACION_DESC TEXT ,"
                    + " HORA_REGISTRO DATETIME default (datetime('now','localtime'))) ")
        }
        private fun createTableSvmPedidosDet(): String {
            return ("CREATE TABLE IF NOT EXISTS vt_pedidos_det"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT       , NUMERO NUMBER               , COD_ARTICULO TEXT        ,"
                    + " CANTIDAD NUMBER                      , PRECIO_UNITARIO NUMBER , MONTO_TOTAL NUMBER          , TOTAL_IVA NUMBER         ,"
                    + " ORDEN NUMBER                         , COD_UNIDAD_MEDIDA TEXT , COD_IVA TEXT                , PORC_IVA NUMBER          ,"
                    + " COD_SUCURSAL TEXT                    , COD_ZONA TEXT          , PRECIO_UNITARIO_C_IVA NUMBER, MONTO_TOTAL_CONIVA NUMBER,"
                    + " PORC_DESC_VAR NUMBER                 , DESCUENTO_VAR NUMBER   , PORC_DESC_FIN NUMBER        , DESCUENTO_FIN NUMBER     ,"
                    + " PRECIO_LISTA NUMBER                  , MULT NUMBER            , TIP_COMPROBANTE_REF TEXT    , SER_COMPROBANTE_REF TEXT ,"
                    + " NRO_COMPROBANTE_REF TEXT             , ORDEN_REF TEXT         , IND_SISTEMA TEXT            , IND_TRANSLADO TEXT       ,"
                    + " IND_BLOQUEADO TEXT                   , IND_DEPOSITO TEXT      , EXISTENCIA_ACTUAL TEXT 	    , PROMOCION NUMBER		   ,"
                    + " TIP_PROMOCION TEXT					 , NRO_AUTORIZACION TEXT  , MONTO_DESC_TC TEXT			, NRO_PROMOCION TEXT       ,"
                    + " COD_VENDEDOR TEXT);")
        }

        //SD
        private fun createTableSvmSolicitudDevCab(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_solicitud_dev_cab "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    	, COD_VENDEDOR TEXT 	,"
                    + " NRO_COMPROBANTE TEXT				 , COD_CLIENTE TEXT    	, COD_SUBCLIENTE TEXT 	,"
                    + " NOM_CLIENTE TEXT                     , TOT_COMPROBANTE TEXT	, NRO_PLANILLA TEXT   	,"
                    + " SIGLAS TEXT         				 , OBSERVACIONES TEXT	, EST_ENVIO TEXT 		,"
                    + " NRO_REGISTRO_REF TEXT 				 , FECHA TEXT )")
        }
        private fun createTableSvmSolicitudDevDet(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_solicitud_dev_det"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT	 	, TIP_PLANILLA TEXT	, "
                    + " NRO_PLANILLA TEXT						, SER_COMP TEXT			, TIP_COMP TEXT		, "
                    + " NRO_COMP TEXT							, COD_VENDEDOR TEXT		, COD_CLIENTE		, "
                    + " COD_SUBCLIENTE							, NRO_REGISTRO_REF TEXT	, NRO_ORDEN TEXT	, "
                    + " COD_ARTICULO TEXT						, DESC_ARTICULO TEXT	, CANTIDAD TEXT		, "
                    + " PAGO TEXT								, MONTO TEXT			, TOTAL TEXT		, "
                    + " COD_UNIDAD_REL TEXT						, REFERENCIA TEXT 		, IND_BASICO TEXT	, "
                    + " COD_PENALIDAD TEXT						, GRABADO_CAB TEXT		, EST_ENVIO TEXT 	, "
                    + " FECHA TEXT	)")
        }
        private fun createTableVtMarcacionVisita(): String {
            return ("CREATE TABLE IF NOT EXISTS vt_marcacion_visita "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_SUCURSAL TEXT, "
                    + " COD_CLIENTE TEXT					, COD_SUBCLIENTE TEXT, COD_VENDEDOR TEXT, "
                    + " COD_MOTIVO TEXT  					, OBSERVACION TEXT   , FECHA TEXT	    , "
                    + " LATITUD TEXT	  					, LONGITUD TEXT		 , ESTADO TEXT	    ,"
                    + " HORA_ALTA TEXT		)")
        }
        private fun createTableCcClientesBajaProv():String{
            return ("CREATE TABLE IF NOT EXISTS cc_clientes_baja_prov (" +
                    "       id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "       COD_EMPRESA VARCHAR(5)," +
                    "       COD_VENDEDOR VARCHAR(10)," +
                    "       DESC_VENDEDOR VARCHAR(50)," +
                    "       COD_CLIENTE VARCHAR(20)," +
                    "       COD_SUBCLIENTE VARCHAR(20)," +
                    "       DESC_CLIENTE VARCHAR(100)," +
                    "       DESC_SUBCLIENTE VARCHAR(100)," +
                    "       COMENTARIO VARCHAR(300)," +
                    "       ESTADO CHARACTER DEFAULT 'P'," +
                    "       FOTO_FACHADA BLOB       " +
                    ");")
        }
        fun listaSQLCreateTable(): ArrayList<String> {
            val lista : ArrayList<String> = ArrayList()
            lista.add(0, createTableSvmModificaCatastro())
            lista.add(1, createTableSvmSolicitudDevDet())
            lista.add(2, createTableSvmSolicitudDevCab())
            lista.add(3, createTableVtMarcacionUbicacion())
            lista.add(4, createTableVtMarcacionVisita())
            lista.add(5, createTableSvmPedidosCab())
            lista.add(6, createTableSvmPedidosDet())
            lista.add(7, createTableSvmCatastroCliente())
            lista.add(8, createTableCcClientesBajaProv())
//            lista.add(9, createTableSpmRetornoComentario())
//            lista.add(10, createTableSvmDiasTomaFotoCliente())
//            lista.add(11, )
//            lista.add(12, createTableSvm_fotos_cab())
//            lista.add(13, createTableSvm_fotos_det())
//            lista.add(14, "PRAGMA automatic_index = true")
//            lista.add(15, createTableSvm_imagenes_det())
//            lista.add(16, createTableSvm_fotos_cab())
//            lista.add(17, createTableSvm_fotos_det())
//            lista.add(18, "")
//            lista.add(19, "")
//            lista.add(20, "")
//            lista.add(21, "")
//            lista.add(22, "")
//            lista.add(23, "")
//            lista.add(24, "")
//            lista.add(25, "")
//            lista.add(26, "")
//            lista.add(27, "")
//            lista.add(28, "")
//            lista.add(29, "")
//            lista.add(30, "")

            return lista
        }



        private fun addCodEmpresaSvmTipoCliente():String{
            return ("alter table svm_tipo_cliente add column COD_EMPRESA TEXT;")
        }

        private fun addCodEmpresaSvmLiqComisionVendedor():String{
            return ("alter table svm_liq_comision_vendedor add column COD_EMPRESA TEXT;")
        }

        private fun addCodEmpresaSvmModuloComisionVend():String{
            return ("alter table svm_modulo_comision_vend add column COD_EMPRESA TEXT;")
        }

        private fun addCodPersonaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column COD_PERSONA TEXT;")
        }

        private fun addCantClienteSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column CANT_CLIENTE TEXT;")
        }

        private fun addCliSemanaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column CLI_SEMANA TEXT;")
        }

        private fun addCantVisitaValidaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column CANT_VISITA_VALIDA TEXT;")
        }

        private fun addCantEnParejaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column CANT_EN_PAREJA TEXT;")
        }

        private fun addCantFueraRutaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column CANT_FUERA_RUTA TEXT;")
        }

        private fun addCantVisitaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column CANT_VISITA TEXT;")
        }

        private fun addPorcVisitaValidaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column PORC_VISITA_VALIDA TEXT;")
        }

        private fun addPorcTotalVisitaSvmSegVisitasSemanal():String{
            return ("alter table svm_seg_visitas_semanal add column PORC_TOTAL_VISITA TEXT;")
        }


        fun listaSQLAlterTable(): ArrayList<String> {
            val lista : ArrayList<String> = ArrayList()
            lista.add(0, addCodEmpresaSvmTipoCliente())
            lista.add(1, addCodEmpresaSvmLiqComisionVendedor())
            lista.add(2, addCodEmpresaSvmModuloComisionVend())
            lista.add(3, addCodPersonaSvmSegVisitasSemanal())
            lista.add(4, addCantClienteSvmSegVisitasSemanal())
            lista.add(5, addCliSemanaSvmSegVisitasSemanal())
            lista.add(6, addCantVisitaValidaSvmSegVisitasSemanal())
            lista.add(7, addCantEnParejaSvmSegVisitasSemanal())
            lista.add(8, addCantFueraRutaSvmSegVisitasSemanal())
            lista.add(9, addCantVisitaSvmSegVisitasSemanal())
            lista.add(10, addPorcVisitaValidaSvmSegVisitasSemanal())
            lista.add(11, addPorcTotalVisitaSvmSegVisitasSemanal())


            return lista
        }




        private fun dropVenSvmClienteVendedor():String{
            return ("DROP VIEW IF EXISTS ven_svm_cliente_vendedor;")
        }


        fun listaSQLDropView(): ArrayList<String> {
            val lista : ArrayList<String> = ArrayList()
            lista.add(0, dropVenSvmClienteVendedor())


            return lista
        }


        //VISTAS DE VENDEDORES
        fun venVistaCabecera(tabla:String):String{
            return "CREATE VIEW IF NOT EXISTS ven_" + tabla + " as " +
                    "select DISTINCT B.COD_EMPRESA, A.COD_VENDEDOR, B.DESC_VENDEDOR" +
                    "  FROM $tabla A, svm_vendedor_pedido B" +
                    " WHERE A.COD_EMPRESA = B.COD_EMPRESA " +
                    "   AND A.COD_VENDEDOR= B.COD_VENDEDOR;"
            /*return "CREATE VIEW IF NOT EXISTS ven_" + tabla + " as " +
                    "select DISTINCT A.COD_VENDEDOR, A.COD_VENDEDOR, B.DESC_VENDEDOR" +
                    "  FROM $tabla A, svm_vendedor_pedido B" +
                    " WHERE A.COD_EMPRESA = B.COD_EMPRESA " +
                    "   AND A.COD_VENDEDOR= B.COD_VENDEDOR;"*/
        }

        //INDICES
        fun createIndexSvmClienteVendedor():String{
            return ("CREATE INDEX if not exists ind_svm_cliente_vendedor on svm_cliente_vendedor"
            + "(COD_VENDEDOR, COD_CLIENTE, DESC_CLIENTE, DESC_SUBCLIENTE, DESC_CIUDAD, FEC_VISITA);")
        }
        fun createIndexArticuloPrecioSvmArticulosPrecios():String{
            return ("CREATE INDEX IF NOT EXISTS ind_art_prec on svm_articulos_precios (DESC_ARTICULO, COD_VENDEDOR, COD_LISTA_PRECIO);")
        }
        fun createIndexCodArticuloSvmArticulosPrecios():String{
            return ("CREATE INDEX IF NOT EXISTS IND_COD_ARTICULO ON svm_articulos_precios(COD_ARTICULO) ;" )
        }
        fun createIndexCodArticuloSvmStArticulos():String{
            return ("CREATE INDEX IF NOT EXISTS IND_COD_ARTICULO ON svm_st_articulos(COD_ARTICULO) ;")
        }
        fun createIndexDescArticuloSvmStArticulos():String{
            return ("CREATE INDEX IF NOT EXISTS IND_DESC_ARTICULO ON svm_st_articulos(DESC_ARTICULO) ;")
        }
        fun createIndexCodBarraStArticulos():String{
            return ("CREATE INDEX IF NOT EXISTS IND_COD_BARRA ON svm_st_articulos(COD_BARRA) ;")
        }
//        fun createIndexCodArticuloSurtidoEficiente():String{
//            return ("CREATE INDEX IF NOT EXISTS IND_COD_ARTICULO ON svm_surtido_eficiente(COD_ARTICULO) ;")
//        }
//        fun createIndexCodClienteSurtidoEficiente():String{
//            return ("CREATE INDEX IF NOT EXISTS IND_COD_CLIENTE ON svm_surtido_eficiente(COD_CLIENTE) ;")
//        }
//        fun createIndexCodSubclienteSurtidoEficiente():String{
//            return ("CREATE INDEX IF NOT EXISTS IND_COD_SUBCLIENTE ON svm_surtido_eficiente(COD_SUBCLIENTE) ;")
//        }
//        fun createIndexTipClienteSurtidoEficiente():String{
//            return ("CREATE INDEX IF NOT EXISTS IND_TIP_CLIENTE ON svm_surtido_eficiente(TIP_CLIENTE) ;")
//        }
//        fun createIndexIndSurtidoEficiente():String{
//            return ("CREATE INDEX IF NOT EXISTS IND_SURTIDO_EFICIENTE ON svm_surtido_eficiente(COD_EMPRESA,COD_CLIENTE,COD_SUBCLIENTE,TIP_CLIENTE,COD_ARTICULO) ;")
//        }


    }
}