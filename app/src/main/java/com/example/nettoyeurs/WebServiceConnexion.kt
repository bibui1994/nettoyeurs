package com.example.nettoyeurs

import android.util.Log
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceConnexion(val login: String, val mdpHash: String) {
    val TAG = "WSNewMessage"

    fun call(): ArrayList<String>? {
        if (mdpHash == null || login == null) {
            return null
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
                val status : String = nodeStatus.textContent
                Log.d(TAG, "Thread new connection : status $status")


                if (status.startsWith("OK")){

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}