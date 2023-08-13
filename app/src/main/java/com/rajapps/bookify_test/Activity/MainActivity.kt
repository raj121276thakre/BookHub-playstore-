package com.rajapps.bookify_test.Activity


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
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


    // navigation
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var framelayout: FrameLayout
    lateinit var navigationview: NavigationView
    var previousMenuItem: MenuItem? = null
    // navigation


    // admob open ad
    private var mAppOpenAd: AppOpenAd? = null
    private var isShowingAd = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // in app review
        showReview()

// navigation
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        framelayout = findViewById(R.id.framelayout)
        navigationview = findViewById(R.id.navigationview)

        setUpToolbar()


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()



        navigationview.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {

                R.id.ReferApp -> {
                    //code

                    val appPackageName = packageName // Get your app's package name
                    val playStoreLink =
                        "https://play.google.com/store/apps/details?id=$appPackageName"
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this awesome app!")
                    shareIntent.putExtra(Intent.EXTRA_TEXT, playStoreLink)
                    startActivity(Intent.createChooser(shareIntent, "Refer the App using:"))


                    drawerLayout.closeDrawers()
                }


                R.id.rateUs -> {

                    val appPackageName = packageName // Get your app's package name

                    try {
                        // Open the Play Store page of your app
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        // If the Play Store app is not installed, open the Play Store website
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                            )
                        )
                    }
                    drawerLayout.closeDrawers()
                }
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


    }// functions defined below...........................................................


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
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "BookHub"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    //navigation

    // in app review

    private fun showReview(){
        val manager = ReviewManagerFactory.create(applicationContext)
        manager.requestReviewFlow().addOnCompleteListener {
            if (it.isSuccessful){
                manager.launchReviewFlow(this, it.result)
            }
        }
    }



    // open ads
    private fun showOpenAd() {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(this, "ca-app-pub-5815431236783085/1042283303",
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                }

                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    super.onAdLoaded(appOpenAd)
                    mAppOpenAd = appOpenAd
                    if (!isShowingAd) {
                        mAppOpenAd!!.show(this@MainActivity)
                        isShowingAd = true
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        if (!isShowingAd) {
            showOpenAd()
        }
    }



}










