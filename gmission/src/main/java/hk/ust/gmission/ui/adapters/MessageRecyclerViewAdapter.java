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
import hk.ust.gmission.events.MessageItemClickEvent;
import hk.ust.gmission.models.Message;

public class MessageRecyclerViewAdapter extends BaseRecyclerViewAdapter<MessageRecyclerViewAdapter.MessageViewHolder, Message> {

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        return new MessageViewHolder(itemView);
    }

    public MessageRecyclerViewAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = items.get(position);

        holder.content.setText(message.getContent());
        if (message.getStatus().equals("new")){
            holder.icon.setImageResource(R.drawable.ic_message);
        } else if (message.getStatus().equals("read")){
            holder.icon.setImageResource(R.drawable.ic_message_read);
        }

    }


    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        @Bind(R.id.content) TextView content;
        @Bind(R.id.iv_message_icon) ImageView icon;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            bus.post(new MessageItemClickEvent(view));
        }
    }
}
