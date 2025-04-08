import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object HoraInternetHelper {

    private const val NTP_SERVER = "time.google.com"
    private const val TIMEOUT_MS = 3000

    /**
     * Obtiene la hora UTC actual desde un servidor NTP (nunca null)
     */
    fun obtenerHoraUTC(): LocalDateTime {
        val buffer = ByteArray(48)
        buffer[0] = 0b00100011

        return try {
            DatagramSocket().use { socket ->
                val address = InetAddress.getByName(NTP_SERVER)
                val request = DatagramPacket(buffer, buffer.size, address, 123)
                socket.soTimeout = TIMEOUT_MS
                socket.send(request)

                val response = DatagramPacket(buffer, buffer.size)
                socket.receive(response)

                val seconds = ((buffer[40].toLong() and 0xFF) shl 24) or
                        ((buffer[41].toLong() and 0xFF) shl 16) or
                        ((buffer[42].toLong() and 0xFF) shl 8) or
                        (buffer[43].toLong() and 0xFF)

                val epochMillis = (seconds - 2208988800L) * 1000L
                val instant = Instant.ofEpochMilli(epochMillis)

                LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: hora local como UTC si falla la conexión
            LocalDateTime.now(ZoneOffset.UTC)
        }
    }

    /**
     * Devuelve la hora actual desde internet con el offset en horas (por ejemplo: -3 para GMT-3)
     */
    fun obtenerHoraConOffset(offsetHoras: Int): LocalDateTime {
        return obtenerHoraUTC().plusHours(offsetHoras.toLong())
    }
}