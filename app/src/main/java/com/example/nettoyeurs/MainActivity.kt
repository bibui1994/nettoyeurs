package com.example.nettoyeurs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Node
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_connexion = findViewById<View>(R.id.btn_connexion)
        val votre_id = findViewById<EditText>(R.id.votre_id)
        val mdp = findViewById<EditText>(R.id.mdp)

        val intent =    Intent(this@MainActivity, MenuActivity::class.java)
        var session : String?
        var signature: String?
        btn_connexion.setOnClickListener{
            if(votre_id.text.isNullOrBlank()&&mdp.text.isNullOrBlank()){
                Toast.makeText(this,"please fill the required fields",Toast.LENGTH_SHORT).show()
            }
            else{
                //envoyez et verifier id ,mdp sur server
                var login : String = votre_id.text.toString() // hlewhe
                //var passwd : String = mdp.text.toString() // jd£m/4*sU&
                //hashing mdp
                var mdpHash : String = mdp.text.toString().sha256()
                println("login $login passwd $mdpHash  !")

                Thread {
                    val ws = WebServiceConnexion(login, mdpHash)
                    val ok: ArrayList<Node>? = ws.call()

                    var taille : Int? = ok?.size
                    println("table Ok.size is = $taille")

                    if (taille == 0) runOnUiThread {
                        Toast.makeText(this,
                            "Erreur de la connexion",
                            Toast.LENGTH_LONG).show()
                    } else {
                        runOnUiThread {
                            session  = ok?.get(0)?.textContent?.trim()?.filter { it.isLetterOrDigit() }
                            signature= ok?.get(1)?.textContent?.trim()?.filter { it.isLetterOrDigit() }

                            println("SUCCESS with session = $session and signature = $signature")
                            intent.also {
                                it.putExtra("EXTRA_SESSION",session)
                                it.putExtra("EXTRA_SIGNATURE",signature)
                                startActivity(it)
                            }
                        }
                    }
                }.start()


                //startActivity(intent)
            }
        }
    }
    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }


}