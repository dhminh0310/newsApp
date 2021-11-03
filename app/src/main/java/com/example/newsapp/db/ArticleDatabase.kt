package com.example.newsapp.db

import android.content.Context
import androidx.room.*
import com.example.newsapp.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters( Converters::class )
abstract class ArticleDatabase: RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

       fun getDatabaseInstace(context: Context): ArticleDatabase{
           return INSTANCE ?: Room.databaseBuilder(
               context.applicationContext,
               ArticleDatabase::class.java,
               "article_database"
           ).build().also { INSTANCE = it }
       }
    }

    fun hehe(){
        val list = mutableListOf<Int>()
        list.isEmpty()
    }
}