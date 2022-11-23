package com.example.nettoyeurs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

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
                
                Toast.makeText(this,"${votre_id.text} is logged in",Toast.LENGTH_SHORT).show()
            }
        }



    }
}