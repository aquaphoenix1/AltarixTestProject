package com.example.aqua_phoenix.altarixtestapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.example.aqua_phoenix.altarixtestapplication.viewmodel.PlaceInfoViewModel
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo
import java.lang.Exception

class PlaceDetailsFragment : Fragment() {
    lateinit var imageView: ImageView
    lateinit var textViewAddress : TextView
    private lateinit var placeInfoViewModel: PlaceInfoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_place_details, container, false)

        imageView = v.findViewById(R.id.imageView)
        textViewAddress = v.findViewById(R.id.addressTextView)
        //v.findViewById<TextView>(R.id.addressTextView).text = arguments!!.getCharSequence("address", "Невозможно найти адрес").toString()
        //imageView = v.findViewById(R.id.imageView)


        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        placeInfoViewModel = ViewModelProviders.of(this).get(PlaceInfoViewModel::class.java)


        placeInfoViewModel.getPlaceInfo().observe(this, android.arch.lifecycle.Observer {
            refreshUI(it)
        })
    }

    fun refreshUI(placeInfo: PlaceInfo?){
        if(placeInfo != null) {
            textViewAddress.text = placeInfo.address

            if(placeInfo.imagePath != null){
                try{
                    imageView.setImageURI(placeInfo.imagePath)
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    fun setImage(bitmap: Bitmap){
        //imageView.setImageBitmap(bitmap)
    }

    fun setDefaultImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}