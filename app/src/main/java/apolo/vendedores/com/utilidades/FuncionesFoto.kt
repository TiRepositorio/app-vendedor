package apolo.vendedores.com.utilidades

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import java.io.*


class FuncionesFoto(var context: Context) {

    private var foto1 : String = ""
    private var foto2 : String = ""
    private var fotoFachada : String = ""

    fun stringToByte2(imagen: String): ByteArray? {
        var resultado: ByteArray? = null
        try {
            resultado = imagen.toByteArray(charset("ISO-8859-1"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultado
    }

    private fun byteToString2(imagen: ByteArray): String {
        var doc = ""
        try {
            doc = String(imagen, Charsets.ISO_8859_1)
//            doc = String(imagen, "ISO-8859-1")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return doc
    }
    private fun byteToString3(imagen: ByteArray): String {
        var doc = ""
        try {
            doc = Base64.encodeToString(imagen,0)
//            doc = String(imagen, "ISO-8859-1")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return doc
    }

    /*fun resizeImage(ruta_archivo: String?, w: Int, h: Int): Drawable {

        // cargamos la imagen de origen
        val BitmapOrg = BitmapFactory.decodeFile(ruta_archivo)
        val width = BitmapOrg.width
        val height = BitmapOrg.height

        // calculamos el escalado de la imagen destino
        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height

        // para poder manipular la imagen
        // debemos crear una matriz
        val matrix = Matrix()
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight)

        // volvemos a crear la imagen con los nuevos valores
        val resizedBitmap = Bitmap.createBitmap(
            BitmapOrg, 0, 0, width,
            height, matrix, true
        )

        // si queremos poder mostrar nuestra imagen tenemos que crear un
        // objeto drawable y así asignarlo a un botón, imageview...
        return BitmapDrawable(resizedBitmap)
    }
*/
    private fun resizeImage(img: Bitmap, w: Int, h: Int): Bitmap? {
        val width = img.width
        val height = img.height

        // calculamos el escalado de la imagen destino
        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height

        // para poder manipular la imagen
        // debemos crear una matriz
        val matrix = Matrix()
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight)

        // volvemos a crear la imagen con los nuevos valores
        /*if (resizedBitmap == img) {
            "son iguales"
        }*/

        // si queremos poder mostrar nuestra imagen tenemos que crear un
        // objeto drawable y así asignarlo a un botón, imageview...
        return Bitmap.createBitmap(
            img, 0, 0, width, height,
            matrix, true
        )
    }

    @Throws(IOException::class)
    fun readBytes(inputStream: InputStream): ByteArray? {

        // this dynamically extends to take the bytes you read

        // InputStream inputStream = getContentResolver().openInputStream(uri);
        val byteBuffer = ByteArrayOutputStream()

        // this is storage overwritten on each iteration with bytes
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        // we need to know how may bytes were read to write them to the
        // byteBuffer
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray()
    }

    fun foto1 (
        requestCode: Int,
        nombre: String,
        ivFachada: ImageView,
        tipoFoto: String
    ):String{
        if (tipoFoto == "1") {
            if (requestCode == 1) {
                try {
                    if (File(nombre).exists()) {
                        try {
                            val options = BitmapFactory.Options()
                            options.inSampleSize = 8
                            val fis = FileInputStream(nombre)
                            var bm = BitmapFactory.decodeStream(fis, null, options)
                            options.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(nombre, options)
                            val w: Int = options.outWidth
                            val h: Int = options.outHeight
                            bm = if (w < h) {
                                val y = h.toFloat() / 768
                                resizeImage(
                                    bm!!, (w / y).toInt(), (h / y).toInt()
                                )
                            } else {
                                val x = w.toFloat() / 768
                                resizeImage(bm!!, (w / x).toInt(), (h / x).toInt())
                            }
                            var out: FileOutputStream? = null
                            try {
                                out = FileOutputStream(nombre)
                                bm!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
                                // bmp is your Bitmap instance
                                // PNG is a lossless format, the compression factor (100) is ignored
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            } finally {
                                try {
                                    out?.close()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }

//									Bitmap bm = BitmapFactory.decodeFile(name);
                            ivFachada.setImageBitmap(bm)
                        } catch (e2: java.lang.Exception) {
                            e2.message
                        }
                        val output =
                            Uri.fromFile(File(nombre))
                        val inputStream: InputStream? = context.contentResolver.openInputStream(output)
                        val imagen: ByteArray? = readBytes(inputStream!!)
                        val strImagen: String = byteToString3(imagen!!)
                        fotoFachada = strImagen
                        File(nombre).delete()
                        return strImagen
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    return ""
                }
            }
        }
        return ""
    }

    fun foto2 (
        requestCode: Int,
        data: Intent?,
        ivFoto1: ImageView,
        ivFoto2: ImageView,
        nombre: String,
        tipoFoto: String
    ) : String{
        if (tipoFoto == "2") {
            if (requestCode == 1) {
                if (data == null) {
                    val iv: ImageView = if (nombre.contains("/foto1.jpg")) { ivFoto1 } else { ivFoto2 }
                    try {
                        if (File(nombre).exists()) {
                            try {
                                val options = BitmapFactory.Options()
                                options.inSampleSize = 8
                                val fis = FileInputStream(nombre)
                                var bm = BitmapFactory.decodeStream(fis, null, options)
                                options.inJustDecodeBounds = true
                                BitmapFactory.decodeFile(nombre, options)
                                val w: Int = options.outWidth
                                val h: Int = options.outHeight
                                bm = if (w < h) {
                                    val y = h.toFloat() / 768
                                    resizeImage(bm!!, (w / y).toInt(), (h / y).toInt())
                                } else {
                                    val x = w.toFloat() / 768
                                    resizeImage(bm!!, (w / x).toInt(), (h / x).toInt())
                                }
                                var out: FileOutputStream? = null
                                try {
                                    out = FileOutputStream(nombre)
                                    bm!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
                                    // bmp is your Bitmap instance
                                    // PNG is a lossless format, the compression factor (100) is ignored
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                } finally {
                                    try {
                                        out?.close()
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }

//									Bitmap bm = BitmapFactory.decodeFile(name);
                                iv.setImageBitmap(bm)
                            } catch (e2: java.lang.Exception) {
                                e2.message
                            }
                            val output = Uri.fromFile(File(nombre))
                            val inputStream: InputStream? = context.contentResolver.openInputStream(output)
                            val imagen: ByteArray? = readBytes(inputStream!!)
                            val strImagen: String = byteToString2(imagen!!)
                            if (nombre.contains("/foto1.jpg")) {
                                foto1 = strImagen
                            } else {
                                foto2 = strImagen
                            }
                            File(nombre).delete()
                            return strImagen
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        return ""
                    }
                }
            }
        }
        return ""
    }


}