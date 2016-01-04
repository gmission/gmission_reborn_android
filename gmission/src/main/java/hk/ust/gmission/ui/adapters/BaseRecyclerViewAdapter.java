package hk.ust.gmission.ui.adapters;

import android.support.v7.widget.RecyclerView;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hk.ust.gmission.Injector;
import hk.ust.gmission.models.BaseModel;

/**
 * Created by bigstone on 21/12/2015.
 */
public abstract class BaseRecyclerViewAdapter<E extends RecyclerView.ViewHolder, I extends BaseModel> extends RecyclerView.Adapter<E> {

    /**
     * List items provided
     */
    public List<I> items = new ArrayList<>();

    @Inject protected Bus bus;

    public BaseRecyclerViewAdapter() {
        Injector.inject(this);
    }

    public void setItems(List<I> newList) {
        items = newList;
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

    public void removeItem(String itemId){
        for (I item: items) {
            if (item.getId().equals(itemId)){
                items.remove(item);
                break;
            }
        }
    }

    public void appendItem(I item){
        items.add(item);
    }


}
