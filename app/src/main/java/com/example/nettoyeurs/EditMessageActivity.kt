package com.example.nettoyeurs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_message)

        val btnValider = findViewById<View>(R.id.buttonValider)

        btnValider.setOnClickListener{
            val titreView = findViewById<EditText>(R.id.titre)
            val contenuView = findViewById<EditText>(R.id.contenu)

            Log.d("SecondeActivity", "Titre : $titreView.text.toString() - contenu : $contenuView.text.toString() ")
            val intentActivityForResult = Intent()

            intentActivityForResult.putExtra("EXTRA_AUTHOR", titreView.text.toString())
            intentActivityForResult.putExtra("EXTRA_CONTENT", contenuView.text.toString())
            setResult(Activity.RESULT_OK, intentActivityForResult)
            finish()
        }
    }
}