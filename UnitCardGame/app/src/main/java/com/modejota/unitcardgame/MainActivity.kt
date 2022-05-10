package com.modejota.unitcardgame

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.LinkProperties
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import com.modejota.unitcardgame.clientstuff.Jugador
import com.modejota.unitcardgame.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var iAmServer = false
    private var jugador: Jugador? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createPartyButton.setOnClickListener {
            val qrCode = getQrCodeBitmap(getIpAddress())
            iAmServer = true
            ShowQrDialog(qrCode, onSubmitClickListener = {
                jugador = Jugador(getIpAddress(), this)
                jugador!!.crearPartida(2)
            }).show(supportFragmentManager, "QR_DIALOG")
        }

        binding.joinPartyButton.setOnClickListener {
            // Camera permissions are handled automatically when initialized.
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan a QR code to join a party.")
            integrator.setBeepEnabled(true)
            integrator.initiateScan()
        }
    }

    @Deprecated("Deprecated in Java")
    // Manage the result of the QRScanner, which calls "startActivityForResult" under the hood
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // Create a dialog with a button to join the party.
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Join party")
                dialog.setMessage("Do you want to join the party?")
                dialog.setPositiveButton("Yes") { _, _ ->
                    jugador = Jugador(getIPAddressFromQRCode(result.contents),this)
                    jugador!!.unirseAPartida()
                }
                dialog.setNegativeButton("No") { _, _ ->
                    // Cancel the join request. No need to do nothing.
                }
                dialog.show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
            val link: LinkProperties =
                connectivityManager.getLinkProperties(connectivityManager.activeNetwork) as LinkProperties
            for (address in link.linkAddresses) {
                if (address.address is java.net.Inet4Address) {
                    // Check this further
                    //Toast.makeText(this, address.address.hostAddress, Toast.LENGTH_SHORT).show()
                    return address.address.toString().dropLast(3)
                }
                //return link.linkAddresses[1].toString().dropLast(3) // Primero viene la IPv6, luego la IPv4. Quitamos la máscara.
            }
        }
        return ""
    }

    private fun getIPAddressFromQRCode(text: String): String = text.split(",")[1].split(":")[1]


    // Podría usarse para obtener el nombre del dispositivo y mostrarlo en el dialogo de confiramación.
    /*
    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else {
            manufacturer
        }
    }
    */
}