package hk.ust.gmission.models.wrapper;

import java.util.List;

import hk.ust.gmission.models.dao.Campaign;

/**
 * Created by bigstone on 23/12/2015.
 */
public class CampaignWrapper {
    private List<Campaign> objects;
    private int total_pages;
    private int num_results;
    private int page;

    public List<Campaign> getObjects() {
        return objects;
    }

    public void setObjects(List<Campaign> objects) {
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
