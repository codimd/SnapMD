package de.claudiuscoenen.hackmdsnapshot.padselection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import de.claudiuscoenen.hackmdsnapshot.R;
import de.claudiuscoenen.hackmdsnapshot.model.Pad;

public class SelectPadActivity extends AppCompatActivity implements
		PadSelectionFragment.Listener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pad);
	}

	@Override
	public void onPadSelected(Pad pad) {
		Toast.makeText(this, pad.getText(), Toast.LENGTH_SHORT).show();
	}
}
