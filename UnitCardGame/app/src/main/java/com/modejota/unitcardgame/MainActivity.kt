package com.modejota.unitcardgame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.LinkProperties
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.modejota.unitcardgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createPartyButton.setOnClickListener {
            // val qrCode = getQrCodeBitmap(getIpAddress())
            // Set the QRCode to be shown in the UI among the confirmation button.
            // binding.imageView.setImageBitmap(qrCode)
        }

        binding.joinPartyButton.setOnClickListener {
            // Open the QR code scanner to scan the QR code.


        }
    }

    private fun getQrCodeBitmap(hostIpAddress: String): Bitmap {
        val size = 512  // QR code size in pixels
        val qrCodeContent = "HOST:$hostIpAddress,PORT:9029"
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    private fun getIpAddress(): String {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        if (connectivityManager is ConnectivityManager) {
            val link: LinkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork) as LinkProperties
            return link.linkAddresses[1].toString().dropLast(3) // Primero viene la IPv6, luego la IPv4. Quitamos la máscara.
        }
        return ""
    }

}