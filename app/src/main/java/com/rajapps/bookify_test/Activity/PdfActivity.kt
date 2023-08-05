package com.rajapps.bookify_test.Activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rajapps.bookify_test.databinding.ActivityPdfBinding
import java.util.Timer
import java.util.TimerTask

class PdfActivity : AppCompatActivity() {
    val activity = this

    private var mInterstitialAd: InterstitialAd? = null // interstetial ad
    private var adUnitId = "ca-app-pub-5815431236783085/8163490774" // interstetial ad unit id
    private val adDisplayInterval: Long =
        10 * 60 * 1000 // 4 minutes in milliseconds Long = 1 * 60 * 1000


    lateinit var binding: ActivityPdfBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfBinding.inflate(layoutInflater)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(binding.root)



        loadInterstitialAd() // interstetial ad
        showAdTime() //show ad in every   minutes


        supportActionBar?.hide()

        binding.apply {
            val bookPDF = intent.getStringExtra("book_pdf").toString()
            pdfView.fromUri(Uri.parse(bookPDF))
                .swipeHorizontal(true)
                .enableSwipe(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .load()
        }

    }


    private fun showAdTime() {
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
        InterstitialAd.load(this, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {

                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
            }
        }) // interstetial ad //

    }


}