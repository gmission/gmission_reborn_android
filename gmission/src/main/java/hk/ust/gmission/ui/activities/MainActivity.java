

package hk.ust.gmission.ui.activities;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.R;
import hk.ust.gmission.authenticator.ApiKeyProvider;
import hk.ust.gmission.authenticator.LogoutService;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.NavItemSelectedEvent;
import hk.ust.gmission.events.NetworkErrorEvent;
import hk.ust.gmission.events.RequestLocationEvent;
import hk.ust.gmission.events.RestAdapterErrorEvent;
import hk.ust.gmission.events.UnAuthorizedErrorEvent;
import hk.ust.gmission.services.LocationTraceService;
import hk.ust.gmission.ui.fragments.CampaignRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.CarouselFragment;
import hk.ust.gmission.ui.fragments.HitRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.MessageRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.NavigationDrawerFragment;
import hk.ust.gmission.ui.fragments.TaskMapFragment;
import hk.ust.gmission.ui.fragments.UserProfilePFragment;
import hk.ust.gmission.util.Ln;
import hk.ust.gmission.util.SafeAsyncTask;


/**
 * Initial activity for the application.
 *
 * If you need to remove the authentication from the application please see
 * {@link hk.ust.gmission.authenticator.ApiKeyProvider#getAuthKey(android.app.Activity)}
 */
public class MainActivity extends BootstrapFragmentActivity{

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;
    @Inject protected ApiKeyProvider keyProvider;

    private Fragment currentFragment = null;
    private int currentNavItemPosition = 0;

    private boolean userHasAuthenticated = false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence title;
    private NavigationDrawerFragment navigationDrawerFragment;

    private Fragment homeFragment = new UserProfilePFragment();
    private Fragment campaignFragment = new CampaignRecyclerViewFragment();
    private Fragment mapFragment = new TaskMapFragment();
    private Fragment messageFragment = new MessageRecyclerViewFragment();
    private Fragment initFragment = campaignFragment;


    private static Intent serviceIntent;

    @Subscribe
    public void onNavItemSelectedEvent(NavItemSelectedEvent event) {

        if (currentNavItemPosition == event.getItemPosition() && currentNavItemPosition != 4){
            return;
        } else {
            currentNavItemPosition = event.getItemPosition();
            switch (currentNavItemPosition) {
                case 0: //campaign
                    title = getString(R.string.title_campaign);
                    replaceCurrentFragment(campaignFragment);
                    break;
                case 1: //map
                    title = getString(R.string.title_map);
                    replaceCurrentFragment(mapFragment);
                    break;
                case 2: //messages
                    title = getString(R.string.title_message);
                    replaceCurrentFragment(messageFragment);
                    break;
                case 3: //home page
                    title = getString(R.string.title_home);
                    startActivity(new Intent(this.getActivity(), AskSpatialTaskActivity.class));
//                    replaceCurrentFragment(homeFragment);
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

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        bus.unregister(this);
    }


    protected Activity getActivity(){
        return this;
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        checkAuth();

        startAndBindService();

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


    private void initScreen() {
        if (userHasAuthenticated) {
            title = getString(R.string.title_campaign);
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

}
