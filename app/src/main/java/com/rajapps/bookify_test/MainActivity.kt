package com.rajapps.bookify_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.rajapps.bookify_test.Adapters.HomeAdapter
import com.rajapps.bookify_test.Models.HomeModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            mRvHome.adapter = adapter
            SpringScrollHelper().attachToRecyclerView(mRvHome)
            viewModel.getHomeData()
            handleHomeBackend()

            mErrorLayout.mTryAgainBtn.setOnClickListener {
                viewModel.getHomeData()
            }

        }

       // supportActionBar?.title= ""

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

}










