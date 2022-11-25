package com.example.nettoyeurs

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_connexion = findViewById<View>(R.id.btn_connexion)
        val votre_id = findViewById<EditText>(R.id.votre_id)
        val mdp = findViewById<EditText>(R.id.mdp)

        btn_connexion.setOnClickListener{
            if(votre_id.text.isNullOrBlank()&&mdp.text.isNullOrBlank()){
                Toast.makeText(this,"please fill the required fields",Toast.LENGTH_SHORT).show()
            }
            else{
                //envoyez et verifier id ,mdp sur server

                //hashing mdp
                val mdpHash = mdp.text.toString().sha256()
                val mVotre_ID= URLEncoder.encode(votre_id.text.toString(),"UTF-8")
                try {
                    val url : URL = URL("http://51.68.124.144/nettoyeurs_srv/connexion.php?login=$mVotre_ID&passwd=$mdpHash")
                    println("notre mpd hash: " + mdpHash)
                    println("notre mVotre_id: " + mVotre_ID)
                    //  mtran
                    //  n73/§fg8E*
                    val connect : HttpURLConnection = url.openConnection() as HttpURLConnection

                    val dbf :DocumentBuilderFactory?= DocumentBuilderFactory.newInstance()
                    val db : DocumentBuilder? = dbf?.newDocumentBuilder()
                    var doc : Document? = db?.parse(connect.inputStream)
                    println("this is problemmmmmmmmmmmmm3333333333333333")
                    val childNodes : NodeList? = doc?.childNodes
                    println("this is problemmmmmmmmmmmmm4444444444444")
                    var nodeStatus: Node? = doc?.getElementsByTagName("STATUS")?.item(0)
                    println("STATUS-11111111111111 ==== ")
                    var status : String? = nodeStatus?.textContent

                    //Log.d(TAG, "Thread last msg : status $status")
                    println("STATUS000000000 ==== " + status)
                    status?.startsWith("OK")
                    println("STATUS ==== " + status)
                } catch (e: Exception) {
                    println("debug CATCHHHHHHHHHHHH")
                }
                //Toast.makeText(this,"${votre_id.text} is logged in",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }


}