package com.modejota.unitcardgame

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.modejota.unitcardgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), WifiP2pManager.ChannelListener {

    private lateinit var binding: ActivityMainBinding

    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: WiFiDirectBroadcastReceiver? = null
    private val intentFilter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        if (!initP2p()) {
            finish()
        }

        askForAccessFineLocationPermission()

        /*
        binding.createMatch.setOnClickListener {
            val fragment = DeviceListFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.device_row, fragment).commit()
        }
        */
    }

    private fun askForAccessFineLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 21)
        }
    }

    override fun onResume() {
        super.onResume()
        receiver = WiFiDirectBroadcastReceiver(manager, channel, this)
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() and (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onChannelDisconnected() {
        if (manager != null) {
            Toast.makeText(this, "Channel lost.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    // Si no se pide permiso explícitamente, el linter se queja.
    // A pesar de que llamemos a una función que contiene exactamente el mismo código.
    private fun connect(config: WifiP2pConfig?) {
        askForAccessFineLocationPermission()
        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Connection failed. Retry.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initP2p(): Boolean {
        // Device capability definition check
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Toast.makeText(this, "Wi-Fi Direct is not supported by this device.", Toast.LENGTH_SHORT).show()
            return false
        }
        // Hardware capability check
        val wifiManager: WifiManager? = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        if (wifiManager == null) {
            Toast.makeText(this, "Cannot get Wi-Fi system service.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!wifiManager.isP2pSupported) {
            Toast.makeText(this, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.", Toast.LENGTH_SHORT).show()
            return false
        }
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        if (manager == null) {
            Toast.makeText(this,"Cannot get Wi-Fi Direct system service.", Toast.LENGTH_SHORT).show()
            return false
        }
        channel = manager!!.initialize(this, mainLooper, null)
        if (channel == null) {
            Toast.makeText(this, "Cannot initialize Wi-Fi Direct.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}