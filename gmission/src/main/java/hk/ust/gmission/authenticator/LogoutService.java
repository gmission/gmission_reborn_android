package hk.ust.gmission.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.util.Log;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.models.BaiduPushInfo;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.services.BaiduPushInfoService;
import hk.ust.gmission.services.BootstrapService;
import hk.ust.gmission.util.Ln;
import hk.ust.gmission.util.SafeAsyncTask;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import javax.inject.Inject;


/**
 * Class used for logging a user out.
 */
public class LogoutService {

    protected final Context context;
    protected final AccountManager accountManager;
    protected final BootstrapService bootstrapService;


    @Inject
    public LogoutService(final Context context, final AccountManager accountManager, final BootstrapService bootstrapService) {
        this.context = context;
        this.accountManager = accountManager;
        this.bootstrapService = bootstrapService;
    }

    public void logout(final Runnable onSuccess) {
        new LogoutTask(context, onSuccess).execute();
    }

    public void invalidBaiduPushInfo(){
        QueryObject queryObject = new QueryObject();
        queryObject.push("user_id", "eq", Constants.Http.PARAM_USER_ID);
        final BaiduPushInfoService baiduPushInfoService = bootstrapService.getBaiduPushInfoService();

        baiduPushInfoService.getBaiduPushInfoList(queryObject.toString())
                .flatMap(new Func1<ModelWrapper<BaiduPushInfo>, Observable<BaiduPushInfo>>() {
                    @Override
                    public Observable<BaiduPushInfo> call(ModelWrapper<BaiduPushInfo> wrapper) {
                        if (wrapper.getNum_results() > 0){
                            return Observable.from(wrapper.getObjects());
                        }
                        return Observable.just(new BaiduPushInfo());
                    }
                })
                .flatMap(new Func1<BaiduPushInfo, Observable<BaiduPushInfo>>() {

                    @Override
                    public Observable<BaiduPushInfo> call(BaiduPushInfo baiduPushInfo) {
                        baiduPushInfo.setIs_valid(false);

                        if (baiduPushInfo.getId() != null) {
                            return baiduPushInfoService.updateBaiduPushInfo(baiduPushInfo.getId(), baiduPushInfo);
                        } else {
                            return null;
                        }
                    }
                })
                .doOnNext(new Action1<BaiduPushInfo>() {

                    @Override
                    public void call(BaiduPushInfo baiduPushInfo) {
                        Ln.d("baidu Push info is disabled:" + baiduPushInfo.getId());
                    }
                })
                .subscribe();
    }

    private class LogoutTask extends SafeAsyncTask<Boolean> {

        private final Context taskContext;
        private final Runnable onSuccess;

        protected LogoutTask(final Context context, final Runnable onSuccess) {
            this.taskContext = context;
            this.onSuccess = onSuccess;
        }

        @Override
        public Boolean call() throws Exception {
            Log.d("Logout", "Enter");
            final AccountManager accountManagerWithContext = AccountManager.get(taskContext);
            if (accountManagerWithContext != null) {
                final Account[] accounts = accountManagerWithContext
                        .getAccountsByType(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
                if (accounts.length > 0) {
                    invalidBaiduPushInfo();

                    final AccountManagerFuture<Boolean> removeAccountFuture
                            = accountManagerWithContext.removeAccount(accounts[0], null, null);

                    return removeAccountFuture.getResult();
                }
            } else {
                Ln.w("accountManagerWithContext is null");
            }

            return false;
        }

        @Override
        protected void onSuccess(final Boolean accountWasRemoved) throws Exception {
            super.onSuccess(accountWasRemoved);

            Log.d("Logout", "Success");
            Ln.d("Logout succeeded: %s", accountWasRemoved);
            onSuccess.run();

        }

        @Override
        protected void onException(final Exception e) throws RuntimeException {
            super.onException(e);
            Log.d("Logout", "Exception");
            Ln.e(e.getCause(), "Logout failed.");
        }
    }
}
