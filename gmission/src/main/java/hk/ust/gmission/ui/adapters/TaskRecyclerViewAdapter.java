package hk.ust.gmission.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.R;
import hk.ust.gmission.events.HitItemClickEvent;
import hk.ust.gmission.models.Hit;

/**
 * Created by bigstone on 6/1/2016.
 */
public class TaskRecyclerViewAdapter extends BaseRecyclerViewAdapter<TaskRecyclerViewAdapter.ViewHolder, Hit>  {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new ViewHolder(itemView);
    }

    public TaskRecyclerViewAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Hit hit = items.get(position);
        holder.title.setText(String.format("%s", hit.getTitle()));
        holder.content.setText(hit.getDescription());

        if (hit.getStatus().equals("closed")){
            holder.hitIcon.setImageResource(R.drawable.ic_task_completed);
        }

        if (hit.getStatus().equals("open")){
            holder.hitIcon.setImageResource(R.drawable.ic_task_processing);
        }


    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.task_status) ImageView hitIcon;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.answer_count) TextView content;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            bus.post(new HitItemClickEvent(view));
        }
    }
}