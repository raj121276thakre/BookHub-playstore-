package com.rajapps.bookify_test.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rajapps.bookify_test.Activity.CategoryActivity
import com.rajapps.bookify_test.Activity.DetailsActivity
import com.rajapps.bookify_test.Models.BooksModel
import com.rajapps.bookify_test.Models.HomeModel
import com.rajapps.bookify_test.Utils.SpringScrollHelper
import com.rajapps.bookify_test.Utils.loadOnline
import com.rajapps.bookify_test.databinding.ItemBodBinding
import com.rajapps.bookify_test.databinding.ItemHomeBinding

const val LAYOUT_HOME = 0
const val LAYOUT_BOD = 1

class HomeAdapter(val list: ArrayList<HomeModel>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class HomeItemViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        val mViewPool = RecyclerView.RecycledViewPool()
        fun bind(model: HomeModel, context: Context) {
            binding.apply {
                model.apply {
                    mCategoryTitle.text = catTitle

                    mSeeAllBtn.setOnClickListener {


                        // handle here
                        val intent = Intent()
                        intent.putExtra("book_list", booksList)
                        intent.setClass(context, CategoryActivity::class.java)
                        //1
                        intent.putExtra("toolbar_tilte", catTitle)
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            mChildRvBooks,
                            mChildRvBooks.transitionName
                        )
                        context.startActivity(intent, options.toBundle())
                    }
                    if (booksList != null) {
                        mChildRvBooks.setupChildRv(booksList, context)
                    }
                }
            }

        }

        private fun RecyclerView.setupChildRv(list: ArrayList<BooksModel>, context: Context) {
            val adapter = HomeChildAdapter(list, context)
            this.adapter = adapter
            setRecycledViewPool(mViewPool)
            SpringScrollHelper().attachToRecyclerView(this)
        }
    }

    class BODItemViewHolder(val binding: ItemBodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: HomeModel, context: Context) {

            binding.apply {
                model.bod?.apply {
                    imageView.loadOnline(image)
                    mReadBookBtn.setOnClickListener {
                        //
                        Intent().apply {
                            putExtra("book_model", model.bod)
                            setClass(context, DetailsActivity::class.java)
                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                context as Activity,
                                cardView,
                                cardView.transitionName
                            )
                            context.startActivity(this, options.toBundle())
                        }
                    }
                }
            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        val model = list[position]
        return when (model.LAYOUT_TYPE) {
            LAYOUT_HOME -> LAYOUT_HOME
            else -> LAYOUT_BOD
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LAYOUT_HOME -> {
                HomeItemViewHolder(
                    ItemHomeBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                BODItemViewHolder(
                    ItemBodBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        when (model.LAYOUT_TYPE) {
            LAYOUT_HOME -> {
                (holder as HomeItemViewHolder).bind(model, context)
            }

            else -> {
                (holder as BODItemViewHolder).bind(model, context)
            }
        }
    }




}