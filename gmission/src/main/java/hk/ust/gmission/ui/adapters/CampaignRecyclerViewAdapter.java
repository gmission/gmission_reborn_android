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
import hk.ust.gmission.events.CampaignItemClickEvent;
import hk.ust.gmission.models.Campaign;

public class CampaignRecyclerViewAdapter extends BaseRecyclerViewAdapter<CampaignRecyclerViewAdapter.ViewHolder, Campaign> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.campaign_item, parent, false);

        return new ViewHolder(itemView);
    }

    public CampaignRecyclerViewAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Campaign campaign = items.get(position);
        holder.title.setText(String.format("%s",
                campaign.getTitle()));
        holder.content.setText(campaign.getBrief());

        if (campaign.getStatus().equals("closed")){
            holder.statusIcon.setImageResource(R.drawable.ic_task_completed);
        }

        if (campaign.getStatus().equals("open")){
            holder.statusIcon.setImageResource(R.drawable.ic_campaign);
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.iv_campaign_icon) ImageView statusIcon;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.content) TextView content;

        public ViewHolder(View itemView) {
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
