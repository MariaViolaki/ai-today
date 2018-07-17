package com.example.android.aitoday;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private String mDataUrl;

    public ArticleLoader(Context context, String dataUrl) {
        super(context);
        mDataUrl = dataUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // Use QueryTools to load a list of Articles
    @Override
    public List<Article> loadInBackground() {
        if (mDataUrl == null) {
            return null;
        }

        List<Article> articles = QueryTools.fetchArticleData(mDataUrl);

        return articles;
    }
}