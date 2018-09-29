package tourguide.android.example.com.newsapplist;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs HTTP Request and handles responses
 */
class NetworkUtils {

    // URL from which HTTP Request is received
    private static final String GAURDIAN_DATA_URL = "https://content.guardianapis.com/search?q=debate&from-date=2014-01-01&api-key=a85b461a-aecc-424e-a1b3-7fe8bed68ae2&show-tags=contributor";
    private static final String GAURDIAN_SECTIONS_URL = "https://content.guardianapis.com/sections?api-key=a85b461a-aecc-424e-a1b3-7fe8bed68ae2";
    private String sectionNamePreference = null;
    private String orderDate = null;
    private ResultsInfo resultsInfo;
    private static PreferencesData preferencesData = new PreferencesData();
    private String orderBy = null;

    NetworkUtils(String orderByPreference, String orderdate, String sectionNamePreference) {
        this.resultsInfo = new ResultsInfo();
        this.orderBy = orderByPreference;
        this.orderDate = orderdate;
        this.sectionNamePreference = sectionNamePreference;
    }

    private MainActivity.NewsItemsResponse<NewsItem> getResponseFromHttpUrl() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String newsJSONString = null;

        // page - attribute returns the results of the given page number
        Uri.Builder builder = Uri.parse(GAURDIAN_DATA_URL).buildUpon();
        builder.appendQueryParameter("page", resultsInfo.getPageNumber());
        if(orderBy != null) {
            builder.appendQueryParameter("order-by", orderBy);
        } else {
            builder.appendQueryParameter("order-by", "newest");
        }

        if(orderDate != null) {
            builder.appendQueryParameter("order-date", orderDate);
        } else {
            builder.appendQueryParameter("order-date", "published");
        }

        if(sectionNamePreference != null) {
            builder.appendQueryParameter("section", sectionNamePreference);
        }
        Uri builtURI = builder.build();

