package com.modejota.unitcardgame

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
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

    private val peers: MutableList<WifiP2pDevice> = ArrayList()

    @SuppressLint("MissingPermission")
    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        // Toda la lógica de la conexión por lo visto no debería ir aquí, ya que esto se refresca cada X tiempo.
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
            Toast.makeText(this, "Peers: ${peers.size}", Toast.LENGTH_SHORT).show()

            if (peers.isNotEmpty()) {
                connect(peers.first())
            }

            if (peers.isEmpty()) {
                Toast.makeText(this@MainActivity, "No devices found", Toast.LENGTH_SHORT).show()
                return@PeerListListener
            }
        }
    }
    val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        // After the group negotiation, we can determine the group owner (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
            TODO("Lanzar hebra que accepta las conexiones y tal")
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
            TODO("Lanzar la hebra que envia las jugadas del cliente")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        if (!initP2p()) { finish() }

        askForAccessFineLocationPermission()

        binding.discoverPeersButton.setOnClickListener {
            askForAccessFineLocationPermission()
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(this@MainActivity,"Discovery Initiated",
                        Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(reasonCode: Int) {
                    Toast.makeText(this@MainActivity, "Discovery Failed : $reasonCode",
                        Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.cancelConnectionButton.setOnClickListener {
            disconnect()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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
    // Si no se pide permiso explícitamente, el linter se queja, a pesar de que llamemos a una función que contiene exactamente el mismo código.
    fun connect(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }
        askForAccessFineLocationPermission()
        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "Connected to ${device.deviceName}", Toast.LENGTH_SHORT).show()
                if (device.deviceName == "Host") {
                    Toast.makeText(this@MainActivity, "Host", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Client", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Failed to connect to ${device.deviceName}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun disconnect(){
        manager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "Disconnected", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Failed to disconnect", Toast.LENGTH_SHORT).show()
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