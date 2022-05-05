package com.modejota.unitcardgame

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.modejota.unitcardgame.databinding.RowDeviceBinding

class DeviceAdapter(private val devices: MutableList<WifiP2pDevice>):
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = RowDeviceBinding.bind(itemView)

        fun render(device: WifiP2pDevice) {
            binding.deviceName.text = device.deviceName
            binding.deviceAddress.text = device.deviceAddress
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.row_device, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(devices[position])
    }

    override fun getItemCount(): Int = devices.size

}