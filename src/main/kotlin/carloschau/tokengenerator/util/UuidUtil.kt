package carloschau.tokengenerator.util
import org.bson.types.Binary
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.experimental.and


object UuidUtil {
    private  val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun fromStringWithoutDash(name: String) : UUID
    {
        val uuidWithDash = name.replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)".toRegex()
                                                , "$1-$2-$3-$4-$5" )
        return UUID.fromString(uuidWithDash)
    }

    fun toBytes(uuid : UUID) : ByteArray
    {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }


    /**
     * Convert a UUID object to a Binary with a subtype 0x04
     */
    fun toStandardBinaryUUID(uuid: UUID): Binary {
        var msb = uuid.mostSignificantBits
        var lsb = uuid.leastSignificantBits

        val uuidBytes = ByteArray(16)

        for (i in 15 downTo 8) {
            uuidBytes[i] = (lsb and 0xFFL).toByte()
            lsb = lsb shr 8
        }

        for (i in 7 downTo 0) {
            uuidBytes[i] = (msb and 0xFFL).toByte()
            msb = msb shr 8
        }

        return Binary(0x04.toByte(), uuidBytes)
    }

    /**
     * Convert a Binary with a subtype 0x04 to a UUID object
     * Please note: the subtype is not being checked.
     */
    fun fromStandardBinaryUUID(binary: Binary): UUID {
        var msb: Long = 0
        var lsb: Long = 0
        val uuidBytes = binary.data

        for (i in 8..15) {
            lsb = lsb shl 8
            lsb = lsb or ((uuidBytes[i] and 0xFFL.toByte()).toLong())
        }

        for (i in 0..7) {
            msb = msb shl 8
            msb = msb or ((uuidBytes[i] and 0xFFL.toByte()).toLong())
        }

        return UUID(msb, lsb)
    }
}