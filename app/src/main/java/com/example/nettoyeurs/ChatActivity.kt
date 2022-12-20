package com.example.nettoyeurs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChatActivity : AppCompatActivity(), MessagesFragment.OnListFragmentInteractionListener {
    var messagesFragment: MessagesFragment? = null
    var messageDetailFragment: MessageDetailsFragment? = null
    var selectedMessage: Message? = null

    var nextId = 0
    private val TAG = "ChatActivity"
    var isResume = false

    var session : String? = null
    var signature: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageDetailFragment = supportFragmentManager.findFragmentById(R.id.mainNoteDetailFrag) as MessageDetailsFragment?
        messagesFragment = supportFragmentManager.findFragmentById(R.id.mainNoteFrag) as MessagesFragment?

        val btnCreer = findViewById<View>(R.id.buttonCreer)
        val btnsuppr = findViewById<View>(R.id.buttonSuppr)

        session = intent.getStringExtra("EXTRA_SESSION")
        signature = intent.getStringExtra("EXTRA_SIGNATURE").toString()

        println("Niveau onCreate : Session $session signature $signature  !")

        btnCreer.setOnClickListener {
            val intent = Intent(this, EditMessageActivity::class.java)
            startActivityForResult(intent,1)
        }

        btnsuppr.setOnClickListener{
            var id : Int? = selectedMessage?.id
            messagesFragment?.deleteMessage(id!!)
            messageDetailFragment!!.update(null)
            Toast.makeText(this, "Un message supprimé", Toast.LENGTH_SHORT).show()
        }
    }

    @JvmName("onListFragmentInteraction1")
    fun onListFragmentInteraction(item: Message) {
        messageDetailFragment!!.update(item)
        selectedMessage = item
        Log.d("NoteCallback", item.author + " - " + item.msg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            session = intent.getStringExtra("EXTRA_SESSION")
            signature =intent.getStringExtra("EXTRA_SIGNATURE")

            var auteur = data!!.getStringExtra("EXTRA_AUTHOR")
            var contenu = data.getStringExtra("EXTRA_CONTENT")

            println("Niveau onActivityResult : Session $session signature $signature  autheur $auteur contenu $contenu !")
            Thread {
                val ws_new_msg = WebServiceNewMSG(session!!.toInt(), signature!!.toLong(), contenu!!)
                val ok = ws_new_msg.call()
                if (!ok) runOnUiThread {
                    Toast.makeText(this,
                        "Erreur dans l'envoi du message",
                        Toast.LENGTH_LONG).show()
                } else {
                    runOnUiThread { if (isResume) raffaichirMessages() }
                }
            }.start()
        }
    }

    override fun onResume() {
        super.onResume()
        isResume = true
        raffaichirMessages()
    }

    override fun onPause() {
        super.onPause()
        isResume = false
        super.onPause()
    }

    private fun raffaichirMessages() {
        println("Niveau refresh : Session $session signature $signature  !")
        Thread {
            val ws_last_msg = WebServiceLastMSG(session!!.toInt(), signature!!.toLong())
            val aAjouter = ws_last_msg.call()
            try {
                runOnUiThread {
                    messagesFragment!!.deleteMessages()
                    for (m in aAjouter!!) {
                        messagesFragment!!.addMessage(m.id,
                            m.date,
                            m.author,
                            m.msg)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun onSendPressed(view: View?) {}
    override fun onListFragmentInteraction(item: Message?) {
        Log.d("MsgCallback", (item?.date.toString()) + (item?.author ?: "(null)") + " - " + (item?.msg ?: "(null)"))
        //TODO
        selectedMessage = item
        messageDetailFragment?.update(item)
    }

}