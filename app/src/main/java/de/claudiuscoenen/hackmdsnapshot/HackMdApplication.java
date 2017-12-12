package de.claudiuscoenen.hackmdsnapshot;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import de.claudiuscoenen.hackmdsnapshot.api.HackMdApi;


public class HackMdApplication extends Application {

	private HackMdApi api;

	public HackMdApi getApi() {
		return api;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cookieManager);

		api = new HackMdApi();
	}
}
