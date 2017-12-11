package de.claudiuscoenen.hackmdsnapshot.padselection;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.claudiuscoenen.hackmdsnapshot.R;
import de.claudiuscoenen.hackmdsnapshot.model.Pad;


public class PadSelectionFragment extends Fragment {

	@BindView(R.id.list_pads)
	RecyclerView padList;

	private Listener listener;

	public PadSelectionFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pad_selection, container, false);
		ButterKnife.bind(this, view);

		List<Pad> testPads = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			testPads.add(new Pad("id" + i, "Test Pad " + i));
		}

		PadListAdapter adapter = new PadListAdapter(listener::onPadSelected);
		adapter.setPads(testPads);

		padList.setLayoutManager(new LinearLayoutManager(getContext()));
		padList.setAdapter(adapter);

		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof Listener) {
			listener = (Listener) context;
		} else {
			throw new RuntimeException(context.toString() + " must implement Listener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 */
	public interface Listener {
		void onPadSelected(Pad pad);
	}
}
