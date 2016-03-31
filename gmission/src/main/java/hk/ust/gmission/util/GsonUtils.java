package hk.ust.gmission.util;

import com.google.gson.Gson;

import javax.inject.Inject;

import hk.ust.gmission.Injector;
import hk.ust.gmission.core.api.QueryObject;

/**
 * Created by bigstone on 28/12/2015.
 */
public class GsonUtils {
    @Inject protected Gson gson;
    private static GsonUtils instance = null;

    private GsonUtils() {
        Injector.inject(this);
    }

    public static GsonUtils getInstance(){
        if (instance == null){
            instance = new GsonUtils();
        }
        return instance;
    }

    public static String getQueryString(QueryObject queryObject){
        return getInstance().gson.toJson(queryObject);
    }

    public static Gson getGson(){
        return getInstance().gson;
    }
}
