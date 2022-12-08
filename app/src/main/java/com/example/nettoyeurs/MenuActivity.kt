package com.example.nettoyeurs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Node
import kotlin.concurrent.thread


class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val btn_creer = findViewById<View>(R.id.btn_creer)
        val btn_stat = findViewById<View>(R.id.btn_stat)
        val btn_stat_equipe = findViewById<View>(R.id.btn_stat_equipe)

        var session : String?
        var signature: String?
        var nomNettoyeur: String?
        btn_creer.setOnClickListener{
            session= intent.getStringExtra("EXTRA_SESSION")
            var session_Int:Int? = session?.toInt()
            signature =intent.getStringExtra("EXTRA_SIGNATURE")
//            var signature_Int:Int? = signature?.toInt()
            var signature_Int:Int? = Integer.parseInt(signature)
            println("EXTRA SESSION: " + session_Int + session_Int!!::class.simpleName )
            println("EXTRA SIGNATURE: " + signature_Int + signature_Int!!::class.simpleName)

            Thread {
                var wsCreer = WebServiceCreer(session_Int,signature_Int,47.845431,2.0)
                val ok: ArrayList<Node>? = wsCreer.call()
                var taille : Int? = ok?.size
                if (taille == 0) runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur la creation nettoyeur",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else{
                    runOnUiThread{
                        nomNettoyeur=ok?.get(0)?.textContent
                        println("SUCCESS with nom nettoyeur = $nomNettoyeur ")
                    }
                }
            }.start()
        }

    }



}