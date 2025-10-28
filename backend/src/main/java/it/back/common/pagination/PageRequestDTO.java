package it.back.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

public class PageRequestDTO {
    private int page = 0; // 0부터 시작
    private int size = 10;
    private List<String> sort; // 예: ["uid,asc", "title,desc"]

    public PageRequestDTO() {}
    public PageRequestDTO(int page, int size, List<String> sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public List<String> getSort() { return sort; }
    public void setSort(List<String> sort) { this.sort = sort; }

    public Pageable toPageable() {
        List<Order> orders = new ArrayList<>();
        if (this.getSort() != null && !this.getSort().isEmpty()) {
            List<String> sortList = this.getSort();

            boolean isCommaSeparated = sortList.stream().anyMatch(s -> s.contains(","));

            if(isCommaSeparated) {
                for (String sort : sortList) {
                    String[] parts = sort.split(",", 2);
                    String property = parts[0];
                    Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
                    orders.add(new Order(direction, property));
                }
            } else {
                for (int i = 0; i < sortList.size(); i++) {
                    String property = sortList.get(i);
                    Sort.Direction direction = Sort.Direction.ASC;
                    if (i + 1 < sortList.size()) {
                        String next = sortList.get(i + 1);
                        if ("asc".equalsIgnoreCase(next) || "desc".equalsIgnoreCase(next)) {
                            direction = Sort.Direction.fromString(next);
                            i++;
                        }
                    }
                    orders.add(new Order(direction, property));
                }
            }
        }
        return PageRequest.of(this.getPage(), this.getSize(), Sort.by(orders));
    }
}
