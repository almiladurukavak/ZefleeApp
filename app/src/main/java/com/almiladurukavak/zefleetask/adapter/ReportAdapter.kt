package com.almiladurukavak.zefleetask.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.almiladurukavak.zefleetask.R
 import com.almiladurukavak.zefleetask.model.Report
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReportAdapter(private val reportList:ArrayList<Report>):RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportAdapter.MyViewHolder {

        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.report_item,parent,false)

        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ReportAdapter.MyViewHolder, position: Int) {

        val report: Report=reportList[position]
        holder.addressText.text=report.address
        holder.carPlateText.text=report.carPlate



        val sdf = SimpleDateFormat("MM/dd/yyyy")

        val netDate = Date(report.date!!.toLong())
        holder.dateText.text=  sdf.format(netDate)


        val imageref = Firebase.storage.reference.child(report.photo.toString())
        imageref.downloadUrl.addOnSuccessListener {Uri->

            val imageURL = Uri.toString()

            Glide.with(holder.itemView)
                .load(imageURL)
                .into(holder.photo)

        }




    }

    override fun getItemCount(): Int {

        return  reportList.size
    }
    public class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val addressText =itemView.findViewById<AppCompatTextView>(R.id.addressText)
        val dateText =itemView.findViewById<AppCompatTextView>(R.id.dateText)
        val carPlateText=itemView.findViewById<AppCompatTextView>(R.id.carPlateText)
        val photo=itemView.findViewById<AppCompatImageView>(R.id.photo)





    }


}