package hk.ust.gmission.ui.fragments;

import android.accounts.AccountsException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import hk.ust.gmission.models.dao.User;
import hk.ust.gmission.models.wrapper.UsersWrapper;
import hk.ust.gmission.ui.activities.UserActivity;
import hk.ust.gmission.ui.adapters.UserListAdapter;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import static hk.ust.gmission.core.Constants.Extra.USER;

public class UserListFragment extends ItemListFragment<User> {

    @Inject protected BootstrapServiceProvider serviceProvider;

    private boolean isLoaderInitialized = false;

    protected UserListFragment userListFragment = this;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_list, null);
    }

    private void loadData() throws IOException, AccountsException {
        serviceProvider.getService(getActivity()).getUserService().getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<UsersWrapper>() {
                    @Override
                    public void call(UsersWrapper newsWrapper) {

                        if (!isLoaderInitialized){
                            items = newsWrapper.getResults();
                        } else {
                            User user = new User();
                            user.setName("Test");

                            items.add(user);
                        }

                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        e.printStackTrace();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if (!isLoaderInitialized){
                            getLoaderManager().initLoader(0, null, userListFragment);
                            isLoaderInitialized = true;
                        } else {
                            userListFragment.getListAdapter().getWrappedAdapter().setItems(items);
                            userListFragment.getListAdapter().getWrappedAdapter().notifyDataSetChanged();
                        }

                    }
                })
                .subscribe();
    }


    private void configPullToRefresh(final View view){

        final StoreHouseHeader header = new StoreHouseHeader(this.getActivity().getApplicationContext());
        header.setPadding(0, 15, 0, 0);
        header.initWithString(getString(R.string.loading));
        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.ptr_frame);
        frame.addPtrUIHandler(new PtrUIHandler() {

            @Override
            public void onUIReset(PtrFrameLayout frame) {
                header.initWithString(getString(R.string.refreshing));
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {
                try {
                    loadData();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AccountsException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {

            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });

        frame.setDurationToCloseHeader(1000);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(false);
            }
        }, 200);

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (canScrollUp(listView)){
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 200);
            }
        });
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_users);
        configPullToRefresh(getView());

    }

    @Override
    protected void configureList(final Activity activity, final ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(10);

    }


    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final User user = ((User) l.getItemAtPosition(position));

        startActivity(new Intent(getActivity(), UserActivity.class).putExtra(USER, user));
    }

    @Override
    public void onLoadFinished(final Loader<List<User>> loader, final List<User> items) {
        super.onLoadFinished(loader, items);

    }

    @Override
    protected int getErrorMessage(final Exception exception) {
        return R.string.error_loading_users;
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(final List<User> items) {
        return new UserListAdapter(getActivity().getLayoutInflater(), items);
    }
}