        try {
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    if (sb.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    newsJSONString = sb.toString();
                    break;
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    return new MainActivity.NewsItemsResponse<NewsItem>(null, resultsInfo, null, new NewsItemFetchException("Server unavailable. Please try again in sometime!"));
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    return new MainActivity.NewsItemsResponse<NewsItem>(null, resultsInfo, null, new NewsItemFetchException("Something went wrong! Please try again in sometime!"));
                case 429:
                    return new MainActivity.NewsItemsResponse<NewsItem>(null, resultsInfo, null, new NewsItemFetchException("Server API rate limit exceeded. Please try again in sometime!"));
                default:
                    System.out.println("Received a non success response: [" + responseCode + "]");
                    break;
            }
            preferencesData.clear();
            List<NewsItem> newsItems = parseJSON(newsJSONString);
            return new MainActivity.NewsItemsResponse<NewsItem>(newsItems, resultsInfo, preferencesData, null);
        } catch (UnknownHostException e) {
            return new MainActivity.NewsItemsResponse<NewsItem>(null, resultsInfo, null, e);
        } catch (IOException e) {
            e.printStackTrace();
            return new MainActivity.NewsItemsResponse<NewsItem>(null, resultsInfo, null, e);
        } finally {
            // close the reader and http connections
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static void getSectionsFromHttpUrl() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String sectionsJSONString = null;

        // page - attribute returns the results of the given page number
        Uri.Builder builder = Uri.parse(GAURDIAN_SECTIONS_URL).buildUpon();

        Uri builtURI = builder.build();

        try {
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    if (inputStream == null) {
                        // Nothing to do.
                        return;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    if (sb.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return;
                    }
                    sectionsJSONString = sb.toString();
                    break;
                default:
                    System.out.println("Received a non success response: [" + responseCode + "]");
                    break;
            }
            preferencesData.clear();
            parseSections(sectionsJSONString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close the reader and http connections
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * 
     * @param newsJSONString - The response JSON string received from the server
     *                       If null, empty list is returned.
     * @return - List of NewsItem objects that are part of the JSON String
     */
    private List<NewsItem> parseJSON(String newsJSONString) {
        List<NewsItem> newsItems = new ArrayList<>();
        if(newsJSONString == null) {
            return newsItems;
        }
        try {
            JSONObject newsResponse = new JSONObject(newsJSONString);
            JSONObject httpResponse = newsResponse.getJSONObject("response");
            if (isFirstPage()) {
//                pageSize = httpResponse.getInt("pageSize");
                resultsInfo.setTotalPages(httpResponse.getInt("pages"));
                resultsInfo.setOrderBy(httpResponse.getString("orderBy"));
//                totalNewsStories = httpResponse.getInt("total"); //Unused as of now
            }
            JSONArray newsItemResultsArray = httpResponse.getJSONArray("results");
            for (int i = 0; i < newsItemResultsArray.length(); i++) {
                JSONObject newsItemJSON = newsItemResultsArray.getJSONObject(i);
                String sectionId = newsItemJSON.getString("sectionId");
                String sectionName = newsItemJSON.getString("sectionName");
                preferencesData.addSection(sectionId, sectionName);
                NewsItem item = new NewsItem(newsItemJSON.getString("id"),
                        newsItemJSON.getString("type"),
                        sectionId,
                        sectionName,
                        newsItemJSON.getString("webPublicationDate"),
                        newsItemJSON.getString("webTitle"),
                        newsItemJSON.getString("webUrl"),
                        newsItemJSON.getString("apiUrl"),
                        newsItemJSON.getString("isHosted"),
                        newsItemJSON.getString("pillarId"),
                        newsItemJSON.getString("pillarName"));
                setAuthorNameIfExists(newsItemJSON, item);
                newsItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsItems;
    }


    private static void parseSections(String sectionsJSONString) {
        try {
            JSONObject newsResponse = new JSONObject(sectionsJSONString);
            JSONObject httpResponse = newsResponse.getJSONObject("response");
            JSONArray sectionsResultsArray = httpResponse.getJSONArray("results");
            for (int i = 0; i < sectionsResultsArray.length(); i++) {
                JSONObject newsItemJSON = sectionsResultsArray.getJSONObject(i);
                String sectionId = newsItemJSON.getString("id");
                String sectionName = newsItemJSON.getString("webTitle");
                preferencesData.addSection(sectionId, sectionName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 
     * @param newsItemJSON - Each news item object that may or may not contain an author field
     * @param item - Corresponding Java model object of each news items
     * @throws JSONException - if the newsItemJSON is not well formed, this exception will be thrown
     */
    private void setAuthorNameIfExists(JSONObject newsItemJSON, NewsItem item) throws JSONException {
        JSONArray requestedTags = newsItemJSON.getJSONArray("tags");
        for (int j = 0; j < requestedTags.length(); j++) {
            JSONObject tagObject = requestedTags.getJSONObject(j);
            if (tagObject != null) {
                String authorFullName = tagObject.getString("webTitle");
                if(authorFullName != null && !authorFullName.isEmpty()) {
                    item.setAuthor(authorFullName);
                }
            }
        }
    }

    public MainActivity.NewsItemsResponse<NewsItem> getNextPage() {
        resultsInfo.setPageNumber(String.valueOf(Integer.parseInt(resultsInfo.getPageNumber(), 10) + 1));
        return getResponseFromHttpUrl();
    }

    private boolean isFirstPage() {
        return Integer.parseInt(resultsInfo.getPageNumber(), 10) == 1;
    }

    public boolean hasMorePages() {
        return Integer.parseInt(resultsInfo.getPageNumber(), 10) == 0 || Integer.parseInt(resultsInfo.getPageNumber(), 10) < resultsInfo.getTotalPages();
    }

    static class NewsItemFetchException extends Exception {
        NewsItemFetchException(String message) {
            super(message);
        }
    }
}
