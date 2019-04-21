package de.claudiuscoenen.snapmd.padselection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.claudiuscoenen.snapmd.R;
import de.claudiuscoenen.snapmd.SnapMdApplication;
import de.claudiuscoenen.snapmd.api.SocketIoWrapper;
import de.claudiuscoenen.snapmd.api.model.Media;
import de.claudiuscoenen.snapmd.login.LoginActivity;
import de.claudiuscoenen.snapmd.model.Pad;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class SelectPadActivity extends AppCompatActivity implements
		PadSelectionFragment.Listener {

	@BindView(R.id.container_pad_selection)
	protected View padSelectionContainer;

	@BindView(R.id.progress)
	protected ProgressBar progressBar;

	private final CompositeDisposable disposables = new CompositeDisposable();
	private SnapMdApplication app;
	private SocketIoWrapper socketIoWrapper;
	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pad);
		ButterKnife.bind(this);

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		imageUri = null;
		app = (SnapMdApplication) getApplication();

		if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
			imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		}

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.container_image, ImageFragment.newInstance(imageUri), "ImageFragment")
					.commit();

			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.container_pad_selection, PadSelectionFragment.newInstance(), "PadSelectionFragment")
					.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		socketIoWrapper = new SocketIoWrapper(
				app.getLoginDataRepository().getServerUrl(),
				app.getApi().getHttpClient()
		);
	}

	@Override
	protected void onPause() {
		super.onPause();
		socketIoWrapper.disconnect();
		socketIoWrapper = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		disposables.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_pad, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_logout:
				app.getLoginDataRepository().deleteLoginData();
				startActivity(new Intent(this, LoginActivity.class));
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPadSelected(Pad pad) {
		if (imageUri == null) {
			return;
		}

		socketIoWrapper.connect(pad.getId());

		byte[] bytes;
		try {
			//noinspection ConstantConditions
			InputStream is = getContentResolver().openInputStream(imageUri);
			bytes = IOUtils.toByteArray(is);
			is.close();
		} catch (IOException | NullPointerException e) {
			onUploadError(e);
			return;
		}

		// TODO: get progress
		Disposable disposable = app.getApi()
				.uploadImage(bytes)
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally(this::onUploadEnd)
				.subscribe(this::onUploadSuccess, this::onUploadError);

		disposables.add(disposable);
		onUploadStart();
	}

	private void onUploadSuccess(Media media) {
		String uploadedImageUrl = media.getLink();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String timestamp = formatter.format(new Date());

		socketIoWrapper.setText("\n![SnapMD Upload at " + timestamp + "](" + uploadedImageUrl + ")\n");
		Timber.i("upload successful, URL: %s", uploadedImageUrl);
		Toast.makeText(this, media.getLink(), Toast.LENGTH_LONG).show();
	}

	private void onUploadError(Throwable t) {
		Timber.e(t, "upload failed");
		Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
	}

	private void onUploadStart() {
		padSelectionContainer.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void onUploadEnd() {
		padSelectionContainer.setEnabled(true);
		progressBar.setVisibility(View.GONE);
	}
}
