package hk.ust.gmission.core;


import static hk.ust.gmission.core.Constants.Http.HEADER_PARSE_APP_ID;
import static hk.ust.gmission.core.Constants.Http.HEADER_PARSE_REST_API_KEY;
import static hk.ust.gmission.core.Constants.Http.PARSE_APP_ID;
import static hk.ust.gmission.core.Constants.Http.PARSE_REST_API_KEY;
import static hk.ust.gmission.core.Constants.Http.SESSION_TOKEN;

import hk.ust.gmission.models.UserAgentProvider;
import retrofit.RequestInterceptor;

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
        request.addHeader(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);
        request.addHeader(HEADER_PARSE_APP_ID, PARSE_APP_ID);

        request.addHeader("Authorization", "gMission " + SESSION_TOKEN);
//        request.addHeader("token", SESSION_TOKEN);


        // Add the user agent to the request.
        request.addHeader("User-Agent", userAgentProvider.get());

    }
}
