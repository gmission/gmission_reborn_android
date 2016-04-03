package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by bigstone on 26/3/2016.
 */
public interface ExtraService {

    @GET(Constants.Http.URL_3D_TASK_DIRECTION_FRAG + "/{id}")
    Observable<String> get3DReconstructionTaskDirection(@Path("id") String id);

    @GET(Constants.Http.URL_REQUEST_3D_EMAIL_FRAG + "/{request_id}/{hit_id}/{filename}")
    Observable<String> request3DEmail(@Path("request_id") String requestId, @Path("hit_id") String hitId, @Path("filename") String filename);


}
