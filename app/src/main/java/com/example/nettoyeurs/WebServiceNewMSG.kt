package com.example.nettoyeurs

import android.util.Log
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceNewMSG(private val session: Int, private val signature: Long, private val message: String) {

    private val TAG = "WSNewMessage"
    private var mContenu: String? = null



    fun call(): Boolean {
        try {
            mContenu = URLEncoder.encode(message, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return if (mContenu == null) false else try {

            val url = URL("http://51.68.124.144/nettoyeurs_srv/new_msg.php?session=$session&signature=$signature&message=$mContenu")
            val cnx = url.openConnection()
            val `in` = cnx.getInputStream()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val xml = db.parse(`in`)
            val nl = xml.getElementsByTagName("STATUS")
            val nodeStatus = nl.item(0)
            val status = nodeStatus.textContent
            Log.d(TAG, "Thread new msg : status $status")
            status.startsWith("OK")
        } catch (e: Exception) {
            false
        }
    }
}