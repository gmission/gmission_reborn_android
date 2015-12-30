package hk.ust.gmission.core;


import hk.ust.gmission.models.UserAgentProvider;
import retrofit.RequestInterceptor;

import static hk.ust.gmission.core.Constants.Http.PARAM_SESSION_TOKEN;

public class RestAdapterRequestInterceptor implements RequestInterceptor {

    private UserAgentProvider userAgentProvider;

    public RestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        this.userAgentProvider = userAgentProvider;
    }

    @Override
    public void intercept(RequestFacade request) {

        // Add header to set content type of JSON
        request.addHeader("Content-Type", "application/json");


        //TODO remove thir parse api key.
        // Add auth info for PARSE, normally this is where you'd add your auth info for this request (if needed).


        request.addHeader("Authorization", "gMission " + PARAM_SESSION_TOKEN);


        // Add the user agent to the request.
        request.addHeader("User-Agent", userAgentProvider.get());

    }
}
