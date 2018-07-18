package com.example.android.aitoday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    // Display different Article information depending on the position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        TextView sectionView = convertView.findViewById(R.id.section);
        sectionView.setText(currentArticle.getSection());

        TextView titleView = convertView.findViewById(R.id.title);
        titleView.setText(currentArticle.getTitle());

        TextView dateView = convertView.findViewById(R.id.date);
        dateView.setText(formatDate(currentArticle.getDate()));

        TextView authorView = convertView.findViewById(R.id.author);
        authorView.setText(currentArticle.getAuthor());

        return convertView;
    }

    private String formatDate(String date) {

        date = date.substring(0, 10);
        String[] dateParts = date.split("-");
        String day = dateParts[2];
        String month = dateParts[1];
        String year = dateParts[0];

        // Remove the first zero from the day if it exists
        if (day.startsWith("0")) {
            day = day.substring(1);
        }

        // Convert month number to its abbreviation
        switch (month) {
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
        }

        // Concatenate the full formatted date
        date = day + " " + month + " " + year;

        return date;
    }
}