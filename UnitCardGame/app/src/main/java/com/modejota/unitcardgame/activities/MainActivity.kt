package com.modejota.unitcardgame.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import com.modejota.unitcardgame.otherstuff.ShowQrDialog
import com.modejota.unitcardgame.clientstuff.Jugador
import com.modejota.unitcardgame.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var jugador: Jugador? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hay que asegurarse de que haya conexión WIFI. -> Si me da tiempo, botón para activar o redirigir.
        // Si no, devuelve una IP 0.0.0.0 de tapadillo, no levanta error.
        // RECORDATORIO: no lo puedo probar entre emulador y dispositivo, porque son subredes diferentes.

        binding.createPartyButton.setOnClickListener {
            val qrCode = getQrCodeBitmap(getIpAddress()!!)
            ShowQrDialog(qrCode, onSubmitClickListener = {

                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("IP", getIpAddress())
                intent.putExtra("AM_I_SERVER", true)

                startActivity(intent)

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

                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra("IP", getIPAddressFromQRCode(result.contents))
                    intent.putExtra("AM_I_SERVER", false)

                    startActivity(intent)

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

    private fun getIpAddress(): String? {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        if (connectivityManager is ConnectivityManager) {
            val link: LinkProperties =
                connectivityManager.getLinkProperties(connectivityManager.activeNetwork) as LinkProperties
            for (address in link.linkAddresses) {
                if (address.address is java.net.Inet4Address) {
                    // Return the first IPv4 address found.
                    return address.address.hostAddress
                }
            }
        }
        return ""
    }

    private fun getIPAddressFromQRCode(text: String): String = text.split(",")[0].split(":")[1]


    // Podría usarse para obtener el nombre del dispositivo y mostrarlo en el dialogo de confiramación.
    // Implementar si me da tiempo, hay que tener ojo porque cada fabricante rellena esto como quiere.
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