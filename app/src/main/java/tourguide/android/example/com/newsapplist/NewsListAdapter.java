package tourguide.android.example.com.newsapplist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ListAdapter to populate the news item results
 */
class NewsListAdapter extends BaseAdapter {

    private final Context context;
    private List<NewsItem> newsItems;
    private ResultsInfo resultsInfo;
    private PreferencesData preferencesData;

    NewsListAdapter(Context context) {
        this.context = context;
        this.newsItems = new ArrayList<>();
    }

    /**
     *
     * @param nextPageItems - Adds all the items from input list to the list of items to be shown in the view
     * @param resultsInfo - Metadata of the results such as page count and order by value etc.
     */
    public void addNextPageToView(List<NewsItem> nextPageItems, ResultsInfo resultsInfo, PreferencesData preferencesData) {
        newsItems.addAll(nextPageItems);
        this.resultsInfo = resultsInfo;
        this.preferencesData = preferencesData;
    }

    /**
     * Clears all the newsitem objects from the view
     */
    public void clear() {
        newsItems.clear();
        resultsInfo = null;
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public Object getItem(int i) {
        return newsItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        String id = newsItems.get(i).getId();
        return id.hashCode();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.news_item_row, viewGroup, false);
            assert view != null;
        }

        final NewsItem item = newsItems.get(i);
        ViewHolder viewHolder = new ViewHolder(view);
        showTextviewIfDataAvailable(viewHolder.newsItemTitle, item.getWebTitle());
        showTextviewIfDataAvailable(viewHolder.sectionName, item.getSectionName());
        showTextviewIfDataAvailable(viewHolder.webPublicationDate, item.getWebPublicationDate());
        showTextviewIfDataAvailable(viewHolder.author, item.getAuthor());


        // To open newsitem in the browser when clicked, we are registering an onclick listener
        View.OnClickListener openNewsItemUrl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(item.getWebUrl()));
                NewsListAdapter.this.context.startActivity(intent);
            }
        };
        viewHolder.newsRowItemContainer.setOnClickListener(openNewsItemUrl);

        return view;
    }

    private void showTextviewIfDataAvailable(TextView textView, String str) {
        boolean isDataAvailable = str != null && !str.isEmpty();
        if(isDataAvailable) {
            textView.setText(str);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    public ResultsInfo getResultsInfo() {
        return resultsInfo;
    }

    public void setResultsInfo(ResultsInfo resultsInfo) {
        this.resultsInfo = resultsInfo;
    }

    public PreferencesData getPreferencesData() {
        return preferencesData;
    }

    public class ViewHolder {
        // UI controls from the news_item_row layout xml file
        public View layout;
        public TextView newsItemTitle;
        TextView sectionName;
        TextView webPublicationDate;
        LinearLayout newsRowItemContainer;
        TextView author;

        ViewHolder(View v) {
            layout = v;
            newsItemTitle = (TextView) v.findViewById(R.id.newsItemTitle);
            sectionName = (TextView) v.findViewById(R.id.section_name);
            webPublicationDate = (TextView) v.findViewById(R.id.publication_date);
            newsRowItemContainer = (LinearLayout) v.findViewById(R.id.newsRowItemContainer);
            author = (TextView) v.findViewById(R.id.author);
        }
    }


}
