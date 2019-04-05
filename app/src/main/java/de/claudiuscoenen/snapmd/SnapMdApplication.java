package de.claudiuscoenen.snapmd;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import de.claudiuscoenen.snapmd.api.CodiMdApi;
import de.claudiuscoenen.snapmd.repository.LoginDataRepository;
import timber.log.Timber;


public class SnapMdApplication extends Application {

	private CodiMdApi api;
	private LoginDataRepository loginDataRepository;

	public CodiMdApi getApi() {
		return api;
	}
	public LoginDataRepository getLoginDataRepository() {
		return loginDataRepository;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Timber.plant(new Timber.DebugTree());

		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cookieManager);

		loginDataRepository = new LoginDataRepository(this);
		api = new CodiMdApi(loginDataRepository);
	}
}
