package com.example.nettoyeurs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Node

class CreerJoueurActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var session : String? = null
    var signature: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creer_joueur)
        val btn_creer = findViewById<View>(R.id.btn_creerJ)
//        val btn_skip = findViewById<View>(R.id.btn_skip)

        var intentToMenu=Intent(this@CreerJoueurActivity,MenuNavigationActivity::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        session = intent.getStringExtra("EXTRA_SESSION")
        signature =intent.getStringExtra("EXTRA_SIGNATURE")
        btn_creer.setOnClickListener{
            getCurrentLocation()
            intentToMenu.also {
                it.putExtra("EXTRA_SESSION",session)
                it.putExtra("EXTRA_SIGNATURE",signature)
                startActivity(it)
            }
        }
    }
    private fun getCurrentLocation() {
        if (checkPermissions()){
            if(isLocationEnabled()){

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    //return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location : Location? = task.result
                    println("location : $location")
                    if (location == null){
                        Toast.makeText(this, "Problème de localisation", Toast.LENGTH_LONG).show()
                    }else {
                        var lat = location.latitude
                        var lon = location.longitude
                        println("Latitude : $lat, Longitude : $lon")
                        Thread {
                            var wsCreer = WebServiceCreer(session!!.toInt(),signature!!.toLong(),lon,lat)
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
                                    var status:String = ok?.get(0)?.textContent.toString()
                                    if(status.startsWith("OK")!!){
                                        var nomNettoyeur : String? = ok?.get(1)?.textContent
                                        if (nomNettoyeur == null){
                                            println("Votre nettoyeur déjà crée !")
                                        }
                                        else{
                                            println("SUCCESS with nom nettoyeur = $nomNettoyeur ")
                                        }
                                    }

                                }

                            }
                        }.start()
                    }
                }

            }else{
                //setting open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            // request permissions here
            requestPermission()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)

    }

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){

            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    println("The permission has been granted")
                    getCurrentLocation()
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}