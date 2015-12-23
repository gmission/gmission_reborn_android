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
import hk.ust.gmission.models.dao.News;

public class NewsListAdapter extends BaseRecyclerViewAdapter<NewsListAdapter.NewsViewHolder, News> {

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Inject
    public NewsListAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News news = items.get(position);
        holder.nameTextView.setText(String.format("%s %s",
                news.getTitle(), news.getObjectId()));
        holder.emailTextView.setText(news.getContent().substring(0, 10));
    }


    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.view_hex_color) View hexColorView;
        @Bind(R.id.text_name) TextView nameTextView;
        @Bind(R.id.text_email) TextView emailTextView;

        public NewsViewHolder(View itemView) {
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
