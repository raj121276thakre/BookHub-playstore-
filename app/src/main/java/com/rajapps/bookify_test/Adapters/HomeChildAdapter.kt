package com.rajapps.bookify_test.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.rajapps.bookify_test.Activity.DetailsActivity
import com.rajapps.bookify_test.Models.BooksModel
import com.rajapps.bookify_test.Utils.loadOnline
import com.rajapps.bookify_test.databinding.ItemBookBinding

class HomeChildAdapter(val list: ArrayList<BooksModel>, val context: Context) :
    RecyclerView.Adapter<HomeChildAdapter.ChildViewHolder>() {

    class ChildViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: BooksModel, context: Context) {
            binding.apply {
                model.apply {
                    imageView.loadOnline(image)
                    cardView.setOnClickListener {
                        Intent().apply {
                            putExtra("book_model", model)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        return ChildViewHolder(ItemBookBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model, context)
    }
}