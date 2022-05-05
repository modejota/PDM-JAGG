package com.modejota.unitcardgame

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Parcelable
import android.widget.Toast

class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager?,
    private val channel: WifiP2pManager.Channel?,
    private val activity: MainActivity
   // Por lo visto, no se recomienda pasar actividades, por leaks de memoria.
   // En su lugar deberían pasarse contextos, según la documentación.
) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                if (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1) != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(context, "WiFi Direct is not enabled", Toast.LENGTH_SHORT).show()
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Toast.makeText(context, "Peers changed", Toast.LENGTH_SHORT).show()
                manager?.requestPeers(channel, activity.peerListListener)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Toast.makeText(context, "Connection changed", Toast.LENGTH_SHORT).show()
                manager?.let { manager ->
                    // Es curioso que la propia documentación de Google te proporcione métodos deprecated
                    val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo?
                    if (networkInfo?.isConnected == true) {
                        // We are connected with the other device, request connection  info to find group owner IP
                        manager.requestConnectionInfo(channel, activity.connectionListener)
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Toast.makeText(context, "This device changed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}