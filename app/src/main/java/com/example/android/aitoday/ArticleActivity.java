package com.example.android.aitoday;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final String ARTICLE_REQUEST_URL =
            "https://content.guardianapis.com/search?q=%22artificial%20intelligence%22&api-key=1e5d5e05-66fd-49b5-85c0-cf26d845cb67";
    private ArticleAdapter mArticleAdapter;
    private TextView mDefaultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_activity);

        // Bind the list of Articles to the custom ArticleAdapter
        ListView articleList = findViewById(R.id.article_list);
        mArticleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleList.setAdapter(mArticleAdapter);

        // Show default view in case the custom adapter is empty
        mDefaultView = findViewById(R.id.default_view);
        articleList.setEmptyView(mDefaultView);

        // Check for network connectivity
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Initialize the Loader
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);
        }
        else {
            // Show error message if there is no internet connection
            View progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            mDefaultView.setText(R.string.no_connection);
        }

        // Open the article on a browser when a list item is clicked on
        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Article currentArticle = mArticleAdapter.getItem(position);
                Uri articleUri = Uri.parse(currentArticle.getUrl());
                Intent articleIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(articleIntent);
            }
        });
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        return new ArticleLoader(this, ARTICLE_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {

        // Since there are data, hide the progress bar
        View progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        mDefaultView.setText(R.string.no_articles);

        // Clear existing data before loading anew
        mArticleAdapter.clear();

        if (articles != null && !articles.isEmpty()) {
            mArticleAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mArticleAdapter.clear();
    }
}