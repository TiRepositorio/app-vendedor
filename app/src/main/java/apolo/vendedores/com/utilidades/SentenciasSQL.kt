package apolo.vendedores.com.utilidades

import java.util.*

class SentenciasSQL {
    companion object{
        var sql: String = ""


        fun createTableUsuarios(): String{
            sql = "CREATE TABLE IF NOT EXISTS usuarios " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT  , NOMBRE TEXT       , " +
                    " LOGIN TEXT                            , TIPO TEXT         , " +
                    " ACTIVO TEXT                           , COD_EMPRESA TEXT  , " +
                    " VERSION TEXT                          , MIN_FOTOS   TEXT  , " +
                    " MAX_FOTOS TEXT);"
            return sql
        }

        fun insertUsuario(usuario: HashMap<String, String>):String{
            sql = "INSERT INTO usuarios (NOMBRE, LOGIN, TIPO, ACTIVO, COD_EMPRESA, VERSION/*, MIN_FOTOS, MAX_FOTOS*/ ) VALUES " +
                    "('" +  usuario.get("NOMBRE")       + "'," +
                     "'" +  usuario.get("LOGIN")         + "'," +
                     "'" +  usuario.get("TIPO")         + "'," +
                     "'" +  usuario.get("ACTIVO")         + "'," +
                     "'1'," +
                     "'" +  usuario.get("VERSION")      + "'" +
//                     "'" +  usuario.get("MIN_FOTOS")         + "'," +
//                     "'" +  usuario.get("MAX_FOTOS")         + "' " +
                     ")"
            return sql
        }

        fun createTableSvm_cliente_vendedor(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_cliente_vendedor "
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
                    + " TIEMPO_MAX TEXT 					 , SOL_TARG TEXT ); "
                    + " CREATE INDEX if not exists cliente_vendedor on cli_vendedor(COD_VENDEDOR, COD_CLIENTE, DESC_CLIENTE, DESC_SUBCLIENTE, DESC_CIUDAD, FEC_VISITA)")
            return sql
        }

        fun createTableSvm_vendedor_pedido(): String {
            sql = "CREATE TABLE IF NOT EXISTS svm_vendedor_pedido " +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT , COD_EMPRESA TEXT      , COD_VENDEDOR TEXT         ,       " +
                    " IND_PALM TEXT                         , TIPO TEXT             , SERIE TEXT                ,       " +
                    " NUMERO TEXT                           , FECHA TEXT            , ULTIMA_SINCRO TEXT        ,       " +
                    " VERSION TEXT                          , COD_CLI_VEND TEXT     , COD_PERSONA TEXT          ,       " +
                    " RANGO TEXT						    , PER_VENDER TEXT	    , PER_PRESENCIAL TEXT       ,       " +
                    " HORA TEXT							    , MIN_FOTOS TEXT	    , MAX_FOTOS TEXT            ,       " +
                    " VERSION_SISTEMA TEXT 				    , PER_ASISTENCIA TEXT   , FRECUENCIA_RASTREO TEXT   ,       " +
                    " HORA_INICIO TEXT					    , HORA_FIN TEXT         , TIEMPO_ASISTENCIA TEXT    ,       " +
                    " ULTIMA_VEZ TEXT 					    , IND_FOTO TEXT         , MIN_VENTA TEXT            ,       " +
                    " VENT_CON_MARC TEXT 					, PER_MARC_ASIS TEXT );"
            return sql
        }

        fun createTableSvm_articulos_precios(): String {
            sql = "CREATE TABLE IF NOT EXISTS svm_articulos_precios " +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA  TEXT     , COD_VENDEDOR TEXT    , MODULO TEXT      ," +
                    " COD_LISTA_PRECIO TEXT                , COD_ARTICULO TEXT     , DESC_ARTICULO TEXT   , PREC_UNID NUMBER ," +
                    " PREC_CAJA NUMBER                     , COD_UNIDAD_MEDIDA TEXT, PORC_IVA NUMBER      , REFERENCIA TEXT  ," +
                    " MULT NUMBER                          , DIV NUMBER            , IND_LIM_VENTA NUMBER , COD_LINEA TEXT   ," +
                    " COD_FAMILIA TEXT                     , COD_BARRA TEXT        , PORC_DESCUENTO NUMBER,                   " +
                    " CANT_MINIMA NUMBER                   , IND_PROMO_ACT TEXT DEFAULT 'N');" +
                    " CREATE INDEX IF NOT EXISTS IND_COD_ARTICULO ON svm_articulos_precios(COD_ARTICULO) ;" +
                    " CREATE INDEX IF NOT EXISTS ind_art_prec on svm_articulos_precios (DESC_ARTICULO, COD_VENDEDOR, COD_LISTA_PRECIO);"
            return sql
        }

