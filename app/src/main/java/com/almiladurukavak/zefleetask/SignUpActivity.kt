package com.almiladurukavak.zefleetask

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.internal.InternalTokenProvider
import kotlin.math.log


class SignUpActivity : AppCompatActivity() {


    lateinit var usernameText:TextInputEditText
    lateinit var passwordText:TextInputEditText
    lateinit var carNoText:TextInputEditText
    lateinit var emailText:TextInputEditText
    lateinit var carPlateText:TextInputEditText

    private lateinit var auth:FirebaseAuth
    private lateinit var Userdb:FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth= FirebaseAuth.getInstance()
        Userdb= FirebaseFirestore.getInstance()


        val loginText=findViewById<AppCompatTextView>(R.id.loginText)
        loginText.setOnClickListener(listener)

        val back=findViewById<AppCompatImageView>(R.id.back)
        back.setOnClickListener(listener)

         usernameText=findViewById(R.id.usernameText)
         passwordText=findViewById(R.id.passwordText)
         carNoText=findViewById(R.id.carNoText)
         emailText=findViewById(R.id.emailText)
         carPlateText=findViewById(R.id.carPlateText)

        val emailSign=findViewById<AppCompatButton>(R.id.emailSignIn)
        emailSign.setOnClickListener(listener)



    }

    val  listener= View.OnClickListener { view ->
        when (view.getId()) {

            R.id.loginText -> {

                val intent= Intent(this,LoginActivity::class.java)
                startActivity(intent)

            }
            R.id.back ->{

                finish()
            }

            R.id.emailSignIn ->{

                signInUser()


            }
        }

    }

    private fun signInUser() {

        if (usernameText.text.toString().isNotEmpty()&&passwordText.text.toString().isNotEmpty()
            &&carNoText.text.toString().isNotEmpty()&&emailText.text.toString().isNotEmpty()&&carPlateText.text.toString().isNotEmpty()){

            auth.createUserWithEmailAndPassword(emailText.text.toString(),passwordText.text.toString()).addOnCompleteListener{

                    task-> if (task.isSuccessful){
                     createUser(auth.uid.toString())

                val intent=Intent(this,HomeSelectActivity::class.java)
                startActivity(intent)
                finish()

            }


            }.addOnFailureListener{
                    exception->

                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_SHORT).show()


            }



        }else{

            Toast.makeText(this,"fill in the blanks",Toast.LENGTH_SHORT).show()

        }
    }

    private fun createUser(uid:String) {

        val userAdd=HashMap<String,Any>()

        userAdd["username"] =usernameText.text.toString()
        userAdd["password"] =passwordText.text.toString()
        userAdd["carNo"] =carNoText.text.toString()
        userAdd["email"] =emailText.text.toString()
        userAdd["carPlate"] =carPlateText.text.toString()

        Userdb.collection("Users").document(uid).set(userAdd).addOnSuccessListener {

            Toast.makeText(this,"user added",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{

            Toast.makeText(this,"user data not added",Toast.LENGTH_SHORT).show()


        }

    }

}