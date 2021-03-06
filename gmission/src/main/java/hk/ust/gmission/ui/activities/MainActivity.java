

package hk.ust.gmission.ui.activities;

import android.accounts.OperationCanceledException;
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

import javax.inject.Inject;

import hk.ust.gmission.R;
import hk.ust.gmission.authenticator.ApiKeyProvider;
import hk.ust.gmission.authenticator.LogoutService;
import hk.ust.gmission.core.AppUpdateCheckTask;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.events.NavItemSelectedEvent;
import hk.ust.gmission.events.NetworkErrorEvent;
import hk.ust.gmission.events.RestAdapterErrorEvent;
import hk.ust.gmission.events.TaskCreateSuccessEvent;
import hk.ust.gmission.events.UnAuthorizedErrorEvent;
import hk.ust.gmission.services.LocationTraceService;
import hk.ust.gmission.ui.fragments.CampaignRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.MessageRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.NavigationDrawerFragment;
import hk.ust.gmission.ui.fragments.TaskMapFragment;
import hk.ust.gmission.ui.fragments.UserProfilePFragment;
import hk.ust.gmission.util.BaiduPushUtils;
import hk.ust.gmission.util.Ln;
import hk.ust.gmission.util.SafeAsyncTask;


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



    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        checkAuth();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);



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
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                BaiduPushUtils.getMetaValue(MainActivity.this, "api_key"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
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
        Toast.makeText(this.getApplicationContext(), getString(R.string.unauthorized_error_message), Toast.LENGTH_SHORT);
        logoutService.logout(new Runnable() {
            @Override
            public void run() {
                checkAuth();
            }
        });
    }

    @Subscribe
    public void onNetworkErrorEvent(NetworkErrorEvent networkErrorEvent) {
        Toast.makeText(this.getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_SHORT);
    }

    @Subscribe
    public void onRetrofitErrorEvent(RestAdapterErrorEvent restAdapterErrorEvent) {
        Toast.makeText(this.getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_SHORT);
    }


    @Subscribe
    public void onTaskCreatedEvent(TaskCreateSuccessEvent event){
        //TODO: add updates for related lists
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
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {

                // The call to keyProvider.getAuthKey(...) is what initiates the login screen. Call that now.
                String sessionToken = keyProvider.getAuthKey(MainActivity.this);

                Constants.Http.PARAM_SESSION_TOKEN = sessionToken;
                Constants.Http.PARAM_USERNAME = keyProvider.getUserName();
                Constants.Http.PARAM_USER_ID = keyProvider.getUserId();

                return sessionToken == null;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                userHasAuthenticated = true;
                initScreen();
            }
        }.execute();
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
