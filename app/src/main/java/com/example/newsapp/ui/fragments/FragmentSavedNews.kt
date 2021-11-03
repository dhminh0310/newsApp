package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.models.Article
import com.example.newsapp.ui.adapter.NewsAdapter
import com.example.newsapp.ui.fragments.base.BaseFragment
import com.example.newsapp.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class FragmentSavedNews : BaseFragment(R.layout.fragment_saved_news) {

    private val TAG = "FragmentSavedNews"
    lateinit var newsAdapter: NewsAdapter

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_news, container, false)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setOnItemClickListener(object : NewsAdapter.OnItemClickListener{
            override fun onClick(article: Article) {
                val actionClickArticle = FragmentSavedNewsDirections.actionFragmentSavedNewsToFragmentArticle(article)
                findNavController().navigate(actionClickArticle)
            }
        })

        val onItemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view, "Deleted article successfully", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        newsViewModel.saveArticle(article)
                    }.show()
                }
            }
        }
        ItemTouchHelper(onItemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        newsViewModel.allSavedNews.observe(viewLifecycleOwner){listArticles ->
            newsAdapter.differ.submitList(listArticles)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}