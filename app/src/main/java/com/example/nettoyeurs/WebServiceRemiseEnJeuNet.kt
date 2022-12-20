package com.example.nettoyeurs

import android.util.Log
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceRemiseEnJeuNet(val session: Int,val signature: Long, val lon:Int, val lat:Int) {

    val TAG = "WSRemiseEnJeuNettoyeur"

    fun call(): Boolean {
        return if (session == null || signature == null || lon == null || lat == null) false
        else try {
                val url =
                    URL("http://51.68.124.144/nettoyeurs_srv/remise_en_jeu.php?session=$session&signature=$signature&lon=$lon&lat=$lat")
                val cnx = url.openConnection()
                val `in` = cnx.getInputStream()
                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()

                val xml = db.parse(`in`)
                val nlStatus : NodeList = xml.getElementsByTagName("STATUS")
                val nodeStatus : Node = nlStatus.item(0)
                val status : String = nodeStatus.textContent
                Log.d(TAG, "Thread remise en jeu du nettoyeur  : status $status")
                status.startsWith("OK")

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}