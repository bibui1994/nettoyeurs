package com.example.nettoyeurs

import android.util.Log
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceConnexion(val login: String, val mdpHash: String) {
    val TAG = "WSNewMessage"

    fun call(): Boolean {
        if (mdpHash == null || login == null) {
            return false
        } else {
            println("login $login passwd $mdpHash  !")
            try {
                val url =
                    URL("http://51.68.124.144/nettoyeurs_srv/connexion.php?login=$login&passwd=$mdpHash")
                val cnx = url.openConnection()
                println("IT MUST WORK !!!!")
                val `in` = cnx.getInputStream()
                println("IT MUST WORK nowwwww!!!!")
                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()

                val xml = db.parse(`in`)
                val nl = xml.getElementsByTagName("STATUS")
                val nodeStatus = nl.item(0)
                val status = nodeStatus.textContent
                Log.d(TAG, "Thread new connection : status $status")
                status.startsWith("OK")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
    }
}