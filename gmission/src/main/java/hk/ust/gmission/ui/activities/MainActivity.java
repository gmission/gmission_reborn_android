

package hk.ust.gmission.ui.activities;

import android.accounts.AccountsException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.squareup.otto.Subscribe;

import java.io.IOException;

import javax.inject.Inject;

import hk.ust.gmission.R;
import hk.ust.gmission.authenticator.ApiKeyProvider;
import hk.ust.gmission.authenticator.LogoutService;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.AuthorizationInitializedEvent;
import hk.ust.gmission.events.LocationUpdateEvent;
import hk.ust.gmission.events.NavItemSelectedEvent;
import hk.ust.gmission.events.NetworkErrorEvent;
import hk.ust.gmission.events.RequestLocationEvent;
import hk.ust.gmission.events.RestAdapterErrorEvent;
import hk.ust.gmission.events.TaskCreateSuccessEvent;
import hk.ust.gmission.events.UnAuthorizedErrorEvent;
import hk.ust.gmission.models.BaiduPushInfo;
import hk.ust.gmission.models.PositionTrace;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.services.BaiduPushInfoService;
import hk.ust.gmission.services.LocationTraceService;
import hk.ust.gmission.ui.fragments.CampaignRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.MessageRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.NavigationDrawerFragment;
import hk.ust.gmission.ui.fragments.TaskMapFragment;
import hk.ust.gmission.ui.fragments.UserProfilePFragment;
import hk.ust.gmission.util.BaiduPushUtils;
import hk.ust.gmission.util.Ln;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;


/**
 * Initial activity for the application.
 *
 * If you need to remove the authentication from the application please see
 * {@link hk.ust.gmission.authenticator.ApiKeyProvider#getAuthKey(android.app.Activity)}
 */
public class MainActivity extends BootstrapFragmentActivity{

    @Inject protected LogoutService logoutService;
    @Inject protected ApiKeyProvider keyProvider;

    private Fragment currentFragment = null;
    private int currentNavItemPosition = 0;

    private boolean userHasAuthenticated = false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence title;
    private NavigationDrawerFragment navigationDrawerFragment;

    private Fragment homeFragment;
    private Fragment campaignFragment;
    private Fragment mapFragment;
    private Fragment messageFragment;
    private Fragment initFragment;


    private static Intent serviceIntent;

    boolean doubleBackToExitPressedOnce = false;

    boolean isAuthenticating = false;

    public static MainActivity mActivity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.i("MainActivity", "MainActivity Creating!");

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        mActivity = this;
        checkAuth();

        setContentView(R.layout.main_activity);

        // Set up navigation drawer
        title = getString(R.string.title_home);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,                    /* Host activity */
                drawerLayout,           /* DrawerLayout object */
                R.drawable.ic_drawer_full,    /* nav drawer icon to replace 'Up' caret */
                R.string.navigation_drawer_open,    /* "open drawer" description */
                R.string.navigation_drawer_close);

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_full);
        getSupportActionBar().setHomeButtonEnabled(true);

        startAndBindService();

//        new AppUpdateCheckTask(this.getActivity()).execute();

        //initial baidu push service
