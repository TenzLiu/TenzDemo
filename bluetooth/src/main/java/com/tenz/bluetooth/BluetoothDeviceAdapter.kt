package com.tenz.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BluetoothDeviceAdapter(context: Context, dataList: ArrayList<BluetoothDevice>):
    RecyclerView.Adapter<BluetoothDeviceAdapter.MyViewHolder>() {

    private var mContext: Context = context
    private var mDataList: ArrayList<BluetoothDevice> = dataList

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvAddress: TextView = itemView.findViewById(R.id.tv_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth_device, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bluetoothDevice = mDataList[position]
        holder.tvName.text = if (bluetoothDevice.name.isNullOrBlank()) "UnKnown" else bluetoothDevice.name
        holder.tvAddress.text = bluetoothDevice.address
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

}