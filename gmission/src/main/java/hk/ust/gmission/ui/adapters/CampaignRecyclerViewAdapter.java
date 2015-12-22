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
import hk.ust.gmission.events.NewsItemClickEvent;
import hk.ust.gmission.models.News;

public class CampaignRecyclerViewAdapter extends BaseRecyclerViewAdapter<CampaignRecyclerViewAdapter.CampaignViewHolder, News> {

    @Override
    public CampaignViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.campaign_item, parent, false);

        return new CampaignViewHolder(itemView);
    }

    @Inject
    public CampaignRecyclerViewAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(CampaignViewHolder holder, int position) {
        News news = items.get(position);
        holder.id.setText(String.format("%s %s",
                news.getTitle(), news.getObjectId()));
        holder.content.setText(news.getContent().substring(0, 10));
    }


    class CampaignViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        @Bind(R.id.id) TextView id;
        @Bind(R.id.content) TextView content;

        public CampaignViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            bus.post(new NewsItemClickEvent(view));
        }
    }
}
