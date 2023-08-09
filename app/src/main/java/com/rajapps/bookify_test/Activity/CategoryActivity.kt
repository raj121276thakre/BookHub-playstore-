package com.rajapps.bookify_test.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rajapps.bookify_test.Adapters.CategoryAdapter
import com.rajapps.bookify_test.Models.BooksModel
import com.rajapps.bookify_test.R
import com.rajapps.bookify_test.Utils.SpringScrollHelper
import com.rajapps.bookify_test.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCategoryBinding.inflate(layoutInflater)
    }
    private val activity = this

    private val list = ArrayList<BooksModel>()
    private val adapter = CategoryAdapter(list, activity)



    //toolbar category

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar

    //toolbar category
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // toolbar category

        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)

        // toolbar category

        binding.apply {

            mRvCategory.adapter = adapter
            SpringScrollHelper().attachToRecyclerView(mRvCategory)
            val bookList = intent.getSerializableExtra("book_list") as ArrayList<BooksModel>
            //2
            val catTitle = intent.getStringExtra("toolbar_tilte") as String
            //supportActionBar?.title = catTitle
            setUpToolbar(catTitle)

            bookList.forEach {
                list.add(it)
            }
        }


    }// functions below






    //toolbar
    fun setUpToolbar(Title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = Title
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    //toolbar

    override fun onBackPressed() {
        finish()
        with(window) {
            sharedElementReenterTransition = null
            sharedElementReturnTransition = null
        }
        binding.mRvCategory.transitionName = null
    }
}