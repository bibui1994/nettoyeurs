package com.example.nettoyeurs

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

                try {
                    val url : URL = URL("http://51.68.124.144/nettoyeurs_srv/connexion.php?login="+votre_id.text.toString()+"&passwd="+ mdpHash)
                    println("notre mpd hash: " + mdpHash)
                    //mtran
                    //n73/§fg8E*
                    val connect : HttpURLConnection = url.openConnection() as HttpURLConnection
                    val dbf = DocumentBuilderFactory.newInstance()
                    val db : DocumentBuilder? = dbf.newDocumentBuilder()
                    var doc : Document? = db?.parse(connect.inputStream)
                    val childNodes : NodeList? = doc?.childNodes
                    var nodeStatus : Node? = doc?.getElementsByTagName("STATUS")?.item(0)
                    var status : String? = nodeStatus?.textContent

                    //Log.d(TAG, "Thread last msg : status $status")
                    status?.startsWith("OK")
                    println("STATUS ==== " + status)
                } catch (e: Exception) {
                    println("debug CATCHHHHHHHHHHHH")
                    false
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