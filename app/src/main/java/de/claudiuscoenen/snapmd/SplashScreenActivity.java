package de.claudiuscoenen.snapmd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.claudiuscoenen.snapmd.login.LoginActivity;
import de.claudiuscoenen.snapmd.padselection.SelectPadActivity;

public class SplashScreenActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add styling

		SnapMdApplication app = (SnapMdApplication) getApplication();

		// pass SEND intents on to next activity
		Intent intent = Intent.ACTION_SEND.equals(getIntent().getAction()) ?
				getIntent() : new Intent();

		if (app.getLoginDataRepository().isLoggedIn()) {
			startActivity(intent.setClass(this, SelectPadActivity.class));
		} else {
			startActivity(intent.setClass(this, LoginActivity.class));
		}

		finish();
	}
}
