package org.oecd.qrcontact

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keep screen bright so QR code is easy to scan
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val qrImageView = findViewById<ImageView>(R.id.qrCodeImage)
        val nameText = findViewById<TextView>(R.id.nameText)
        val titleText = findViewById<TextView>(R.id.titleText)
        val orgText = findViewById<TextView>(R.id.orgText)

        nameText.text = "Alejandro Guerrero-Ruiz"
        titleText.text = "Head, OECD Development Impact"
        orgText.text = "Development Co-operation Directorate"

        val vcard = buildVCard()
        val bitmap = generateQRCode(vcard, 800)
        qrImageView.setImageBitmap(bitmap)
    }

    private fun buildVCard(): String {
        return """
            BEGIN:VCARD
            VERSION:3.0
            N:Guerrero-Ruiz;Alejandro;;;
            FN:Alejandro Guerrero-Ruiz
            ORG:OECD;Development Co-operation Directorate
            TITLE:Head, OECD Development Impact - Reforms and Partnerships for Development Impact Division
            ADR;TYPE=WORK:;;2, rue André Pascal;Paris;;75775 Cedex 16;France
            TEL;TYPE=WORK,VOICE:+33145248363
            TEL;TYPE=CELL:+34616367846
            EMAIL;TYPE=WORK:Alejandro.GUERRERO-RUIZ@oecd.org
            URL:https://www.oecd.org
            END:VCARD
        """.trimIndent()
    }

    private fun generateQRCode(content: String, size: Int): Bitmap {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 2
        )

        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }
}
