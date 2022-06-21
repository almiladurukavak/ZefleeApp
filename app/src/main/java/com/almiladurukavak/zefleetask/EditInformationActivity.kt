package com.almiladurukavak.zefleetask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class EditInformationActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var Userdb: FirebaseFirestore
    lateinit var passwordText: TextInputEditText
    lateinit var passwordTwiceText:TextInputEditText
    lateinit var carNoText: TextInputEditText
    lateinit var usernameText: TextInputEditText
    lateinit var carPlateText:TextInputEditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_information)

        Userdb= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()

        var submitButton=findViewById<AppCompatButton>(R.id.submitButton)
        submitButton.setOnClickListener(listener)

        val back=findViewById<AppCompatImageView>(R.id.back)
        back.setOnClickListener(listener)

        passwordText=findViewById(R.id.passwordText)
        passwordTwiceText=findViewById(R.id.passwordTwiceText)
        carNoText=findViewById(R.id.carNoText)
        usernameText=findViewById(R.id.usernameText)
        carPlateText=findViewById(R.id.carPlateText)


        readUserData()


    }
    val  listener= View.OnClickListener { view ->
        when (view.getId()) {

            R.id.submitButton -> {

                editInfo()

            }
            R.id.back -> {

               finish()

            }
        }
    }



    private fun editInfo() {
        val userAdd=HashMap<String,Any>()

        userAdd["username"] =usernameText.text.toString()
        userAdd["password"] =passwordText.text.toString()
        userAdd["carNo"] =carNoText.text.toString()
        userAdd["carPlate"] =carPlateText.text.toString().replace(" ", "")


        if (passwordText.text.toString().isNotEmpty()&&passwordTwiceText.text.toString().isNotEmpty()
            &&carNoText.text.toString().isNotEmpty()&&carPlateText.text.toString().isNotEmpty()){

            if (passwordTwiceText.text.toString().equals(passwordText.text.toString())){

                Userdb.collection("Users").document(auth.uid.toString()).update(userAdd).addOnSuccessListener {

                    Toast.makeText(this,"user updated",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{

                    Toast.makeText(this,"user data not updated",Toast.LENGTH_SHORT).show()


                }

            }else{

                Toast.makeText(this,"Passwords doesn't matched",Toast.LENGTH_SHORT).show()

            }

        }else{

            Toast.makeText(this,"Please fill in the blanks",Toast.LENGTH_SHORT).show()

        }




    }
    private fun readUserData() {

        Userdb.firestoreSettings= FirebaseFirestoreSettings.Builder().build()

        val usersRef = Userdb.collection("Users")
        usersRef.document(auth.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {

                     passwordText.setText(document.getString("password"))
                     carNoText.setText(document.getString("carNo"))
                     carPlateText.setText(document.getString("carPlate"))
                     usernameText.setText(document.getString("username"))



                } else {

                }
            } else {
                task.exception?.message?.let {

                }
            }
        }
    }

}