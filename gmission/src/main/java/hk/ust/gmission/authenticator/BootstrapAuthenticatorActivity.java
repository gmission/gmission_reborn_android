package hk.ust.gmission.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.wishlist.Toaster;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.R;
import hk.ust.gmission.R.id;
import hk.ust.gmission.R.layout;
import hk.ust.gmission.R.string;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.models.BaiduPushInfo;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.models.User;
import hk.ust.gmission.services.BaiduPushInfoService;
import hk.ust.gmission.services.BootstrapService;
import hk.ust.gmission.util.BaiduPushUtils;
import hk.ust.gmission.util.Ln;
import hk.ust.gmission.util.SafeAsyncTask;
import hk.ust.gmission.util.Strings;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.text.TextUtils.isEmpty;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.github.kevinsawicki.http.HttpRequest.post;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class BootstrapAuthenticatorActivity extends ActionBarAccountAuthenticatorActivity {
    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";


    private AccountManager accountManager;

    @Inject BootstrapService bootstrapService;
    @Inject ApiKeyProvider keyProvider;
    @Inject protected SharedPreferences prefs;

    @Bind(id.et_username) protected AutoCompleteTextView mEtUsername;
    @Bind(id.et_password) protected EditText mEtPassword;
    @Bind(id.b_signin) protected Button mSigninBtn;
    @Bind(id.b_register) protected Button mRegisterBtn;
    @Bind(R.id.tv_notice) TextView mTvNotice;

    private String accountId;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
//    private Boolean confirmCredentials = false;

    private String mUsername;

    private String mPassword;


    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String token;

    private Observable<CharSequence> nameChangeObservable;
    private Observable<CharSequence> passwdChangeObservable;

    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        accountManager = AccountManager.get(this);

        setContentView(layout.account_login_activity);
        ButterKnife.bind(this);


        mEtPassword.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && mSigninBtn.isEnabled()) {
                    mSigninBtn.callOnClick();
                    return true;
                }
                return false;
            }
        });

        mEtPassword.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && mSigninBtn.isEnabled()) {
                    mSigninBtn.callOnClick();
                    return true;
                }
                return false;
            }
        });



        nameChangeObservable = RxTextView.textChanges(mEtUsername).skip(1);
        passwdChangeObservable = RxTextView.textChanges(mEtPassword).skip(1);
        combineEditTextLatestEvents();

        subscribeRegisterButton();


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.message_signing_in));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
    }

    private void combineEditTextLatestEvents() {
        Observable.combineLatest(nameChangeObservable,
                passwdChangeObservable,
                new Func2<CharSequence,  CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence userName,
                                        CharSequence password) {

                        boolean nameValid = !isEmpty(userName) && userName.length() >= 6;
                        if (!nameValid) {
                            mTvNotice.setText(getString(R.string.register_check_name));
                        } else {
                            mUsername = userName.toString();
                        }

                        boolean passValid = !isEmpty(password) && password.length() >= 6;
                        if (!passValid) {
                            mTvNotice.setText(getString(R.string.register_check_passwd));
                        } else {
                            mPassword = password.toString();
                        }

                        return nameValid && passValid;
                    }
                })
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean formValid) {
                        if (formValid)mTvNotice.setText("");
                        mSigninBtn.setEnabled(formValid);
                    }
                })
                .subscribe();
    }

    private void subscribeRegisterButton(){
            RxView.clicks(mSigninBtn)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        showProgress();
                        return o;
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;

                        String message;
                        // A 404 is returned as an Exception with this message
                        if ("Received authentication challenge is null".equals(cause.getMessage())){
                            message = getResources().getString(R.string.message_login_error);
                        }
                        else {
                            message = cause.getMessage();
                        }

                        Toaster.showLong(BootstrapAuthenticatorActivity.this, message);
                        hideProgress();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<Object, Object>() {

                    @Override
                    public Object call(Object o) {

                        final String query = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", mUsername, mPassword);
                        Log.i("PostContent", query);
                        HttpRequest request = post(Constants.Http.URL_AUTH).contentType(Constants.Http.CONTENT_TYPE_JSON).send(query);


                        Ln.d("Authentication response=%s", request.code());

                        if (request.ok()) {
                            String buffer = Strings.toString(request.buffer());
                            final User model = new Gson().fromJson(buffer, User.class);

                            token = model.getToken();
                            Constants.Http.PARAM_SESSION_TOKEN = token;
                            accountId = String.valueOf(model.getId());
                        }

                        onAuthenticationResult(request.ok());
                        return o;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        hideProgress();
                    }
                })
                .subscribe();

    }


    public void updateBaiduPushInfo(){

        final String baidu_user_id = BaiduPushUtils.getBaiduPushUserID(BootstrapAuthenticatorActivity.this);
        final String baidu_channel_id = BaiduPushUtils.getBaiduPushChannelID(BootstrapAuthenticatorActivity.this);

        QueryObject queryObject = new QueryObject();
        queryObject.push("user_id", "eq", Constants.Http.PARAM_USER_ID);

        final BaiduPushInfoService baiduPushInfoService = bootstrapService.getBaiduPushInfoService();

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


    public void handleRegister(final View view) {
        startActivity(new Intent(this, BootstrapAccountRegisterActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            setAccountAuthenticatorResult(data.getExtras());
            setResult(RESULT_OK, data);
            finish();
        }
    }


    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        final Account account = new Account(mUsername, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, mPassword, null);

        accountManager.setAuthToken(account, Constants.Auth.AUTHTOKEN_TYPE, token);
        accountManager.setUserData(account, Constants.Auth.USER_ID, accountId);


        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);


        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(final boolean result) {
        if (result) {
            updateBaiduPushInfo();
            finishLogin();
        } else {
            Ln.d("onAuthenticationResult: failed to authenticate");
            Toaster.showLong(BootstrapAuthenticatorActivity.this,
                    string.message_auth_failed_new_account);

        }
    }
}
