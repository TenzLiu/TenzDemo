package com.tenz.nfc

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var tvCardId: TextView? = null
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    @SuppressLint("MissingSuperCall", "UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvCardId = findViewById(R.id.tv_card_id)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        if (nfcAdapter == null) {
            Toast.makeText(this, "设备不支持NFC", Toast.LENGTH_SHORT).show()
            return
        }
        if (nfcAdapter?.isEnabled != true) {
            Toast.makeText(this, "请开启NFC功能", Toast.LENGTH_SHORT).show()
            return
        }
        onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let {
            it.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.let {
            it.disableForegroundDispatch(this)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)?.let {
            processTag(it)
        }
    }

    private fun processTag(tag: Tag) {
        val byteIds = tag.id
        tvCardId?.text = byteArrayToHexString(byteIds)
    }

    /**
     * 字节数组转换十六进制
     */
    private fun byteArrayToHexString(byteArray: ByteArray): String? {
        var i = 0
        var j = 0
        var input = 0
        val hex = arrayOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
            "B", "C", "D", "E", "F"
        )
        var outPut = ""
        j = 0
        while (j < byteArray.size) {
            input = byteArray[j].toInt() and 0xff
            i = input shr 4 and 0x0f
            outPut += hex[i]
            i = input and 0x0f
            outPut += hex[i]
            ++j
        }
        return outPut
    }


}