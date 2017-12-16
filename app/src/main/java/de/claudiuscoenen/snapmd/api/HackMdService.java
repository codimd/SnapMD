package de.claudiuscoenen.snapmd.api;


import de.claudiuscoenen.snapmd.api.model.History;
import de.claudiuscoenen.snapmd.api.model.Media;
import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

interface HackMdService {

	@FormUrlEncoded
	@POST
	Completable login(@Url String url, @Field("email") String email, @Field("password") String password);

	@FormUrlEncoded
	@POST("login")
	Completable login(@Field("email") String email, @Field("password") String password);

	@Multipart
	@POST("uploadimage")
	Single<Media> upload(@Part MultipartBody.Part image);

	@GET("history")
	Single<History> history();
}
