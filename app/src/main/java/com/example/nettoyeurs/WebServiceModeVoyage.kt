package com.example.nettoyeurs

import android.util.Log
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceModeVoyage(val session: Int,val signature: Long) {
    val TAG = "WSModeVoyage"

    fun call(): ArrayList<Node>? {
        if (session == null || signature == null) {
            return null
        } else {
            try {
                val url =
                    URL("http://51.68.124.144/nettoyeurs_srv/mode_voyage.php?session=$session&signature=$signature")
                val cnx = url.openConnection()
                val `in` = cnx.getInputStream()

                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()

                val xml = db.parse(`in`)
                val nlStatus: NodeList = xml.getElementsByTagName("STATUS")
                val nodeStatus: Node = nlStatus.item(0)
                val status: String = nodeStatus.textContent
                Log.d(TAG, "Thread MISE EN MODE VOYAGE : status $status")

                val params: ArrayList<Node> = ArrayList()

                if (!status.startsWith("OK"))
                    return null

                val nlParams: NodeList = xml.getElementsByTagName("PARAMS")
                val nodeParams: Node = nlParams.item(0)
                val paramsXML: NodeList = nodeParams.childNodes

                var len: Int? = paramsXML?.length
                println("table paramsXML.size = $len")

                for (i in 0..paramsXML.length - 1) {
                    println(paramsXML.item(i).textContent)
                    params.add(paramsXML.item(i))
                }
                var lenParam: Int = params.size
                println("table params.size = $lenParam")
                return params

           } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
}
}