package de.claudiuscoenen.hackmdsnapshot.api;


import java.net.CookieHandler;
import java.util.List;

import de.claudiuscoenen.hackmdsnapshot.api.model.History;
import de.claudiuscoenen.hackmdsnapshot.api.model.Media;
import de.claudiuscoenen.hackmdsnapshot.model.Pad;
import de.claudiuscoenen.hackmdsnapshot.repository.LoginDataRepository;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HackMdApi {

	private final LoginDataRepository loginDataRepository;
	private final OkHttpClient httpClient;
	private HackMdService apiService;

	public HackMdApi(LoginDataRepository loginDataRepository) {
		this.loginDataRepository = loginDataRepository;
		loginDataRepository.addListener(this::onLoginDataChanged);

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
				.setLevel(HttpLoggingInterceptor.Level.BODY);

		JavaNetCookieJar cookieJar = new JavaNetCookieJar(CookieHandler.getDefault());
		httpClient = new OkHttpClient.Builder()
				.addInterceptor(logging)
				.cookieJar(cookieJar)
				.build();

		onLoginDataChanged();
	}

	public Single<List<Pad>> getPads() {
		return apiService.login(loginDataRepository.getMail(), loginDataRepository.getPassword())
				.andThen(apiService.history())
				.map(History::getHistory)
				.subscribeOn(Schedulers.io());
	}

	public Completable validateLogin(String serverUrl, String email, String password) {
		if (!serverUrl.endsWith("/")) {
			serverUrl += "/";
		}

		return apiService
				.login(serverUrl + "login", email, password)
				.subscribeOn(Schedulers.io());
	}

	public Single<Media> uploadImage(byte[] bytes) {
		RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), bytes);
		MultipartBody.Part body = MultipartBody.Part.createFormData("image", "notimportant.jpg", reqFile);

		return apiService.login(loginDataRepository.getMail(), loginDataRepository.getPassword())
				.andThen(apiService.upload(body))
				.subscribeOn(Schedulers.io());
	}

	private void onLoginDataChanged() {
		String url = loginDataRepository.getServerUrl() == null ?
				"https://example.com/" : loginDataRepository.getServerUrl();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(url)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(httpClient)
				.build();

		apiService = retrofit.create(HackMdService.class);
	}
}
