package de.claudiuscoenen.hackmdsnapshot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.claudiuscoenen.hackmdsnapshot.login.LoginActivity;
import de.claudiuscoenen.hackmdsnapshot.padselection.SelectPadActivity;

public class SplashScreenActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add styling

		HackMdApplication app = (HackMdApplication) getApplication();

		if (app.getLoginDataRepository().isLoggedIn()) {
			startActivity(getIntent().setClass(this, SelectPadActivity.class));
		} else {
			startActivity(getIntent().setClass(this, LoginActivity.class));
		}

		finish();
	}
}
