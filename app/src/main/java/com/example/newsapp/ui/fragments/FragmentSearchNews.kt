package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.models.Article
import com.example.newsapp.ui.adapter.NewsAdapter
import com.example.newsapp.ui.fragments.base.BaseFragment
import com.example.newsapp.utils.Constant
import com.example.newsapp.utils.Constant.SEARCH_NEWS_TIME_DELAY
import com.example.newsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_freaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentSearchNews : BaseFragment(R.layout.fragment_search_news) {

    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setOnItemClickListener(object : NewsAdapter.OnItemClickListener{
            override fun onClick(article: Article) {
                val actionClickArticle = FragmentSearchNewsDirections.actionFragmentSearchNewsToFragmentArticle(article)
                findNavController().navigate(actionClickArticle)
            }
        })

        var job: Job? = null
        etSearch.addTextChangedListener { text ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                text?.let {
                    if(text.toString().isNotEmpty()){
                        newsViewModel.searchNewsPage = 1
                        newsViewModel.searchNewsResponse = null
                        newsViewModel.searchNews(text.toString())
                    }
                }
            }
        }

        btnClearText.setOnClickListener{
            etSearch.setText("")
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner){ newsResponse ->
            when(newsResponse){
                is Resource.Success -> {
                    hideProgressBar()
                    newsResponse.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constant.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        /*if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }*/
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    newsResponse.message?.let { message ->
                        Toast.makeText(activity, "$message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = rvSearchNews.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCall = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCall >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constant.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                newsViewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@FragmentSearchNews.scrollListener)
        }
    }
}