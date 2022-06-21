package com.almiladurukavak.zefleetask.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.almiladurukavak.zefleetask.R
import com.almiladurukavak.zefleetask.adapter.ReportAdapter
import com.almiladurukavak.zefleetask.model.Report
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ReportHistoryFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var reportList: ArrayList<Report>
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val  view= inflater.inflate(R.layout.fragment_report_history, container, false)
        auth= FirebaseAuth.getInstance()

        recyclerView=view.findViewById(R.id.recycler_report)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        reportList= arrayListOf()

        reportAdapter= ReportAdapter(reportList)

        recyclerView.adapter=reportAdapter
        EventChangeListener()







      return view


    }

    private fun EventChangeListener() {

        db= FirebaseFirestore.getInstance()
        db.collection("Reports").whereEqualTo("userid",auth.uid.toString()) .addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {

                if (error!=null){
                    Log.e("Firestore error", error.message.toString())
                    return
                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type==DocumentChange.Type.ADDED){

                        reportList.add(dc.document.toObject(Report::class.java))

                    }

                }
                reportAdapter.notifyDataSetChanged()
            }



        })
    }

}