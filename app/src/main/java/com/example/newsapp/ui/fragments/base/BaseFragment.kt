package com.example.newsapp.ui.fragments.base

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.ui.viewmodels.NewsViewModel

open class BaseFragment(private val layout: Int) : Fragment(layout) {

    val newsViewModel: NewsViewModel by activityViewModels()
    //lateinit var newsViewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //newsViewModel = (activity as MainActivity).newsViewModel
    }
}