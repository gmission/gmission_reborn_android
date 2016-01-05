package hk.ust.gmission;

import android.accounts.AccountManager;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hk.ust.gmission.authenticator.ApiKeyProvider;
import hk.ust.gmission.authenticator.BootstrapAccountRegisterActivity;
import hk.ust.gmission.authenticator.BootstrapAuthenticatorActivity;
import hk.ust.gmission.authenticator.LogoutService;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.PostFromAnyThreadBus;
import hk.ust.gmission.core.RestAdapterRequestInterceptor;
import hk.ust.gmission.core.RestErrorHandler;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.models.UserAgentProvider;
import hk.ust.gmission.services.BootstrapService;
import hk.ust.gmission.services.LocationTraceService;
import hk.ust.gmission.ui.activities.*;
import hk.ust.gmission.ui.adapters.*;
import hk.ust.gmission.ui.fragments.*;
import hk.ust.gmission.util.GsonUtil;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                BootstrapApplication.class,
                BootstrapAuthenticatorActivity.class,
                BootstrapAccountRegisterActivity.class,
                MainActivity.class,
                HitActivity.class,
                WorkingAreaSetupActivity.class,
                AskSpatialTaskActivity.class,
                NavigationDrawerFragment.class,
                CampaignRecyclerViewFragment.class,
                MessageRecyclerViewFragment.class,
                HitRecyclerViewFragment.class,
                HitListActivity.class,
                SelectionHitFragment.class,
                TextHitFragment.class,
                ImageHitFragment.class,
                UserProfilePFragment.class,
                TaskMapFragment.class,
                HitRecyclerViewAdapter.class,
                MessageRecyclerViewAdapter.class,
                CampaignRecyclerViewAdapter.class,
                QueryObject.class,
                GsonUtil.class,
                LocationTraceService.class
        }
)
public class BootstrapModule {
    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    };
    private static class DateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOF,
                                JsonDeserializationContext context) throws JsonParseException {
            for (String format : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
        }
    }


    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

    @Provides
    BootstrapService provideBootstrapService(RestAdapter restAdapter) {
        return new BootstrapService(restAdapter);
    }

    @Provides
    BootstrapServiceProvider provideBootstrapServiceProvider(RestAdapter restAdapter) {
        return new BootstrapServiceProvider(restAdapter);
    }

    @Provides
    ApiKeyProvider provideApiKeyProvider(AccountManager accountManager) {
        return new ApiKeyProvider(accountManager);
    }

    @Provides
    Gson provideGson() {
        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder()
//                    .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
    }

    @Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }

    @Provides
    RestAdapterRequestInterceptor provideRestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        return new RestAdapterRequestInterceptor(userAgentProvider);
    }

    @Provides
    OkClient provideOkClient(){
        final OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
//        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        return new OkClient(okHttpClient);
    }

    @Provides
    RestAdapter provideRestAdapter(RestErrorHandler restErrorHandler, RestAdapterRequestInterceptor restRequestInterceptor, Gson gson, OkClient okClient) {

        return new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .setErrorHandler(restErrorHandler)
                .setRequestInterceptor(restRequestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .setClient(okClient)
                .build();
    }

}
