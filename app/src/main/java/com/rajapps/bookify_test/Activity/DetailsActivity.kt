package com.rajapps.bookify_test.Activity

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
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
    private var adUnitId = "ca-app-pub-3940256099942544/1033173712" // interstetial ad unit id
    private val adDisplayInterval: Long =   4 * 60 * 1000 // 4 minutes in milliseconds Long = 1 * 60 * 1000

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


        loadInterstitialAd() // interstetial ad
        showAdTime() //show ad in every   minutes



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

                //loadinter() // interstetial ads load
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

                            putExtra("book_pdf", it.data?.filePath)
                            setClass(activity, PdfActivity::class.java)
                            startActivity(this)
                        }
                    }

                }
            }

        }


    } // functions,,,,,,,,,,,,,,,,,,,,,,,,,





    private fun showAdTime(){
        val timer = Timer()
        val adDisplayTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(activity)
                    } else {
                        // The interstitial ad was not loaded yet, you may want to handle this case.
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    }
                    loadInterstitialAd() // Load a new ad for the next display
                }
            }
        }

        timer.schedule(adDisplayTask, adDisplayInterval, adDisplayInterval)
    }

    private fun loadInterstitialAd() {

        // interstetial ads ca-app-pub-3940256099942544/1033173712
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {

                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
            }
        }) // interstetial ad //

    }

}


