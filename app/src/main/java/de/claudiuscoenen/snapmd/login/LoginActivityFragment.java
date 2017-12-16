package de.claudiuscoenen.snapmd.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.claudiuscoenen.snapmd.SnapMdApplication;
import de.claudiuscoenen.snapmd.R;
import de.claudiuscoenen.snapmd.padselection.SelectPadActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class LoginActivityFragment extends Fragment {

	@BindView(R.id.input_url)
	EditText urlText;

	@BindView(R.id.input_mail)
	EditText mailText;

	@BindView(R.id.input_password)
	EditText passwordText;

	private final CompositeDisposable loadingOperations = new CompositeDisposable();
	private SnapMdApplication app;

	public LoginActivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		ButterKnife.bind(this, view);

		app = (SnapMdApplication) getActivity().getApplication();

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		loadingOperations.dispose();
	}

	@OnClick(R.id.btn_login)
	void onLoginClick() {
		// TODO: show loading indicator
		Disposable disposable = app.getApi()
				.validateLogin(urlText.getText().toString(), mailText.getText().toString(), passwordText.getText().toString())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::onLoginComplete, this::onLoginError);
		loadingOperations.add(disposable);
	}

	private void onLoginComplete() {
		app.getLoginDataRepository().saveLoginData(urlText.getText().toString(), mailText.getText().toString(), passwordText.getText().toString());
		startActivity(new Intent(getContext(), SelectPadActivity.class));
	}

	private void onLoginError(Throwable t) {
		Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
	}
}
