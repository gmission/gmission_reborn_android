package hk.ust.gmission.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.wishlist.Toaster;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.dao.User;
import hk.ust.gmission.util.Ln;
import hk.ust.gmission.util.Strings;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func4;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;
import static com.github.kevinsawicki.http.HttpRequest.post;

public class BootstrapAccountRegisterActivity extends ActionBarAccountAuthenticatorActivity {
    
    @Bind(R.id.et_name) EditText mEtUsername;
    @Bind(R.id.et_email) AutoCompleteTextView mEtEmail;
    @Bind(R.id.et_password) EditText mEtPassword1;
    @Bind(R.id.et_password2) EditText mEtPassword2;
    @Bind(R.id.b_signin) Button mBSigninBtn;
    @Bind(R.id.tv_notice) TextView mTvNotice;

    private CompositeSubscription subcriptions = new CompositeSubscription();


    private Observable<CharSequence> nameChangeObservable;
    private Observable<CharSequence> emailChangeObservable;
    private Observable<CharSequence> passwd1ChangeObservable;
    private Observable<CharSequence> passwd2ChangeObservable;


    private AccountManager accountManager;

    String username;
    String passwd;
    String email;
    private String token = null;

    private String accountId = null;

    private String authToken;
    private String authTokenType;

    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_register_activity);
        ButterKnife.bind(this);

        nameChangeObservable = RxTextView.textChanges(mEtUsername).skip(1);
        emailChangeObservable = RxTextView.textChanges(mEtEmail).skip(1);
        passwd1ChangeObservable = RxTextView.textChanges(mEtPassword1).skip(1);
        passwd2ChangeObservable = RxTextView.textChanges(mEtPassword2).skip(1);

        accountManager = AccountManager.get(this);

        combineLatestEvents();
        subscribeRegisterButton();

    }



    @Override
    public void onPause() {
        super.onPause();
        subcriptions.unsubscribe();
    }


    private void subscribeRegisterButton(){

        Subscription buttonSubscription = RxView.clicks(mBSigninBtn)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        showProgress();
                        return o;
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        hideProgress();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;

                        String message;
                        // A 404 is returned as an Exception with this message
                        if ("Received authentication challenge is null".equals(cause.getMessage())){
                            message = getResources().getString(R.string.message_reg_error);
                        }
                        else {
                            message = cause.getMessage();
                        }

                        Toaster.showLong(BootstrapAccountRegisterActivity.this, message);
                        hideProgress();
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        final String query = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"email\":\"%s\"}", username, passwd, email);

                        Log.i("PostContent", query);
                        HttpRequest request = post(Constants.Http.URL_REG).contentType(Constants.Http.CONTENT_TYPE_JSON).send(query);

                        Log.i("REGISTER", "Authentication response=" + request.code());

                        if (request.ok()) {
                            final User model = new Gson().fromJson(Strings.toString(request.buffer()), User.class);

                            if( model.getRes() == -1 ) return;

                            model.setEmail(email);
                            token = model.getToken();
                            accountId = String.valueOf(model.getId());
                        }

                        onAuthenticationResult(request.ok());
                    }
                })

                .subscribe();
        subcriptions.add(buttonSubscription);
    }

    private void combineLatestEvents() {
        Subscription formSubscription = Observable.combineLatest(nameChangeObservable,
                emailChangeObservable,
                passwd1ChangeObservable,
                passwd2ChangeObservable,
                new Func4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence newName,
                                        CharSequence newEmail,
                                        CharSequence newPasswd1,
                                        CharSequence newPasswd2) {


                        boolean nameValid = !isEmpty(newName) && newName.length() >= 6;
                        if (!nameValid) {
                            mTvNotice.setText(getString(R.string.register_check_name));
                        } else {
                            username = newName.toString();
                        }


                        boolean emailValid = !isEmpty(newEmail) &&
                                EMAIL_ADDRESS.matcher(newEmail).matches();
                        if (!emailValid) {
                            mTvNotice.setText(getString(R.string.register_check_email));
                        } else {
                            email = newEmail.toString();
                        }

                        boolean passValid1 = !isEmpty(newPasswd1) && newPasswd1.length() >= 6;
                        if (!passValid1) {
                            mTvNotice.setText(getString(R.string.register_check_passwd));
                        }

                        boolean passValid2 = TextUtils.equals(newPasswd1, newPasswd2);
                        if (!passValid2) {
                            mTvNotice.setText(getString(R.string.register_check_password_same));
                        } else {
                            passwd = newPasswd2.toString();
                        }



                        return nameValid && emailValid && passValid1 && passValid2;

                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Ln.d("completed");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Ln.e(e, "there was an error");
                    }

                    @Override
                    public void onNext(Boolean formValid) {
                        if (formValid)mTvNotice.setText("");
                        mBSigninBtn.setEnabled(formValid);
                    }
                });

        subcriptions.add(formSubscription);
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


    protected void finishLogin() {
        final Account account = new Account(username, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        authToken = token;
        Constants.Http.PARAM_SESSION_TOKEN = authToken;
        accountManager.addAccountExplicitly(account, passwd, null);
        accountManager.setAuthToken(account, Constants.Auth.AUTHTOKEN_TYPE, authToken);
        accountManager.setUserData(account, Constants.Auth.USER_ID, accountId);

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, username);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (authTokenType != null
                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        }



        intent.putExtra( KEY_BOOLEAN_RESULT, accountId);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        finish();
    }

    public void onAuthenticationResult(boolean result) {
        if (result)
            finishLogin();
        else {
            Toaster.showLong(BootstrapAccountRegisterActivity.this,
                    getResources().getString(
                            R.string.message_reg_error));
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.message_signing_up));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {

            }
        });
        return dialog;
    }
}
