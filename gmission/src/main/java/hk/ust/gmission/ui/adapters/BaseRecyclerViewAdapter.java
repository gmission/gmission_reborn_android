package hk.ust.gmission.ui.adapters;

import android.support.v7.widget.RecyclerView;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by bigstone on 21/12/2015.
 */
public abstract class BaseRecyclerViewAdapter<E extends RecyclerView.ViewHolder, I> extends RecyclerView.Adapter<E> {

    /**
     * List items provided
     */
    protected List<I> items = new ArrayList<I>();

    @Inject
    protected Bus bus;



    public void setNewsList(List<I> newsList) {
        items = newsList;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public I getItem(int position) {
        return items.get(position);
    }

    public void addNewItem(I item){

        items.add(0,item);

    }

    public void appendItem(I item){
        items.add(item);
    }
}
