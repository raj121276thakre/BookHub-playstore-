package com.rajapps.bookify_test.Activity

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

import com.rajapps.bookify_test.Models.BooksModel
import com.rajapps.bookify_test.Repository.BookRepo
import com.rajapps.bookify_test.Utils.MyResponses
import com.rajapps.bookify_test.Utils.loadOnline
import com.rajapps.bookify_test.ViewModels.BookViewModel
import com.rajapps.bookify_test.ViewModels.BookViewModelFactory

import com.rajapps.bookify_test.databinding.ActivityDetailsBinding
import com.rajapps.bookify_test.databinding.LayoutProgressBinding
import java.util.Timer
import java.util.TimerTask

class DetailsActivity : AppCompatActivity() {
    val activity = this
    lateinit var binding: ActivityDetailsBinding

    private var mInterstitialAd: InterstitialAd? = null // interstetial ad


    private val repo = BookRepo(activity)
    private val viewModel by lazy {
        ViewModelProvider(
            activity,
            BookViewModelFactory(repo)
        )[BookViewModel::class.java]
    }

    private val TAG = "Details_Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initializeAd()





        val bookModel = intent.getSerializableExtra("book_model") as BooksModel

        binding.apply {
            bookModel.apply {
                mBookTitle.text = title
                mAuthorName.text = author
                mBookDesc.text = description
                mBookImage.loadOnline(image)
            }

            // read button
            mReadBookBtn.setOnClickListener {

                loadinter() // interstetial ads load
                viewModel.downloadFile(bookModel.bookPDF, "${bookModel.title}.pdf")

            }


            val dialogBinding = LayoutProgressBinding.inflate(layoutInflater)
            val dialog = Dialog(activity).apply {
                setCancelable(false)
                setContentView(dialogBinding.root)
                this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                this.window!!.setLayout(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT
                )
            }

            viewModel.downloadLiveData.observe(activity) {
                when (it) {
                    is MyResponses.Error -> {
                        dialog.dismiss()
                        Log.e(TAG, "onCreate: ${it.errorMessage}")
                    }

                    is MyResponses.Loading -> {
                        dialogBinding.mProgress.text = "${it.progress}%"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            dialogBinding.mProgressBar.setProgress(it.progress, true)
                        } else {
                            dialogBinding.mProgressBar.progress = it.progress

                        }
                        dialog.show()
                        Log.i(TAG, "onCreate: Progress ${it.progress}")

                    }

                    is MyResponses.Success -> {
                        dialog.dismiss()

                        Log.i(TAG, "onCreate: Downloaded ${it.data}")
                        Intent().apply {
                            loadinter() // interstetial ads load
                            putExtra("book_pdf", it.data?.filePath)
                            setClass(activity, PdfActivity::class.java)
                            startActivity(this)
                        }
                    }

                }
            }

        }


    } // functions,,,,,,,,,,,,,,,,,,,,,,,,,




    //ads interstetial

    private fun initializeAd(){
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity,"ca-app-pub-5815431236783085/2961891710", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {

                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
            }
        })
    }
    private fun loadinter(){

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }




}