//        PushManager.startWork(getApplicationContext(),
//                PushConstants.LOGIN_TYPE_API_KEY,
//                BaiduPushUtils.getMetaValue(MainActivity.this, "api_key"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        bus.register(this);
        bus.post(new RequestLocationEvent(true));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    protected Activity getActivity(){
        return this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
        unbindService(gpsServiceConnection);

    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }



    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);

    }


    /**
     * support double press return to quit app
     */

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.message_double_return_to_quit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    public void updateBaiduPushInfo(){

        final String baidu_user_id = BaiduPushUtils.getBaiduPushUserID(MainActivity.this);
        final String baidu_channel_id = BaiduPushUtils.getBaiduPushChannelID(MainActivity.this);

        QueryObject queryObject = new QueryObject();
        queryObject.push("user_id", "eq", Constants.Http.PARAM_USER_ID);

        final BaiduPushInfoService baiduPushInfoService = serviceProvider.getService().getBaiduPushInfoService();

        baiduPushInfoService.getBaiduPushInfoList(queryObject.toString())
                .flatMap(new Func1<ModelWrapper<BaiduPushInfo>, Observable<BaiduPushInfo>>() {
                    @Override
                    public Observable<BaiduPushInfo> call(ModelWrapper<BaiduPushInfo> wrapper) {
                        if (wrapper.getNum_results() > 0){
                            return Observable.just(wrapper.getObjects().get(0));
                        }
                        return Observable.just(new BaiduPushInfo());
                    }
                })
                .flatMap(new Func1<BaiduPushInfo, Observable<BaiduPushInfo>>() {

                    @Override
                    public Observable<BaiduPushInfo> call(BaiduPushInfo baiduPushInfo) {
                        baiduPushInfo.setIs_valid(true);
                        baiduPushInfo.setType("android");
                        baiduPushInfo.setBaidu_user_id(baidu_user_id);
                        baiduPushInfo.setBaidu_channel_id(baidu_channel_id);

                        if (baiduPushInfo.getId() == null) {
                            baiduPushInfo.setUser_id(Constants.Http.PARAM_USER_ID);
                            return baiduPushInfoService.postBaiduPushInfo(baiduPushInfo);
                        } else {
                            return baiduPushInfoService.updateBaiduPushInfo(baiduPushInfo.getId(), baiduPushInfo);
                        }
                    }
                })
                .doOnNext(new Action1<BaiduPushInfo>() {

                    @Override
                    public void call(BaiduPushInfo baiduPushInfo) {
                        Ln.d("baidu Push info updated:" + baiduPushInfo.getId());
                    }
                })
                .subscribe();
    }

    @Subscribe
    public void onNavItemSelectedEvent(NavItemSelectedEvent event) {

        if (currentNavItemPosition == event.getItemPosition() && currentNavItemPosition != 4){
            return;
        } else {
            currentNavItemPosition = event.getItemPosition();
            switch (currentNavItemPosition) {
                case 0: //home
                    title = getString(R.string.title_home);
                    replaceCurrentFragment(homeFragment);

                    break;
                case 1: //campaign
                    title = getString(R.string.title_campaign);
                    replaceCurrentFragment(campaignFragment);
                    Intent intent = new Intent(this, SpatialDirectHitActivity.class).putExtra(HIT_ID, "1");
//                    startActivity(intent);
                    break;
                case 2: //map
                    title = getString(R.string.title_map);
                    replaceCurrentFragment(mapFragment);
                    break;
                case 3: //messages
                    title = getString(R.string.title_message);
                    replaceCurrentFragment(messageFragment);
                    break;
                case 4: //log out
                    Log.d("logout","log out");
                    logoutService.logout(new Runnable() {
                        @Override
                        public void run() {
                            checkAuth();
                        }
                    });
                    break;
                default:
                    return;
            }

            getSupportActionBar().setTitle(title);
        }

    }




    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent unAuthorizedErrorEvent) {
        if (isAuthenticating) return;
        Log.e("authorizedError", "failed");
        Toast.makeText(this.getApplicationContext(), getString(R.string.unauthorized_error_message), Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, BootstrapAuthenticatorActivity.class);
//        startActivity(intent);
    }

    @Subscribe
    public void onNetworkErrorEvent(NetworkErrorEvent networkErrorEvent) {
        Toast.makeText(this.getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onRetrofitErrorEvent(RestAdapterErrorEvent restAdapterErrorEvent) {
        Toast.makeText(this.getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
    }


    @Subscribe
    public void onTaskCreatedEvent(TaskCreateSuccessEvent event){
        //TODO: add updates for related lists
    }


    @Subscribe
    public void onLocationUpdate(LocationUpdateEvent event){
        Log.i("locationTrace","create LocationTrace");
        PositionTrace positionTrace = new PositionTrace();
        positionTrace.setLatitude(event.getLocation().getLatitude());
        positionTrace.setLongitude(event.getLocation().getLongitude());
        positionTrace.setZ(event.getLocation().getAltitude());
        positionTrace.setUser_id(Constants.Http.PARAM_USER_ID);

        serviceProvider.getService().getGeoService()
                .createPositionTrace(positionTrace)
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<PositionTrace>() {
                    @Override
                    public void call(PositionTrace positionTrace) {
                        Log.i("locationTrace ID", positionTrace.getId());
                    }
                }).subscribe();

    }

    /**
     * Provides a connection to the GPS Logging Service
     */
    private final ServiceConnection gpsServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Ln.d("Disconnected from GPSLoggingService from MainActivity");

        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Ln.d("Connected to GPSLoggingService from MainActivity");
        }
    };


    /**
     * Starts the service and binds the activity to it.
     */
    private void startAndBindService() {
        serviceIntent = new Intent(this, LocationTraceService.class);
        // Start the service in case it isn't already running
        startService(serviceIntent);
        // Now bind to service
        bindService(serviceIntent, gpsServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private void initScreen() {
        if (userHasAuthenticated) {

            //initial fragments
            homeFragment = new UserProfilePFragment();
            campaignFragment = new CampaignRecyclerViewFragment();
            mapFragment = new TaskMapFragment();
            messageFragment = new MessageRecyclerViewFragment();
            initFragment = homeFragment;


            title = getString(R.string.title_home);
            getSupportActionBar().setTitle(title);
            replaceCurrentFragment(initFragment);
            updateBaiduPushInfo();
            bus.post(new RequestLocationEvent(true));
        }

    }

    private void replaceCurrentFragment(Fragment newFragment){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (currentFragment == null){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, newFragment)
                    .commitAllowingStateLoss();
            //refresh fragments
            fragmentManager
                    .beginTransaction()
                    .detach(newFragment)
                    .attach(newFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(currentFragment.getId(), newFragment)
                    .commitAllowingStateLoss();
        }

        currentFragment = newFragment;

    }

    private void checkAuth() {

        if (isAuthenticating){
            return;
        } else {
            isAuthenticating = true;
        }

        Observable.just(true)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        String sessionToken = null;
                        try {
                            sessionToken = keyProvider.getAuthKey(MainActivity.this);
                            Constants.Http.PARAM_SESSION_TOKEN = sessionToken;
                            Constants.Http.PARAM_USERNAME = keyProvider.getUserName();
                            Constants.Http.PARAM_USER_ID = keyProvider.getUserId();

                        } catch (AccountsException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return Observable.just(sessionToken != null);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean hasSessionToken) {

                        if  (hasSessionToken) {
                            userHasAuthenticated = true;
                            initScreen();
                        }
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        finish();
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        isAuthenticating = false;
                        bus.post(new AuthorizationInitializedEvent());
                    }
                })
                .subscribe();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }




}
