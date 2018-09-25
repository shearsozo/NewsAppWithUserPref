package tourguide.android.example.com.newsapplist;

class ResultsInfo {
    private String pageNumber;
    private int totalPages;
    private String orderBy;

    ResultsInfo(String pageNumber, int totalPages, String orderBy) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.orderBy = orderBy;
    }

    ResultsInfo() {
        this("0", 0, null);
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
