package de.claudiuscoenen.hackmdsnapshot.padselection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import butterknife.ButterKnife;
import de.claudiuscoenen.hackmdsnapshot.R;
import de.claudiuscoenen.hackmdsnapshot.model.Pad;

public class SelectPadActivity extends AppCompatActivity implements
		PadSelectionFragment.Listener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pad);
		ButterKnife.bind(this);

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		Uri imageUri = null;
		if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
			imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		}

		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.container_image, ImageFragment.newInstance(imageUri), "ImageFragment")
					.commit();
		}

		Log.d("SelectPadActivity", "" + getIntent().getType());
	}

	@Override
	public void onPadSelected(Pad pad) {
		Toast.makeText(this, pad.getText(), Toast.LENGTH_SHORT).show();
	}
}
