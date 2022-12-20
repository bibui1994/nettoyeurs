package com.example.nettoyeurs

import android.util.Log
import org.w3c.dom.Node
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceLastMSG(val session: Int,val signature: Long) {

    val TAG = "WSLastMessages"

    /// à ne pas exécuter dans le thread principal
    fun call(): ArrayList<Message>? {
        return try {
            val url = URL("http://51.68.124.144/nettoyeurs_srv/last_msgs.php?session=$session&signature=$signature")
            val cnx = url.openConnection()
            val `in` = cnx.getInputStream()
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val xml = db.parse(`in`)
            var nl = xml.getElementsByTagName("STATUS")
            val nodeStatus = nl.item(0)
            val status = nodeStatus.textContent
            Log.d(TAG, "Thread last msg : status $status")

            if (!status.startsWith("OK")) return null
            nl = xml.getElementsByTagName("CONTENT")
            val nodeContent = nl.item(0)
            val messagesXML = nodeContent.childNodes
            val aAjouter = ArrayList<Message>()

            for (i in 0 until messagesXML.length) {
                val message = messagesXML.item(i)
                aAjouter.add(parseMessage(message)!!)
            }
            aAjouter
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun parseMessage(msgNode: Node): Message? {
        var id = -1
        var auteur: String? = null
        var contenu: String? = null
        var stringDate: String? = null
        val messageFields = msgNode.childNodes
        for (j in 0 until messageFields.length) {
            val field = messageFields.item(j)
            if (field.nodeName.equals("ID", ignoreCase = true)) id =
                field.textContent.toInt() else if (field.nodeName.equals("DATESENT",
                    ignoreCase = true)
            ) stringDate = field.textContent else if (field.nodeName.equals("AUTHOR",
                    ignoreCase = true)
            ) auteur = field.textContent else if (field.nodeName.equals("MSG",
                    ignoreCase = true)
            ) contenu = field.textContent
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        assert(stringDate != null)
        var date: Date? = null
        date = try {
            formatter.parse(stringDate!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
        assert(auteur != null)
        assert(contenu != null)
        assert(date != null)
        return Message(id, auteur!!, contenu!!, date!!)
    }
}