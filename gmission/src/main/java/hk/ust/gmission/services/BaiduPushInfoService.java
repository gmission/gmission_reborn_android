package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.BaiduPushInfo;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by bigstone on 14/1/2016.
 */
public interface BaiduPushInfoService {
    @GET(Constants.Http.URL_BAIDU_PUSH_INFO_FRAG)
    Observable<ModelWrapper<BaiduPushInfo>> getBaiduPushInfoList(@Query("q") String query);
    @POST(Constants.Http.URL_BAIDU_PUSH_INFO_FRAG)
    Observable<BaiduPushInfo> postBaiduPushInfo(@Body BaiduPushInfo baiduPushInfo);
    @PUT(Constants.Http.URL_BAIDU_PUSH_INFO_FRAG + "/{id}")
    Observable<BaiduPushInfo> updateBaiduPushInfo(@Path("id") String pushInfoId, @Body BaiduPushInfo baiduPushInfo);
}
