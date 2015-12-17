package hk.ust.gmission.ui.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;

import hk.ust.gmission.BootstrapApplication;
import hk.ust.gmission.R;
import hk.ust.gmission.core.User;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter to display a list of traffic items
 */
public class UserListAdapter extends SingleTypeAdapter<User> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd");

    /**
     * @param inflater
     * @param items
     */
    public UserListAdapter(final LayoutInflater inflater, final List<User> items) {
        super(inflater, R.layout.user_list_item);

        setItems(items);
    }

    /**
     * @param inflater
     */
    public UserListAdapter(final LayoutInflater inflater) {
        this(inflater, null);

    }

    @Override
    public long getItemId(final int position) {
        final String id = String.valueOf(getItem(position).getId());
        return !TextUtils.isEmpty(id) ? id.hashCode() : super
                .getItemId(position);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{R.id.iv_avatar, R.id.tv_name};
    }

    @Override
    protected void update(final int position, final User user) {

        Picasso.with(BootstrapApplication.getInstance())
                .load("https://www.google.com.hk/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwjx84uw8d_JAhUC4aYKHY72DaoQjRwIBw&url=http%3A%2F%2Fwww.cnet.com%2Fandroid-update%2F&psig=AFQjCNEIU5E8BIsN_Yi8bDplkM6RRpBKcQ&ust=1450338309102634")
                .placeholder(R.drawable.gravatar_icon)
                .into(imageView(0));

        setText(1, String.format("%1$s", user.getName()));

    }

}
