package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.dao.CheckIn;
import hk.ust.gmission.models.wrapper.ModelWrapper;
import retrofit.http.GET;

public interface CheckInService {

    @GET(Constants.Http.URL_CHECKINS_FRAG)
    ModelWrapper<CheckIn> getCheckIns();
}
