package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Attachment;
import hk.ust.gmission.models.ImageVideoResult;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

/**
 * Created by bigstone on 3/1/2016.
 */
public interface AttachmentService {
    @Multipart
    @POST(Constants.Http.URL_IMAGE + "/upload")
    Observable<ImageVideoResult> createImage(@Part("file") TypedFile photo, @Part("description") TypedString description);

    @GET(Constants.Http.URL_ATTACHMENTS_FRAG + "/{id}")
    Observable<Attachment> getAttachment(@Path("id") String aId);
    @POST(Constants.Http.URL_ATTACHMENTS_FRAG)
    Observable<Attachment> createAttachment(@Body Attachment attachment);
}
