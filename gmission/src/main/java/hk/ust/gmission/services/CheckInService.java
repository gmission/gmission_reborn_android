package hk.ust.gmission.services;

import hk.ust.gmission.models.wrapper.CheckInWrapper;
import hk.ust.gmission.core.Constants;
import retrofit.http.GET;

public interface CheckInService {

    @GET(Constants.Http.URL_CHECKINS_FRAG)
    CheckInWrapper getCheckIns();
}
