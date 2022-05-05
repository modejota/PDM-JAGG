package com.modejota.unitcardgame

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.ListFragment

class DeviceListFragment: ListFragment(), WifiP2pManager.PeerListListener {

    private val peers: MutableList<WifiP2pDevice> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.listAdapter = DeviceListAdapter(requireActivity(), R.layout.row_device, peers)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val device = listAdapter?.getItem(position) as WifiP2pDevice
        Toast.makeText(activity, "Connecting to " + device.deviceName, Toast.LENGTH_SHORT).show()
        //(activity as MainActivity).connect(device.deviceAddress)
    }

    override fun onPeersAvailable(peerList: WifiP2pDeviceList) {
        peers.clear()
        peers.addAll(peerList.deviceList)
        if (peers.isEmpty()) {
            Toast.makeText(activity, "No devices found", Toast.LENGTH_SHORT).show()
            return
        }
    }

    inner class DeviceListAdapter(context: Context?,
                                          textViewResourceId: Int,
                                          private val items: List<WifiP2pDevice>) :
        ArrayAdapter<WifiP2pDevice>(context!!, textViewResourceId, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var v = convertView
                if (v == null) {
                    v = LayoutInflater.from(context).inflate(R.layout.row_device, null)
                }
                val device = items[position]
                val deviceName = v?.findViewById<View>(R.id.device_name)
                val deviceAddress = v?.findViewById<View>(R.id.device_address)
                (deviceName as TextView).text = device.deviceName
                (deviceAddress as TextView).text = device.deviceAddress
                return v!!
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