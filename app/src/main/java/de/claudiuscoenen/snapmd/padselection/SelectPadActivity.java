package de.claudiuscoenen.snapmd.padselection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.claudiuscoenen.snapmd.R;
import de.claudiuscoenen.snapmd.SnapMdApplication;
import de.claudiuscoenen.snapmd.api.model.Media;
import de.claudiuscoenen.snapmd.model.Pad;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SelectPadActivity extends AppCompatActivity implements
		PadSelectionFragment.Listener {

	@BindView(R.id.container_pad_selection)
	protected View padSelectionContainer;

	@BindView(R.id.progress)
	protected ProgressBar progressBar;

	private final CompositeDisposable disposables = new CompositeDisposable();
	private SnapMdApplication app;
	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pad);
		ButterKnife.bind(this);

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		app = (SnapMdApplication) getApplication();

		imageUri = null;
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
	protected void onDestroy() {
		super.onDestroy();
		disposables.clear();
	}

	@Override
	public void onPadSelected(Pad pad) {
		Toast.makeText(this, pad.getText(), Toast.LENGTH_SHORT).show();

		if (imageUri == null) {
			return;
		}

		byte[] bytes;
		try {
			//noinspection ConstantConditions
			bytes = IOUtils.toByteArray(getContentResolver().openInputStream(imageUri));
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
		Toast.makeText(this, media.getLink(), Toast.LENGTH_LONG).show();
	}

	private void onUploadError(Throwable t) {
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
