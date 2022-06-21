package com.almiladurukavak.zefleetask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()


        val loginButton=findViewById<AppCompatButton>(R.id.loginButton)
        loginButton.setOnClickListener(listener)

        val signUpButton=findViewById<AppCompatButton>(R.id.signUpButton)
        signUpButton.setOnClickListener(listener)


    }
    val  listener= View.OnClickListener { view ->
        when(view.getId()){

            R.id.loginButton ->{

                val intent= Intent(this,LoginActivity::class.java)
                startActivity(intent)

            }


            R.id.signUpButton ->{

                val intent=Intent(this,SignUpActivity::class.java)
                startActivity(intent)

            }


        }



    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent=Intent(this,HomeSelectActivity::class.java)
            startActivity(intent)

        }
    }


}