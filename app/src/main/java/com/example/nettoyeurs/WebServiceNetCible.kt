package com.example.nettoyeurs

import android.util.Log
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceNetCible(val session: Int,val signature: Long, val cible_id:Int) {

    val TAG = "WSNettoyageCible"

    fun call(): ArrayList<Node>? {
        if (session == null || signature == null || cible_id == null) {
            return null
        } else {
            try {
                val url =
                    URL("http://51.68.124.144/nettoyeurs_srv/frappe_cible.php?session=$session&signature=$signature&cible_id=$cible_id")
                val cnx = url.openConnection()
                val `in` = cnx.getInputStream()
                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()

                val xml = db.parse(`in`)
                val nlStatus : NodeList = xml.getElementsByTagName("STATUS")
                val nodeStatus : Node = nlStatus.item(0)
                val status : String = nodeStatus.textContent
                Log.d(TAG, "Thread nettoyage d'une cible  : status $status")

                val params: ArrayList<Node> = ArrayList()

                if (!status.startsWith("OK"))
                    return null

                val nlParams : NodeList = xml.getElementsByTagName("PARAMS")
                val nodeParams : Node = nlParams.item(0)
                val paramsXML : NodeList = nodeParams.childNodes

                var len :Int? = paramsXML?.length
                println("table paramsXML.size = $len")

                for (i in 0..paramsXML.length-1) {
                    println(paramsXML.item(i).textContent)
                    params.add(paramsXML.item(i))
                }
                var lenParam :Int = params.size
                println("table params.size = $lenParam")
                return params

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}