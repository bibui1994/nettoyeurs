package com.example.nettoyeurs

import java.util.*

class ListeMessages {

    var listmsg = arrayListOf<Message>()

    fun ajouteMessage(id:Int, date: Date, author: String, msg: String) {
        //TODO
        listmsg.add(Message(id, author, msg, date))
    }

    operator fun get(i: Int): Message? {
        //TODO
        return listmsg.get(i)
    }

    fun deleteMessageFromIndex(i: Int): Boolean {
        //TODO
        while (i < listmsg.size){
            listmsg.removeAt(i)
            return true
        }
        return false
    }

    fun deleteMessageFromId(id: Int): Int {
        //TODO

        for (i in 0..listmsg.size){
            var msg = listmsg.get(i)
            if (msg.id == id){
                listmsg.remove(msg)
                return i
            }
        }
        return -1
    }

    fun size(): Int {
        //TODO
        println("You got " + listmsg.size + " messages\uD83D\uDE0E")
        return listmsg.size
    }

    fun deleteMessages(): Boolean {
        if (listmsg.isEmpty()) return false
        listmsg.clear()
        return true
    }

    init {
        ajouteMessage(1, Calendar.getInstance().time,"Riri", "Bonjour bonjour")
        ajouteMessage(2, Calendar.getInstance().time,"Fifi", "Broudaf, zog-zog ! ")
        ajouteMessage(3, Calendar.getInstance().time,"Loulou", "On va faire un contenu un peu plus long, pour voir comment ça passe sur tous les affichages. Hopla ! Et même encore un peu plus long histoire de dire. Après tout, normalement on a tout un écran pour l'afficher, donc on est bien large...")
    }
}