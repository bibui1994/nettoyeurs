package com.example.nettoyeurs

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Node


class MenuActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val btn_creer = findViewById<View>(R.id.btn_creer)
        val btn_stat = findViewById<View>(R.id.btn_stat)
        val btn_stat_equipe = findViewById<View>(R.id.btn_stat_equipe)

        var session : String?
        var signature: String?
        var nomNettoyeur: String?

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btn_creer.setOnClickListener{
            session= intent.getStringExtra("EXTRA_SESSION")
            var session_Int:Int? = session?.toInt()
            signature =intent.getStringExtra("EXTRA_SIGNATURE")
//            var signature_Int:Int? = signature?.toInt()
            var signature_Int:Int? = Integer.parseInt(signature)
            println("EXTRA SESSION: " + session_Int + session_Int!!::class.simpleName )
            println("EXTRA SIGNATURE: " + signature_Int + signature_Int!!::class.simpleName)

            Thread {
                checkPermissions()
                var longitude : Double = checkPermissions()[0]
                var latitude : Double = checkPermissions()[1]
                var wsCreer = WebServiceCreer(session_Int,signature_Int,longitude,latitude)
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

    private fun checkPermissions() : ArrayList<Double>{
        var arrayCheckPermission : ArrayList<Double> = ArrayList()
        if(ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION), 1)
        }else{
            arrayCheckPermission = getLocations()
        }
        return arrayCheckPermission
    }

    @SuppressLint("MissingPermission")
    private fun getLocations() : ArrayList<Double> {
        var arrayLocation : ArrayList<Double> = ArrayList()
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            println("it : $it")
            if (it == null){
                Toast.makeText(this, "Sorry Can't get Location", Toast.LENGTH_LONG).show()
            }else it.apply{
                var lat = it.latitude
                var lon = it.longitude
                println("Latitude : $lat, Longitude : $lon")
                arrayLocation.add(lon)
                arrayLocation.add(lat)
            }
        }
        return arrayLocation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    getLocations()
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}