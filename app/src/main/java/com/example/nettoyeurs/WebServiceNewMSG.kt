package com.example.nettoyeurs

import android.util.Log
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceNewMSG {

    val TAG = "WSNewMessage"
    private var mAuteur: String? = null
    private var mContenu: String? = null

    fun WebServiceNewMSG(auteur: String?, contenu: String?) {
        try {
            mAuteur = URLEncoder.encode(auteur, "UTF-8")
            mContenu = URLEncoder.encode(contenu, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    fun call(): Boolean {
        return if (mContenu == null) false else try {
            val url = URL("http://51.68.124.144/ws_chat/new_msg.php?author=$mAuteur&msg=$mContenu")
            val cnx = url.openConnection()
            val `in` = cnx.getInputStream()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val xml = db.parse(`in`)
            val nl = xml.getElementsByTagName("STATUS")
            val nodeStatus = nl.item(0)
            val status = nodeStatus.textContent
            Log.d(TAG, "Thread last msg : status $status")
            status.startsWith("OK")
        } catch (e: Exception) {
            false
        }
    }
}