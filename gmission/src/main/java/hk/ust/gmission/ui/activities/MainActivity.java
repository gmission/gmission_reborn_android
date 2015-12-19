

package hk.ust.gmission.ui.activities;

import android.accounts.OperationCanceledException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


import javax.inject.Inject;

import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.R;
import hk.ust.gmission.authenticator.LogoutService;
import hk.ust.gmission.services.BootstrapService;
import hk.ust.gmission.events.NavItemSelectedEvent;
import hk.ust.gmission.ui.fragments.CarouselFragment;
import hk.ust.gmission.ui.fragments.CheckInsListFragment;
import hk.ust.gmission.ui.fragments.NavigationDrawerFragment;
import hk.ust.gmission.ui.fragments.NewsListFragment;
import hk.ust.gmission.ui.fragments.UserListFragment;
import hk.ust.gmission.util.SafeAsyncTask;
import hk.ust.gmission.util.UIUtils;


/**
 * Initial activity for the application.
 *
 * If you need to remove the authentication from the application please see
 * {@link hk.ust.gmission.authenticator.ApiKeyProvider#getAuthKey(android.app.Activity)}
 */
public class MainActivity extends BootstrapFragmentActivity {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected Bus bus;
    @Inject protected LogoutService logoutService;


    private Fragment currentFragment = null;
    private int currentNavItemPosition = -1;

    private boolean userHasAuthenticated = false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private NavigationDrawerFragment navigationDrawerFragment;

    @Subscribe
    public void onNavItemSelectedEvent(NavItemSelectedEvent event) {

        if (currentNavItemPosition == event.getItemPosition()){
            return;
        } else {
            currentNavItemPosition = event.getItemPosition();
            switch (currentNavItemPosition) {
                case 0://home page
                    replaceCurrentFragment(new UserListFragment());
                    break;
                case 1: //campaign
                    replaceCurrentFragment(new NewsListFragment());
                    break;
                case 2: //campaign
                    replaceCurrentFragment(new CarouselFragment());
                    break;
                case 3: //campaign
                    replaceCurrentFragment(new CheckInsListFragment());
                    break;
                case 4://log out
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
        }

        Log.d("event",String.valueOf(event.getItemPosition()));


    }


    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        eventBus.unregister(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        if(isTablet()) {
            setContentView(R.layout.main_activity_tablet);
        } else {
            setContentView(R.layout.main_activity);
        }

        // View injection with Butterknife
//        Views.inject(this);

        // Set up navigation drawer
        title = drawerTitle = getTitle();

        if(!isTablet()) {
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerToggle = new ActionBarDrawerToggle(
                    this,                    /* Host activity */
                    drawerLayout,           /* DrawerLayout object */
                    R.drawable.ic_drawer,    /* nav drawer icon to replace 'Up' caret */
                    R.string.navigation_drawer_open,    /* "open drawer" description */
                    R.string.navigation_drawer_close) { /* "close drawer" description */

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    getSupportActionBar().setTitle(title);
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    getSupportActionBar().setTitle(drawerTitle);
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(drawerToggle);

            navigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

            // Set up the drawer.
            navigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        checkAuth();

    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(!isTablet()) {
            // Sync the toggle state after onRestoreInstanceState has occurred.
            drawerToggle.syncState();
        }
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(!isTablet()) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }


    private void initScreen() {
        if (userHasAuthenticated) {
            replaceCurrentFragment(new UserListFragment());
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
                final BootstrapService svc = serviceProvider.getService(MainActivity.this);
                return svc != null;
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

        if (!isTablet() && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
//                menuDrawer.toggleMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
