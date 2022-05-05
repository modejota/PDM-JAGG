package com.modejota.unitcardgame

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
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
                manager?.requestPeers(channel, activity.supportFragmentManager.findFragmentByTag("DeviceListFragment") as WifiP2pManager.PeerListListener)
                Toast.makeText(context, "Peers changed", Toast.LENGTH_SHORT).show()
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Toast.makeText(context, "Connection changed", Toast.LENGTH_SHORT).show()
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Toast.makeText(context, "This device changed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}