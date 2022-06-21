package com.almiladurukavak.zefleetask

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import com.almiladurukavak.zefleetask.EditInformationActivity
import com.almiladurukavak.zefleetask.MainActivity
import com.almiladurukavak.zefleetask.R
import com.almiladurukavak.zefleetask.ReportActivity
import timber.log.Timber


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.DialogTitle
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.auth.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private lateinit var auth:FirebaseAuth
private lateinit var Userdb: FirebaseFirestore
lateinit var usernameText: AppCompatTextView

class HomeSelectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_select)
        Userdb= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()

        readUserData()


        val editCarButton=findViewById<LinearLayoutCompat>(R.id.edit_car_info_lin)
        editCarButton.setOnClickListener(listener)

        val urbanHeroButton=findViewById<LinearLayoutCompat>(R.id.urban_hero_lin)
        urbanHeroButton.setOnClickListener(listener)

        val logoutButton=findViewById<Button>(R.id.logout)
        logoutButton.setOnClickListener(listener)

        usernameText=findViewById(R.id.userName)

        val intent = Intent(this, HomeSelectActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


    }




    private fun readUserData() {

        val PREFS_FILENAME = "com.almiladurukavak.zefleeprefs"
        val prefences=getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        val editor = prefences.edit()

        val KEY_USERNAME=""



        Userdb.firestoreSettings=FirebaseFirestoreSettings.Builder().build()

        val usersRef = Userdb.collection("Users")
        usersRef.document(auth.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val username = document.getString("username")


                    editor.putString(KEY_USERNAME,username)
                    if (KEY_USERNAME.isEmpty()){

                        editor.apply()
                    }


                    usernameText.text=applicationContext.getString(R.string.welcome)+"  ${prefences.getString(KEY_USERNAME,"DEFAULT_VALUE")}  "+applicationContext.getString(R.string.to_zeflee)


                } else {

                }
            } else {
                task.exception?.message?.let {

                }
            }
        }
    }

    val  listener= View.OnClickListener { view ->
        when(view.getId()){

            R.id.edit_car_info_lin ->{

                val intent= Intent(this, EditInformationActivity::class.java)
                startActivity(intent)
            }


            R.id.urban_hero_lin ->{

                val intent= Intent(this, ReportActivity::class.java)
                startActivity(intent)

            }

            R.id.logout->{

                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }



    }

}