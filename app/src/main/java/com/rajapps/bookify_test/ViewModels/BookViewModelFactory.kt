package com.rajapps.bookify_test.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rajapps.bookify_test.Repository.BookRepo

class BookViewModelFactory(private val repo: BookRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookViewModel(repo) as T
    }
}