package com.modejota.unitcardgame

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.ListFragment

class DeviceListFragment: ListFragment(), WifiP2pManager.PeerListListener {

    private val peers: MutableList<WifiP2pDevice> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //this.listAdapter = DeviceListAdapter(requireActivity(), peers)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val device = listAdapter?.getItem(position) as WifiP2pDevice
        Toast.makeText(activity, "Connecting to " + device.deviceName, Toast.LENGTH_SHORT).show()
        //(activity as DeviceActionListener).connect(device)
    }

    override fun onPeersAvailable(peerList: WifiP2pDeviceList) {
        peers.clear()
        peers.addAll(peerList.deviceList)
        if (peers.isEmpty()) {
            Toast.makeText(activity, "No devices found", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun getDeviceStatus(deviceStatus: Int): String {
        return when (deviceStatus) {
            WifiP2pDevice.AVAILABLE -> "Available"
            WifiP2pDevice.INVITED -> "Invited"
            WifiP2pDevice.CONNECTED -> "Connected"
            WifiP2pDevice.FAILED -> "Failed"
            WifiP2pDevice.UNAVAILABLE -> "Unavailable"
            else -> "Unknown"
        }
    }

}