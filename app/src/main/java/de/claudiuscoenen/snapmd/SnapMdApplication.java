package de.claudiuscoenen.snapmd;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import de.claudiuscoenen.snapmd.api.HackMdApi;
import de.claudiuscoenen.snapmd.repository.LoginDataRepository;
import timber.log.Timber;


public class SnapMdApplication extends Application {

	private HackMdApi api;
	private LoginDataRepository loginDataRepository;

	public HackMdApi getApi() {
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
		api = new HackMdApi(loginDataRepository);
	}
}
