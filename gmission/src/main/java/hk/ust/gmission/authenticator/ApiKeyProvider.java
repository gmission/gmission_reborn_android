

package hk.ust.gmission.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountsException;
import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.services.BootstrapService;

import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static hk.ust.gmission.core.Constants.Auth.AUTHTOKEN_TYPE;
import static hk.ust.gmission.core.Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE;
import static hk.ust.gmission.core.Constants.Auth.USER_ID;

/**
 * Bridge class that obtains a API key for the currently configured account
 */
public class ApiKeyProvider {

    private AccountManager accountManager;

    public ApiKeyProvider(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * This call blocks, so shouldn't be called on the UI thread.
     * This call is what makes the login screen pop up. If the user has
     * not logged in there will no accounts in the {@link android.accounts.AccountManager}
     * and therefore the Activity that is referenced in the
     * {@link hk.ust.gmission.authenticator.BootstrapAccountAuthenticator} will get started.
     * If you want to remove the authentication then you can comment out the code below and return a string such as
     * "foo" and the authentication process will not be kicked off. Alternatively, you can remove this class
     * completely and clean up any references to the authenticator.
     *
     *
     * @return API key to be used for authorization with a
     * {@link BootstrapService} instance
     * @throws AccountsException
     * @throws IOException
     */
    public String getAuthKey(final Activity activity) throws AccountsException, IOException {
        final AccountManagerFuture<Bundle> accountManagerFuture
                = accountManager.getAuthTokenByFeatures(BOOTSTRAP_ACCOUNT_TYPE,
                AUTHTOKEN_TYPE, new String[0], activity, null, null, null, null);

        return accountManagerFuture.getResult().getString(KEY_AUTHTOKEN);
    }

    public String getUserName() throws AccountsException, IOException {
        Account[] accounts = accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE);
        if (accounts.length > 0){
            return accounts[0].name;
        }

        return null;
    }

    public String getUserId() throws AccountsException, IOException {
        Account[] accounts = accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE);
        if (accounts.length > 0){
            return accountManager.getUserData(accounts[0], USER_ID);
        }

        return null;
    }
}
