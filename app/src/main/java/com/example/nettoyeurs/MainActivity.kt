package com.example.nettoyeurs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Node
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_connexion = findViewById<View>(R.id.btn_connexion)
        val btn_test = findViewById<View>(R.id.btn_test)
        val btn_test_map = findViewById<View>(R.id.btn_test_map)

        val votre_id = findViewById<EditText>(R.id.votre_id)
        val mdp = findViewById<EditText>(R.id.mdp)
        var erreurMessage= findViewById<TextView>(R.id.erreur_connexion)
        val intent =    Intent(this@MainActivity, CreerJoueurActivity::class.java)
        val intentTest= Intent(this@MainActivity,MenuNavigationActivity::class.java)
        val intentGGMap= Intent(this@MainActivity,MapsActivity::class.java)
        var session : String?
        var signature: String?
        btn_test.setOnClickListener{
            startActivity(intentTest)
        }
        btn_test_map.setOnClickListener{
            startActivity(intentGGMap)
        }
        btn_connexion.setOnClickListener{
            if(votre_id.text.isNullOrBlank()  || mdp.text.isNullOrBlank()){
                Toast.makeText(this,"Veuillez remplir tous les champs",Toast.LENGTH_SHORT).show()
            }
            else{
                var login : String = votre_id.text.toString()
                // hlewhe// jd£m/4*sU&
//                login: mtran
//
//                passwd: n73/§fg8E*
                //hashing mdp
                var mdpHash : String = mdp.text.toString().sha256()
                println("login $login passwd $mdpHash  !")

                Thread {
                    val ws = WebServiceConnexion(login, mdpHash)
                    val ok: ArrayList<Node>? = ws.call()
                    var taille : Int? = ok?.size
                    if (taille == 0) runOnUiThread {
                        Toast.makeText(this,
                            "Erreur de la connexion",
                            Toast.LENGTH_LONG).show()
                    } else {
                        runOnUiThread {
                            var status:String = ok?.get(0)?.textContent.toString()
                            if (status.startsWith("OK")!!){
                                erreurMessage.visibility = View.INVISIBLE
                                session  = ok?.get(1)?.textContent?.trim()?.filter { it.isLetterOrDigit() }
                                signature= ok?.get(2)?.textContent?.trim()?.filter { it.isLetterOrDigit() }
                                Toast.makeText(this,"Connexion réussie",Toast.LENGTH_SHORT).show()
//                                println("SUCCESS with session = $session and signature = $signature")
                                intent.also {
                                    it.putExtra("EXTRA_SESSION",session)
                                    it.putExtra("EXTRA_SIGNATURE",signature)
                                    startActivity(it)
                                }
                            }
                            else{
                                erreurMessage.visibility=View.VISIBLE
                                if(status.equals("KO - WRONG CREDENTIALS")){
                                    erreurMessage.setText("Veuillez saisir les bons identifiants")
                                }
                                else{
                                    erreurMessage.setText("Problème technique")
                                }
                            }
                        }
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