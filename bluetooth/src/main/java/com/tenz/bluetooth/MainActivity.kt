package com.tenz.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tenz.bluetooth.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mBluetoothDeviceAdapter: BluetoothDeviceAdapter
    private var mBluetoothDeviceList: ArrayList<BluetoothDevice> = arrayListOf()
    private var isScanningBluetooth = false
    private lateinit var mBluetoothLeScanner: BluetoothLeScanner
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                addBluetoothDeviceList(it.device)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addBluetoothDeviceList(bluetoothDevice: BluetoothDevice) {
        var exist = false
        for (device in mBluetoothDeviceList) {
            //为了美观，剔除名称为空的设备
            if (bluetoothDevice.name.isNullOrBlank() || bluetoothDevice.address.equals(device.address)) {
                exist = true
            }
        }
        if (!exist) {
            mBluetoothDeviceList.add(bluetoothDevice)
        }
        mBluetoothDeviceAdapter.notifyDataSetChanged()
    }

    //打开蓝牙意图
    private var enableBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            if (isOpenBluetooth()) {
                mBluetoothAdapter = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
                mBluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
                showMsg("bluetooth has opened")
            } else {
                showMsg("bluetooth is not open")
            }

        }
    }
    //请求BLUETOOTH_CONNECT权限意图
    private val requestBluetoothConnect = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) {
            //打开蓝牙
            enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            showMsg("If this permission is not obtained in Android 12, Bluetooth cannot be turned on")
        }
    }
    //请求BLUETOOTH_SCAN权限意图
    private val requestBluetoothScan = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) {
            //扫描蓝牙
            startScanBluetooth()
        } else {
            showMsg("If you don't get this permission in Android 12, you can't scan Bluetooth")
        }
    }
    //请求定位权限意图
    private val requestLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) {
            //扫描蓝牙
            startScanBluetooth()
        } else {
            showMsg("Android 12 and below, 6 and above require location permission to scan devices")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvBluetooth.layoutManager = LinearLayoutManager(this)
        mBluetoothDeviceAdapter = BluetoothDeviceAdapter(this, mBluetoothDeviceList)
        binding.rvBluetooth.adapter = mBluetoothDeviceAdapter

        if (isOpenBluetooth()) {
            mBluetoothAdapter = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
            mBluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
        }
        binding.btnOpenBluetooth.setOnClickListener {
            //蓝牙是否已打
            if (isOpenBluetooth()) {
                mBluetoothAdapter = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
                mBluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
                showMsg("bluetooth has opened")
                return@setOnClickListener
            }
            //是Android12
            if (isAndroid12()) {
                //检查是否有BLUETOOTH_CONNECT权限
                if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    //打开蓝牙
                    enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                } else {
                    //请求权限
                    requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }
                return@setOnClickListener
            }
            //不是Android12 直接打开蓝牙
            enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
        binding.btnScanBluetooth.setOnClickListener {
            if (isAndroid12()) {
                if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                    //扫描或者停止扫描
                    if (isScanningBluetooth) stopScanBluetooth() else startScanBluetooth()
                } else {
                    //请求权限
                    requestBluetoothScan.launch(Manifest.permission.BLUETOOTH_SCAN)
                }
            } else {
                if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //开始扫描
                    if (isScanningBluetooth) stopScanBluetooth() else startScanBluetooth()
                } else {
                    requestLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

        }
    }

    private fun startScanBluetooth() {
        if (!isScanningBluetooth) {
            mBluetoothLeScanner.startScan(mScanCallback)
            isScanningBluetooth = true
            binding.btnScanBluetooth.text = "stop scan bluetooth"
        }
    }

    private fun stopScanBluetooth() {
        if (isScanningBluetooth) {
            mBluetoothLeScanner.stopScan(mScanCallback)
            isScanningBluetooth = false
            binding.btnScanBluetooth.text = "scan bluetooth"
        }
    }

    private fun isOpenBluetooth(): Boolean {
        val manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter ?: return false
        return adapter.isEnabled
    }

    private fun isAndroid12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    @SuppressLint("NewApi")
    private fun hasPermission(permission: String) =
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    private fun showMsg(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}