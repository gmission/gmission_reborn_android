package hk.ust.gmission.core.api;

import java.util.ArrayList;
import java.util.List;

import hk.ust.gmission.util.GsonUtil;

/**
 * Created by rui on 14-5-5.
 */
public class QueryObject {
    private List<FilterObject> filters;
    private List<OrderObject> order_by;
    private int limit;
    private int offset;


    public QueryObject() {
        filters = new ArrayList<FilterObject>();
    }

    public void push(FilterObject obj){
        filters.add(obj);
    }

    public void push(String name, String op, String val){
        FilterObject filterObject = new FilterObject(name, op, val);
        filters.add(filterObject);
    }

    public void setLimit(int count){
        limit = count;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setOrder_by(String field, String direction){
        if(order_by == null){
            order_by = new ArrayList<OrderObject>();
        }
        order_by.add(new OrderObject(field, direction));
    }

    @Override
    public String toString() {
        return GsonUtil.getQueryString(this);
    }

    public void clear(){
        filters.clear();
        order_by.clear();
    }

}
