package com.rajapps.bookify_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rajapps.bookify_test.Adapters.CategoryAdapter
import com.rajapps.bookify_test.Models.BooksModel
import com.rajapps.bookify_test.Utils.SpringScrollHelper
import com.rajapps.bookify_test.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCategoryBinding.inflate(layoutInflater)
    }
    private val activity = this

    private val list = ArrayList<BooksModel>()
    private val adapter = CategoryAdapter(list, activity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            mRvCategory.adapter = adapter
            SpringScrollHelper().attachToRecyclerView(mRvCategory)
            val bookList = intent.getSerializableExtra("book_list") as ArrayList<BooksModel>
            bookList.forEach {
                list.add(it)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        with(window) {
            sharedElementReenterTransition = null
            sharedElementReturnTransition = null
        }
        binding.mRvCategory.transitionName = null
    }
}