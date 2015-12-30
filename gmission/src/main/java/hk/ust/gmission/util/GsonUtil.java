package hk.ust.gmission.util;

import com.google.gson.Gson;

import javax.inject.Inject;

import hk.ust.gmission.Injector;
import hk.ust.gmission.core.api.QueryObject;

/**
 * Created by bigstone on 28/12/2015.
 */
public class GsonUtil {
    @Inject protected Gson gson;
    private static GsonUtil instance = null;

    private GsonUtil() {
        Injector.inject(this);
    }

    public static GsonUtil getInstance(){
        if (instance == null){
            instance = new GsonUtil();
        }
        return instance;
    }

    public static String getQueryString(QueryObject queryObject){
        return getInstance().gson.toJson(queryObject);
    }
}
