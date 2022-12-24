package com.example.nettoyeurs

import android.util.Log
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class WebServiceMisePosition(val session: Int,val signature: Long,val lon: Double, val lat: Double) {
    val TAG = "WSMisePosition"
    var latLocal= 47.84748
    var lonLocal =1.93909
    fun call(): Detection? {
        if (session == null || signature == null || lon == null || lat == null) {
            return null
        } else {
            try {
                val url =
                    URL("http://51.68.124.144/nettoyeurs_srv/deplace.php?session=$session&signature=$signature&lon=$lon&lat=$lat")
                val cnx = url.openConnection()
                val `in` = cnx.getInputStream()

                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()

                val xml = db.parse(`in`)
                val nlStatus: NodeList = xml.getElementsByTagName("STATUS")
                val nodeStatus: Node = nlStatus.item(0)
                val status: String = nodeStatus.textContent
                Log.d(TAG, "Thread MISE POSITION : status $status")

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

                var nodeDetectionCRT: Node= paramsXML.item(0)
                val nodeDetectionCRTXML: NodeList = nodeDetectionCRT.childNodes
                var nodeDetectionNET: Node= paramsXML.item(1)
                val nodeDetectionNETXML: NodeList = nodeDetectionNET.childNodes


                var netArray: ArrayList<DetectedNET> = ArrayList()
                var cibleArray: ArrayList<DetectedCTR> = ArrayList()
                println("nodeDetectionCRTXML.length: "+nodeDetectionCRTXML.length )
                println("nodeDetectionNETXML.length: "+nodeDetectionNETXML.length )
                if(nodeDetectionCRTXML.length !=0){
                    for (i in 0 .. nodeDetectionCRTXML.length-1){
                        var cible_id: Node? = nodeDetectionCRTXML.item(i).childNodes.item(0)
                        var value: Node? = nodeDetectionCRTXML.item(i).childNodes.item(1)
                        var lon: Node? = nodeDetectionCRTXML.item(i).childNodes.item(2)
                        var lat: Node? = nodeDetectionCRTXML.item(i).childNodes.item(3)
                        var cible: DetectedCTR? = DetectedCTR(cible_id!!.textContent.toInt(),value!!.textContent.toInt()
                            ,lon!!.textContent.toDouble(),lat!!.textContent.toDouble())
                        cibleArray.add(cible!!)
                    }
                }
                if(nodeDetectionNETXML.length != 0){
                    for (i in 0..nodeDetectionNETXML.length-1){
                        var net_id: Node? = nodeDetectionNETXML.item(i).childNodes.item(0)
                        var value: Node? = nodeDetectionNETXML.item(i).childNodes.item(1)
                        var lon: Node? = nodeDetectionNETXML.item(i).childNodes.item(2)
                        var lat: Node? = nodeDetectionNETXML.item(i).childNodes.item(3)
                        var lifespan: Node? = nodeDetectionNETXML.item(i).childNodes.item(4)
                        var net: DetectedNET? = DetectedNET(net_id!!.textContent.toInt(),value!!.textContent.toInt()
                            ,lon!!.textContent.toDouble(),lat!!.textContent.toDouble(),lifespan!!.textContent.toInt())
                        netArray.add(net!!)
                    }
                }

                println("ARRAY CIBLE WS mise position"+cibleArray.size)
                println("ARRAY NETT WS mise position"+netArray.size)
                var detection= Detection(cibleArray,netArray)

                return detection

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }


}