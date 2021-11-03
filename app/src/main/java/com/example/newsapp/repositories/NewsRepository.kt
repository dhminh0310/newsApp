package com.example.newsapp.repositories

import com.example.newsapp.db.ArticleDao
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.models.Article
import com.example.newsapp.network.RetrofitInstance

class NewsRepository(
    val articleDao: ArticleDao
) {
    suspend fun getFreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getFreakingNews(countryCode = countryCode, pageNumber = pageNumber)
    suspend fun searchNews(searchQuery: String, pageNumber: Int) = RetrofitInstance.api.searchForNews(searchQuery, pageNumber)
    suspend fun upsertArticle(article: Article) = articleDao.upsert(article)
    suspend fun deleteArticle(article: Article) = articleDao.delete(article)
    fun getSavedNews() = articleDao.getAllArticles()
}