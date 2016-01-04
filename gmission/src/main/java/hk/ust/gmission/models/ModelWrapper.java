package hk.ust.gmission.models;

import java.util.List;

/**
 * Created by bigstone on 1/1/2016.
 */
public class ModelWrapper<T> {
    private List<T> objects;
    private int total_pages;
    private int num_results;
    private int page;

    public List<T> getObjects() {
        return objects;
    }

    public void setObjects(List<T> objects) {
        this.objects = objects;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getNum_results() {
        return num_results;
    }

    public void setNum_results(int num_results) {
        this.num_results = num_results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