        fun createTableSvm_st_articulos(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_st_articulos"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA  TEXT , COD_ARTICULO TEXT, DESC_ARTICULO TEXT,"
                    + " COD_UNIDAD_REL TEXT                  , REFERENCIA TEXT   , MULT NUMBER      , DIV  NUMBER       ,"
                    + " COD_IVA TEXT                         , PORC_IVA TEXT     , COD_LINEA TEXT   , COD_FAMILIA TEXT  ,"
                    + " IND_BASICO TEXT                      , CANT_MINIMA NUMBER, CANT_EXIST TEXT  , COD_BARRA TEXT);"
                    + " CREATE INDEX IF NOT EXISTS IND_COD_ARTICULO ON svm_st_articulos(COD_ARTICULO) ;"
                    + " CREATE INDEX IF NOT EXISTS IND_DESC_ARTICULO ON svm_st_articulos(DESC_ARTICULO) ;"
                    + " CREATE INDEX IF NOT EXISTS IND_COD_BARRA ON svm_st_articulos(COD_BARRA) ;")
            return sql
        }

        fun createTableSvm_vt_motivos_sd_dev(): String { //SD
            sql = ("CREATE TABLE IF NOT EXISTS svm_motivos_sd_dev"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT		, COD_MOTIVO TEXT	, "
                    + " DESC_MOTIVO TEXT) ")
            return sql
        }






        //No sincronizados
        fun createTableVt_marcacion_ubicacion(): String {
            sql = "CREATE TABLE IF NOT EXISTS vt_marcacion_ubicacion" +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT , COD_EMPRESA TEXT      , FECHA TEXT        , COD_PROMOTOR TEXT, " +
                    "  COD_CLIENTE TEXT   				    , COD_SUBCLIENTE TEXT   , TIPO TEXT  	    , ESTADO TEXT      , " +
                    "  LATITUD TEXT    					    , LONGITUD TEXT	        , OBSERVACION TEXT  , IND_RRHH TEXT DEFAULT 'N'," +
                    "  IND_GC TEXT DEFAULT 'N'              , IND_COORD TEXT DEFAULT 'N'                , IND_SUBSOORD TEXT DEFAULT 'N' );"
            return sql
        }

        fun createTableSvm_fotos_cab(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_fotos_cab "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_PROMOTOR TEXT, COD_CLIENTE TEXT	,"
                    + "  COD_SUBCLIENTE TEXT, ESTADO TEXT    , FECHA_ENTRADA TEXT , HORA_ENTRADA TEXT, LATITUD TEXT	 	,"
                    + "  LONGITUD TEXT						 , FECHA_ENVIO TEXT );")
            return sql
        }

        fun createTableSvm_fotos_det(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_fotos_det "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, ID_CAB INTEGER,  COD_CATEGORIA TEXT, TIPO TEXT,"
                    + "  COMENTARIO TEXT 					 , COD_SEGMENTO TEXT );")
            return sql
        }

        fun createTableSvm_imagenes_det(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_imagenes_det "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, ID_DET INTEGER, IMAGEN BLOB, NRO_ITEM TEXT )")
            return sql
        }

        fun createTableStv_categorias(): String {
            sql = ("CREATE TABLE IF NOT EXISTS stv_categoria"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT		, COD_CATEGORIA TEXT, DESC_CATEGORIA TEXT,"
                    + "	 COD_SEGMENTO TEXT					 , DESC_SEGMENTO TEXT	, ORDEN TEXT		, TIPO TEXT		 	 , "
                    + "  COD_TIP_CLIENTE TEXT)")
            return sql
        }

        fun createTableSvm_retorno_comentario(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_retorno_comentario"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , FEC_ENTRADA TEXT   , FEC_SALIDA TEXT   , "
                    + "  COD_PROMOTOR TEXT					 , COD_CLIENTE TEXT   , COD_SUBCLIENTE TEXT, COD_CATEGORIA TEXT, "
                    + "  COD_SEGMENTO TEXT					 , TIPO TEXT 		  , PUNTUACION TEXT    , COMENTARIO_SUPERVISOR TEXT);")
            return sql
        }

        fun createTableSvm_periodo_foto(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_periodo_foto"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT, COD_TIP_CLIENTE TEXT, FEC_INICIO TEXT,"
                    + "  FEC_FIN TEXT);")
            return sql
        }

        fun createTableSvm_modifica_catastro(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_modifica_catastro"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	, COD_CLIENTE TEXT		,"
                    + " COD_SUBCLIENTE TEXT					 , TELEFONO1 TEXT	, TELEFONO2 TEXT		,"
                    + " DIRECCION TEXT						 , CERCA_DE TEXT	, LATITUD TEXT			,"
                    + " LONGITUD TEXT						 , ESTADO TEXT		, FECHA TEXT		    ,"
                    + " FOTO_FACHADA BLOB		 			 , TIPO TEXT)")
            return sql
        }

        fun createTableSvmPedidosCab(): String {
            sql = ("CREATE TABLE IF NOT EXISTS vt_pedidos_cab"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT     , COD_CLIENTE TEXT  , COD_SUBCLIENTE TEXT        ,"
                    + " COD_VENDEDOR TEXT                    , COD_LISTA_PRECIO TEXT, FECHA TEXT        , FECHA_INT TEXT             ,"
                    + " NUMERO NUMBER                        , NRO_ORDEN_COMPRA TEXT, COD_CONDICION TEXT, TIP_CAMBIO                 ,"
                    + " TOT_COMPROBANTE TEXT                 , ESTADO TEXT          , COD_MONEDA TEXT   , DECIMALES TEXT             ,"
                    + " COMENTARIO TEXT                      , porc_desc_var TEXT   , porc_desc_fin TEXT, descuento_var TEXT         ,"
                    + " descuento_fin TEXT                   , tot_descuento TEXT   , DESC_CLIENTE TEXT , DIAS_INICIAL               ,"
                    + " COD_CONDICION_VENTA TEXT             , NRO_AUTORIZACION TEXT, FEC_ALTA TEXT     , LATITUD TEXT		         ,"
                    + " LONGITUD TEXT						 , IND_PRESENCIAL TEXT  , HORA_ALTA TEXT    , NRO_AUTORIZACION_DESC TEXT ,"
                    + " HORA_REGISTRO DATETIME default (datetime('now','localtime'))) ")
            return sql
        }

        fun renameTablePedidoCab(): String {
            sql = "alter table 'vt_pedidos_cab' rename to 'vt_pedidos_prov';"
            return sql
        }

        fun fillTablePedidoCab(): String {
            sql = ("insert into vt_pedidos_cab "
                    + " (id					, COD_EMPRESA 		, COD_CLIENTE	, COD_SUBCLIENTE		,"
                    + " COD_VENDEDOR		, COD_LISTA_PRECIO	, FECHA			, FECHA_INT				,"
                    + " NUMERO				, NRO_ORDEN_COMPRA	, COD_CONDICION	, TIP_CAMBIO			,"
                    + " TOT_COMPROBANTE		, ESTADO			, COD_MONEDA	, DECIMALES				,"
                    + " COMENTARIO			, porc_desc_var		, porc_desc_fin	, descuento_var			,"
                    + " descuento_fin		, tot_descuento		, DESC_CLIENTE	, DIAS_INICIAL			,"
                    + " COD_CONDICION_VENTA	, NRO_AUTORIZACION	, FEC_ALTA		, LATITUD				,"
                    + " LONGITUD			, IND_PRESENCIAL	, HORA_ALTA		, NRO_AUTORIZACION_DESC)"
                    + " "
                    + " select id			, COD_EMPRESA 		, COD_CLIENTE	, COD_SUBCLIENTE		,"
                    + " COD_VENDEDOR		, COD_LISTA_PRECIO	, FECHA			, FECHA_INT				,"
                    + " NUMERO				, NRO_ORDEN_COMPRA	, COD_CONDICION	, TIP_CAMBIO			,"
                    + " TOT_COMPROBANTE		, ESTADO			, COD_MONEDA	, DECIMALES				,"
                    + " COMENTARIO			, porc_desc_var		, porc_desc_fin	, descuento_var			,"
                    + " descuento_fin		, tot_descuento		, DESC_CLIENTE	, DIAS_INICIAL			,"
                    + " COD_CONDICION_VENTA	, NRO_AUTORIZACION	, FEC_ALTA		, LATITUD				,"
                    + " LONGITUD			, IND_PRESENCIAL	, HORA_ALTA		, NRO_AUTORIZACION_DESC"
                    + " from vt_pedidos_prov;")
            return sql
        }

        fun createTableSvmPedidosDet(): String {
            sql = ("CREATE TABLE IF NOT EXISTS vt_pedidos_det"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT       , NUMERO NUMBER               , COD_ARTICULO TEXT        ,"
                    + " CANTIDAD NUMBER                      , PRECIO_UNITARIO NUMBER , MONTO_TOTAL NUMBER          , TOTAL_IVA NUMBER         ,"
                    + " ORDEN NUMBER                         , COD_UNIDAD_MEDIDA TEXT , COD_IVA TEXT                , PORC_IVA NUMBER          ,"
                    + " COD_SUCURSAL TEXT                    , COD_ZONA TEXT          , PRECIO_UNITARIO_C_IVA NUMBER, MONTO_TOTAL_CONIVA NUMBER,"
                    + " PORC_DESC_VAR NUMBER                 , DESCUENTO_VAR NUMBER   , PORC_DESC_FIN NUMBER        , DESCUENTO_FIN NUMBER     ,"
                    + " PRECIO_LISTA NUMBER                  , MULT NUMBER            , TIP_COMPROBANTE_REF TEXT    , SER_COMPROBANTE_REF TEXT ,"
                    + " NRO_COMPROBANTE_REF TEXT             , ORDEN_REF TEXT         , IND_SISTEMA TEXT            , IND_TRANSLADO TEXT       ,"
                    + " IND_BLOQUEADO TEXT                   , IND_DEPOSITO TEXT      , EXISTENCIA_ACTUAL TEXT 	    , PROMOCION NUMBER		   ,"
                    + " TIP_PROMOCION TEXT					 , NRO_AUTORIZACION TEXT  , MONTO_DESC_TC TEXT			, NRO_PROMOCION TEXT);")
            return sql
        }

        //SD
        fun createTableSvm_solicitud_dev_cab(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_solicitud_dev_cab "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    	, COD_VENDEDOR TEXT 	,"
                    + " NRO_COMPROBANTE TEXT				 , COD_CLIENTE TEXT    	, COD_SUBCLIENTE TEXT 	,"
                    + " NOM_CLIENTE TEXT                     , TOT_COMPROBANTE TEXT	, NRO_PLANILLA TEXT   	,"
                    + " SIGLAS TEXT         				 , OBSERVACIONES TEXT	, EST_ENVIO TEXT 		,"
                    + " NRO_REGISTRO_REF TEXT 				 , FECHA TEXT )")
            return sql
        }

        fun createTableSvm_solicitud_dev_det(): String {
            sql = ("CREATE TABLE IF NOT EXISTS svm_solicitud_dev_det"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT	 	, TIP_PLANILLA TEXT	, "
                    + " NRO_PLANILLA TEXT						, SER_COMP TEXT			, TIP_COMP TEXT		, "
                    + " NRO_COMP TEXT							, COD_VENDEDOR TEXT		, COD_CLIENTE		, "
                    + " COD_SUBCLIENTE							, NRO_REGISTRO_REF TEXT	, NRO_ORDEN TEXT	, "
                    + " COD_ARTICULO TEXT						, DESC_ARTICULO TEXT	, CANTIDAD TEXT		, "
                    + " PAGO TEXT								, MONTO TEXT			, TOTAL TEXT		, "
                    + " COD_UNIDAD_REL TEXT						, REFERENCIA TEXT 		, IND_BASICO TEXT	, "
                    + " COD_PENALIDAD TEXT						, GRABADO_CAB TEXT		, EST_ENVIO TEXT 	, "
                    + " FECHA TEXT	)")
            return sql
        }



        //VISITAS

        //INFORMES


        //NO SINCRONIZADOS




        fun listaSQLCreateTable(): Vector<String> {
            var lista : Vector<String> = Vector<String>()
            lista.add(0, createTableSvm_fotos_cab())
            lista.add(1, createTableSvm_fotos_det())
            lista.add(2, createTableSvm_modifica_catastro())
            lista.add(3, createTableSvmPedidosCab())
            lista.add(4, createTableSvmPedidosDet())
            lista.add(5, createTableSvm_imagenes_det())
            lista.add(6, createTableSvm_fotos_cab())
            lista.add(7, createTableSvm_fotos_det())
            lista.add(8, createTableStv_categorias())
            lista.add(9, createTableSvm_retorno_comentario())
            lista.add(10, createTableSvm_periodo_foto())
            lista.add(11, createTableVt_marcacion_ubicacion())
            lista.add(12, createTableSvm_solicitud_dev_cab())
            lista.add(13, createTableSvm_solicitud_dev_det())
            lista.add(14, "PRAGMA automatic_index = true")
//            lista.add(15, "")
//            lista.add(16, "")
//            lista.add(17, "")
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

    }
}