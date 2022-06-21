package com.almiladurukavak.zefleetask.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
 import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.almiladurukavak.zefleetask.R
import com.almiladurukavak.zefleetask.ReportDetailsActivity
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {


    lateinit var carPlateText: AppCompatEditText

    lateinit var Userdb:FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


       val  view= inflater.inflate(R.layout.fragment_home, container, false)
        Userdb= FirebaseFirestore.getInstance()

        carPlateText=view.findViewById(R.id.carPlateText)



        val towitButton=view.findViewById<AppCompatTextView>(R.id.towitButton)
        towitButton.setOnClickListener(listener)




        return view

    }



    val  listener= View.OnClickListener { view ->
        when (view.getId()) {

            R.id.towitButton -> {

                if (!carPlateText.text.toString().isEmpty()){

                    Userdb.collection("Users").orderBy("carPlate").startAt(carPlateText.text.toString()).endAt(carPlateText.text.toString() + "\uf8ff").get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if(document != null) {
                                if (!document.isEmpty) {
                                    sendReportDetails()
                                } else {

                                    Toast.makeText(context,"This car plate doesn't exist database",Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context,task.exception.toString(),Toast.LENGTH_SHORT).show()

                        }
                    }


                }else{

                    Toast.makeText(requireContext(),"You should enter car plate",Toast.LENGTH_SHORT).show()

                }




            }
        }
    }

    private fun sendReportDetails() {

                    Userdb.collection("Users")
                        .whereEqualTo("carPlate", carPlateText.text.toString())
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {

                                val intent=Intent(context,ReportDetailsActivity::class.java)
                                intent.putExtra("email",document.data.get("email").toString())
                                intent.putExtra("carplate",carPlateText.text.toString())
                                startActivity(intent)


                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("almila", "Error getting documents: ", exception)
                        }



    }

}