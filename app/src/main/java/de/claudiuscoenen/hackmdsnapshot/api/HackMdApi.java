package de.claudiuscoenen.hackmdsnapshot.api;


import java.net.CookieHandler;
import java.util.List;

import de.claudiuscoenen.hackmdsnapshot.api.model.History;
import de.claudiuscoenen.hackmdsnapshot.model.Pad;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HackMdApi {

	private HackMdService apiService;

	public HackMdApi() {

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
				.setLevel(HttpLoggingInterceptor.Level.BODY);

		JavaNetCookieJar cookieJar = new JavaNetCookieJar(CookieHandler.getDefault());
		OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor(logging)
				.cookieJar(cookieJar)
				.build();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://myhackmd.instance/")
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(httpClient)
				.build();

		apiService = retrofit.create(HackMdService.class);
	}

	public Single<List<Pad>> getPads(String email, String password) {
		return apiService.login(email, password)
				.andThen(apiService.history())
				.map(History::getHistory)
				.subscribeOn(Schedulers.io());
	}
}
