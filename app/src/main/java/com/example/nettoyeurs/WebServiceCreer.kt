package com.example.nettoyeurs

import android.location.Location
import android.util.Log
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceCreer(val session: Int,val signature: Long,val lon: Double, val lat: Double) {
    val TAG = "WSCreer"

    fun call(): ArrayList<Node>? {
        if (session == null || signature == null || lon == null || lat == null) {
            return null
        } else {
            try {
                val url =
                    URL("http://51.68.124.144/nettoyeurs_srv/new_nettoyeur.php?session=$session&signature=$signature&lon=$lon&lat=$lat")
                val cnx = url.openConnection()
                val `in` = cnx.getInputStream()

                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()

                val xml = db.parse(`in`)
                val nlStatus: NodeList = xml.getElementsByTagName("STATUS")
                val nodeStatus: Node = nlStatus.item(0)
                val status: String = nodeStatus.textContent
                Log.d(TAG, "Thread creation nettoyeur : status $status")

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