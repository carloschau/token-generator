package carloschau.tokengenerator.util

import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import java.io.File

object QRCodeUtil {
    fun generateQRCode(content: String, size: Int?): ByteArray {

        return QRCode
                .from(content)
                .let { if (size != null) setQRCodeSize(it, size) else setQRCodeSize(it) }
                .to(ImageType.PNG).file()
                .readBytes()
    }

    private fun setQRCodeSize(qrCode: QRCode, size: Int = 250) = qrCode.withSize(size, size)
}