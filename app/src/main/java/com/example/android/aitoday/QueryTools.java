package com.example.android.aitoday;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryTools {

    private static final String LOG_TAG = QueryTools.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    private QueryTools() {
    }

    // Call the private methods to create an Article list for the ArticleLoader
    public static List<Article> fetchArticleData(String urlString) {
        URL url = createUrl(urlString);
        String jsonResponse = null;

        try {
            jsonResponse = requestData(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem fetching data.", e);
        }

        List<Article> articles = extractJsonData(jsonResponse);

        return articles;
    }

    // Convert from String to URL
    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating the URL.", e);
        }
        return url;
    }

    // Connect to network and request data using the URL created
    private static String requestData(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = convertResponse(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem requesting data.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    // Convert the InputStream using an InputStreamReader and a BufferedReader
    private static String convertResponse(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }

        return stringBuilder.toString();
    }

    // Use the JSON response to create a list of articles
    private static List<Article> extractJsonData(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Article> articles = new ArrayList<>();

        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentArticle = resultsArray.getJSONObject(i);

                String title = currentArticle.getString("webTitle");
                String section = currentArticle.getString("sectionName");
                String date = currentArticle.getString("webPublicationDate");
                String url = currentArticle.getString("webUrl");

                String author = "";
                JSONArray tagsArray = currentArticle.getJSONArray("tags");
                if (tagsArray != null && tagsArray.length() > 0 ) {
                    JSONObject tagsObject = tagsArray.getJSONObject(0);
                    author = tagsObject.getString("webTitle");
                }

                Article article = new Article(title, section, date, author, url);
                articles.add(article);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem extracting data.", e);
        }

        return articles;
    }
}