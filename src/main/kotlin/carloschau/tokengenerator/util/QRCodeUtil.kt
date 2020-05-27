package carloschau.tokengenerator.util

import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import java.io.File

object QRCodeUtil {
    fun generateQRCode(content: String): ByteArray {
        return QRCode.from(content).to(ImageType.PNG).file()
                .readBytes()
    }
}