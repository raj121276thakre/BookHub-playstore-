package com.rajapps.bookify_test.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.rajapps.bookify_test.Adapters.HomeAdapter
import com.rajapps.bookify_test.Models.HomeModel
import com.rajapps.bookify_test.R
import com.rajapps.bookify_test.Repository.MainRepo
import com.rajapps.bookify_test.Utils.MyResponses
import com.rajapps.bookify_test.Utils.SpringScrollHelper
import com.rajapps.bookify_test.Utils.removeWithAnim
import com.rajapps.bookify_test.Utils.showWithAnim
import com.rajapps.bookify_test.ViewModels.MainViewModel
import com.rajapps.bookify_test.ViewModels.MainViewModelFactory


import com.rajapps.bookify_test.databinding.ActivityMainBinding
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val activity = this
    val list: ArrayList<HomeModel> = ArrayList()
    val adapter = HomeAdapter(list, activity)
    private val TAG = "MainActivity"
    private val repo = MainRepo(activity)
    private val viewModel by lazy {
        ViewModelProvider(activity, MainViewModelFactory(repo))[MainViewModel::class.java]
    }


    private var mInterstitialAd: InterstitialAd? = null // interstetial ad
    private val adDisplayInterval: Long =  3 * 60 * 1000 //  minutes in milliseconds Long = 1 * 60 * 1000


    // navigation
    lateinit var drawerLayout : DrawerLayout
    lateinit var coordinatorLayout : CoordinatorLayout
    lateinit var toolbar : Toolbar
    lateinit var framelayout : FrameLayout
    lateinit var navigationview : NavigationView
    var previousMenuItem : MenuItem? = null
    // navigation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadInterstitialAd() // interstetial ad
        showAdTime() //show ad in every   minutes



// navigation
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        framelayout = findViewById(R.id.framelayout)
        navigationview = findViewById(R.id.navigationview)

        setUpToolbar()



        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()






        navigationview.setNavigationItemSelectedListener {

            if (previousMenuItem != null){
                previousMenuItem?.isChecked =false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){



                R.id.ReferApp ->{

                    //code
                    Toast.makeText(this,"Not implemented yet!",Toast.LENGTH_LONG).show()
//                    val intent = Intent(this,FavouriteBooksActivity::class.java)
//                    startActivity(intent)
//                    supportActionBar?.title="Your Favourite Books"
                    drawerLayout.closeDrawers()
                }



//                R.id.rateUs ->{
//
//                    //code
//                    supportActionBar?.title="Rate Us"
//                    drawerLayout.closeDrawers()
//                }
//
//                R.id.terms ->{
//
//                    //code
//                    supportActionBar?.title="Terms Conditions"
//                    drawerLayout.closeDrawers()
//                }
//
//                R.id.privacy ->{
//
//                    //code
//                    supportActionBar?.title="Privacy Policy"
//                    drawerLayout.closeDrawers()
//                }
//
//                R.id.developer ->{
//
//                    //code
//                    supportActionBar?.title="Developer"
//                    drawerLayout.closeDrawers()
//                }

            }

            return@setNavigationItemSelectedListener true
        }

        // navigation


        binding.apply {

            mRvHome.adapter = adapter
            SpringScrollHelper().attachToRecyclerView(mRvHome)
            viewModel.getHomeData()
            handleHomeBackend()

            mErrorLayout.mTryAgainBtn.setOnClickListener {
                viewModel.getHomeData()
            }

        }


    }// functions defined below............................


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
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {

                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
            }
        }) // interstetial ad //

    }

    private fun handleHomeBackend() {
        viewModel.homeLiveData.observe(activity) {
            when (it) {
                is MyResponses.Error -> {
                    Log.i(TAG, "handleHomeBackend: ${it.errorMessage}")
                    binding.mErrorHolder.showWithAnim()
                    binding.mLoaderHolder.removeWithAnim()
                }

                is MyResponses.Loading -> {
                    Log.i(TAG, "handleHomeBackend: Loading...")
                    binding.mErrorHolder.removeWithAnim()
                    binding.mLoaderHolder.showWithAnim()
                }

                is MyResponses.Success -> {
                    binding.mErrorHolder.removeWithAnim()
                    binding.mLoaderHolder.removeWithAnim()
                    val tempList = it.data
                    list.clear()
                    Log.i(TAG, "handleHomeBackend: Success Called $tempList ")
                    tempList?.forEach {
                        list.add(it)

                    }
                    adapter.notifyDataSetChanged()
                }
            }

        }
    }


    //navigation
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="BookHub"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    //navigation




}










