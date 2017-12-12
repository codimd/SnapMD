package de.claudiuscoenen.hackmdsnapshot.api;


import de.claudiuscoenen.hackmdsnapshot.api.model.History;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface HackMdService {

	@FormUrlEncoded
	@POST("login")
	Completable login(@Field("email") String email, @Field("password") String password);

	@GET("history")
	Single<History> history();
}
