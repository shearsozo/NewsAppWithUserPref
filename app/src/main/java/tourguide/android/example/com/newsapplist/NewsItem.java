package tourguide.android.example.com.newsapplist;

/**
 * JSON response from server is marshalled into List of NewsItem objects
 */
public class NewsItem {

    private String id;
    private String type;
    private String sectionName;
    private String webPublicationDate;
    private String webTitle;
    private String webUrl;
    private String author;

    // Unused fields from the JSON String
    private String sectionId;
    private String apiUrl;
    private String isHosted;
    private String pillarId;
    private String pillarName;

    NewsItem(String id, String type, String sectionId, String sectionName, String webPublicationDate, String webTitle, String webUrl, String apiUrl, String isHosted, String pillarId, String pillarName) {
        this.id = id;
        this.type = type;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.webTitle = webTitle;
        this.webUrl = webUrl;
        this.apiUrl = apiUrl;
        this.isHosted = isHosted;
        this.pillarId = pillarId;
        this.pillarName = pillarName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

}
