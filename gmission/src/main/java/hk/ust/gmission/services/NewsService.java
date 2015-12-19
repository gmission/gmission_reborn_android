package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.NewsWrapper;
import retrofit.http.GET;
import rx.Observable;


/**
 * Interface for defining the news service to communicate with Parse.com
 */
public interface NewsService {

    @GET(Constants.Http.URL_NEWS_FRAG)
    Observable<NewsWrapper> getNews();

}
