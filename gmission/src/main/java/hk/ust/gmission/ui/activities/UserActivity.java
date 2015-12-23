package hk.ust.gmission.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import hk.ust.gmission.R;
import hk.ust.gmission.models.dao.User;

import static hk.ust.gmission.core.Constants.Extra.USER;

public class UserActivity extends BootstrapActivity {

    @Bind(R.id.iv_avatar) protected ImageView avatar;
    @Bind(R.id.tv_name) protected TextView name;

    private User user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_view);

        if (getIntent() != null && getIntent().getExtras() != null) {
            user = (User) getIntent().getExtras().getSerializable(USER);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        Picasso.with(this).load(user.getAvatarUrl())
//                .placeholder(R.drawable.gravatar_icon)
//                .into(avatar);
//
        name.setText(String.format("%s", user.getName()));

    }


}
