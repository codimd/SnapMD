package de.claudiuscoenen.hackmdsnapshot;

import android.content.Intent;
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
			startActivity(new Intent(this, SelectPadActivity.class));
		} else {
			startActivity(new Intent(this, LoginActivity.class));
		}

		finish();
	}
}
