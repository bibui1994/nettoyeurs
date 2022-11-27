package com.example.nettoyeurs

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.math.BigInteger
import java.security.MessageDigest

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
                var login : String = votre_id.text.toString() // hlewhe
                var passwd : String = mdp.text.toString() // jd£m/4*sU&
                //hashing mdp
                var mdpHash : String = mdp.text.toString().sha256()
                println("login $login passwd $mdpHash  !")

                Thread {
                    val ws = WebServiceConnexion(login, mdpHash)
                    val ok: Boolean = ws.call()
                    if (!ok) runOnUiThread {
                        Toast.makeText(this,
                            "Erreur de la connexion",
                            Toast.LENGTH_LONG).show()
                    } else {
                        runOnUiThread { println("SUCCESS") }
                    }
                }.start()
            }
        }
    }
    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }


}