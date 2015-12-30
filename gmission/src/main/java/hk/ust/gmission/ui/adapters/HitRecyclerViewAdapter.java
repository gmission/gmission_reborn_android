package hk.ust.gmission.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.R;
import hk.ust.gmission.events.CampaignItemClickEvent;
import hk.ust.gmission.models.dao.Hit;


public class HitRecyclerViewAdapter extends BaseRecyclerViewAdapter<HitRecyclerViewAdapter.HitViewHolder, Hit> {

    @Override
    public HitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hit_item, parent, false);

        return new HitViewHolder(itemView);
    }

    @Inject
    public HitRecyclerViewAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(HitViewHolder holder, int position) {
        Hit campaign = items.get(position);
        holder.id.setText(String.format("%s %s",
                campaign.getTitle(), campaign.getId()));
        holder.content.setText(campaign.getDescription());
    }


    class HitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        @Bind(R.id.id) TextView id;
        @Bind(R.id.content) TextView content;

        public HitViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            bus.post(new CampaignItemClickEvent(view));
        }
    }
}