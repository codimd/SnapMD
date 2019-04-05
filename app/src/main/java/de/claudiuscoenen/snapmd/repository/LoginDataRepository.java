package de.claudiuscoenen.snapmd.repository;


import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LoginDataRepository {

	private final static String SHARED_PREFS_NAME = "de.claudiuscoenen.snapmd.SHARED_PREFS";
	private final static String PREF_SERVER_URL = "PREF_SERVER_URL";
	private final static String PREF_MAIL = "PREF_MAIL";
	private final static String PREF_PASSWORD = "PREF_PASSWORD";

	private final List<WeakReference<Listener>> listeners = new ArrayList<>();
	private final Context context;

	public LoginDataRepository(Context context) {
		this.context = context;
	}

	public void addListener(Listener listener) {
		listeners.add(new WeakReference<>(listener));
	}

	public boolean isLoggedIn() {
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		return settings.getString(PREF_MAIL, null) != null;
	}

	public String getServerUrl() {
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		return settings.getString(PREF_SERVER_URL, null);
	}

	public String getMail() {
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		return settings.getString(PREF_MAIL, null);
	}

	public String getPassword() {
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		return settings.getString(PREF_PASSWORD, null);
	}

	public void saveLoginData(String serverUrl, String email, String password) {
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(PREF_SERVER_URL, serverUrl);
		editor.putString(PREF_MAIL, email);
		editor.putString(PREF_PASSWORD, password); // TODO: encrypt password

		editor.apply();

		for (WeakReference<Listener> listenerReference : listeners) {
			Listener listener = listenerReference.get();
			if (listener != null) {
				listener.onLoginDataChanged();
			}
		}
	}

	public void deleteLoginData() {
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(PREF_SERVER_URL, null);
		editor.putString(PREF_MAIL, null);
		editor.putString(PREF_PASSWORD, null);

		editor.apply();
	}

	public interface Listener {
		void onLoginDataChanged();
	}
}
