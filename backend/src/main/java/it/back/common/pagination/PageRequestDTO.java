package it.back.common.pagination;

public class PageRequestDTO {
    private int page = 0; // 0부터 시작
    private int size = 10;
    private String[] sort; // 예: ["uid,asc", "title,desc"]

    public PageRequestDTO() {}
    public PageRequestDTO(int page, int size, String[] sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String[] getSort() { return sort; }
    public void setSort(String[] sort) { this.sort = sort; }
}