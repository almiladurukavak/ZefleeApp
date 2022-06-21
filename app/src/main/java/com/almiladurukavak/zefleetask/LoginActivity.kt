package com.almiladurukavak.zefleetask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    lateinit var emailText:TextInputEditText
    lateinit var passText:TextInputEditText
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val backButton=findViewById<AppCompatImageView>(R.id.back)
        backButton.setOnClickListener(listener)

        val signInButton=findViewById<AppCompatButton>(R.id.emailSignIn)
        signInButton.setOnClickListener(listener)

        emailText=findViewById(R.id.emailText)
        emailText.setOnClickListener(listener)

        passText=findViewById(R.id.passwordText)
        passText.setOnClickListener(listener)

    }

    val  listener= View.OnClickListener { view ->
        when(view.getId()){

            R.id.back ->{

               finish()

            }
            R.id.emailSignIn->{


                if (emailText.text.toString().isNotEmpty()&&passText.text.toString().isNotEmpty()){

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailText.text.toString(),passText.text.toString()).addOnCompleteListener(object :OnCompleteListener<AuthResult>{

                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful){

                                Toast.makeText(this@LoginActivity,"successfully login",Toast.LENGTH_SHORT).show()

                                val user=auth.currentUser

                                val intent= Intent(this@LoginActivity,HomeSelectActivity::class.java)
                                startActivity(intent)
                                finish()

                            }else{

                                Toast.makeText(this@LoginActivity,"failed",Toast.LENGTH_SHORT).show()

                            }
                        }




                    })

                }





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