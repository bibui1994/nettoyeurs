package com.example.nettoyeurs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessagesFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */

    : Fragment() {
        private var mListener: OnListFragmentInteractionListener? = null
        private var mMessages: ListeMessages? = null
        private var mAdapter: MyMessageRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_message_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            val recyclerView = view
            recyclerView.layoutManager = LinearLayoutManager(context)
            if (mMessages == null) mMessages = ListeMessages()
            if (mAdapter == null) mAdapter = MyMessageRecyclerViewAdapter(mMessages!!, mListener)
            recyclerView.adapter = mAdapter
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnListFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Message?)
    }

    fun addMessage(id:Int, date: Date, author: String, msg: String){
        mMessages?.ajouteMessage(id, date, author, msg)
        mAdapter?.notifyItemInserted(mMessages!!.size().minus(1))
    }

    fun deleteMessage(id: Int){
        val msgpos = mMessages?.deleteMessageFromId(id)

        if (msgpos != null) {
            if (msgpos >= 0){
                msgpos?.let { mAdapter?.notifyItemRemoved(it) }
                mAdapter?.notifyItemRangeChanged(msgpos, mMessages!!.size())
            }
        }
    }

    fun deleteMessages() {
        mMessages!!.deleteMessages()
        mAdapter!!.notifyDataSetChanged()
    }
}