package com.example.android.aitoday;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ARTICLE_REQUEST_URL = "https://content.guardianapis.com/search?";
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
        } else {
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
                // Check if there is an app that can handle the event
                if (articleIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(articleIntent);
                }
            }
        });
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get preference for the preferred order of articles displayed
        String orderBy = sharedPreferences.
                getString(getString(R.string.articles_order_key),
                        getString(R.string.articles_order_default));

        // Get preference for the preferred number of articles displayed
        String articlesNumber = sharedPreferences.
                getString(getString(R.string.articles_number_key),
                        getString(R.string.articles_number_default));

        // Edit the initial URL to add the appropriate parameters
        Uri uri = Uri.parse(ARTICLE_REQUEST_URL);
        Uri.Builder uriBuilder = uri.buildUpon();

        uriBuilder.appendQueryParameter("q", "\"artificial intelligence\"");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "1e5d5e05-66fd-49b5-85c0-cf26d845cb67");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", articlesNumber);

        return new ArticleLoader(this, uriBuilder.toString());
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

    // Create the Settings menu_main
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Switch to SettingsActivity using an Intent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_item) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Refresh the list of Articles according to the preference changes
        if (key.equals(getString(R.string.articles_order_key)) ||
                key.equals(getString(R.string.articles_number_key))) {
            mArticleAdapter.clear();

            mDefaultView.setVisibility(View.GONE);
            View progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);

            // Restart the Loader to collect the appropriate data
            getLoaderManager().restartLoader(1, null, this);
        }
    }
}