package com.example.nettoyeurs

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
import org.w3c.dom.Node
import java.security.Signature
import kotlin.concurrent.thread

class MapsFragment(detection: Detection?,long : Double, lati:Double,session:Int,signature: Long) : Fragment(),
    GoogleMap.OnMarkerClickListener {
    var dec = detection
    var lon = long
    var lat = lati
    var ses=session
    var sig=signature
    lateinit var builder: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder=AlertDialog.Builder(activity)
        println("Longtitude in FRAGMENT: "+ lon)
        println("Latitude in FRAGMENT: " + lat)
//
//        println("detectionCRT size FRAGMENT: ${dec!!.detectionCRT.size}")
//        println("detectionNET size FRAGMENT: ${dec!!.detectionNet.size}")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        if(dec!= null){
            var detectedCTR:ArrayList <DetectedCTR> = dec!!.detectionCRT
            var detectedNet:ArrayList <DetectedNET> = dec!!.detectionNet

            mapFragment?.getMapAsync { mMap ->
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

                mMap.clear() //clear old markers

                val googlePlex = CameraPosition.builder()
                    .target(LatLng(lat, lon))
                    .zoom(18f)
                    .bearing(0f)
                    .tilt(45f)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null)
                mMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(LatLng(lat, lon))
                        .title("me " + "lat: " +lat + "lon : "+ lon)
                        .draggable(true)

                )

                if (detectedCTR.size != 0){
                    for(i in 0..detectedCTR.size-1){
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(detectedCTR[i].lat,detectedCTR[i].lon))
                                .title("Cible id: ${detectedCTR[i].cible_id}")
                        )
                        println("Cib id:" + detectedCTR[i].cible_id +" lat: "+detectedCTR[i].lat + " lon: "+detectedCTR[i].lon)
                    }
                }

                if (detectedNet.size != 0){
                    for(i in 0..detectedNet.size-1){
                        mMap.addMarker(
                            MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .position(LatLng(detectedNet[i].lat,detectedNet[i].lon))
                                .title("Nettoyeur id: ${detectedNet[i].net_id}")
                        )
                        println("net id:" + detectedNet[i].net_id +" lat: "+detectedNet[i].lat + " lon: "+detectedNet[i].lon)
                    }
                }
                mMap.setOnMarkerClickListener(this)

            }
        }
        else{
            mapFragment?.getMapAsync { mMap ->
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

                mMap.clear() //clear old markers

                val googlePlex = CameraPosition.builder()
                    .target(LatLng(lat, lon))
                    .zoom(18f)
                    .bearing(0f)
                    .tilt(45f)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null)
                mMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(LatLng(lat, lon))
                        .title("me " + "lat: " +lat + "lon : "+ lon)
                        .draggable(true)

                )
            }
        }




    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(activity,marker.title, Toast.LENGTH_LONG).show()
        var res = marker.title!!.filter { it.isDigit() }


        marker.getPosition().latitude
        marker.getPosition().longitude
        if(!marker.title!!.equals("me")){

            builder.setTitle("Alert!")
                .setMessage("Do you want to exit ?")
                .setCancelable(true)
                .setPositiveButton("Yes"){ dialogInterface : DialogInterface, it ->
                    if(marker.title!!.take(3).equals("Net")){
                        var idInt = marker.title!!.filter { it.isDigit() }
//                        println(idInt +" "+ ses +" "+ " "+sig)
                        NettoyageNet(idInt.toInt())
//                        println("NETTOYYYYYYYYYYYYYYYYYYYYYYYYy")
                    }
                    else if (marker.title!!.take(3).equals("Cib")){
                        var idInt = marker.title!!.filter { it.isDigit() }
//                        println(idInt +" "+ ses +" "+ " "+sig)
                        NettoyageCib(idInt.toInt())

                   }

                }
                .setNegativeButton("No"){dialogInterface, it -> dialogInterface.cancel()}
                .setNeutralButton("Help"){dialogInterface, it ->
                    Toast.makeText(activity, "Help clicked", Toast.LENGTH_LONG).show()
                }
                .show()
        }
        return false
    }


    fun NettoyageCib(cibId:Int){
        Thread {
        println(" "+ ses +" "+ " "+sig+ " " + cibId)
        var wsNettoyageCib = WebServiceNetCible(ses,sig,cibId)
        val ok: ArrayList<Node>? = wsNettoyageCib.call()
        var taille : Int? = ok?.size
        if (taille == 0) activity?.runOnUiThread {


            Toast.makeText(
                activity, "Erreur Nettoyage Cible", Toast.LENGTH_LONG).show()
        }
        else{
            activity?.runOnUiThread {
                var outcome = ok?.get(0)?.textContent
                var detected = ok?.get(1)?.textContent
                println("SUCCESS with outcome = $outcome | detected = $detected")
            }
        }
        }.start()
    }


    fun NettoyageNet(netId: Int){
        Thread {
        println(" "+ ses +" "+ " "+sig+ " " + netId)
            var wsNettoyageNet = WebSerciveNetNettoyAdverse(ses,sig,netId)
            val ok: ArrayList<Node>? = wsNettoyageNet.call()
            var taille : Int? = ok?.size
            if (taille == 0) activity?.runOnUiThread {
                Toast.makeText(
                    activity,
                    "Erreur Nettoyage Net Adverse",
                    Toast.LENGTH_LONG
                ).show()
            }

            else{
                activity?.runOnUiThread{
                    var outcome = ok?.get(0)?.textContent

                    println("SUCCESS with outcome = $outcome")
                }
            }
        }.start()
    }




}