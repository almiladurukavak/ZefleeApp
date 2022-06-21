package com.almiladurukavak.zefleetask

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ReportDetailsActivity : AppCompatActivity() {

    lateinit var carPlate: String
    lateinit var email:String
    lateinit var takedPhoto: ImageView
    lateinit var dateText: AppCompatTextView
    lateinit var carPlateText: AppCompatTextView
    lateinit var addressText: AppCompatTextView
    lateinit var confirmButton: AppCompatButton
    private lateinit var auth: FirebaseAuth
    private lateinit var reportDb: FirebaseFirestore
    lateinit var progressBar: ProgressBar
    val KEY_NAME="12345"

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener
    val PREFS_FILENAME = "com.almiladurukavak.zefleeprefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_details)

        val prefences=getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

         val editor = prefences.edit()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {

            override fun onLocationChanged(p0: Location) {
                var latitude = p0!!.latitude
                var longitude = p0!!.longitude


                Log.d("konum", "latitude $latitude, longitude $longitude")
                try {
                    val geo = Geocoder(
                        applicationContext,
                        Locale.getDefault()
                    )
                    val addresses = geo.getFromLocation(latitude, longitude, 1)
                    if (addresses.isEmpty()) {
                        addressText.text = "Waiting for Location"
                    } else {
                        if (addresses.size > 0) {
                            addressText.text = addresses[0].getAddressLine(0).toString()
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // getFromLocation() may sometimes fail
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
            }
        }

        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (ex: SecurityException) {

            Toast.makeText(this, "Please turn on your location settings", Toast.LENGTH_SHORT).show()
        }


        auth = FirebaseAuth.getInstance()
        reportDb = FirebaseFirestore.getInstance()

        addressText = findViewById(R.id.addressText)
        carPlate = getIntent().getStringExtra("carplate").toString()
        email= getIntent().getStringExtra("email").toString()
        takedPhoto = findViewById(R.id.takedPhoto)

        carPlateText = findViewById(R.id.carPlateText)
        carPlateText.text = carPlate

        progressBar = findViewById(R.id.progress)


        val takePhoto = findViewById<AppCompatButton>(R.id.takePhoto)
        takePhoto.setOnClickListener(listener)

        confirmButton = findViewById(R.id.confirmBttn)
        confirmButton.isEnabled = false
        confirmButton.alpha = 0.5F
        confirmButton.setOnClickListener(listener)


        dateText = findViewById(R.id.dateText)
        val currentTimestamp = System.currentTimeMillis()

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date((currentTimestamp))

        dateText.text = sdf.format(netDate)


        val deleteButton = findViewById<ImageButton>(R.id.deleteButton)
        deleteButton.setOnClickListener(listener)

        checkLocation()


    }

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }


    private fun checkLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> checkLocation()
                PackageManager.PERMISSION_DENIED -> Toast.makeText(
                    this,
                    "You should check your location permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }


    val listener = View.OnClickListener { view ->
        when (view.getId()) {

            R.id.confirmBttn -> {

                submitAlert()

            }
            R.id.deleteButton -> {

                finish()
            }
            R.id.takePhoto -> {

                dispatchTakePictureIntent()
            }

        }
    }

    fun submitAlert() {

        if (takedPhoto.getDrawable() != null) {

            if (addressText.text.toString().isNotEmpty()){

               val reportDb= FirebaseFirestore.getInstance()
                val reportAdd =HashMap<String,Any>()

                reportAdd["address"] =addressText.text.toString()
                reportAdd["carPlate"] =carPlateText.text.toString()
                reportAdd["date"] = System.currentTimeMillis().toString()
                val prefences=getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

                Mailer.sendMail(
                    email,
                    "Move Your Car",
                    "Please Move Your Car"
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Toast.makeText(this, "Mail send check e-mail", Toast.LENGTH_SHORT).show()
                        }, {
                            Timber.e(it)
                        }
                    )


                reportAdd["photo"] =  "${prefences.getString(KEY_NAME,"DEFAULT_VALUE")}"
                reportAdd["userid"] = auth.uid.toString()
                reportDb.collection("Reports").add(reportAdd).addOnSuccessListener {

                    Toast.makeText(this,"added report",Toast.LENGTH_SHORT).show()

                    finish()

                }.addOnFailureListener{

                    Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show()


                }

            }else{

                Toast.makeText(this,"please turn on your location settings",Toast.LENGTH_SHORT).show()
            }



          } else {

            Toast.makeText(this, "Please take photo", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            takedPhoto.setImageBitmap(imageBitmap)

            uploadImageToFirebase()
        }

    }

    private fun uploadImageToFirebase() {
        // Get the data from an ImageView as bytes

        // Create a storage reference from our app
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val uid = UUID.randomUUID().toString()

// Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child(uid + ".jpg")
        takedPhoto.isDrawingCacheEnabled = true
        takedPhoto.buildDrawingCache()
        val bitmap = (takedPhoto.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)

        progressBar.visibility = View.VISIBLE
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->

            val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
            val editor = prefences.edit()

            editor.putString(KEY_NAME,uploadTask.result.metadata?.path)
            editor.apply()


            progressBar.visibility = View.GONE
            confirmButton.isEnabled = true
            confirmButton.alpha = 1F

            //Toast.makeText(this,"yğklendi",Toast.LENGTH_SHORT).show()
        }

    }

    companion object {

        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100

    }

}
