package de.claudiuscoenen.snapmd;

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

		if (app.getLoginDataRepository().isLoggedIn()) {
			startActivity(getIntent().setClass(this, SelectPadActivity.class));
		} else {
			startActivity(getIntent().setClass(this, LoginActivity.class));
		}

		finish();
	}
}
