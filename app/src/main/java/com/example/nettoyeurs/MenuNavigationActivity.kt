package com.example.nettoyeurs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.nettoyeurs.R.id.text_view_countdown
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView

import org.w3c.dom.Node
import java.util.*
import kotlin.collections.ArrayList

class MenuNavigationActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener  {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var COUNT_DOWN_TIMER: Long =60000
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawer: DrawerLayout
    var session : String? = null
    var signature: String? = null
    var mode: Int? =null
    var lonFirst: Double? = null
    var latFirst: Double? = null
    var allDetection: Detection?=null
    var handler : Handler = Handler()
    var runnable : Runnable? = null
    var delay : Int = 15000



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_navigation)

        session = intent.getStringExtra("EXTRA_SESSION")
        signature =intent.getStringExtra("EXTRA_SIGNATURE")
        println("SESSION MENU NAVIGATION: " + session + "SIGNATURE MAIN MENU: " + signature)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //navigation slider
        var toolbar  = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as androidx.appcompat.widget.Toolbar?)
        drawer=findViewById(R.id.drawer_layout)
        var navigationView: NavigationView=findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        if(savedInstanceState==null){
            mode=1
            MisePosition()
            StatNettoyeur()
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,StatNettoyeurFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_stat_nettoyeur)

        }
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,StatNettoyeurFragment()).commit()
//        navigationView.setCheckedItem(R.id.nav_stat_nettoyeur)
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_stat_nettoyeur ->
            {
                StatNettoyeur();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,StatNettoyeurFragment()).commit()
            }

            R.id.nav_stat_equipe ->
            {
                StatEquipe()
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,StatEquipeFragment()).commit()
            }
            R.id.nav_position ->{
                mode=1
                MisePosition()
            }

            R.id.nav_voyage -> {
                MiseVoyage()
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, CountDownTimerFragment()).commit()
            }
            R.id.nav_remise_jeu->{
                RemiseEnJeu()
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, RemiseEnJeuFragment()).commit()
            }
            R.id.nav_chat->{
                Chat()
            }
            R.id.nav_map->{
                MisePosition()
                println("lonFirst switch case: " + lonFirst)
                println("latFirst switch case: " + latFirst)
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, MapsFragment(allDetection,lonFirst!!,latFirst!! ,session!!.toInt(),signature!!.toLong())).commit()
            }

        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun StatNettoyeur(){
        Thread {
            var wsStatNettoyeur = WebServiceStatNettoyeur(session?.toInt()!!,signature?.toLong()!!)
            val ok: ArrayList<Node>? = wsStatNettoyeur.call()
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
                    var nom_view = findViewById<TextView>(R.id.nom_nettoyeur)
                    var value_view = findViewById<TextView>(R.id.value_nettoyeur)
                    var lon_view = findViewById<TextView>(R.id.lon)
                    var lat_view = findViewById<TextView>(R.id.lat)
                    var status_view = findViewById<TextView>(R.id.status_nettoyeur)

                    var nom_header= findViewById<TextView>(R.id.nom_header)
                    var score_header= findViewById<TextView>(R.id.score_header)
                    var status_header= findViewById<TextView>(R.id.status_header)

                    var nomNettoyeur =ok?.get(0)?.textContent
                    var value = ok?.get(1)?.textContent?.toInt()
                    var pos_lon = ok?.get(2)?.textContent?.toDouble()
                    var pos_lat = ok?.get(3)?.textContent?.toDouble()
                    var status = ok?.get(4)?.textContent

                    nom_view.setText("Nom: $nomNettoyeur")
                    value_view.setText("Score: $value")
                    lon_view.setText("Longitude: $pos_lon")
                    lat_view.setText("Latitude: $pos_lat")
                    status_view.setText("Status: $status")

                    nom_header.setText("Nom: $nomNettoyeur")
                    score_header.setText("Score: $value")
                    status_header.setText("Status: $status")

                    println("SUCCESS with nom nettoyeur = $nomNettoyeur, value = $value, pos_lon = $pos_lon, pos_lat = $pos_lat and status = $status")
                }
            }
        }.start()

    }

    fun StatEquipe(){
        Thread {
            var wsStatEquipes = WebServiceStatEquipes(session?.toInt()!!,signature?.toLong()!!)
            val ok: ArrayList<Node>? = wsStatEquipes.call()
            var taille : Int? = ok?.size
            if (taille == 0) runOnUiThread {
                Toast.makeText(
                    this,
                    "Erreur StatEquipe",
                    Toast.LENGTH_LONG
                ).show()
            }
            else{
                runOnUiThread{
                    var value_equipe_view = findViewById<TextView>(R.id.value_equipe)
                    var value_adv_view = findViewById<TextView>(R.id.value_adv)
                    var active_membre_view = findViewById<TextView>(R.id.active_membre)

                    var value = ok?.get(0)?.textContent?.toInt()
                    var adv_value = ok?.get(1)?.textContent?.toDouble()
                    var active_membre = ok?.get(2)?.textContent?.toInt()

                    value_equipe_view.setText("Score de l'équipe: $value")
                    value_adv_view.setText("Score de l'dversaire: $adv_value")
                    active_membre_view.setText("Active membre: $active_membre")

                    println("SUCCESS with value= $value, adv_value = $adv_value, active_membre = $active_membre")
                }
            }
        }.start()
    }

    fun Chat(){
        val intent_chat = Intent(this, ChatActivity::class.java)
        intent_chat.also {
            it.putExtra("EXTRA_SESSION",session)
            it.putExtra("EXTRA_SIGNATURE",signature)
            startActivity(intent_chat)
        }
    }

    fun Map(){

    }


    fun MiseVoyage(){
        Thread {
            var wsModeVoyage = WebServiceModeVoyage(session?.toInt()!!,signature?.toLong()!!)
            val ok: ArrayList<Node>? = wsModeVoyage.call()
            var taille : Int? = ok?.size
            if (taille == 0) runOnUiThread {
                Toast.makeText(
                    this,
                    "Erreur MODE VOYAGE",
                    Toast.LENGTH_LONG
                ).show()
            }
            else{
                runOnUiThread{
                    var status = ok?.get(0)?.textContent
                    println("SUCCESS with status= $status")
                    val timer = object: CountDownTimer(COUNT_DOWN_TIMER, 1000) {
                        override fun onTick(millisUntilFinished: Long){
                            var textTimer=findViewById<TextView>(R.id.text_view_countdown)
                            var sec: Long = (millisUntilFinished/1000)%60
                            textTimer.setText(sec.toString())
                        }

                        override fun onFinish() {
                            RemiseEnJeu()
                            MisePosition()
//                            getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.fragment_container, MapsFragment(allDetection,lonFirst!!,latFirst!! ,session!!.toInt(),signature!!.toLong())).commit()
                        }
                    }
                    timer.start()
                }
            }
        }.start()


    }

    @SuppressLint("DetachAndAttachSameFragment")
    private fun MisePosition() {
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

                    var location : Location= task.result
                    println("location inside task Menu Navigation: $location")
                    if (location == null){
                        Toast.makeText(this, "Sorry Can't get Location", Toast.LENGTH_LONG).show()
                    }else {
                        var lat = location.latitude
                        var lon = location.longitude
                        lonFirst= lon
                        latFirst=lat
                        println("Latitude MenuNavi: $lat, LongitudeMenuNavi : $lon")

                        Thread {
                            var wsMisePosition = WebServiceMisePosition(session!!.toInt(),signature!!.toLong(),
                                lon, lat
                            )

                            allDetection = wsMisePosition.call()


                            if (allDetection == null) runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "Aucune objets sont detectées",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else{
                                runOnUiThread{
                                    println("detectionCRT Menu Navi: ${allDetection!!.detectionCRT.size}")
                                    println("detectionNET Menu Navi: ${allDetection!!.detectionNet.size}")
                                    var currFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)
                                    supportFragmentManager.beginTransaction()
                                        .detach(currFragment!!)
                                        .add(R.id.fragment_container,MapsFragment(allDetection,lon,lat ,session!!.toInt(),signature!!.toLong()))
                                        .attach(currFragment)
                                        .commit()
//                                    MapsFragment(allDetection,lon,lat ,session!!.toInt(),signature!!.toLong())

                                }
                            }
                        }.start()
                    }
                }
                ///here

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
//                        MisePosition()
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun RemiseEnJeu() {
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
                        Toast.makeText(this, "Sorry Can't get Location", Toast.LENGTH_LONG).show()
                    }else {
                        var latCurrent = location.latitude
                        var lonCurrent= location.longitude
                        println("Latitude current : $latCurrent, Longitude current : $lonCurrent")
                        Thread {
                            var wsRemiseEnJeu = WebServiceRemiseEnJeuNet(session?.toInt()!!,signature?.toLong()!!,lonCurrent!!,latCurrent!!)
                            val ok: ArrayList<Node>? = wsRemiseEnJeu.call()
                            var taille : Int? = ok?.size
                            if (taille == 0) runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "Erreur Remise EN Jeu",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else{
                                runOnUiThread{


                                    var status = ok?.get(0)?.textContent
                                    println("SUCCESS with status= $status")
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

    override fun onResume() {

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            Toast.makeText(this, "The postion and detection should be updated every 10 sec", Toast.LENGTH_LONG).show()
            println("MISE POSITION onRESUME ! ")
            MisePosition()
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }




}