package com.example.nettoyeurs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyMessageRecyclerViewAdapter(private val mValues: ListeMessages, private val mListener: MessagesFragment.OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyMessageRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mAuthorView.text = mValues[position]!!.author
        var textContent = mValues[position]!!.msg
        if (textContent.length > 50) textContent = textContent.substring(0, 47) + "..."
        holder.mMsgView.text = textContent
        holder.mView.setOnClickListener { mListener?.onListFragmentInteraction(holder.mItem) }
    }

    override fun getItemCount(): Int {
        return mValues.size()
    }

    fun ajouteMessage(id:Int, date: Date, author: String, contenu: String) {
        mValues.ajouteMessage(id,date,author, contenu)
        notifyItemInserted(mValues.size() - 1)
    }

    fun supprimeMessage(id: Int) {
        val notePos = mValues.deleteMessageFromId(id)
        if (notePos >= 0) {
            notifyItemRemoved(notePos)
            this.notifyItemRangeChanged(notePos, mValues.size())
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mAuthorView: TextView
        val mMsgView: TextView
        val mDateView: TextView
        var mItem: Message? = null
        override fun toString(): String {
            return super.toString() + " '" + mMsgView.text + "'"
        }

        init {
            mAuthorView = mView.findViewById<View>(R.id.id) as TextView
            mMsgView = mView.findViewById<View>(R.id.content) as TextView
            mDateView = mView.findViewById<View>(R.id.date) as TextView
        }
    }

}