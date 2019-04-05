package de.claudiuscoenen.snapmd.api;


import java.io.IOException;
import java.net.CookieHandler;
import java.util.List;

import de.claudiuscoenen.snapmd.api.model.History;
import de.claudiuscoenen.snapmd.api.model.Media;
import de.claudiuscoenen.snapmd.model.Pad;
import de.claudiuscoenen.snapmd.repository.LoginDataRepository;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CodiMdApi {

	private final LoginDataRepository loginDataRepository;
	private final OkHttpClient httpClient;
	private CodiMdService apiService;

	public CodiMdApi(LoginDataRepository loginDataRepository) {
		this.loginDataRepository = loginDataRepository;
		loginDataRepository.addListener(this::onLoginDataChanged);

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
				.setLevel(HttpLoggingInterceptor.Level.BODY);

		Interceptor userAgent = new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request originalRequest = chain.request();
				Request requestWithUserAgent = originalRequest.newBuilder()
					.removeHeader("User-Agent")
					.addHeader("User-Agent", "SnapMD")
					.build();
				return chain.proceed(requestWithUserAgent);
			}
		};

		JavaNetCookieJar cookieJar = new JavaNetCookieJar(CookieHandler.getDefault());
		httpClient = new OkHttpClient.Builder()
				.addInterceptor(logging)
				.addInterceptor(userAgent)
				.cookieJar(cookieJar)
				.build();

		onLoginDataChanged();
	}

	public OkHttpClient getHttpClient() {
		return httpClient;
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

		apiService = retrofit.create(CodiMdService.class);
	}
}
